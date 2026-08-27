package org.michelin.filemanager.file;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.michelin.filemanager.config.Config;
import org.michelin.filemanager.config.ConfigValidationException;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TC-250..TC-255 — SftpFileSource behavior, against an embedded MINA SSH server
 * in-process. No Docker, no live network. The server roots its virtual FS at a
 * @TempDir, so file-system effects are verifiable.
 */
class SftpFileSourceTest {

    private static final Pattern PATTERN =
            Pattern.compile("^opening_balance_\\d{8}\\.csv$");

    private SshServer sshd;
    private int port;
    private Path remoteRoot;

    @BeforeEach
    void startServer(@TempDir Path tmp) throws Exception {
        remoteRoot = tmp.resolve("remote");
        Files.createDirectories(remoteRoot.resolve("inbound"));
        Files.createDirectories(remoteRoot.resolve("archive"));
        Files.createDirectories(remoteRoot.resolve("reject"));

        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(0);
        sshd.setHost("127.0.0.1");
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(tmp.resolve("hostkey.ser")));
        sshd.setPasswordAuthenticator((u, p, s) -> "testuser".equals(u) && "testpass".equals(p));
        sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
        sshd.setSubsystemFactories(List.of(new SftpSubsystemFactory()));
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(remoteRoot));
        sshd.start();
        port = sshd.getPort();
    }

    @AfterEach
    void stopServer() throws Exception {
        if (sshd != null) sshd.stop(true);
    }

    private Config.FileConfig.SftpConfig passwordCfg() {
        return new Config.FileConfig.SftpConfig(
                "127.0.0.1", port, "testuser", "testpass",
                "", "", "",                          // no key, no passphrase, no known_hosts
                "/inbound", "/archive", "/reject",
                10_000);
    }

    @Test
    @DisplayName("TC-250: password auth — list returns only files matching name pattern")
    void list_passwordAuth_filtersByPattern() throws Exception {
        Files.writeString(remoteRoot.resolve("inbound/opening_balance_20260601.csv"), "h\n");
        Files.writeString(remoteRoot.resolve("inbound/opening_balance_20260602.csv"), "h\n");
        Files.writeString(remoteRoot.resolve("inbound/notes.txt"), "x\n");
        Files.createDirectories(remoteRoot.resolve("inbound/subdir"));

        SftpFileSource src = new SftpFileSource(passwordCfg(), PATTERN);

        List<SourceFile> files = src.list();

        assertThat(files).extracting(SourceFile::name).containsExactly(
                "opening_balance_20260601.csv",
                "opening_balance_20260602.csv");
    }

    @Test
    @DisplayName("TC-251: download writes remote bytes to local dest")
    void download_writesBytes(@TempDir Path local) throws Exception {
        Files.writeString(remoteRoot.resolve("inbound/opening_balance_20260601.csv"), "hello\n");
        SftpFileSource src = new SftpFileSource(passwordCfg(), PATTERN);

        SourceFile sf = src.list().getFirst();
        Path dest = local.resolve("staged/opening_balance_20260601.csv");
        src.download(sf, dest);

        assertThat(Files.readString(dest)).isEqualTo("hello\n");
    }

    @Test
    @DisplayName("TC-252: moveToArchive renames remote file into archive folder")
    void moveToArchive_renamesOnServer() throws Exception {
        Path src = remoteRoot.resolve("inbound/opening_balance_20260601.csv");
        Files.writeString(src, "h\n");
        SftpFileSource source = new SftpFileSource(passwordCfg(), PATTERN);

        source.moveToArchive(source.list().getFirst(),
                "20260601_120000_opening_balance_20260601.csv");

        assertThat(Files.exists(src)).isFalse();
        assertThat(Files.exists(remoteRoot.resolve(
                "archive/20260601_120000_opening_balance_20260601.csv"))).isTrue();
    }

    @Test
    @DisplayName("TC-253: moveToReject places file into reject folder")
    void moveToReject_renamesOnServer() throws Exception {
        Files.writeString(remoteRoot.resolve("inbound/opening_balance_20260601.csv"), "h\n");
        SftpFileSource source = new SftpFileSource(passwordCfg(), PATTERN);

        source.moveToReject(source.list().getFirst(), "rejected_20260601.csv");

        assertThat(Files.exists(remoteRoot.resolve("reject/rejected_20260601.csv"))).isTrue();
    }

    @Test
    @DisplayName("TC-254: private-key auth — list works when key path resolves a usable key")
    void list_keyAuth_usesPrivateKey(@TempDir Path tmp) throws Exception {
        Files.writeString(remoteRoot.resolve("inbound/opening_balance_20260601.csv"), "h\n");

        Path keyPath = tmp.resolve("id_rsa.pem");
        writePkcs8RsaKey(keyPath);                       // generates + writes PEM

        Config.FileConfig.SftpConfig cfg = new Config.FileConfig.SftpConfig(
                "127.0.0.1", port, "testuser", "",
                keyPath.toString(), "", "",
                "/inbound", "/archive", "/reject",
                10_000);
        SftpFileSource src = new SftpFileSource(cfg, PATTERN);

        List<SourceFile> files = src.list();

        assertThat(files).extracting(SourceFile::name)
                .containsExactly("opening_balance_20260601.csv");
    }

    @Test
    @DisplayName("TC-255: construction fails when neither password nor private_key_path is set")
    void blankAuth_failsConstruction() {
        Config.FileConfig.SftpConfig cfg = new Config.FileConfig.SftpConfig(
                "127.0.0.1", port, "testuser", "",
                "", "", "",
                "/inbound", "/archive", "/reject",
                10_000);

        assertThatThrownBy(() -> new SftpFileSource(cfg, PATTERN))
                .isInstanceOf(ConfigValidationException.class)
                .hasMessageContaining("private_key_path or password");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Generates a fresh 2048-bit RSA keypair and writes the private key in
     * PKCS#8 PEM format to {@code dest}. The embedded server accepts any
     * public key, so any valid key works here.
     */
    private static void writePkcs8RsaKey(Path dest) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair pair = gen.generateKeyPair();
        byte[] encoded = pair.getPrivate().getEncoded();
        String b64 = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII))
                .encodeToString(encoded);
        try (Writer w = new OutputStreamWriter(Files.newOutputStream(dest), StandardCharsets.US_ASCII)) {
            w.write("-----BEGIN PRIVATE KEY-----\n");
            w.write(b64);
            w.write("\n-----END PRIVATE KEY-----\n");
        }
    }
}
