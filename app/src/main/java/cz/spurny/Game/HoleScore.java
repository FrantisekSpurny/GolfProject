package cz.spurny.Game;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Settings.UserPreferences;

public class HoleScore extends ActionBarActivity {

    /*** Atributy ***/

    /** Kontext hry **/
    Game         game;
    List<Hole>   holeList;
    List<Player> playerList;

    /** Vybrane hodnoty **/
    Hole         actualHole;
    Player       actualPlayer;
    int          actualScore;
    int          actualPuts;
    int          actualShots;
    int          actualPenaltyShots;

    /** Prvky GUI **/

    /** Databaze **/
    DatabaseHandlerInternal dbi;
    DatabaseHandlerResort   dbr;

    /** Kontext **/
    Context context;

    /*** ZIVOTNI CYKLUS ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hole_score_layout);

        /* Ziskani kontextu */
        context = this;

        /* Inicializace aktivity */
        init();


    }

    /*** INICIALIZACE ***/

    /** Zakladni inicializace aktivity **/
    public void init() {

        /* Pripojeni databaze */
        dbr = new DatabaseHandlerResort  (context);
        dbi = new DatabaseHandlerInternal(context);

        /* Prevzeti hodnot z volajici aktivity */
        getExtras();

        /* Pripojeni prvku GUI */
        connectGui;

    }

    /** Prevzeti hodnot z volajici aktivity a ziskani kontextu **/
    public void getExtras() {

        int idGame,idHole,shotsCount;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idGame          = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_IDGAME", -1);
        idHole          = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_IDHOLE", -1);
        shotsCount      = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_IDHOLE", -1);

        /* Naplneni atributu */
        game                = dbi.getGame(idGame);
        actualHole          = dbr.getHole(idHole);
        holeList            = dbi.getAllHolesOfGame(idGame);
        playerList          = dbi.getAllPlaymatesOfGame(idGame);
        actualPlayer        = dbi.getPlayer((int) UserPreferences.getMainUserId(context));
        actualScore         = shotsCount+2;
        actualShots         = shotsCount;
        actualPuts          = 2;
        actualPenaltyShots  = 0;
    }

    /** Pripojeni prvku GUI **/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
