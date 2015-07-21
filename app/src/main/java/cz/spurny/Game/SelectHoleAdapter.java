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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseResort.Hole;

public class SelectHoleAdapter extends ArrayAdapter<Hole> {

    private List<Hole> holes = null;
    Context context;
    int courseCount;
    double[] holeLengthArray;

    /* Konstruktor */
    public SelectHoleAdapter(Context context, List<Hole> data,int courseCount,double[] holeLengthArray) {
        super(context,R.layout.select_hole_adapter_layout,data);
        this.holes           = data ;
        this.context         = context;
        this.courseCount     = courseCount;
        this.holeLengthArray = holeLengthArray;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.select_hole_adapter_layout, parent, false);

        /* Barevne odliseni radku seznamu */
        colorList(position,rowView);

        TextView tvNumber;
        TextView tvPar;
        TextView tvName;
        TextView tvCourse;
        TextView tvLenght;

        tvNumber     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_number);
        tvPar        = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_par);
        tvName       = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_name);
        tvCourse     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_course);
        tvLenght     = (TextView) rowView.findViewById(R.id.SelectHoleAdapter_textView_length);

        tvNumber.setText(String.valueOf(holes.get(position).getNumber()) + ".");
        tvPar   .setText(String.valueOf(holes.get(position).getPar()));
        tvName  .setText(holes.get(position).getName());
        tvCourse.setText(String.valueOf(holes.get(position).getName()));
        tvLenght.setText(String.valueOf(holeLengthArray[position] + "m"));

        return rowView;
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