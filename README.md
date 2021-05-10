## Create new `FileConfig`
````java
FileConfig fileConfig = FileConfig.newConfig("my_config.cfg");
        
FileConfig fileConfig = FileConfig.newConfig("my_config.cfg", (FileConfig) onInit -> {
    onInit.init("key", "value");
}); 
        
File file = new File("my_config.cfg");
FileConfig fileConfig = FileConfig.newConfig(file, (FileConfig) onInit -> {
    onInit.init("key", "value");
}); 
````

## Create new `List<File>`
````java
List<File> fileConfigList = FileConfig.listFiles("my_config_directory/");
````

## Delete `File`
````java
FileConfig.delete("my_config.cfg");

File file = new File("my_config.cfg");
FileConfig.delete(file);
````

## Get `File` of `FileConfig`
````java
File file = fileConfig.getFile();
````

## Value Getter of `FileConfig`
````java
Object value = fileConfig.get("key");
String value = fileConfig.getString("key");
Integer value = fileConfig.getInt("key");
Double value = fileConfig.getDouble("key");
Float value = fileConfig.getFloat("key");
Short value = fileConfig.getShort("key");
Long value = fileConfig.getLong("key");
Boolean value = fileConfig.getBoolean("key");
byte[] value = fileConfig.getByteArray("key");
````

## Value Getter with default Value of `FileConfig`
````java
Object value = fileConfig.get("key", null);
````
````java
String value = fileConfig.getString("key", "defaultString");
int value = fileConfig.getInt("key", 1);
double value = fileConfig.getDouble("key", 1.0);
float value = fileConfig.getFloat("key", 0.1);
short value = fileConfig.getShort("key", 1);
long value = fileConfig.getLong("key", 1L);
boolean value = fileConfig.getBoolean("key", true);
byte[] value = fileConfig.getByteArray("key", new byte[0]);
````
