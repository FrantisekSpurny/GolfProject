package cz.spurny.Game;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;

public class RecordGame extends ActionBarActivity {

    /*** ATRIBUTY ***/

    /** Kontext **/
    Context context;

    /** Kontext Hry **/
    Game game;

    /** Databaze **/
    DatabaseHandlerInternal dbi;

    /** Prvky GUI **/
    EditText etNote;
    TabHost  thWeather;
    TabHost  thWindSpecification;
    TabHost  thWindSpeed;
    TabHost  thCourseRoughness;

    /*** ZIVOTNI CYKLUS ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_game_layout);

        /* Ziskani kontextu */
        context = this;

        /* Inicializace aktivity */
        init();
    }

    /** Reakce na ukonceni aktivity **/
    @Override
    protected void onStop() {
        super.onStop();

        /* Uzavzeni databaze */
        dbi.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /*** INICIALIZACE ***/

    /** Inicializace aktivity **/
    public void init() {

        /* Pripojeni databaze */
        dbi = new DatabaseHandlerInternal(context);

        /* Ziskani hodnot z predchozi aktivity */
        getExtra();

        /* Pripojeni prvku GUI */
        connectGui();

        /* Inicializace "TabHost" polozek */
        initTabHosts();
    }

    /** Prevzeti hodnot z volajici aktivity **/
    public void getExtra() {

        int idGame;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idGame               = iPrevActivity.getIntExtra("EXTRA_RECORD_GAME_IDGAME", -1);
        game                 = dbi.getGame(idGame);
    }

    /** Pripojeni prvku GUI **/
    public void connectGui() {
        etNote              = (EditText) findViewById(R.id.RecordGame_editText_note);
        thWeather           = (TabHost)  findViewById(R.id.RecordGame_tabHost_weather);
        thWindSpecification = (TabHost)  findViewById(R.id.RecordGame_tabHost_windSpecification);
        thWindSpeed         = (TabHost)  findViewById(R.id.RecordGame_tabHost_windSpeed);
        thCourseRoughness   = (TabHost)  findViewById(R.id.RecordGame_tabHost_courseRoughness);
    }

    /** Inicializace "TabHost" polozek **/
    public void initTabHosts() {

        /* Pocasi */
        initImageTabHost(getNumberList(0, 4), thWeather, Weather.getDrawableList(context));

        /* Smer vetru */
        initImageTabHost(getNumberList(0, 7), thWindSpecification, Wind.getDrawableList(context));

        /* Sila vetru */
        initTabHost(getNumberList(0,5),getNumberList(0,5), thWindSpeed);

        /* Tvrdost hriste */
        initTabHost(getNumberList(-2,2),getNumberList(0,5), thCourseRoughness);
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
                    return (new TextView(RecordGame.this));
                }
            });

            /* Nastaveni popisku */
            spec.setIndicator(namesList.get(i));

            /* Pridani "Tabu" */
            tabHost.addTab(spec);
        }
    }

    /** Inicializace "TabHost" prvku **/
    public void initImageTabHost(List<String> indexList,
                                 TabHost tabHost,
                                 List<Drawable> drawables) {

        /* Odstraneni vsech predeslych hodnot */
        if (tabHost.getTabWidget() != null && tabHost.getTabWidget().getTabCount() > 0)
            tabHost.clearAllTabs();

        /* inicializace */
        tabHost.setup();

        TabHost.TabSpec spec;

        for (int i = 0; i < indexList.size(); i++) {

            /* Nastaveni identifikatoru */
            spec = tabHost.newTabSpec(indexList.get(i));

            /* Nastaveni obsahu */
            spec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return (new TextView(RecordGame.this));
                }
            });

            /* Nastaveni Obrazku a textu */
            View tabIndicator = LayoutInflater.from(this).inflate(R.layout.image_tab_layout, tabHost.getTabWidget(), false);
            ((ImageView) tabIndicator.findViewById(R.id.icon)).setImageDrawable(drawables.get(i));

            spec.setIndicator(tabIndicator);

            /* Pridani "Tabu" */
            tabHost.addTab(spec);
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
}
