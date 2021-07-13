package eu.koboo.config.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReadUtilities {

    public static List<String> readNio(String filePath) {
        List<String> content = new ArrayList<>();
        try {
            Path path = Paths.get(filePath);
            content = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static List<String> readBuffer(String filePath) {
        List<String> content = new ArrayList<>();
        try {
            File file = new File(filePath);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.add(line);
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}
