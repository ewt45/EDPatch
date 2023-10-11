package com.winlator.core;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.lang.Process;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;

/* loaded from: classes4.dex */
public abstract class ProcessHelper {
    private static final byte SIGCONT = 18;
    private static final byte SIGSTOP = 19;
    public static Callback<String> debugCallback;
    public static boolean debugMode = true;

    public static void suspendProcess(int pid) {
        android.os.Process.sendSignal(pid, SIGSTOP);
    }

    public static void resumeProcess(int pid) {
        android.os.Process.sendSignal(pid, SIGCONT);
    }

    public static int createSubprocess(String command) {
        return createSubprocess(command, null);
    }

    public static int createSubprocess(String command, String[] envp) {
        return createSubprocess(command, envp, null);
    }

    public static int createSubprocess(String command, String[] envp, File workingDir) {
        return createSubprocess(command, envp, workingDir, null);
    }

    public static int createSubprocess(String command, String[] envp, File workingDir, Callback<Integer> terminationCallback) {
        int pid = -1;
        try {
            Process process = Runtime.getRuntime().exec(command, envp, workingDir);
            Field pidField = process.getClass().getDeclaredField("pid");
            pidField.setAccessible(true);
            pid = pidField.getInt(process);
            pidField.setAccessible(false);
            if (debugMode) {
//                createDebugThread(process.getInputStream(), debugCallback);
//                createDebugThread(process.getErrorStream(), debugCallback);
                createDebugThread2(process.getInputStream(), "out");
                createDebugThread2(process.getErrorStream(), "err");
            }
            if (terminationCallback != null) {
                createWaitForThread(process, terminationCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pid;
    }

    private static void createDebugThread(final InputStream inputStream, final Callback<String> debugCallback2) {
        // from class: com.winlator.core.ProcessHelper$$ExternalSyntheticLambda0
// java.lang.Runnable
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        if (debugCallback2 != null) {
                            debugCallback2.call(line);
                        }
                        System.out.println(line);
                    } else {
                        reader.close();
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void createDebugThread2(final InputStream inputStream, String type) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Log.d("ProcessHelper", "createDebugThread2: 开始重定向输出到txt-"+type+", 流为null？"+(inputStream==null));
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                @SuppressLint("DefaultLocale") FileWriter writer = new FileWriter(new File(Environment.getExternalStorageDirectory(),String.format("winlator-x86-std%s.txt", type)));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        System.out.println(line);
                        writer.write(line);
                        writer.write('\n');
                        writer.flush();
                    } else {
                        reader.close();
                        writer.flush();
                        writer.close();
                        Log.d("ProcessHelper", "createDebugThread2: 进程结束，线程执行完毕");
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void createWaitForThread(final Process process, final Callback<Integer> terminationCallback) {
        // from class: com.winlator.core.ProcessHelper$$ExternalSyntheticLambda1
// java.lang.Runnable
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                int status = process.waitFor();
                terminationCallback.call(status);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}