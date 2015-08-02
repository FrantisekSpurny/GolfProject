package cz.spurny.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Shot;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Game.BallPosition;
import cz.spurny.Game.GameOnHole;
import cz.spurny.Game.HoleScore;

/**
 * Objekt: SelectBallPosition.java
 * Popis:  Dialog umoznujici vyber polohy mice.
 * Autor:  Frantisek Spurny
 * Datum:  02.08.2015
 */
public class SelectBallPosition {

    public static Dialog dialog(final Context context,Shot shot) {

        /* Tvorba pole retercu pro adapter */
        String[] positionsArray = new String[5];

        for (int i = 0; i < 5; i++) {
            positionsArray[i] = BallPosition.getString(i,context);
        }

        /* Tvorba dialogu */
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.point_selection_hole_points_layout);

        ListView lbBallPositions = (ListView) dialog.findViewById(R.id.PointSelectionHolePoints_listView_pointList);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(R.string.HoleScoreHoleList_string_title));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,positionsArray);
        lbBallPositions.setAdapter(adapter);

        onListClickHandle(lbBallPositions,dialog,context,shot);

        return dialog;
    }

    /** Reakce na pokliknuti na polozku seznamu **/
    public static void onListClickHandle(final ListView lv,
                                         final Dialog dialog,
                                         final Context context,
                                         final Shot shot) {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int arg2, long arg3) {

                shot.setBallPosition(arg2);
                ((GameOnHole)context).infoPanelCaptureShot();
                dialog.hide();
            }
        });

    }

}
