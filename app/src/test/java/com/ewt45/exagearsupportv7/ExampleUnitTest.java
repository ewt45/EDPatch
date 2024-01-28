package com.ewt45.exagearsupportv7;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.*;

import static java.nio.charset.StandardCharsets.US_ASCII;

import com.example.datainsert.exagear.mutiWine.WineNameComparator;
import com.example.datainsert.exagear.mutiWine.v2.HQParser;
import com.example.datainsert.exagear.mutiWine.v2.HQWineInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void test_math_methods_angle(){
        double angle = Math.atan2(1,2);//以pi为单位的
        System.out.println(angle);
    }
    @Test
    public void test_hex_color_string_to_int(){
        int argb = 0xffffffff;

        System.out.println(Integer.parseUnsignedInt("fffffafa",16));
//        System.out.println(Integer.decode("0xffffffff"));
        System.out.println((int)Long.parseLong("ffffffff",16));

    }

    @Test
    public void test_index_of_wine_cmd(){
        System.out.println("找到的index="+setOtherArgv_findWineIndexInCmd("wine exe"));
        System.out.println("找到的index="+setOtherArgv_findWineIndexInCmd("eval \"wine exe"));
        System.out.println("找到的index="+setOtherArgv_findWineIndexInCmd("eval \" wine exe"));
    }
    private static int setOtherArgv_findWineIndexInCmd(String wineCmd){
        int findIndex = -1;
        if(wineCmd.startsWith("wine "))
            findIndex = 0;
        if(wineCmd.startsWith("eval \"wine "))
            findIndex = 6;
        if(findIndex == -1)
            findIndex = wineCmd.indexOf(" wine ")+1;
        return findIndex;
    }
    /**
     * xsdl中。so文件名，需要按照assets中的txt重命名，然后才能正常使用pulseaudio
     * @throws IOException
     */
    @Test
    public void rename_xsdl_libname() throws IOException {
        List<String> reNameLines = Files.readAllLines(Paths.get("E:\\tmp\\pa\\bin-map-arm64-v8a.txt"));
        File parentFolder = new File("E:\\tmp\\pa");
        for(int i=0; i<reNameLines.size(); i++){
            String wrongName = reNameLines.get(i);
            i++;
            String rightName = reNameLines.get(i);
            File[] wrongFiles =parentFolder.listFiles((dir, name) -> name.equals(wrongName));
            if(wrongFiles.length>0)
                wrongFiles[0].renameTo(new File(parentFolder,rightName));

        }
    }
    @Test
    public void FileUtils_copyDirectory_and_copyDirectoryToDirectory() throws IOException {
        //copyDirectory只复制src中的子文件和文件夹，即dst目录下出现src目录下的子文件
        //copyDirectoryToDirectory复制包括src文件夹本身，即dst目录下出现src目录
        FileUtils.copyDirectory(
                new File("E:\\tmp\\srcFolder"),
                new File("E:\\tmp\\dstFolder"));
//        FileUtils.copyDirectoryToDirectory();
    }
    @Test
    public void extractDebManually() throws Exception {
        File debFile = new File("E:\\tmp\\wine-devel_8.5~bionic-1_i386.deb");
        byte[] bytes = FileUtils.readFileToByteArray(debFile);
        byte[] dataBytes; //存储data.tar.xz 的字节数组

        //捕捉一下数组越界吧，虽然一般应该不会
        try {
            int pos = 0;
            String rHeader  ="!<arch>";
            //deb文件头
            byte[] headers = Arrays.copyOfRange(bytes,pos,pos+rHeader.length());
            System.out.println(new String(headers,US_ASCII)); //直接new 一个String，设定ascii格式，就将byte数组转为字符串了=-=
            if(!"!<arch>".equals(new String(headers, US_ASCII))){
                throw new Exception("不是deb文件");
            }
            pos = pos+rHeader.length()+1; //还有个换行

            String memberName;
            int fileSize;
            do{
                //成员名，应该16字节
                memberName =  new String(Arrays.copyOfRange(bytes,pos,pos+16)).trim();
                System.out.println("成员："+memberName);
                //如果不是需要解压的内容，就读取其大小并跳过这些长度
                /*
                char    fileName[16];
                char    modification_timestamp[12];
                char    ownerID[6];
                char    groupID[6];
                char    fileMode[8];
                char    fileSize[10];
                char    endMarker[2];
                 */
                pos = pos + 16+12+6+6+8; //读文件大小
                String sizeStr = new String(Arrays.copyOfRange(bytes,pos,pos+10)).trim();
                pos = pos + 10; //读结尾标识
                String endMarker = new String(Arrays.copyOfRange(bytes,pos,pos+2));
                //根据模版，只有结尾标识为这个的时候才读取下面的文件内容，不知道有什么说法
                if(!endMarker.equals("`\n")){
                    throw new Exception("成员"+memberName+"的endmarker"+endMarker+"不是`\\n");
                }
                pos = pos + 2 ; //读取内容
                int sizeInt = Integer.parseInt(sizeStr);
                dataBytes = Arrays.copyOfRange(bytes,pos,pos+sizeInt);
                pos = pos + sizeInt;
                //对齐偶数字节
                if((pos & 1)!=0)
                    pos++;
            }while(!memberName.startsWith("data.tar"));

            System.out.println("找到data.tar");
            if(!memberName.endsWith(".xz")){
                throw new Exception("data.tar不是xz压缩");
            }

            FileUtils.writeByteArrayToFile(new File("E:\\tmp\\data手动提取.tar.xz"),dataBytes,false);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }


        //解压tar.xz


    }


    @Test
    public void readDebianRepositoryPackages() throws IOException {
        String tagName = "wine-devel_8.4";
        List<String> lines = FileUtils.readLines(new File("E:\\tmp\\Packages"));
        String pkg = tagName.split("_")[0];
        String version = tagName.split("_")[1];
        String depPkg  = pkg+"-i386";

        HQParser.InfoWrapper wrapper = new HQParser.InfoWrapper();
        wrapper.lines = lines;
        wrapper.pos = 0;

        List<HQWineInfo> infoList = new ArrayList<>();
        do {
            HQParser.readOneInfo(wrapper);
            HQWineInfo info = wrapper.info;
            //otherosfs是正常的，其他可能是调试什么的。版本带~rc的是抢先体验，会造成多个info版本号完全相同。winehq是纯快捷方式不需要
            if(info.section.equals("otherosfs") && !info.version.contains("~rc") && !info.mpackage.contains("winehq"))
                infoList.add(info);

        } while (wrapper.pos<wrapper.lines.size());

        //把依赖包从列表中挪到主包中，
        for(int i=0; i<infoList.size();i++){
            HQWineInfo depInfo = infoList.get(i);
            if(depInfo.mpackage.endsWith("-i386")){
                infoList.remove(i);
                i--;
                //寻找对应的主包
                for(HQWineInfo mainInfo:infoList){
                    if(mainInfo.getTagName().equals(depInfo.getTagName().replace("-i386",""))){
                        assert mainInfo.depInfo == null;
                        mainInfo.depInfo = depInfo;
                            break;
                    }
                }
            }
        }
        Collections.sort(infoList,new WineNameComparator());

        for(HQWineInfo info: infoList){
            assert info.depInfo!=null;
            System.out.println(info);
        }

    }

    @Test
    public void willForLoopSimpleFormatAffectIndex(){
        //不行，这种for循环的写法 如果remove会报iterator的错
        List<String> list = Arrays.asList("1","2","3","4","5");
        for(String s:list){
            if(s.equals("2") || s.equals("4"))
                list.remove(s);
            else
                System.out.println(s);
        }
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void multipleSpaceSplit() {
        String testStr = "671940c6dd1c3fac9715a6d2b64b5a028e0f20fee04b482ff5788f54f0f4fadb  wine-8.10-x86.tar.xz";
        String[] arr = testStr.split(" ");
        System.out.println(Arrays.toString(arr));
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
                String op1 = o1.substring(0, o1.length() - 1).split(" = ")[1];
                String op2 = o2.substring(0, o2.length() - 1).split(" = ")[1];
                return Integer.parseInt(op1) - Integer.parseInt(op2);
            }
        });
        for (String str : allLines) {
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

    @Test
    public void convertI18n() throws IOException {
        List<String> allLines = Files.readAllLines(new File("E:\\111.txt").toPath());
        List<String> stringLines = FileUtils.readLines(new File("E:\\222.txt"));
        for (int i = 0; i < allLines.size(); i++) {
            String s = allLines.get(i).trim();
            if (!s.startsWith("zhArray") || !s.endsWith(";")) {
                allLines.remove(i);
                i--;
            }
        }
        for (int i = 0; i < stringLines.size(); i++) {
            String s = stringLines.get(i).trim();
            if (!s.startsWith("\"") || !s.endsWith(",") || s.contains("</ul>")) {
                stringLines.remove(i);
                i--;
            }
        }
        if (allLines.size() != stringLines.size()) {
            System.out.println("两列表长度不等，无法转换" + allLines.size() + stringLines.size());
            return;
        }
        for (int i = 0; i < allLines.size(); i++) {
            String newS = allLines.get(i).trim();
            String oldS = stringLines.get(i).trim();

            int begin = newS.indexOf(',');
            int end = newS.length() - 2;
            System.out.println("ru" + newS.substring(2, begin + 1) + oldS.substring(0, oldS.length() - 1) + ");");

        }

    }

    class clz1 {
        public void m1() {
            System.out.println("clz1 m1");
        }

        public void m2() {

        }
    }

    class clz2 extends clz1 {
        @Override
        public void m1() {
            System.out.println("clz2 m1");
        }
    }
}