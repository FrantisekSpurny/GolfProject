package cz.spurny.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;

import ar.com.daidalos.afiledialog.FileChooserActivity;
import cz.spurny.CreateGame.R;

/**
 * Objekt: ReloadResortDatabase.java
 * Popis:  Dialog dotazujici se uzivatele zdali chce opravdu nahradit stavajici databazi.
 * Autor:  Frantisek Spurny
 * Datum:  16.08.2015
 */
public class ReloadResortDatabase {

    public static Dialog dialog(final Context context) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.ReloadResortDatabase_string_title))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent iFileChooser = new Intent(context, FileChooserActivity.class);
                                iFileChooser.putExtra(FileChooserActivity.INPUT_START_FOLDER, Environment.getExternalStorageDirectory());
                                ((Activity) context).startActivityForResult(iFileChooser, 0);
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
