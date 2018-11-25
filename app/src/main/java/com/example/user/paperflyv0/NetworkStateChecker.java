package com.example.user.paperflyv0;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private Database database;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        database = new Database(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = database.getUnsyncedassignment();
                if (cursor.moveToFirst()) {
                    do {
                       /* String executive_name = cursor.getString(0);
                        String executive_code = cursor.getString(1);
                        String order_count = cursor.getString(2);
                        String merchant_code = cursor.getString(3);
                        String assigned_by = cursor.getString(4);
                        String created_at = cursor.getString(5);
                        int id = cursor.getInt(7);*/
                        //calling the method to save the unsynced name to MySQL
                        saveName(cursor.getInt(7),cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    private void saveName(final int id, final String executive_name,final String executive_code, final String order_count,final String merchant_code,final String assigned_by,final String created_at) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.0.114/new/insertassign.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                database.updateAssignStatus(id, AssignPickup_Manager.NAME_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(AssignPickup_Manager.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("executive_name", executive_name);
                params.put("executive_code", executive_code);
                params.put("order_count", order_count);
                params.put("merchant_code", merchant_code);
                params.put("assigned_by", assigned_by);
                params.put("created_at", created_at);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}