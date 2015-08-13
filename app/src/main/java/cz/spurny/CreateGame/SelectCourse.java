package cz.spurny.CreateGame;

/**
 * Objekt: SelectCourse.java
 * Popis:  Aktivita slouzici pro vyber hriste a odpaliste v ramci zvoleneho resortu
 * Autor:  Frantisek Spurny
 * Datum:  18.6.2015
 **/

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.spurny.DatabaseResort.Course;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Tee;

public class SelectCourse extends ActionBarActivity {

    /* Id zvoleneho resortu */
    int idResort;

    /* Kontext */
    Context context;

    /* Prvky GUI */
    Button   bCreateGame;
    TabHost  thCourse1;
    TabHost  thCourse2;
    TabHost  thTee1;
    TabHost  thTee2;
    TableRow trTitleCourse2;
    TableRow trTabHostCourse2;
    TableRow trTitleTee2;
    TableRow trTabHostTee2;

    /* Vybrana hriste a odpaliste */
    Course selectedCourse1;
    Course selectedCourse2;
    Tee    selectedTee1;
    Tee    selectedTee2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_course_layout);

        /* Ziskani kontextu */
        context = this;

        /* Prevzeti hodnot z predchozi aktivity */
        Intent iPrevActivity = getIntent();
        idResort = iPrevActivity.getIntExtra("EXTRA_SELECT_COURSE_IDRESORT",-1);

        /* Pripojeni databaze */
        DatabaseHandlerResort dbr = new DatabaseHandlerResort(this);
        try { dbr.openDataBase(); } catch(SQLException exception){ /**TODO**/ }

        /* Pripojeni prvku GUI */
        bCreateGame      = (Button)   findViewById(R.id.SelectCourse_button_CreateGame);
        thCourse1        = (TabHost)  findViewById(R.id.SelectCourse_tabHost_course1);
        thCourse2        = (TabHost)  findViewById(R.id.SelectCourse_tabHost_course2);
        thTee1           = (TabHost)  findViewById(R.id.SelectCourse_tabHost_tee1);
        thTee2           = (TabHost)  findViewById(R.id.SelectCourse_tabHost_tee2);
        trTabHostCourse2 = (TableRow) findViewById(R.id.SelectCourse_tableRow_tabHost3);
        trTabHostTee2    = (TableRow) findViewById(R.id.SelectCourse_tableRow_tabHost4);
        trTitleCourse2   = (TableRow) findViewById(R.id.SelectCourse_tableRow_Course2);
        trTitleTee2      = (TableRow) findViewById(R.id.SelectCourse_tableRow_Tee2);

        /* Inicializace hodnot */
        selectedCourse1 = null;
        selectedCourse2 = null;
        selectedTee1    = null;
        selectedTee2    = null;

        /* inicializace "TabHost" prvniho hriste */
        initCourse1(dbr);
        tabChangeHandlerCourse1(dbr);

        /* Reakce na zmenu "tabu" v libovolnem "TabHost" */
        tabChangeHandler(dbr,thCourse1,thCourse2,thTee1,thTee2);

        /* Reakce na tlacitko "vytvor hru" */
        bCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame();
            }
        });

        /* Uzavreni databaze */
        dbr.closeDatabase();
    }

    /** Tvorba nove hry na zaklade zadanych parametru **/
    public void createGame() {

        Intent iSelectPlaymate = new Intent(this,SelectPlaymate.class);
        iSelectPlaymate.putExtra("EXTRA_SELECT_PLAYMATE_ID_COURSE_1",selectedCourse1.getId());
        iSelectPlaymate.putExtra("EXTRA_SELECT_PLAYMATE_ID_TEE_1"   ,selectedTee1.getId());
        iSelectPlaymate.putExtra("EXTRA_SELECT_PLAYMATE_ID_COURSE_2",selectedCourse2==null?-1:selectedCourse2.getId());
        iSelectPlaymate.putExtra("EXTRA_SELECT_PLAYMATE_ID_TEE_2"   ,selectedTee2   ==null?-1:selectedTee2.getId());

        startActivity(iSelectPlaymate);
    }

    /** Reakce na zmeny v libovolnem "TabHost" **/
    public void tabChangeHandler(final DatabaseHandlerResort dbr,TabHost thCourse1,TabHost thCourse2, TabHost thTee1, TabHost thTee2) {

        thCourse1.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {

                /* nastaveni noveho aktualniho zvoleneho hriste */
                selectedCourse1 = dbr.getCourse(Long.valueOf(tabId));
                tabChangeHandlerCourse1(dbr);
            }
        });

        thCourse2.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {

                /* nastaveni noveho aktualniho zvoleneho hriste */
                selectedCourse2 = dbr.getCourse(Long.valueOf(tabId));
            }
        });

        thTee1.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {

                /* nastaveni noveho aktualniho zvoleneho odpaliste */
                selectedTee1 = dbr.getTee(Long.valueOf(tabId));
            }
        });

        thTee2.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                /* nastaveni noveho aktualniho zvoleneho odpaliste */
                selectedTee2 = dbr.getTee(Long.valueOf(tabId));
            }
        });
    }


    /** Obsluha a naplneni hodnotami prvku "TabHost" pro prvni hriste **/
    public void initCourse1(DatabaseHandlerResort dbr) {

        /* Ziskani pole retezcu s nazvy hrist pro naplneni "TabHost" */
        List<String> namesCourse = getNameListCourse(dbr.getAllCoursesInResort(idResort),null);

        /* Ziskani pole indexu pro jednotlive "taby" */
        List<String> indexesCourse = getIndexListCourse(dbr.getAllCoursesInResort(idResort),null);

        /* Naplneni hodnotami */
        initTabHost(namesCourse, indexesCourse, thCourse1);

        /* Nastaveni aktualniho zvoleneho hriste */
        selectedCourse1 = dbr.getCourse(Long.valueOf(indexesCourse.get(0)));
    }

    /** Obsluha a naplneni hodnotami prvku "TabHost" pro druhe hriste **/
    public void initCourse2(DatabaseHandlerResort dbr) {

        /* Ziskani pole retezcu s nazvy hrist pro naplneni "TabHost" */
        List<String> namesCourse = getNameListCourse(dbr.getAllCoursesInResort(idResort),selectedCourse1);

        /* Ziskani pole indexu pro jednotlive "taby" */
        List<String> indexesCourse = getIndexListCourse(dbr.getAllCoursesInResort(idResort),selectedCourse1);

        /* Naplneni hodnotami */
        initTabHost(namesCourse, indexesCourse, thCourse2);

        /* Nastaveni aktualniho zvoleneho hriste */
        selectedCourse2 = dbr.getCourse(Long.valueOf(indexesCourse.get(0)));
    }

    /** Obsluha a naplneni hodnotami prvku "TabHost" pro odpaliste prvniho hriste **/
    public void initTee1(DatabaseHandlerResort dbr) {

        /* Ziskani pole odpalist s nazvy odpalist pro naplneni "TabHost" */
        List<String> namesTee   = getStringListTee(dbr.getAllTeesOnCourse(selectedCourse1.getId()), null);

        /* Ziskani pole indexu pro jednotlive "taby" */
        List<String> indexesTee = getIndexListTee(dbr.getAllTeesOnCourse(selectedCourse1.getId()), null);

        /* Naplneni hodnotami */
        initTabHost(namesTee,indexesTee,thTee1);

        /* Nastaveni aktualniho zvoleneho odpaliste */
        selectedTee1 = dbr.getTee(Long.valueOf(indexesTee.get(0)));
    }

    /** Obsluha a naplneni hodnotami prvku "TabHost" pro odpaliste prvniho hriste **/
    public void initTee2(DatabaseHandlerResort dbr) {

        /* Ziskani pole odpalist s nazvy odpalist pro naplneni "TabHost" */
        List<String> namesTee   = getStringListTee(dbr.getAllTeesOnCourse(selectedCourse2.getId()),null);

        /* Ziskani pole indexu pro jednotlive "taby" */
        List<String> indexesTee = getIndexListTee(dbr.getAllTeesOnCourse(selectedCourse2.getId()),null);

        /* Naplneni hodnotami */
        initTabHost(namesTee,indexesTee,thTee2);

        /* Nastaveni aktualniho zvoleneho odpaliste */
        selectedTee2 = dbr.getTee(Long.valueOf(indexesTee.get(0)));
    }

    /** Reakce na zmenu hriste **/
    public void tabChangeHandlerCourse1(DatabaseHandlerResort dbr) {

        initTee1(dbr);

        /** Reseni konfliktu z vyberem druheho hriste **/

        /* Prvni zvolene hriste ma 18 jamek - druhe hriste se jiz nevoli */
        if (selectedCourse1.getHoleCount() == 18) {
            hideCourse2();
        /* Prvni zvolene hriste ma 9 jamek - voli se druhe hriste */
        } else {
            showCourse2();
            initCourse2(dbr);
            initTee2(dbr);
        }
    }

    /** Tvorba pole retezcu s nazvy hrist **/
    public List<String> getNameListCourse(List<Course> courseList,Course excludedCourse) {
        List<String> finalList = new ArrayList<>();

        for (Course c : courseList) {
            if (excludedCourse == null || excludedCourse.getId() != c.getId())
                finalList.add(c.getName());
        }

        return finalList;
    }

    /** Tvorba pole retezcu s nazvy odpalist **/
    public List<String> getStringListTee(List<Tee> teeList,Tee excludedTee) {
        List<String> finalList = new ArrayList<>();

        for (Tee t : teeList) {
            if (excludedTee == null || excludedTee.getId() != t.getId())
                finalList.add(t.getKind());
        }

        return finalList;
    }

    /** Tvorba pole retezcu s indexy hrist **/
    public List<String> getIndexListCourse(List<Course> courseList,Course excludedCourse) {
        List<String> finalList = new ArrayList<>();

        for (Course c : courseList) {
            if (excludedCourse == null || excludedCourse.getId() != c.getId())
                finalList.add(Integer.toString(c.getId()));
        }

        return finalList;
    }

    /** Tvorba pole retezcu s indexy hrist **/
    public List<String> getIndexListTee(List<Tee> teeList,Tee excludedTee) {
        List<String> finalList = new ArrayList<>();

        for (Tee t : teeList) {
            if (excludedTee == null || excludedTee.getId() != t.getId())
                finalList.add(Integer.toString(t.getId()));
        }

        return finalList;
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
                    return (new TextView(SelectCourse.this));
                }
            });

            /* Nastaveni popisku */
            spec.setIndicator(namesList.get(i));

            /* Pridani "Tabu" */
            tabHost.addTab(spec);
        }
    }

    /* Zobrazi volbu druheho hriste */
    public void showCourse2() {
        trTitleCourse2  .setVisibility(View.VISIBLE);
        trTitleTee2     .setVisibility(View.VISIBLE);
        trTabHostCourse2.setVisibility(View.VISIBLE);
        trTabHostTee2   .setVisibility(View.VISIBLE);
    }

    /* Skryje volbu druheho hriste */
    public void hideCourse2() {
        trTitleCourse2  .setVisibility(View.GONE);
        trTitleTee2     .setVisibility(View.GONE);
        trTabHostCourse2.setVisibility(View.GONE);
        trTabHostTee2   .setVisibility(View.GONE);
    }

    /** Reakce na zmacknuti tlacitka "zpet" - navrat na predchozi aktivitu **/
    @Override
    public void onBackPressed()
    {
        Intent iSelectResort = new Intent(this,SelectResort.class);
        this.startActivity(iSelectResort);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false; //Nechceme menu v liste
    }
}