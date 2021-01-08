### `FileConfig#newConfig()`

````java
FileConfig fileConfig = FileConfig.newConfig("my_config.cfg");
````
````java
FileConfig fileConfig = FileConfig.newConfig("my_config.cfg", (FileConfig) onInit -> {
    onInit.init("key", "value");
}); 
````
````java
File file = new File("my_config.cfg");
FileConfig fileConfig = FileConfig.newConfig(file, (FileConfig) onInit -> {
    onInit.init("key", "value");
}); 
````

### `FileConfig#newConfigList()`
````java
List<FileConfig> fileConfigList = FileConfig.newConfigList("my_config_directory/");
````
````java
List<FileConfig> fileConfigList = FileConfig.newConfigList("my_config_directory/", (FileConfig) onInit -> {
    onInit.init("key", "value");
});
````
````java
File file = new File("my_config_directory/");
List<FileConfig> fileConfigList = FileConfig.newConfigList(file, (FileConfig) onInit -> {
    onInit.init("key", "value");
});
````

### `FileConfig#listFiles()`
````java
List<File> fileConfigList = FileConfig.listFiles("my_config_directory/");
````

### `FileConfig#exists()`
````java
boolean exists = FileConfig.exists("my_config.cfg");
````
````java
boolean exists = FileConfig.exists("my_config_directory/", my_config.cfg");
````
````java
File parent = new File("my_config_directory/");
boolean exists = FileConfig.exists(parent, "my_config.cfg");
````
### `FileConfig#delete()`
````java
FileConfig.delete("my_config.cfg");
````
````java
File file = new File("my_config.cfg");
FileConfig.delete(file);
````
### `FileConfig#getFile()`
````java
File file = fileConfig.getFile();
````
### `FileConfig#get()`
````java
Object value = fileConfig.get("key");
````
### `FileConfig#getString()`
````java
String value = fileConfig.getString("key");
````
### `FileConfig#getInt()`
````java
Integer value = fileConfig.getInt("key");
````
### `FileConfig#getDouble()`
````java
Double value = fileConfig.getDouble("key");
````
### `FileConfig#getFloat()`
````java
Float value = fileConfig.getFloat("key");
````
### `FileConfig#getShort()`
````java
Short value = fileConfig.getShort("key");
````
### `FileConfig#getLong()`
````java
Long value = fileConfig.getLong("key");
````
### `FileConfig#getBoolean()`
````java
Boolean value = fileConfig.getBoolean("key");
````
### `FileConfig#getByteArray()`
````java
byte[] value = fileConfig.getByteArray("key");
````
### `FileConfig#getOr()`
````java
Object value = fileConfig.getOr("key", null);
````
### `FileConfig#getStringOr()`
````java
String value = fileConfig.getStringOr("key", null);
````
### `FileConfig#getIntOr()`
````java
Integer value = fileConfig.getIntOr("key", null);
````
### `FileConfig#getDoubleOr()`
````java
Double value = fileConfig.getDoubleOr("key", null);
````
### `FileConfig#getFloatOr()`
````java
Float value = fileConfig.getFloatOr("key", null);
````
### `FileConfig#getShortOr()`
````java
Short value = fileConfig.getShortOr("key", null);
````
### `FileConfig#getLongOr()`
````java
Long value = fileConfig.getLongOr("key", null);
````
### `FileConfig#getBooleanOr()`
````java
Boolean value = fileConfig.getBooleanOr("key", null);
````
### `FileConfig#getByteArrayOr()`
````java
byte[] value = fileConfig.getByteArrayOr("key", null);
````
