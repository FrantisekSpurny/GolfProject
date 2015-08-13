package cz.spurny.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Shot;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Point;
import cz.spurny.DatabaseResort.View;
import cz.spurny.Game.GameOnHole;
import cz.spurny.Game.MeasureDraw;
import cz.spurny.Game.ShotCaptureDraw;

/**
 * Objekt: CaptureShotHolePointsList.java
 * Popis:  Zobrazeni vyznamnych bodu jamky.
 * Autor:  Frantisek Spurny
 * Datum:  02.08.2015
 */
public class CaptureShotHolePointsList {

    public static Dialog dialog(final Context context,
                                final ShotCaptureDraw shotCaptureDraw,
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

        onListClickHandle(lvPoints, shotCaptureDraw, from, pointsList, dialog,context);

        /* Odpojeni databaze */
        dbr.close();

        return dialog;
    }

    /** Reakce na pokliknuti na polozku seznamu **/
    public static void onListClickHandle(final ListView lv,
                                         final ShotCaptureDraw shotCaptureDraw,
                                         final boolean from,
                                         final List<Point> points,
                                         final Dialog dialog,
                                         final Context context) {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int arg2, long arg3) {

                /* zadani zdrojoveho/ciloveho bodu */
                if (from)
                    setPointFrom(points.get(arg2), shotCaptureDraw.getActualShot());
                else
                    setPointTo  (points.get(arg2), shotCaptureDraw.getActualShot());


                /* Prepocet vzdalenosti bodu a zmena hole na vhodnou */
                shotCaptureDraw.calculateShotDistance(shotCaptureDraw.getActualShot());
                shotCaptureDraw.determineClub        (shotCaptureDraw.getActualShot());

                /** Prekresleni **/
                ((GameOnHole)context).reinitBitmap();
                shotCaptureDraw.drawShotList();
                shotCaptureDraw.drawActualShot();

                dialog.hide();
            }
        });

    }

    /** Nastaveni bodu odpalu **/
    public static void setPointFrom(Point point,Shot shot) {
        shot.setFromX        (shot.getFromX());
        shot.setFromY        (shot.getFromY());
        shot.setFromLatitude (shot.getToLatitude());
        shot.setFromlongitude(shot.getFromlongitude());
    }

    /** Nastaveni bodu dopadu **/
    public static void setPointTo(Point point,Shot shot) {
        shot.setToX        (shot.getToX());
        shot.setToY        (shot.getToY());
        shot.setToLatitude (shot.getToLatitude());
        shot.setToLongitude(shot.getToLongitude());
    }
}
