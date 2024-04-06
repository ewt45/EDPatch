package com.eltechs.axs.helpers.iab;

import android.text.TextUtils;
import android.util.Log;
import com.eltechs.axs.helpers.Base64;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/* loaded from: classes.dex */
public class Security {
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String TAG = "IabHelper/Security";

    public static boolean verifyPurchase(String str, String str2, String str3) {
        Log.i(TAG, "signedData = " + str2);
        Log.i(TAG, "signature = " + str3);
        if (TextUtils.isEmpty(str2)) {
            Log.e(TAG, "Purchase verification failed: missing data.");
            return false;
        } else if (TextUtils.isEmpty(str)) {
            Log.i(TAG, "Empty public key, assuming success validation.");
            return true;
        } else if (TextUtils.isEmpty(str3)) {
            Log.e(TAG, "Purchase verification failed: missing data signature.");
            return false;
        } else {
            return verify(generatePublicKey(str), str2, str3);
        }
    }

    public static PublicKey generatePublicKey(String str) {
        try {
            return KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.decode(str)));
        } catch (Base64.DecoderException e) {
            Log.e(TAG, "Base64 decoding failed.");
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        } catch (InvalidKeySpecException e3) {
            Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e3);
        }
    }

    public static boolean verify(PublicKey publicKey, String str, String str2) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(str.getBytes());
            if (signature.verify(Base64.decode(str2))) {
                return true;
            }
            Log.e(TAG, "Signature verification failed.");
            return false;
        } catch (Base64.DecoderException unused) {
            Log.e(TAG, "Base64 decoding failed.");
            return false;
        } catch (InvalidKeyException unused2) {
            Log.e(TAG, "Invalid key specification.");
            return false;
        } catch (NoSuchAlgorithmException unused3) {
            Log.e(TAG, "NoSuchAlgorithmException.");
            return false;
        } catch (SignatureException unused4) {
            Log.e(TAG, "Signature exception.");
            return false;
        }
    }
}
