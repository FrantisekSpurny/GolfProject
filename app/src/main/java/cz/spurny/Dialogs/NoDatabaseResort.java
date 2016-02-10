package cz.spurny.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import cz.spurny.CreateGame.R;
import cz.spurny.Settings.Settings;

/**
 * Objekt: NoDabaseResort.java
 * Popis:  Dialog informujici uzivatele o tom ze neexistuje zadna databaze resortu.
 * Autor:  Frantisek Spurny
 * Datum:  16.08.2015
 */
public class NoDatabaseResort {

    public static Dialog dialog(final Context context) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.NoDabaseResort_string_title))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.NoDabaseResort_string_settings),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final Intent iSettings  = new Intent(context,Settings.class);
                                context.startActivity(iSettings);
                                dialog.cancel();
                            }
                        })

               /* Negativni volba */
                .setNegativeButton(context.getString(R.string.cancel),
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
