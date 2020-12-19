package eu.binflux.config;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class FileDirectory<T extends ConfigObject> {

    private final String fileDirectory;
    private final String fileExtension;

    private final Map<String, T> objectCache;
    private final ExecutorService executor;

    public FileDirectory(String fileDirectory, String fileExtension) {
        this.fileDirectory = fileDirectory;
        this.fileExtension = fileExtension;
        this.objectCache = new HashMap<>();
        this.executor = Executors.newSingleThreadExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));

        File directory = new File(fileDirectory);
        if (directory.listFiles() != null)
            for (File file : directory.listFiles()) {
                FileConfig fileConfig = FileConfig.newConfig(file);
                T configObject = mapFromFileConfig(fileConfig);
                objectCache.put(configObject.getFileIdentifier(), configObject);
            }
    }

    public boolean existsObject(String objectId) {
        return this.objectCache.containsKey(objectId);
    }

    public void saveObject(T configObject) {
        this.objectCache.put(configObject.getFileIdentifier(), configObject);
        this.executor.execute(() -> {
            FileConfig fileConfig = FileConfig.newConfig(getFileObject(configObject));
            saveInFileConfig(configObject, fileConfig);
        });
    }

    public void removeObject(String objectId) {
        this.objectCache.remove(objectId);
        this.executor.execute(() -> {
            for (File file : Objects.requireNonNull(new File(fileDirectory).listFiles())) {
                FileConfig fileConfig = FileConfig.newConfig(file);
                T configObject = mapFromFileConfig(fileConfig);
                if (configObject.getFileIdentifier().equals(objectId))
                    fileConfig.delete();
            }
        });
    }

    public T getObject(String objectId) {
        return this.objectCache.get(objectId);
    }

    public Map<String, T> getObjectMap() {
        return this.objectCache;
    }

    private File getFileObject(ConfigObject configObject) {
        return new File(fileDirectory, configObject.getFileIdentifier() + "." + fileExtension);
    }

    public abstract T mapFromFileConfig(FileConfig fileConfig);
    public abstract FileConfig saveInFileConfig(T configObject, FileConfig fileConfig);

}
