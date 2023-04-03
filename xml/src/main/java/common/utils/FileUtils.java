package common.utils;

import java.io.*;

/**
 * Created by Sens on 2021/8/27.
 */
public class FileUtils {
    public static byte[] getFileData(File file) {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            int available = inputStream.available();
            byte[] datas = new byte[available];
            inputStream.read(datas);
            inputStream.close();
            return datas;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
