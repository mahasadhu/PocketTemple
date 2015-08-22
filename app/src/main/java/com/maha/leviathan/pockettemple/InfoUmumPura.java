package com.maha.leviathan.pockettemple;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class InfoUmumPura extends Fragment {

    ListView listView;
    ProgressDialog pDialog;
    private static final String TAG = MoreDetail.class.getSimpleName();
    String url;
    List<PuraEntity> puraEntities = new ArrayList<PuraEntity>();
    CustomListPuraEntityAdapter customListPuraEntityAdapter;
    JsonArrayRequest infoPuraReq;

    Utility utility;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.infoumumpura_layout, container, false);
        utility = new Utility();
        listView = (ListView) view.findViewById(R.id.listViewInfoUmumPura);

        String idpura = ((MoreDetail) getActivity()).idpura;
        url = "http://202.52.11.147/sipura/includes/web-services.php?flag=getDetailPura&idpura="+idpura;

        customListPuraEntityAdapter = new CustomListPuraEntityAdapter(getActivity(), puraEntities);
        listView.setAdapter(customListPuraEntityAdapter);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        infoPuraReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("INI URL", url);
                Log.e("INI JSON", response.toString());
                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        if (!obj.getString("nama").equals("id_pura")){
                            PuraEntity puraEntity = new PuraEntity();

                            puraEntity.setNama(obj.getString("nama"));
                            puraEntity.setDefinisi(obj.getString("val"));
                            puraEntity.setTambahan("");

                            puraEntities.add(puraEntity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                customListPuraEntityAdapter.notifyDataSetChanged();
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
                utility.repeatRequestVolley(infoPuraReq, null, null, getActivity());
            }
        });

        PocketTempleController.getInstance().addToRequestQueue(infoPuraReq);

        return view;
    }
}
