package cz.spurny.Dialogs;

/**
 * Objekt: RealoadInternalDatabase.java
 * Popis:  Dialog dotazujici se uzivatele zdali opravdu chce vymazat interni databazi.
 * Autor:  Frantisek Spurny
 * Datum:  13.08.2015
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.Toasts.InternalDatabaseRemoved;

public class ReloadInternalDatabase {

    public static Dialog dialog(final Context context) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.RealoadInternalDatabase_string_title))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeInternalDatabase(context);
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

    /** Mazani interni databaze **/
    public static void removeInternalDatabase(Context context) {

        /* Zjisteni jmena databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
        String dbName = dbi.getDatabaseName();
        dbi.close();

        /* Vymazani databaze */
        context.deleteDatabase(dbName);

		/* Informace o uspesnem vymazani */
        InternalDatabaseRemoved.getToast(context).show();

		/* restart aplikace */
        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);

    }

}
