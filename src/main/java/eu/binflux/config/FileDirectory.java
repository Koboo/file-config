package eu.binflux.config;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
        loadFiles();
    }

    public void loadFiles() {
        File directory = new File(this.fileDirectory);
        objectCache.clear();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                final int length = files.length;
                for (int i = 0; i < length; ++i) {
                    File file = files[i];
                    FileConfig fileConfig = FileConfig.newConfig(file);
                    T configObject = this.mapFromFileConfig(fileConfig);
                    this.objectCache.put(configObject.getFileIdentifier(), configObject);
                }
            }
        } else {
            directory.mkdirs();
        }
    }

    public boolean existsObject(String objectId) {
        return this.objectCache.containsKey(objectId);
    }

    public void saveObject(T configObject) {
        this.objectCache.put(configObject.getFileIdentifier(), configObject);
        this.executor.execute(() -> {
            FileConfig fileConfig = FileConfig.newConfig(getFileObject(configObject.getFileIdentifier()));
            saveInFileConfig(configObject, fileConfig);
        });
    }

    public void removeObject(String objectId) {
        this.objectCache.remove(objectId);
        this.executor.execute(() -> FileConfig.newConfig(getFileObject(objectId)).delete());
    }

    public T getObject(String objectId) {
        T object = this.objectCache.getOrDefault(objectId, null);
        if (object == null) {
            File file = getFileObject(objectId);
            if (file.exists())
                object = mapFromFileConfig(FileConfig.newConfig(file));
        }
        return object;
    }

    public Map<String, T> getObjectMap() {
        return this.objectCache;
    }

    private File getFileObject(String identifier) {
        return new File(fileDirectory, identifier + "." + fileExtension);
    }

    public abstract T mapFromFileConfig(FileConfig fileConfig);

    public abstract FileConfig saveInFileConfig(T configObject, FileConfig fileConfig);

}
