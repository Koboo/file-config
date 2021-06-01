package eu.koboo.config;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public static void downloadFile(String urlFrom, File file) {
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();

            URLConnection connection = new URL(urlFrom).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
            FileOutputStream out = new FileOutputStream(file);
            out.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

            out.close();
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzipFile(File zippedFile, Predicate<ZipEntry> unzipCondition) {
        try {

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zippedFile));
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (unzipCondition.test(zipEntry)) {
                    FileOutputStream fos = new FileOutputStream(zipEntry.getName());
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
