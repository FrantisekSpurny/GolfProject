package cz.spurny.Game;

/**
 * Objekt: SelectHoleAdapter.java
 * Popis:  Adapter predstavujici jednu polozku seznamu resortu.
 * Autor:  Frantisek Spurny
 * Datum:  08.07.2015
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.DatabaseHandlerInternal;
import cz.spurny.DatabaseInternal.Player;
import cz.spurny.DatabaseInternal.Score;
import cz.spurny.DatabaseResort.Hole;

public class SelectHoleAdapter extends ArrayAdapter<Hole> {

    private List<Hole> holes = null;
    private Context context;
    private int courseCount;
    private double[] holeLengthArray;
    private List<Player> players;
    DatabaseHandlerInternal dbi;
    int gameId;

    /* Konstruktor */
    public SelectHoleAdapter(Context context,
                             List<Hole> data,
                             int courseCount,
                             double[] holeLengthArray,
                             List<Player> players,
                             DatabaseHandlerInternal dbi,
                             int gameId) {

        super(context,R.layout.select_hole_adapter_layout,data);
        this.holes           = data ;
        this.context         = context;
        this.courseCount     = courseCount;
        this.holeLengthArray = holeLengthArray;
        this.players         = players;
        this.dbi             = dbi;
        this.gameId          = gameId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.select_hole_adapter_layout, parent, false);

        /* Barevne odliseni radku seznamu */
        colorList(position, rowView);

        /* Udaje jamky */
        holeProperties(rowView, position);

        /* Udaje o hracih */
        playerPropreties(rowView, position);

        return rowView;
    }

    /** Zobrazeni udaju jamky **/
    public void holeProperties(View rowView,int position) {
        TextView tvNumber,tvPar,tvName,tvHcp,tvLenght;

        tvNumber     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_number);
        tvPar        = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_par);
        tvName       = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_name);
        tvHcp        = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_hcp);
        tvLenght     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_length);

        tvNumber.setText(String.valueOf(holes.get(position).getNumber()) + ".");
        tvPar   .setText(String.valueOf(holes.get(position).getPar()));
        tvName  .setText(holes.get(position).getName());
        tvHcp   .setText(String.valueOf(holes.get(position).getHcp()));
        tvLenght.setText(String.valueOf(holeLengthArray[position] + "m"));
    }

    /** Zobrazeni udaju o hracich **/
    public void playerPropreties(View rowView,int position) {
        TextView tvPlayer1,tvPlayer2,tvPlayer3,tvPlayer4,tvPlayed1,tvPlayed2,tvPlayed3,tvPlayed4;

        tvPlayed1     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_player1);
        tvPlayed2     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_player2);
        tvPlayed3     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_player3);
        tvPlayed4     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_player4);

        tvPlayer1     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_player1Title);
        tvPlayer2     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_player2Title);
        tvPlayer3     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_player3Title);
        tvPlayer4     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_player4Title);

        switch (players.size()) {
            case 1:
                tvPlayer1.setVisibility(View.VISIBLE);
                tvPlayed1.setVisibility(View.VISIBLE);
                tvPlayer1.setText(players.get(0).getNickname() + "  ");
                tvPlayed1.setText(Html.fromHtml(hasScore(players.get(0).getId(),holes.get(position).getId())));
                break;
            case 2:
                tvPlayer1.setVisibility(View.VISIBLE);
                tvPlayed1.setVisibility(View.VISIBLE);
                tvPlayer1.setText(players.get(0).getNickname() + "  ");
                tvPlayed1.setText(Html.fromHtml(hasScore(players.get(0).getId(),holes.get(position).getId())));
                tvPlayer2.setVisibility(View.VISIBLE);
                tvPlayed2.setVisibility(View.VISIBLE);
                tvPlayer2.setText(players.get(1).getNickname() + "  ");
                tvPlayed2.setText(Html.fromHtml(hasScore(players.get(1).getId(),holes.get(position).getId())));
                break;
            case 3:
                tvPlayer1.setVisibility(View.VISIBLE);
                tvPlayed1.setVisibility(View.VISIBLE);
                tvPlayer1.setText(players.get(0).getNickname() + "  ");
                tvPlayed1.setText(Html.fromHtml(hasScore(players.get(0).getId(),holes.get(position).getId())));
                tvPlayer2.setVisibility(View.VISIBLE);
                tvPlayed2.setVisibility(View.VISIBLE);
                tvPlayer2.setText(players.get(1).getNickname() + "  ");
                tvPlayed2.setText(Html.fromHtml(hasScore(players.get(1).getId(),holes.get(position).getId())));
                tvPlayer3.setVisibility(View.VISIBLE);
                tvPlayed3.setVisibility(View.VISIBLE);
                tvPlayer3.setText(players.get(2).getNickname() + "  ");
                tvPlayed3.setText(Html.fromHtml(hasScore(players.get(2).getId(),holes.get(position).getId())));
                break;
            case 4:
                tvPlayer1.setVisibility(View.VISIBLE);
                tvPlayed1.setVisibility(View.VISIBLE);
                tvPlayer1.setText(players.get(0).getNickname() + "  ");
                tvPlayed1.setText(Html.fromHtml(hasScore(players.get(0).getId(),holes.get(position).getId())));
                tvPlayer2.setVisibility(View.VISIBLE);
                tvPlayed2.setVisibility(View.VISIBLE);
                tvPlayer2.setText(players.get(1).getNickname() + "  ");
                tvPlayed2.setText(Html.fromHtml(hasScore(players.get(1).getId(),holes.get(position).getId())));
                tvPlayer3.setVisibility(View.VISIBLE);
                tvPlayed3.setVisibility(View.VISIBLE);
                tvPlayer3.setText(players.get(2).getNickname() + "  ");
                tvPlayed3.setText(Html.fromHtml(hasScore(players.get(2).getId(),holes.get(position).getId())));
                tvPlayer4.setVisibility(View.VISIBLE);
                tvPlayed4.setVisibility(View.VISIBLE);
                tvPlayer4.setText(players.get(3).getNickname() + "  ");
                tvPlayed4.setText(Html.fromHtml(hasScore(players.get(3).getId(),holes.get(position).getId())));
                break;
        }
    }

    /** Ziskani informace o tom zdali dany hrac ma ulozene skore na jamce **/
    public String hasScore(int playerId,int holeId) {

        Score score = dbi.getScore(holeId,playerId,gameId);

        /* Score neexistuje */
        if (score == null)
            return "<font color=#cc0029>" + context.getString(R.string.SelectHoleAdapter_string_noScore)+"</font>";
        /* Score je zadano */
        else
            return "<font color=#00CC00>"+ context.getString(R.string.SelectHoleAdapter_string_hasScore)+"</font>";
    }

    /** Obarveni polozek seznau jamek **/
    public void colorList (int position,View rowView) {
        if (courseCount == 2) {
            if (position < 9) { // jamky prvniho hriste
                colorItem(position,rowView,Color.parseColor("#E0FFFF"),Color.parseColor("#F0FFFF"));
            } else {
                colorItem(position,rowView,Color.parseColor("#FFE0E0"),Color.parseColor("#FFF0F0"));
            }
        } else {
            colorItem(position,rowView,Color.parseColor("#E0FFFF"),Color.parseColor("#F0FFFF"));
        }
    }

    /** Obarveni jedne polozky seznamu **/
    public void colorItem(int position,View rowView,int colorA,int colorB) {
        if ((position % 2)==0) {
            rowView.setBackgroundColor(colorA);
        } else {
            rowView.setBackgroundColor(colorB);
        }
    }
}