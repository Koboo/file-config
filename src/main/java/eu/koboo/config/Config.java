package eu.koboo.config;

import eu.koboo.config.utilities.FileUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Config {

    public static FileConfig of(String file, Consumer<FileConfig> consumer) {
        return new FileConfig(file, consumer);
    }

    public static FileConfig of(String file) {
        return of(file, null);
    }

    public static FileConfig of(File file) {
        return of(file.getPath(), null);
    }

    public static FileConfig of(File file, Consumer<FileConfig> consumer) {
        return of(file.getPath(), consumer);
    }

    public static FileConfig ofResource(String resource) {
        return ofResource(resource, null);
    }

    public static FileConfig ofResource(String resource, Consumer<FileConfig> consumer) {
        File file = FileUtilities.exportResource(resource);
        return file.exists() ? new FileConfig(resource, consumer) : null;
    }

    public static List<FileConfig> ofDirectory(String directory) {
        return ofDirectory(new File(directory));
    }

    public static List<FileConfig> ofDirectory(File directory) {
        List<FileConfig> fileList = new ArrayList<>();
        File[] dirFiles = directory.listFiles();
        if (directory.exists() && directory.isDirectory() && dirFiles != null) {
            for (int i = 0; i < dirFiles.length; i++) {
                File file = dirFiles[i];
                if (file.exists()) {
                    fileList.add(Config.of(file));
                }
            }
        }
        return fileList;
    }

}
