package cz.spurny.Dialogs;

/**
 * Objekt: GameTerminate.java
 * Popis:  Dialog upozornujici uzovatele na to, ze opousti hru.
 * Autor:  Frantisek Spurny
 * Datum:  08.07.2015
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.Settings.UserPreferences;
import cz.spurny.Toasts.GameRecordedSuccessfully;

public class GameTerminate {
    public static Dialog dialog(final Context context,final Game game) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.GameTerminate_string_dialogMessage))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (!UserPreferences.getSaveDialogShow(context)) {
                                    SaveGame.dialog(context, game).show();
                                    dialog.cancel();
                                } else {
                                    /* Prechod do hlavniho menu */
                                    Intent iMainMenu = new Intent(context,MainMenu.class);
                                    context.startActivity(iMainMenu);
                                }
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
