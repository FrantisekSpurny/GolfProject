package cz.spurny.Dialogs;

/**
 * Objekt: MeasureDrawHolePointsList.java
 * Popis:
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
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.DatabaseResort.Point;
import cz.spurny.DatabaseResort.View;
import cz.spurny.Game.GameOnHole;
import cz.spurny.Game.MeasureDraw;
import cz.spurny.Toasts.NotValidPlaymate;

public class MeasureDrawHolePointsList {

    public static Dialog dialog(final Context context,
                                final MeasureDraw measureDraw,
                                final boolean from,
                                final View view) {

        /* Pripojeni databaze */
        DatabaseHandlerResort dbr = new DatabaseHandlerResort(context);
        List<Point> pointsList = dbr.getAllPointsOfView(view.getId());

        /* Tvorba pole retercu pro adapter */
        String[] pointsArray = new String[pointsList.size()];
        for (int i = 0; i < pointsList.size(); i++)
            pointsArray[i] = pointsList.get(i).getType() + "(" + pointsList.get(i).getName() + ")";

        /* Tvorba dialogu */
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.point_selection_hole_points_layout);

        ListView lvPoints = (ListView) dialog.findViewById(R.id.PointSelectionHolePoints_listView_pointList);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(R.string.PointSelectionHolePoints_string_title));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,pointsArray);
        lvPoints.setAdapter(adapter);

        onListClickHandle(lvPoints, measureDraw, from, pointsList, dialog,context);

        /* Odpojeni databaze */
        dbr.close();

        return dialog;
    }

    /** Reakce na pokliknuti na polozku seznamu **/
    public static void onListClickHandle(final ListView lv,
                                         final MeasureDraw measureDraw,
                                         final boolean from,
                                         final List<Point> points,
                                         final Dialog dialog,
                                         final Context context) {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int arg2, long arg3) {

                /* zadani zdrojoveho/ciloveho bodu */
                if (from)
                    measureDraw.setActualPoint     (points.get(arg2));
                else
                    measureDraw.setDestinationPoint(points.get(arg2));

                if (measureDraw.getDestinationPoint() != null)
                    measureDraw.drawMeasure();
                else
                    measureDraw.drawPoints();

                /* Aktualizace info panelu */
                ((GameOnHole)context).infoPanelMeasure();

                dialog.hide();
            }
        });

    }
}