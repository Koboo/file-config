package eu.binflux.config;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"all"})
public class FileConfig {

    /*     VARIABLES     */

    private static final String SEPARATOR = ": ";
    private static final String LIST_SPLITTER = "  - ";

    /*     CONFIG METHODS     */

    public static FileConfig newConfig(String file) {
        return new FileConfig(file, null);
    }

    public static FileConfig newConfig(String file, Consumer<FileConfig> consumer) {
        return new FileConfig(file, consumer);
    }

    public static FileConfig newConfig(File file) {
        return new FileConfig(file.getPath(), null);
    }

    public static FileConfig newConfig(File file, Consumer<FileConfig> consumer) {
        return new FileConfig(file.getPath(), consumer);
    }

    /*     CONFIG DIRECTORY METHODS     */

    public static List<FileConfig> newConfigList(String parent) {
        return newConfigList(new File(parent), null);
    }

    public static List<FileConfig> newConfigList(String parent, Consumer<FileConfig> consumer) {
        return newConfigList(new File(parent), consumer);
    }

    public static List<FileConfig> newConfigList(File parent) {
        return newConfigList(parent, null);
    }

    public static List<FileConfig> newConfigList(File parent, Consumer<FileConfig> consumer) {
        ArrayList<FileConfig> configArrayList = new ArrayList<>();
        if (!parent.exists())
            parent.mkdirs();
        for (File file : Objects.requireNonNull(parent.listFiles())) {
            FileConfig fileConfig = newConfig(file, consumer);
            configArrayList.add(fileConfig);
        }
        return configArrayList;
    }

    /*     STATIC FUNCTION METHODS     */

    public static boolean exists(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean exists(String parent, String filePath) {
        return new File(parent, filePath).exists();
    }

    public static boolean exists(File parent, String filePath) {
        return new File(parent, filePath).exists();
    }

    public static void delete(String filePath) {
        delete(new File(filePath));
    }

    public static void delete(File file) {
        try {
            if (file.exists())
                file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*     CLASS CONSTRUCTOR     */

    private final String filePath;

    FileConfig(String filePath, Consumer<FileConfig> consumer) {
        this.filePath = filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                File parents = file.getParentFile();
                if (parents != null)
                    parents.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (consumer != null)
            consumer.accept(this);
    }

    /*     GETTER METHODS     */

    public String getString(String key) {
        return getContentValue(key);
    }

    public Integer getInt(String key) {
        return Integer.parseInt(getContentValue(key));
    }

    public Double getDouble(String key) {
        return Double.parseDouble(getContentValue(key));
    }

    public Float getFloat(String key) {
        return Float.parseFloat(getContentValue(key));
    }

    public Short getShort(String key) {
        return Short.parseShort(getContentValue(key));
    }

    public Long getLong(String key) {
        return Long.parseLong(getContentValue(key));
    }

    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(getContentValue(key));
    }

    /*     GETTER DEFAULT METHODS     */

    public String getStringOr(String key, String defaultS) {
        try {
            String value = getString(key);
            return value == null ? defaultS : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultS;
    }

    public int getIntOr(String key, int defaultI) {
        try {
            Integer value = getInt(key);
            return value == null ? defaultI : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultI;
    }

    public double getDoubleOr(String key, double defaultD) {
        try {
            Double value = getDouble(key);
            return value == null ? defaultD : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultD;
    }

    public float getFloatOr(String key, float defaultF) {
        try {
            Float value = getFloat(key);
            return value == null ? defaultF : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultF;
    }

    public short getShortOr(String key, short defaultS) {
        try {
            Short value = getShort(key);
            return value == null ? defaultS : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultS;
    }

    public long getLongOr(String key, long defaultL) {
        try {
            Long value = getLong(key);
            return value == null ? defaultL : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultL;
    }

    public boolean getBooleanOr(String key, boolean defaultB) {
        try {
            Boolean value = getBoolean(key);
            return value == null ? defaultB : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultB;
    }

    /*     NON-STATIC FUNCTION METHODS     */

    public <T> void init(String key, T value) {
        init(key, value, null);
    }

    public <T> void init(String key, T value, String comment) {
        if (!contains(key))
            set(key, value, comment);
    }

    public void delete() {
        delete(this.filePath);
    }

    public boolean contains(String key) {
        return fileContent().parallelStream().anyMatch(line -> line.startsWith(key));
    }

    public <T> void set(String key, T value) {
        set(key, value, null);
    }

    public <T> void set(String key, T value, String comment) {
        try {
            String keyValueString = key + SEPARATOR + value;
            ArrayList<String> contentList = new ArrayList<>();
            String lastContent = null;
            for (String contentString : fileContent()) {
                if (contentString.startsWith(key + SEPARATOR) && !contentString.equals(keyValueString)) {
                    contentString = keyValueString;
                    if (lastContent != null && lastContent.startsWith("#") && comment != null && !lastContent.equals(comment))
                        contentList.remove(lastContent);
                    if (comment != null)
                        contentList.add("#" + comment);
                }
                contentList.add(contentString);
                lastContent = contentString;
            }
            if (!contentList.contains(keyValueString)) {
                if (comment != null)
                    contentList.add("#" + comment);
                contentList.add(keyValueString);
            }
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            contentList.forEach(writer::println);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> getList(String key) {
        ArrayList<T> arrayList = new ArrayList<>();
        boolean readList = false;
        for (String contentLines : fileContent()) {
            if (readList && contentLines.startsWith(LIST_SPLITTER))
                arrayList.add((T) contentLines.replaceFirst(LIST_SPLITTER, ""));
            else
                readList = false;
            if (contentLines.startsWith(key + SEPARATOR))
                readList = true;
        }
        return arrayList;
    }

    public <T extends List> void setList(String key, T value) {
        setList(key, value, null);
    }

    public <T extends List> void setList(String key, T value, String comment) {
        try {
            ArrayList<String> contentList = new ArrayList<>();
            boolean readList = false;
            String lastContent = null;
            for (String contentString : fileContent()) {
                if (!readList && !contentString.startsWith(key + SEPARATOR) && !contentString.startsWith(LIST_SPLITTER))
                    contentList.add(contentString);
                // End of List
                if (!(readList && contentString.startsWith(LIST_SPLITTER)))
                    readList = false;
                // Start of List
                if (contentString.startsWith(key + SEPARATOR)) {
                    readList = true;
                    if (lastContent != null && lastContent.startsWith("#") && comment != null && !lastContent.equals(comment))
                        contentList.remove(lastContent);
                }
                lastContent = contentString;
            }
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            contentList.forEach(writer::println);
            List list = getList(key);
            for (Object object : value) {
                if (!list.contains(object))
                    list.add(object);
            }
            for (Object object : list) {
                if (!value.contains(object))
                    list.remove(object);
            }
            if (comment != null)
                writer.println("#" + comment);
            writer.println(key + SEPARATOR);
            list.forEach(line -> writer.println(LIST_SPLITTER + line));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T> T getContentValue(String key) {
        for (String contentLines : fileContent()) {
            if (contentLines.startsWith(key)) {
                return (T) contentLines.replaceFirst(key + SEPARATOR, "");
            }
        }
        return null;
    }

    private ArrayList<String> fileContent() {
        ArrayList<String> content = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                content.add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}

