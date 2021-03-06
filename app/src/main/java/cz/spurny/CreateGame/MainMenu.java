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

import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.Dialogs.ApplicationTerminate;
import cz.spurny.Dialogs.NoDatabaseResort;
import cz.spurny.LoadGame.SelectSavedGame;
import cz.spurny.Player.CreatePlayer;
import cz.spurny.Settings.Settings;
import cz.spurny.Settings.UserPreferences;
import cz.spurny.Toasts.NoSavedGames;

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

        /* Nastaveni titulku */
        setTitle(this.getString(R.string.MainMenu_string_title));

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
        final Intent iSettings     = new Intent(this,Settings.class);

        /* Nova hra */
        bNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Kontrola zdali existuje database resortu */
                databaseResort(iNewGame);
            }
        });

        /* Ulozene hry */
        bSavedGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedGames();
            }
        });

        /* Nastaveni */
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(iSettings);
            }
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

    /** Kotrola zdali existuje databaze resortu **/
    public void databaseResort(Intent iNewGame) {
        DatabaseHandlerResort myDbHelper = new DatabaseHandlerResort(this);

        if (!myDbHelper.checkDataBase())
            NoDatabaseResort.dialog(context).show();
        else
            startActivity(iNewGame);
    }

    /** Prechod na aktivity ulozenych her **/
    public void savedGames() {

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);

        /* Nebyly ulozeny zadne hry */
        if (dbi.getAllSavedGames() == null)
            NoSavedGames.getToast(context).show();
        else {
            Intent iSavedGames = new Intent(context, SelectSavedGame.class);
            startActivity(iSavedGames);
        }

        dbi.close();
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
