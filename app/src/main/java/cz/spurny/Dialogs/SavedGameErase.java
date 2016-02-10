package cz.spurny.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Club;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.SavedGame;
import cz.spurny.LoadGame.SelectSavedGameAdapter;
import cz.spurny.Player.EditBagAdapter;
import cz.spurny.Toasts.ClubRemovedSuccessfully;
import cz.spurny.Toasts.SavedGameErasedSuccessfully;

/**
 * Objekt: SavedGameErase.java
 * Popis:  Dialog umoznujici vymazat ulozenou hru.
 * Autor:  Frantisek Spurny
 * Datum:  23.08.2015
 */
public class SavedGameErase {

    public static Dialog dialog(final Context context,
                                final SavedGame savedGame,
                                final List<Game> gamesList,
                                final SelectSavedGameAdapter adapter,
                                final int gameId) {

        /* Tvorba dialogu */
        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        /* Tvorba titulku dialogu */
        builder.setMessage(context.getString(R.string.SavedGameErase_string_title))
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                /* Odstranni ulozene hry z databaze */
                                DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
                                dbi.deleteSavedGame(savedGame.getId());
                                dbi.close();

                                /* Aktualizace "ListView" */
                                gamesList.remove(gameId);
                                adapter.notifyDataSetChanged();

                                /* Informovani o uspesnem odstraneni */
                                SavedGameErasedSuccessfully.getToast(context).show();

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
