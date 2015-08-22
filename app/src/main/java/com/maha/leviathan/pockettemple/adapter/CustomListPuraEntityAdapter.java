package com.maha.leviathan.pockettemple.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.maha.leviathan.pockettemple.objects.PuraEntity;
import com.maha.leviathan.pockettemple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leviathan on 6/3/2015.
 */
public class CustomListPuraEntityAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<PuraEntity> puraItems;
    private ArrayList<PuraEntity> arraylist;

    public CustomListPuraEntityAdapter(Activity activity, List<PuraEntity> puraItems) {
        this.activity = activity;
        this.puraItems = puraItems;
        this.arraylist = new ArrayList<PuraEntity>();
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
            convertView = inflater.inflate(R.layout.listrowdetailpura, null);

        TextView nama = (TextView) convertView.findViewById(R.id.textViewListRowDetailPuraLarge);
        TextView definisi = (TextView) convertView.findViewById(R.id.textView2ListRowDetailPuraMedium);
        TextView tambahan = (TextView) convertView.findViewById(R.id.textViewListRowDetailPuraNormal);

        PuraEntity puraEntity = puraItems.get(position);

        nama.setText(puraEntity.getNama());

        if (puraEntity.getDefinisi().equals("")){
            definisi.setVisibility(View.GONE);
        }
        else {
            definisi.setText(puraEntity.getDefinisi());
        }
        if (puraEntity.getTambahan().equals("")){
            tambahan.setVisibility(View.GONE);
        }
        else {
            tambahan.setText(puraEntity.getTambahan());
        }

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase();
        puraItems.clear();
        if (charText.length() == 0) {
            puraItems.addAll(arraylist);
        } else {
            for (PuraEntity puraEntity : arraylist) {
                if (puraEntity.getNama().toLowerCase().contains(charText) ||
                        puraEntity.getDefinisi().toLowerCase().contains(charText) ||
                        puraEntity.getTambahan().toLowerCase().contains(charText)) {
                    puraItems.add(puraEntity);
                }
            }
        }
        notifyDataSetChanged();
    }
}
