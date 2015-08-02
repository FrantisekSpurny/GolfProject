package cz.spurny.Dialogs;

/**
 * Objekt: SelectClub.java
 * Popis:  Dialog slouzici pro vyber hole.
 * Autor:  Frantisek Spurny
 * Datum:  02.08.2015
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Club;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Shot;
import cz.spurny.Game.GameOnHole;
import cz.spurny.Player.EditBagAdapter;

public class SelectClub {

    /* Prvky GUI */
    static ListView lvClubList;
    static Button   bAddClub;
    static Button   bDone;

    /* Adapter */
    static public EditBagAdapter adapter = null;

    /* Kontext */
    static Context context;

    /* Seznam holi */
    static List<Club> clubs;

    /* Upravovana rana */
    static Shot shot;

    /* Dialog */
    static Dialog dialog;

    public static Dialog dialog(final Context activityContext,
                                List<Club> activityClubs,
                                Shot activityShot) {

        /* Prirazeni hodnot */
        context = activityContext;
        clubs   = activityClubs;
        shot    = activityShot;

        /* Tvorba dialogu */
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.point_selection_hole_points_layout);
        dialog.setTitle(R.string.SelectClub_string_title);

        /* Pripojeni databaze */
        DatabaseHandlerInternal dbi = new DatabaseHandlerInternal(context);

        /* Pripojeni prvku GUI */
        lvClubList = (ListView) dialog.findViewById(R.id.PointSelectionHolePoints_listView_pointList);

        /* Pridani tlacitka na konec seznamu, ktere slouzi pro pridani noveho hrace */
        View vAddPlayer = ((Activity)context).getLayoutInflater().inflate(R.layout.select_playmate_list_footer, null);
        lvClubList.addFooterView(vAddPlayer);

        /* Inicializace adapteru */
        initAdapter(dbi);

        /* Reakce na dlouhe stlaceni polozky listu */
        if (dbi.getAllClubs()!=null)
            longPressHandler(dbi);

        /* Reakce na kliknuti na polozku seznamu */
        if (dbi.getAllClubs()!=null)
            listClickHandler(dbi);

        /* Odpojeni databaze */
        dbi.close();

        return dialog;
    }

    /** Inicializace adapteru pro list holi **/
    public static void initAdapter(DatabaseHandlerInternal dbi) {

        if (dbi.getAllClubs() != null)
            clubs = dbi.getAllClubs();
        else
            clubs = new ArrayList<>();

        adapter = new EditBagAdapter(context, clubs);
        lvClubList.setAdapter(adapter);
    }

    /** Reakce na kliknuti na polozku seznamu - editace dane hole **/
    public static void listClickHandler(final DatabaseHandlerInternal dbi) {
        lvClubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == clubs.size())
                    AddClub.dialog(dbi, context, adapter, clubs).show();
                else
                    selectClub(dbi,clubs.get(position));
            }
        });
    }

    /** Reakce na dlouhe stlaceni polozky listu **/
    public static void longPressHandler(final DatabaseHandlerInternal dbi) {
        lvClubList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                EditClub.dialog(dbi, context, adapter, clubs, pos).show();
                return true;
            }
        });
    }

    /** Vyber hole **/
    public static void selectClub(final DatabaseHandlerInternal dbi,Club club) {
        shot.setClubId(club.getId());
        ((GameOnHole)context).infoPanelCaptureShot();
        dialog.hide();
    }
}