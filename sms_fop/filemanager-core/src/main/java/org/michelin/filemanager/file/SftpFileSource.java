package org.michelin.filemanager.file;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.keyverifier.DefaultKnownHostsServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.config.ConfigValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SFTP-protocol {@link FileSource} backed by Apache MINA SSHD. Sibling of
 * {@link FilesComFileSource} — same SPI, different wire. Use when the customer
 * mandates SSH-key auth (or any non-files.com SFTP server).
 *
 * <p>Connection lifecycle: <b>per-call</b>. Each {@code list/download/move}
 * opens an {@link SshClient} + {@link ClientSession} + {@link SftpClient},
 * does its work, and closes everything. Simpler than maintaining a long-lived
 * connection that has to detect and recover from broken pipes, and the file-
 * manager only polls every few minutes — the connect overhead is dwarfed by
 * the polling interval. Revisit if poll frequency ever climbs to seconds.
 *
 * <p>Auth: {@code private_key_path} wins if set, else falls back to
 * {@code password}. Key files: standard PEM (OpenSSH, PKCS#8, ed25519, RSA).
 * Passphrase is optional. If both auth methods are blank, construction throws.
 *
 * <p>Host key verification: if {@code known_hosts_path} is set, MINA verifies
 * against that file (production posture). If blank, the verifier accepts any
 * host key and logs a loud warning — only acceptable for dev.
 */
public class SftpFileSource implements FileSource {

    private static final Logger log = LoggerFactory.getLogger(SftpFileSource.class);

    private final Config.FileConfig.SftpConfig cfg;
    private final Pattern namePattern;
    private final Duration timeout;

    public SftpFileSource(Config.FileConfig.SftpConfig sftp, Pattern namePattern) {
        if (sftp == null) {
            throw new ConfigValidationException(
                "file.sftp block is missing — required when file.source=sftp");
        }
        if (sftp.host() == null || sftp.host().isBlank()) {
            throw new ConfigValidationException("file.sftp.host is required");
        }
        if (sftp.username() == null || sftp.username().isBlank()) {
            throw new ConfigValidationException("file.sftp.username is required");
        }
        if (sftp.pickupPath() == null || sftp.pickupPath().isBlank()) {
            throw new ConfigValidationException("file.sftp.pickup_path is required");
        }
        if (sftp.archivePath() == null || sftp.archivePath().isBlank()) {
            throw new ConfigValidationException("file.sftp.archive_path is required");
        }
        if (sftp.rejectPath() == null || sftp.rejectPath().isBlank()) {
            throw new ConfigValidationException("file.sftp.reject_path is required");
        }
        boolean hasKey  = sftp.privateKeyPath() != null && !sftp.privateKeyPath().isBlank();
        boolean hasPass = sftp.password()       != null && !sftp.password().isBlank();
        if (!hasKey && !hasPass) {
            throw new ConfigValidationException(
                "file.sftp requires either private_key_path or password (both blank)");
        }
        this.cfg = sftp;
        this.namePattern = namePattern;
        this.timeout = Duration.ofMillis(sftp.connectTimeoutMs());
        log.info("SftpFileSource ready (host={}, port={}, auth={}, knownHosts={})",
                 sftp.host(), sftp.port(),
                 hasKey ? "private_key" : "password",
                 sftp.knownHostsPath() == null || sftp.knownHostsPath().isBlank() ? "<unset/INSECURE>" : sftp.knownHostsPath());
    }

    @Override
    public List<SourceFile> list() throws IOException {
        return withSftp(sftp -> {
            List<SourceFile> out = new ArrayList<>();
            for (SftpClient.DirEntry e : sftp.readDir(cfg.pickupPath())) {
                String name = e.getFilename();
                if (".".equals(name) || "..".equals(name)) continue;
                if (e.getAttributes().isDirectory()) continue;
                if (!namePattern.matcher(name).matches()) continue;
                String id = joinPath(cfg.pickupPath(), name);
                out.add(new SourceFile(id, name, e.getAttributes().getSize()));
            }
            out.sort(Comparator.comparing(SourceFile::name));
            return out;
        });
    }

    @Override
    public Path download(SourceFile file, Path dest) throws IOException {
        return withSftp(sftp -> {
            Files.createDirectories(dest.getParent() != null ? dest.getParent() : dest);
            try (InputStream in = sftp.read(file.id())) {
                Files.copy(in, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            return dest;
        });
    }

    @Override
    public void moveToArchive(SourceFile file, String newName) throws IOException {
        move(file, cfg.archivePath(), newName);
    }

    @Override
    public void moveToReject(SourceFile file, String newName) throws IOException {
        move(file, cfg.rejectPath(), newName);
    }

    private void move(SourceFile file, String dir, String newName) throws IOException {
        withSftp(sftp -> {
            String target = joinPath(dir, newName != null ? newName : file.name());
            sftp.rename(file.id(), target);
            return null;
        });
    }

    // ------------------------------------------------------------------
    // Connection lifecycle
    // ------------------------------------------------------------------

    @FunctionalInterface
    private interface SftpOp<T> { T run(SftpClient sftp) throws IOException; }

    private <T> T withSftp(SftpOp<T> op) throws IOException {
        SshClient client = SshClient.setUpDefaultClient();
        configureHostKeyVerifier(client);
        client.start();
        try (ClientSession session = client.connect(cfg.username(), cfg.host(), cfg.port())
                                           .verify(timeout).getSession()) {
            authenticate(session);
            session.auth().verify(timeout);
            try (SftpClient sftp = SftpClientFactory.instance().createSftpClient(session)) {
                return op.run(sftp);
            }
        } finally {
            client.stop();
        }
    }

    private void configureHostKeyVerifier(SshClient client) {
        String known = cfg.knownHostsPath();
        if (known == null || known.isBlank()) {
            log.warn("known_hosts_path is unset — accepting ANY host key. Set SFTP_KNOWN_HOSTS_PATH in prod.");
            client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
        } else {
            Path khp = Path.of(known);
            if (!Files.isRegularFile(khp)) {
                throw new ConfigValidationException(
                    "file.sftp.known_hosts_path does not exist or is not a file: " + known);
            }
            client.setServerKeyVerifier(new DefaultKnownHostsServerKeyVerifier(
                    AcceptAllServerKeyVerifier.INSTANCE, false, khp));
        }
    }

    private void authenticate(ClientSession session) throws IOException {
        String keyPath = cfg.privateKeyPath();
        if (keyPath != null && !keyPath.isBlank()) {
            Path kp = Path.of(keyPath);
            if (!Files.isRegularFile(kp)) {
                throw new ConfigValidationException(
                    "file.sftp.private_key_path does not exist or is not a file: " + keyPath);
            }
            FilePasswordProvider pwd = (cfg.privateKeyPassphrase() == null || cfg.privateKeyPassphrase().isBlank())
                    ? FilePasswordProvider.EMPTY
                    : FilePasswordProvider.of(cfg.privateKeyPassphrase());
            Iterable<KeyPair> pairs;
            try {
                pairs = SecurityUtils.loadKeyPairIdentities(session, () -> kp.toString(),
                        Files.newInputStream(kp), pwd);
            } catch (GeneralExceptionWrapper e) {
                throw e.io;
            } catch (Exception e) {
                throw new IOException("Failed to load SFTP private key: " + e.getMessage(), e);
            }
            if (pairs == null) {
                throw new IOException("No key pairs loaded from " + keyPath
                        + " (unsupported format or wrong passphrase)");
            }
            for (KeyPair pair : pairs) {
                session.addPublicKeyIdentity(pair);
            }
        } else {
            session.addPasswordIdentity(cfg.password());
        }
    }

    private static String joinPath(String dir, String name) {
        if (dir.endsWith("/")) return dir + name;
        return dir + "/" + name;
    }

    /** Marker for wrapping checked-but-impossible exceptions from MINA SecurityUtils. */
    private static final class GeneralExceptionWrapper extends RuntimeException {
        final IOException io;
        GeneralExceptionWrapper(IOException io) { this.io = io; }
    }
}
