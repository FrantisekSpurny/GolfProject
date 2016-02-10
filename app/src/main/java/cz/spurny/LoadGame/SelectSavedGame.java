package cz.spurny.LoadGame;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.Calculations.DistanceCalculations;
import cz.spurny.Calculations.ScoreCardCounting;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.SavedGame;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Tee;
import cz.spurny.Dialogs.SavedGameErase;
import cz.spurny.Game.SelectHole;
import cz.spurny.Game.SelectHoleAdapter;

public class SelectSavedGame extends ActionBarActivity {

    /** Kontext **/
    Context context;

    /** Prvky GUI **/
    ListView lvSavedGames;

    /** Ulozene hry **/
    List<SavedGame> savedGameList;
    List<Game>      gamesList;

    /** Adapter listu **/
    SelectSavedGameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_saved_game_layout);

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(this);
        DatabaseHandlerResort   dbr = new DatabaseHandlerResort(this);

        /* Ziskani kontextu */
        context = this;

        /* Pripojeni prvku GUI */
        lvSavedGames = (ListView) findViewById(R.id.SelectSavedGame_listView_gamesList);

        /* Seznam ulozenych her */
        savedGameList = dbi.getAllSavedGames();

        /* Tvorba adapteru */
        gamesList = getSavedGames(savedGameList,dbi);
        adapter = new SelectSavedGameAdapter(context,gamesList,dbi,dbr);

		/* Prirazeni adapteru */
        lvSavedGames.setAdapter(adapter);

        listClickHandler();

        /* Uzavreni databaze */
        dbi.close();
    }

    /** Tvorba seznamu obsahujici objekty konkretnich her */
    public List<Game> getSavedGames(List<SavedGame> savedGameList,DatabaseHandlerInternal dbi) {
        List<Game> games = new ArrayList<>();

        for (int i = 0; i < savedGameList.size(); i++) {
            games.add(dbi.getGame(savedGameList.get(i).getGameId()));
        }

        return games;
    }

    /** Reakce na pokliknuti na seznam **/
    public void listClickHandler() {

        lvSavedGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                loadGame(gamesList.get(arg2));
            }
        });

        lvSavedGames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {

                SavedGameErase.dialog(context,savedGameList.get(index),gamesList,adapter,index).show();
                return true;
            }
        });

    }

    /** Nacteni vybrane hry **/
    public void loadGame(Game game) {

        /* Vypocet vzdalenosti jednotlivych jamek k vypoctu dochazi zde, aby se neopakoval */
        List<Double> holeLengthList = DistanceCalculations.getLenghtOfAllHoles(game.getId(), context);
        double[] holeLengthArray = new double[holeLengthList.size()];
        for(int i = 0; i < holeLengthList.size(); i++) holeLengthArray[i] = holeLengthList.get(i);

        /* Vypocet osobniho paru na jednotlivych jamkach */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);
        int idTee1 = dbi.getAllGameCourseOfGame(game.getId()).get(0).getIdTee();
        int[] personalPar = ScoreCardCounting.countPersonalPar(idTee1, dbi.getMainPlayer(), game, context);
        dbi.close();

        /* Spusteni aktivity Vyber jamky */
        goToSelectHole(game.getId(), holeLengthArray, personalPar);
    }

    /** Spusteni aktivity "Vyber jamky" **/
    public void goToSelectHole(long gameId,double[] holeLengthArray,int[] personalPar) {
        Intent iSelectHole = new Intent(this,SelectHole.class);
        iSelectHole.putExtra("EXTRA_SELECT_HOLE_IDGAME",gameId);
        iSelectHole.putExtra("EXTRA_SELECT_HOLE_LENGHT_ARRAY",holeLengthArray);
        iSelectHole.putExtra("EXTRA_SELECT_HOLE_PERSONAL_PAR",personalPar);
        this.startActivity(iSelectHole);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
