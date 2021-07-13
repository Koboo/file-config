package eu.koboo.config.directory;

import eu.koboo.config.Config;
import eu.koboo.config.FileConfig;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ConfigDirectory<T extends ConfigMapping> {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String fileDirectory;
    private final String fileExtension;

    public ConfigDirectory(String fileDirectory, String fileExtension) {
        this.fileDirectory = fileDirectory;
        this.fileExtension = fileExtension;
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));
    }

    public boolean existsObject(String id) {
        return getJavaFile(id).exists();
    }

    public void saveObject(T config) {
        executor.execute(() -> {
            FileConfig fileConfig = Config.of(getJavaFile(config.getId()));
            toConfig(config, fileConfig);
        });
    }

    public void removeObject(String id) {
        executor.execute(() -> Config.of(getJavaFile(id)).delete());
    }

    public T getObject(String id) {
        File file = getJavaFile(id);
        if (file.exists())
            return fromConfig(Config.of(file));
        return null;
    }

    private File getJavaFile(String identifier) {
        return new File(fileDirectory, identifier + "." + fileExtension);
    }

    public abstract T fromConfig(FileConfig fileConfig);

    public abstract void toConfig(T configObject, FileConfig fileConfig);

}
