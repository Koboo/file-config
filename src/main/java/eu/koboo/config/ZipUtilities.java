package eu.koboo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtilities {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public static void zipFile(String zipFileName, String... filesToZip) {
        zipFile(DEFAULT_BUFFER_SIZE, zipFileName, filesToZip);
    }

    public static void zipFile(String zipFileName, File... filesToZip) {
        zipFile(DEFAULT_BUFFER_SIZE, zipFileName, filesToZip);
    }

    public static void zipFile(int bufferSize, String zipFileName, String... filesToZip) {
        List<File> fileList = new ArrayList<>();
        for(String filePath : filesToZip) {
            fileList.add(new File(filePath));
        }
        zipFile(bufferSize, zipFileName, fileList.toArray(new File[0]));
    }

    public static void zipFile(int bufferSize, String zipFileName, File... filesToZip) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName))) {
            for (File file : filesToZip) {
                zipSpecificFile(bufferSize, file, file.getName(), zos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unzipFile(String zipFile, String toDirectory) {
        unzipFile(DEFAULT_BUFFER_SIZE, zipFile, toDirectory);
    }

    public static void unzipFile(int bufferSize, String zipFile, String toDirectory) {
        if(toDirectory == null)
            toDirectory = "";
        File destFile = new File(toDirectory);
        byte[] buffer = new byte[bufferSize];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = newFile(destFile, entry);
                if(entry.isDirectory()) {
                    if(!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Directory creation failed: " + newFile.getAbsolutePath());
                    }
                } else {
                    File parentFile = newFile.getParentFile();
                    if(!parentFile.isDirectory() && !parentFile.mkdirs()) {
                        throw new IOException("Directory creation failed: " + parentFile.getAbsolutePath());
                    }

                    try(FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void zipSpecificFile(int bufferSize, File fileToZip, String zipFileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden())
            return;
        System.out.println("Zip: " + fileToZip.getPath());
        if (fileToZip.isDirectory()) {
            String fileName = fileToZip.getPath();
            String entryName = fileName.endsWith("/") ? fileName : fileName + "/";
            zos.putNextEntry(new ZipEntry(entryName));
            zos.closeEntry();
            File[] children = fileToZip.listFiles();
            if (children == null)
                return;
            for (File childFile : children) {
                zipSpecificFile(bufferSize, childFile, zipFileName + "/" + childFile.getName(), zos);
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(zipFileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[bufferSize];
            int len;
            while ((len = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File newFile(File destinationDir, ZipEntry entry) throws IOException {
        File destFile = new File(destinationDir.getAbsolutePath(), entry.getName());
        if (!destFile.getAbsolutePath().startsWith(destinationDir.getAbsolutePath() + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + destFile.getAbsolutePath() + " | " + destinationDir.getAbsoluteFile() + File.separator);
        }
        return destFile;
    }

}
