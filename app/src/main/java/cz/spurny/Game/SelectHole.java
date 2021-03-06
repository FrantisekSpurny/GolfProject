package cz.spurny.Game;

/**
 * Objekt: SelectHole.java
 * Popis:  Aktivita zobrazujici seznam jamek.
 * Autor:  Frantisek Spurny
 * Datum:  08.07.2015
 **/

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import cz.spurny.Calculations.DistanceCalculations;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Dialogs.ApplicationTerminate;
import cz.spurny.Dialogs.CaptureShotPointSelectionMethod;
import cz.spurny.Dialogs.GameTerminate;
import cz.spurny.Dialogs.SaveGameResults;

public class SelectHole extends ActionBarActivity {

    /* Context */
    Context context;

    /* Id aktualni hry */
    long gameId;

    /* Pole obsahujici vzdalenosti jednotlivych jamek */
    double[] holeLengthArray;

    /* Pole obsahujici osobni par hrace */
    int[] personalPar;

    /* Prvky GUI */
    ListView lvHoles;

    /* Adapter */
    SelectHoleAdapter adapter;
    List<Hole> holes = null; // hodnoty pro

    /* Seznam hracu dane hry */
    List<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_hole_layout);

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(this);

        /* Ziskani kontextu */
        context = this;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        gameId          = iPrevActivity.getLongExtra("EXTRA_SELECT_HOLE_IDGAME", -1);
        holeLengthArray = iPrevActivity.getDoubleArrayExtra("EXTRA_SELECT_HOLE_LENGHT_ARRAY");
        personalPar     = iPrevActivity.getIntArrayExtra("EXTRA_SELECT_HOLE_PERSONAL_PAR");

        /* Pripojeni prvku GUI */
        lvHoles = (ListView) findViewById(R.id.SelectHole_listView_holesList);

        /* Ziskani vsech hracu dane hry */
        players = dbi.getAllPlaymatesOfGame((int)gameId);

        /* Ziskani hodnot z databaze */
        holes = dbi.getAllHolesOfGame((int)gameId);

        /* Tvorba adapteru */
        adapter = new SelectHoleAdapter(this, holes,dbi.getNumOfGameCourses((int)gameId),holeLengthArray,players,dbi,(int)gameId);

		/* Prirazeni adapteru */
        lvHoles.setAdapter(adapter);

        /* Klinuti na polozku seznamu */
        listItemClickHandler();

        /* Uzavreni databaze */
        dbi.close();
    }

    /** Rakce na kliknuti na polozku seznamu jamek **/
    public void listItemClickHandler() {

        /* Zachytavani kliknuti na polozku seznamu */
        lvHoles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent iGameOnHole =  new Intent(context,GameOnHole.class);
                iGameOnHole.putExtra("EXTRA_GAME_ON_HOLE_IDGAME",(int)gameId);
                iGameOnHole.putExtra("EXTRA_GAME_ON_HOLE_IDHOLE",holes.get(arg2).getId());
                iGameOnHole.putExtra("EXTRA_GAME_ON_HOLE_LENGHT_ARRAY",holeLengthArray);
                iGameOnHole.putExtra("EXTRA_GAME_ON_HOLE_INDEX",arg2);
                iGameOnHole.putExtra("EXTRA_GAME_ON_HOLE_PERSONAL_PAR",personalPar);
                startActivity(iGameOnHole);
            }
        });
    }

    /** Reakce na zmacknuti tlacitka "zpet" - navrat do hlavniho menu **/
    @Override
    public void onBackPressed()
    {
        /* Ziskani obejktu hry */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
        Game game = dbi.getGame(gameId);
        dbi.close();

        GameTerminate.dialog(this,game).show();
    }

    /** Zobrazeni menu nabidky **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_hole_menu, menu);

        return true;
    }

    /** Reakce na kliknuti na polozku menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.SelectHoleMenu_item_saveGame:

                /* Ziskani obejktu hry */
                DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
                Game game = dbi.getGame(gameId);
                dbi.close();

                /* Dialog zobrazujici pocet zahranych jamek jednotlivimi hraci */
                SaveGameResults.dialog(context, game, dbi).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
