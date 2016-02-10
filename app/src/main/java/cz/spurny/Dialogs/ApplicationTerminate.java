package cz.spurny.Dialogs;

/**
 * Objekt: AplicationTerminate.java
 * Popis:  Dialog, ktery se uzivatele dotaze zda-li chce doopravdy ukoncit aplikaci.
 * Autor:  Frantisek Spurny
 * Datum:  13.06.2015
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;

public class ApplicationTerminate {

    public static Dialog dialog(final Context context) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.ApplicationTerminate_string_dialogMessage))
               .setCancelable(false)

               /* Pozitivni volba */
               .setPositiveButton(context.getString(R.string.ok),
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           ((Activity) context).finishAffinity();
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
