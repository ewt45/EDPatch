package com.example.datainsert.exagear.mutiWine;

import com.eltechs.axs.Globals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WineVersionConfig {

    //wine版本信息列表
    public static ArrayList<WineVersion> wineList;
    //使用说明
    public static String usage ="这是一条使用说明";
    private static String TAG = "WineVersionConfig";

    /**
     * 读取assets中的wine版本信息，初始化列表以供静态调用
     * 应该在初始化menu的时候被调用一次。之后才可以使用本类中的静态成员变量
     */
    public static void initList(){
        if(Globals.getAppContext()==null)
            return;
        //读取assets下的txt
        try {
            //用reader，正好能使用inputStream，不用转成文件了
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Globals.getAppContext().getAssets().open("WinesVersionInfo.txt"),
                            StandardCharsets.UTF_8)
            );
            wineList = new ArrayList<>();
            String line;
            String[] arrs;
            while((line=br.readLine())!=null){
                //如果是注释，略过这一行
                if(line.length()>0&& (line.charAt(0) == '#' || line.charAt(0) == '\n')){
                    continue;
                }
                //如果是使用说明
                else if(line.length()>6&& line.startsWith("usage:")){
                    usage = line.substring(6);
                }
                //如果是wine信息，加入列表
                else if(line.length()>1){
                    arrs = line.split(" ");
//                Log.d(TAG, "initList: 读取一行："+ Arrays.toString(arrs));
                    wineList.add(new WineVersion(arrs));
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 将assets文件夹中的文件复制到本地. 暂时用不上。
     * @param fileName 要复制文件的文件名
     * @param outsidePath   要复制到哪里( 默认复制到内部files/文件名）
     */
    public static File copyAssetsFile(String fileName, String outsidePath){
        try {
            if(outsidePath==null)
                outsidePath = Globals.getAppContext().getFilesDir().getAbsolutePath()+"/"+fileName;
            File newFile = new File(outsidePath);
            InputStream is = Globals.getAppContext().getAssets().open(fileName);
            if (newFile.exists()) {
                boolean b = newFile.delete();
                assert b;
            }
            if (!newFile.createNewFile()) {
                return null;
            }
            FileOutputStream fos = new FileOutputStream(newFile);                   //新文件输出流
            int len = -1;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
            return newFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
