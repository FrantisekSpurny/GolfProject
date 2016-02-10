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
import android.widget.Button;
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
import cz.spurny.DatabaseInternal.Score;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.Dialogs.ApplicationTerminate;
import cz.spurny.Dialogs.HoleScoreHoleList;
import cz.spurny.Dialogs.HoleScorePlayerList;
import cz.spurny.Dialogs.MeasureDrawPointSelectionMethod;
import cz.spurny.Dialogs.ScoreNotSaved;
import cz.spurny.Dialogs.UpdateScore;
import cz.spurny.Settings.UserPreferences;
import cz.spurny.Toasts.ScoreSaved;

public class HoleScore extends ActionBarActivity {

    /*** KONSTANTY ***/
    public final int SCORE_SAVED     = 1;
    public final int SCORE_NOT_SAVED = 0;

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
    int          actualPenaltyShots;

    /** Prvky GUI **/
    TabHost  thScore;
    TabHost  thPuts;
    TabHost  thPenaltyShots;
    TextView tvHole;
    TextView tvNickname;
    TextView tvName;
    TableRow trHole;
    TableRow trNickname;
    TableRow trName;
    Button   bSaveScore;

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

        /* Reakce na zmenu polozky "Tabhost" */
        tabHostHandler();

        /* Reakce na kliknuti na tlacitko "ulozit vysledek" */
        saveScoreButton();
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

        /* Defaultni hodnota vylsedku aktivity */
        setResult(SCORE_NOT_SAVED);

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

        /* Nastaveni pocatecnich hodnot */
        initValues();
    }

    /** Prevzeti hodnot z volajici aktivity a ziskani kontextu **/
    public void getExtras() {

        int idGame,idHole,shotsCount,penaltyShots;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idGame               = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_IDGAME", -1);
        idHole               = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_IDHOLE", -1);
        shotsCount           = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_NUM_OF_SHOTS", -1);
        penaltyShots         = iPrevActivity.getIntExtra("EXTRA_HOLE_SCORE_PENALTY_SHOTS", -1);

        /* Naplneni atributu */
        game                = dbi.getGame(idGame);
        actualHole          = dbr.getHole(idHole);
        holeList            = dbi.getAllHolesOfGame(idGame);
        playerList          = dbi.getAllPlaymatesOfGame(idGame);
        actualPlayer        = dbi.getPlayer((int) UserPreferences.getMainUserId(context));
        actualScore         = shotsCount+ 2 + penaltyShots;
        actualPuts          = 2;
        actualPenaltyShots  = penaltyShots;
    }

    /** Pripojeni prvku GUI **/
    public void connectGui() {

        thScore         = (TabHost)  findViewById(R.id.HoleScore_tabHost_score);
        thPuts          = (TabHost)  findViewById(R.id.HoleScore_tabHost_put);
        thPenaltyShots  = (TabHost)  findViewById(R.id.HoleScore_tabHost_penaltyShots);
        tvHole          = (TextView) findViewById(R.id.HoleScore_textView_hole);
        tvNickname      = (TextView) findViewById(R.id.HoleScore_textView_nickname);
        tvName          = (TextView) findViewById(R.id.HoleScore_textView_name);
        trHole          = (TableRow) findViewById(R.id.HoleScore_tableRow_hole);
        trNickname      = (TableRow) findViewById(R.id.HoleScore_tableRow_player);
        trName          = (TableRow) findViewById(R.id.HoleScore_tableRow_name);
        bSaveScore      = (Button)   findViewById(R.id.HoleScore_button_saveScore);
    }

    /** Inicializace a naplneni hodnotami polozek "TabHost" **/
    public void initTabHosts() {

        /* Skore */
        initTabHost(getScoreNamesList(1, 18)  ,getNumberList(1,18)  ,thScore);

        /* Paty */
        initTabHost(getNumberList(0, 6), getNumberList(0, 6), thPuts);

        /* Trestne rany */
        initTabHost(getNumberList(0,5) ,getNumberList(0,5)  ,thPenaltyShots);
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
    public List<String> getScoreNamesList(int n,int m) {
        List<String> scoreList = new ArrayList<>();

        for (int i = n; i <= m; i++) {
            scoreList.add(String.valueOf(i) + scoreName(i));
        }

        return scoreList;
    }

    /* Textovy popis hodnoty score */
    public String scoreName(int score) {

        int par = actualHole.getPar();

        switch(score-par) {
            case 0: //Par
                return " [" + getString(R.string.DisplayScoreCardAbout_string_par) + "]";
            case -1: //Birdie
                return " [" + getString(R.string.DisplayScoreCardAbout_string_birdie) + "]";
            case -2: //Eagle
                return " [" + getString(R.string.DisplayScoreCardAbout_string_eagle) + "]";
            case -3: //Albatros
                return " [" + getString(R.string.DisplayScoreCardAbout_string_albatros) + "]";
            case 1:  //Bogey
                return " [" + getString(R.string.DisplayScoreCardAbout_string_bogey) + "]";
            case 2:  //2 Bogey
                return " [" + getString(R.string.DisplayScoreCardAbout_string_doubleBogey) + "]";
            default: //jine
                return "";
        }
    }

    /** Tvorba seznamu obsahujici ciselne retezce od 1 do n **/
    public List<String> getNumberList(int n,int m) {
        List<String> numberList = new ArrayList<>();

        for (int i = n; i <= m; i++) {
            numberList.add(String.valueOf(i));
        }

        return numberList;
    }

    /** Inicializace textovych poli vychozimi hodnotami **/
    public void initTextView() {
        tvHole    .setText(formatStringHole  (actualHole));
        tvNickname.setText(actualPlayer.getNickname());
        tvName    .setText(actualPlayer.getName() + " " + actualPlayer.getSurname());
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
                HoleScoreHoleList.dialog(context, holeList).show();
            }
        });

        trNickname.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                HoleScorePlayerList.dialog(context, playerList).show();
            }
        });

        trName.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                HoleScorePlayerList.dialog(context, playerList).show();
            }
        });
    }

    /** Incializace zadanych hodnot na dane jamkck a pro daneho hrace **/
    public void initValues() {

        Score score = dbi.getScore(actualHole.getId(),actualPlayer.getId(),game.getId());

        thScore       .setCurrentTab(actualScore-1);
        thPuts        .setCurrentTab(actualPuts);
        thPenaltyShots.setCurrentTab(actualPenaltyShots);
    }

    /*** REAKCE NA ZMENY ***/

    /** Reakce na zmeny zvolenych hodnot v "TabHost" polozkach **/
    public void tabHostHandler() {

        /* Zmena skore */
        thScore.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                actualScore = Integer.valueOf(tabId);
                scoreChangeHandler();
            }
        });

        /* Zmena skore */
        thPuts.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                actualPuts = Integer.valueOf(tabId);
                putsChangeHandler();
            }
        });

        /* Zmena skore */
        thPenaltyShots.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                actualPenaltyShots = Integer.valueOf(tabId);
                penaltyShotsChangeHandler();
            }
        });
    }

    /** Reakce na zmenu skore **/
    public void scoreChangeHandler() {

        /* Vypocet noveho poctu trestnych bodu a patu */
        while(actualPuts + actualPenaltyShots + 1 > actualScore ) {
                actualPuts = actualPuts > 0 ? actualPuts - 1 : actualPuts;

            if (actualPuts + actualPenaltyShots + 1 <= actualScore)
                continue;

            actualPenaltyShots = actualPenaltyShots > 0 ? actualPenaltyShots - 1 : actualPenaltyShots;
       }

        /* Zmena na naaktualni hodnoty */
        thPuts        .setCurrentTab(actualPuts);
        thPenaltyShots.setCurrentTab(actualPenaltyShots);
    }

    /** Reakce na zmenu poctu patu **/
    public void putsChangeHandler() {
        while (actualScore <= actualPuts + actualPenaltyShots) {
            actualScore++;
        }
        thScore.setCurrentTab(actualScore-1);
    }

    /** Reakce na zmenu poctu trestnych ran **/
    public void penaltyShotsChangeHandler() {
        while (actualScore <= actualPuts + actualPenaltyShots) {
            actualScore++;
        }
        thScore.setCurrentTab(actualScore-1);
    }

    /*** ULOZENI SKORE ***/

    /** Reakce na poklik na tlacitko "uloz skore" **/
    public void saveScoreButton() {
        bSaveScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScore();
            }
        });
    }

    /** Ulozeni skore jamky **/
    public void saveScore() {

        Score score = dbi.getScore(actualHole.getId(),actualPlayer.getId(),game.getId());

        /* Pokud uz bylo skore zadano - dotaz na aktualizaci */
        if (score != null) {
            score = new Score(score.getId(),game.getId(),actualHole.getId(),actualPlayer.getId(),actualScore,actualPuts,actualPenaltyShots);
            UpdateScore.dialog(context, score, dbi).show();
        } else {
            score = new Score(game.getId(),actualHole.getId(),actualPlayer.getId(),actualScore,actualPuts,actualPenaltyShots);
            dbi.createScore(score);

            /* Informovani uzivatele o ulozeni skore */
            ScoreSaved.getToast(context).show();

            /* Nastaveni vysledku aktivity */
            setResult(SCORE_SAVED);
        }
    }

    /** Reakce na zmacknuti tlacitka "zpet" - dotaz na ulozeni **/
    @Override
    public void onBackPressed()
    {
        if (dbi.getScore(actualHole.getId(),actualPlayer.getId(),game.getId()) == null) {
            Score score = new Score(game.getId(),
                    actualHole.getId(),
                    actualPlayer.getId(),
                    actualScore,
                    actualPuts,
                    actualPenaltyShots);


            ScoreNotSaved.dialog(context,score,dbi).show();

        } else
            this.finish();
    }


    /*** GETTERS AND SETTERS ***/
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Hole> getHoleList() {
        return holeList;
    }

    public void setHoleList(List<Hole> holeList) {
        this.holeList = holeList;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public Hole getActualHole() {
        return actualHole;
    }

    public void setActualHole(Hole actualHole) {
        this.actualHole = actualHole;
    }

    public Player getActualPlayer() {
        return actualPlayer;
    }

    public void setActualPlayer(Player actualPlayer) {
        this.actualPlayer = actualPlayer;
    }

    public int getActualScore() {
        return actualScore;
    }

    public void setActualScore(int actualScore) {
        this.actualScore = actualScore;
    }

    public int getActualPuts() {
        return actualPuts;
    }

    public void setActualPuts(int actualPuts) {
        this.actualPuts = actualPuts;
    }

    public int getActualPenaltyShots() {
        return actualPenaltyShots;
    }

    public void setActualPenaltyShots(int actualPenaltyShots) {
        this.actualPenaltyShots = actualPenaltyShots;
    }

    public DatabaseHandlerInternal getDbi() {
        return dbi;
    }

    public void setDbi(DatabaseHandlerInternal dbi) {
        this.dbi = dbi;
    }

    public DatabaseHandlerResort getDbr() {
        return dbr;
    }

    public void setDbr(DatabaseHandlerResort dbr) {
        this.dbr = dbr;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
