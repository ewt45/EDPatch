package com.example.datainsert.exagear.obb;

import static com.example.datainsert.exagear.RR.getS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
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

import com.eltechs.axs.Globals;
import com.eltechs.axs.activities.FrameworkActivity;
import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.helpers.ZipInstallerObb;
import com.eltechs.ed.activities.EDStartupActivity;
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
    Button mBtn;
    /**
     * 用于zipinstallerobb获取和删除临时obb。
     * 仅当tmp.obb存在时才应不为null
     */
    public static File obbFile=null;

    /**
     * fragment内部获取tmp.obb的file对象时用。因为obbFile不应随意从null变为File实例，
     */
    static final File mInternalObbFile =  new File(Globals.getAppContext().getFilesDir(), "tmp.obb");;
    private ZipInstallerObb zipInstallerObb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 新建视图");

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setTag(TAG); //设置tag，用于在布局树中标识fragment，删除那个隐藏布局的其他子布局的时候会用到
//        root.setGravity(Gravity.TOP);
        mTv = new TextView(requireContext());
        mTv.setText(getS(RR.SelObb_info));
        mTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
        root.addView(mTv);

        //试试能不能添加到原本的文字显示位置上(不行，要重写UnpackExagearImageObb，里面新建ZipInstallerObb时，传入的callback，reportProgress方法是传递文本的）
        addTextToOriHintPlace(getS(RR.SelObb_info));
//        mTv = new TextView(requireContext());
//        mTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
        mBtn = new Button(requireContext());
        mBtn.setText(getS(RR.SelObb_btn));
        mBtn.setOnClickListener(new View.OnClickListener() {
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
        l2.addView(mBtn);
//        l2.addView(mTv);
//        l2.setMinimumHeight(50);
        root.addView(l2,new ViewGroup.LayoutParams(-1,-2));

//        root.addView(btn);
//        root.addView(mTv);
        root.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        return root;
    }
    private void addTextToOriHintPlace(String str) {
        FrameworkActivity a = ((ApplicationStateBase)Globals.getApplicationState()).getCurrentActivity();
        if(!(a instanceof EDStartupActivity))
            return;
//        EDStartupActivity activity = (EDStartupActivity) a;
//        TextView textView = activity.findViewById(RSIDHelper.rslvID(com.ewt45.exagearsupportv7.R.id.sa_step_description,com.eltechs.axs.R.id.sa_step_description));
//        if(textView == null)
//            return;
//
//        textView.setText(str);

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
     * 解压完成后删掉复制的数据包(顺便隐藏视图，否则初次解压之后启动容器时这个还会显示）
     */
    public static void delCopiedObb(){
        //删obb
        if(mInternalObbFile.exists()){
            boolean b = mInternalObbFile.delete();
        }

        //将选择obb部分的视图移除。
        FrameworkActivity a = ((ApplicationStateBase)Globals.getApplicationState()).getCurrentActivity();
        SelectObbFragment fragment = (SelectObbFragment) a.getSupportFragmentManager().findFragmentByTag(SelectObbFragment.TAG);
        if(fragment==null) {
            Log.d(TAG, "receiveResultManually: 未找到fragment，无法隐藏视图");
            return;
        }
        a.getSupportFragmentManager().beginTransaction().remove(fragment).commit();

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
        String filename = null;
        //如果是从“最近”分类下打开的话，uri里不包含文件名，需要通过这个获取
        DocumentFile documentFile = DocumentFile.fromSingleUri(activity,uri);
        if(documentFile!=null)
            filename = documentFile.getName();
        //保留原来的从uri提取文件名的方法吧
        if(filename == null){
            List<String> list = uri.getPathSegments();
            String[] names = list.get(list.size() - 1).split("/");
            filename = names[names.length - 1]; //文件
        }

        Log.d(TAG, "onActivityResult: 所选文件属性" + filename + ", " + uri.getPathSegments().toString()
                +", type(ContentResolver().getType):"+fragment.requireContext().getContentResolver().getType(uri)
                +", type(MimeTypeMap):"+MimeTypeMap.getSingleton().getMimeTypeFromExtension("obb"));
        //判断一下后缀吧，如果不是obb就显示错
        Toast.makeText(fragment.requireContext(), filename, Toast.LENGTH_SHORT).show();

        if(filename.length()<4 || !filename.endsWith(".obb")){
            fragment.mTv.setText(getS(RR.SelObb_selResult).split("\\$")[0]);
            return;
        }
        fragment.mTv.setText(getS(RR.SelObb_selResult).split("\\$")[1]);

        if (mInternalObbFile.exists()) {
            boolean b = mInternalObbFile.delete();
        }
        //禁用选择按钮
        fragment.mBtn.setEnabled(false);
        //新建线程复制数据包
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (InputStream is = fragment.requireContext().getContentResolver().openInputStream(uri);
                     FileOutputStream fos = new FileOutputStream(mInternalObbFile);){
                    if(is!=null){
                        IOUtils.copy(is, fos);
                        obbFile = mInternalObbFile; //仅在复制完成后才赋值
                        //复制完了之后再进入常规解压数据包操作
                        fragment.zipInstallerObb.installImageFromObbIfNeeded();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();



    }

}
