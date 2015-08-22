package com.maha.leviathan.pockettemple.other;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.SphericalUtil;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;

import org.json.JSONObject;

/**
 * Created by Leviathan on 6/26/2015.
 */
public class Utility {

    public static double lat = 3000;
    public static double lng = 3000;

    public void repeatRequestVolley(final JsonArrayRequest reqArray, final Request<JSONObject> reqObj, final StringRequest reqStr, Activity mActivity){
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(mActivity);
        dialog.title("Request Error.");
        dialog.content("Failed to send request. Please Try Again.");
        dialog.positiveText("Retry");
        dialog.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (reqArray != null){
                    PocketTempleController.getInstance().addToRequestQueue(reqArray);
                }
                else if (reqObj != null){
                    PocketTempleController.getInstance().addToRequestQueue(reqObj);
                }
                else if (reqStr != null){
                    PocketTempleController.getInstance().addToRequestQueue(reqStr);
                }
            }
        });
        dialog.show();
    }

    public double distanceFrom(LatLng newLatlng, LatLng oldLatLng){
        double radiusBumi = 6378137.0;
        double oldLat = oldLatLng.latitude;
        double oldLng = oldLatLng.longitude;
        double newLat = newLatlng.latitude;
        double newLng = newLatlng.longitude;

        double dLat = (newLat-oldLat) * Math.PI / 180;
        double dLng = (newLng-oldLng) * Math.PI / 180;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(oldLat * Math.PI / 180) * Math.cos(newLat * Math.PI / 180) *
                        Math.sin(dLng / 2) * Math.sin(dLng/2);

        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double c = radiusBumi * b;
//        Log.e("INI C", String.valueOf(c));
        return c;
    }

    public LatLng getPointAtDistance(Polyline polyline, double rangeJarak){
        int a = 1;
        if (rangeJarak == 0) return polyline.getPoints().get(0);
        if (polyline.getPoints().size() < 2) return null;
        double jarak = 0;
        double oldJarak = 0;
        for (int i = 1; (i < polyline.getPoints().size() && jarak < rangeJarak); i++){
            a++;
            oldJarak = jarak;
            LatLng latLng = polyline.getPoints().get(i);
            jarak += distanceFrom(latLng, polyline.getPoints().get(i-1));
//            jarak += SphericalUtil.computeDistanceBetween(polyline.getPoints().get(i-1), polyline.getPoints().get(i));
        }
        if (jarak < rangeJarak) return null;

        LatLng pos1 = polyline.getPoints().get(a-2);
        LatLng pos2 = polyline.getPoints().get(a-1);
        double m = (rangeJarak - oldJarak)/(jarak - oldJarak);

        return new LatLng(pos1.latitude + (pos2.latitude-pos1.latitude) * m, pos1.longitude + (pos2.longitude-pos1.longitude) * m);
    }
}
