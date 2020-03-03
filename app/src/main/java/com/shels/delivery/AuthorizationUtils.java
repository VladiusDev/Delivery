package com.shels.delivery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.shels.delivery.Activity.LoginActivity;
import com.shels.delivery.DataBaseUtils.ViewModel.BarcodeViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.DataBaseUtils.ViewModel.ProductViewModel;

public class AuthorizationUtils {

    public static void clearAuthorization(Context context, Activity activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        DeliveryActsViewModel deliveryActsViewModel = ViewModelProviders.of((FragmentActivity) activity).get(DeliveryActsViewModel.class);
        ProductViewModel productViewModel = ViewModelProviders.of((FragmentActivity) activity).get(ProductViewModel.class);
        BarcodeViewModel barcodeViewModel = ViewModelProviders.of((FragmentActivity) activity).get(BarcodeViewModel.class);

        // Сбросим все настройки пользователя и очистим БД
        deliveryActsViewModel.deleteAllDeliveryActs();
        productViewModel.deleteAllProducts();
        barcodeViewModel.deleteAllBarcodes();

        if (preferences.contains(Constants.PREFERENCE_USER)){
            preferences.edit().putString(Constants.PREFERENCE_USER, "").apply();
        }
        if (preferences.contains(Constants.PREFERENCE_USER_PASSWORD)){
            preferences.edit().putString(Constants.PREFERENCE_USER_PASSWORD, "").apply();
        }
        if (preferences.contains(Constants.PREFERENCE_USER_PROFILE)){
            preferences.edit().putString(Constants.PREFERENCE_USER_PROFILE, "").apply();
        }
        if (preferences.contains(Constants.PREFERENCE_USER_SAVE_PASSWORD)){
            preferences.edit().putBoolean(Constants.PREFERENCE_USER_SAVE_PASSWORD, false).apply();
        }
        if (preferences.contains(Constants.PREFERENCE_USER_ID)){
            preferences.edit().putString(Constants.PREFERENCE_USER_ID, "").apply();
        }
        if (preferences.contains(Constants.PREFERENCE_USER_AUTH_SUCCESS)){
            preferences.edit().putBoolean(Constants.PREFERENCE_USER_AUTH_SUCCESS, false).apply();
        }

        // Откроем окно авторизации
        activity.finish();

        Intent intent = new Intent(context, LoginActivity.class);
        activity.startActivity(intent);
    }

}
