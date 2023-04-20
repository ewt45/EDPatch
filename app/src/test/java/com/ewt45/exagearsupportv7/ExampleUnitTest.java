package com.ewt45.exagearsupportv7;

import org.junit.Test;

import static org.junit.Assert.*;

import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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