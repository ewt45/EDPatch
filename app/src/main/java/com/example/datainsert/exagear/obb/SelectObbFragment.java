package com.example.datainsert.exagear.obb;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eltechs.axs.helpers.ZipInstallerObb;
import com.example.datainsert.exagear.RR;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SelectObbFragment extends Fragment {
    final static String TAG = "SelectObbFragment";
    static final int PICK_OBB_FILE = 123;
    TextView mTv;
    public static File obbFile=null; //用于zipinstallerobb获取和删除临时obb
    private ZipInstallerObb zipInstallerObb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 新建视图");
        obbFile = new File(requireContext().getFilesDir(), "tmp.obb");

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setTag(TAG); //设置tag，用于在布局树中标识fragment，删除那个隐藏布局的其他子布局的时候会用到
//        root.setGravity(Gravity.TOP);
        mTv = new TextView(requireContext());
        mTv.setText(getS(RR.SelObb_info));
        mTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
        root.addView(mTv);
//        mTv = new TextView(requireContext());
//        mTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
        Button btn = new Button(requireContext());
        btn.setText(getS(RR.SelObb_btn));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                requireActivity().getSupportFragmentManager().popBackStack();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");//仅显示obb类型
                startActivityForResult(intent, PICK_OBB_FILE);
            }
        });
        LinearLayout l2 = new LinearLayout(requireContext());
        l2.setOrientation(LinearLayout.HORIZONTAL);
//        l2.setVerticalGravity(Gravity.CENTER);
        l2.addView(btn);
//        l2.addView(mTv);
//        l2.setMinimumHeight(50);
        root.addView(l2,new ViewGroup.LayoutParams(-1,-2));

//        root.addView(btn);
//        root.addView(mTv);
        root.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: 获取到文件？"+requestCode+" " +resultCode+" "+data);
        receiveResultManually((AppCompatActivity) requireActivity(),requestCode,resultCode,data);
    }


    public void setZipInstallerObb(ZipInstallerObb zipInstallerObb) {
        this.zipInstallerObb = zipInstallerObb;
    }
//    private static boolean isSuffixObb(String name){
//        String[] splits = name.split("\\.");
//        return  splits[splits.length-1].equals("obb");
//    }

    /**
     * 解压完成后删掉复制的数据包
     */
    public static void delCopiedObb(){
        if(obbFile!=null)
            obbFile.delete();
    }

    public static void receiveResultManually(AppCompatActivity activity, int requestCode, int resultCode, Intent data){
        Log.d(TAG, "receiveResultManually: activity里没调用super，只好手动调用");
        //找到fragment
        SelectObbFragment fragment = (SelectObbFragment) activity.getSupportFragmentManager().findFragmentByTag(SelectObbFragment.TAG);
        if(fragment==null) {
            Log.d(TAG, "receiveResultManually: 未找到fragment，无法处理结果");
            return;
        }

        //获取选中的，想要修改apk
        if ((requestCode& 0x0000ffff) != PICK_OBB_FILE) {
//            super.onActivityResult(requestCode, resultCode, data);
            Log.d(TAG, "receiveResultManually: 选择文件的fragment接收到的requestcode不是123.不进行处理:"+requestCode);
            return;
        }
        if (data == null || data.getData() == null) {
            return;
        }
        //如果选择了一个obb,将其复制到内部目录下,并开始解压
        //获取文件名
        Uri uri = data.getData();
        List<String> list = uri.getPathSegments();
        String[] names = list.get(list.size() - 1).split("/");
        String filename = names[names.length - 1]; //文件
        Log.d(TAG, "onActivityResult: 所选文件属性" + filename + ", " + uri.getPathSegments().toString()
                +", type(ContentResolver().getType):"+fragment.requireContext().getContentResolver().getType(uri)
                +", type(MimeTypeMap):"+MimeTypeMap.getSingleton().getMimeTypeFromExtension("obb"));
        //判断一下后缀吧，如果不是obb就显示错
        Toast.makeText(fragment.requireContext(), filename, Toast.LENGTH_SHORT).show();
        if(filename.length()>=4 && !filename.endsWith(".obb")){
            fragment.mTv.setText(getS(RR.SelObb_selResult).split("\\$")[0]);
            return;
        }
        fragment.mTv.setText(getS(RR.SelObb_selResult).split("\\$")[1]);

        if (obbFile.exists()) {
            obbFile.delete();
        }
        //新建线程复制数据包
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = fragment.requireContext().getContentResolver().openInputStream(uri);
                    FileOutputStream fos = new FileOutputStream(obbFile);
                    IOUtils.copy(is, fos);
                    is.close();
                    fos.close();
                    //复制完了之后再进入常规解压数据包操作
                    fragment.zipInstallerObb.installImageFromObbIfNeeded();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();



    }

}
