package com.example.datainsert.exagear.test;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileTreePrinter {
//    private String test(){
//        String parentPath = "/sdcard";
//        StringBuilder builder = new StringBuilder();
//
//        File parentFile = new File(parentPath);
//        if(!parentFile.exists()){
//            builder.append("目录不存在");
//            return builder.toString();
//        }else if(!parentFile.isDirectory()){
//            builder.append("不是目录");
//            return builder.toString();
//        }
//
//        ArrayList<A> prepareList = new ArrayList<>();
//        {
//            int totalLen = 0;
//            for (File file : parentFile.listFiles()) {
//                prepareList.add(new A(file, totalLen, file.getName().length()));
//                totalLen += file.getName().length();
////                builder.insert(totalLen,file.getName());
//            }
//        }
//
//        /*
//        第一层文件名：按顺序写入
//        第二层文件名：找到其距离末尾的位置，每个1目录对应的全部2目录，拼成一个字符串后插入。
//        插入后，获取每个2目录的3目录，并配上当前2目录距离结尾的位置
//        第二个2目录开始，插入后，前面所有1目录对应的2目录对应的距离末尾的位置会改变。所以要修改？（每次字符串插入都需要修改）
//         */
//
//        while(prepareList.size()>0){
//            ArrayList<A> nextList = new ArrayList<>();
//            int level = -1;
//            for(A a:prepareList){
//                if(level==-1)
//                    level = a.level;
//
//                a.file.getName()
//            }
//        }
//
//
//    }

    private final static String TAG = "FileTreePrinter";
    public String getDirTree(File file, int level){
        try {
            if(file.isFile()){
                return getPrefix(level)+file.getName();
            }else if(file.isDirectory()){
                if(!file.canRead())
                    throw new RuntimeException("该目录读取权限不足"+file.getAbsolutePath());
                StringBuilder builder = new StringBuilder();
                String prefix = getPrefix(level);
                builder.append(prefix).append(file.getName());
                for(File sub:file.listFiles()){
                    String subStr = getDirTree(sub,level+1);
                    if(subStr!=null)
                        builder.append('\n').append(subStr);
                }
                return builder.toString();
            }else{
                throw new RuntimeException(" 既不是文件也不是文件夹："+file.getAbsolutePath());
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getPrefix(int level){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<level; i++){
            builder.append("| ");
        }
        return builder.append("- ").toString();
    }

    public static void test(){
        new Thread(()->{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    long starttime = System.currentTimeMillis();
//                    String rootDir = "/sdcard/Download";
                    String rootDir = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv";
                    String dirTreeStr = new FileTreePrinter().getDirTree(new File(rootDir),0);
                    Files.write(new File("/sdcard/testdirtree.txt").toPath(),(dirTreeStr==null?"":dirTreeStr).getBytes(StandardCharsets.UTF_8));
                    Log.d(TAG, "callWhenFirstStart: 测试生成目录树-完成, 耗时"+(System.currentTimeMillis()-starttime));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },"测试生成目录树").start();
    }

    static class A{
        File file;
        /**
         * 自身在stringbuilder中的起始偏移。
         * 初始时，是其父目录在stringbuilder中的起始偏移，
         * 但自身的实际偏移位置可能更偏后，因为同一父目录下有多个子文件的话，自己的位置会被挤到后面去。所以需要实时更新
         */
        int start;
        int off;
        //第几层子文件夹。输入目录的第一层子文件的level就是1
        int level;
        public A(File file, int start, int off){
            this.file = file;
            this.start = start;
            this.off = off;
        }
        public A(){

        }
    }
}
