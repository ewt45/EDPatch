import com.axml.AndroidBinaryXml;

import java.io.File;
import java.io.IOException;

/**
 * Created by Sens on 2021/8/27.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        System.out.println("file->" + file);

        AndroidBinaryXml androidBinaryXml = new AndroidBinaryXml(file);

        byte[] datas = androidBinaryXml.toBytes();

        AndroidBinaryXml manifest = new AndroidBinaryXml(datas);
        System.out.println(manifest);
    }
}
