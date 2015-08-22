package com.maha.leviathan.pockettemple;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Leviathan on 5/1/2015.
 */
public class testFrag extends Fragment {

    Button klik;
    TextView nama, email, mobile;
    String tag_json_obj = "json_obj_req";
    String url = "https://maps.googleapis.com/maps/api/directions/json?origin=-8.5223292,115.2168846&destination=-8.5101907,115.2174425&key=AIzaSyDfJk77k2LCBWzn7wWGudh7Njry-VULwJ4";
    private static String TAG = Main.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View tampilanTest = inflater.inflate(R.layout.testfrag_layout, container, false);
        klik = (Button) tampilanTest.findViewById(R.id.buttonTestFrag);
        nama = (TextView) tampilanTest.findViewById(R.id.textViewNama);
        email = (TextView) tampilanTest.findViewById(R.id.textViewEmail);
        mobile = (TextView) tampilanTest.findViewById(R.id.textViewmobile);

        klik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Loading...");
                pDialog.show();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray maindata = response.getJSONArray("routes");
                            JSONObject rute = (JSONObject) maindata.get(0);
                            JSONArray legs = rute.getJSONArray("legs");
                            JSONObject legs1 = (JSONObject) legs.get(0);
                            JSONArray steps = legs1.getJSONArray("steps");
                            JSONObject steps1 = (JSONObject) steps.get(0);
                            JSONObject poly = steps1.getJSONObject("polyline");
                            String encPoly = poly.getString("points");
                            List<LatLng> decPoly = PolyUtil.decode(encPoly);
                            Log.e("CEKINI", decPoly.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getActivity(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

//                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                            JSONObject data = (JSONObject) response.get(0);
//                            String namaStr = data.getString("name");
//                            String emailStr = data.getString("email");
//                            JSONObject telp = data.getJSONObject("phone");
//                            String mobileStr = telp.getString("mobile");
//
//                            nama.setText(namaStr);
//                            email.setText(emailStr);
//                            mobile.setText(mobileStr);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getActivity(),
//                                    "Error: " + e.getMessage(),
//                                    Toast.LENGTH_LONG).show();
//                        }
//                        pDialog.dismiss();
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        VolleyLog.d(TAG, "Error: " + error.getMessage());
//                        Toast.makeText(getActivity(),
//                                error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
                PocketTempleController.getInstance().addToRequestQueue(jsonObjectRequest);
            }
        });

        return tampilanTest;
    }


}
