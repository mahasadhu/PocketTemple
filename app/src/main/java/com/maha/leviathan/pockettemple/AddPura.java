package com.maha.leviathan.pockettemple;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;
import com.maha.leviathan.pockettemple.objects.Desa;
import com.maha.leviathan.pockettemple.objects.Karakterisasi;
import com.maha.leviathan.pockettemple.other.CustomRequest;
import com.maha.leviathan.pockettemple.other.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;

/**
 * Created by Leviathan on 7/1/2015.
 */
public class AddPura extends Fragment implements OnMapReadyCallback {

    private static View view;
    EditText nama, alamat, deskripsi;
    Spinner karakterisasi, desa;
    Button save, addfoto;
    final String TAG = Main.class.getSimpleName();
    ArrayList<String> listKarak = new ArrayList<String>();
    ArrayList<Karakterisasi> karakterisasis = new ArrayList<Karakterisasi>();
    ArrayList<String> listDesa = new ArrayList<String>();
    ArrayList<Desa> desas= new ArrayList<Desa>();

    GoogleMap googleMap;
    Marker marker;

    Utility utility;
    JsonArrayRequest getKarak, getDesa;
    CustomRequest addPura;
    Activity mActivity;
    String idKarak, idDesa;

    SharedPreferences sharedPreferences;
    ProgressDialog pDialog;
    int sudah = 0;
    int INTENT_REQUEST_GET_IMAGES = 10;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {
                Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {
                    for (Uri uri : uris) {
                        Log.e("INI LIST GAMBAR", " uri: " + uri);
//                        mMedia.add(uri);
                    }

//                    showMedia();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.addpura_layout, container, false);
        } catch (InflateException e){

        }

        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Loading...");
        pDialog.show();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragmentMapAddPura);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        Log.e("latitude", String.valueOf(Utility.lat));
        Log.e("longitude", String.valueOf(Utility.lng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Utility.lat, Utility.lng), 17));
        if (marker != null){
            marker = null;
        }
        marker = googleMap.addMarker(new MarkerOptions()
                        .title("New Pura")
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.puramarker))
                        .position(new LatLng(Utility.lat, Utility.lng))
        );

        sharedPreferences = mActivity.getSharedPreferences(getString(R.string.sharedprefname), Context.MODE_PRIVATE);

        nama = (EditText) view.findViewById(R.id.editTextNamaPuraInsert);
        alamat = (EditText) view.findViewById(R.id.editTextAlamatPuraInsert);
        deskripsi = (EditText) view.findViewById(R.id.editTextDeskripsiInsert);
        karakterisasi = (Spinner) view.findViewById(R.id.spinnerKarakterisasiInsert);
        desa = (Spinner) view.findViewById(R.id.spinnerDesaInsert);
        save = (Button) view.findViewById(R.id.buttonInsertData);
        addfoto = (Button) view.findViewById(R.id.buttonAddFoto);

        String url1 = "http://202.52.11.147/sipura/includes/web-services.php?flag=getKarak";
        String url2 = "http://202.52.11.147/sipura/includes/web-services.php?flag=getDesa&address="+String.valueOf(Utility.lat)+","+String.valueOf(Utility.lng);
        Log.e("URL", url2);
        final String url3 = "http://202.52.11.147/sipura/includes/web-services.php?flag=mobileAddPura";

        utility = new Utility();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nama.getText().toString().equals("") && !alamat.getText().toString().equals("") && !deskripsi.getText().toString().equals("")) {
                    final Map<String, String> post = new HashMap<String, String>();
                    final AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(mActivity);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mActivity.onBackPressed();
                        }
                    });
                    dialog.setCancelable(false);
                    post.put("lat", String.valueOf(Utility.lat));
                    post.put("lng", String.valueOf(Utility.lng));
                    post.put("nama_pura", nama.getText().toString());
                    post.put("alamat", alamat.getText().toString());
                    post.put("deskripsi", deskripsi.getText().toString());
                    post.put("id_kar", idKarak);
                    post.put("id_desa", idDesa);
                    post.put("iduser", sharedPreferences.getString("id", "null"));

                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Loading...");
                    pDialog.show();

                    addPura = new CustomRequest(Request.Method.POST, url3, post, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String resStr = response.getString("status");
                                Log.e("STATUS", resStr);
                                pDialog.dismiss();
                                if (resStr.equals("OK")) {
                                    dialog.setTitle("Data Saved. Please Wait for Admin approval");
                                    dialog.setMessage("Please use the web application to add more detailed data.");
                                    dialog.show();
//                                mActivity.onBackPressed();
//                                Toast.makeText(mActivity, "Data Saved", Toast.LENGTH_SHORT).show();
                                } else if (resStr.equals("FAIL")) {
                                    Toast.makeText(mActivity, "Failed to Save Data", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                        Log.e("RESPONSE INSERT", resStr);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            utility.repeatRequestVolley(null, addPura, null, mActivity);
                            Log.e("VOLLEY", error.toString());
                        }
                    });

                    PocketTempleController.getInstance().addToRequestQueue(addPura);
                } else {
                    Toast.makeText(mActivity, "Please Fill All Data", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ImagePickerActivity.class);
                Config config = new Config.Builder()
                        .setTabBackgroundColor(R.color.white)
                        .setTabSelectionIndicatorColor(R.color.blue)
                        .setCameraButtonColor(R.color.green)
                        .setSelectionLimit(10)
                        .build();
                ImagePickerActivity.setConfig(config);
                startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
            }
        });



        getKarak = new JsonArrayRequest(url1, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Karakterisasi karakterisasi = new Karakterisasi();
                        karakterisasi.setIdKarakterisasi(jsonObject.getString("id"));
                        karakterisasi.setKarakterisasi(jsonObject.getString("karakterisasi"));
                        karakterisasis.add(karakterisasi);

                        listKarak.add(jsonObject.getString("karakterisasi"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                karakterisasi.setAdapter(new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, listKarak));
                karakterisasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        idKarak = karakterisasis.get(position).getIdKarakterisasi();
//                        String idSpin = karakterisasis.get(position).getIdKarakterisasi();
//                        String karakSpin = karakterisasis.get(position).getKarakterisasi();
//                        Log.e("IDDARISPINNER", idSpin);
//                        Log.e("KARAKDARISPINNER", karakSpin);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                sudah++;
                if (sudah == 2){
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getKarak, null, null, mActivity);
            }
        });

        getDesa = new JsonArrayRequest(url2, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Desa desa = new Desa();
                        desa.setIdDesa(jsonObject.getString("id"));
                        desa.setDesa(jsonObject.getString("desa"));
                        desas.add(desa);

                        listDesa.add(jsonObject.getString("desa"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                desa.setAdapter(new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, listDesa));
                desa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        idDesa = desas.get(position).getIdDesa();
//                        String idSpin = desas.get(position).getIdDesa();
//                        String karakSpin = desas.get(position).getDesa();
//                        Log.e("IDDARISPINNER", idSpin);
//                        Log.e("KARAKDARISPINNER", karakSpin);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                sudah++;
                if (sudah == 2){
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getDesa, null, null, mActivity);
                Log.e("ERRORDESA", error.toString());
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getDesa.setRetryPolicy(policy);

        PocketTempleController.getInstance().addToRequestQueue(getKarak);
        PocketTempleController.getInstance().addToRequestQueue(getDesa);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        marker.remove();
        marker = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        if (Utility.lat == 3000 && Utility.lng == 3000){
            Toast.makeText(mActivity, "Haven't found your location. Please Wait.", Toast.LENGTH_SHORT).show();
            mActivity.onBackPressed();
        }
    }
}
