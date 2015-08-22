package com.maha.leviathan.pockettemple;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.maha.leviathan.pockettemple.adapter.CustomListPuraAdapter;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;
import com.maha.leviathan.pockettemple.objects.Pura;
import com.maha.leviathan.pockettemple.other.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leviathan on 5/2/2015.
 */
public class ListPura extends Fragment {

    ProgressDialog pDialog;
    private static View view;
    private static final String TAG = Main.class.getSimpleName();
    String url = "http://202.52.11.147/sipura/includes/web-services.php?flag=getAllPura";
    List<Pura> puraList = new ArrayList<Pura>();
    ListView listView;
    CustomListPuraAdapter customListPuraAdapter;
    EditText sQuery;
//    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.sharedprefname), Context.MODE_PRIVATE);


    JsonArrayRequest puraReq;
    Utility utility;

//    public ListPura(int pilih){
//        if (pilih == 1){
//            url = "http://202.52.11.147/sipura/includes/web-services.php?flag=getAllPura";
//        }
//        else if (pilih == 2){
//            url = "http://202.52.11.147/sipura/includes/web-services.php?flag=getMyPura&iduser="+sharedPreferences.getString("id", "1");
//        }
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.listfrag_layout, container, false);
        } catch (InflateException e){

        }

        utility = new Utility();
        listView = (ListView) view.findViewById(R.id.listViewPura);
        sQuery = (EditText) view.findViewById(R.id.editTextSearchPura);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        puraReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                puraList.clear();
                customListPuraAdapter = new CustomListPuraAdapter(getActivity(), puraList);
                listView.setAdapter(customListPuraAdapter);
                customListPuraAdapter.notifyDataSetChanged();

                Log.e("INI JSON", response.toString());
                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Pura pura = new Pura();
                        pura.setNamaPura(obj.getString("nama"));
                        pura.setAlamatPura(obj.getString("alamat"));
                        pura.setDesaPura(obj.getString("desa"));
                        pura.setId(obj.getString("id"));
                        pura.setLat(obj.getDouble("lat"));
                        pura.setLng(obj.getDouble("lng"));

                        puraList.add(pura);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                }
                customListPuraAdapter = new CustomListPuraAdapter(getActivity(), puraList);
                listView.setAdapter(customListPuraAdapter);
                customListPuraAdapter.notifyDataSetChanged();

                sQuery.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        String text = sQuery.getText().toString().toLowerCase();
                        customListPuraAdapter.filter(text);
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(puraReq, null, null, getActivity());
            }
        });

        PocketTempleController.getInstance().addToRequestQueue(puraReq);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Pura pura = (Pura) customListPuraAdapter.getItem(position);

                MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
                dialog.title(pura.getNamaPura());
                dialog.positiveText("Detail");
                dialog.negativeText("Go to Map");
                dialog.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        Intent i = new Intent(getActivity(), DetailPura.class);
                        i.putExtra("NamaPura", pura.getNamaPura());
                        i.putExtra("id", pura.getId());
                        startActivity(i);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        MapFrag mapFrag = new MapFrag();

                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat", pura.getLat());
                        bundle.putDouble("lng", pura.getLng());
                        mapFrag.setArguments(bundle);

//                        ((Main) getActivity()).getCurrentSection().unSelect();
                        ((Main) getActivity()).setSection(((Main) getActivity()).getSectionByTitle("Maps"));
                        ((Main) getActivity()).setFragment(mapFrag, "Maps");
                    }
                });
                dialog.show();
            }
        });

        return view;
    }
}
