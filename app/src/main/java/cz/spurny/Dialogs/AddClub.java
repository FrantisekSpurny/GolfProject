package cz.spurny.Dialogs;

/**
 * Objekt: AddClub.java
 * Popis:  Dialog umoznujici vytvorit novou holi.
 * Autor:  Frantisek Spurny
 * Datum:  24.06.2015
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Club;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.Player.EditBagAdapter;
import cz.spurny.Toasts.ClubAddedSuccessfully;
import cz.spurny.Toasts.NotValidClubToast;

public class AddClub {

    public static Boolean[] isValid = {false,false,false};

    /* Prvky GUI */
    public static EditText etName;
    public static EditText etModel;
    public static EditText etSsl;
    public static Button   bDone;
    public static TextView tvName;
    public static TextView tvModel;
    public static TextView tvSsl;

    /* Kontext */
    public static Context context;

    /* Adapter */
    public static EditBagAdapter adapter;

    /* Seznam holi */
    public static List<Club> clubs;

    public static Dialog dialog(final DatabaseHandlerInternal dbi,
                                final Context                 activityContext,
                                final EditBagAdapter          activityAdapter,
                                      List<Club>              activityClubs) {

		/* tvorba dialogu pro tvorbu hole */
        final Dialog dialog = new Dialog(activityContext);
        dialog.setContentView(R.layout.add_club_layout);
        dialog.setTitle(activityContext.getString(R.string.AddClub_string_title));

        context = activityContext;
        adapter = activityAdapter;
        clubs   = activityClubs;

        /* pripojeni GUI*/
        etName  = (EditText) dialog.findViewById(R.id.AddClub_editText_name);
        etModel = (EditText) dialog.findViewById(R.id.AddClub_editText_model);
        etSsl   = (EditText) dialog.findViewById(R.id.AddClub_editText_ssl);
        bDone   = (Button)   dialog.findViewById(R.id.AddClub_button_done);
        tvName  = (TextView) dialog.findViewById(R.id.AddClub_textView_name);
        tvModel = (TextView) dialog.findViewById(R.id.AddClub_textView_model);
        tvSsl   = (TextView) dialog.findViewById(R.id.AddClub_textView_ssl);

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClub(dbi,dialog);
            }
        });

        editTextChangeHandler();

        return dialog;
    }

    /* Reakce na kliknuti na tlacitko "hotovo" */
    public static void addClub(DatabaseHandlerInternal dbi,Dialog dialog) {

        Boolean valid = true;

        for (int i=0;i<isValid.length;i++) {

            if (!isValid[i])
                valid = false;

            switch (i) {
                case 0: // Jmeno
                    displayNonValid(isValid[i],tvName,etName);   break;
                case 1: // Model
                    displayNonValid(isValid[i],tvModel,etModel); break;
                case 2: // Standardni delka rany
                    displayNonValid(isValid[i],tvSsl,etSsl);     break;
            }
        }

        /* Kontrola zdali byla vsechna pole vyplnena validne */
        if (valid) { // ok
            saveClub(dbi);
            dialog.dismiss();
        } else {     // chyba
            NotValidClubToast.getToast(context).show();
        }
    }

    /** Reakce na zmeny v editText polich **/
    public static void editTextChangeHandler() {

        /* Zmena jmena */
        etName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                isNameValid(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etName.setTextColor(Color.BLACK);
                tvName.setTextColor(Color.BLACK);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        /* Zmena modelu */
        etModel.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                isModelValid(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etModel.setTextColor(Color.BLACK);
                tvModel.setTextColor(Color.BLACK);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        /* Zmena standardni delky rany */
        etSsl.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                isSslValid(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etSsl.setTextColor(Color.BLACK);
                tvSsl.setTextColor(Color.BLACK);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    /** Ulozeni udaju o hraci do databaze **/
    public static void saveClub(DatabaseHandlerInternal dbi) {

        Club newClub =  new Club();

        /* Naplneni hodnotami */
        newClub.setName(etName.getText().toString());
        newClub.setModel(etModel.getText().toString());
        newClub.setStandardStrokeLength(Double.valueOf(etSsl.getText().toString()));
        newClub.setAverageStrokeLength(Double.valueOf(etSsl.getText().toString()));

        /* Vlozeni do databaze */
        newClub.setId((int) dbi.createClub(newClub));
        clubs.add(newClub);
        adapter.notifyDataSetChanged();

        /* Zobrazeni hlaseni o uspesnosti pridani */
        ClubAddedSuccessfully.getToast(context).show();

        /* Oddpojeni databaze */
        dbi.close();
    }

    /** Pokud je dane pole nevalidni oznac ho **/
    public static void displayNonValid(Boolean valid,TextView tv,EditText et) {
        if (valid)
            return;

        tv.setTextColor(Color.RED);
        et.setTextColor(Color.RED);
    }

    /** Kontrola validity jmena **/
    public static void isNameValid(String name) {

        if (name.length() == 0) {
            isValid[0] = false;
        } else {
            isValid[0] = true;
        }
    }

    /** Kontrola validity modelu **/
    public static void isModelValid(String surname) {

        if (surname.length() == 0) {
            isValid[1] = false;
        } else {
            isValid[1] = true;
        }
    }

    /** Kontrola validity standardni delky rany **/
    public static void isSslValid(String ssl) {

        if (ssl.length() == 0) {
            isValid[2] = false; return;
        }

        double dSsl = Double.valueOf(ssl);

        if (ssl.length() == 0 || dSsl < 0 ) {
            isValid[2] = false;
        } else {
            isValid[2] = true;
        }
    }
}
