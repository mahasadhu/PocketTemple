package com.maha.leviathan.pockettemple.usermanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.maha.leviathan.pockettemple.Main;
import com.maha.leviathan.pockettemple.controller.PocketTempleController;
import com.maha.leviathan.pockettemple.R;
import com.maha.leviathan.pockettemple.other.Utility;

import org.json.JSONArray;
import org.json.JSONException;

public class Account extends ActionBarActivity {

    Button gotoregist, signIn, logout;
    EditText username, pass;
    TextView namaLoggedin, emailLoggedIn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    final String TAG = Account.class.getSimpleName();

    JsonArrayRequest login;
    Utility utility;

    final Context context = Account.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedprefname), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        utility = new Utility();
        if (sharedPreferences.getBoolean("logged", false) == true){
            setContentView(R.layout.account_loggedin);

            logout = (Button) findViewById(R.id.buttonLogOut);
            namaLoggedin = (TextView) findViewById(R.id.textViewNamaLoggedIn);
            emailLoggedIn = (TextView) findViewById(R.id.textViewEmailLoggedIn);

            namaLoggedin.setText(sharedPreferences.getString("nama", "usernameNull"));
            emailLoggedIn.setText(sharedPreferences.getString("email", "emailNull"));

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.clear();
                    editor.commit();

                    Intent i = new Intent(Account.this, Main.class);
                    startActivity(i);
                    finish();
                }
            });
        }
        else {
            setContentView(R.layout.activity_account);

            gotoregist = (Button) findViewById(R.id.buttonGoToRegister);
            signIn = (Button) findViewById(R.id.buttonSignIn);
            username = (EditText) findViewById(R.id.editTextUsername);
            pass = (EditText) findViewById(R.id.editTextPassword);

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!username.getText().toString().equals("") && !pass.getText().toString().equals("")){
                        final ProgressDialog pDialog = new ProgressDialog(Account.this);
                        pDialog.setMessage("Please Wait...");
                        pDialog.show();

                        final AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(Account.this);
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        String url = "http://" + Utility.servernya + "/sipura/includes/web-services.php?flag=mobileLogin&username="+username.getText().toString()+"&password="+pass.getText().toString();

                        login = new JsonArrayRequest(Request.Method.POST, url, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    if (response.getString(0).equals("fail")){
                                        pDialog.dismiss();
                                        dialog.setMessage("Login Failed. Please Try Again.");
                                        dialog.show();
                                    }
                                    else {
                                        pDialog.dismiss();
                                        editor.putBoolean("logged", true);
                                        editor.putString("id", response.getString(0));
                                        editor.putString("nama", response.getString(1));
                                        editor.putString("email", response.getString(2));
                                        editor.commit();

                                        Intent i = new Intent(Account.this, Main.class);
                                        startActivity(i);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                                Toast.makeText(getApplicationContext(),
//                                        error.getMessage(), Toast.LENGTH_LONG).show();
//                                pDialog.dismiss();
                                utility.repeatRequestVolley(login, null, null, Account.this);
                            }
                        });

                        PocketTempleController.getInstance().addToRequestQueue(login);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please fill Username and Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            gotoregist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Account.this, Registrasi.class);
                    startActivity(i);
                }
            });
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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
    public void onBackPressed() {
//        super.onBackPressed();
        Intent i = new Intent(Account.this, Main.class);
        startActivity(i);
        finish();
    }
}
