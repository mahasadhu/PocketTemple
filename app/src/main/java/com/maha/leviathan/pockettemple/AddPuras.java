package com.maha.leviathan.pockettemple;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import com.maha.leviathan.pockettemple.objects.Pancawara;
import com.maha.leviathan.pockettemple.objects.PurnamaTilem;
import com.maha.leviathan.pockettemple.objects.Saptawara;
import com.maha.leviathan.pockettemple.objects.Sasih;
import com.maha.leviathan.pockettemple.objects.Wuku;
import com.maha.leviathan.pockettemple.other.CustomRequest;
import com.maha.leviathan.pockettemple.other.PocketTempleImageUploader;
import com.maha.leviathan.pockettemple.other.PocketTempleImageUploaderv2;
import com.maha.leviathan.pockettemple.other.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;


public class AddPuras extends ActionBarActivity implements OnMapReadyCallback {

    EditText nama, alamat, deskripsi;
    Spinner karakterisasi, desa, jenisOdalan, pancawara, saptawara, wuku, purnamaTilem, sasih;
    Button save, addfoto;
    final String TAG = Main.class.getSimpleName();
    ArrayList<String> listKarak = new ArrayList<String>();
    ArrayList<Karakterisasi> karakterisasis = new ArrayList<Karakterisasi>();
    ArrayList<String> listDesa = new ArrayList<String>();
    ArrayList<Desa> desas = new ArrayList<Desa>();
    ArrayList<String> listPancawara = new ArrayList<String>();
    ArrayList<Pancawara> pancawaras = new ArrayList<Pancawara>();
    ArrayList<String> listSaptawara = new ArrayList<String>();
    ArrayList<Saptawara> saptawaras = new ArrayList<Saptawara>();
    ArrayList<String> listWuku = new ArrayList<String>();
    ArrayList<Wuku> wukus = new ArrayList<Wuku>();
    ArrayList<String> listPurnamaTilem = new ArrayList<String>();
    ArrayList<PurnamaTilem> purnamaTilems = new ArrayList<PurnamaTilem>();
    ArrayList<String> listSasih = new ArrayList<String>();
    ArrayList<Sasih> sasihs = new ArrayList<Sasih>();

    RelativeLayout layoutWuku, layoutSasih;

    GoogleMap googleMap;
    Marker marker;

    Utility utility;
    JsonArrayRequest getKarak, getDesa, getPancawara, getSaptawara, getWuku, getPurnamaTilem, getSasih;
    CustomRequest addPura;
    String idKarak, idDesa, idJenisOdalan = "1", idPancawara, idSaptawara, idWuku, idPurnamaTilem, idSasih;

    SharedPreferences sharedPreferences;
    ProgressDialog pDialog;
    int sudah = 0;
    int INTENT_REQUEST_GET_IMAGES = 10;
    String[] listGambar;
    int totImage = 0;

    String url1 = "http://202.52.11.147/sipura/includes/web-services.php?flag=";
    String url2 = "http://202.52.11.147/sipura/includes/web-services.php?flag=getDesa&address="+String.valueOf(Utility.lat)+","+String.valueOf(Utility.lng);
    final String url3 = "http://202.52.11.147/sipura/includes/web-services.php?flag=mobileAddPura";
    String url4 = "http://202.52.11.147/sipura/includes/web-services.php?flag=mobileUploadFotoPura&idpura=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpura_layout);

        if (Utility.lat == 3000 && Utility.lng == 3000){
            Toast.makeText(AddPuras.this, "Haven't found your location. Please Wait.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            pDialog = new ProgressDialog(AddPuras.this);
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

            sharedPreferences = AddPuras.this.getSharedPreferences(getString(R.string.sharedprefname), Context.MODE_PRIVATE);

            nama = (EditText) findViewById(R.id.editTextNamaPuraInsert);
            alamat = (EditText) findViewById(R.id.editTextAlamatPuraInsert);
            deskripsi = (EditText) findViewById(R.id.editTextDeskripsiInsert);
            karakterisasi = (Spinner) findViewById(R.id.spinnerKarakterisasiInsert);
            desa = (Spinner) findViewById(R.id.spinnerDesaInsert);
            save = (Button) findViewById(R.id.buttonInsertData);
            addfoto = (Button) findViewById(R.id.buttonAddFoto);
            jenisOdalan = (Spinner) findViewById(R.id.spinnerJenisOdalanAdd);
            layoutSasih = (RelativeLayout) findViewById(R.id.layoutSasihInsert);
            layoutWuku = (RelativeLayout) findViewById(R.id.layoutWukuInsert);
            pancawara = (Spinner) findViewById(R.id.spinnerPancarawaInsert);
            saptawara = (Spinner) findViewById(R.id.spinnerSaptawaraInsert);
            wuku = (Spinner) findViewById(R.id.spinnerWukuInsert);
            purnamaTilem = (Spinner) findViewById(R.id.spinnerPurnamaTilemInsert);
            sasih = (Spinner) findViewById(R.id.spinnerSasihInsert);

            utility = new Utility();

            jenisOdalan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0){
                        idJenisOdalan = "1";
                        layoutWuku.setVisibility(View.VISIBLE);
                        layoutSasih.setVisibility(View.GONE);
                    }
                    else {
                        idJenisOdalan = "2";
                        layoutWuku.setVisibility(View.GONE);
                        layoutSasih.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!nama.getText().toString().equals("") && !alamat.getText().toString().equals("") && !deskripsi.getText().toString().equals("")) {
                        final Map<String, String> post = new HashMap<String, String>();
                        final AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(AddPuras.this);
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AddPuras.this.finish();
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
                        post.put("idJenisOdalan", idJenisOdalan);
                        if (idJenisOdalan.equals("1")){
                            post.put("idPancawara", idPancawara);
                            post.put("idSaptawara", idSaptawara);
                            post.put("idWuku", idWuku);
                        }
                        else if (idJenisOdalan.equals("2")){
                            post.put("idPurnamaTilem", idPurnamaTilem);
                            post.put("idSasih", idSasih);
                        }

                        pDialog = new ProgressDialog(AddPuras.this);
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
                                        url4 = url4 + response.getString("idpura");
                                        Log.e("URL4", url4);
                                        if (listGambar != null) {
                                            final MaterialDialog progressBar = new MaterialDialog.Builder(AddPuras.this)
                                                    .title("Uploading Image")
                                                    .content("Uploading Image to Server")
                                                    .progress(false, listGambar.length, true)
                                                    .show();
                                            for (int i = 0; i < listGambar.length; i++) {
                                                File image = new File(listGambar[i]);
                                                PocketTempleImageUploaderv2 imageUpload = new PocketTempleImageUploaderv2(url4, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.e("ERROR UPLOAD", error.toString());
                                                        progressBar.incrementProgress(1);
                                                    }
                                                }, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Log.e("RESPONSE UPLOAD", response);
                                                        if (response.toString().equals("0")) {
                                                            totImage++;
                                                        }
                                                        progressBar.incrementProgress(1);
                                                        if (progressBar.getCurrentProgress() == progressBar.getMaxProgress()) {
                                                            dialog.setTitle("Data Saved. Please Wait for Admin approval");
                                                            dialog.setMessage("(" + totImage + " of " + listGambar.length + " image(s) successfully uploaded). Please use the web application to add more detailed data.");
                                                            dialog.show();
                                                            progressBar.dismiss();
                                                        }
                                                    }
                                                }, image);
                                                int socketTimeout = 120000;
                                                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                                imageUpload.setRetryPolicy(policy);
                                                PocketTempleController.getInstance().addToRequestQueue(imageUpload);
                                            }
                                        } else {
                                            dialog.setTitle("Data Saved. Please Wait for Admin approval");
                                            dialog.setMessage("Please use the web application to add more detailed data.");
                                            dialog.show();
                                        }
                                    } else if (resStr.equals("FAIL")) {
                                        Toast.makeText(AddPuras.this, "Failed to Save Data", Toast.LENGTH_SHORT).show();
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
                                utility.repeatRequestVolley(null, addPura, null, AddPuras.this);
                                Log.e("VOLLEY", error.toString());
                            }
                        });

                        PocketTempleController.getInstance().addToRequestQueue(addPura);
                    } else {
                        Toast.makeText(AddPuras.this, "Please Fill All Data", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            addfoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AddPuras.this, "Please Choose Up to 5 Images to send", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPuras.this, ImagePickerActivity.class);
                    Config config = new Config.Builder()
                            .setTabBackgroundColor(R.color.white)
                            .setTabSelectionIndicatorColor(R.color.warnaActionBar)
                            .setCameraButtonColor(R.color.warnaActionBar)
                            .setSelectionLimit(5)
                            .build();
                    ImagePickerActivity.setConfig(config);
                    startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
                }
            });



            getKarak = new JsonArrayRequest(url1+"getKarak", new Response.Listener<JSONArray>() {
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

                    karakterisasi.setAdapter(new ArrayAdapter<String>(AddPuras.this, android.R.layout.simple_spinner_dropdown_item, listKarak));
                    karakterisasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            idKarak = karakterisasis.get(position).getIdKarakterisasi();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    sudah++;
                    if (sudah == 7){
                        pDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    utility.repeatRequestVolley(getKarak, null, null, AddPuras.this);
                }
            });

            getPancawara = new JsonArrayRequest(url1+"getPancawara", new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i<response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            Pancawara pancawara = new Pancawara();
                            pancawara.setId(jsonObject.getString("id"));
                            pancawara.setPancawara(jsonObject.getString("pancawara"));
                            pancawaras.add(pancawara);

                            listPancawara.add(jsonObject.getString("pancawara"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    pancawara.setAdapter(new ArrayAdapter<String>(AddPuras.this, android.R.layout.simple_spinner_dropdown_item, listPancawara));
                    pancawara.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            idPancawara = pancawaras.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    sudah++;
                    if (sudah == 7){
                        pDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    utility.repeatRequestVolley(getPancawara, null, null, AddPuras.this);
                }
            });

            getSaptawara = new JsonArrayRequest(url1+"getSaptawara", new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i<response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            Saptawara saptawara = new Saptawara();
                            saptawara.setId(jsonObject.getString("id"));
                            saptawara.setSaptawara(jsonObject.getString("saptawara"));
                            saptawaras.add(saptawara);

                            listSaptawara.add(jsonObject.getString("saptawara"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    saptawara.setAdapter(new ArrayAdapter<String>(AddPuras.this, android.R.layout.simple_spinner_dropdown_item, listSaptawara));
                    saptawara.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            idSaptawara = saptawaras.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    sudah++;
                    if (sudah == 7){
                        pDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    utility.repeatRequestVolley(getSaptawara, null, null, AddPuras.this);
                }
            });

            getWuku = new JsonArrayRequest(url1+"getWuku", new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i<response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            Wuku wuku = new Wuku();
                            wuku.setId(jsonObject.getString("id"));
                            wuku.setWuku(jsonObject.getString("wuku"));
                            wukus.add(wuku);

                            listWuku.add(jsonObject.getString("wuku"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    wuku.setAdapter(new ArrayAdapter<String>(AddPuras.this, android.R.layout.simple_spinner_dropdown_item, listWuku));
                    wuku.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            idWuku = wukus.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    sudah++;
                    if (sudah == 7){
                        pDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    utility.repeatRequestVolley(getWuku, null, null, AddPuras.this);
                }
            });

            getPurnamaTilem = new JsonArrayRequest(url1+"getPurnamaTilem", new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i<response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            PurnamaTilem purnamaTilem = new PurnamaTilem();
                            purnamaTilem.setId(jsonObject.getString("id"));
                            purnamaTilem.setPurnamaTilem(jsonObject.getString("purnama_tilem"));
                            purnamaTilems.add(purnamaTilem);

                            listPurnamaTilem.add(jsonObject.getString("purnama_tilem"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    purnamaTilem.setAdapter(new ArrayAdapter<String>(AddPuras.this, android.R.layout.simple_spinner_dropdown_item, listPurnamaTilem));
                    purnamaTilem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            idPurnamaTilem = purnamaTilems.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    sudah++;
                    if (sudah == 7){
                        pDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    utility.repeatRequestVolley(getPurnamaTilem, null, null, AddPuras.this);
                }
            });

            getSasih = new JsonArrayRequest(url1+"getSasih", new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i<response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            Sasih sasih = new Sasih();
                            sasih.setId(jsonObject.getString("id"));
                            sasih.setSasih(jsonObject.getString("sasih"));
                            sasihs.add(sasih);

                            listSasih.add(jsonObject.getString("sasih"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    sasih.setAdapter(new ArrayAdapter<String>(AddPuras.this, android.R.layout.simple_spinner_dropdown_item, listSasih));
                    sasih.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            idSasih = sasihs.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    sudah++;
                    if (sudah == 7){
                        pDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    utility.repeatRequestVolley(getSasih, null, null, AddPuras.this);
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

                    desa.setAdapter(new ArrayAdapter<String>(AddPuras.this, android.R.layout.simple_spinner_dropdown_item, listDesa));
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
                    if (sudah == 7){
                        pDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    utility.repeatRequestVolley(getDesa, null, null, AddPuras.this);
                    Log.e("ERRORDESA", error.toString());
                }
            });
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            getDesa.setRetryPolicy(policy);

            PocketTempleController.getInstance().addToRequestQueue(getKarak);
            PocketTempleController.getInstance().addToRequestQueue(getDesa);
            PocketTempleController.getInstance().addToRequestQueue(getPancawara);
            PocketTempleController.getInstance().addToRequestQueue(getSaptawara);
            PocketTempleController.getInstance().addToRequestQueue(getWuku);
            PocketTempleController.getInstance().addToRequestQueue(getPurnamaTilem);
            PocketTempleController.getInstance().addToRequestQueue(getSasih);
        }

    }

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
                    listGambar = new String[parcelableUris.length];
                    for (int i = 0; i < uris.length; i++) {
                        Log.e("INI LIST GAMBAR", " uri: " + uris[i]);
                        listGambar[i] = uris[i].toString();
                    }

//                    showMedia();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add_puras, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == 16908332){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
