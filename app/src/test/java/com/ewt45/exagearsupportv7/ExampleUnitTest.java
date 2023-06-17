package com.ewt45.exagearsupportv7;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.*;

import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test1() throws IOException {
//        clz1 c1=new clz2();
//        c1.m1();
//        System.out.println(15f / 0);// infinite
//        System.out.println(Arrays.toString("aaa$bbb$ccc".split("\\$")));
//        System.out.println(Arrays.toString("".getBytes(StandardCharsets.US_ASCII)));
        String[] strings = new String[119];
        List<String> allLines = Files.readAllLines(new File("E:\\111.txt").toPath());
        System.out.println(allLines.toString());
        Collections.sort(allLines, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String op1 = o1.substring(0,o1.length()-1).split(" = ")[1];
                String op2 = o2.substring(0,o2.length()-1).split(" = ")[1];
                return Integer.parseInt(op1)-Integer.parseInt(op2);
            }
        });
        for(String str: allLines){
            System.out.println(str);
        }
    }

    @Test
    public void checksum256() throws NoSuchAlgorithmException, IOException {
        File file = new File("E:\\tmp\\wine-8.10-x86.tar.xz");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] tarSha = digest.digest(FileUtils.readFileToByteArray(file));

        StringBuilder builder = new StringBuilder();
        for (byte b : tarSha) {
            //java.lang.Integer.toHexString() 方法的参数是int(32位)类型，
            //如果输入一个byte(8位)类型的数字，这个方法会把这个数字的高24为也看作有效位，就会出现错误
            //如果使用& 0XFF操作，可以把高24位置0以避免这样错误
            String temp = Integer.toHexString(b & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                builder.append("0");
            }
            builder.append(temp);
        }


        System.out.println("671940c6dd1c3fac9715a6d2b64b5a028e0f20fee04b482ff5788f54f0f4fadb");
        System.out.println(builder.toString());

    }
    class clz1{
        public void m1(){
            System.out.println("clz1 m1");
        }
        public void m2(){

        }
    }
    class clz2 extends clz1{
        @Override
        public void m1() {
            System.out.println("clz2 m1");
        }
    }
}