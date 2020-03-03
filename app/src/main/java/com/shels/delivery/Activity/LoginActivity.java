package com.shels.delivery.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.shels.delivery.Constants;
import com.shels.delivery.DialogFactory;
import com.shels.delivery.JsonUtils.JsonParser1C;
import com.shels.delivery.R;
import com.shels.delivery.WebService.WebService1C;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

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
        context = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        savePassword = findViewById(R.id.save_password);
        btn_auth = findViewById(R.id.btn_auth);
        edt_login = findViewById(R.id.edt_login);
        edt_password = findViewById(R.id.edt_password);

        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString(Constants.PREFERENCE_USER, edt_login.getText().toString()).apply();
                preferences.edit().putString(Constants.PREFERENCE_USER_PASSWORD, edt_password.getText().toString()).apply();
                preferences.edit().putBoolean(Constants.PREFERENCE_USER_SAVE_PASSWORD, savePassword.isChecked()).apply();

                new AuthorizationTask().execute();
            }
        });

        //Проверим сохранены ли данные авторизации и была ли выполнена успешная авторизация
        if (preferences.contains(Constants.PREFERENCE_USER_SAVE_PASSWORD) && preferences.contains(Constants.PREFERENCE_USER_AUTH_SUCCESS)){
            if (preferences.getBoolean(Constants.PREFERENCE_USER_SAVE_PASSWORD, false) == true
                    && preferences.getBoolean(Constants.PREFERENCE_USER_AUTH_SUCCESS, false) == true){
                finish();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }

        // Отобразим сохраненное имя пользователя
        if (preferences.contains(Constants.PREFERENCE_USER)){
            edt_login.setText(preferences.getString(Constants.PREFERENCE_USER, ""));
        }
    }

    private class AuthorizationTask extends AsyncTask<Void, Void, ContentValues>{
        @Override
        protected ContentValues doInBackground(Void... voids) {
             return WebService1C.sendRequest(Constants.SOAP_METHOD_GET_USER_INFO, getApplicationContext());
        }

        @Override
        protected void onPreExecute() {
            btn_auth.startAnimation();
        }

        @Override
        protected void onPostExecute(ContentValues contentValues) {
           if ((Boolean) contentValues.get("authorization") == true) {
                // Установим картинку на кнопку авторизации при успешной авторизации
                Bitmap doneBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_done_white_48dp);

                btn_auth.doneLoadingAnimation(R.color.mainColor, doneBitmap);

                // Получим данные о пользователи с результата JSON
                ContentValues userInfo = JsonParser1C.getUserInfo(contentValues.get("result").toString());

                // Сохраним данные пользователя
                preferences.edit().putBoolean(Constants.PREFERENCE_USER_AUTH_SUCCESS, true).apply();
                preferences.edit().putString(Constants.PREFERENCE_USER_PROFILE, userInfo.get(Constants.JSON_FIELD_USER_PROFILE).toString()).apply();
                preferences.edit().putString(Constants.PREFERENCE_USER_ID, userInfo.get(Constants.JSON_FIELD_USER_ID).toString()).apply();

                // Открываем основное приложение
               finish();

               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               startActivity(intent);
           }else{
               btn_auth.revertAnimation();

               DialogFactory.showAlertDialog(context, contentValues.get("message").toString(), context.getString(R.string.check_error_authorization));
           }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        btn_auth.dispose();
    }

}
