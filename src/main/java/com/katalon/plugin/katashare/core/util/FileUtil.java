package com.katalon.plugin.katashare.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class FileUtil {
    
    public static boolean isEmptyFolder(File folder) {
        if (folder == null) {
            throw new NullPointerException("Folder must not be null");
        }
        
        if (folder.exists() && !folder.isDirectory()) {
            throw new IllegalArgumentException("'" + folder + "' is not a directory");
        }
        
        File[] files = folder.listFiles();
        return files != null && files.length == 0;
    }
    
    public static String getAvailableFolderName(File parentFolder, String prefix) {
        if (parentFolder == null) {
            throw new NullPointerException("Folder must not be null");
        }
        
        if (parentFolder.exists() && !parentFolder.isDirectory()) {
            throw new IllegalArgumentException("'" + parentFolder + "' is not a directory");
        }
        List<String> availableFolderNames = Arrays.asList(parentFolder.listFiles())
                .stream()
                .filter(f -> f.isDirectory())
                .map(f -> f.getName())
                .collect(Collectors.toList());
        String folderName = prefix;
        int index = 0;
        while (isNameDuplicated(folderName, availableFolderNames)) {
            index++;
            folderName = prefix + " (" + index + ")";
        }
        return folderName;
    }
    
    private static boolean isNameDuplicated(String fileName, List<String> availableNames) {
        for (String name : availableNames) {
            if (fileName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static List<File> listFilesWithExtension(File folder, String extension) throws IOException {
        if (folder == null) {
            throw new NullPointerException("Folder must not be null");
        }
        
        if (folder.exists() && !folder.isDirectory()) {
            throw new IllegalArgumentException("'" + folder + "' is not a directory");
        }
        
        if (StringUtils.isBlank(extension)) {
            throw new IllegalArgumentException("Extension must not be null or empty");
        }
        
        return Files.walk(Paths.get(folder.getAbsolutePath()))
            .filter(p -> FilenameUtils.getExtension(p.toFile().getAbsolutePath()).equals(extension))
            .map(p -> p.toFile())
            .collect(Collectors.toList());
    }
}
