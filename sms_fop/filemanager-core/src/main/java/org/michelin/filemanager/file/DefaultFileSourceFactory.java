package org.michelin.filemanager.file;

import org.michelin.filemanager.config.Config;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class DefaultFileSourceFactory implements FileSourceFactory {

    @Override
    public FileSource create(Config.FileConfig fileConfig) {
        Pattern pattern = Pattern.compile(fileConfig.namePattern());
        FileSourceType type = FileSourceType.from(fileConfig.source());
        return switch (type) {
            case LOCAL -> {
                Config.FileConfig.LocalConfig l = fileConfig.local();
                yield new LocalFolderFileSource(
                        Path.of(l.pickupPath()),
                        Path.of(l.archivePath()),
                        Path.of(l.rejectPath()),
                        pattern);
            }
            case SFTP -> new SftpFileSource(fileConfig.sftp(), pattern);
            case FILESCOM -> {
                Config.FileConfig.FilesComConfig fc = fileConfig.filescom();
                FilesComGateway gateway = new FilesComSdkGateway(fc);
                yield new FilesComFileSource(gateway, fc, pattern);
            }
        };
    }
}
