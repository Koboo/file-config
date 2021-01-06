package eu.binflux.config.collection;

import java.io.*;
import java.util.*;

public class OffHeapMap<V> implements Map<String, V> {

    private static final String PARENT_PATH = "offheap/";
    private static final String FILE_EXTENSTION = ".offheap";

    private final String cacheDirPath;

    public OffHeapMap(String cacheName, boolean deleteOnExit) {
        this.cacheDirPath = PARENT_PATH + cacheName + "/";

        File parentDir = new File(PARENT_PATH);
        if (!parentDir.exists())
            parentDir.mkdirs();
        File cacheDir = new File(this.cacheDirPath);
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        if (deleteOnExit) {
            System.out.println("Delete on Exit");
            Runtime.getRuntime().addShutdownHook(new Thread(this::delete));
        }
    }

    private String prepareFile(Object key) {
        return cacheDirPath + key.toString() + FILE_EXTENSTION;
    }

    private void writeFile(String fileKey, V value) {
        try {
            RandomAccessFile raf = new RandomAccessFile(fileKey, "rw");
            FileOutputStream fileOutput = new FileOutputStream(raf.getFD());
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(value);
            objectOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private V readFile(String fileKey) {
        V value = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(fileKey, "rw");
            FileInputStream fileInput = new FileInputStream(raf.getFD());
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            value = (V) objectInput.readObject();
            objectInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public int size() {
        File cacheDir = new File(this.cacheDirPath);
        File[] files = cacheDir.listFiles();
        if (files != null && files.length > 0)
            return files.length;
        return 0;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return new File(prepareFile(key)).exists();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("This operation is not supported by OffheapMap");
    }

    @Override
    public V get(Object key) {
        String fileKey = prepareFile(key);
        return readFile(fileKey);
    }

    @Override
    public V put(String key, V value) {
        String fileKey = prepareFile(key);
        writeFile(fileKey, value);
        return value;
    }

    @Override
    public V remove(Object key) {
        File file = new File(prepareFile(key));
        if (file.exists())
            file.delete();
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for (String key : m.keySet()) {
            V value = m.get(key);
            put(key, value);
        }
    }

    @Override
    public void clear() {
        File cacheDir = new File(this.cacheDirPath);
        File[] files = cacheDir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                file.delete();
            }
    }

    @Override
    public Set<String> keySet() {
        Set<String> set = new HashSet<>();
        File cacheDir = new File(this.cacheDirPath);
        File[] files = cacheDir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                set.add(file.getName().replaceFirst(FILE_EXTENSTION, ""));
            }
        return set;
    }

    @Override
    public Collection<V> values() {
        Collection<V> collection = new ArrayList<>();
        File cacheDir = new File(this.cacheDirPath);
        File[] files = cacheDir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                String key = file.getName().replaceFirst(FILE_EXTENSTION, "");
                V value = readFile(prepareFile(key));
                if (value != null)
                    collection.add(value);
                else
                    file.delete();
            }
        return collection;
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        Set<Entry<String, V>> set = new HashSet<>();
        File cacheDir = new File(this.cacheDirPath);
        File[] files = cacheDir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                String key = file.getName().replaceFirst(FILE_EXTENSTION, "");
                V value = readFile(key);
                if (value != null)
                    set.add(new AbstractMap.SimpleEntry<>(key, value));
                else
                    file.delete();
            }
        return set;
    }

    public void delete() {
        File cacheDir = new File(this.cacheDirPath);
        cacheDir.delete();
        File parentDir = new File(PARENT_PATH);
        if(parentDir.exists())
            parentDir.delete();
    }

}
