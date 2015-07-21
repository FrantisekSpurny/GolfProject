package cz.spurny.Dialogs;

/**
 * Objekt: SelectPoint.java
 * Popis:  Dialog slouzici pro vyber zpusobu zvoleni jamky.
 * Autor:  Frantisek Spurny
 * Datum:  16.07.2015
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Game.GameOnHole;
import cz.spurny.Game.MeasureDraw;
import cz.spurny.Settings.UserPreferences;

public class MeasureDrawPointSelectionMethod {

    public static Dialog dialog(final Context context,
                                final MeasureDraw measureDraw,
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

                                /** Reinicializace bitmapy **/
                                if (measureDraw.isPointsVisible()) {
                                    measureDraw.reinitBitmap();
                                    measureDraw.drawMeasureActualPoint();
                                }

                                /** Zvoleni bodu pomoci gps **/
                                if (rbGps.isChecked())
                                    System.out.println("TODOGPS");

                                /** Zvoleni bodu jamky **/
                                else if (rbHolePoint.isChecked())
                                    MeasureDrawHolePointsList.dialog(context,measureDraw,from,view).show();
                                /** Zvoleni bodu rucne **/
                                else {
                                    if (from) { //zadani bodu od
                                        measureDraw.setDestinationSelection(false);
                                        measureDraw.setFromSelection(true);
                                    } else {   //zadani bodu kam
                                        measureDraw.setFromSelection(false);
                                        measureDraw.setDestinationSelection(true);
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
