package cz.spurny.Dialogs;

import  android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Game.RecordGame;

/**
 * Objekt: SaveGameResults.java
 * Popis:  Dialog zobrazujici pocet zahranych jamek pro jednotlive hrace hry.
 *         Umoznuje hru ulozit a uzavrit.
 * Autor:  Frantisek Spurny
 * Datum:  18.08.2015
 */

public class SaveGameResults {

    public static Dialog dialog(final Context context,
                                final Game game,
                                final DatabaseHandlerInternal dbi) {

        /* Tvorba dialogu */
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.save_game_results_layout);
        dialog.setTitle(context.getString(R.string.SaveGameResults_string_title));

        /* Naplneni seznamu hracu */
        ListView lvPlayers = (ListView) dialog.findViewById(R.id.SaveGameResult_listView_list);

        /* Pridani hlavicky seznamu */
        View vHeader = LayoutInflater.from(context).inflate(R.layout.save_game_results_list_header, null);
        lvPlayers.addHeaderView(vHeader);

        /* Ziskani vsech hracu dane hry */
        final List<Player> players = dbi.getAllPlaymatesOfGame(game.getId());

        /* Ziskani seznamu poctu zahranych jamek pro jednotlive hrace */
        final List<Integer> score = calculatePlayedHoles(players,game,dbi);

        /* Tvorba adapteru */
        SaveGameResultsAdapter adapter = new SaveGameResultsAdapter(context,players,score);

		/* Prirazeni adapteru */
        lvPlayers.setAdapter(adapter);

        /** Reakce na stisknuti tlacitka **/
        Button bSaveGame = (Button) dialog.findViewById(R.id.SaveGameResults_button_saveGame);

        bSaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean scoreSet = true;

                /* kontrola zdali bylo zadano skore pro vsechny hrace na vsech jamkach */
                for (int i = 0; i < score.size(); i++) {
                    if (score.get(i) != 18) {
                        scoreSet = false;
                        ScoreNotSetForAll.dialog(context,game,score,dbi).show();
                        dialog.hide();
                        break;
                    }
                }

                /** Score bylo zaznamenano pro vsechny hrace **/
                if (scoreSet)
                    saveGame(context,game,dialog);
            }
        });

        return dialog;
    }

    /** Vyhodnoceni hry, vsichny hraci maji zaznamenany vsechny jamky **/
    public static void saveGame(Context context,Game game,Dialog dialog) {

        Intent iRecordGame = new Intent(context, RecordGame.class);
        iRecordGame.putExtra("EXTRA_RECORD_GAME_IDGAME",game.getId());
        dialog.hide();
        context.startActivity(iRecordGame);
    }

    /** Vypocet poctu zahranych jamek danym hracem **/
    public static List<Integer> calculatePlayedHoles(List<Player> players,Game game,DatabaseHandlerInternal dbi) {

        List<Integer> playedList =  new ArrayList<>();
        List<Hole> holes  = dbi.getAllHolesOfGame(game.getId());
        int played;
        
        /** Projdeme kazdeho hrace **/
        for (int i = 0; i < players.size() ; i++) {

            played = 0;

            /** Projdeme vysledek kazde jamky **/
            for (int j = 0; j < holes.size(); j++) {
                if (dbi.getScore(holes.get(j).getId(),players.get(i).getId(),game.getId()) != null)
                    played++;
            }

            playedList.add(played);
        }

        return playedList;
    }

}
