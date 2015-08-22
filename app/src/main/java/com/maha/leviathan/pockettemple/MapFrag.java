package com.maha.leviathan.pockettemple;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;
import com.maha.leviathan.pockettemple.adapter.CustomListPuraAdapter;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;
import com.maha.leviathan.pockettemple.objects.Pura;
import com.maha.leviathan.pockettemple.other.Utility;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leviathan on 4/23/2015.
 */
public class MapFrag extends Fragment
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, SensorEventListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Bundle bundle;
    private static View view;
    GoogleMap googleMap, googlemap2;
    String encPoly;
    List<LatLng> decPoly = new ArrayList<LatLng>();
    final String TAG = Main.class.getSimpleName();
    Location myLoc;
    IconGenerator iconFactory;
    Polyline polyRoute;
    ProgressDialog pDialog, locDialog;
    SlidingUpPanelLayout slidingUpPanelLayout;
    Button buttondetnav, stopNav;

    Marker titikStart;
    LinearLayout Nav, NonNav;
    ListView detPerjalanan;
    List<Pura> detailList = new ArrayList<Pura>();
    CustomListPuraAdapter customListPuraAdapter;
    double reso;
    Activity mActivity;
    boolean isNavMode = false;

    JsonObjectRequest getDirek;
    JsonArrayRequest puraLoad;
    double jarakDouble;

    Utility utility;

    SensorManager sensorManager;
    Sensor sensor;
    double declination;
    float bearingHasil;
    long lastTime = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);

        }
        try {
            view = inflater.inflate(R.layout.mapfrag_layout, container, false);
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.fragmentMap);
            mapFragment.getMapAsync(this);
            googleMap = mapFragment.getMap();

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        sensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
//        sensorManager.registerListener(mActivity, )
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        bundle = this.getArguments();
        if (bundle != null){
            double latBundle = bundle.getDouble("lat", 99999);
            double lngBundle = bundle.getDouble("lng", 99999);
            if (latBundle != 99999 && lngBundle != 99999){
                LatLng pos = new LatLng(latBundle, lngBundle);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(pos)
                        .zoom(17)
                        .build();
                 ((MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap))
                         .getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                googlemap2 = mapFragment.getMap();
            }
            Log.e("LAT BUNDLE", String.valueOf(latBundle));
            Log.e("LNG BUNDLE", String.valueOf(lngBundle));
        }

        utility = new Utility();

        locDialog = new ProgressDialog(mActivity);
        // Showing progress dialog before making http request
        locDialog.setMessage("Finding Your Location...");

        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        buttondetnav = (Button) view.findViewById(R.id.buttonDetNav);
        stopNav = (Button) view.findViewById(R.id.buttonStopNav);
        Nav = (LinearLayout) view.findViewById(R.id.layoutNav);
        NonNav = (LinearLayout) view.findViewById(R.id.layoutNonNav);
        detPerjalanan = (ListView) view.findViewById(R.id.listDetPerjalanan);

        customListPuraAdapter = new CustomListPuraAdapter(mActivity, detailList);
        detPerjalanan.setAdapter(customListPuraAdapter);

        slidingUpPanelLayout.setTouchEnabled(false);

//        dragView = (LinearLayout) view.findViewById(R.id.dragView);

        buttondetnav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.COLLAPSED)){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
                else {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        });

        stopNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableNavMode();
//                slidingUpPanelLayout.setPanelHeight(0);
            }
        });

        slidingUpPanelLayout.setDragView(R.id.keliatan);
//        slidingUpPanelLayout.setAnchorPoint((float) 0.17474048);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
//                Log.e("POS", String.valueOf(v));
            }

            @Override
            public void onPanelCollapsed(View view) {
                buttondetnav.setText("SHOW DETAILS");
            }

            @Override
            public void onPanelExpanded(View view) {
                buttondetnav.setText("HIDE DETAILS");
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });
//        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//        slidingUpPanelLayout.setPanelHeight(0);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iconFactory = new IconGenerator(mActivity);
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-8.799785, 115.169000), 10));



//        googleMap.addMarker(new MarkerOptions()
//                .title("Rektorat Universitas Udayana")
//                .snippet("Gedung paling bagus di kampus bukit.")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.puramarker))
//                .position(bukit));
//
//        googleMap.addMarker(new MarkerOptions()
//                .title("Imam Bonjol")
//                .snippet("Test")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.puramarker))
//                .position(imbo));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getSnippet().equals("me")) {
                    final Marker marker1 = marker;
                    MaterialDialog.Builder dialog = new MaterialDialog.Builder(mActivity);
                    dialog.title(marker.getTitle());
                    dialog.positiveText("Detail");
                    dialog.negativeText("Directions");
                    dialog.callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            Intent i = new Intent(mActivity, DetailPura.class);
                            i.putExtra("NamaPura", marker1.getTitle());
                            i.putExtra("id", marker1.getSnippet());
                            startActivity(i);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            createRoute(myLoc, marker1.getPosition());
                        }
                    });
                    dialog.show();
                }
                return true;
            }
        });

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (myLoc == null) {
                    locDialog.show();
                } else {
                    LatLng me = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(me)
                            .zoom(17)
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                return true;
            }
        });

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Utility.lat = location.getLatitude();
                Utility.lng = location.getLongitude();
            }
        });

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        loadMarker();

    }

    public void newMarker(String nama, LatLng latLng, String id){
        googleMap.addMarker(new MarkerOptions()
                        .title(nama)
                        .snippet(id)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.puramarker))
                        .position(latLng)
        );
    }



    public void loadMarker(){
        final String TAG = Main.class.getSimpleName();
        String url = "http://202.52.11.147/sipura/includes/web-services.php?flag=getAllPura";
        List<Pura> puraList = new ArrayList<Pura>();
        manageDialog(1, "Loading Pura...");

        puraLoad = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("INI JSON", response.toString());
                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        LatLng pos = new LatLng(obj.getDouble("lat"), obj.getDouble("lng"));

                        newMarker(obj.getString("nama"), pos, obj.getString("id"));
//                        Pura pura = new Pura();
//                        pura.setNamaPura(obj.getString("nama"));
//                        pura.setAlamatPura(obj.getString("alamat"));
//                        pura.setDesaPura(obj.getString("desa"));
//
//                        puraList.add(pura);
                        manageDialog(0, "done");
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Log.e("error puraload", e.getMessage());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                manageDialog(0, "done");
                utility.repeatRequestVolley(puraLoad, null, null, mActivity);

                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(mActivity,
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                Log.e("EROR COEG", error.getMessage());

            }
        });

        PocketTempleController.getInstance().addToRequestQueue(puraLoad);
    }



    public void manageDialog(int stat, String message){
        if (stat == 1){
            pDialog = new ProgressDialog(mActivity);
            // Showing progress dialog before making http request
            pDialog.setMessage(message);
            pDialog.show();
        }
        else {
            pDialog.dismiss();
        }
    }

    public void createRoute(final Location start, LatLng destination){
        if (myLoc == null){
            locDialog.show();
        }
        else {
            if (polyRoute != null){
                polyRoute.remove();
                decPoly.clear();
            }
            if (titikStart != null){
                titikStart.remove();
            }

            view.setKeepScreenOn(true);

            manageDialog(1, "Calculating Route");
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+String.valueOf(start.getLatitude())+","+String.valueOf(start.getLongitude())+"&destination="+String.valueOf(destination.latitude)+","+String.valueOf(destination.longitude)+"&key=AIzaSyDfJk77k2LCBWzn7wWGudh7Njry-VULwJ4";
            detailList.clear();
            customListPuraAdapter.notifyDataSetChanged();
            getDirek = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject rute = (JSONObject) response.getJSONArray("routes").get(0);
                        JSONObject overview_route = rute.getJSONObject("overview_polyline");
                        polyRoute = googleMap.addPolyline(new PolylineOptions().addAll(PolyUtil.decode(overview_route.getString("points"))).color(Color.BLUE));

                        JSONObject detail = (JSONObject) rute.getJSONArray("legs").get(0);
                        Pura jarak = new Pura();
                        jarak.setNamaPura("Distance");
                        jarak.setAlamatPura(detail.getJSONObject("distance").getString("text"));
                        jarakDouble = detail.getJSONObject("distance").getDouble("value");
                        Log.e("CEKJARAK", String.valueOf(jarakDouble));
                        detailList.add(jarak);

                        Pura durasi = new Pura();
                        durasi.setNamaPura("Duration");
                        durasi.setAlamatPura(detail.getJSONObject("duration").getString("text"));
                        detailList.add(durasi);

                        Pura locStart = new Pura();
                        locStart.setNamaPura("Start Address");
                        locStart.setAlamatPura(detail.getString("start_address"));
                        detailList.add(locStart);

                        Pura locEnd = new Pura();
                        locEnd.setNamaPura("End Address");
                        locEnd.setAlamatPura(detail.getString("end_address"));
                        detailList.add(locEnd);

//                        JSONObject legs = (JSONObject) rute.getJSONArray("legs").get(0);
//                        JSONArray steps = legs.getJSONArray("steps");
//                        for (int i = 0; i<steps.length(); i++){
//                            JSONObject steps1 = (JSONObject) steps.get(i);
//                            JSONObject poly = steps1.getJSONObject("polyline");
//                            decPoly.addAll(PolyUtil.decode(poly.getString("points")));
//                        }
//                        polyRoute = googleMap.addPolyline(new PolylineOptions().addAll(decPoly).color(Color.BLUE));
                        Log.e("CEKINI", PolyUtil.decode(overview_route.getString("points")).toString());
                        manageDialog(0, "done");
                        LatLng pos = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(pos)
                                .zoom(19)
                                .tilt(60)
                                .build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                lastTime = System.currentTimeMillis()+1500;
                                isNavMode = true;
                            }

                            @Override
                            public void onCancel() {

                            }
                        });

                        IconGenerator iconFactory = new IconGenerator(mActivity);
                        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                        titikStart = googleMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Start!")))
                                        .snippet("me")
                                        .position(new LatLng(start.getLatitude(), start.getLongitude()))
                                        .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
                        );

                        NonNav.setVisibility(View.GONE);
                        Nav.setVisibility(View.VISIBLE);

//                        double asdasd = 0;
//                        for (int i = 1; i < polyRoute.getPoints().size(); i++){
//                            LatLng poi = polyRoute.getPoints().get(i);
//                            asdasd += utility.distanceFrom(poi, polyRoute.getPoints().get(i-1));
//                        }
//                        Log.e("JARAKDARIHITUNGAN", String.valueOf(asdasd));

//                        DARI SINI JANGAN DIKOMEN
//                        for (int i = 0; i<jarakDouble; i+=50){
//                            LatLng titik = utility.getPointAtDistance(polyRoute, i);
////                            Log.e("INI I", String.valueOf(i));
////                            Log.e("TITIKRANGE", titik.toString());
//                            if (titik != null){
//                                newMarker("titik ke "+String.valueOf(i), titik, "titik"+String.valueOf(i));
//                            }
//                        }

                        customListPuraAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(mActivity,
//                                "Error: " + e.getMessage(),
//                                Toast.LENGTH_LONG).show();
                        manageDialog(0, "done");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
//                    Toast.makeText(mActivity,
//                            error.getMessage(), Toast.LENGTH_SHORT).show();
//                    manageDialog(0, "done");
                    utility.repeatRequestVolley(null, getDirek, null, mActivity);
                }
            });

            PocketTempleController.getInstance().addToRequestQueue(getDirek);
        }
    }

    public void disableNavMode(){
//        dragView.setVisibility(LinearLayout.INVISIBLE);
//        slidingUpPanelLayout.setEnabled(false);
//        indDis = 1;
//        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//        slidingUpPanelLayout.setParalaxOffset(100);
//        slidingUpPanelLayout.setPanelHeight(0);
//
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        NonNav.setVisibility(View.VISIBLE);
        Nav.setVisibility(View.GONE);
        slidingUpPanelLayout.setTouchEnabled(false);
        titikStart.remove();
        CameraPosition cameraPosition1 = new CameraPosition.Builder()
            .target(googleMap.getCameraPosition().target)
            .zoom(10)
            .tilt(0)
            .bearing(0)
            .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
        polyRoute.remove();
        decPoly.clear();
        view.setKeepScreenOn(false);
        isNavMode = false;
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.e("LOC UPDATE STOP", "LOC UPDATE STOP");
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLoc = location;
        Utility.lat = location.getLatitude();
        Utility.lng = location.getLongitude();
        if (locDialog.isShowing()){
            locDialog.dismiss();
        }
        GeomagneticField geomagneticField = new GeomagneticField(
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude(),
                System.currentTimeMillis()
        );
        declination = geomagneticField.getDeclination();
        Log.e("LATITUDE SEKARANG", String.valueOf(location.getLatitude()));
        Log.e("LONGITUDE SEKARANG", String.valueOf(location.getLongitude()));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(mActivity,
                "Error: Conn Failed",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        sensorManager.unregisterListener(this);
        Log.e("DESTROY", "DESTROY");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public static float exponentialSmoothing(float input, float output, float alpha) {
        output = output + alpha * (input - output);
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.e("MASUK SENSORCHANGED", "MASUK SENSORCHANGED");
//        Log.e("INI BEARING", String.valueOf(event.values[0]));
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION && googleMap != null && isNavMode){
            long curTime = System.currentTimeMillis();
            if ((curTime-lastTime) > 1000){
                lastTime = curTime;
                bearingHasil = exponentialSmoothing(bearingHasil, (float) (event.values[0] + declination), 0.33f);
//                bearingHasil = (float) (event.values[0] + declination);
                CameraPosition pos = new CameraPosition.Builder(googleMap.getCameraPosition())
                        .bearing(bearingHasil)
                        .zoom(19)
                        .tilt(60)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

//            sensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
//            float[] orientation = new float[3];
//            sensorManager.getOrientation(rotationMatrix, orientation);
//            double bearing = Math.toDegrees(orientation[0]) + declination;
//                Log.e("INI BEARING", "INI BEARING");
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class RepeatRoute extends AsyncTask<URL, Integer, Long>{

        @Override
        protected Long doInBackground(URL... params) {
            for (int i = 0; i<jarakDouble; i+=50){
                LatLng titik = utility.getPointAtDistance(polyRoute, i);
//                            Log.e("INI I", String.valueOf(i));
//                            Log.e("TITIKRANGE", titik.toString());
                if (titik != null){
                    newMarker("titik ke "+String.valueOf(i), titik, "titik"+String.valueOf(i));
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading route");
//            progressDialog.setMax();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
