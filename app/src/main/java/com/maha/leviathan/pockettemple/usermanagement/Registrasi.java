package com.maha.leviathan.pockettemple.usermanagement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;
import com.maha.leviathan.pockettemple.R;
import com.maha.leviathan.pockettemple.other.Utility;

import java.util.HashMap;
import java.util.Map;

public class Registrasi extends ActionBarActivity {

    EditText email, nama, username, pass1, pass2;
    Button reg;
    int succ = 0;

    StringRequest reqRegister;
    Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        email = (EditText) findViewById(R.id.editTextEmailRegist);
        nama = (EditText) findViewById(R.id.editTextNamaUser);
        username = (EditText) findViewById(R.id.editTextUsernameRegist);
        pass1 = (EditText) findViewById(R.id.editTextPasswordRegist);
        pass2 = (EditText) findViewById(R.id.editTextRepeatPassword);

        reg = (Button) findViewById(R.id.buttonRegister);

        utility = new Utility();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String TAG = Registrasi.class.getSimpleName();
                if (pass1.getText().toString().equals(pass2.getText().toString())){
                    final ProgressDialog pDialog = new ProgressDialog(Registrasi.this);
                    final AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(Registrasi.this);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (succ == 1){
                                finish();
                            }
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (succ == 1){
                                finish();
                            }
                        }
                    });
                    // Showing progress dialog before making http request
                    pDialog.setMessage("Please Wait...");
                    pDialog.show();

                    String url = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=mobileRegist";
                    reqRegister = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.toString().equals("0")){
                                pDialog.dismiss();
                                dialog.setMessage("Registration Success. Please Wait for Admin Approval.");
                                dialog.show();

                                succ = 1;

                                email.setText("");
                                nama.setText("");
                                username.setText("");
                                pass1.setText("");
                                pass2.setText("");
                            }
                            else if (response.toString().equals("1")){
                                pDialog.dismiss();
                                dialog.setMessage("Username Already Taken. Please Try Again");
                                dialog.show();
                            }
                            else if (response.toString().equals("2")){
                                pDialog.dismiss();
                                dialog.setMessage("Password Already Taken. Please Try Again");
                                dialog.show();
                            }
                            else if (response.toString().equals("3")){
                                pDialog.dismiss();
                                dialog.setMessage("Registration Failed. Please Try Again");
                                dialog.show();
                            }
//                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
//                            Toast.makeText(getApplicationContext(),
//                                    error.getMessage(), Toast.LENGTH_LONG).show();
//                            pDialog.dismiss();
                            utility.repeatRequestVolley(null, null, reqRegister, Registrasi.this);

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("username", username.getText().toString());
                            params.put("password", pass1.getText().toString());
                            params.put("nama", nama.getText().toString());
                            params.put("email", email.getText().toString());

                            return params;
                        }
                    };

                    PocketTempleController.getInstance().addToRequestQueue(reqRegister);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_registrasi, menu);
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
