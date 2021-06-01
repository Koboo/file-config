package eu.koboo.config;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class FileDirectory<T extends ConfigObject> {

    private final String fileDirectory;
    private final String fileExtension;

    private final ExecutorService executor;

    public FileDirectory(String fileDirectory, String fileExtension) {
        this.fileDirectory = fileDirectory;
        this.fileExtension = fileExtension;
        this.executor = Executors.newSingleThreadExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));
    }

    public boolean existsObject(String fileIdentifier) {
        return getFileObject(fileIdentifier).exists();
    }

    public void saveObject(T configObject) {
        this.executor.execute(() -> {
            FileConfig fileConfig = FileConfig.newConfig(getFileObject(configObject.getFileIdentifier()));
            saveInFileConfig(configObject, fileConfig);
        });
    }

    public void removeObject(String objectId) {
        this.executor.execute(() -> FileConfig.newConfig(getFileObject(objectId)).delete());
    }

    public T getObject(String objectId) {
        File file = getFileObject(objectId);
        if (file.exists())
            return mapFromFileConfig(FileConfig.newConfig(file));
        return null;
    }

    private File getFileObject(String identifier) {
        return new File(fileDirectory, identifier + "." + fileExtension);
    }

    public abstract T mapFromFileConfig(FileConfig fileConfig);

    public abstract FileConfig saveInFileConfig(T configObject, FileConfig fileConfig);

}
