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
import cz.spurny.Game.ScoreCard;

/**
 * Objekt: ScoreCardPlayerList.java
 * Popis:  Dialog umoznujici zmenu hrace pro zobrazeni score karty.
 * Autor:  Frantisek Spurny
 * Datum:  18.08.2015
 */

public class ScoreCardPlayerList {

    public static Dialog dialog(final Context context,
                                List<Player> players) {

        /* Tvorba pole retercu pro adapter */
        String[] playersArray = new String[players.size()];
        for (int i = 0; i < players.size(); i++)
            playersArray[i] =  ((ScoreCard)context).formatStringPlayer(players.get(i));

        /* Tvorba dialogu */
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.point_selection_hole_points_layout);

        ListView lvPlayers = (ListView) dialog.findViewById(R.id.PointSelectionHolePoints_listView_pointList);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(R.string.HoleScorePlayerList_string_title));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,playersArray);
        lvPlayers.setAdapter(adapter);

        onListClickHandle(lvPlayers,dialog,context,players);

        return dialog;
    }

    /** Reakce na pokliknuti na polozku seznamu **/
    public static void onListClickHandle(final ListView lv,
                                         final Dialog dialog,
                                         final Context context,
                                         final List<Player> players) {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int arg2, long arg3) {

                /** Zobrazeni skore karty pro nove zvoleneho hrace **/
                ((ScoreCard)context).setPlayer(players.get(arg2));
                ((ScoreCard)context).displayScoreCard();

                dialog.hide();
            }
        });

    }

}
