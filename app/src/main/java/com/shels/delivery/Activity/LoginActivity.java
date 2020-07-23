package com.shels.delivery.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.shels.delivery.AuthorizationUtils;
import com.shels.delivery.Constants;
import com.shels.delivery.DialogFactory;
import com.shels.delivery.R;
import com.shels.delivery.Retrofit.ApiFactory;
import com.shels.delivery.Retrofit.ApiService;
import com.shels.delivery.Retrofit.UserInfo;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import retrofit2.HttpException;

public class LoginActivity extends AppCompatActivity {
    private CircularProgressButton btn_auth;
    private EditText edt_password;
    private EditText edt_login;
    private CheckBox savePassword;
    private Context context;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        context     = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        savePassword = findViewById(R.id.save_password);
        btn_auth     = findViewById(R.id.btn_auth);
        edt_login    = findViewById(R.id.edt_login);
        edt_password = findViewById(R.id.edt_password);

        btn_auth.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                Boolean hasConnection = AuthorizationUtils.hasConnection(context);

                if (hasConnection) {
                    btn_auth.startAnimation();

                    String usr = edt_login.getText().toString();
                    String pas = edt_password.getText().toString();

                    // Save login and pass
                    preferences.edit().putString(Constants.PREFERENCE_USER, usr).apply();
                    preferences.edit().putString(Constants.PREFERENCE_USER_PASSWORD, pas).apply();
                    preferences.edit().putBoolean(Constants.PREFERENCE_USER_SAVE_PASSWORD, savePassword.isChecked()).apply();

                    // Execute auth
                    ApiFactory apiFactory = ApiFactory.getInstance(getApplicationContext());
                    ApiService apiService = apiFactory.getApiService();

                    apiService.getUserInfo(Credentials.basic(usr, pas))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<UserInfo>() {
                                @Override
                                public void accept(UserInfo userInfoResult) throws Exception {
                                    // Save user info
                                    preferences.edit().putBoolean(Constants.PREFERENCE_USER_AUTH_SUCCESS, true).apply();
                                    preferences.edit().putString("name",     userInfoResult.getName()).apply();
                                    preferences.edit().putString("username", userInfoResult.getUsername()).apply();
                                    preferences.edit().putString("group",    userInfoResult.getGroup()).apply();
                                    preferences.edit().putString("id",       userInfoResult.getId()).apply();

                                    // Set picture to the button
                                    Bitmap doneBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_done_white_48dp);
                                    btn_auth.doneLoadingAnimation(R.color.mainColor, doneBitmap);

                                    // Finish login activity
                                    finish();

                                    // Open main activity
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    if (throwable instanceof HttpException) {
                                        HttpException exception = (HttpException) throwable;
                                        switch (exception.code()){
                                            case 401:
                                                btn_auth.revertAnimation();
                                                DialogFactory.showAlertDialog(context, context.getString(R.string.login_error), context.getString(R.string.check_error_authorization));
                                                break;
                                            default:
                                                DialogFactory.showAlertDialog(context, exception.getMessage(), context.getString(R.string.error));
                                                btn_auth.revertAnimation();
                                        }
                                    }
                                }
                            });
                } else {
                    DialogFactory.showAlertDialog(context, context.getString(R.string.check_hasConnection), context.getString(R.string.error));
                }
            }
        });

        // Auth check
        if (preferences.contains(Constants.PREFERENCE_USER_SAVE_PASSWORD) && preferences.contains(Constants.PREFERENCE_USER_AUTH_SUCCESS)){
            if (preferences.getBoolean(Constants.PREFERENCE_USER_SAVE_PASSWORD, false)
                    && preferences.getBoolean(Constants.PREFERENCE_USER_AUTH_SUCCESS, false)){
                finish();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }

        // Show username
        if (preferences.contains(Constants.PREFERENCE_USER)){
            edt_login.setText(preferences.getString(Constants.PREFERENCE_USER, ""));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        btn_auth.dispose();
    }

}
