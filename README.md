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

## Create new `List<FileConfig>`
````java
List<FileConfig> fileConfigList = FileConfig.newConfigList("my_config_directory/");
        
List<FileConfig> fileConfigList = FileConfig.newConfigList("my_config_directory/", (FileConfig) onInit -> {
    onInit.init("key", "value");
});
        
File file = new File("my_config_directory/");
List<FileConfig> fileConfigList = FileConfig.newConfigList(file, (FileConfig) onInit -> {
    onInit.init("key", "value");
});
````

## Create new `List<File>`
````java
List<File> fileConfigList = FileConfig.listFiles("my_config_directory/");
````

## Check if `File` exists
````java
boolean exists = FileConfig.exists("my_config.cfg");
        
boolean exists = FileConfig.exists("my_config_directory/", my_config.cfg");
        
File parent = new File("my_config_directory/");
boolean exists = FileConfig.exists(parent, "my_config.cfg");
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
Object value = fileConfig.getOr("key", null);
````
````java
String value = fileConfig.getStringOr("key", "defaultString");
int value = fileConfig.getIntOr("key", 1);
double value = fileConfig.getDoubleOr("key", 1.0);
float value = fileConfig.getFloatOr("key", 0.1);
short value = fileConfig.getShortOr("key", 1);
long value = fileConfig.getLongOr("key", 1L);
boolean value = fileConfig.getBooleanOr("key", true);
byte[] value = fileConfig.getByteArrayOr("key", new byte[0]);
````
