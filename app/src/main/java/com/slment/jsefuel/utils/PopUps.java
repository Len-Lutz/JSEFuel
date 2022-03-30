package com.slment.jsefuel.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class PopUps {

    public static int okAlert(Context context, String title, String message, String btnText ) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle( title )
                .setMessage(message)
                .setPositiveButton(btnText, null)
                .show();
        return 0;
    }

}
