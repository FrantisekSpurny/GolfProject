package cz.spurny.Dialogs;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Player;

/**
 * Objekt: SaveGameResultsAdapter.java
 * Popis:  Adapter seznamu hracu a poctu zahranych jamek.
 * Autor:  Frantisek Spurny
 * Datum:  18.08.2015
 */
public class SaveGameResultsAdapter extends ArrayAdapter<Player> {

    private final Context context;
    private final List<Player> players;
    private final List<Integer> played;

    public SaveGameResultsAdapter(Context context, List<Player> players,List<Integer> played) {
        super(context, R.layout.save_game_results_adapter_layout, players);
        this.context = context;
        this.players = players;
        this.played  = played;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.save_game_results_adapter_layout, parent, false);

		/* Prvky GUI */
        TextView tvPlayer = (TextView) rowView.findViewById(R.id.SaveGameResultAdapter_textView_playerName);
        TextView tvPlayed = (TextView) rowView.findViewById(R.id.SaveGameResultAdapter_textView_playedHoles);

        tvPlayer.setText(players.get(position).getNickname());
        tvPlayed.setText(Html.fromHtml(formatPlayedString(played.get(position))));

        return rowView;
    }

    /** Obarveni textu na zaklade toto jestli byli zahrany vsechny jamky ci nikoliv **/
    public static String formatPlayedString(int played) {

        if (played == 18)
            return "<font color=#00CC00>"+ played + " / 18 </font>";
        else
            return "<font color=#cc0029>" + played + " / 18 </font>";
    }

}
