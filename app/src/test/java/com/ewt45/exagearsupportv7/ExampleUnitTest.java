package com.ewt45.exagearsupportv7;

import org.junit.Test;

import static org.junit.Assert.*;

import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import java.io.File;

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
    public void test1(){
//        clz1 c1=new clz2();
//        c1.m1();
        System.out.println(15f / 0);
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