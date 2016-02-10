package cz.spurny.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.SavedGame;
import cz.spurny.Settings.UserPreferences;
import cz.spurny.Toasts.GameRecordedSuccessfully;
import cz.spurny.Toasts.GameSavedSuccessfully;

/**
 * Objekt: SaveGame.java
 * Popis:  Dialog dotazjucici se uzivatele zdali chce ulozit aktualni hru.
 * Autor:  Frantisek Spurny
 * Datum:  19.08.2015
 */

public class SaveGame {

    public static Dialog dialog(final Context context,final Game game) {

        Dialog dialog = null;

        /* Tvorba pohledu obsahujiciho "checkbox" ktery slouzi pro zjisteni zdali chce uzivatel
           nadale vidat tento dialog ci ne */
        View checkBoxView = View.inflate(context, R.layout.check_box_dialog_layout, null);
        CheckBox checkBox = (CheckBox) (checkBoxView.findViewById(R.id.CheckBoxDialog_checkBox_neverShowAgain));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               /* Ulozeni preference uzivatele */
                UserPreferences.setSaveDialogShow(context, isChecked);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.SaveGame_string_title))
                .setView(checkBoxView)
                .setCancelable(false)

               /* Pozitivni volba */
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Boolean isSaved;

                                /* Pripojeni databaze */
                                DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
                                isSaved = dbi.isGameSaved(game.getId());

                                /* Hra jeste neni ulozena */
                                if (!isSaved) {
                                    dbi.createSavedGame(new SavedGame(game.getId()));
                                }

                                /* Uzavreni databaze */
                                dbi.close();

                                /* Zobrazeni Toast ze je hra zaznamenana */
                                GameSavedSuccessfully.getToast(context).show();

                                /* Prechod do hlavniho menu */
                                Intent iMainMenu = new Intent(context,MainMenu.class);
                                context.startActivity(iMainMenu);
                            }
                        })

                /* Negativni volba */
                .setNegativeButton(context.getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /* Prechod do hlavniho menu */
                                Intent iMainMenu = new Intent(context,MainMenu.class);
                                context.startActivity(iMainMenu);
                            }
                        });

        /* Tvorba alert dialogu */
        AlertDialog alert = builder.create();
        dialog = alert;

        return dialog;
    }

}
