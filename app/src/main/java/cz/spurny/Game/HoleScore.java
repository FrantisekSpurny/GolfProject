package cz.spurny.Game;

/**
 * Objekt: HoleScore.java
 * Popis:  Zaznamenani skore jamky.
 * Autor:  Frantisek Spurny
 * Datum:  21.07.2015
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Settings.UserPreferences;

public class HoleScore extends ActionBarActivity {

    /*** ATRIBUTY ***/

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
    TabHost  thHole;
    TabHost  thPlayer;
    TabHost  thScore;
    TabHost  thPuts;
    TabHost  thShots;
    TabHost  thPenaltyShots;

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

    /** Tato aktivita neobsahuje menu **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /** Reakce na ukonceni aktivity **/
    @Override
    protected void onStop() {
        super.onStop();

        /* Uzavzeni databaze */
        dbr.close();
        dbi.close();
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
        connectGui();

        /* Inicializace "TabHost" polozek */
        initTabHosts();

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
    public void connectGui() {
        thHole          = (TabHost) findViewById(R.id.HoleScore_tabHost_hole);
        thPlayer        = (TabHost) findViewById(R.id.HoleScore_tabHost_player);
        thScore         = (TabHost) findViewById(R.id.HoleScore_tabHost_score);
        thPuts          = (TabHost) findViewById(R.id.HoleScore_tabHost_put);
        thShots         = (TabHost) findViewById(R.id.HoleScore_tabHost_shots);
        thPenaltyShots  = (TabHost) findViewById(R.id.HoleScore_tabHost_penaltyShots);
    }

    /** Inicializace a naplneni hodnotami polozek "TabHost" **/
    public void initTabHosts() {

        /* Seznam jamek */
        initTabHost(getHoleNamesList()  ,getHoleIdList()  ,thHole);

        /* Seznam hracu */
        initTabHost(getPlayerNamesList(),getPlayerIdList(),thPlayer);

        /* Skore */
        initTabHost(getScoreNamesList(18)  ,getNumberList(18)  ,thScore);

        /* Paty */
        initTabHost(getNumberList(6)     ,getNumberList(6)  ,thPuts);

        /* Rany */
        initTabHost(getNumberList(12)     ,getNumberList(12)  ,thShots);

        /* Trestne rany */
        initTabHost(getNumberList(5)     ,getNumberList(5)  ,thPenaltyShots);

    }

    /** Inicializace "TabHost" prvku **/
    public void initTabHost(List<String> namesList,List<String> indexList,TabHost tabHost) {

        /* Odstraneni vsech predeslych hodnot */
        if (tabHost.getTabWidget() != null && tabHost.getTabWidget().getTabCount() > 0)
            tabHost.clearAllTabs();

        /* inicializace */
        tabHost.setup();

        TabHost.TabSpec spec;
        for (int i = 0; i < namesList.size(); i++) {

            /* Nastaveni identifikatoru */
            spec = tabHost.newTabSpec(indexList.get(i));

            /* Nastaveni obsahu */
            spec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return (new TextView(HoleScore.this));
                }
            });

            /* Nastaveni popisku */
            spec.setIndicator(namesList.get(i));

            /* Pridani "Tabu" */
            tabHost.addTab(spec);
        }
    }

    /** Tvorba seznamu jmen jamek **/
    public List<String> getHoleNamesList() {

        List<String> nameList = new ArrayList<>();

        for (Hole h:holeList)
            nameList.add(h.getNumber() + ".");

        return nameList;
    }

    /** Tvorba seznamu id jamek */
    public List<String> getHoleIdList() {

        List<String> idList = new ArrayList<>();

        for (Hole h:holeList)
            idList.add(String.valueOf(h.getId()));

        return idList;
    }

    /** Tvorba seznamu jmen hracu **/
    public List<String> getPlayerNamesList() {

        List<String> nameList = new ArrayList<>();

        for (Player p:playerList)
            nameList.add(p.getNickname());

        return nameList;
    }

    /** Tvorba seznamu id hracu **/
    public List<String> getPlayerIdList() {

        List<String> idList = new ArrayList<>();

        for (Player p:playerList)
            idList.add(String.valueOf(p.getId()));

        return idList;
    }

    /** Tvorba seznamu score **/
    public List<String> getScoreNamesList(int n) {
        List<String> scoreList = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            scoreList.add(String.valueOf(i));
        }

        return scoreList;
    }

    /** Tvorba seznamu obsahujici ciselne retezce od 1 do n **/
    public List<String> getNumberList(int n) {
        List<String> numberList = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            numberList.add(String.valueOf(i));
        }

        return numberList;
    }

}
