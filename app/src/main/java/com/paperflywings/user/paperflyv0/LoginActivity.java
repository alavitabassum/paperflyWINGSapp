package com.paperflywings.user.paperflyv0;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button signin;
    EditText username,pass;
    private boolean loggedIn = false;
    private String userRole;
    Button tempButton;
    BarcodeDbHelper barcodedb;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signin = (Button) findViewById(R.id.sign_in);
        username = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.pass);
        // tempButton = findViewById(R.id.temp_btn);

        barcodedb=new BarcodeDbHelper(getApplicationContext());
        barcodedb.getWritableDatabase();
        db = new Database(getApplicationContext());
        db.getWritableDatabase();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

                //validate(username.getText().toString(),pass.getText().toString());

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);
        userRole = sharedPreferences.getString(Config.USER_ROLE_SHARED_PREF,"");

        //If we will get true
        if(loggedIn ){
            //We will start the Welcome Activity
            if(userRole.contains("1")) {
                Intent intent = new Intent(LoginActivity.this, ManagerCardMenu.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(LoginActivity.this, ExecutiveCardMenu.class);
                startActivity(intent);
            }
        }
    }


    public void login()
    {
        //Getting values from edit texts
        final String user = username.getText().toString().trim();
        final String pas = pass.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, "http://paperflybd.com/la.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.contains("failure")){
                    Toast.makeText(getApplicationContext(),"Please login with correct Name or Password!",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    try {

                        JSONArray arr = new JSONArray(response);
                        JSONObject jObj = arr.getJSONObject(0);
                        String userRole = jObj.getString("userRole");

                        //Creating a shared preference
                        SharedPreferences sharedPreferences1 = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Creating editor to store values to shared preferences
                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                        editor.putString(Config.USER_ROLE_SHARED_PREF, userRole);
                        //Saving values to editor
                        editor.commit();

                        if (userRole.contains("1")) {
                            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                            db.clearPTMList(sqLiteDatabase);
                            db.deletemerchantList(sqLiteDatabase);
                            db.deletemerchantList_Fulfillment(sqLiteDatabase);
                            db.deletemerchantList_ajkerDeal(sqLiteDatabase);
                            db.deletemerchantList_ajkerDealEkshopList(sqLiteDatabase);
                            db.deletemerchantList_ajkerDealOtherList(sqLiteDatabase);
                            db.deletemerchants(sqLiteDatabase);
                            db.deletemerchantsfor_executives(sqLiteDatabase);
                            db.deletecom_ex(sqLiteDatabase);
                            db.delete_fullfillment_merchantList(sqLiteDatabase);
                            db.deletecom_fulfillment_supplier(sqLiteDatabase);
                            db.deletecom_fullfillment_product(sqLiteDatabase);

                            startActivity(new Intent(getApplicationContext(),ManagerCardMenu.class));
                        } else {

                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            final String match_date = df.format(c);

                            SQLiteDatabase sqLiteDatabase = barcodedb.getWritableDatabase();
                            barcodedb.deleteAssignedList(sqLiteDatabase);
                            barcodedb.barcode_factory(sqLiteDatabase,match_date);
                            barcodedb.barcode_factory_fulfillment(sqLiteDatabase,match_date);

                            startActivity(new Intent(getApplicationContext(),ExecutiveCardMenu.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(),"Successfully Logged In",Toast.LENGTH_SHORT).show();
                    //Creating a shared preference
                    SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();


                    //Adding values to editor
                    editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                    editor.putString(Config.EMAIL_SHARED_PREF, user);


                    //Saving values to editor
                    editor.commit();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",user);
                params.put("pass",pas);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    @Override
    public void onBackPressed() {
        finish();
    }


}