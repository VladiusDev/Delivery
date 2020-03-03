package com.shels.delivery.BarcodeScanner;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;

public class BarcodeScannerUtils {

    public static void scanBarcode(Activity activity){
        scan(activity, null);
    }

    public static void scanBarcode(Activity activity, Fragment fragment){
        scan(activity, fragment);
    }

    public static void scan(Activity activity, Fragment fragment){
        IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
        intentIntegrator.setCaptureActivity(Portrait.class);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);

        if (fragment == null) {
            intentIntegrator.initiateScan();
        }else{
            intentIntegrator.forSupportFragment(fragment).setCaptureActivity(Portrait.class).initiateScan();
        }
    }
}
