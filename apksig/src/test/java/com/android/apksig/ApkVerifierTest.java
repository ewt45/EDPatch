/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.apksig;

import static com.android.apksig.ApkSignerTest.FIRST_RSA_2048_SIGNER_RESOURCE_NAME;
import static com.android.apksig.ApkSignerTest.SECOND_RSA_2048_SIGNER_RESOURCE_NAME;
import static com.android.apksig.ApkSignerTest.assertResultContainsSigners;
import static com.android.apksig.ApkSignerTest.assertV31SignerTargetsMinApiLevel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNoException;

import com.android.apksig.ApkVerifier.Issue;
import com.android.apksig.ApkVerifier.IssueWithParams;
import com.android.apksig.ApkVerifier.Result.SourceStampInfo.SourceStampVerificationStatus;
import com.android.apksig.apk.ApkFormatException;
import com.android.apksig.internal.apk.v3.V3SchemeConstants;
import com.android.apksig.internal.util.AndroidSdkVersion;
import com.android.apksig.internal.util.HexEncoding;
import com.android.apksig.internal.util.Resources;
import com.android.apksig.util.DataSources;

import java.security.Provider;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(JUnit4.class)
public class ApkVerifierTest {

    private static final String[] DSA_KEY_NAMES = {"1024", "2048", "3072"};
    private static final String[] DSA_KEY_NAMES_1024_AND_SMALLER = {"1024"};
    private static final String[] DSA_KEY_NAMES_2048_AND_LARGER = {"2048", "3072"};
    private static final String[] EC_KEY_NAMES = {"p256", "p384", "p521"};
    private static final String[] RSA_KEY_NAMES = {"1024", "2048", "3072", "4096", "8192", "16384"};
    private static final String[] RSA_KEY_NAMES_2048_AND_LARGER = {
            "2048", "3072", "4096", "8192", "16384"
    };

    private static final String RSA_2048_CERT_SHA256_DIGEST =
            "fb5dbd3c669af9fc236c6991e6387b7f11ff0590997f22d0f5c74ff40e04fca8";
    private static final String EC_P256_CERT_SHA256_DIGEST =
            "6a8b96e278e58f62cfe3584022cec1d0527fcb85a9e5d2e1694eb0405be5b599";

    @Test
    public void testOriginalAccepted() throws Exception {
        // APK signed with v1 and v2 schemes. Obtained by building
        // cts/hostsidetests/appsecurity/test-apps/tinyapp.
        // This APK is used as a basis for many of the other tests here. Hence, we check that this
        // APK verifies.
        assertVerified(verify("original.apk"));
    }

    @Test
    public void testV1OneSignerMD5withRSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-md5-1.2.840.113549.1.1.1-%s.apk", RSA_KEY_NAMES);
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-md5-1.2.840.113549.1.1.4-%s.apk", RSA_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA1withRSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha1-1.2.840.113549.1.1.1-%s.apk", RSA_KEY_NAMES);
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha1-1.2.840.113549.1.1.5-%s.apk", RSA_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA224withRSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha224-1.2.840.113549.1.1.1-%s.apk", RSA_KEY_NAMES);
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha224-1.2.840.113549.1.1.14-%s.apk", RSA_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA256withRSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha256-1.2.840.113549.1.1.1-%s.apk", RSA_KEY_NAMES);
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha256-1.2.840.113549.1.1.11-%s.apk", RSA_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA384withRSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha384-1.2.840.113549.1.1.1-%s.apk", RSA_KEY_NAMES);
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha384-1.2.840.113549.1.1.12-%s.apk", RSA_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA512withRSAVerifies() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha512-1.2.840.113549.1.1.1-%s.apk", RSA_KEY_NAMES);
        assertVerifiedForEach(
                "v1-only-with-rsa-pkcs1-sha512-1.2.840.113549.1.1.13-%s.apk", RSA_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA1withECDSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach("v1-only-with-ecdsa-sha1-1.2.840.10045.2.1-%s.apk", EC_KEY_NAMES);
        assertVerifiedForEach("v1-only-with-ecdsa-sha1-1.2.840.10045.4.1-%s.apk", EC_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA224withECDSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach("v1-only-with-ecdsa-sha224-1.2.840.10045.2.1-%s.apk", EC_KEY_NAMES);
        assertVerifiedForEach("v1-only-with-ecdsa-sha224-1.2.840.10045.4.3.1-%s.apk", EC_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA256withECDSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach("v1-only-with-ecdsa-sha256-1.2.840.10045.2.1-%s.apk", EC_KEY_NAMES);
        assertVerifiedForEach("v1-only-with-ecdsa-sha256-1.2.840.10045.4.3.2-%s.apk", EC_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA384withECDSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach("v1-only-with-ecdsa-sha384-1.2.840.10045.2.1-%s.apk", EC_KEY_NAMES);
        assertVerifiedForEach("v1-only-with-ecdsa-sha384-1.2.840.10045.4.3.3-%s.apk", EC_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA512withECDSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach("v1-only-with-ecdsa-sha512-1.2.840.10045.2.1-%s.apk", EC_KEY_NAMES);
        assertVerifiedForEach("v1-only-with-ecdsa-sha512-1.2.840.10045.4.3.4-%s.apk", EC_KEY_NAMES);
    }

    @Test
    public void testV1OneSignerSHA1withDSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        // NOTE: This test is split into two because JCA Providers shipping with OpenJDK refuse to
        // verify DSA signatures with keys too long for the SHA-1 digest.
        assertVerifiedForEach(
                "v1-only-with-dsa-sha1-1.2.840.10040.4.1-%s.apk", DSA_KEY_NAMES_1024_AND_SMALLER);
        assertVerifiedForEach(
                "v1-only-with-dsa-sha1-1.2.840.10040.4.3-%s.apk", DSA_KEY_NAMES_1024_AND_SMALLER);
    }

    @Test
    public void testV1OneSignerSHA1withDSAAcceptedWithKeysTooLongForDigest() throws Exception {
        // APK signed with v1 scheme only, one signer

        // OpenJDK's default implementation of Signature.SHA1withDSA refuses to verify signatures
        // created with keys too long for the digest used. Android Package Manager does not reject
        // such signatures. We thus skip this test if Signature.SHA1withDSA exhibits this issue.
        PublicKey publicKey =
                Resources.toCertificate(getClass(), "dsa-2048.x509.pem").getPublicKey();
        Signature s = Signature.getInstance("SHA1withDSA");
        try {
            s.initVerify(publicKey);
        } catch (InvalidKeyException e) {
            assumeNoException(e);
        }

        assertVerifiedForEach(
                "v1-only-with-dsa-sha1-1.2.840.10040.4.1-%s.apk", DSA_KEY_NAMES_2048_AND_LARGER);
        assertVerifiedForEach(
                "v1-only-with-dsa-sha1-1.2.840.10040.4.3-%s.apk", DSA_KEY_NAMES_2048_AND_LARGER);
    }

    @Test
    public void testV1OneSignerSHA224withDSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        // NOTE: This test is split into two because JCA Providers shipping with OpenJDK refuse to
        // verify DSA signatures with keys too long for the SHA-224 digest.
        assertVerifiedForEach(
                "v1-only-with-dsa-sha224-1.2.840.10040.4.1-%s.apk", DSA_KEY_NAMES_1024_AND_SMALLER);
        assertVerifiedForEach(
                "v1-only-with-dsa-sha224-2.16.840.1.101.3.4.3.1-%s.apk",
                DSA_KEY_NAMES_1024_AND_SMALLER);
    }

    @Test
    public void testV1OneSignerSHA224withDSAAcceptedWithKeysTooLongForDigest() throws Exception {
        // APK signed with v1 scheme only, one signer

        // OpenJDK's default implementation of Signature.SHA224withDSA refuses to verify signatures
        // created with keys too long for the digest used. Android Package Manager does not reject
        // such signatures. We thus skip this test if Signature.SHA224withDSA exhibits this issue.
        PublicKey publicKey =
                Resources.toCertificate(getClass(), "dsa-2048.x509.pem").getPublicKey();
        Signature s = Signature.getInstance("SHA224withDSA");
        try {
            s.initVerify(publicKey);
        } catch (InvalidKeyException e) {
            assumeNoException(e);
        }
        assertVerifiedForEach(
                "v1-only-with-dsa-sha224-1.2.840.10040.4.1-%s.apk", DSA_KEY_NAMES_2048_AND_LARGER);
        assertVerifiedForEach(
                "v1-only-with-dsa-sha224-2.16.840.1.101.3.4.3.1-%s.apk",
                DSA_KEY_NAMES_2048_AND_LARGER);
    }

    @Test
    public void testV1OneSignerSHA256withDSAAccepted() throws Exception {
        // APK signed with v1 scheme only, one signer
        assertVerifiedForEach("v1-only-with-dsa-sha256-1.2.840.10040.4.1-%s.apk", DSA_KEY_NAMES);
        assertVerifiedForEach(
                "v1-only-with-dsa-sha256-2.16.840.1.101.3.4.3.2-%s.apk", DSA_KEY_NAMES);
    }

    @Test
    public void testV2StrippedRejected() throws Exception {
        // APK signed with v1 and v2 schemes, but v2 signature was stripped from the file (by using
        // zipalign).
        // This should fail because the v1 signature indicates that the APK was supposed to be
        // signed with v2 scheme as well, making the platform's anti-stripping protections reject
        // the APK.
        assertVerificationFailure("v2-stripped.apk", Issue.JAR_SIG_MISSING_APK_SIG_REFERENCED);

        // Similar to above, but the X-Android-APK-Signed anti-stripping header in v1 signature
        // lists unknown signature schemes in addition to APK Signature Scheme v2. Unknown schemes
        // should be ignored.
        assertVerificationFailure(
                "v2-stripped-with-ignorable-signing-schemes.apk",
                Issue.JAR_SIG_MISSING_APK_SIG_REFERENCED);
    }

    @Test
    public void testV3StrippedRejected() throws Exception {
        // APK signed with v2 and v3 schemes, but v3 signature was stripped from the file by
        // modifying the v3 block ID to be the verity padding block ID. Without the stripping
        // protection this modification ignores the v3 signing scheme block.
        assertVerificationFailure("v3-stripped.apk", Issue.V2_SIG_MISSING_APK_SIG_REFERENCED);
    }

    @Test
    public void testSignaturesIgnoredForMaxSDK() throws Exception {
        // The V2 signature scheme was introduced in N, and V3 was introduced in P. This test
        // verifies a max SDK of pre-P ignores the V3 signature and a max SDK of pre-N ignores both
        // the V2 and V3 signatures.
        assertVerified(
                verifyForMaxSdkVersion(
                        "v1v2v3-with-rsa-2048-lineage-3-signers.apk", AndroidSdkVersion.O));
        assertVerified(
                verifyForMaxSdkVersion(
                        "v1v2v3-with-rsa-2048-lineage-3-signers.apk", AndroidSdkVersion.M));
    }

    @Test
    public void testV2OneSignerOneSignatureAccepted() throws Exception {
        // APK signed with v2 scheme only, one signer, one signature
        assertVerifiedForEachForMinSdkVersion(
                "v2-only-with-dsa-sha256-%s.apk", DSA_KEY_NAMES, AndroidSdkVersion.N);
        assertVerifiedForEachForMinSdkVersion(
                "v2-only-with-ecdsa-sha256-%s.apk", EC_KEY_NAMES, AndroidSdkVersion.N);
        assertVerifiedForEachForMinSdkVersion(
                "v2-only-with-rsa-pkcs1-sha256-%s.apk", RSA_KEY_NAMES, AndroidSdkVersion.N);
        // RSA-PSS signatures tested in a separate test below

        // DSA with SHA-512 is not supported by Android platform and thus APK Signature Scheme v2
        // does not support that either
        // assertInstallSucceedsForEach("v2-only-with-dsa-sha512-%s.apk", DSA_KEY_NAMES);
        assertVerifiedForEachForMinSdkVersion(
                "v2-only-with-ecdsa-sha512-%s.apk", EC_KEY_NAMES, AndroidSdkVersion.N);
        assertVerifiedForEachForMinSdkVersion(
                "v2-only-with-rsa-pkcs1-sha512-%s.apk", RSA_KEY_NAMES, AndroidSdkVersion.N);
    }

    @Test
    public void testV3OneSignerOneSignatureAccepted() throws Exception {
        // APK signed with v3 scheme only, one signer, one signature
        assertVerifiedForEachForMinSdkVersion(
                "v3-only-with-dsa-sha256-%s.apk", DSA_KEY_NAMES, AndroidSdkVersion.P);
        assertVerifiedForEachForMinSdkVersion(
                "v3-only-with-ecdsa-sha256-%s.apk", EC_KEY_NAMES, AndroidSdkVersion.P);
        assertVerifiedForEachForMinSdkVersion(
                "v3-only-with-rsa-pkcs1-sha256-%s.apk", RSA_KEY_NAMES, AndroidSdkVersion.P);

        assertVerifiedForEachForMinSdkVersion(
                "v3-only-with-ecdsa-sha512-%s.apk", EC_KEY_NAMES, AndroidSdkVersion.P);
        assertVerifiedForEachForMinSdkVersion(
                "v3-only-with-rsa-pkcs1-sha512-%s.apk", RSA_KEY_NAMES, AndroidSdkVersion.P);
    }

    @Test
    public void testV2OneSignerOneRsaPssSignatureAccepted() throws Exception {
        assumeThatRsaPssAvailable();
        // APK signed with v2 scheme only, one signer, one signature
        assertVerifiedForEachForMinSdkVersion(
                "v2-only-with-rsa-pss-sha256-%s.apk", RSA_KEY_NAMES, AndroidSdkVersion.N);
        assertVerifiedForEachForMinSdkVersion(
                "v2-only-with-rsa-pss-sha512-%s.apk",
                RSA_KEY_NAMES_2048_AND_LARGER, // 1024-bit key is too short for PSS with SHA-512
                AndroidSdkVersion.N);
    }

    @Test
    public void testV2SignatureDoesNotMatchSignedDataRejected() throws Exception {
        // APK signed with v2 scheme only, but the signature over signed-data does not verify

        // Bitflip in certificate field inside signed-data. Based on
        // v2-only-with-dsa-sha256-1024.apk.
        assertVerificationFailure(
                "v2-only-with-dsa-sha256-1024-sig-does-not-verify.apk",
                Issue.V2_SIG_DID_NOT_VERIFY);

        // Signature claims to be RSA PKCS#1 v1.5 with SHA-256, but is actually using SHA-512.
        // Based on v2-only-with-rsa-pkcs1-sha256-2048.apk.
        assertVerificationFailure(
                "v2-only-with-rsa-pkcs1-sha256-2048-sig-does-not-verify.apk",
                Issue.V2_SIG_VERIFY_EXCEPTION);

        // Bitflip in the ECDSA signature. Based on v2-only-with-ecdsa-sha256-p256.apk.
        assertVerificationFailure(
                "v2-only-with-ecdsa-sha256-p256-sig-does-not-verify.apk",
                Issue.V2_SIG_DID_NOT_VERIFY);
    }

    @Test
    public void testV3SignatureDoesNotMatchSignedDataRejected() throws Exception {
        // APK signed with v3 scheme only, but the signature over signed-data does not verify

        // Bitflip in DSA signature. Based on v3-only-with-dsa-sha256-2048.apk.
        assertVerificationFailure(
                "v3-only-with-dsa-sha256-2048-sig-does-not-verify.apk",
                Issue.V3_SIG_DID_NOT_VERIFY);

        // Bitflip in signed data. Based on v3-only-with-rsa-pkcs1-sha256-3072.apk
        assertVerificationFailure(
                "v3-only-with-rsa-pkcs1-sha256-3072-sig-does-not-verify.apk",
                Issue.V3_SIG_DID_NOT_VERIFY);

        // Based on v3-only-with-ecdsa-sha512-p521 with the signature ID changed to be ECDSA with
        // SHA-256.
        assertVerificationFailure(
                "v3-only-with-ecdsa-sha512-p521-sig-does-not-verify.apk",
                Issue.V3_SIG_DID_NOT_VERIFY);
    }

    @Test
    public void testV2RsaPssSignatureDoesNotMatchSignedDataRejected() throws Exception {
        assumeThatRsaPssAvailable();

        // APK signed with v2 scheme only, but the signature over signed-data does not verify.

        // Signature claims to be RSA PSS with SHA-256 and 32 bytes of salt, but is actually using 0
        // bytes of salt. Based on v2-only-with-rsa-pkcs1-sha256-2048.apk. Obtained by modifying APK
        // signer to use the wrong amount of salt.
        assertVerificationFailure(
                "v2-only-with-rsa-pss-sha256-2048-sig-does-not-verify.apk",
                Issue.V2_SIG_DID_NOT_VERIFY);
    }

    @Test
    public void testV2ContentDigestMismatchRejected() throws Exception {
        // APK signed with v2 scheme only, but the digest of contents does not match the digest
        // stored in signed-data
        ApkVerifier.Issue error = Issue.V2_SIG_APK_DIGEST_DID_NOT_VERIFY;

        // Based on v2-only-with-rsa-pkcs1-sha512-4096.apk. Obtained by modifying APK signer to
        // flip the leftmost bit in content digest before signing signed-data.
        assertVerificationFailure("v2-only-with-rsa-pkcs1-sha512-4096-digest-mismatch.apk", error);

        // Based on v2-only-with-ecdsa-sha256-p256.apk. Obtained by modifying APK signer to flip the
        // leftmost bit in content digest before signing signed-data.
        assertVerificationFailure("v2-only-with-ecdsa-sha256-p256-digest-mismatch.apk", error);
    }

    @Test
    public void testV3ContentDigestMismatchRejected() throws Exception {
        // APK signed with v3 scheme only, but the digest of contents does not match the digest
        // stored in signed-data.

        // Based on v3-only-with-rsa-pkcs1-sha512-8192. Obtained by flipping a bit in the local
        // file header of the APK.
        assertVerificationFailure(
                "v3-only-with-rsa-pkcs1-sha512-8192-digest-mismatch.apk",
                Issue.V3_SIG_APK_DIGEST_DID_NOT_VERIFY);

        // Based on v3-only-with-dsa-sha256-3072.apk. Obtained by modifying APK signer to flip the
        // leftmost bit in content digest before signing signed-data.
        assertVerificationFailure(
                "v3-only-with-dsa-sha256-3072-digest-mismatch.apk",
                Issue.V3_SIG_APK_DIGEST_DID_NOT_VERIFY);
    }

    @Test
    public void testNoApkSignatureSchemeBlockRejected() throws Exception {
        // APK signed with v2 scheme only, but the rules for verifying APK Signature Scheme v2
        // signatures say that this APK must not be verified using APK Signature Scheme v2.

        // Obtained from v2-only-with-rsa-pkcs1-sha512-4096.apk by flipping a bit in the magic
        // field in the footer of APK Signing Block. This makes the APK Signing Block disappear.
        assertVerificationFailure(
                "v2-only-wrong-apk-sig-block-magic.apk", Issue.JAR_SIG_NO_MANIFEST);

        // Obtained by modifying APK signer to insert "GARBAGE" between ZIP Central Directory and
        // End of Central Directory. The APK is otherwise fine and is signed with APK Signature
        // Scheme v2. Based on v2-only-with-rsa-pkcs1-sha256.apk.
        assertVerificationFailure(
                "v2-only-garbage-between-cd-and-eocd.apk", Issue.JAR_SIG_NO_MANIFEST);

        // Obtained by modifying the size in APK Signature Block header. Based on
        // v2-only-with-ecdsa-sha512-p521.apk.
        assertVerificationFailure(
                "v2-only-apk-sig-block-size-mismatch.apk", Issue.JAR_SIG_NO_MANIFEST);

        // Obtained by modifying the ID under which APK Signature Scheme v2 Block is stored in
        // APK Signing Block and by modifying the APK signer to not insert anti-stripping
        // protections into JAR Signature. The APK should appear as having no APK Signature Scheme
        // v2 Block and should thus successfully verify using JAR Signature Scheme.
        assertVerified(verify("v1-with-apk-sig-block-but-without-apk-sig-scheme-v2-block.apk"));
    }

    @Test
    public void testNoV3ApkSignatureSchemeBlockRejected() throws Exception {
        // Obtained from v3-only-with-ecdsa-sha512-p384.apk by flipping a bit in the magic field
        // in the footer of the APK Signing Block.
        assertVerificationFailure(
                "v3-only-with-ecdsa-sha512-p384-wrong-apk-sig-block-magic.apk",
                Issue.JAR_SIG_NO_MANIFEST);

        // Obtained from v3-only-with-rsa-pkcs1-sha512-4096.apk by modifying the size in the APK
        // Signature Block header and footer.
        assertVerificationFailure(
                "v3-only-with-rsa-pkcs1-sha512-4096-apk-sig-block-size-mismatch.apk",
                Issue.JAR_SIG_NO_MANIFEST);
    }

    @Test(expected = ApkFormatException.class)
    public void testTruncatedZipCentralDirectoryRejected() throws Exception {
        // Obtained by modifying APK signer to truncate the ZIP Central Directory by one byte. The
        // APK is otherwise fine and is signed with APK Signature Scheme v2. Based on
        // v2-only-with-rsa-pkcs1-sha256.apk
        verify("v2-only-truncated-cd.apk");
    }

    @Test
    public void testV2UnknownPairIgnoredInApkSigningBlock() throws Exception {
        // Obtained by modifying APK signer to emit an unknown ID-value pair into APK Signing Block
        // before the ID-value pair containing the APK Signature Scheme v2 Block. The unknown
        // ID-value should be ignored.
        assertVerified(
                verifyForMinSdkVersion(
                        "v2-only-unknown-pair-in-apk-sig-block.apk", AndroidSdkVersion.N));
    }

    @Test
    public void testV3UnknownPairIgnoredInApkSigningBlock() throws Exception {
        // Obtained by modifying APK signer to emit an unknown ID value pair into APK Signing Block
        // before the ID value pair containing the APK Signature Scheme v3 Block. The unknown
        // ID value should be ignored.
        assertVerified(
                verifyForMinSdkVersion(
                        "v3-only-unknown-pair-in-apk-sig-block.apk", AndroidSdkVersion.P));
    }

    @Test
    public void testV2UnknownSignatureAlgorithmsIgnored() throws Exception {
        // APK is signed with a known signature algorithm and with a couple of unknown ones.
        // Obtained by modifying APK signer to use "unknown" signature algorithms in addition to
        // known ones.
        assertVerified(
                verifyForMinSdkVersion(
                        "v2-only-with-ignorable-unsupported-sig-algs.apk", AndroidSdkVersion.N));
    }

    @Test
    public void testV3UnknownSignatureAlgorithmsIgnored() throws Exception {
        // APK is signed with a known signature algorithm and a couple of unknown ones.
        // Obtained by modifying APK signer to use "unknown" signature algorithms in addition to
        // known ones.
        assertVerified(
                verifyForMinSdkVersion(
                        "v3-only-with-ignorable-unsupported-sig-algs.apk", AndroidSdkVersion.P));
    }

    @Test
    public void testV3WithOnlyUnknownSignatureAlgorithmsRejected() throws Exception {
        // APK is only signed with an unknown signature algorithm. Obtained by modifying APK
        // signer's ID for a known signature algorithm.
        assertVerificationFailure(
                "v3-only-no-supported-sig-algs.apk", Issue.V3_SIG_NO_SUPPORTED_SIGNATURES);
    }

    @Test
    public void testV2UnknownAdditionalAttributeIgnored() throws Exception {
        // APK's v2 signature contains an unknown additional attribute, but is otherwise fine.
        // Obtained by modifying APK signer to output an additional attribute with ID 0x01020304
        // and value 0x05060708.
        assertVerified(
                verifyForMinSdkVersion("v2-only-unknown-additional-attr.apk", AndroidSdkVersion.N));
    }

    @Test
    public void testV3UnknownAdditionalAttributeIgnored() throws Exception {
        // APK's v3 signature contains unknown additional attributes before and after the lineage.
        // Obtained by modifying APK signer to output additional attributes with IDs 0x11223344
        // and 0x99aabbcc with values 0x55667788 and 0xddeeff00
        assertVerified(
                verifyForMinSdkVersion("v3-only-unknown-additional-attr.apk", AndroidSdkVersion.P));

        // APK's v2 and v3 signatures contain unknown additional attributes before and after the
        // anti-stripping and lineage attributes.
        assertVerified(
                verifyForMinSdkVersion("v2v3-unknown-additional-attr.apk", AndroidSdkVersion.P));
    }

    @Test
    public void testV2MismatchBetweenSignaturesAndDigestsBlockRejected() throws Exception {
        // APK is signed with a single signature algorithm, but the digests block claims that it is
        // signed with two different signature algorithms. Obtained by modifying APK Signer to
        // emit an additional digest record with signature algorithm 0x12345678.
        assertVerificationFailure(
                "v2-only-signatures-and-digests-block-mismatch.apk",
                Issue.V2_SIG_SIG_ALG_MISMATCH_BETWEEN_SIGNATURES_AND_DIGESTS_RECORDS);
    }

    @Test
    public void testV3MismatchBetweenSignaturesAndDigestsBlockRejected() throws Exception {
        // APK is signed with a single signature algorithm, but the digests block claims that it is
        // signed with two different signature algorithms. Obtained by modifying APK Signer to
        // emit an additional digest record with signature algorithm 0x11223344.
        assertVerificationFailure(
                "v3-only-signatures-and-digests-block-mismatch.apk",
                Issue.V3_SIG_SIG_ALG_MISMATCH_BETWEEN_SIGNATURES_AND_DIGESTS_RECORDS);
    }

    @Test
    public void testV2MismatchBetweenPublicKeyAndCertificateRejected() throws Exception {
        // APK is signed with v2 only. The public key field does not match the public key in the
        // leaf certificate. Obtained by modifying APK signer to write out a modified leaf
        // certificate where the RSA modulus has a bitflip.
        assertVerificationFailure(
                "v2-only-cert-and-public-key-mismatch.apk",
                Issue.V2_SIG_PUBLIC_KEY_MISMATCH_BETWEEN_CERTIFICATE_AND_SIGNATURES_RECORD);
    }

    @Test
    public void testV3MismatchBetweenPublicKeyAndCertificateRejected() throws Exception {
        // APK is signed with v3 only. The public key field does not match the public key in the
        // leaf certificate. Obtained by modifying APK signer to write out a modified leaf
        // certificate where the RSA modulus has a bitflip.
        assertVerificationFailure(
                "v3-only-cert-and-public-key-mismatch.apk",
                Issue.V3_SIG_PUBLIC_KEY_MISMATCH_BETWEEN_CERTIFICATE_AND_SIGNATURES_RECORD);
    }

    @Test
    public void testV2SignerBlockWithNoCertificatesRejected() throws Exception {
        // APK is signed with v2 only. There are no certificates listed in the signer block.
        // Obtained by modifying APK signer to output no certificates.
        assertVerificationFailure("v2-only-no-certs-in-sig.apk", Issue.V2_SIG_NO_CERTIFICATES);
    }

    @Test
    public void testV3SignerBlockWithNoCertificatesRejected() throws Exception {
        // APK is signed with v3 only. There are no certificates listed in the signer block.
        // Obtained by modifying APK signer to output no certificates.
        assertVerificationFailure("v3-only-no-certs-in-sig.apk", Issue.V3_SIG_NO_CERTIFICATES);
    }

    @Test
    public void testTwoSignersAccepted() throws Exception {
        // APK signed by two different signers
        assertVerified(verify("two-signers.apk"));
        assertVerified(verify("v1-only-two-signers.apk"));
        assertVerified(verifyForMinSdkVersion("v2-only-two-signers.apk", AndroidSdkVersion.N));
    }

    @Test
    public void testV2TwoSignersRejectedWhenOneBroken() throws Exception {
        // Bitflip in the ECDSA signature of second signer. Based on two-signers.apk.
        // This asserts that breakage in any signer leads to rejection of the APK.
        assertVerificationFailure(
                "two-signers-second-signer-v2-broken.apk", Issue.V2_SIG_DID_NOT_VERIFY);
    }

    @Test
    public void testV2TwoSignersRejectedWhenOneWithoutSignatures() throws Exception {
        // APK v2-signed by two different signers. However, there are no signatures for the second
        // signer.
        assertVerificationFailure(
                "v2-only-two-signers-second-signer-no-sig.apk", Issue.V2_SIG_NO_SIGNATURES);
    }

    @Test
    public void testV2TwoSignersRejectedWhenOneWithoutSupportedSignatures() throws Exception {
        // APK v2-signed by two different signers. However, there are no supported signatures for
        // the second signer.
        assertVerificationFailure(
                "v2-only-two-signers-second-signer-no-supported-sig.apk",
                Issue.V2_SIG_NO_SUPPORTED_SIGNATURES);
    }

    @Test
    public void testCorrectCertUsedFromPkcs7SignedDataCertsSet() throws Exception {
        // Obtained by prepending the rsa-1024 certificate to the PKCS#7 SignedData certificates set
        // of v1-only-with-rsa-pkcs1-sha1-1.2.840.113549.1.1.1-2048.apk META-INF/CERT.RSA. The certs
        // (in the order of appearance in the file) are thus: rsa-1024, rsa-2048. The package's
        // signing cert is rsa-2048.
        ApkVerifier.Result result = verify("v1-only-pkcs7-cert-bag-first-cert-not-used.apk");
        assertVerified(result);
        List<X509Certificate> signingCerts = result.getSignerCertificates();
        assertEquals(1, signingCerts.size());
        assertEquals(
                "fb5dbd3c669af9fc236c6991e6387b7f11ff0590997f22d0f5c74ff40e04fca8",
                HexEncoding.encode(sha256(signingCerts.get(0).getEncoded())));
    }

    @Test
    public void testV1SchemeSignatureCertNotReencoded() throws Exception {
        // Regression test for b/30148997 and b/18228011. When PackageManager does not preserve the
        // original encoded form of signing certificates, bad things happen, such as rejection of
        // completely valid updates to apps. The issue in b/30148997 and b/18228011 was that
        // PackageManager started re-encoding signing certs into DER. This normally produces exactly
        // the original form because X.509 certificates are supposed to be DER-encoded. However, a
        // small fraction of Android apps uses X.509 certificates which are not DER-encoded. For
        // such apps, re-encoding into DER changes the serialized form of the certificate, creating
        // a mismatch with the serialized form stored in the PackageManager database, leading to the
        // rejection of updates for the app.
        //
        // v1-only-with-rsa-1024-cert-not-der.apk cert's signature is not DER-encoded. It is
        // BER-encoded, with length encoded as two bytes instead of just one.
        // v1-only-with-rsa-1024-cert-not-der.apk META-INF/CERT.RSA was obtained from
        // v1-only-with-rsa-1024.apk META-INF/CERT.RSA by manually modifying the ASN.1 structure.
        ApkVerifier.Result result = verify("v1-only-with-rsa-1024-cert-not-der.apk");

        // On JDK 8u131 and newer, when the default (SUN) X.509 CertificateFactory implementation is
        // used, PKCS #7 signature verification fails because the certificate is not DER-encoded.
        // This contrived block of code disables this test in this scenario.
        if (!result.isVerified()) {
            List<ApkVerifier.Result.V1SchemeSignerInfo> signers = result.getV1SchemeSigners();
            if (signers.size() > 0) {
                ApkVerifier.Result.V1SchemeSignerInfo signer = signers.get(0);
                for (IssueWithParams issue : signer.getErrors()) {
                    if (issue.getIssue() == Issue.JAR_SIG_PARSE_EXCEPTION) {
                        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                        if ("SUN".equals(certFactory.getProvider().getName())) {
                            Throwable exception = (Throwable) issue.getParams()[1];
                            Throwable e = exception;
                            while (e != null) {
                                String msg = e.getMessage();
                                e = e.getCause();
                                if ((msg != null)
                                        && (msg.contains("Redundant length bytes found"))) {
                                    Assume.assumeNoException(exception);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        assertVerified(result);
        List<X509Certificate> signingCerts = result.getSignerCertificates();
        assertEquals(1, signingCerts.size());
        assertEquals(
                "c5d4535a7e1c8111687a8374b2198da6f5ff8d811a7a25aa99ef060669342fa9",
                HexEncoding.encode(sha256(signingCerts.get(0).getEncoded())));
    }

    @Test
    public void testV1SchemeSignatureCertNotReencoded2() throws Exception {
        // Regression test for b/30148997 and b/18228011. When PackageManager does not preserve the
        // original encoded form of signing certificates, bad things happen, such as rejection of
        // completely valid updates to apps. The issue in b/30148997 and b/18228011 was that
        // PackageManager started re-encoding signing certs into DER. This normally produces exactly
        // the original form because X.509 certificates are supposed to be DER-encoded. However, a
        // small fraction of Android apps uses X.509 certificates which are not DER-encoded. For
        // such apps, re-encoding into DER changes the serialized form of the certificate, creating
        // a mismatch with the serialized form stored in the PackageManager database, leading to the
        // rejection of updates for the app.
        //
        // v1-only-with-rsa-1024-cert-not-der2.apk cert's signature is not DER-encoded. It is
        // BER-encoded, with the BIT STRING value containing an extraneous leading 0x00 byte.
        // v1-only-with-rsa-1024-cert-not-der2.apk META-INF/CERT.RSA was obtained from
        // v1-only-with-rsa-1024.apk META-INF/CERT.RSA by manually modifying the ASN.1 structure.
        ApkVerifier.Result result = verify("v1-only-with-rsa-1024-cert-not-der2.apk");
        assertVerified(result);
        List<X509Certificate> signingCerts = result.getSignerCertificates();
        assertEquals(1, signingCerts.size());
        assertEquals(
                "da3da398de674541313deed77218ce94798531ea5131bb9b1bb4063ba4548cfb",
                HexEncoding.encode(sha256(signingCerts.get(0).getEncoded())));
    }

    @Test
    public void testMaxSizedZipEocdCommentAccepted() throws Exception {
        // Obtained by modifying apksigner to produce a max-sized (0xffff bytes long) ZIP End of
        // Central Directory comment, and signing the original.apk using the modified apksigner.
        assertVerified(verify("v1-only-max-sized-eocd-comment.apk"));
        assertVerified(
                verifyForMinSdkVersion("v2-only-max-sized-eocd-comment.apk", AndroidSdkVersion.N));
    }

    @Test
    public void testEmptyApk() throws Exception {
        // Unsigned empty ZIP archive
        try {
            verifyForMinSdkVersion("empty-unsigned.apk", 1);
            fail("ApkFormatException should've been thrown");
        } catch (ApkFormatException expected) {
        }

        // JAR-signed empty ZIP archive
        try {
            verifyForMinSdkVersion("v1-only-empty.apk", 18);
            fail("ApkFormatException should've been thrown");
        } catch (ApkFormatException expected) {
        }

        // APK Signature Scheme v2 signed empty ZIP archive
        try {
            verifyForMinSdkVersion("v2-only-empty.apk", AndroidSdkVersion.N);
            fail("ApkFormatException should've been thrown");
        } catch (ApkFormatException expected) {
        }

        // APK Signature Scheme v3 signed empty ZIP archive
        try {
            verifyForMinSdkVersion("v3-only-empty.apk", AndroidSdkVersion.P);
            fail("ApkFormatException should've been thrown");
        } catch (ApkFormatException expected) {
        }
    }

    @Test
    public void testTargetSandboxVersion2AndHigher() throws Exception {
        // This APK (and its variants below) use minSdkVersion 18, meaning it needs to be signed
        // with v1 and v2 schemes

        // This APK is signed with v1 and v2 schemes and thus should verify
        assertVerified(verify("targetSandboxVersion-2.apk"));

        // v1 signature is needed only if minSdkVersion is lower than 24
        assertVerificationFailure(
                verify("v2-only-targetSandboxVersion-2.apk"), Issue.JAR_SIG_NO_MANIFEST);
        assertVerified(verifyForMinSdkVersion("v2-only-targetSandboxVersion-2.apk", 24));

        // v2 signature is required
        assertVerificationFailure(
                verify("v1-only-targetSandboxVersion-2.apk"),
                Issue.NO_SIG_FOR_TARGET_SANDBOX_VERSION);
        assertVerificationFailure(
                verify("unsigned-targetSandboxVersion-2.apk"),
                Issue.NO_SIG_FOR_TARGET_SANDBOX_VERSION);

        // minSdkVersion 28, meaning v1 signature not needed
        assertVerified(verify("v2-only-targetSandboxVersion-3.apk"));
    }

    @Test
    public void testTargetSdkMinSchemeVersionNotMet() throws Exception {
        // Android 11 / SDK version 30 requires apps targeting this SDK version or higher must be
        // signed with at least the V2 signature scheme. This test verifies if an app is targeting
        // this SDK version and is only signed with a V1 signature then the verifier reports the
        // platform will not accept it.
        assertVerificationFailure(verify("v1-ec-p256-targetSdk-30.apk"),
                Issue.MIN_SIG_SCHEME_FOR_TARGET_SDK_NOT_MET);
    }

    @Test
    public void testTargetSdkMinSchemeVersionMet() throws Exception {
        // This test verifies if an app is signed with the minimum required signature scheme version
        // for the target SDK version then the verifier reports the platform will accept it.
        assertVerified(verify("v2-ec-p256-targetSdk-30.apk"));

        // If an app is only signed with a signature scheme higher than the required version for the
        // target SDK the verifier should also report that the platform will accept it.
        assertVerified(verify("v3-ec-p256-targetSdk-30.apk"));
    }

    @Test
    public void testTargetSdkMinSchemeVersionNotMetMaxLessThanTarget() throws Exception {
        // If the minimum signature scheme for the target SDK version is not met but the maximum
        // SDK version is less than the target then the verifier should report that the platform
        // will accept it since the specified max SDK version does not know about the minimum
        // signature scheme requirement.
        verifyForMaxSdkVersion("v1-ec-p256-targetSdk-30.apk", 29);
    }

    @Test
    public void testTargetSdkNoUsesSdkElement() throws Exception {
        // The target SDK minimum signature scheme version check will attempt to obtain the
        // targetSdkVersion attribute value from the uses-sdk element in the AndroidManifest. If
        // the targetSdkVersion is not specified then the verifier should behave the same as the
        // platform; the minSdkVersion should be used when available and when neither the minimum or
        // target SDK are specified a default value of 1 should be used. This test verifies that the
        // verifier does not fail when the uses-sdk element is not specified.
        verify("v1-only-no-uses-sdk.apk");
    }

    @Test
    public void testV1MultipleDigestAlgsInManifestAndSignatureFile() throws Exception {
        // MANIFEST.MF contains SHA-1 and SHA-256 digests for each entry, .SF contains only SHA-1
        // digests. This file was obtained by:
        //   jarsigner -sigalg SHA256withRSA -digestalg SHA-256 ... <file> ...
        //   jarsigner -sigalg SHA1withRSA -digestalg SHA1 ... <same file> ...
        assertVerified(verify("v1-sha1-sha256-manifest-and-sha1-sf.apk"));

        // MANIFEST.MF and .SF contain SHA-1 and SHA-256 digests for each entry. This file was
        // obtained by modifying apksigner to output multiple digests.
        assertVerified(verify("v1-sha1-sha256-manifest-and-sf.apk"));

        // One of the digests is wrong in either MANIFEST.MF or .SF. These files were obtained by
        // modifying apksigner to output multiple digests and to flip a bit to create a wrong
        // digest.

        // SHA-1 digests in MANIFEST.MF are wrong, but SHA-256 digests are OK.
        // The APK will fail to verify on API Level 17 and lower, but will verify on API Level 18
        // and higher.
        assertVerificationFailure(
                verify("v1-sha1-sha256-manifest-and-sf-with-sha1-wrong-in-manifest.apk"),
                Issue.JAR_SIG_ZIP_ENTRY_DIGEST_DID_NOT_VERIFY);
        assertVerificationFailure(
                verifyForMaxSdkVersion(
                        "v1-sha1-sha256-manifest-and-sf-with-sha1-wrong-in-manifest.apk", 17),
                Issue.JAR_SIG_ZIP_ENTRY_DIGEST_DID_NOT_VERIFY);
        assertVerified(
                verifyForMinSdkVersion(
                        "v1-sha1-sha256-manifest-and-sf-with-sha1-wrong-in-manifest.apk", 18));

        // SHA-1 digests in .SF are wrong, but SHA-256 digests are OK.
        // The APK will fail to verify on API Level 17 and lower, but will verify on API Level 18
        // and higher.
        assertVerificationFailure(
                verify("v1-sha1-sha256-manifest-and-sf-with-sha1-wrong-in-sf.apk"),
                Issue.JAR_SIG_MANIFEST_SECTION_DIGEST_DID_NOT_VERIFY);
        assertVerificationFailure(
                verifyForMaxSdkVersion(
                        "v1-sha1-sha256-manifest-and-sf-with-sha1-wrong-in-sf.apk", 17),
                Issue.JAR_SIG_MANIFEST_SECTION_DIGEST_DID_NOT_VERIFY);
        assertVerified(
                verifyForMinSdkVersion(
                        "v1-sha1-sha256-manifest-and-sf-with-sha1-wrong-in-sf.apk", 18));

        // SHA-256 digests in MANIFEST.MF are wrong, but SHA-1 digests are OK.
        // The APK will fail to verify on API Level 18 and higher, but will verify on API Level 17
        // and lower.
        assertVerificationFailure(
                verify("v1-sha1-sha256-manifest-and-sf-with-sha256-wrong-in-manifest.apk"),
                Issue.JAR_SIG_ZIP_ENTRY_DIGEST_DID_NOT_VERIFY);
        assertVerificationFailure(
                verifyForMinSdkVersion(
                        "v1-sha1-sha256-manifest-and-sf-with-sha256-wrong-in-manifest.apk", 18),
                Issue.JAR_SIG_ZIP_ENTRY_DIGEST_DID_NOT_VERIFY);
        assertVerified(
                verifyForMaxSdkVersion(
                        "v1-sha1-sha256-manifest-and-sf-with-sha256-wrong-in-manifest.apk", 17));

        // SHA-256 digests in .SF are wrong, but SHA-1 digests are OK.
        // The APK will fail to verify on API Level 18 and higher, but will verify on API Level 17
        // and lower.
        assertVerificationFailure(
                verify("v1-sha1-sha256-manifest-and-sf-with-sha256-wrong-in-sf.apk"),
                Issue.JAR_SIG_MANIFEST_SECTION_DIGEST_DID_NOT_VERIFY);
        assertVerificationFailure(
                verifyForMinSdkVersion(
                        "v1-sha1-sha256-manifest-and-sf-with-sha256-wrong-in-sf.apk", 18),
                Issue.JAR_SIG_MANIFEST_SECTION_DIGEST_DID_NOT_VERIFY);
        assertVerified(
                verifyForMaxSdkVersion(
                        "v1-sha1-sha256-manifest-and-sf-with-sha256-wrong-in-sf.apk", 17));
    }

    @Test
    public void testV1WithUnsupportedCharacterInZipEntryName() throws Exception {
        // Android Package Manager does not support ZIP entry names containing CR or LF
        assertVerificationFailure(
                verify("v1-only-with-cr-in-entry-name.apk"),
                Issue.JAR_SIG_UNNNAMED_MANIFEST_SECTION);
        assertVerificationFailure(
                verify("v1-only-with-lf-in-entry-name.apk"),
                Issue.JAR_SIG_UNNNAMED_MANIFEST_SECTION);
    }

    @Test
    public void testWeirdZipCompressionMethod() throws Exception {
        // Any ZIP compression method other than STORED is treated as DEFLATED by Android.
        // This APK declares compression method 21 (neither STORED nor DEFLATED) for CERT.RSA entry,
        // but the entry is actually Deflate-compressed.
        assertVerified(verify("weird-compression-method.apk"));
    }

    @Test
    public void testZipCompressionMethodMismatchBetweenLfhAndCd() throws Exception {
        // Android Package Manager ignores compressionMethod field in Local File Header and always
        // uses the compressionMethod from Central Directory instead.
        // In this APK, compression method of CERT.RSA is declared as STORED in Local File Header
        // and as DEFLATED in Central Directory. The entry is actually Deflate-compressed.
        assertVerified(verify("mismatched-compression-method.apk"));
    }

    @Test
    public void testV1SignedAttrs() throws Exception {
        String apk = "v1-only-with-signed-attrs.apk";
        assertVerificationFailure(
                verifyForMinSdkVersion(apk, AndroidSdkVersion.JELLY_BEAN_MR2),
                Issue.JAR_SIG_VERIFY_EXCEPTION);
        assertVerified(verifyForMinSdkVersion(apk, AndroidSdkVersion.KITKAT));

        apk = "v1-only-with-signed-attrs-signerInfo1-good-signerInfo2-good.apk";
        assertVerificationFailure(
                verifyForMinSdkVersion(apk, AndroidSdkVersion.JELLY_BEAN_MR2),
                Issue.JAR_SIG_VERIFY_EXCEPTION);
        assertVerified(verifyForMinSdkVersion(apk, AndroidSdkVersion.KITKAT));
    }

    @Test
    public void testV1SignedAttrsNotInDerOrder() throws Exception {
        // Android does not re-order SignedAttributes despite it being a SET OF. Pre-N, Android
        // treated them as SEQUENCE OF, meaning no re-ordering is necessary. From N onwards, it
        // treats them as SET OF, but does not re-encode into SET OF during verification if all
        // attributes parsed fine.
        assertVerified(verify("v1-only-with-signed-attrs-wrong-order.apk"));
        assertVerified(
                verify("v1-only-with-signed-attrs-signerInfo1-wrong-order-signerInfo2-good.apk"));
    }

    @Test
    public void testV1SignedAttrsMissingContentType() throws Exception {
        // SignedAttributes must contain ContentType. Pre-N, Android ignores this requirement.
        // Android N onwards rejects such APKs.
        String apk = "v1-only-with-signed-attrs-missing-content-type.apk";
        assertVerified(verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1));
        assertVerificationFailure(verify(apk), Issue.JAR_SIG_VERIFY_EXCEPTION);
        // Assert that this issue fails verification of the entire signature block, rather than
        // skipping the broken SignerInfo. The second signer info SignerInfo verifies fine, but
        // verification does not get there.
        apk = "v1-only-with-signed-attrs-signerInfo1-missing-content-type-signerInfo2-good.apk";
        assertVerified(verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1));
        assertVerificationFailure(verify(apk), Issue.JAR_SIG_VERIFY_EXCEPTION);
    }

    @Test
    public void testV1SignedAttrsWrongContentType() throws Exception {
        // ContentType of SignedAttributes must equal SignedData.encapContentInfo.eContentType.
        // Pre-N, Android ignores this requirement.
        // From N onwards, Android rejects such SignerInfos.
        String apk = "v1-only-with-signed-attrs-wrong-content-type.apk";
        assertVerified(verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1));
        assertVerificationFailure(verify(apk), Issue.JAR_SIG_DID_NOT_VERIFY);
        // First SignerInfo does not verify on Android N and newer, but verification moves on to the
        // second SignerInfo, which verifies.
        apk = "v1-only-with-signed-attrs-signerInfo1-wrong-content-type-signerInfo2-good.apk";
        assertVerified(verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1));
        assertVerified(verifyForMinSdkVersion(apk, AndroidSdkVersion.N));
        // Although the APK's signature verifies on pre-N and N+, we reject such APKs because the
        // APK's verification results in different verified SignerInfos (and thus potentially
        // different signing certs) between pre-N and N+.
        assertVerificationFailure(verify(apk), Issue.JAR_SIG_DID_NOT_VERIFY);
    }

    @Test
    public void testV1SignedAttrsMissingDigest() throws Exception {
        // Content digest must be present in SignedAttributes
        String apk = "v1-only-with-signed-attrs-missing-digest.apk";
        assertVerificationFailure(
                verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1),
                Issue.JAR_SIG_VERIFY_EXCEPTION);
        assertVerificationFailure(
                verifyForMinSdkVersion(apk, AndroidSdkVersion.N), Issue.JAR_SIG_VERIFY_EXCEPTION);
        // Assert that this issue fails verification of the entire signature block, rather than
        // skipping the broken SignerInfo. The second signer info SignerInfo verifies fine, but
        // verification does not get there.
        apk = "v1-only-with-signed-attrs-signerInfo1-missing-digest-signerInfo2-good.apk";
        assertVerificationFailure(
                verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1),
                Issue.JAR_SIG_VERIFY_EXCEPTION);
        assertVerificationFailure(
                verifyForMinSdkVersion(apk, AndroidSdkVersion.N), Issue.JAR_SIG_VERIFY_EXCEPTION);
    }

    @Test
    public void testV1SignedAttrsMultipleGoodDigests() throws Exception {
        // Only one content digest must be present in SignedAttributes
        String apk = "v1-only-with-signed-attrs-multiple-good-digests.apk";
        assertVerificationFailure(
                verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1),
                Issue.JAR_SIG_PARSE_EXCEPTION);
        assertVerificationFailure(
                verifyForMinSdkVersion(apk, AndroidSdkVersion.N), Issue.JAR_SIG_PARSE_EXCEPTION);
        // Assert that this issue fails verification of the entire signature block, rather than
        // skipping the broken SignerInfo. The second signer info SignerInfo verifies fine, but
        // verification does not get there.
        apk = "v1-only-with-signed-attrs-signerInfo1-multiple-good-digests-signerInfo2-good.apk";
        assertVerificationFailure(
                verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1),
                Issue.JAR_SIG_PARSE_EXCEPTION);
        assertVerificationFailure(
                verifyForMinSdkVersion(apk, AndroidSdkVersion.N), Issue.JAR_SIG_PARSE_EXCEPTION);
    }

    @Test
    public void testV1SignedAttrsWrongDigest() throws Exception {
        // Content digest in SignedAttributes does not match the contents
        String apk = "v1-only-with-signed-attrs-wrong-digest.apk";
        assertVerificationFailure(
                verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1), Issue.JAR_SIG_DID_NOT_VERIFY);
        assertVerificationFailure(
                verifyForMinSdkVersion(apk, AndroidSdkVersion.N), Issue.JAR_SIG_DID_NOT_VERIFY);
        // First SignerInfo does not verify, but Android N and newer moves on to the second
        // SignerInfo, which verifies.
        apk = "v1-only-with-signed-attrs-signerInfo1-wrong-digest-signerInfo2-good.apk";
        assertVerificationFailure(
                verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1), Issue.JAR_SIG_DID_NOT_VERIFY);
        assertVerified(verifyForMinSdkVersion(apk, AndroidSdkVersion.N));
    }

    @Test
    public void testV1SignedAttrsWrongSignature() throws Exception {
        // Signature over SignedAttributes does not verify
        String apk = "v1-only-with-signed-attrs-wrong-signature.apk";
        assertVerificationFailure(
                verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1), Issue.JAR_SIG_DID_NOT_VERIFY);
        assertVerificationFailure(
                verifyForMinSdkVersion(apk, AndroidSdkVersion.N), Issue.JAR_SIG_DID_NOT_VERIFY);
        // First SignerInfo does not verify, but Android N and newer moves on to the second
        // SignerInfo, which verifies.
        apk = "v1-only-with-signed-attrs-signerInfo1-wrong-signature-signerInfo2-good.apk";
        assertVerificationFailure(
                verifyForMaxSdkVersion(apk, AndroidSdkVersion.N - 1), Issue.JAR_SIG_DID_NOT_VERIFY);
        assertVerified(verifyForMinSdkVersion(apk, AndroidSdkVersion.N));
    }

    @Test
    public void testSourceStampBlock_correctSignature() throws Exception {
        ApkVerifier.Result verificationResult = verify("valid-stamp.apk");
        // Verifies the signature of the APK.
        assertVerified(verificationResult);
        // Verifies the signature of source stamp.
        assertTrue(verificationResult.isSourceStampVerified());
    }

    @Test
    public void verifySourceStamp_correctSignature() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("valid-stamp.apk");
        // Since the API is only verifying the source stamp the result itself should be marked as
        // verified.
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);

        // The source stamp can also be verified by platform version; confirm the verification works
        // using just the max signature scheme version supported by that platform version.
        verificationResult = verifySourceStamp("valid-stamp.apk", 18, 18);
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);

        verificationResult = verifySourceStamp("valid-stamp.apk", 24, 24);
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);

        verificationResult = verifySourceStamp("valid-stamp.apk", 28, 28);
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);
    }

    @Test
    public void testSourceStampBlock_signatureMissing() throws Exception {
        ApkVerifier.Result verificationResult = verify("stamp-without-block.apk");
        // A broken stamp should not block a signing scheme verified APK.
        assertVerified(verificationResult);
        assertSourceStampVerificationFailure(verificationResult, Issue.SOURCE_STAMP_SIG_MISSING);
    }

    @Test
    public void verifySourceStamp_signatureMissing() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("stamp-without-block.apk");
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_NOT_VERIFIED);
        assertSourceStampVerificationFailure(verificationResult, Issue.SOURCE_STAMP_SIG_MISSING);
    }

    @Test
    public void testSourceStampBlock_certificateMismatch() throws Exception {
        ApkVerifier.Result verificationResult = verify("stamp-certificate-mismatch.apk");
        // A broken stamp should not block a signing scheme verified APK.
        assertVerified(verificationResult);
        assertSourceStampVerificationFailure(
                verificationResult,
                Issue.SOURCE_STAMP_CERTIFICATE_MISMATCH_BETWEEN_SIGNATURE_BLOCK_AND_APK);
    }

    @Test
    public void verifySourceStamp_certificateMismatch() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("stamp-certificate-mismatch.apk");
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(
                verificationResult,
                Issue.SOURCE_STAMP_CERTIFICATE_MISMATCH_BETWEEN_SIGNATURE_BLOCK_AND_APK);
    }

    @Test
    public void testSourceStampBlock_v1OnlySignatureValidStamp() throws Exception {
        ApkVerifier.Result verificationResult = verify("v1-only-with-stamp.apk");
        assertVerified(verificationResult);
        assertTrue(verificationResult.isSourceStampVerified());
    }

    @Test
    public void verifySourceStamp_v1OnlySignatureValidStamp() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("v1-only-with-stamp.apk");
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);

        // Confirm that the source stamp verification succeeds when specifying platform versions
        // that supported later signature scheme versions.
        verificationResult = verifySourceStamp("v1-only-with-stamp.apk", 28, 28);
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);

        verificationResult = verifySourceStamp("v1-only-with-stamp.apk", 24, 24);
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);
    }

    @Test
    public void testSourceStampBlock_v2OnlySignatureValidStamp() throws Exception {
        ApkVerifier.Result verificationResult = verify("v2-only-with-stamp.apk");
        assertVerified(verificationResult);
        assertTrue(verificationResult.isSourceStampVerified());
    }

    @Test
    public void verifySourceStamp_v2OnlySignatureValidStamp() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("v2-only-with-stamp.apk");
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);

        // Confirm that the source stamp verification succeeds when specifying a platform version
        // that supports a later signature scheme version.
        verificationResult = verifySourceStamp("v2-only-with-stamp.apk", 28, 28);
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);
    }

    @Test
    public void testSourceStampBlock_v3OnlySignatureValidStamp() throws Exception {
        ApkVerifier.Result verificationResult = verify("v3-only-with-stamp.apk");
        assertVerified(verificationResult);
        assertTrue(verificationResult.isSourceStampVerified());
    }

    @Test
    public void verifySourceStamp_v3OnlySignatureValidStamp() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("v3-only-with-stamp.apk");
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);
    }

    @Test
    public void testSourceStampBlock_apkHashMismatch_v1SignatureScheme() throws Exception {
        ApkVerifier.Result verificationResult = verify("stamp-apk-hash-mismatch-v1.apk");
        // A broken stamp should not block a signing scheme verified APK.
        assertVerified(verificationResult);
        assertSourceStampVerificationFailure(verificationResult, Issue.SOURCE_STAMP_DID_NOT_VERIFY);
    }

    @Test
    public void verifySourceStamp_apkHashMismatch_v1SignatureScheme() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("stamp-apk-hash-mismatch-v1.apk");
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult, Issue.SOURCE_STAMP_DID_NOT_VERIFY);
    }

    @Test
    public void testSourceStampBlock_apkHashMismatch_v2SignatureScheme() throws Exception {
        ApkVerifier.Result verificationResult = verify("stamp-apk-hash-mismatch-v2.apk");
        // A broken stamp should not block a signing scheme verified APK.
        assertVerified(verificationResult);
        assertSourceStampVerificationFailure(verificationResult, Issue.SOURCE_STAMP_DID_NOT_VERIFY);
    }

    @Test
    public void verifySourceStamp_apkHashMismatch_v2SignatureScheme() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("stamp-apk-hash-mismatch-v2.apk");
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult, Issue.SOURCE_STAMP_DID_NOT_VERIFY);
    }

    @Test
    public void testSourceStampBlock_apkHashMismatch_v3SignatureScheme() throws Exception {
        ApkVerifier.Result verificationResult = verify("stamp-apk-hash-mismatch-v3.apk");
        // A broken stamp should not block a signing scheme verified APK.
        assertVerified(verificationResult);
        assertSourceStampVerificationFailure(verificationResult, Issue.SOURCE_STAMP_DID_NOT_VERIFY);
    }

    @Test
    public void verifySourceStamp_apkHashMismatch_v3SignatureScheme() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("stamp-apk-hash-mismatch-v3.apk");
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult, Issue.SOURCE_STAMP_DID_NOT_VERIFY);
    }

    @Test
    public void testSourceStampBlock_malformedSignature() throws Exception {
        ApkVerifier.Result verificationResult = verify("stamp-malformed-signature.apk");
        // A broken stamp should not block a signing scheme verified APK.
        assertVerified(verificationResult);
        assertSourceStampVerificationFailure(
                verificationResult, Issue.SOURCE_STAMP_MALFORMED_SIGNATURE);
    }

    @Test
    public void verifySourceStamp_malformedSignature() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("stamp-malformed-signature.apk");
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(
                verificationResult, Issue.SOURCE_STAMP_MALFORMED_SIGNATURE);
    }

    @Test
    public void verifySourceStamp_expectedDigestMatchesActual() throws Exception {
        // The ApkVerifier provides an API to specify the expected certificate digest; this test
        // verifies that the test runs through to completion when the actual digest matches the
        // provided value.
        ApkVerifier.Result verificationResult = verifySourceStamp("v3-only-with-stamp.apk",
                RSA_2048_CERT_SHA256_DIGEST);
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);
    }

    @Test
    public void verifySourceStamp_expectedDigestMismatch() throws Exception {
        // If the caller requests source stamp verification with an expected cert digest that does
        // not match the actual digest in the APK the verifier should report the mismatch.
        ApkVerifier.Result verificationResult = verifySourceStamp("v3-only-with-stamp.apk",
                EC_P256_CERT_SHA256_DIGEST);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.CERT_DIGEST_MISMATCH);
        assertSourceStampVerificationFailure(verificationResult,
                Issue.SOURCE_STAMP_EXPECTED_DIGEST_MISMATCH);
    }

    @Test
    public void verifySourceStamp_validStampLineage() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("stamp-lineage-valid.apk");
        assertVerified(verificationResult);
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFIED);
    }

    @Test
    public void verifySourceStamp_invalidStampLineage() throws Exception {
        ApkVerifier.Result verificationResult = verifySourceStamp("stamp-lineage-invalid.apk");
        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult,
                Issue.SOURCE_STAMP_POR_CERT_MISMATCH);
    }

    @Test
    public void verifySourceStamp_noTimestamp_returnsDefaultValue() throws Exception {
        // A timestamp attribute was added to the source stamp, but verification of APKs that were
        // generated prior to the addition of the timestamp should still complete successfully,
        // returning a default value of 0 for the timestamp.
        ApkVerifier.Result verificationResult = verifySourceStamp("v3-only-with-stamp.apk");

        assertTrue(verificationResult.isSourceStampVerified());
        assertEquals(
                "A value of 0 should be returned for the timestamp when the attribute is not "
                        + "present",
                0, verificationResult.getSourceStampInfo().getTimestampEpochSeconds());
    }

    @Test
    public void verifySourceStamp_validTimestamp_returnsExpectedValue() throws Exception {
        // Once an APK is signed with a source stamp that contains a valid value for the timestamp
        // attribute, verification of the source stamp should result in the same value for the
        // timestamp returned to the verifier.
        ApkVerifier.Result verificationResult = verifySourceStamp(
                "stamp-valid-timestamp-value.apk");

        assertTrue(verificationResult.isSourceStampVerified());
        assertEquals(1644886584, verificationResult.getSourceStampInfo().getTimestampEpochSeconds());
    }

    @Test
    public void verifySourceStamp_validTimestampLargerBuffer_returnsExpectedValue()
            throws Exception {
        // The source stamp timestamp attribute value is expected to be written to an 8 byte buffer
        // as a little-endian long; while a larger buffer will not result in an error, any
        // additional space after the buffer's initial 8 bytes will be ignored. This test verifies a
        // valid timestamp value written to the first 8 bytes of a 16 byte buffer can still be read
        // successfully.
        ApkVerifier.Result verificationResult = verifySourceStamp(
                "stamp-valid-timestamp-16-byte-buffer.apk");

        assertTrue(verificationResult.isSourceStampVerified());
        assertEquals(1645126786,
                verificationResult.getSourceStampInfo().getTimestampEpochSeconds());
    }

    @Test
    public void verifySourceStamp_invalidTimestampValueEqualsZero_verificationFails()
            throws Exception {
        // If the source stamp timestamp attribute exists and is <= 0, then a warning should be
        // reported to notify the caller to the invalid attribute value. This test verifies a
        // a warning is reported when the timestamp attribute value is 0.
        ApkVerifier.Result verificationResult = verifySourceStamp(
                "stamp-invalid-timestamp-value-zero.apk");

        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult,
                Issue.SOURCE_STAMP_INVALID_TIMESTAMP);
    }

    @Test
    public void verifySourceStamp_invalidTimestampValueLessThanZero_verificationFails()
            throws Exception {
        // If the source stamp timestamp attribute exists and is <= 0, then a warning should be
        // reported to notify the caller to the invalid attribute value. This test verifies a
        // a warning is reported when the timestamp attribute value is < 0.
        ApkVerifier.Result verificationResult = verifySourceStamp(
                "stamp-invalid-timestamp-value-less-than-zero.apk");

        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult,
                Issue.SOURCE_STAMP_INVALID_TIMESTAMP);
    }

    @Test
    public void verifySourceStamp_invalidTimestampZeroInFirst8BytesOfBuffer_verificationFails()
            throws Exception {
        // The source stamp's timestamp attribute value is expected to be written to the first 8
        // bytes of the attribute's value buffer; if a larger buffer is used and the timestamp
        // value is not written as a little-endian long to the first 8 bytes of the buffer, then
        // an error should be reported for the timestamp attribute since the rest of the buffer will
        // be ignored.
        ApkVerifier.Result verificationResult = verifySourceStamp(
                "stamp-timestamp-in-last-8-of-16-byte-buffer.apk");

        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult,
                Issue.SOURCE_STAMP_INVALID_TIMESTAMP);
    }


    @Test
    public void verifySourceStamp_intTimestampValue_verificationFails() throws Exception {
        // Since the source stamp timestamp attribute value is a long, an attribute value with
        // insufficient space to hold a long value should result in a warning reported to the user.
        ApkVerifier.Result verificationResult = verifySourceStamp(
                "stamp-int-timestamp-value.apk");

        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult,
                Issue.SOURCE_STAMP_MALFORMED_ATTRIBUTE);
    }

    @Test
    public void verifySourceStamp_modifiedTimestampValue_verificationFails() throws Exception {
        // The source stamp timestamp attribute is part of the block's signed data; this test
        // verifies if the value of the timestamp in the stamp block is modified then verification
        // of the source stamp should fail.
        ApkVerifier.Result verificationResult = verifySourceStamp(
                "stamp-valid-timestamp-value-modified.apk");

        assertSourceStampVerificationStatus(verificationResult,
                SourceStampVerificationStatus.STAMP_VERIFICATION_FAILED);
        assertSourceStampVerificationFailure(verificationResult,
                Issue.SOURCE_STAMP_DID_NOT_VERIFY);
    }

    @Test
    public void apkVerificationIssueAdapter_verifyAllBaseIssuesMapped() throws Exception {
        Field[] fields = ApkVerificationIssue.class.getFields();
        StringBuilder msg = new StringBuilder();
        for (Field field : fields) {
            // All public static int fields in the ApkVerificationIssue class should be issue IDs;
            // if any are added that are not intended as IDs a filter set should be applied to this
            // test.
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                if (!ApkVerifier.ApkVerificationIssueAdapter
                        .sVerificationIssueIdToIssue.containsKey(field.get(null))) {
                    if (msg.length() > 0) {
                        msg.append('\n');
                    }
                    msg.append(
                            "A mapping is required from ApkVerificationIssue." + field.getName()
                                    + " to an ApkVerifier.Issue in ApkVerificationIssueAdapter");
                }
            }
        }
        if (msg.length() > 0) {
            fail(msg.toString());
        }
    }

    @Test
    public void verifySignature_negativeModulusConscryptProvider() throws Exception {
        Provider conscryptProvider = null;
        try {
            conscryptProvider = new org.conscrypt.OpenSSLProvider();
            Security.insertProviderAt(conscryptProvider, 1);
            assertVerified(verify("v1v2v3-rsa-2048-negmod-in-cert.apk"));
        } catch (UnsatisfiedLinkError e) {
            // If the library for conscrypt is not available then skip this test.
            return;
        } finally {
            if (conscryptProvider != null) {
                Security.removeProvider(conscryptProvider.getName());
            }
        }
    }

    @Test
    public void verifyV31_rotationTarget34_containsExpectedSigners() throws Exception {
        // This test verifies an APK targeting a specific SDK version for rotation properly reports
        // that version for the rotated signer in the v3.1 block, and all other signing blocks
        // use the original signing key.
        ApkVerifier.Result result = verify("v31-rsa-2048_2-tgt-34-1-tgt-28.apk");

        assertVerified(result);
        assertResultContainsSigners(result, true, FIRST_RSA_2048_SIGNER_RESOURCE_NAME,
            SECOND_RSA_2048_SIGNER_RESOURCE_NAME);
        assertV31SignerTargetsMinApiLevel(result, SECOND_RSA_2048_SIGNER_RESOURCE_NAME, 34);
    }

    @Test
    public void verifyV31_missingStrippingAttr_warningReported() throws Exception {
        // The v3.1 signing block supports targeting SDK versions; to protect against these target
        // versions being modified the v3 signer contains a stripping protection attribute with the
        // SDK version on which rotation should be applied. This test verifies a warning is reported
        // when this attribute is not present in the v3 signer.
        ApkVerifier.Result result = verify("v31-tgt-33-no-v3-attr.apk");

        assertVerificationWarning(result, Issue.V31_ROTATION_MIN_SDK_ATTR_MISSING);
    }

    @Test
    public void verifyV31_strippingAttrMismatch_errorReportedOnSupportedVersions()
            throws Exception {
        // This test verifies if the stripping protection attribute does not properly match the
        // minimum SDK version on which rotation is supported then the APK should fail verification.
        ApkVerifier.Result result = verify("v31-tgt-34-v3-attr-value-33.apk");
        assertVerificationFailure(result, Issue.V31_ROTATION_MIN_SDK_MISMATCH);

        // SDK versions that do not support v3.1 should ignore the stripping protection attribute
        // and the v3.1 signing block.
        result = verifyForMaxSdkVersion("v31-tgt-34-v3-attr-value-33.apk",
            V3SchemeConstants.MIN_SDK_WITH_V31_SUPPORT - 1);
        assertVerified(result);
    }

    @Test
    public void verifyV31_missingV31Block_errorReportedOnSupportedVersions() throws Exception {
        // This test verifies if the stripping protection attribute contains a value for rotation
        // but a v3.1 signing block was not found then the APK should fail verification.
        ApkVerifier.Result result = verify("v31-block-stripped-v3-attr-value-33.apk");
        assertVerificationFailure(result, Issue.V31_BLOCK_MISSING);

        // SDK versions that do not support v3.1 should ignore the stripping protection attribute
        // and the v3.1 signing block.
        result = verifyForMaxSdkVersion("v31-block-stripped-v3-attr-value-33.apk",
            V3SchemeConstants.MIN_SDK_WITH_V31_SUPPORT - 1);
        assertVerified(result);
    }

    @Test
    public void verifyV31_v31BlockWithoutV3Block_reportsError() throws Exception {
        // A v3.1 block must always exist alongside a v3.0 block; if an APK's minSdkVersion is the
        // same as the version supporting rotation then it should be written to a v3.0 block.
        ApkVerifier.Result result = verify("v31-tgt-33-no-v3-block.apk");
        assertVerificationFailure(result, Issue.V31_BLOCK_FOUND_WITHOUT_V3_BLOCK);
    }

    @Test
    public void verifyV31_rotationTargetsDevRelease_resultReportsDevReleaseFlag() throws Exception {
        // Development releases use the SDK version of the previous release until the SDK is
        // finalized. In order to only target the development release and later, the v3.1 signature
        // scheme supports targeting development releases such that the SDK version X will install
        // on a device running X with the system property ro.build.version.codename set to a new
        // development codename (eg T); a release platform will have this set to "REL", and the
        // platform will ignore the v3.1 signer if the minSdkVersion is X and the codename is "REL".
        ApkVerifier.Result result = verify("v31-rsa-2048_2-tgt-34-dev-release.apk");

        assertVerified(result);
        assertV31SignerTargetsMinApiLevel(result, SECOND_RSA_2048_SIGNER_RESOURCE_NAME, 34);
        assertResultContainsSigners(result, true, FIRST_RSA_2048_SIGNER_RESOURCE_NAME,
                SECOND_RSA_2048_SIGNER_RESOURCE_NAME);
    }

    @Test
    public void verifyV3_v3RotatedSignerTargetsDevRelease_warningReported() throws Exception {
        // While a v3.1 signer can target a development release, v3.0 does not support the same
        // attribute since it is only intended for v3.1 with v3.0 using the original signer. This
        // test verifies a warning is reported if an APK has this flag set on a v3.0 signer since it
        // will be ignored by the platform.
        ApkVerifier.Result result = verify("v3-rsa-2048_2-tgt-dev-release.apk");

        assertVerificationWarning(result, Issue.V31_ROTATION_TARGETS_DEV_RELEASE_ATTR_ON_V3_SIGNER);
    }

    @Test
    public void verifyV31_rotationTargets34_resultContainsExpectedLineage() throws Exception {
        // During verification of the v3.1 and v3.0 signing blocks, ApkVerifier will set the
        // signing certificate lineage in the Result object; this test verifies a null lineage from
        // a v3.0 signer does not overwrite a valid lineage from a v3.1 signer.
        ApkVerifier.Result result = verify("v31-rsa-2048_2-tgt-34-1-tgt-28.apk");

        assertNotNull(result.getSigningCertificateLineage());
        SigningCertificateLineageTest.assertLineageContainsExpectedSigners(
                result.getSigningCertificateLineage(), FIRST_RSA_2048_SIGNER_RESOURCE_NAME,
                SECOND_RSA_2048_SIGNER_RESOURCE_NAME);
    }

    @Test
    public void verify31_minSdkVersionT_resultSuccessfullyVerified() throws Exception {
        // When a min-sdk-version of 33 is explicitly specified, apksig will behave the same as a
        // device running this API level and only verify a v3.1 signature if it exists. This test
        // verifies this v3.1 signature is sufficient to report the APK as verified.
        ApkVerifier.Result result = verifyForMinSdkVersion("v31-rsa-2048_2-tgt-33-1-tgt-28.apk",
                33);

        assertVerified(result);
        assertTrue(result.isVerifiedUsingV31Scheme());
    }

    @Test
    public void verify31_minSdkVersionTTargetSdk30_resultSuccessfullyVerified() throws Exception {
        // This test verifies when a min-sdk-version of 33 is specified and the APK targets API
        // level 30 or later, the v3.1 signature is sufficient to report the APK meets the
        // requirement of a minimum v2 signature.
        ApkVerifier.Result result = verifyForMinSdkVersion(
                "v31-ec-p256-2-tgt-33-1-tgt-28-targetSdk-30.apk", 33);

        assertVerified(result);
        assertTrue(result.isVerifiedUsingV31Scheme());
    }

    private ApkVerifier.Result verify(String apkFilenameInResources)
            throws IOException, ApkFormatException, NoSuchAlgorithmException {
        return verify(apkFilenameInResources, null, null);
    }

    private ApkVerifier.Result verifyForMinSdkVersion(
            String apkFilenameInResources, int minSdkVersion)
            throws IOException, ApkFormatException, NoSuchAlgorithmException {
        return verify(apkFilenameInResources, minSdkVersion, null);
    }

    private ApkVerifier.Result verifyForMaxSdkVersion(
            String apkFilenameInResources, int maxSdkVersion)
            throws IOException, ApkFormatException, NoSuchAlgorithmException {
        return verify(apkFilenameInResources, null, maxSdkVersion);
    }

    private ApkVerifier.Result verify(
            String apkFilenameInResources,
            Integer minSdkVersionOverride,
            Integer maxSdkVersionOverride)
            throws IOException, ApkFormatException, NoSuchAlgorithmException {
        byte[] apkBytes = Resources.toByteArray(getClass(), apkFilenameInResources);

        ApkVerifier.Builder builder =
                new ApkVerifier.Builder(DataSources.asDataSource(ByteBuffer.wrap(apkBytes)));
        if (minSdkVersionOverride != null) {
            builder.setMinCheckedPlatformVersion(minSdkVersionOverride);
        }
        if (maxSdkVersionOverride != null) {
            builder.setMaxCheckedPlatformVersion(maxSdkVersionOverride);
        }
        return builder.build().verify();
    }

    private ApkVerifier.Result verifySourceStamp(String apkFilenameInResources) throws Exception {
        return verifySourceStamp(apkFilenameInResources, null, null, null);
    }

    private ApkVerifier.Result verifySourceStamp(String apkFilenameInResources,
            String expectedCertDigest) throws Exception {
        return verifySourceStamp(apkFilenameInResources, expectedCertDigest, null, null);
    }

    private ApkVerifier.Result verifySourceStamp(String apkFilenameInResources,
            Integer minSdkVersionOverride, Integer maxSdkVersionOverride) throws Exception {
        return verifySourceStamp(apkFilenameInResources, null, minSdkVersionOverride,
                maxSdkVersionOverride);
    }

    private ApkVerifier.Result verifySourceStamp(String apkFilenameInResources,
            String expectedCertDigest, Integer minSdkVersionOverride, Integer maxSdkVersionOverride)
            throws Exception {
        byte[] apkBytes = Resources.toByteArray(getClass(), apkFilenameInResources);
        ApkVerifier.Builder builder = new ApkVerifier.Builder(
                DataSources.asDataSource(ByteBuffer.wrap(apkBytes)));
        if (minSdkVersionOverride != null) {
            builder.setMinCheckedPlatformVersion(minSdkVersionOverride);
        }
        if (maxSdkVersionOverride != null) {
            builder.setMaxCheckedPlatformVersion(maxSdkVersionOverride);
        }
        return builder.build().verifySourceStamp(expectedCertDigest);
    }

    static void assertVerified(ApkVerifier.Result result) {
        assertVerified(result, "APK");
    }

    static void assertVerified(ApkVerifier.Result result, String apkId) {
        if (result.isVerified()) {
            return;
        }

        StringBuilder msg = new StringBuilder();
        for (IssueWithParams issue : result.getErrors()) {
            if (msg.length() > 0) {
                msg.append('\n');
            }
            msg.append(issue);
        }
        for (ApkVerifier.Result.V1SchemeSignerInfo signer : result.getV1SchemeSigners()) {
            String signerName = signer.getName();
            for (IssueWithParams issue : signer.getErrors()) {
                if (msg.length() > 0) {
                    msg.append('\n');
                }
                msg.append("JAR signer ")
                        .append(signerName)
                        .append(": ")
                        .append(issue.getIssue())
                        .append(": ")
                        .append(issue);
            }
        }
        for (ApkVerifier.Result.V2SchemeSignerInfo signer : result.getV2SchemeSigners()) {
            String signerName = "signer #" + (signer.getIndex() + 1);
            for (IssueWithParams issue : signer.getErrors()) {
                if (msg.length() > 0) {
                    msg.append('\n');
                }
                msg.append("APK Signature Scheme v2 signer ")
                        .append(signerName)
                        .append(": ")
                        .append(issue.getIssue())
                        .append(": ")
                        .append(issue);
            }
        }
        for (ApkVerifier.Result.V3SchemeSignerInfo signer : result.getV3SchemeSigners()) {
            String signerName = "signer #" + (signer.getIndex() + 1);
            for (IssueWithParams issue : signer.getErrors()) {
                if (msg.length() > 0) {
                    msg.append('\n');
                }
                msg.append("APK Signature Scheme v3 signer ")
                        .append(signerName)
                        .append(": ")
                        .append(issue.getIssue())
                        .append(": ")
                        .append(issue);
            }
        }

        fail(apkId + " did not verify: " + msg);
    }

    private void assertVerified(
            String apkFilenameInResources,
            Integer minSdkVersionOverride,
            Integer maxSdkVersionOverride)
            throws Exception {
        assertVerified(
                verify(apkFilenameInResources, minSdkVersionOverride, maxSdkVersionOverride),
                apkFilenameInResources);
    }

    static void assertVerificationFailure(ApkVerifier.Result result, Issue expectedIssue) {
        assertVerificationIssue(result, expectedIssue, true);
    }

    static void assertVerificationWarning(ApkVerifier.Result result, Issue expectedIssue) {
        assertVerificationIssue(result, expectedIssue, false);
    }

    /**
     * Asserts the provided {@code result} contains the {@code expectedIssue}; if {@code
     * verifyError} is set to {@code true} then the specified {@link Issue} will be expected as an
     * error, otherwise it will be expected as a warning.
     */
    private static void assertVerificationIssue(ApkVerifier.Result result, Issue expectedIssue,
        boolean verifyError) {
        if (result.isVerified() && verifyError) {
            fail("APK verification succeeded instead of failing with " + expectedIssue);
            return;
        }

        StringBuilder msg = new StringBuilder();
        for (IssueWithParams issue : (verifyError ? result.getErrors() : result.getWarnings())) {
            if (expectedIssue.equals(issue.getIssue())) {
                return;
            }
            if (msg.length() > 0) {
                msg.append('\n');
            }
            msg.append(issue);
        }
        for (ApkVerifier.Result.V1SchemeSignerInfo signer : result.getV1SchemeSigners()) {
            String signerName = signer.getName();
            for (ApkVerifier.IssueWithParams issue : (verifyError ? signer.getErrors()
                    : signer.getWarnings())) {
                if (expectedIssue.equals(issue.getIssue())) {
                    return;
                }
                if (msg.length() > 0) {
                    msg.append('\n');
                }
                msg.append("JAR signer ")
                        .append(signerName)
                        .append(": ")
                        .append(issue.getIssue())
                        .append(" ")
                        .append(issue);
            }
        }
        for (ApkVerifier.Result.V2SchemeSignerInfo signer : result.getV2SchemeSigners()) {
            String signerName = "signer #" + (signer.getIndex() + 1);
            for (IssueWithParams issue : (verifyError ? signer.getErrors()
                    : signer.getWarnings())) {
                if (expectedIssue.equals(issue.getIssue())) {
                    return;
                }
                if (msg.length() > 0) {
                    msg.append('\n');
                }
                msg.append("APK Signature Scheme v2 signer ")
                        .append(signerName)
                        .append(": ")
                        .append(issue);
            }
        }
        for (ApkVerifier.Result.V3SchemeSignerInfo signer : result.getV3SchemeSigners()) {
            String signerName = "signer #" + (signer.getIndex() + 1);
            for (IssueWithParams issue : (verifyError ? signer.getErrors()
                    : signer.getWarnings())) {
                if (expectedIssue.equals(issue.getIssue())) {
                    return;
                }
                if (msg.length() > 0) {
                    msg.append('\n');
                }
                msg.append("APK Signature Scheme v3 signer ")
                        .append(signerName)
                        .append(": ")
                        .append(issue);
            }
        }
        for (ApkVerifier.Result.V3SchemeSignerInfo signer : result.getV31SchemeSigners()) {
            String signerName = "signer #" + (signer.getIndex() + 1);
            for (IssueWithParams issue : (verifyError ? signer.getErrors()
                : signer.getWarnings())) {
                if (expectedIssue.equals(issue.getIssue())) {
                    return;
                }
                if (msg.length() > 0) {
                    msg.append('\n');
                }
                msg.append("APK Signature Scheme v3.1 signer ")
                    .append(signerName)
                    .append(": ")
                    .append(issue);
            }
        }

        fail(
                "APK failed verification for the wrong reason"
                        + ". Expected: "
                        + expectedIssue
                        + ", actual: "
                        + msg);
    }

    private static void assertSourceStampVerificationFailure(
            ApkVerifier.Result result, Issue expectedIssue) {
        if (result.isSourceStampVerified()) {
            fail(
                    "APK source stamp verification succeeded instead of failing with "
                            + expectedIssue);
            return;
        }

        StringBuilder msg = new StringBuilder();
        List<IssueWithParams> resultIssueWithParams =
                Stream.of(result.getErrors(), result.getWarnings())
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
        for (IssueWithParams issue : resultIssueWithParams) {
            if (expectedIssue.equals(issue.getIssue())) {
                return;
            }
            if (msg.length() > 0) {
                msg.append('\n');
            }
            msg.append(issue);
        }

        ApkVerifier.Result.SourceStampInfo signer = result.getSourceStampInfo();
        if (signer != null) {
            List<IssueWithParams> sourceStampIssueWithParams =
                    Stream.of(signer.getErrors(), signer.getWarnings())
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
            for (IssueWithParams issue : sourceStampIssueWithParams) {
                if (expectedIssue.equals(issue.getIssue())) {
                    return;
                }
                if (msg.length() > 0) {
                    msg.append('\n');
                }
                msg.append("APK SourceStamp signer").append(": ").append(issue);
            }
        }

        fail(
                "APK source stamp failed verification for the wrong reason"
                        + ". Expected: "
                        + expectedIssue
                        + ", actual: "
                        + msg);
    }

    private static void assertSourceStampVerificationStatus(ApkVerifier.Result result,
            SourceStampVerificationStatus verificationStatus) throws Exception {
        assertEquals(verificationStatus,
                result.getSourceStampInfo().getSourceStampVerificationStatus());
    }

    private void assertVerificationFailure(
            String apkFilenameInResources, ApkVerifier.Issue expectedIssue) throws Exception {
        assertVerificationFailure(verify(apkFilenameInResources), expectedIssue);
    }

    private void assertVerifiedForEach(String apkFilenamePatternInResources, String[] args)
            throws Exception {
        assertVerifiedForEach(apkFilenamePatternInResources, args, null, null);
    }

    private void assertVerifiedForEach(
            String apkFilenamePatternInResources,
            String[] args,
            Integer minSdkVersionOverride,
            Integer maxSdkVersionOverride)
            throws Exception {
        for (String arg : args) {
            String apkFilenameInResources =
                    String.format(Locale.US, apkFilenamePatternInResources, arg);
            assertVerified(apkFilenameInResources, minSdkVersionOverride, maxSdkVersionOverride);
        }
    }

    private void assertVerifiedForEachForMinSdkVersion(
            String apkFilenameInResources, String[] args, int minSdkVersion) throws Exception {
        assertVerifiedForEach(apkFilenameInResources, args, minSdkVersion, null);
    }

    private static byte[] sha256(byte[] msg) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(msg);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create SHA-256 MessageDigest", e);
        }
    }

    private static void assumeThatRsaPssAvailable() {
        Assume.assumeTrue(Security.getProviders("Signature.SHA256withRSA/PSS") != null);
    }
}
