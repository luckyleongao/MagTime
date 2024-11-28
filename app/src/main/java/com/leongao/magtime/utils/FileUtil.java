package com.leongao.magtime.utils;

import android.os.Environment;

import com.leongao.magtime.app.MyApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileUtil {

    private static String fileDir;
    private static String downloadFilePath;

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @param deleteRootDir 是否删除根目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir, boolean deleteRootDir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下的文件
            for (int i = 0; i < children.length; i ++) {
                boolean success = deleteDir(new File(dir, children[i]), true);
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return !deleteRootDir || dir.delete();
    }


    public static boolean isFileExist(Path filePath) {
        // file exists and it is not a directory
        return Files.exists(filePath) && !Files.isDirectory(filePath);
    }

    public static boolean isDirectoryEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            }
        }
        return false;
    }

    public static List<String> getAllFilesAbsolutePath(File dir) {
        List<String> list = new ArrayList<>();
        if (dir == null) return list;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }

}
