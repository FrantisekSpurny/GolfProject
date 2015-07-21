package cz.spurny.CreateGame;

/**
 * Objekt: SelectResortAdapter.java
 * Popis:  Adapter predstavujici jednu polozku seznamu resortu.
 * Autor:  Frantisek Spurny
 * Datum:  16.06.2015
 */

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import cz.spurny.DatabaseResort.Resort;

public class SelectResortAdapter extends BaseAdapter implements Filterable {

    private List<Resort>originalData = null;
    private List<Resort>filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
    Context context;

    /* Konstruktor */
    public SelectResortAdapter(Context context, List<Resort> data) {
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
            convertView = mInflater.inflate(R.layout.select_resort_adapter_layout, null);

            /* Creates a ViewHolder and store references to the two children views
               we want to bind data to. */
            holder = new ViewHolder();
            holder.tvName       = (TextView) convertView.findViewById(R.id.SelectResortAdapter_textView_name);
            holder.tvCity       = (TextView) convertView.findViewById(R.id.SelectResortAdapter_textView_city);
            holder.tvStreet     = (TextView) convertView.findViewById(R.id.SelectResortAdapter_textView_street);
            holder.tvStreetNum  = (TextView) convertView.findViewById(R.id.SelectResortAdapter_textView_streetNum);
            holder.tvArrea      = (TextView) convertView.findViewById(R.id.SelectResortAdapter_textView_arrea);

            /* Bind the data efficiently with the holder. */
            convertView.setTag(holder);
        } else {
            /* Get the ViewHolder back to get fast access to the TextView
               and the ImageView. */
            holder = (ViewHolder) convertView.getTag();
        }

        /* If weren't re-ordering this you could rely on what you set last time */
        holder.tvName      .setText(                filteredData.get(position).getName());
        holder.tvCity      .setText(                filteredData.get(position).getCity());
        holder.tvStreet    .setText(                filteredData.get(position).getStreet());
        holder.tvStreetNum .setText(String.valueOf( filteredData.get(position).getStreetNum()));
        holder.tvArrea     .setText(                filteredData.get(position).getArea()
                                                    + " "
                                                    + context.getString(R.string.SelectResortAdapter_string_arrea));

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvCity;
        TextView tvStreet;
        TextView tvStreetNum;
        TextView tvArrea;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Resort> list = originalData;

            int count = list.size();
            final ArrayList<Resort> nlist = new ArrayList<>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
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
            filteredData = (ArrayList<Resort>) results.values;
            notifyDataSetChanged();
        }

    }
}