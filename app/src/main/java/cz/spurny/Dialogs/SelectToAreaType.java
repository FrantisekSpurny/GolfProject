package cz.spurny.Dialogs;

/**
 * Objekt: SelectToAreaType.java
 * Popis:  Dialog slouzici k volbe typu plochy bodu dopadu.
 * Autor:  Frantisek Spurny
 * Datum:  02.08.2015
 */

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Shot;
import cz.spurny.Game.AreaType;
import cz.spurny.Game.BallPosition;
import cz.spurny.Game.GameOnHole;

public class SelectToAreaType {
    public static Dialog dialog(final Context context,Shot shot) {

        /* Tvorba pole retercu pro adapter */
        String[] areaTypeArray = new String[9];

        for (int i = 0; i < 9; i++) {
            areaTypeArray[i] = AreaType.getString(i, context);
        }

        /* Tvorba dialogu */
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.point_selection_hole_points_layout);

        ListView lvSpecification = (ListView) dialog.findViewById(R.id.PointSelectionHolePoints_listView_pointList);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(R.string.SelectToAreaType_string_title));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,areaTypeArray);
        lvSpecification.setAdapter(adapter);

        onListClickHandle(lvSpecification,dialog,context,shot);

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

                shot.setToAreaType(arg2);
                shot.setBallPosition(changeBallPosition(shot));

                ((GameOnHole)context).infoPanelCaptureShot();
                dialog.hide();
            }
        });

    }

    /** Zmena polohy dopadu na zaklade typu plochy **/
    public static int changeBallPosition(Shot shot) {

        switch (shot.getToAreaType()) {

            case AreaType.FAIRWAY:
            case AreaType.SEMIROUGH:
            case AreaType.ROUGHT:
            case AreaType.BUNKER:
            case AreaType.GREEN:
                return BallPosition.OK;
            case AreaType.BIOZONE:
            case AreaType.WATER:
            case AreaType.OUT:
                return BallPosition.DROP_PENALTY;
        }

        return BallPosition.OK;
    }

}
