package com.maha.leviathan.pockettemple.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maha.leviathan.pockettemple.objects.Pura;
import com.maha.leviathan.pockettemple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leviathan on 5/2/2015.
 */
public class CustomListPuraAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Pura> puraItems;
    private ArrayList<Pura> arraylist;

    public CustomListPuraAdapter(Activity activity, List<Pura> puraItems) {
        this.activity = activity;
        this.puraItems = puraItems;
        this.arraylist = new ArrayList<Pura>();
        this.arraylist.addAll(puraItems);
    }

    @Override
    public int getCount() {
        return puraItems.size();
    }

    @Override
    public Object getItem(int position) {
        return puraItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listrow, null);

        TextView nama = (TextView) convertView.findViewById(R.id.textViewListNamaPura);
        TextView alamat = (TextView) convertView.findViewById(R.id.textViewListAlamatPura);
        TextView desa = (TextView) convertView.findViewById(R.id.textViewListDesaPura);

        Pura pura = puraItems.get(position);

        nama.setText(pura.getNamaPura());
        alamat.setText(pura.getAlamatPura());
        desa.setText(pura.getDesaPura());

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase();
        puraItems.clear();
        if (charText.length() == 0) {
            puraItems.addAll(arraylist);
        } else {
            for (Pura pura : arraylist) {
                if (pura.getNamaPura().toLowerCase().contains(charText) ||
                        pura.getDesaPura().toLowerCase().contains(charText) ||
                        pura.getAlamatPura().toLowerCase().contains(charText)) {
                    puraItems.add(pura);
                }
            }
        }
        notifyDataSetChanged();
    }

}
