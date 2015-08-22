package com.maha.leviathan.pockettemple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.maha.leviathan.pockettemple.adapter.CustomListPuraEntityAdapter;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;
import com.maha.leviathan.pockettemple.objects.PuraEntity;
import com.maha.leviathan.pockettemple.other.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leviathan on 6/3/2015.
 */
public class ListBangunanPura extends Fragment {

    ListView listView;
    EditText editText;
    private static View view;
    ProgressDialog pDialog;
    private static final String TAG = MoreDetail.class.getSimpleName();
    String url;
    List<PuraEntity> puraEntities = new ArrayList<PuraEntity>();
    CustomListPuraEntityAdapter customListPuraEntityAdapter;

    JsonArrayRequest listBangunan;
    Utility utility;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.listbangunanpura_layout, container, false);
        } catch (InflateException e){

        }

        utility = new Utility();
        listView = (ListView) view.findViewById(R.id.listViewBangunanPura);
        editText = (EditText) view.findViewById(R.id.editTextSearchBangunan);

        String idpura = ((MoreDetail) getActivity()).idpura;
        url = "http://202.52.11.147/sipura/includes/web-services.php?flag=getListBangunan&idpura="+idpura;

//        customListPuraEntityAdapter = new CustomListPuraEntityAdapter(getActivity(), puraEntities);
//        listView.setAdapter(customListPuraEntityAdapter);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        listBangunan = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                puraEntities.clear();
                customListPuraEntityAdapter = new CustomListPuraEntityAdapter(getActivity(), puraEntities);
                listView.setAdapter(customListPuraEntityAdapter);
                customListPuraEntityAdapter.notifyDataSetChanged();

                Log.e("INI URL", url);
                Log.e("INI JSON", response.toString());
                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        PuraEntity puraEntity = new PuraEntity();
                        puraEntity.setNama(obj.getString("val"));
                        puraEntity.setId(obj.getString("id"));
                        puraEntity.setTambahan(obj.getString("jenis"));

                        puraEntity.setDefinisi("");

                        puraEntities.add(puraEntity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                customListPuraEntityAdapter = new CustomListPuraEntityAdapter(getActivity(), puraEntities);
                listView.setAdapter(customListPuraEntityAdapter);
                customListPuraEntityAdapter.notifyDataSetChanged();

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = editText.getText().toString().toLowerCase();
                        customListPuraEntityAdapter.filter(text);
                    }
                });

                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getActivity(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                Log.e("EROR COEG", error.getMessage());
//                pDialog.dismiss();
                utility.repeatRequestVolley(listBangunan, null, null, getActivity());
            }
        });

        PocketTempleController.getInstance().addToRequestQueue(listBangunan);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PuraEntity puraEntity = (PuraEntity) customListPuraEntityAdapter.getItem(position);
                Intent i = new Intent(getActivity(), DetailBangunan.class);
                i.putExtra("nama", puraEntity.getNama());
                i.putExtra("id", puraEntity.getId());
                startActivity(i);
            }
        });

        return view;
    }
}
