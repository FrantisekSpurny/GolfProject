package cz.spurny.Dialogs;

/**
 * Objekt: HoleScorePlayerList.java
 * Popis:  Dialog zobrazujici seznam hracu.
 * Autor:  Frantisek Spurny
 * Datum:  22.07.2015
 */

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Game.HoleScore;

public class HoleScorePlayerList {

    public static Dialog dialog(final Context context,List<Player> players) {

        /* Tvorba pole retercu pro adapter */
        String[] playersArray = new String[players.size()];
        for (int i = 0; i < players.size(); i++)
            playersArray[i] =  ((HoleScore)context).formatStringPlayer(players.get(i));

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

                ((HoleScore)context).setActualPlayer(players.get(arg2));
                ((HoleScore)context).initTextView();

                dialog.hide();
            }
        });

    }

}
