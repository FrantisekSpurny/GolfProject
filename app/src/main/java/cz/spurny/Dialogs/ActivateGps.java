package cz.spurny.Dialogs;

/**
 * Objekt: ActivateGps.java
 * Popis:  Dialog dotazujici se uzivatele na zapnuti GPS.
 * Autor:  Frantisek Spurny
 * Datum:  16.06.2015
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

import cz.spurny.CreateGame.R;
import cz.spurny.Settings.UserPreferences;

public class ActivateGps {

    public static Dialog dialog(final Context context) {

        Dialog dialog = null;

        /* Tvorba pohledu obsahujiciho "checkbox" ktery slouzi pro zjisteni zdali chce uzivatel
           nadale vidat tento dialog ci ne */
        View checkBoxView = View.inflate(context, R.layout.activate_gps_check_box_layout, null);
        CheckBox checkBox = (CheckBox) (checkBoxView.findViewById(R.id.ActivateGps_checkBox_neverShowAgain));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               /* Ulozeni preference uzivatele */
                UserPreferences.setGpsDialogShow(context,isChecked);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.ActivateGps_string_dialogMessage))
                .setView(checkBoxView)
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ActivateGps_string_settings),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                /* Uzivateli se otevre nastaveni GPS */
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(intent);

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
