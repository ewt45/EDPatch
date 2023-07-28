package com.ewt45.patchapp.thread;

import android.content.res.AssetManager;

import com.android.apksig.ApkSigner;
import com.android.apksig.internal.util.X509CertificateUtils;
import com.android.apksig.util.DataSource;
import com.android.apksig.util.DataSources;
import com.ewt45.patchapp.PatchUtils;
import com.ewt45.patchapp.R;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class SignApk implements Action {
    boolean useDefaultKey ;
    AssetManager mAssetManager;
    public SignApk(AssetManager assetManager, boolean useDefaultKey){
        mAssetManager = assetManager;
        this.useDefaultKey = useDefaultKey;
    }

    @Override
    public Integer call() throws Exception {
        File inApkFile = new File(PatchUtils.getExaExtractDir(), "dist/tmp.apk");
        try (RandomAccessFile apkFile = new RandomAccessFile(inApkFile, "r")) {
            DataSource in = DataSources.asDataSource(apkFile);
            List<ApkSigner.SignerConfig> ecP256SignerConfig = Collections.singletonList(
                    getSignerConfig());//getDefaultSignerConfigFromResources(EC_P256_SIGNER_RESOURCE_NAME,false)
            ApkSigner.Builder apkSignerBuilder = new ApkSigner.Builder(ecP256SignerConfig)
                    .setV1SigningEnabled(true)
                    .setV2SigningEnabled(false)
                    .setV3SigningEnabled(false)
                    .setV4SigningEnabled(false)
//                    .setOtherSignersSignaturesPreserved(true)
                    .setInputApk(in)
                    .setOutputApk(PatchUtils.getExaNewPatchedApk());

            apkSignerBuilder.build().sign();
            mAssetManager=null;
            return R.string.actmsg_signapk;
        }
    }

    private ApkSigner.SignerConfig getSignerConfig(){
        byte[] encoded; //rsa-2048.pk8
        try{
            String pk8Filename = useDefaultKey?"rsa_testkey.pk8":"rsa.pk8";
            String x509Filename = useDefaultKey?"rsa_testkey.x509.pem":"rsa.x509.pem";
            encoded=IOUtils.toByteArray(mAssetManager.open(pk8Filename));
            PrivateKey privateKey = KeyFactory.getInstance("rsa").generatePrivate(new PKCS8EncodedKeySpec(encoded));
            InputStream in = mAssetManager.open(x509Filename);//new FileInputStream("rsa-2048.x509.pem");//"rsa-2048.x509.pem" "rsa-2048_negmod.x509.der"
            Collection<? extends Certificate> certs0 = X509CertificateUtils.generateCertificates(in);
            in.close();
            List<X509Certificate> certs = new ArrayList<>(certs0.size());
            for (Certificate cert : certs0) {
                certs.add((X509Certificate) cert);
            }

            return new ApkSigner.SignerConfig.Builder("rsa", privateKey, certs).build();
        }catch (Exception e){
            e.printStackTrace();
        }
                //"rsa-2048.pk8"
        return null;
    }

    @Override
    public int getStartMessage() {
        return R.string.actmsg_signapk;
    }
}
