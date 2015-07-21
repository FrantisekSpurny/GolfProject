package cz.spurny.Dialogs;

/**
 * Objekt: RemoveClub.java
 * Popis:  Dialog dotazujici se uzivatele zdali doopravdy chce odstranit hul.
 * Autor:  Frantisek Spurny
 * Datum:  25.06.2015
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Club;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.Player.EditBagAdapter;
import cz.spurny.Toasts.ClubRemovedSuccessfully;

public class RemoveClub {

    /* Adapter */
    static EditBagAdapter adapter;

    public static Dialog dialog(final DatabaseHandlerInternal dbi,
                                final List<Club> clubs,
                                final int clubId,
                                final Context context,
                                final EditBagAdapter activityAdapter) {

        /* Prirazeni adapteru */
        adapter = activityAdapter;

        /* Tvorba dialogu */
        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        /* Tvorba titulku dialogu */
        builder.setMessage(context.getString(R.string.RemoveClub_string_title)
                           + " "
                           + clubs.get(clubId).getName()
                           + " ("
                           + clubs.get(clubId).getModel()
                           + ") ?")

                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                /* Odstraneni hole z databaze */
                                dbi.removeClub(clubs.get(clubId).getId());

                                /* Zobrazeni zpravy o uspesnem dokonceni */
                                ClubRemovedSuccessfully.getToast(context).show();

                                /* Aktualizace "ListView" */
                                clubs.remove(clubId);
                                adapter.notifyDataSetChanged();
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
