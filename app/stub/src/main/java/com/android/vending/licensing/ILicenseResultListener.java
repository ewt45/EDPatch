package com.android.vending.licensing;

public interface ILicenseResultListener extends android.os.IInterface{
    void verifyLicense(int responseCode, String signedData, String signature) throws android.os.RemoteException;
    public static abstract class Stub extends android.os.Binder implements ILicenseResultListener{
        public Stub() {
            throw new RuntimeException();
        }
        public static ILicenseResultListener asInterface(android.os.IBinder obj){
            throw new RuntimeException();
        }
        @Override public android.os.IBinder asBinder() {
            throw new RuntimeException();
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException{
            throw new RuntimeException();
        }
    }
}
