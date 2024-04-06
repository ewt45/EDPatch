package com.android.vending.billing;

import android.os.Bundle;
import android.os.IBinder;

import com.android.vending.licensing.ILicenseResultListener;

public interface IInAppBillingService {

    public static class Stub {
        public static IInAppBillingService asInterface(IBinder iBinder) {
            throw new RuntimeException();
        }
    }

    /**
     * Checks support for the requested billing API version, package and in-app type.
     * Minimum API version supported by this interface is 3.
     * @param apiVersion the billing version which the app is using
     * @param packageName the package name of the calling app
     * @param type type of the in-app item being purchased "inapp" for one-time purchases
     *        and "subs" for subscription.
     * @return RESULT_OK(0) on success, corresponding result code on failures
     */
    int isBillingSupported(int apiVersion, String packageName, String type) throws android.os.RemoteException;

    /**
     * Provides details of a list of SKUs
     * Given a list of SKUs of a valid type in the skusBundle, this returns a bundle
     * with a list JSON strings containing the productId, price, title and description.
     * This API can be called with a maximum of 20 SKUs.
     * @param apiVersion billing API version that the Third-party is using
     * @param packageName the package name of the calling app
     * @param skusBundle bundle containing a StringArrayList of SKUs with key "ITEM_ID_LIST"
     * @return Bundle containing the following key-value pairs
     *         "RESPONSE_CODE" with int value, RESULT_OK(0) if success, other response codes on
     *              failure as listed above.
     *         "DETAILS_LIST" with a StringArrayList containing purchase information
     *              in JSON format similar to:
     *              '{ "productId" : "exampleSku", "type" : "inapp", "price" : "$5.00",
     *                 "title : "Example Title", "description" : "This is an example description" }'
     */
    Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle) throws android.os.RemoteException;

    /**
     * Returns a pending intent to launch the purchase flow for an in-app item by providing a SKU,
     * the type, a unique purchase token and an optional developer payload.
     * @param apiVersion billing API version that the app is using
     * @param packageName package name of the calling app
     * @param sku the SKU of the in-app item as published in the developer console
     * @param type the type of the in-app item ("inapp" for one-time purchases
     *        and "subs" for subscription).
     * @param developerPayload optional argument to be sent back with the purchase information
     * @return Bundle containing the following key-value pairs
     *         "RESPONSE_CODE" with int value, RESULT_OK(0) if success, other response codes on
     *              failure as listed above.
     *         "BUY_INTENT" - PendingIntent to start the purchase flow
     *
     * The Pending intent should be launched with startIntentSenderForResult. When purchase flow
     * has completed, the onActivityResult() will give a resultCode of OK or CANCELED.
     * If the purchase is successful, the result data will contain the following key-value pairs
     *         "RESPONSE_CODE" with int value, RESULT_OK(0) if success, other response codes on
     *              failure as listed above.
     *         "INAPP_PURCHASE_DATA" - String in JSON format similar to
     *              '{"orderId":"12999763169054705758.1371079406387615",
     *                "packageName":"com.example.app",
     *                "productId":"exampleSku",
     *                "purchaseTime":1345678900000,
     *                "purchaseToken" : "122333444455555",
     *                "developerPayload":"example developer payload" }'
     *         "INAPP_DATA_SIGNATURE" - String containing the signature of the purchase data that
     *                                  was signed with the private key of the developer
     *                                  TODO: change this to app-specific keys.
     */
    Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
                        String developerPayload) throws android.os.RemoteException;

    /**
     * Returns the current SKUs owned by the user of the type and package name specified along with
     * purchase information and a signature of the data to be validated.
     * This will return all SKUs that have been purchased in V3 and managed items purchased using
     * V1 and V2 that have not been consumed.
     * @param apiVersion billing API version that the app is using
     * @param packageName package name of the calling app
     * @param type the type of the in-app items being requested
     *        ("inapp" for one-time purchases and "subs" for subscription).
     * @param continuationToken to be set as null for the first call, if the number of owned
     *        skus are too many, a continuationToken is returned in the response bundle.
     *        This method can be called again with the continuation token to get the next set of
     *        owned skus.
     * @return Bundle containing the following key-value pairs
     *         "RESPONSE_CODE" with int value, RESULT_OK(0) if success, other response codes on
     *              failure as listed above.
     *         "INAPP_PURCHASE_ITEM_LIST" - StringArrayList containing the list of SKUs
     *         "INAPP_PURCHASE_DATA_LIST" - StringArrayList containing the purchase information
     *         "INAPP_DATA_SIGNATURE_LIST"- StringArrayList containing the signatures
     *                                      of the purchase information
     *         "INAPP_CONTINUATION_TOKEN" - String containing a continuation token for the
     *                                      next set of in-app purchases. Only set if the
     *                                      user has more owned skus than the current list.
     */
    Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken) throws android.os.RemoteException;

    /**
     * Consume the last purchase of the given SKU. This will result in this item being removed
     * from all subsequent responses to getPurchases() and allow re-purchase of this item.
     * @param apiVersion billing API version that the app is using
     * @param packageName package name of the calling app
     * @param purchaseToken token in the purchase information JSON that identifies the purchase
     *        to be consumed
     * @return 0 if consumption succeeded. Appropriate error values for failures.
     */
    int consumePurchase(int apiVersion, String packageName, String purchaseToken) throws android.os.RemoteException;
}
