package cz.spurny.CreateGame;

/**
 * Objekt: SelectPlaymate.java
 * Popis:  Aktivita slouzici pro vyber spoluhracu v dane hre.
 * Autor:  Frantisek Spurny
 * Datum:  1.7.2015
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.spurny.Calculations.DistanceCalculations;
import cz.spurny.Calculations.ScoreCardCounting;
import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.GameCourse;
import cz.spurny.DatabaseInternal.GamePlayer;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.Dialogs.AddPlayer;
import cz.spurny.Game.SelectHole;
import cz.spurny.Toasts.GameCreatedSuccessfully;
import cz.spurny.Toasts.NotValidPlaymate;
import cz.spurny.Toasts.ToManyPlaymates;

public class SelectPlaymate extends ActionBarActivity {

    /* Prvky GUI */
    ListView lvPlayersList;
    Button   bCreateGame;
    EditText etFindNickname;

    /* Kontext */
    Context context;

    /* Seznam hracu */
    List<Player> players;

    /* Seznam vybranych spoluhracu */
    List<Boolean> isPlaymateList = new ArrayList<>();

    /* Adapter seznamu */
    SelectPlaymateAdapter adapter;

    /* Vybrane hriste a odpaliste */
    long idCourse1,idCourse2;
    long idTee1,idTee2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_playmate_layout);

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(this);

        /* Ziskani kontextu */
        context = this;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idCourse1 = iPrevActivity.getIntExtra("EXTRA_SELECT_PLAYMATE_ID_COURSE_1",-1);
        idCourse2 = iPrevActivity.getIntExtra("EXTRA_SELECT_PLAYMATE_ID_COURSE_2",-1);
        idTee1    = iPrevActivity.getIntExtra("EXTRA_SELECT_PLAYMATE_ID_TEE_1"   ,-1);
        idTee2    = iPrevActivity.getIntExtra("EXTRA_SELECT_PLAYMATE_ID_TEE_2"   ,-1);

        /* Pripojeni prvku GUI */
        lvPlayersList  = (ListView) findViewById(R.id.SelectPlaymate_listView_playersList);
        bCreateGame    = (Button)   findViewById(R.id.SelectPlaymate_button_createGame);
        etFindNickname = (EditText) findViewById(R.id.SelectPlaymate_editText_findNickname);

        /* Ziskani hodnot z databaze */
        players = dbi.getAllPlayers();

        /* Pridani tlacitka na konec seznamu, ktere slouzi pro pridani noveho hrace */
        View vAddPlayer = getLayoutInflater().inflate(R.layout.select_playmate_list_footer, null);
        lvPlayersList.addFooterView(vAddPlayer);

        /* Tvorba adapteru */
        adapter = new SelectPlaymateAdapter(this, players);

		/* Prirazeni adapteru */
        lvPlayersList.setAdapter(adapter);

        /* Funkcionalita EditText */
        searchEditText(etFindNickname);

        /* Raeakce na kliknuti na polozku seznamu */
        listClickHandler(dbi);

        /* Reakce na klinuti na tlacitko vytvor hru */
        createGameButtonHandler(dbi);

        /* Inicializace pole uchovavajiciho kdo je spoluhracem */
        initIsPlayerList();

        /* Odpojeni databaze */
        dbi.close();
    }

    /** Reakce na kliknuti na tlacitko "Vytvor hru" **/
    public void createGameButtonHandler(final DatabaseHandlerInternal dbi) {

        bCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame(dbi);
            }
        });

    }

    /** Tvorba hry na zaklade zadanych parametru **/
    public void createGame(DatabaseHandlerInternal dbi) {

        Game       game;
        long       gameId;
        GamePlayer gamePlayer;
        GameCourse gameCourse;


        /* Tvorba nove hry */
        game = new Game();
        game.setDate(getCurrentDate());
        game.setCourseToughness(0);
        game.setDescription("");
        game.setWeather(0);
        game.setWind(0);
        game.setWindPower(0);

        gameId = dbi.createGame(game);

        game.setId((int)gameId);

        /* Ulozeni zvolenych spoluhracu */
        for (int i = 0;i<isPlaymateList.size();i++) {
            if (isPlaymateList.get(i)) {
                gamePlayer = new GamePlayer();
                gamePlayer.setIdGame  ((int)gameId);
                gamePlayer.setIdPlayer(players.get(i).getId());

                dbi.createGamePlayer  (gamePlayer);
            }
        }

        /* Ulozeni zvoleneho hriste/hrist a odpaliste/odpalist */
        gameCourse = new GameCourse();
        gameCourse.setIdGame  ((int)gameId);
        gameCourse.setIdCourse((int)idCourse1);
        gameCourse.setIdTee   ((int)idTee1);

        dbi.createGameCourse(gameCourse);

        /* Pokud uzivatel zvolil 2 hriste vlozime do databaze i druhe hriste */
        if (idCourse2 != -1) {
            gameCourse = new GameCourse();
            gameCourse.setIdGame  ((int)gameId);
            gameCourse.setIdCourse((int)idCourse2);
            gameCourse.setIdTee   ((int)idTee2);

            dbi.createGameCourse(gameCourse);
        }

        /* Informovani o uzivatele o vytvoreni nove hry */
        GameCreatedSuccessfully.getToast(context).show();

        /* Vypocet vzdalenosti jednotlivych jamek
        *  k vypoctu dochazi zde, aby se neopakoval */
        List<Double> holeLengthList = DistanceCalculations.getLenghtOfAllHoles((int) gameId, context);
        double[] holeLengthArray = new double[holeLengthList.size()];
        for(int i = 0; i < holeLengthList.size(); i++) holeLengthArray[i] = holeLengthList.get(i);

        /* Vypocet osobniho paru na jednotlivych jamkach */
        int[] personalPar = ScoreCardCounting.countPersonalPar((int)idTee1,dbi.getMainPlayer(),game,context);

        /* Spusteni aktivity Vyber jamky */
        goToSelectHole(gameId, holeLengthArray, personalPar);
    }

    /** Spusteni aktivity "Vyber jamky" **/
    public void goToSelectHole(long gameId,double[] holeLengthArray,int[] personalPar) {
        Intent iSelectHole = new Intent(this,SelectHole.class);
        iSelectHole.putExtra("EXTRA_SELECT_HOLE_IDGAME",gameId);
        iSelectHole.putExtra("EXTRA_SELECT_HOLE_LENGHT_ARRAY",holeLengthArray);
        iSelectHole.putExtra("EXTRA_SELECT_HOLE_PERSONAL_PAR",personalPar);
        this.startActivity(iSelectHole);
    }

    /** Ziskani aktualniho data **/
    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c.getTime());
    }

    /** Reakce na pokliknuti na polozku seznam **/
    public void listClickHandler(final DatabaseHandlerInternal dbi) {

        lvPlayersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int arg2,long arg3) {

                /* Pridani noveho hrace */
                if (arg2 == players.size()) {
                    AddPlayer.dialog(context,adapter,players,isPlaymateList).show();
                }
                /* Hlavni profil - je vzdy vybran */
                else if (arg2 == 0){
                    NotValidPlaymate.getToast(context).show();
                }
                /* Vyber spoluhrace */
                else {
                    AddPlaymate(arg1, arg2);
                }

            }
        });

    }

    /** Reakce na kliknuti na polozku seznamu - pridani/odebrani spoluhrace **/
    public void AddPlaymate(View listItem,int index) {

        /* Tento spoluhrac ma byt odebran */
        if (isPlaymateList.get(index)) {
            listItem.setBackgroundColor(Color.TRANSPARENT);
            isPlaymateList.set(index,false);
        /* Tento hrac je novy spoluhrac */
        } else {

            /* Je mozne zvolit maximalne 4 hrace */
            if (numOfPlaymates(isPlaymateList) >= 3) { // 3 + hlavni profil
                ToManyPlaymates.getToast(context).show();
                return;
            }


            listItem.setBackgroundColor(Color.parseColor("#A3E0FF"));
            isPlaymateList.set(index,true);
        }
    }

    /** Implementace filtrovani polde prezdivky hrace **/
    public void searchEditText(EditText etSearch) {
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                /* Uzivatel zmeni text - na tuto zmenu reagujeme */
                SelectPlaymate.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // Neiplmentovano

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // Neiplmentovano
            }
        });
    }

    /** Incializace listu uchovavajiciho kdo je spoluhracem **/
    public void initIsPlayerList() {
        for (int i = 0;i < players.size();i++) {
            isPlaymateList.add(false);
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /** Zjisti pocet hracu ktery jsou vybrani jako spoluhraci **/
    public int numOfPlaymates (List<Boolean> list) {

        int count = 0;
        for (Boolean b : list) {
            if (b) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
