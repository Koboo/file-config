package eu.binflux.config.data;


import eu.binflux.config.FileConfig;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class DataDirectory<T extends DataFile> {

    private final String directoryPath;
    private final String fileExtension;

    private final Map<String, T> dataFileCache;
    private final Map<String, FileConfig> fileConfigCache;

    private final AtomicReference<Boolean> update;
    private final Queue<Runnable> taskQueue;

    public DataDirectory(String directoryPath, String fileExtension) {
        this.directoryPath = directoryPath;
        this.fileExtension = "." + fileExtension;
        this.dataFileCache = new HashMap<>();
        this.fileConfigCache = new HashMap<>();
        this.update = new AtomicReference<>(true);
        this.taskQueue = new ArrayDeque<>();
        Thread fileThread = new Thread(() -> {
            while (update.get() && !taskQueue.isEmpty()) {
                Runnable runnable = taskQueue.poll();
                runnable.run();
            }
        });
        loadFileCache();
        fileThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            update.set(false);
            taskQueue.forEach(Runnable::run);
        }));
    }

    private void register(Runnable runnable) {
        this.taskQueue.add(runnable);
    }

    private File getDirectory() {
        return new File(this.directoryPath);
    }

    @SuppressWarnings("all")
    private List<File> listFiles() {
        List<File> fileList = new ArrayList<>();
        File directory = getDirectory();
        if (!directory.exists())
            directory.mkdirs();
        if (directory.listFiles() != null && directory.listFiles().length > 0)
            fileList.addAll(Arrays.asList(directory.listFiles()));
        return fileList;
    }

    private String getIdentifier(File file) {
        return file.getName().replaceFirst(fileExtension, "");
    }

    private FileConfig getFileConfig(String identifier) {
        FileConfig fileConfig;
        if (fileConfigCache.containsKey(identifier)) {
            fileConfig = fileConfigCache.get(identifier);
        } else {
            fileConfig = FileConfig.newConfig(getFile(identifier));
            fileConfigCache.put(identifier, fileConfig);
        }
        return fileConfig;
    }

    private File getFile(String identifier) {
        return new File(directoryPath, identifier + fileExtension);
    }

    private List<FileConfig> listFileConfig() {
        List<FileConfig> fileConfigList = listFiles()
                .parallelStream()
                .map(this::getIdentifier)
                .map(this::getFileConfig)
                .collect(Collectors.toList());
        return fileConfigList.isEmpty() ? new ArrayList<>() : fileConfigList;
    }

    private void loadFileCache() {
        listFileConfig().forEach(this::doMapping);
    }

    private T doMapping(FileConfig fileConfig) {
        String identifier = getIdentifier(fileConfig.getFile());
        T object = this.dataFileCache.getOrDefault(identifier, null);
        if (object == null && getFile(identifier).exists()) {
            object = mapFromFileConfig(getFileConfig(identifier));
            dataFileCache.put(identifier, object);
        }
        return object;
    }


    public void save(T dataFile) {
        this.dataFileCache.put(dataFile.getFileIdentifier(), dataFile);
        register(() -> {
            FileConfig fileConfig = saveInFileConfig(dataFile, getFileConfig(dataFile.getFileIdentifier()));
            this.fileConfigCache.put(dataFile.getFileIdentifier(), fileConfig);
        });
    }

    public void delete(String identifier) {
        register(() -> {
            FileConfig fileConfig = getFileConfig(identifier);
            T configObject = doMapping(fileConfig);
            if (configObject.getFileIdentifier().equals(identifier))
                fileConfig.delete();
        });
        this.dataFileCache.remove(identifier);
        this.fileConfigCache.remove(identifier);
    }

    public Map<String, T> getMap() {
        return this.dataFileCache;
    }

    public boolean containsAny(Filter.ExistsFilter... filters) {
        List<FileConfig> fileConfigList = listFileConfig();
        if (!fileConfigList.isEmpty()) {
            return fileConfigList
                    .stream()
                    .anyMatch(config -> {
                        for (Filter.ExistsFilter filter : filters) {
                            if (!config.containsKey(filter.getKey()))
                                return false;
                        }
                        return true;
                    });
        }
        return false;
    }

    public boolean containsAny(Filter.EqualsFilter... filters) {
        List<FileConfig> fileConfigList = listFileConfig();
        if (!fileConfigList.isEmpty()) {
            return fileConfigList
                    .stream()
                    .anyMatch(config -> {
                        for (Filter.EqualsFilter filter : filters) {
                            if (!config.containsKey(filter.getKey()) || !config.get(filter.getKey()).equals(filter.getValue()))
                                return false;
                        }
                        return true;
                    });
        }
        return false;
    }

    public T findAny(Filter.ExistsFilter... filters) {
        return listFileConfig()
                .stream()
                .filter(config -> {
                    for (Filter.ExistsFilter filter : filters) {
                        if (!config.containsKey(filter.getKey()))
                            return false;
                    }
                    return true;
                })
                .map(this::doMapping)
                .findAny()
                .orElse(null);
    }

    public T findAny(Filter.EqualsFilter... filters) {
        return listFileConfig()
                .stream()
                .filter(config -> {
                    for (Filter.EqualsFilter filter : filters) {
                        if (!config.containsKey(filter.getKey()) || !config.get(filter.getKey()).equals(filter.getValue()))
                            return false;
                    }
                    return true;
                })
                .map(this::doMapping)
                .findAny()
                .orElse(null);
    }

    public List<T> collectAny(Filter.ExistsFilter... filters) {
        return listFileConfig()
                .stream()
                .filter(config -> {
                    for (Filter.ExistsFilter filter : filters) {
                        if (!config.containsKey(filter.getKey()))
                            return false;
                    }
                    return true;
                })
                .map(this::doMapping)
                .collect(Collectors.toList());
    }

    public List<T> collectAny(Filter.EqualsFilter... filters) {
        return listFileConfig()
                .stream()
                .filter(config -> {
                    for (Filter.EqualsFilter filter : filters) {
                        if (!config.containsKey(filter.getKey()) || !config.get(filter.getKey()).equals(filter.getValue()))
                            return false;
                    }
                    return true;
                })
                .map(this::doMapping)
                .collect(Collectors.toList());
    }

    public abstract T mapFromFileConfig(FileConfig fileConfig);

    public abstract FileConfig saveInFileConfig(T configObject, FileConfig fileConfig);

}
