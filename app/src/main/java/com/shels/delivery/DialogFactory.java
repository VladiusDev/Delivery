package com.shels.delivery;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class DialogFactory {

    public static void showAlertDialog(Context context, String text){
        showDialog(context, text, null);
    }

    public static void showAlertDialog(Context context, String text, String title){
        showDialog(context, text, title);
    }

    private static void showDialog(Context context, String text, String title){
        String mTitle = "";
        if (title != null) {
            mTitle = title;
        }else{
            mTitle = context.getString(R.string.error);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(mTitle);
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.alertDialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
