package cz.spurny.CreateGame;

/**
 * Objekt: SelectPlaymateAdapter.java
 * Popis:  Adapter predstavujici jednu polozku seznamu hracu.
 * Autor:  Frantisek Spurny
 * Datum:  1.7.2015
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.spurny.DatabaseInternal.Player;

public class SelectPlaymateAdapter extends BaseAdapter implements Filterable {

    private List<Player> originalData = null;
    private List<Player>filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
    Context context;

    /* Konstruktor */
    public SelectPlaymateAdapter(Context context, List<Player> data) {
        this.filteredData = data ;
        this.originalData = data ;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        /* A ViewHolder keeps references to children views to avoid unnecessary calls
           to findViewById() on each row. */
        ViewHolder holder;

        /* When convertView is not null, we can reuse it directly, there is no need
           to reinflate it. We only inflate a new View when the convertView supplied
           by ListView is null. */
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.select_playmate_adapter_layout, null);

            /* Creates a ViewHolder and store references to the two children views
               we want to bind data to. */
            holder = new ViewHolder();
            holder.tvName       = (TextView) convertView.findViewById(R.id.SelectPlaymateAdapter_textView_name);
            holder.tvNickname   = (TextView) convertView.findViewById(R.id.SelectPlaymateAdapter_textView_nickname);

            /* Bind the data efficiently with the holder. */
            convertView.setTag(holder);
        } else {
            /* Get the ViewHolder back to get fast access to the TextView
               and the ImageView. */
            holder = (ViewHolder) convertView.getTag();
        }

        /* If weren't re-ordering this you could rely on what you set last time */
        holder.tvName    .setText(filteredData.get(position).getName() + " " + filteredData.get(position).getSurname());
        holder.tvNickname.setText(filteredData.get(position).getNickname());

        if (position == 0) {
            convertView.setBackgroundColor(Color.parseColor("#A3E0FF"));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvNickname;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Player> list = originalData;

            int count = list.size();
            final ArrayList<Player> nlist = new ArrayList<>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getNickname();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Player>) results.values;
            notifyDataSetChanged();
        }

    }
}
