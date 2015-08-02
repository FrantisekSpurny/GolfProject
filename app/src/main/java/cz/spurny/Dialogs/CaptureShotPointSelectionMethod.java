package cz.spurny.Dialogs;

/**
 * Objekt: CaptureShotPointSelectionMethod.java
 * Popis:  Dialog slouzici k urceni metody zadavani polohy.
 * Autor:  Frantisek Spurny
 * Datum:  02.08.2015
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.RadioButton;

import cz.spurny.CreateGame.R;
import cz.spurny.Game.MeasureDraw;
import cz.spurny.Game.ShotCaptureDraw;

public class CaptureShotPointSelectionMethod {

    public static Dialog dialog(final Context context,
                                final ShotCaptureDraw shotCaptureDraw,
                                final boolean from,
                                final cz.spurny.DatabaseResort.View view) {

        Dialog dialog = null;

        View radioButtonView = View.inflate(context, R.layout.point_selection_method_layout, null);

        /* Pripojeni prvku gui */
        final RadioButton rbByHand    = (RadioButton) radioButtonView.findViewById(R.id.PointSelectionMethod_radioButton_byHand);
        final RadioButton rbGps       = (RadioButton) radioButtonView.findViewById(R.id.PointSelectionMethod_radioButton_gps);
        final RadioButton rbHolePoint = (RadioButton) radioButtonView.findViewById(R.id.PointSelectionMethod_radioButton_holePoint);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.PointSelectionMethod_string_title))
                .setView(radioButtonView)
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                /** Zvoleni bodu pomoci gps **/
                                if (rbGps.isChecked())
                                    System.out.println("TODOGPS");

                                /** Zvoleni bodu jamky **/
                                else if (rbHolePoint.isChecked())
                                    CaptureShotHolePointsList.dialog(context,shotCaptureDraw,from,view).show();
                                /** Zvoleni bodu rucne **/
                                else {
                                    if (from) { //zadani bodu od
                                        shotCaptureDraw.setDestinationSelection(false);
                                        shotCaptureDraw.setFromSelection(true);
                                    } else {   //zadani bodu kam
                                        shotCaptureDraw.setFromSelection(false);
                                        shotCaptureDraw.setDestinationSelection(true);
                                    }
                                }
                            }
                        })

               /* Negativni volba */
                .setNegativeButton(context.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               /* zavreni dialogu */
                                dialog.cancel();
                            }
                        });

        /* Tvorba alert dialogu */
        AlertDialog alert = builder.create();
        dialog = alert;

        return dialog;
    }

}
