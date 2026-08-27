package org.michelin.filemanager.file;

import org.michelin.filemanager.config.Config;

/**
 * Strategy/Factory for choosing a FileSource implementation based on config.
 * Keeps the App / Application class out of the if (source == local) game.
 */
public interface FileSourceFactory {
    FileSource create(Config.FileConfig fileConfig);
}
