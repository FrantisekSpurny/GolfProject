package cz.spurny.Player;

/**
 * Objekt: EditBagAdapter.java
 * Popis:  Adapter slouzici k zobrazeni jedne polozky seznamu holi
 * Autor:  Frantisek Spurny
 * Datum:  24.06.2015
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.spurny.CreateGame.R;
import cz.spurny.DatabaseInternal.Club;

public class EditBagAdapter extends ArrayAdapter<Club> {

    private final Context context;
    private final List<Club> clubs;

    public EditBagAdapter(Context context, List<Club> clubs) {
        super(context, R.layout.edit_bag_adapter_layout, clubs);
        this.context = context;
        this.clubs = clubs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.edit_bag_adapter_layout, parent, false);

		/* Prvky GUI */
        TextView tvName  = (TextView) rowView.findViewById(R.id.EditBagAdapter_textView_name);
        TextView tvModel = (TextView) rowView.findViewById(R.id.EditBagAdapter_textView_model);
        TextView tvSsl   = (TextView) rowView.findViewById(R.id.EditBagAdapter_textView_ssl);

        /* Naplneni hodnotami */
        tvName .setText               (clubs.get(position).getName());
        tvModel.setText               (clubs.get(position).getModel());
        tvSsl  .setText(String.valueOf(clubs.get(position).getStandardStrokeLength())+" m");

        return rowView;
    }
}