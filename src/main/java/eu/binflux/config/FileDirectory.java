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
                T configObject = mapFromConfig(fileConfig);
                objectCache.put(configObject.getFileIdentifier(), configObject);
            }
    }

    public boolean existsFile(String entryId) {
        return this.objectCache.containsKey(entryId);
    }

    public void saveFile(T configObject) {
        this.objectCache.put(configObject.getFileIdentifier(), configObject);
        this.executor.execute(() -> {
            FileConfig fileConfig = FileConfig.newConfig(getFileObject(configObject));
            mapToConfig(configObject, fileConfig);
        });
    }

    public void removeFile(String objectId) {
        this.objectCache.remove(objectId);
        this.executor.execute(() -> {
            for (File file : Objects.requireNonNull(new File(fileDirectory).listFiles())) {
                FileConfig fileConfig = FileConfig.newConfig(file);
                T configObject = mapFromConfig(fileConfig);
                if (configObject.getFileIdentifier().equals(objectId))
                    fileConfig.delete();
            }
        });
    }

    public T getFromFile(String objectId) {
        return this.objectCache.get(objectId);
    }

    public Map<String, T> getObjectMap() {
        return this.objectCache;
    }

    private File getFileObject(ConfigObject configObject) {
        return new File(fileDirectory, configObject.getFileIdentifier() + "." + fileExtension);
    }

    public abstract T mapFromConfig(FileConfig fileConfig);
    public abstract FileConfig mapToConfig(T configObject, FileConfig fileConfig);

}
