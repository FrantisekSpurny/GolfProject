package cz.spurny.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import cz.spurny.Calculations.ScoreCardCounting;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.Game.CurrentScore;
import cz.spurny.Game.HoleScore;

/**
 * Objekt: CurrentScorePlayerList.java
 * Popis:  Zobrazeni seznamu hracu pri zobrazeni aktualniho skore.
 * Autor:  Frantisek Spurny
 * Datum:  16.08.2015
 */
public class CurrentScorePlayerList {

    public static Dialog dialog(final Context context,
                                List<Player> players,
                                final Game game,
                                final DatabaseHandlerInternal dbi,
                                final DatabaseHandlerResort dbr) {

        /* Tvorba pole retercu pro adapter */
        String[] playersArray = new String[players.size()];
        for (int i = 0; i < players.size(); i++)
            playersArray[i] =  ((CurrentScore)context).formatStringPlayer(players.get(i));

        /* Tvorba dialogu */
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.point_selection_hole_points_layout);

        ListView lvPlayers = (ListView) dialog.findViewById(R.id.PointSelectionHolePoints_listView_pointList);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(R.string.HoleScorePlayerList_string_title));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,playersArray);
        lvPlayers.setAdapter(adapter);

        onListClickHandle(lvPlayers,dialog,context,game,dbi,dbr,players);

        return dialog;
    }

    /** Reakce na pokliknuti na polozku seznamu **/
    public static void onListClickHandle(final ListView lv,
                                         final Dialog dialog,
                                         final Context context,
                                         final Game game,
                                         final DatabaseHandlerInternal dbi,
                                         final DatabaseHandlerResort dbr,
                                         final List<Player> players) {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int arg2, long arg3) {

                /** Nastaveni aktualne zvoleneho hrace **/
                ((CurrentScore)context).setPlayer(players.get(arg2));

                /** Vypocet noveho score **/
                ((CurrentScore)context)
                        .displayScore(ScoreCardCounting.countScorecard(game, players.get(arg2), dbi, dbr,context));

                dialog.hide();
            }
        });

    }

}
