package cz.spurny.LoadGame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Game;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseResort.DatabaseHandlerResort;
import cz.spurny.DatabaseResort.Hole;

/**
 * Objekt: SelectSavedGameAdapter.java
 * Popis:  Adapter pro zobrazeni polozek seznamu ulozenych her.
 * Autor:  Frantisek Spurny
 * Datum:  20.08.2015
 */

public class SelectSavedGameAdapter extends ArrayAdapter<Game> {

    Context context;
    List<Game> savedGames;
    DatabaseHandlerInternal dbi;
    DatabaseHandlerResort   dbr;

    /* Konstruktor */
    public SelectSavedGameAdapter(Context context,
                                  List<Game> savedGames,
                                  DatabaseHandlerInternal dbi,
                                  DatabaseHandlerResort dbr) {

        super(context, R.layout.select_hole_adapter_layout, savedGames);
        this.context    = context;
        this.savedGames = savedGames;
        this.dbi        = dbi;
        this.dbr        = dbr;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.select_saved_game_adapter_layout, parent, false);

        TextView tvNumber, tvDate, tvResort, tvCourse;

        tvNumber = (TextView) rowView.findViewById(R.id.SelectSavedGameAdapter_textView_number);
        tvDate   = (TextView) rowView.findViewById(R.id.SelectSavedGameAdapter_textView_date);
        tvResort = (TextView) rowView.findViewById(R.id.SelectSavedGameAdapter_textView_resort);
        tvCourse = (TextView) rowView.findViewById(R.id.SelectSavedGameAdapter_textView_courses);

        tvNumber.setText(String.valueOf(position + 1) + ".");
        tvDate  .setText(savedGames.get(position).getDate());
        tvResort.setText(dbi.getResortOfGame(savedGames.get(position).getId()).getName());

        /** nastaveni hrist **/
        String courseString;

        if (dbi.getNumOfGameCourses(savedGames.get(position).getId())==1)
            courseString = " " + dbr.getCourse(dbi.getAllGameCourseOfGame(savedGames.get(position).getId()).get(0).getIdCourse()).getName();
        else
            courseString = " " + dbr.getCourse(dbi.getAllGameCourseOfGame(savedGames.get(position).getId()).get(0).getIdCourse()).getName() + " - " +
                    dbr.getCourse(dbi.getAllGameCourseOfGame(savedGames.get(position).getId()).get(1).getIdCourse()).getName();

        tvCourse.setText(courseString);

        return rowView;
    }

}