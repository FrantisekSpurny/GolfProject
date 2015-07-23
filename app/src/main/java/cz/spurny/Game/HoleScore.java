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
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Dialogs.HoleScoreHoleList;
import cz.spurny.Dialogs.HoleScorePlayerList;
import cz.spurny.Dialogs.MeasureDrawPointSelectionMethod;
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
    TabHost  thScore;
    TabHost  thPuts;
    TabHost  thShots;
    TabHost  thPenaltyShots;
    TextView tvHole;
    TextView tvPlayer;
    TableRow trHole;
    TableRow trPlayer;

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

        /* Incializace "TextView" */
        initTextView();

        /* Rakce na poklinuti na radky tabulky jamka a hrac */
        clickHandlerTableRow();
    }

    /** Prevzeti hodnot z volajici aktivity a ziskani kontextu **/
    public void getExtras() {

        int idGame,idHole,shotsCount;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idGame          = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_IDGAME", -1);
        idHole          = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_IDHOLE", -1);
        shotsCount      = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_NUM_OF_SHOTS", -1);

        /* Naplneni atributu */
        game                = dbi.getGame(idGame);
        actualHole          = dbr.getHole(idHole);
        holeList            = dbi.getAllHolesOfGame(idGame);
        //playerList          = dbi.getAllPlaymatesOfGame(idGame);
        actualPlayer        = dbi.getPlayer((int) UserPreferences.getMainUserId(context));
        actualScore         = shotsCount+2;
        actualShots         = shotsCount;
        actualPuts          = 2;
        actualPenaltyShots  = 0;

        /* Pridani hlavniho profilu mezi hrace */
        playerList = new ArrayList<>();
        playerList.add(0, dbi.getMainPlayer());
    }

    /** Pripojeni prvku GUI **/
    public void connectGui() {

        thScore         = (TabHost)  findViewById(R.id.HoleScore_tabHost_score);
        thPuts          = (TabHost)  findViewById(R.id.HoleScore_tabHost_put);
        thShots         = (TabHost)  findViewById(R.id.HoleScore_tabHost_shots);
        thPenaltyShots  = (TabHost)  findViewById(R.id.HoleScore_tabHost_penaltyShots);
        tvHole          = (TextView) findViewById(R.id.HoleScore_textView_hole);
        tvPlayer        = (TextView) findViewById(R.id.HoleScore_textView_player);
        trHole          = (TableRow) findViewById(R.id.HoleScore_tableRow_hole);
        trPlayer        = (TableRow) findViewById(R.id.HoleScore_tableRow_player);
    }

    /** Inicializace a naplneni hodnotami polozek "TabHost" **/
    public void initTabHosts() {

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

    /** Inicializace textovych poli vychozimi hodnotami **/
    public void initTextView() {
        tvHole  .setText(formatStringHole  (actualHole));
        tvPlayer.setText(formatStringPlayer(actualPlayer));
    }

    /** Formatovani textu pro vypis jedne jamky **/
    public String formatStringHole(Hole h) {
        return h.number + ". (" + h.getName() + ")";
    }

    /** Formatovani textu pro vypis jednoho hrace **/
    public String formatStringPlayer(Player p) {
        return p.getNickname() + " (" + p.getName() + " " + p.getSurname() + ")";
    }

    /** Reakce na kliknuti na textova pole **/
    public void clickHandlerTableRow() {

        trHole.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                HoleScoreHoleList.dialog(context,holeList).show();
            }
        });

        trPlayer.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                HoleScorePlayerList.dialog(context, playerList).show();
            }
        });
    }
}
