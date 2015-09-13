package com.maha.leviathan.pockettemple;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;
import com.maha.leviathan.pockettemple.objects.Desa;
import com.maha.leviathan.pockettemple.objects.Karakterisasi;
import com.maha.leviathan.pockettemple.objects.Pancawara;
import com.maha.leviathan.pockettemple.objects.PurnamaTilem;
import com.maha.leviathan.pockettemple.objects.Saptawara;
import com.maha.leviathan.pockettemple.objects.Sasih;
import com.maha.leviathan.pockettemple.objects.Wuku;
import com.maha.leviathan.pockettemple.other.CustomRequest;
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


public class EditPura extends ActionBarActivity implements OnMapReadyCallback {

    GoogleMap googleMap;

    EditText nama, alamat, deskripsi;
    Spinner karakterisasi, desa, jenisOdalan, pancawara, saptawara, wuku, purnamaTilem, sasih;
    Button save, updateLoc, updatefoto;
    RelativeLayout layoutWuku, layoutSasih;

    final String TAG = EditPura.class.getSimpleName();

    ArrayList<String> listKarak = new ArrayList<String>();
    ArrayList<Karakterisasi> karakterisasis = new ArrayList<Karakterisasi>();
    ArrayList<String> listDesa = new ArrayList<String>();
    ArrayList<Desa> desas= new ArrayList<Desa>();
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

    JsonArrayRequest getKarak, getDesa, getPura, getPancawara, getSaptawara, getWuku, getPurnamaTilem, getSasih;

    int statLocation = 0;
    String idpura;
    int sudah = 0;
    double lat, lng, oldLat, oldLng;
    String namapura, alamatpura, deskripsipura, karakterisasipura, desapura, idKarak, idDesa, idJenisOdalan = "1", idPancawara, idSaptawara, idWuku, idPurnamaTilem, idSasih;
    CustomRequest updatePura;

    ProgressDialog pDialog;
    Utility utility;
    int INTENT_REQUEST_GET_IMAGES = 20;
    String[] listGambar;
    int totImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pura);

        Intent i = getIntent();
        setTitle("Edit " + i.getStringExtra("NamaPura"));
        idpura = i.getStringExtra("id");

        nama = (EditText) findViewById(R.id.editTextNamaPuraEdit);
        alamat = (EditText) findViewById(R.id.editTextAlamatPuraEdit);
        deskripsi = (EditText) findViewById(R.id.editTextDeskripsiEdit);
        karakterisasi = (Spinner) findViewById(R.id.spinnerKarakterisasiEdit);
        desa = (Spinner) findViewById(R.id.spinnerDesaEdit);
        save = (Button) findViewById(R.id.buttonUpdateData);
        updateLoc = (Button) findViewById(R.id.buttonUpdateLoc);
        updatefoto = (Button) findViewById(R.id.buttonEditFoto);
        jenisOdalan = (Spinner) findViewById(R.id.spinnerJenisOdalanEdit);
        layoutWuku = (RelativeLayout) findViewById(R.id.layoutWukuEdit);
        layoutSasih = (RelativeLayout) findViewById(R.id.layoutSasihEdit);
        pancawara = (Spinner) findViewById(R.id.spinnerPancarawaEdit);
        saptawara = (Spinner) findViewById(R.id.spinnerSaptawaraEdit);
        wuku = (Spinner) findViewById(R.id.spinnerWukuEdit);
        purnamaTilem = (Spinner) findViewById(R.id.spinnerPurnamaTilemEdit);
        sasih = (Spinner) findViewById(R.id.spinnerSasihEdit);

        jenisOdalan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("POS", String.valueOf(position));
//                Log.e("ID", String.valueOf(id));
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

        utility = new Utility();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragmentMapEditPura);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Utility.lat, Utility.lng), 17));
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        googleMap.addMarker(new MarkerOptions()
                        .title("New Location")
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("New Location")))
                        .position(new LatLng(Utility.lat, Utility.lng))
        );

        String url0 = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=getPuraByID&id_pura="+i.getStringExtra("id");
        String url1 = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=";
        String url2 = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=getDesa&address="+String.valueOf(Utility.lat)+","+String.valueOf(Utility.lng);
        final String url3 = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=mobileUpdatePura";
        final String url4 = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=mobileUploadFotoPura&idpura="+i.getStringExtra("id");

        updatefoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditPura.this, "Please Choose Up to 5 Images to send", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditPura.this, ImagePickerActivity.class);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nama.getText().toString().equals("") && !alamat.getText().toString().equals("") && !deskripsi.getText().toString().equals("")){
                    final Map<String, String> post = new HashMap<String, String>();
                    final AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(EditPura.this);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            EditPura.this.finish();
                        }
                    });
                    pDialog = new ProgressDialog(EditPura.this);
                    pDialog.setMessage("Loading...");
                    pDialog.show();

                    dialog.setCancelable(false);
                    post.put("lat", String.valueOf(lat));
                    post.put("lng", String.valueOf(lng));
                    post.put("nama_pura", nama.getText().toString());
                    post.put("alamat", alamat.getText().toString());
                    post.put("deskripsi", deskripsi.getText().toString());
                    post.put("id_kar", idKarak);
                    post.put("id_desa", idDesa);
                    post.put("id_pura", idpura);
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

                    updatePura = new CustomRequest(Request.Method.POST, url3, post, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String resStr = response.getString("status");
                                Log.e("STATUS", resStr);
                                pDialog.dismiss();
                                if (resStr.equals("OK")){
                                    if (listGambar != null){
                                        final MaterialDialog progressBar = new MaterialDialog.Builder(EditPura.this)
                                                .title("Uploading Image")
                                                .content("Uploading Image to Server")
                                                .progress(false, listGambar.length, true)
                                                .show();
                                        for (int i = 0; i<listGambar.length; i++){
                                            File image = new File(listGambar[i]);
                                            PocketTempleImageUploaderv2 imageEdit = new PocketTempleImageUploaderv2(url4, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e("ERROR UPLOAD", error.toString());
                                                    progressBar.incrementProgress(1);
                                                }
                                            }, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.e("RESPONSE UPLOAD", response);
                                                    if (response.toString().equals("0")){
                                                        totImage++;
                                                    }
                                                    progressBar.incrementProgress(1);
                                                    if (progressBar.getCurrentProgress() == progressBar.getMaxProgress()) {
                                                        dialog.setTitle("Data Updated.");
                                                        dialog.setMessage("("+totImage+" of "+listGambar.length+" image(s) successfully uploaded). Please use the web application to add more detailed data.");
                                                        dialog.show();
                                                        progressBar.dismiss();
                                                    }
                                                }
                                            }, image);
                                            int socketTimeout = 120000;
                                            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                            imageEdit.setRetryPolicy(policy);
                                            PocketTempleController.getInstance().addToRequestQueue(imageEdit);
                                        }
                                    }
                                    else {
                                        dialog.setTitle("Data Updated.");
                                        dialog.setMessage("Please use the web application to add more detailed data.");
                                        dialog.show();
                                    }
                                }
                                else{
                                    Toast.makeText(EditPura.this, "Failed to Save Data", Toast.LENGTH_SHORT).show();
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
                            utility.repeatRequestVolley(null, updatePura, null, EditPura.this);
                            Log.e("VOLLEY", error.toString());
                        }
                    });

                    PocketTempleController.getInstance().addToRequestQueue(updatePura);
                }
                else {
                    Toast.makeText(EditPura.this, "Please Fill All Data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statLocation == 0){
                    lat = Utility.lat;
                    lng = Utility.lng;
                    updateLoc.setText("Current Location Set! (Click Again to Undo)");
                    statLocation = 1;
                }
                else if (statLocation == 1){
                    lat = oldLat;
                    lng = oldLng;
                    updateLoc.setText("Update Pura Location to Current Location");
                    statLocation = 0;
                }
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

                karakterisasi.setAdapter(new ArrayAdapter<String>(EditPura.this, android.R.layout.simple_spinner_dropdown_item, listKarak));
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
                    PocketTempleController.getInstance().addToRequestQueue(getPura);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getKarak, null, null, EditPura.this);
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

                pancawara.setAdapter(new ArrayAdapter<String>(EditPura.this, android.R.layout.simple_spinner_dropdown_item, listPancawara));
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
                    PocketTempleController.getInstance().addToRequestQueue(getPura);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getPancawara, null, null, EditPura.this);
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

                saptawara.setAdapter(new ArrayAdapter<String>(EditPura.this, android.R.layout.simple_spinner_dropdown_item, listSaptawara));
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
                    PocketTempleController.getInstance().addToRequestQueue(getPura);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getSaptawara, null, null, EditPura.this);
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

                wuku.setAdapter(new ArrayAdapter<String>(EditPura.this, android.R.layout.simple_spinner_dropdown_item, listWuku));
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
                    PocketTempleController.getInstance().addToRequestQueue(getPura);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getWuku, null, null, EditPura.this);
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

                purnamaTilem.setAdapter(new ArrayAdapter<String>(EditPura.this, android.R.layout.simple_spinner_dropdown_item, listPurnamaTilem));
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
                    PocketTempleController.getInstance().addToRequestQueue(getPura);
            }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getPurnamaTilem, null, null, EditPura.this);
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

                sasih.setAdapter(new ArrayAdapter<String>(EditPura.this, android.R.layout.simple_spinner_dropdown_item, listSasih));
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
                    PocketTempleController.getInstance().addToRequestQueue(getPura);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getPurnamaTilem, null, null, EditPura.this);
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

                desa.setAdapter(new ArrayAdapter<String>(EditPura.this, android.R.layout.simple_spinner_dropdown_item, listDesa));
                desa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        idDesa = desas.get(position).getIdDesa();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                sudah++;
                if (sudah == 7){
                    PocketTempleController.getInstance().addToRequestQueue(getPura);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getDesa, null, null, EditPura.this);
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getDesa.setRetryPolicy(policy);

        getPura = new JsonArrayRequest(url0, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    JSONObject jsonObject1 = response.getJSONObject(1);
                    lat = jsonObject.getDouble("lat");
                    lng = jsonObject.getDouble("lng");
                    oldLat = jsonObject.getDouble("lat");
                    oldLng = jsonObject.getDouble("lng");
                    namapura = jsonObject.getString("nama");
                    alamatpura = jsonObject.getString("alamat");
                    deskripsipura = jsonObject.getString("deskripsi");

                    idKarak = jsonObject.getString("id_kar");
                    karakterisasipura = jsonObject.getString("karakterisasi");

                    idDesa = jsonObject.getString("id_desa");
                    desapura = jsonObject.getString("desa");

                    nama.setText(namapura);
                    alamat.setText(alamatpura);
                    deskripsi.setText(deskripsipura);

                    karakterisasi.setSelection(((ArrayAdapter) karakterisasi.getAdapter()).getPosition(karakterisasipura));
                    desa.setSelection(((ArrayAdapter) desa.getAdapter()).getPosition(desapura));
                    jenisOdalan.setSelection(jsonObject.getInt("id_jenis_odalan") - 1);

                    if (jsonObject.getInt("id_jenis_odalan") == 1){
                        idJenisOdalan = "1";
                        pancawara.setSelection(((ArrayAdapter) pancawara.getAdapter()).getPosition(jsonObject1.getString("pancawara")));
                        saptawara.setSelection(((ArrayAdapter) saptawara.getAdapter()).getPosition(jsonObject1.getString("saptawara")));
                        wuku.setSelection(((ArrayAdapter) wuku.getAdapter()).getPosition(jsonObject1.getString("wuku")));
                    }
                    else if (jsonObject.getInt("id_jenis_odalan") == 2){
                        idJenisOdalan = "2";
                        purnamaTilem.setSelection(((ArrayAdapter) purnamaTilem.getAdapter()).getPosition(jsonObject1.getString("purnama_tilem")));
                        sasih.setSelection(((ArrayAdapter) sasih.getAdapter()).getPosition(jsonObject1.getString("sasih")));
                    }

                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(getPura, null, null, EditPura.this);
            }
        });

        PocketTempleController.getInstance().addToRequestQueue(getKarak);
        PocketTempleController.getInstance().addToRequestQueue(getDesa);
        PocketTempleController.getInstance().addToRequestQueue(getPancawara);
        PocketTempleController.getInstance().addToRequestQueue(getSaptawara);
        PocketTempleController.getInstance().addToRequestQueue(getWuku);
        PocketTempleController.getInstance().addToRequestQueue(getPurnamaTilem);
        PocketTempleController.getInstance().addToRequestQueue(getSasih);
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
//        getMenuInflater().inflate(R.menu.menu_edit_pura, menu);
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
