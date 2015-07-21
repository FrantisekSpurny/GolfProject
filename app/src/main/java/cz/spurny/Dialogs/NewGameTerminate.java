package cz.spurny.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;

/**
 * Objekt: NewGameTerminate.java
 * Popis:  Upozorneni, ktere se uzivatele pta zda-li opravdu chce ukoncit vytvareni nove hry
 * Autor:  Frantisek Spurny
 * Datum:  16.06.2015
 */
public class NewGameTerminate {
    public static Dialog dialog(final Context context) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.NewGameTerminate_string_dialogMessage))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                /* Ukonceni aktualni aktivity a navrat do hlavniho menu */
                                Intent iMainMenu = new Intent(context,MainMenu.class);
                                ((Activity) context).finish();
                                context.startActivity(iMainMenu);
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
