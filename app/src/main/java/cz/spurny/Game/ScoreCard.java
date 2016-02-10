package cz.spurny.Game;

/**
 * Objekt: ScoreCard.java
 * Popis:  Zobrazeni Skore karty.
 * Autor:  Frantisek Spurny
 * Datum:  12.08.2015
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import cz.spurny.Calculations.ScoreCardCounting;
import cz.spurny.Calculations.ScoreCardLine;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.Course;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;
import cz.spurny.DatabaseResort.Tee;
import cz.spurny.Dialogs.CaptureShotPointSelectionMethod;
import cz.spurny.Dialogs.CurrentScorePlayerList;
import cz.spurny.Dialogs.DisplayScoreCardAbout;
import cz.spurny.Dialogs.ScoreCardPlayerList;
import cz.spurny.Toasts.NoPlaymates;

public class ScoreCard extends ActionBarActivity {

    /*** ATRIBUTY ***/

    /** Kontext Hry */
    Game   game;
    Player player;
    List<Hole> holes;

    /** Prvky GUI **/
    TableLayout tlScoreCard;

    /** Context **/
    Context context;

    /** Databaze **/
    DatabaseHandlerInternal dbi;
    DatabaseHandlerResort   dbr;

    /*** ZIVOTNI CYKLUS ***/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_card_layout);

        context = this;

        /* Inicializace aktivity */
        init();

        /* Zobrazeni skore karty */
        displayScoreCard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.score_card_menu, menu);

        return true;
    }

    /** Reakce na ukonceni aktivity **/
    @Override
    protected void onStop() {
        super.onStop();

        /* Uzavzeni databaze */
        dbr.close();
        dbi.close();
    }

    /** Reakce na kliknuti na polozku menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ScoreCardMenu_item_about: // Legenda
                DisplayScoreCardAbout.dialog(context).show();
                return true;
            case R.id.ScoreCardMenu_item_player: // Zmena hrace

                if (dbi.getAllPlaymatesOfGame(game.getId()).size() == 0) {
                    NoPlaymates.getToast(context).show();
                    return true;
                }

                ScoreCardPlayerList.dialog(context, dbi.getAllPlaymatesOfGame(game.getId())).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*** INICIALIZACE ***/

    /** Inicializace aktivity **/
    public void init() {

        /* Pripojeni databaze */
        dbi = new DatabaseHandlerInternal(context);
        dbr = new DatabaseHandlerResort(context);

        /* Prevzani hodnot z prechozi aktivity */
        getExtra();

        /* Nastaveni podtitlku */
        setSubtitle();

        /* Pripojeni prvku GUI */
        conectGui();
    }

    /** Prevzeti hodnot z prechozi aktivity **/
    public void getExtra() {
        int idGame;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idGame          = iPrevActivity.getIntExtra("EXTRA_SCORE_CARD_IDGAME", -1);

        game   = dbi.getGame(idGame);
        holes  = dbi.getAllHolesOfGame(idGame);
        player = dbi.getMainPlayer();
    }

    /** Zobrazeni podtitlku aktivity **/
    public void setSubtitle() {

        String subtitle = dbi.getResortOfGame    (game.getId()).getName(); // resort

        if (dbi.getNumOfGameCourses(game.getId())==1)
            subtitle += " " + dbr.getCourse(dbi.getAllGameCourseOfGame(game.getId()).get(0).getIdCourse()).getName();
        else
            subtitle += " " + dbr.getCourse(dbi.getAllGameCourseOfGame(game.getId()).get(0).getIdCourse()).getName() + " - " +
                    dbr.getCourse(dbi.getAllGameCourseOfGame(game.getId()).get(1).getIdCourse()).getName();

        /* Nastaveni podtitulku */
        getSupportActionBar().setSubtitle(subtitle);
    }

    /** Pripojeni prvku GUI **/
    public void conectGui() {

        tlScoreCard = (TableLayout) findViewById(R.id.ScoreCard_tablelayout_scoreCard);

    }

    /*** SCORE KARTA ***/

    /** Inicializace hodnot / zobrazeni skore karty **/
    public void displayScoreCard() {

        /* Vypocet skore */
        cz.spurny.Calculations.ScoreCard scoreCard = ScoreCardCounting.countScorecard(game,player,dbi,dbr,context);
        List<ScoreCardLine> lines = scoreCard.getLines();

        /** Prochazeni vsech polozek tabulky **/
        for (int i = 0, j = tlScoreCard.getChildCount(); i < j; i++) {

            /* Preskoceni prvniho radku */
            if (i == 0) continue;

            /* Ziskani pohledu radku */
            View child = tlScoreCard.getChildAt(i);

            /* Kontrola zdali je dany radek opravdu instanci "TableRow" */
            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                /** Vyplneni hodnot v radku **/
                if (i <= 18)
                    scoreCardRow(row,lines.get(i-1),holes.get(i-1));
                /** Vyplneni radku sumarizace **/
                else
                    scoreCardSummary(row,scoreCard);
            }
        }
    }

    /** Vyplneni hodnot jednoho radku Score karty **/
    public void scoreCardRow(TableRow row, ScoreCardLine scl,Hole hole) {

        /* Projdeme pres vsechny "TextView" v radku */
        for (int i = 0; i < row.getChildCount(); i++) {

            /* Ziskani "TextView" */
            View view = row.getChildAt(i);
            TextView tv = (TextView) view;

            switch (i) {
                case 0: // jamka
                    tv.setText(String.valueOf(hole.getNumber()) + ".");
                    break;
                case 1: // par
                    tv.setText(String.valueOf(scl.getPar()));
                    break;
                case 2: // hcp
                    tv.setText(String.valueOf(hole.getHcp()));
                    break;
                case 3: // os. par
                    tv.setText(String.valueOf(scl.getPersonalPar()));
                    break;
                case 4: // vysledek
                    if (scl.getScore() == 0)
                        tv.setText("-");
                    else
                        tv.setText(String.valueOf(scl.getScore()));
                    tv.setBackground(formatStringScore(scl.getScore(),scl.getPar()));
                    break;
                case 5: // stable
                    tv.setText(String.valueOf(scl.getStableford()));
                    break;
            }
        }
    }

    /** Vyplneni hodnot sumarizace Score karty **/
    public void scoreCardSummary(TableRow row, cz.spurny.Calculations.ScoreCard sc) {

        /* Projdeme pres vsechny "TextView" v radku */
        for (int i = 0; i < row.getChildCount(); i++) {

            /* Ziskani "TextView" */
            View view = row.getChildAt(i);
            TextView tv = (TextView) view;

            switch (i) {
                case 0: // jamka
                    tv.setText(context.getString(R.string.ScoreCard_string_summary));
                    break;
                case 1: // par
                    tv.setText(String.valueOf(sc.getSumPar()));
                    break;
                case 2: // hcp
                    tv.setText("-");
                    break;
                case 3: // os. par
                    tv.setText(String.valueOf(sc.getSumPersonalPar()));
                    break;
                case 4: // vysledek
                    tv.setText(String.valueOf(sc.getSumScore()));
                    break;
                case 5: // stable
                    tv.setText(String.valueOf(sc.getSumStableford()));
                    break;
            }
        }
    }


    /** Naformatovani retezce zobrazujiciho skore **/
    public Drawable formatStringScore(int score,int par) {

        if (score == 1) // hole in one
            return getResources().getDrawable(R.drawable.hole_in_one);

        if (score == 0) // nezahrano
            return getResources().getDrawable(R.drawable.not_set);

        switch(score-par) {
            case 0: //Par
                return getResources().getDrawable(R.drawable.par);
            case -1: //Birdie
                return getResources().getDrawable(R.drawable.birdie);
            case -2: //Eagle
                return getResources().getDrawable(R.drawable.eagle);
            case -3: //Albatros
                return getResources().getDrawable(R.drawable.albatros);
            case 1:  //Bogey
                return getResources().getDrawable(R.drawable.bogey);
            case 2:  //2 Bogey
                return getResources().getDrawable(R.drawable.double_bogey);
            case 3:  //3 Bogey
                return getResources().getDrawable(R.drawable.triple_bogey);
            default: //jine
                return getResources().getDrawable(R.drawable.other);
        }
    }

    /** Formatovani textu pro vypis jednoho hrace **/
    public String formatStringPlayer(Player p) {
        return p.getNickname() + " (" + p.getName() + " " + p.getSurname() + ")";
    }

    /*** GETTERS AND SETTERS ***/
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public void setHoles(List<Hole> holes) {
        this.holes = holes;
    }

    public TableLayout getTlScoreCard() {
        return tlScoreCard;
    }

    public void setTlScoreCard(TableLayout tlScoreCard) {
        this.tlScoreCard = tlScoreCard;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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
}
