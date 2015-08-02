package cz.spurny.Dialogs;

/**
 * Objekt: SelectPlaymate.java
 * Popis:  Dialog slouzici pro tvorbu noveho hrace.
 * Autor:  Frantisek Spurny
 * Datum:  1.7.2015
 **/

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
import cz.spurny.CreateGame.SelectPlaymateAdapter;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.Settings.UserPreferences;
import cz.spurny.Toasts.NotValidPlayerToast;

public class AddPlayer {

    /* Prvky GUI */
    public static TextView tvName;
    public static TextView tvSurname;
    public static TextView tvNickname;
    public static TextView tvHandicap;
    public static EditText etName;
    public static EditText etSurname;
    public static EditText etNickname;
    public static EditText etHandicap;
    public static Button   bDone;

    /* Pole obsahujici, ktera pole jsou vyplnena validnimi hodnotami */
    public static Boolean[] isValid = {false,false,false,false};

    /* Kontext */
    public static Context context;

    /* Adapter */
    public static SelectPlaymateAdapter adapter;

    /* Seznam hracu */
    public static List<Player> players;

    /* Seznam spoluhracu */
    public static List<Boolean> isPlaymate;

    /* Dialog ktery tvorime */
    public static Dialog dialog;

    public static Dialog dialog(final Context activityContext,
                                final SelectPlaymateAdapter activityAdapter,
                                List<Player> activityPlayers,
                                List<Boolean> activityIsPlaymate) {

		/* tvorba dialogu pro tvorbu hole */
        dialog = new Dialog(activityContext);
        dialog.setContentView(R.layout.add_player_layout);
        dialog.setTitle(activityContext.getString(R.string.AddPlayer_string_title));

        context    = activityContext;
        adapter    = activityAdapter;
        players    = activityPlayers;
        isPlaymate = activityIsPlaymate;

        /* Pripojeni prvku GUI */
        tvName      = (TextView) dialog.findViewById(R.id.AddPlayer_textView_name);
        tvSurname   = (TextView) dialog.findViewById(R.id.AddPlayer_textView_surname);
        tvNickname  = (TextView) dialog.findViewById(R.id.AddPlayer_textView_nickname);
        tvHandicap  = (TextView) dialog.findViewById(R.id.AddPlayer_textView_handicap);
        etName      = (EditText) dialog.findViewById(R.id.AddPlayer_editText_name);
        etSurname   = (EditText) dialog.findViewById(R.id.AddPlayer_editText_surname);
        etNickname  = (EditText) dialog.findViewById(R.id.AddPlayer_editText_nickname);
        etHandicap  = (EditText) dialog.findViewById(R.id.AddPlayer_editText_handicap);
        bDone       = (Button)   dialog.findViewById(R.id.AddPlayer_button_AddPlayer);

        /* Tvorba noveho hrace */
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlayer();
            }
        });

        /* Zmeny textovych poli */
        editTextChangeHandler();

        return dialog;
    }

    /** Reakce na zmacknuti tlacitka "hotovo" - tvorba hrace **/
    public static void createPlayer() {

        Boolean valid = true;

        for (int i=0;i<isValid.length;i++) {

            if (!isValid[i])
                valid = false;

            switch (i) {
                case 0: // Jmeno
                    displayNonValid(isValid[i],tvName,etName);         break;
                case 1: // Prijmeni
                    displayNonValid(isValid[i],tvSurname,etSurname);   break;
                case 2: // Prezdivka
                    displayNonValid(isValid[i],tvNickname,etNickname); break;
                case 3: // Handicap
                    displayNonValid(isValid[i],tvHandicap,etHandicap); break;
            }
        }

        /* Kontrola zdali byla vsechna pole vyplnena validne */
        if (valid) { // ok
            savePlayer();
        } else {     // chyba
            NotValidPlayerToast.getToast(context).show();
        }
    }

    /** Ulozeni udaju o hraci do databaze **/
    public static void savePlayer() {

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);

        /* Tvorba hrace */
        Player player = new Player();

        /* Naplneni hodnotami */
        player.setName                   (etName    .getText().toString());
        player.setSurname                (etSurname .getText().toString());
        player.setNickname               (etNickname.getText().toString());
        player.setHandicap(Double.valueOf(etHandicap.getText().toString()));

        /* Vlozeni do databaze */
        long id = dbi.createPlayer(player);

        /* Prdani id */
        player.setId((int)id);

        /* Vlozeni do seznamu hracu v listu */
        players.add(player);

        /* Pidani polozky do seznamu spoluhracu */
        isPlaymate.add(false);

        /* Aktualizace seznamu */
        adapter.notifyDataSetChanged();

        /* Oddpojeni databaze */
        dbi.close();

        /* uzavreni dialogu */
        dialog.hide();
    }

    /** Pokud je dane pole nevalidni oznac ho **/
    public static void displayNonValid(Boolean valid,TextView tv,EditText et) {
        if (valid)
            return;

        tv.setTextColor(Color.RED);
        et.setTextColor(Color.RED);
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

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        /* Zmena prijmeni */
        etSurname.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                isSurnameValid(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etSurname.setTextColor(Color.BLACK);
                tvSurname.setTextColor(Color.BLACK);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        /* Zmena prezdivky */
        etNickname.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                isNicknameValid(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etNickname.setTextColor(Color.BLACK);
                tvNickname.setTextColor(Color.BLACK);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        /* Zmena hendikepu */
        etHandicap.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                isHandicapValid(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etHandicap.setTextColor(Color.BLACK);
                tvHandicap.setTextColor(Color.BLACK);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    /** Kontrola validity jmena **/
    public static void isNameValid(String name) {

        if (name.length() == 0) {
            isValid[0] = false;
        } else {
            isValid[0] = true;
        }
    }

    /** Kontrola validity prijmeni **/
    public static void isSurnameValid(String surname) {

        if (surname.length() == 0) {
            isValid[1] = false;
        } else {
            isValid[1] = true;
        }
    }

    /** Kontrola validity prezdivky **/
    public static void isNicknameValid(String nickname) {

        if (nickname.length() == 0) {
            isValid[2] = false;
        } else {
            isValid[2] = true;
        }
    }

    /** Kontrola validity jmena **/
    public static void isHandicapValid(String hcp) {

        if (hcp.length() == 0) {
            isValid[3] = false; return;
        }

        double handicap = Double.valueOf(hcp);

        if (hcp.length() == 0 || handicap < 0 || handicap > 54 ) {
            isValid[3] = false;
        } else {
            isValid[3] = true;
        }
    }
}
