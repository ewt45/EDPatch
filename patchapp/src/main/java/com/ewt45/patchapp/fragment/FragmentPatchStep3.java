package com.ewt45.patchapp.fragment;

import static android.content.Intent.EXTRA_STREAM;
import static android.support.v4.content.FileProvider.getUriForFile;

import static com.ewt45.patchapp.PatchUtils.getExaNewPatchedSignedApk;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewt45.patchapp.R;
import com.ewt45.patchapp.databinding.FragmentPatchStep3Binding;

public class FragmentPatchStep3 extends BaseFragmentPatchStep {
    FragmentPatchStep3Binding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatchStep3Binding.inflate(inflater, container, false);
        binding.btnBack.setOnClickListener(v->{
            getNavController().navigateUp();
            getNavController().navigateUp();
        });
       
        binding.spinnerBtn.setOnClickListener(v->{
            boolean isShowing = binding.btnViewApk.getVisibility()==View.VISIBLE;
            binding.spinnerBtn.animate().rotationX(isShowing?0:180).setDuration(200).start();
            binding.btnViewApk.setVisibility(isShowing?View.GONE:View.VISIBLE);
        });
        //保存到下载目录。不获取权限能行吗
        binding.btnViewApk.setOnClickListener(v->{
//            ContentResolver resolver = requireActivity().getApplicationContext().getContentResolver();
//            OutputStream os = resolver.openOutputStream(MediaStore.Files.getContentUri())
//            FileUtils.copyFile(PatchUtils.getExaNewPatchedApk(),os);

            try {
                
                Uri uri = getUriForFile(requireContext(), getResources().getString(R.string.provider_ato), getExaNewPatchedSignedApk());
                Intent shareIntent = new Intent(Intent.ACTION_SEND)
                        .putExtra(EXTRA_STREAM,uri )
                        .setType("application/vnd.android.package-archive");//application/vnd.android.package-archive
                startActivity(Intent.createChooser(shareIntent, getString(R.string.patchstep3_chooser_title)));
//                startActivity(Intent.createChooser(shareIntent,null));

                  //用action_view试试
//                requireActivity().getPackageManager().clearPackagePreferredActivities(requireActivity().getPackageName());
//                Uri uri = getUriForFile(requireContext(), getResources().getString(R.string.provider_ato), getExaNewPatchedSignedApk());
//                Intent shareIntent = new Intent(Intent.ACTION_VIEW)
////                        .putExtra(EXTRA_STREAM,uri )
//                        .setDataAndType(uri,"*/*");
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivity(Intent.createChooser(shareIntent, getString(R.string.patchstep3_chooser_title)));


//                AndroidUtils.showSnack(requireActivity(), "已成功保存到Download目录。");
            } catch (Exception e) {
                e.printStackTrace();
//                AndroidUtils.showSnack(requireActivity(), "保存失败");
            }
        });
        binding.btnInstallNew.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = getUriForFile(requireContext(), "com.ewt45.patchapp.fileprovider", getExaNewPatchedSignedApk());
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setStepTitle(R.string.patchstep3_titile);
        getFAB().setOnClickListener(v -> binding.btnInstallNew.performClick());
        getFAB().hide();
        getFAB().setImageResource(R.drawable.ic_done);
        getFAB().show();
    }

}
