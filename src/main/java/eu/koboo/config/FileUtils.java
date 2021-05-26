package eu.koboo.config;

import java.io.*;
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

    public static List<String> readFileContentBuffer(String filePath) {
        List<String> content = new ArrayList<>();
        try {
            File file = new File(filePath);
            if(file.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                String line;
                while((line = reader.readLine()) != null) {
                    content.add(line);
                }
                reader.close();
            }
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
}
