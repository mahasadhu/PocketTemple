package com.maha.leviathan.pockettemple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
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


public class DetailBangunan extends ActionBarActivity {

    String nama, id;
    SliderLayout sliderShow;
    WebView webView;
    int noGambar = 1, drawerOpened = 0;
    SlidingUpPanelLayout slidingUpPanelLayout;
    TouchImageView imageView;

    JsonArrayRequest gambarBangunan;
    Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bangunan);

        Intent i = getIntent();
        nama = i.getStringExtra("nama");
        setTitle(nama);
        id = i.getStringExtra("id");

        utility = new Utility();

        sliderShow = (SliderLayout) findViewById(R.id.sliderBangunan);
        webView = (WebView) findViewById(R.id.webViewDetailBangunan);
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layoutDetailBangunan);
        imageView = (TouchImageView) findViewById(R.id.imageViewFotoBangunan);

        slidingUpPanelLayout.setTouchEnabled(false);
        slidingUpPanelLayout.setDragView(R.id.keliatanDetailBangunan);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Data...");
        pDialog.setCancelable(false);
        pDialog.show();

        final String TAG = DetailBangunan.class.getSimpleName();
        String urlimg = "http://202.52.11.147/sipura/includes/web-services.php?flag=getIMGBangunan&idbangunan="+id;
        final String urldesk = "http://202.52.11.147/sipura/includes/web-services.php?flag=getDeskBangunan&idbangunan="+id;

        gambarBangunan = new JsonArrayRequest(urlimg, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("INI JSON", response.toString());
                try {
                    if (response.getString(0).equals("false")){
                        TextSliderView textSliderView = new TextSliderView(DetailBangunan.this);
                        textSliderView.image(R.drawable.notavail).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                        sliderShow.addSlider(textSliderView);
                    }
                    else {
                        for (int i = 0; i<response.length(); i++) {
                            String nama = response.getString(i);
                            if (nama.length()>4){
                                nama = nama.replace(" ", "%20");
                                TextSliderView textSliderView = new TextSliderView(DetailBangunan.this);
                                textSliderView.description("Gambar "+String.valueOf(noGambar)).image("http://202.52.11.147/sipura/photos/bangunan/"+id+"/"+nama).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView baseSliderView) {
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
//                Toast.makeText(DetailBangunan.this,
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                Log.e("EROR COEG", error.getMessage());
//                pDialog.dismiss();
                utility.repeatRequestVolley(gambarBangunan, null, null, DetailBangunan.this);
            }
        });

        PocketTempleController.getInstance().addToRequestQueue(gambarBangunan);
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
//        getMenuInflater().inflate(R.menu.menu_detail_bangunan, menu);
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
