package cz.spurny.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Score;
import cz.spurny.Toasts.ScoreUpdated;

/**
 * Objekt: UpdateScore.java
 * Popis:  Dialog dotazujici se uzivatele jestli chce aktualizovat skore.
 * Autor:  Frantisek Spurny
 * Datum:  25.07.2015
 */
public class UpdateScore {

    public static Dialog dialog(final Context context,final Score score,final DatabaseHandlerInternal dbi) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.UpdateScore_string_title))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbi.updateScore(score);
                                ScoreUpdated.getToast(context).show();
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
