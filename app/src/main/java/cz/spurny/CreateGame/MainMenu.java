package cz.spurny.CreateGame;

/**
 * Objekt: MainMenu.java
 * Popis:  Aktivita predstavujici hlavni menu aplikace.
 * Autor:  Frantisek Spurny
 * Datum:  12.6.2015
 **/

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.sql.SQLException;

import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.Dialogs.ApplicationTerminate;
import cz.spurny.Player.CreatePlayer;
import cz.spurny.Settings.Settings;
import cz.spurny.Settings.UserPreferences;

public class MainMenu extends ActionBarActivity {

    Context context;

    /* Prvky GUI */
    Button bNewGame;
    Button bSavedGames;
    Button bStatistics;
    Button bSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        /* Pripojeni interni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(this);


        DatabaseHandlerResort myDbHelper = new DatabaseHandlerResort(this);

        if (myDbHelper.checkDataBase()) {
            System.out.println("Databaze existuje");
        } else {
            System.out.println("Databaze neexistuje");
        }

        try {
            myDbHelper.createDataBase("/mnt/sdcard/golfStatDatabaseResort");
        } catch (IOException ioe) {

        }

        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){

        }


        /* Prvni zapnuti aplikace */
        if (dbi.getMainPlayer() == null ) { firstStartOfApplication(); }

        /* Ziskani kontextu */
        context = this;

        /* Pripojeni tlacitek hlavniho menu */
        bNewGame     = (Button) findViewById(R.id.MainMenu_button_newGame);
        bSavedGames  = (Button) findViewById(R.id.MainMenu_button_savedGames);
        bStatistics  = (Button) findViewById(R.id.MainMenu_button_statistics);
        bSettings    = (Button) findViewById(R.id.MainMenu_button_settings);

        /** Reakce na kliknuti na tlacitka **/
        buttonsHandler();

        /* Uzavreni interni databaze */
        dbi.close();
    }

    /** Reakce na kliknuti na tlacitka hlavniho menu **/
    public void buttonsHandler() {

        /* Intenty - slouzi k volani jinych aktivit */
        final Intent iNewGame      = new Intent(this,SelectResort.class);
        //Intent iSavedGames   = new Intent(this,SavedGames.class);
        //Intent iStats        = new Intent(this,Statistics.class);
        final Intent iSettings     = new Intent(this,Settings.class);

        /* Nova hra */
        bNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(iNewGame); }
        });

        /* Nastaveni */
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(iSettings); }
        });

    }

    /* Vykonani akci pri prvnim spusteni aplikace */
    public void firstStartOfApplication() {

        /* Nastaveni preferenci uzivatele */
        UserPreferences.setGpsDialogShow(this, false);

        /* Tvorba hrace */
        createPlayer();
    }

    /* Volani aktivity pro tvorbu hrace */
    public void createPlayer() {
        Intent iCreatePlayer = new Intent(this, CreatePlayer.class);
        startActivity(iCreatePlayer);
    }

    /** Reakce na zmacknuti tlacitka "zpet" - u hlavniho menu povede k uzavreni aplikace **/
    @Override
    public void onBackPressed()
    {
        ApplicationTerminate.dialog(this).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false; //nechceme menu v liste
    }
}
