package cz.spurny.Dialogs;

/**
 * Objekt: HoleScoreHoleList.java
 * Popis:  Zobrazeni seznamu jamek v dialogu.
 * Autor:  Frantisek Spurny
 * Datum:  21.07.2015
 */

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Game.HoleScore;

public class HoleScoreHoleList {

    public static Dialog dialog(final Context context,List<Hole> holes) {

        /* Tvorba pole retercu pro adapter */
        String[] holesArray = new String[holes.size()];
        for (int i = 0; i < holes.size(); i++)
            holesArray[i] =  ((HoleScore)context).formatStringHole(holes.get(i));

        /* Tvorba dialogu */
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.point_selection_hole_points_layout);

        ListView lvHoles = (ListView) dialog.findViewById(R.id.PointSelectionHolePoints_listView_pointList);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(R.string.HoleScoreHoleList_string_title));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,holesArray);
        lvHoles.setAdapter(adapter);

        onListClickHandle(lvHoles,dialog,context,holes);

        return dialog;
    }

    /** Reakce na pokliknuti na polozku seznamu **/
    public static void onListClickHandle(final ListView lv,
                                         final Dialog dialog,
                                         final Context context,
                                         final List<Hole> holes) {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int arg2, long arg3) {

                /* zmena jamky */
                ((HoleScore)context).setActualHole(holes.get(arg2));
                ((HoleScore)context).initTextView();

                dialog.hide();
            }
        });

    }
}
