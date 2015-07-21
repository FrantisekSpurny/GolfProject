package cz.spurny.Settings;

/**
 * Objekt: Settings.java
 * Popis:  Rozcestnik vedouci na ruzne moznosti uzivatelskeho nastaveni
 * Autor:  Frantisek Spurny
 * Datum:  1.7.2015
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import cz.spurny.CreateGame.MainMenu;
import cz.spurny.CreateGame.R;
import cz.spurny.Player.CreatePlayer;
import cz.spurny.Player.EditBag;

public class Settings extends ActionBarActivity {

    /* Prvky GUI */
    Button bEditProfile;
    Button bEditBag;

    /* Intenty */
    Intent iEditProfile;
    Intent iEditBag;

    /* Context */
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        /* Pripojeni prvku GUI */
        bEditProfile = (Button) findViewById(R.id.Settings_button_editProfile);
        bEditBag     = (Button) findViewById(R.id.Settings_button_editBag);

        /* Ulozeni kontextu */
        context = this;

        /* Tvorba intentu */
        iEditProfile = new Intent(this, CreatePlayer.class);
        iEditBag     = new Intent(this, EditBag.class);
        iEditProfile.putExtra("EXTRA_CREATE_PLAYER_TYPE",1);
        iEditBag    .putExtra("EXTRA_EDIT_BAG_TYPE"     ,1);

        /* Reakce na kliknuti na tlacitka */
        buttonClickHandler();
    }

    /** Reakce na kliknuti na tlacitka v aktivite "Settings" **/
    public void buttonClickHandler() {

        /* Editace profilu */
        bEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ((Activity)context).finish(); startActivity(iEditProfile); }
        });

        /* Editace profilu */
        bEditBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ((Activity)context).finish(); startActivity(iEditBag); }
        });

    }

    /** Reakce na kliknuti na tlacitko zpet **/
    public void onBackPressed()
    {
       this.finish();
       startActivity(new Intent(this, MainMenu.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
