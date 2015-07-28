package cz.spurny.Dialogs;

/**
 * Objekt: ScoreNotSaved.java
 * Popis:  Dialog dotazujici se uzivatele na ulozeni skore.
 * Autor:  Frantisek Spurny
 * Datum:  27.07.2015
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Score;
import cz.spurny.Toasts.ScoreSaved;
import cz.spurny.Toasts.ScoreUpdated;

public class ScoreNotSaved {

    public static Dialog dialog(final Context context,final Score score,final DatabaseHandlerInternal dbi) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.ScoreNotSaved_string_title))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ScoreNotSaved_string_save),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbi.createScore(score);
                                ScoreSaved.getToast(context).show();
                                ((Activity)context).finish();
                            }
                        })

               /* Negativni volba */
                .setNegativeButton(context.getString(R.string.ScoreNotSaved_string_noSave),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               /* zavreni dialogu */
                                dialog.cancel();
                                ((Activity)context).finish();
                            }
                        });

        /* Tvorba alert dialogu */
        AlertDialog alert = builder.create();
        dialog = alert;

        return dialog;
    }
}
