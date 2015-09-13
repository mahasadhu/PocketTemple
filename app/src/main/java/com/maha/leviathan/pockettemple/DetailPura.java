package com.maha.leviathan.pockettemple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;
import com.maha.leviathan.pockettemple.other.TouchImageView;
import com.maha.leviathan.pockettemple.other.Utility;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;


public class DetailPura extends ActionBarActivity {

    String idpura, namapura;
    SliderLayout sliderShow;
    WebView webView;
    Button btnMoreDet;
    int noGambar = 1, drawerOpened = 0;
    SlidingUpPanelLayout slidingUpPanelLayout;
    TouchImageView imageView;

    JsonArrayRequest gambarPura;
    Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pura);

        Intent i = getIntent();
        namapura = i.getStringExtra("NamaPura");
        setTitle(namapura);
        idpura = i.getStringExtra("id");

        sliderShow = (SliderLayout) findViewById(R.id.slider);
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layoutDetail);
        webView = (WebView) findViewById(R.id.webView1);
        btnMoreDet = (Button) findViewById(R.id.buttonMoreDetail);
        imageView = (TouchImageView) findViewById(R.id.imageViewFotoPura);

        slidingUpPanelLayout.setTouchEnabled(false);
        slidingUpPanelLayout.setDragView(R.id.keliatanDetail);

        utility = new Utility();
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Data...");
        pDialog.show();

        final String TAG = DetailPura.class.getSimpleName();
        String urlimg = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=getIMGPura&idpura="+idpura;
        final String urldesk = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=getDeskPura&idpura="+idpura;

        btnMoreDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailPura.this, MoreDetail.class);
                i.putExtra("NamaPura", namapura);
                i.putExtra("id", idpura);
                startActivity(i);
            }
        });

//        sliderShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("LOG GAMBAR CLICK", sliderShow.getCurrentSlider().getUrl());
//            }
//        });
//
//        sliderShow.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.e("LOG GAMBAR TOUCH", sliderShow.getCurrentSlider().getUrl());
//                return true;
//            }
//        });

        gambarPura = new JsonArrayRequest(urlimg, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("INI JSON", response.toString());
                    try {
                        if (response.getString(0).equals("false")){
                            TextSliderView textSliderView = new TextSliderView(DetailPura.this);
                            textSliderView.image(R.drawable.notavail).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                            sliderShow.addSlider(textSliderView);
                        }
                        else {
                            for (int i = 0; i<response.length(); i++) {
                                String nama = response.getString(i);
                                if (nama.length()>4){
                                    nama = nama.replace(" ", "%20");
                                    final TextSliderView textSliderView = new TextSliderView(DetailPura.this);
                                    textSliderView.description("Gambar "+String.valueOf(noGambar)).image("http://" + Utility.servernya + "/sipura/photos/pura/"+idpura+"/"+nama).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                    textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView baseSliderView) {
                                            Log.e("URL IMAGE", sliderShow.getCurrentSlider().getUrl());
                                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                            drawerOpened = 1;
                                            Picasso.with(getApplicationContext())
                                                    .load(sliderShow.getCurrentSlider().getUrl())
                                                    .placeholder(R.drawable.loadpura)
                                                    .into(imageView);
                                        }
                                    });

                                    sliderShow.addSlider(textSliderView);
                                    noGambar++;
                                }
                            }
                        }
                        webView.loadUrl(urldesk);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("error puraload", e.getMessage());
                    }

                sliderShow.setPresetTransformer(SliderLayout.Transformer.FlipHorizontal);
                sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                utility.repeatRequestVolley(gambarPura, null, null, DetailPura.this);
//                Toast.makeText(DetailPura.this,
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                Log.e("EROR COEG", error.getMessage());
//                pDialog.dismiss();
            }
        });

        PocketTempleController.getInstance().addToRequestQueue(gambarPura);
    }

    @Override
    public void onBackPressed() {
        if (drawerOpened == 1){
            imageView.resetZoom();
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            drawerOpened = 0;
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_detail_pura, menu);
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
}
