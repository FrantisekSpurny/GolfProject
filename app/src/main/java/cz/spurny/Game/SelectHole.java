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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import cz.spurny.Calculations.DistanceCalculations;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Dialogs.ApplicationTerminate;
import cz.spurny.Dialogs.GameTerminate;

public class SelectHole extends ActionBarActivity {

    /* Context */
    Context context;

    /* Id aktualni hry */
    long gameId;

    /* Pole obsahujici vzdalenosti jednotlivych jamek */
    double[] holeLengthArray;

    /* Prvky GUI */
    ListView lvHoles;

    /* Adapter */
    SelectHoleAdapter adapter;
    List<Hole> holes = null; // hodnoty pro adapter

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

        /* Pripojeni prvku GUI */
        lvHoles = (ListView) findViewById(R.id.SelectHole_listView_holesList);

        /* Ziskani hodnot z databaze */
        holes = dbi.getAllHolesOfGame((int)gameId);

        /* Tvorba adapteru */
        adapter = new SelectHoleAdapter(this, holes,dbi.getNumOfGameCourses((int)gameId),holeLengthArray);

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
                startActivity(iGameOnHole);
            }
        });
    }

    /** Reakce na zmacknuti tlacitka "zpet" - navrat do hlavniho menu **/
    @Override
    public void onBackPressed()
    {
        GameTerminate.dialog(this).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
