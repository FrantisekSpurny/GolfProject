package cz.spurny.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import cz.spurny.CreateGame.R;

/**
 * Objekt: CreatePlayerTerminate.java
 * Popis:  Dialog dotazujici se uzivatele zdali opravdu chce ukoncit tvorbu profilu
 * Autor:  Frantisek Spurny
 * Datum:  23.06.2015
 */
public class CreatePlayerTerminate {

    public static Dialog dialog(final Context context) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.CreatePlayerTerminate_string_dialog))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.CreatePlayerTerminate_string_terminate),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((Activity) context).finish();
                            }
                        })

               /* Negativni volba */
                .setNegativeButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               /* zavreni dialogu */
                                dialog.cancel();
                            }
                        });

        /* Tvorba alert dialogu */
        AlertDialog alert = builder.create();
        dialog = alert;

        return dialog;
    }
}
