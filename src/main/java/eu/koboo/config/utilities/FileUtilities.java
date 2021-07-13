package eu.koboo.config.utilities;

import eu.koboo.config.FileConfig;

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

public class FileUtilities {

    public static File exportResource(String resourcePath) {
        return exportResource(resourcePath, null);
    }

    public static File exportResource(String resourcePath, String filePath) {
        try {
            File file = new File(filePath == null ? resourcePath : filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream input = FileConfig.class.getClassLoader().getResourceAsStream(resourcePath);
            FileOutputStream output = new FileOutputStream(file);

            int n = 0;
            while ((n = input.read()) != -1) {
                output.write(n);
            }
            if (input != null)
                input.close();
            if (output != null)
                output.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void move(File from, File to) {
        try {
            FileInputStream inputStream = new FileInputStream(from);
            FileOutputStream outputStream = new FileOutputStream(to);
            int n = 0;
            while ((n = inputStream.read()) != -1) {
                outputStream.write(n);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<File> listOf(String path) {
        List<File> fileList = new ArrayList<>();
        File directory = new File(path);
        if (directory.exists()) {
            File[] dirFiles = directory.listFiles();
            if (dirFiles != null)
                for (int i = 0; i < dirFiles.length; i++) {
                    File file = dirFiles[i];
                    if(file.exists()) {
                        fileList.add(file);
                    }
                }
        }
        return fileList;
    }

    public static void download(String urlFrom, File toPath) {
        try {
            if (toPath.exists())
                deleteRecursively(toPath);
            toPath.createNewFile();

            URLConnection connection = new URL(urlFrom).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
            FileOutputStream out = new FileOutputStream(toPath);
            out.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

            out.close();
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean deleteRecursively(String toDeleteFile) {
        return deleteRecursively(new File(toDeleteFile));
    }

    public static boolean deleteRecursively(File toDeleteFile) {
        File[] allContents = toDeleteFile.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if (!file.isHidden() && !Files.isSymbolicLink(Paths.get(file.toURI())))
                    deleteRecursively(file);
            }
        }
        return toDeleteFile.delete();
    }
}
