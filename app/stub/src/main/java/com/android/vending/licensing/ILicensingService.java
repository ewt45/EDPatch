package com.android.vending.licensing;

public interface ILicensingService  extends android.os.IInterface{
    void checkLicense(long nonce, String packageName,  ILicenseResultListener listener) throws android.os.RemoteException;

    public static abstract class Stub extends android.os.Binder implements ILicensingService{
        public Stub() {
            throw new RuntimeException();
        }
        public static ILicensingService asInterface(android.os.IBinder obj){
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
