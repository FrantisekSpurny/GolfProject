package cz.spurny.Player;

/**
 * Objekt: CreatePlayer.java
 * Popis:  Aktivita urcena pro tvorbu profilu hrace.
 * Autor:  Frantisek Spurny
 * Datum:  22.06.2015
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.Dialogs.ApplicationTerminate;
import cz.spurny.Dialogs.CreatePlayerTerminate;
import cz.spurny.Settings.Settings;
import cz.spurny.Settings.UserPreferences;
import cz.spurny.Toasts.NotValidPlayerToast;

public class CreatePlayer extends ActionBarActivity {

    /* Prvky GUI */
    TextView tvName;
    TextView tvSurname;
    TextView tvNickname;
    TextView tvHandicap;
    EditText etName;
    EditText etSurname;
    EditText etNickname;
    EditText etHandicap;
    Button   bDone;

    /* Pole obsahujici, ktera pole jsou vyplnena validnimi hodnotami */
    Boolean[] isValid = {false,false,false,false};

    /* Kontext */
    Context context;

    /* Typ */
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_player_layout);

        /* Prevzeti parametru z volajici aktivity - urcuje typ */
        Intent iPrevActivity = getIntent();
        type = iPrevActivity.getIntExtra("EXTRA_CREATE_PLAYER_TYPE",0);

        /* Zmena titulku pokud se jedna o upravu profilu */
        if (type==1)
            setTitle(this.getString(R.string.CreatePlayer_string_titleEdit));

        /* Ulozeni kontextu */
        context = this;

        /* Pripojeni prvku GUI */
        tvName      = (TextView) findViewById(R.id.CreatePlayer_textView_name);
        tvSurname   = (TextView) findViewById(R.id.CreatePlayer_textView_surname);
        tvNickname  = (TextView) findViewById(R.id.CreatePlayer_textView_nickname);
        tvHandicap  = (TextView) findViewById(R.id.CreatePlayer_textView_handicap);

        etName      = (EditText) findViewById(R.id.CreatePlayer_editText_name);
        etSurname   = (EditText) findViewById(R.id.CreatePlayer_editText_surname);
        etNickname  = (EditText) findViewById(R.id.CreatePlayer_editText_nickname);
        etHandicap  = (EditText) findViewById(R.id.CreatePlayer_editText_handicap);

        bDone       = (Button)   findViewById(R.id.CreatePlayer_button_createPlayer);

        /* Zmeny textovych poli */
        editTextChangeHandler();

        /* Tvorba noveho hrace */
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlayer();
            }
        });

        /* Naplneni aktualnich hodnot uzivatelskeho profilu */
        if (type==1)
            initFields();
    }

    /** Reakce na zmacknuti tlacitka "hotovo" - tvorba hrace **/
    public void createPlayer() {

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
            if (type == 0) {
                savePlayer();
                goToEditBag();
            } else {
                editPlayer();
                goToSettings();
            }
        } else {     // chyba
            NotValidPlayerToast.getToast(this).show();
        }
    }

    /** Ulozeni udaju o hraci do databaze **/
    public void savePlayer() {

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

        /* Ulozeni id uzivatele jako id "hlavniho" uzivatele */
        UserPreferences.setMainUserId(context,id);

        /* Oddpojeni databaze */
        dbi.close();
    }

    /** Editace udaju o hraci do databaze **/
    public void editPlayer() {

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);

        /* Tvorba hrace */
        Player player = new Player();

        /* Naplneni hodnotami */
        player.setName(etName.getText().toString());
        player.setSurname(etSurname.getText().toString());
        player.setNickname(etNickname.getText().toString());
        player.setHandicap(Double.valueOf(etHandicap.getText().toString()));

        /* Aktualizace databaze */
        dbi.updateMainPlayer(player);

        /* Oddpojeni databaze */
        dbi.close();
    }

    /** Tvorba profilu hotova - jdi na editaci Bagu **/
    public void goToEditBag() {

        Intent iEditBag = new Intent(context,EditBag.class);
        ((Activity)context).finish();
        startActivity(iEditBag);
    }

    /** Tvorba profilu hotova - jdi do nastaveni **/
    public void goToSettings() {

        Intent iSettings = new Intent(context,Settings.class);
        ((Activity)context).finish();
        startActivity(iSettings);
    }

    /** Pokud je dane pole nevalidni oznac ho **/
    public void displayNonValid(Boolean valid,TextView tv,EditText et) {
        if (valid)
            return;

        tv.setTextColor(Color.RED);
        et.setTextColor(Color.RED);
    }

    /** Reakce na zmeny v editText polich **/
    public void editTextChangeHandler() {

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
    public void isNameValid(String name) {

        if (name.length() == 0) {
            isValid[0] = false;
        } else {
            isValid[0] = true;
        }
    }

    /** Kontrola validity prijmeni **/
    public void isSurnameValid(String surname) {

        if (surname.length() == 0) {
            isValid[1] = false;
        } else {
            isValid[1] = true;
        }
    }

    /** Kontrola validity prezdivky **/
    public void isNicknameValid(String nickname) {

        if (nickname.length() == 0) {
            isValid[2] = false;
        } else {
            isValid[2] = true;
        }
    }

    /** Kontrola validity jmena **/
    public void isHandicapValid(String hcp) {

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

    /** Vlozeni aktualnich hodnot uzivatelskeho profilu do "EditText" poli **/
    public void initFields() {

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);

        Player player = dbi.getMainPlayer();

        /* Vlozeni hodnot */
        etName    .setText(player.getName());
        etSurname .setText(player.getSurname());
        etNickname.setText(player.getNickname());
        etHandicap.setText(String.valueOf(player.getHandicap()));

        /* Odpojeni databaze */
        dbi.close();
    }

    @Override
    public void onBackPressed()
    {
        if (type==0)
            CreatePlayerTerminate.dialog(this).show();
        else
            goToSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false; // nezobrazovat menu v liste
    }
}