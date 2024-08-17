package com.vitalong.inclinometer.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除文件夹下面的文件夹和文件
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 复制文件夹(使用缓冲字节流)
     * @param sourcePath 源文件夹路径
     * @param targetPath 目标文件夹路径
     */
    public static void copyFolder(String sourcePath,String targetPath) throws Exception{
        //源文件夹路径
        File sourceFile = new File(sourcePath);
        //目标文件夹路径
        File targetFile = new File(targetPath);

        if(!sourceFile.exists()){
            throw new Exception("文件夹不存在");
        }
        if(!sourceFile.isDirectory()){
            throw new Exception("源文件夹不是目录");
        }
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        if(!targetFile.isDirectory()){
            throw new Exception("目标文件夹不是目录");
        }

        File[] files = sourceFile.listFiles();
        if(files == null || files.length == 0){
            return;
        }

        for(File file : files){
            //文件要移动的路径
            String movePath = targetFile+File.separator+file.getName();
            if(file.isDirectory()){
                //如果是目录则递归调用
                copyFolder(file.getAbsolutePath(),movePath);
            }else {
                //如果是文件则复制文件
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(movePath));

                byte[] b = new byte[1024];
                int temp = 0;
                while((temp = in.read(b)) != -1){
                    out.write(b,0,temp);
                }
                out.close();
                in.close();
            }
        }
    }

    /**
     * 先根遍历序递归删除文件夹
     *
     * @param dirFile 要被删除的文件或者目录
     * @return 删除成功返回true, 否则返回false
     */
    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }

        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }

        return dirFile.delete();
    }
}
