package cz.spurny.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import java.util.List;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.SavedGame;
import cz.spurny.Game.RecordGame;
import cz.spurny.Settings.UserPreferences;
import cz.spurny.Toasts.GameRecordedSuccessfully;
import cz.spurny.Toasts.GameSavedSuccessfully;

/**
 * Objekt: ScoreNotSetForAll.java
 * Popis:  Dialog informujici uzvatele o tom jak lze postupovat v
 *         pripade ze hru nelze kompletne vyhodnotit.
 * Autor:  Frantisek Spurny
 * Datum:  18.08.2015
 */

public class ScoreNotSetForAll {

    public static Dialog dialog(final Context context,
                                final Game game,
                                final List<Integer> score,
                                final DatabaseHandlerInternal dbi) {

        /* Tvorba dialogu */
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.score_not_set_for_all);
        dialog.setTitle(context.getString(R.string.ScoreNotSetForAll_string_title));

        /* Pripojeni prvku GUI */
        Button bSaveIncomplete = (Button) dialog.findViewById(R.id.ScoreNotSetForAll_button_saveIncomplete);
        Button bSaveGame       = (Button) dialog.findViewById(R.id.ScoreNotSetForAll_button_saveGame);
        Button bCancel         = (Button) dialog.findViewById(R.id.ScoreNotSetForAll_button_cancel);

        /* Reakce na zmacknuti tlacitka */
        buttonClick(bSaveIncomplete, bSaveGame, bCancel, dialog, context, game);

        return dialog;
    }

    public static void buttonClick(Button bSaveIncomplete,
                                   Button bSaveGame,
                                   Button bCancel,
                                   final Dialog dialog,
                                   final Context context,
                                   final Game game) {

        /* Zaznamenani hry pouze pro hlavni profil */
        bSaveIncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Kontrola zdali existuje database resortu */
                saveIncoplete(context,game,dialog);
            }
        });

        /* Ulozeni hry */
        bSaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Kontrola zdali existuje database resortu */
                saveGame(context,game);
            }
        });

        /* Uzavreni dialogu */
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

    }

    /** Zaznamenani hry pouze pro hlavni profil **/
    public static void saveIncoplete(Context context,Game game,Dialog dialog) {

        Intent iRecordGame = new Intent(context, RecordGame.class);
        iRecordGame.putExtra("EXTRA_RECORD_GAME_IDGAME",game.getId());
        dialog.hide();
        context.startActivity(iRecordGame);
    }

    /** Ulozeni hry **/
    public static void saveGame(Context context,Game game) {

        /* Pridani do seznamu ulozenych her */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
        dbi.createSavedGame(new SavedGame(game.getId()));
        dbi.close();

        /* Informovani uzivatele o uspesnem ulozeni hry */
        GameSavedSuccessfully.getToast(context).show();

        /* Prechod do hlavniho menu */
        Intent iMainMenu = new Intent(context,MainMenu.class);
        context.startActivity(iMainMenu);
    }

}
