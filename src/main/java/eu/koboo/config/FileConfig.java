package eu.koboo.config;

import eu.koboo.config.utilities.FileUtilities;
import eu.koboo.config.utilities.ReadUtilities;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"all"})
public class FileConfig {

    private static final String SEPARATOR = ": ";
    private static final String LIST_SPLITTER = "  - ";

    private final String filePath;

    protected FileConfig(String filePath, Consumer<FileConfig> consumer) {
        this.filePath = filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                File parents = file.getParentFile();
                if (parents != null && !parents.exists())
                    parents.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (consumer != null)
            consumer.accept(this);
    }

    public <T> void set(String key, T value) {
        set(key, value, null);
    }

    public <T> void set(String key, T value, String comment) {
        try {
            String valueSet = value.toString();
            if (value instanceof byte[]) {
                valueSet = new String((byte[]) value);
            }
            String keyValueString = key + SEPARATOR + valueSet;
            List<String> contentList = new ArrayList<>();
            String lastLine = null;
            for (String currentLine : ReadUtilities.readBuffer(filePath)) {
                if (currentLine.startsWith(key + SEPARATOR) && !currentLine.equals(keyValueString)) {
                    currentLine = keyValueString;
                    if (lastLine != null && lastLine.startsWith("#") && comment != null && !lastLine.equals(comment)) {
                        contentList.remove(lastLine);
                    }
                    if (comment != null) {
                        contentList.add("#" + comment);
                    }
                }
                contentList.add(currentLine);
                lastLine = currentLine;
            }
            if (!contentList.contains(keyValueString)) {
                if (comment != null) {
                    contentList.add("#" + comment);
                }
                contentList.add(keyValueString);
            }
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            contentList.forEach(writer::println);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        try {
            key = key + SEPARATOR;
            List<String> contentList = new ArrayList<>();
            String lastContent = null;
            for (String contentString : ReadUtilities.readBuffer(filePath)) {
                if (!contentString.startsWith(key)) {
                    contentList.add(contentString);
                }
            }
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            contentList.forEach(writer::println);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> getList(String key) {
        List<T> arrayList = new ArrayList<>();
        boolean readList = false;
        for (String contentLines : ReadUtilities.readBuffer(filePath)) {
            if (readList && contentLines.startsWith(LIST_SPLITTER)) {
                String value = contentLines.replaceFirst(LIST_SPLITTER, "");
                arrayList.add((T) parseType(value));
            } else {
                readList = false;
            }
            if (contentLines.startsWith(key + SEPARATOR)) {
                readList = true;
            }
        }
        return arrayList;
    }

    public <T extends List> void setList(String key, T value) {
        setList(key, value, null);
    }

    public <T extends List> void setList(String key, T value, String comment) {
        try {
            List<String> contentList = new ArrayList<>();
            boolean foundListKey = false;
            String lastLine = null;
            for (String currentLine : ReadUtilities.readBuffer(filePath)) {
                // No current list
                if (!foundListKey) {
                    // Is this the list key?
                    if (currentLine.equals(key + SEPARATOR)) {
                        foundListKey = true;
                        if (lastLine != null && lastLine.startsWith("#") && comment != null && !lastLine.equals(comment)) {
                            contentList.remove(lastLine);
                        }
                    } else {
                        contentList.add(currentLine);
                    }
                } else {
                    if (!currentLine.startsWith(LIST_SPLITTER) && (currentLine.contains(SEPARATOR) || currentLine.startsWith("#"))) {
                        contentList.add(currentLine);
                        foundListKey = false;
                    }
                }
                lastLine = currentLine;
            }
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            contentList.forEach(writer::println);
            List list = getList(key);
            for (Object object : value) {
                if (!list.contains(object)) {
                    list.add(object);
                }
            }
            for (Object object : list) {
                if (!value.contains(object)) {
                    list.remove(object);
                }
            }
            if (comment != null) {
                writer.println("#" + comment);
            }
            writer.println(key + SEPARATOR);
            list.forEach(line -> writer.println(LIST_SPLITTER + line));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> allKeyValues() {
        Map<String, Object> valueMap = new HashMap<>();
        for (String fileContent : ReadUtilities.readBuffer(filePath)) {
            String[] contentSplit = fileContent.split(SEPARATOR);
            if (contentSplit.length >= 2) {
                String key = contentSplit[0];
                String value = contentSplit[1];
                valueMap.put(key, value);
            }
        }
        return valueMap;
    }

    public List<String> allKeys() {
        List<String> keyList = new ArrayList<>();
        for (String fileContent : ReadUtilities.readBuffer(filePath)) {
            String[] contentSplit = fileContent.split(SEPARATOR);
            if (contentSplit.length >= 2) {
                String key = contentSplit[0];
                keyList.add(key);
            }
        }
        return keyList;
    }

    public <T> T get(String key) {
        key = key + SEPARATOR;
        for (String contentLines : ReadUtilities.readBuffer(filePath)) {
            if (contentLines.startsWith(key)) {
                String value = contentLines.replaceFirst(key, "");
                if (value.equalsIgnoreCase("")) {
                    return (T) getList(key);
                }
                return parseType(value);
            }
        }
        return null;
    }

    private <T> T parseType(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return (T) Boolean.valueOf(value);
        }
        if (value.matches("-?\\d+(\\.\\d+)?")) {
            if (value.contains(".")) {
                double valueDouble = Double.valueOf(value);
                if (valueDouble <= Float.MAX_VALUE && valueDouble >= Float.MIN_VALUE) {
                    return (T) Float.valueOf(value);
                }
                return (T) Double.valueOf(value);
            } else {
                long valueLong = Long.valueOf(value);
                if (valueLong <= Short.MAX_VALUE && valueLong >= Short.MIN_VALUE) {
                    return (T) Short.valueOf(value);
                }
                if (valueLong <= Integer.MAX_VALUE && valueLong >= Integer.MIN_VALUE) {
                    return (T) Integer.valueOf(value);
                }
                return (T) Long.valueOf(value);
            }
        }
        return (T) value;
    }

    public int getLineCount() {
        return ReadUtilities.readBuffer(filePath).size();
    }

    public String getFileName() {
        return getFile().getName();
    }

    public File getFile() {
        return new File(filePath);
    }

    public void delete() {
        FileUtilities.deleteRecursively(filePath);
    }

    public boolean containsKey(String key) {
        String finalKey = key + SEPARATOR;
        return ReadUtilities.readBuffer(filePath).parallelStream().anyMatch(line -> line.startsWith(finalKey));
    }

    public boolean containsValue(String value) {
        String finalValue = SEPARATOR + value;
        return ReadUtilities.readBuffer(filePath).parallelStream().anyMatch(line -> line.endsWith(finalValue));
    }

    public <T> void init(String key, T value, String comment) {
        if (!containsKey(key)) {
            set(key, value, comment);
        }
    }

    public <T> void init(String key, T value) {
        init(key, value, null);
    }

    public String getString(String key) {
        return get(key).toString();
    }

    public String getString(String key, String defaultS) {
        try {
            String value = getString(key);
            return value == null ? defaultS : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultS;
    }

    public Integer getInt(String key) {
        return Integer.parseInt(get(key).toString());
    }

    public int getInt(String key, int defaultI) {
        try {
            Integer value = getInt(key);
            return value == null ? defaultI : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultI;
    }

    public Double getDouble(String key) {
        return Double.parseDouble(get(key).toString());
    }

    public double getDouble(String key, double defaultD) {
        try {
            Double value = getDouble(key);
            return value == null ? defaultD : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultD;
    }

    public Float getFloat(String key) {
        return Float.parseFloat(get(key).toString());
    }

    public float getFloat(String key, float defaultF) {
        try {
            Float value = getFloat(key);
            return value == null ? defaultF : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultF;
    }

    public Short getShort(String key) {
        return Short.parseShort(get(key).toString());
    }

    public short getShort(String key, short defaultS) {
        try {
            Short value = getShort(key);
            return value == null ? defaultS : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultS;
    }

    public Long getLong(String key) {
        return Long.parseLong(get(key).toString());
    }

    public long getLong(String key, long defaultL) {
        try {
            Long value = getLong(key);
            return value == null ? defaultL : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultL;
    }

    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key).toString());
    }

    public boolean getBoolean(String key, boolean defaultB) {
        try {
            Boolean value = getBoolean(key);
            return value == null ? defaultB : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultB;
    }

    public byte[] getByteArray(String key) {
        return get(key).toString().getBytes();
    }

    public byte[] getByteArray(String key, byte[] defaultB) {
        try {
            byte[] value = getByteArray(key);
            return value == null ? defaultB : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultB;
    }
}