package com.maha.leviathan.pockettemple;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.AlertDialogWrapper;


public class Splash extends ActionBarActivity {

    AlertDialogWrapper OptionDialog;
    double diagonalInches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width=dm.widthPixels;
//        int height=dm.heightPixels;
//        int dens=dm.densityDpi;
//        double wi=(double)width/(double)dens;
//        double hi=(double)height/(double)dens;
//        double x = Math.pow(wi,2);
//        double y = Math.pow(hi,2);
//        diagonalInches = Math.sqrt(x+y);
//        Log.e("UKURAN", String.valueOf(diagonalInches));
//        if (diagonalInches>=2){
////            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            // 5inch device or bigger
//        }else{
////            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            // smaller device
//        }

        cekLoc();
    }

    public void cekLoc(){
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled = false;
        if(lm==null)
            lm = (LocationManager) Splash.this.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled && !network_enabled){
            final AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(Splash.this);
//            OptionDialog = dialog.create();
            dialog.setMessage("Your Location Service is Not Activated");
            dialog.setPositiveButton("Open Location Setting", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
//                    if (OptionDialog.isShowing()){
//                        OptionDialog.dismiss();
//                    }
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Splash.this.startActivity(myIntent);
                    finish();
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
//                    if (OptionDialog.isShowing()){
//                        OptionDialog.dismiss();
//                    }
                    Intent i = new Intent(Splash.this, Main.class);
                    i.putExtra("reso", diagonalInches);
                    startActivity(i);
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        }
        else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(Splash.this, Main.class));
                    finish();
                }
            }, 1500);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("SPLASH RESTART", "SPLASH RESTART");
        cekLoc();
    }
}
