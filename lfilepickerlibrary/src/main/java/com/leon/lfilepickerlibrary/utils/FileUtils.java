package com.leon.lfilepickerlibrary.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FileUtils {

    private Context mContext;
    private static String SDPATH;
    private String FILESPATH;

    public static List<File> getFileListByDirPath(String path, FileFilter filter) {
        File directory = new File(path);
        File[] files = directory.listFiles(filter);
        List<File> result = new ArrayList<>();
        if (files == null) {
            return new ArrayList<>();
        }

        for (int i = 0; i < files.length; i++) {
            result.add(files[i]);
        }
        Collections.sort(result, new FileComparator());
        return result;
    }

    public static String cutLastSegmentOfPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件长度
     */
    public static long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序,同时过滤掉不符合大小要求的文件
     *
     * @param path
     * @return List<File>
     */
    public static List<File> getFileList(String path, FileFilter filter, boolean isGreater, long targetSize) {
        List<File> list = FileUtils.getFileListByDirPath(path, filter);
        //进行过滤文件大小
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            File f = (File) iterator.next();
            if (f.isFile()) {
                //获取当前文件大小
                long size = FileUtils.getFileLength(f);
                if (isGreater) {
                    //当前想要留下大于指定大小的文件，所以过滤掉小于指定大小的文件
                    if (size < targetSize) {
                        iterator.remove();
                    }
                } else {
                    //当前想要留下小于指定大小的文件，所以过滤掉大于指定大小的文件
                    if (size > targetSize) {
                        iterator.remove();
                    }
                }
            }
        }
        return list;
    }

    //表示SDCard存在并且可以读写
    public static boolean isSDCardState() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取SDCard文件路径
     */
    public static String getSDCardPath() {
        if (isSDCardState()) {//如果SDCard存在并且可以读写
            SDPATH = Environment.getExternalStorageDirectory().getPath();
            return SDPATH;
        } else {
            return null;
        }
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName 要创建的目录名
     * @return 创建得到的目录
     */
    public static File createSDDirection(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 创建文件
     *
     * @param filePath
     * @return
     */
    public static boolean createFile(String filePath) {
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


    /**
     * 判断SD卡里面某个文件是否存在
     *
     * @return true:存在  false:不存在
     */
    public static boolean isFileExits(String fileName) {

        return (new File(SDPATH + fileName)).exists();
    }

    /**
     * 给孔洞的文件夹加Namber_前缀是为了在选择文件时候点击的时候判断是否是孔号的文件夹
     *
     * @param holeName
     * @return
     */
    public static String fetchHoleName(String holeName) {

//        return "Namber_" + holeName;
        return "#" + holeName;
    }


    /**
     * 删除指定目录下的所有文件和子目录，保留指定的子目录
     *
     * @param dir             需要清除的目录
     * @param preserveDirName 要保留的子目录名称
     * @return true 表示成功，false 表示失败
     */
    public static boolean deleteDirectoryContent(File dir, String preserveDirName) {
        if (dir != null && dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    // 如果是需要保留的目录，跳过
                    if (child.isDirectory() && child.getName().equals(preserveDirName)) {
                        continue;
                    }

                    // 如果是目录，递归删除
                    if (child.isDirectory()) {
                        deleteDirectoryContent(child, preserveDirName);
                    }

                    // 删除文件或空目录
                    if (!child.delete()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
