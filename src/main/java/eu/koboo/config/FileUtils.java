package eu.koboo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> readFileContent(String filePath) {
        List<String> content = new ArrayList<>();
        try {
            Path path = Paths.get(filePath);
            content = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
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

    public static File exportResource(String resourcePath) {
        return exportResource(resourcePath, null);
    }

    public static File exportResource(String resourcePath, String filePath) {
        try {
            File file = new File(filePath == null ? resourcePath : filePath);
            if(!file.exists()) {
                file.createNewFile();
            }
            InputStream input = FileConfig.class.getClassLoader().getResourceAsStream(resourcePath);
            FileOutputStream output = new FileOutputStream(file);

            int n = 0;
            while((n = input.read()) != -1) {
                output.write(n);
            }
            if(input != null)
                input.close();
            if(output != null)
                output.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void moveFile(File from, File to) {
        try {
            FileInputStream inputStream = new FileInputStream(from);
            FileOutputStream outputStream = new FileOutputStream(to);
            int n = 0;
            while ((n = inputStream.read()) != -1) {
                outputStream.write(n);
            }
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<File> listFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File directory = new File(path);
        if (directory.exists()) {
            File[] dirFiles = directory.listFiles();
            if (dirFiles != null)
                for (int i = 0; i < dirFiles.length; i++) {
                    File file = dirFiles[i];
                    fileList.add(file);
                }
        }
        return fileList;
    }
}
