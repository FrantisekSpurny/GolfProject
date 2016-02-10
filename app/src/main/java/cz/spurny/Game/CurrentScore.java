package cz.spurny.Game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import cz.spurny.Calculations.ScoreCard;
import cz.spurny.Calculations.ScoreCardCounting;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.Dialogs.CurrentScorePlayerList;
import cz.spurny.Toasts.NoPlaymates;

public class CurrentScore extends ActionBarActivity {

    /*** ATRIBUTY ***/

    /** Kontext **/
    Context context;

    /** Kontext hry **/
    Game   game;
    Player player;

    /** Databaze **/
    DatabaseHandlerInternal dbi;
    DatabaseHandlerResort   dbr;

    /** Prvky GUI **/
    TextView tvHolesPlayed;
    TextView tvPlayer;
    TextView tvShotsCount;
    TextView tvStableford;
    TextView tvEagleBirdiePar;
    TextView tvBoogieOthers;

    /*** ZIVOTNI CYKLUS AKTIVITY ***/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_score_layout);

        /* Ziskani kontextu */
        context = this;

        /* Inicializace */
        init();

        /* Zobrazeni prubezneho skore */
        displayScore(ScoreCardCounting.countScorecard(game, dbi.getMainPlayer(), dbi, dbr,context));
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

    /** Inicializace aktivity **/
    public void init() {

        /* Pripojeni databaze */
        dbi = new DatabaseHandlerInternal(context);
        dbr = new DatabaseHandlerResort  (context);

        /* Ziskani hodnot z predchozi aktivity */
        getExtras();

        /* Pripojeni prvku GUI */
        connectGui();

        /* Vytvoreni podtitlku */
        setSubtitle();
    }

    /** Ziskani hodnot z volajici aktivity **/
    public void getExtras() {

        int idGame;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idGame = iPrevActivity.getIntExtra("EXTRA_CURRENT_SCORE_IDGAME", -1);
        game   = dbi.getGame(idGame);
        player = dbi.getMainPlayer();
    }

    /** Pripojeni prvku GUI **/
    public void connectGui() {

        tvHolesPlayed    = (TextView) findViewById(R.id.CurrentScore_textView_holesPlayed);
        tvPlayer         = (TextView) findViewById(R.id.CurrentScore_textView_player);
        tvShotsCount     = (TextView) findViewById(R.id.CurrentScore_textView_shotsCount);
        tvStableford     = (TextView) findViewById(R.id.CurrentScore_textView_stableford);
        tvEagleBirdiePar = (TextView) findViewById(R.id.CurrentScore_textView_eagleBiridiePar);
        tvBoogieOthers   = (TextView) findViewById(R.id.CurrentScore_textView_boogieOther);
    }

    /** Zobrazeni prubezneho skore **/
    public void displayScore(ScoreCard scoreCard) {

        /* Pocet zahranych jamek */
        tvHolesPlayed.setText(String.valueOf(ScoreCardCounting.playedHoles(scoreCard)));

        /* Hrac */
        tvPlayer.setText(player.getNickname());

        /* Pocet ran */
        tvShotsCount.setText(scoreCard.getSumScore() + " / " + String.valueOf(scoreCard.getSumParDeviation()));

        /* Stableford body */
        tvStableford.setText(String.valueOf(scoreCard.getSumStableford()));

        /* Eagle Birdie Par */
        tvEagleBirdiePar.setText(ScoreCardCounting.eagleCount(scoreCard)  + " / " +
                                 ScoreCardCounting.birdieCount(scoreCard) + " / " +
                                 ScoreCardCounting.parCount(scoreCard));

        tvBoogieOthers.setText(ScoreCardCounting.boogieCount(scoreCard) + " / " +
                ScoreCardCounting.boogie2Count(scoreCard) + " / " +
                ScoreCardCounting.othersCount(scoreCard));
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

    /** Formatovani textu pro vypis jednoho hrace **/
    public String formatStringPlayer(Player p) {
        return p.getNickname() + " (" + p.getName() + " " + p.getSurname() + ")";
    }

    /*** MENU ***/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.current_score_menu, menu);
        return true;
    }

    /** Reakce na kliknuti na polozku menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.CurrentScoreMenu_item_player: // Zmena hrace

                if (dbi.getAllPlaymatesOfGame(game.getId()).size() == 0) {
                    NoPlaymates.getToast(context).show();
                    return true;
                }

                CurrentScorePlayerList.dialog(context, dbi.getAllPlaymatesOfGame(game.getId()), game, dbi, dbr).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*** GETTERS AND SETTERS ***/
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

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

    public TextView getTvHolesPlayed() {
        return tvHolesPlayed;
    }

    public void setTvHolesPlayed(TextView tvHolesPlayed) {
        this.tvHolesPlayed = tvHolesPlayed;
    }

    public TextView getTvPlayer() {
        return tvPlayer;
    }

    public void setTvPlayer(TextView tvPlayer) {
        this.tvPlayer = tvPlayer;
    }

    public TextView getTvShotsCount() {
        return tvShotsCount;
    }

    public void setTvShotsCount(TextView tvShotsCount) {
        this.tvShotsCount = tvShotsCount;
    }

    public TextView getTvStableford() {
        return tvStableford;
    }

    public void setTvStableford(TextView tvStableford) {
        this.tvStableford = tvStableford;
    }

    public TextView getTvEagleBirdiePar() {
        return tvEagleBirdiePar;
    }

    public void setTvEagleBirdiePar(TextView tvEagleBirdiePar) {
        this.tvEagleBirdiePar = tvEagleBirdiePar;
    }

    public TextView getTvBoogieOthers() {
        return tvBoogieOthers;
    }

    public void setTvBoogieOthers(TextView tvBoogieOthers) {
        this.tvBoogieOthers = tvBoogieOthers;
    }

}
