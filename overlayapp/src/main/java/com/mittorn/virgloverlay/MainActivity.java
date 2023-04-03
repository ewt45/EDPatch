package com.mittorn.virgloverlay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;

public class MainActivity extends Activity {

    public static final String T = "VirGL Overlay";
    //#define FL_GLX (1<<1)
    public static final int FL_GLES = (1<<2);
    //#define FL_OVERLAY (1<<3)
    public static final int FL_MULTITHREAD = (1<<4);

    public int overlay_position_var = 0, restart_var = 0, protocol_version = 0;
    public int dxtn_decompress = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText socket_path = (EditText)findViewById(R.id.socket_path);
        CheckBox use_gles = (CheckBox)findViewById(R.id.use_gles);
        CheckBox use_threads = (CheckBox)findViewById(R.id.use_threads);

        CheckBox restart_box = (CheckBox)findViewById(R.id.restart);
        CheckBox protocol_version_box = (CheckBox)findViewById(R.id.protocol_version);

        CheckBox dxtn_decompress_box = (CheckBox)findViewById(R.id.dxtn_decompress);

        RadioGroup overlay_position = (RadioGroup)findViewById(R.id.overlay_position);
        RadioButton overlay_topleft = (RadioButton)findViewById(R.id.overlay_topleft);
        RadioButton overlay_centered = (RadioButton)findViewById(R.id.overlay_centered);
        RadioButton overlay_hide = (RadioButton)findViewById(R.id.overlay_hide);

        int flags = 0;

        //Проверка разрешения на показ поверх других приложений
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 222);
        }

        try
        {
            CharBuffer buffer = CharBuffer.allocate(128);
            FileReader settings_reader = new FileReader(getFilesDir().getPath()+"/settings");
            BufferedReader reader = new BufferedReader(settings_reader);
            String[] parts = reader.readLine().split(" ");
            flags = Integer.parseInt(parts[0]);
            socket_path.setText(parts[1]);
            reader.close();
            settings_reader.close();
        }
        catch(Exception e){}

        try {
            // Считываем
            FileReader settings_reader1 = new FileReader(getFilesDir().getPath() + "/settings2");
            BufferedReader reader1 = new BufferedReader(settings_reader1);
            String[] parts1 = reader1.readLine().split(" ");
            overlay_position_var = Integer.parseInt(parts1[0]);
            restart_var = Integer.parseInt(parts1[1]);
            protocol_version = Integer.parseInt(parts1[2]);
            dxtn_decompress = Integer.parseInt(parts1[3]);
            reader1.close();
            settings_reader1.close();
        }catch(Exception e){}

        use_gles.setChecked((flags & FL_GLES) != 0);
        use_threads.setChecked((flags & FL_MULTITHREAD) != 0);

        restart_box.setChecked(restart_var != 0);
        protocol_version_box.setChecked(protocol_version != 0);

        dxtn_decompress_box.setChecked(dxtn_decompress != 0);

        overlay_topleft.setChecked(overlay_position_var == 0);
        overlay_centered.setChecked(overlay_position_var == 1);
        overlay_hide.setChecked(overlay_position_var == 2);
    }

    public void onClickClean (View view) {
        if (restart_var == 1) {
            writeStop(1);
        }
        for(int i = 1; i < 32; i++)
        {
            try{
                stopService( new Intent().setClassName(MainActivity.this, getPackageName() + ".process.p"+i));
            }
            catch(Exception e){}
        }
        Log.d(T,"All services cleaned!");
        if (restart_var == 1) {
            writeStop(0);
        }
    }

    public void writeStop(int stop) {
        try {
            FileWriter writer3 = new FileWriter(getFilesDir().getPath()+"/stop");
            writer3.write(String.valueOf(stop));
            writer3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickStart (View view) {
        EditText socket_path = (EditText)findViewById(R.id.socket_path);
        CheckBox use_gles = (CheckBox)findViewById(R.id.use_gles);
        CheckBox use_threads = (CheckBox)findViewById(R.id.use_threads);

        CheckBox restart_box = (CheckBox)findViewById(R.id.restart);
        CheckBox protocol_version_box = (CheckBox)findViewById(R.id.protocol_version);

        CheckBox dxtn_decompress_box = (CheckBox)findViewById(R.id.dxtn_decompress);

        RadioGroup overlay_position = (RadioGroup)findViewById(R.id.overlay_position);
        RadioButton overlay_topleft = (RadioButton)findViewById(R.id.overlay_topleft);
        RadioButton overlay_centered = (RadioButton)findViewById(R.id.overlay_centered);
        RadioButton overlay_hide = (RadioButton)findViewById(R.id.overlay_hide);

        try{
            // Сохраняем

            if (overlay_topleft.isChecked()){
                overlay_position_var = 0;
            } else if (overlay_centered.isChecked()){
                overlay_position_var = 1;
            } else if (overlay_hide.isChecked()){
                overlay_position_var = 2;
            }

            if (restart_box.isChecked()){
                restart_var = 1;
            }else{
                restart_var = 0;
            }

            if (protocol_version_box.isChecked()){
                protocol_version = 1;
            }else{
                protocol_version = 0;
            }

            if (dxtn_decompress_box.isChecked()){
                dxtn_decompress = 1;
            }else{
                dxtn_decompress = 0;
            }

            FileWriter writer1 = new FileWriter(getFilesDir().getPath()+"/settings2");
            writer1.write(String.valueOf(overlay_position_var));
            writer1.write(' ');
            writer1.write(String.valueOf(restart_var));
            writer1.write(' ');
            writer1.write(String.valueOf(protocol_version));
            writer1.write(' ');
            writer1.write(String.valueOf(dxtn_decompress));
            writer1.close();

            int flags = 0;
            if(use_gles.isChecked())
                flags |= FL_GLES;
            if(use_threads.isChecked())
                flags |= FL_MULTITHREAD;

            FileWriter writer = new FileWriter(getFilesDir().getPath()+"/settings");
            writer.write(String.valueOf(flags));
            writer.write(' ');
            writer.write(socket_path.getText().toString());
            writer.close();

            if (restart_var == 1) {
                writeStop(0);
            }

            Intent intent = new Intent().setClassName(MainActivity.this, getPackageName() + ".process.p1");
            startService(intent);

            Log.d(T,"Service p1 started!");
        }
        catch(Exception e) {
            Log.d(T,"Service p1 failed!");
        }
    }
}

