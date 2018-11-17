package com.example.user.paperflyv0;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignPickup_Manager extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AssignExecutiveAdapter.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener{

    String[] executive_num_list;
    public static final String MERCHANT_NAME = "Merchant Name";
    private String EXECUTIVE_URL = "http://paperflybd.com/executiveList.php";
    private String INSERT_URL = "http://192.168.0.117/new/insertassign.php";
    private String MERCHANT_URL= "http://192.168.0.117/new/merchantlistt.php";
    // private String MERCHANT_URL = "http://192.168.0.102/new/merchantlist.php";
    private AssignExecutiveAdapter assignExecutiveAdapter;
    List<AssignManager_ExecutiveList> executiveLists;
    List<AssignManager_Model> assignManager_modelList;
    Database database;
    int count =0;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //    RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_pickup__manager);
        database = new Database(getApplicationContext());
        database.getWritableDatabase();

        executiveLists = new ArrayList<>();
        assignManager_modelList = new ArrayList<>();

        //Fetching email from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        String user = username.toString();

        //recycler with cardview
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_merchant);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getallmerchant();
        getallexecutives();
        loadmerchantlist(user);
        loadexecutivelist(user);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Load executive from api
    private void loadexecutivelist(final String user)
    {

        StringRequest postRequest1 = new StringRequest(Request.Method.POST, EXECUTIVE_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("executivelist");
                            for(int i =0;i<array.length();i++)
                            {
                                JSONObject o = array.getJSONObject(i);
                                database.addexecutivelist(o.getString("empName"),o.getString("empCode"));
                            }
                            getallexecutives();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String>  params1 = new HashMap<String,String>();
                params1.put("username",user);
                return params1;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest1);
    }
    //Get Executive List from sqlite
    private void getallexecutives()
    {
        try{

            SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();
            Cursor c = database.get_executivelist(sqLiteDatabase);
            while (c.moveToNext())
            {
                String empName = c.getString(0);
                String empCode = c.getString(1);
                AssignManager_ExecutiveList assignManager_executiveList = new AssignManager_ExecutiveList(empName,empCode);
                executiveLists.add(assignManager_executiveList);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Merchant List API hit
    private void loadmerchantlist(final String user)
    {

        StringRequest postRequest1 = new StringRequest(Request.Method.POST,MERCHANT_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("merchantlist");
                            for(int i =0;i<array.length();i++)
                            {
                                JSONObject o = array.getJSONObject(i);
                                database.addmerchantlist(o.getString("merchantName"),o.getString("merchantCode"));
                            }
                            getallmerchant();


                        } catch (JSONException e) {
                            e.printStackTrace();


                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Check Your Internet Connection" ,Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String>  params1 = new HashMap<String,String>();
                params1.put("username",user);
                return params1;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest1);
    }

    // merchant List generation from sqlite
    private void getallmerchant()
    {
        try{

            SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();
            Cursor c = database.get_merchantlist(sqLiteDatabase);
            while (c.moveToNext())
            {
                String merchantName = c.getString(0);
                String merchantCode = c.getString(1);
                String assigned = c.getString(2);
                String executive1 = c.getString(3);
                AssignManager_Model todaySummary = new AssignManager_Model(merchantName,merchantCode,assigned,executive1);
                assignManager_modelList.add(todaySummary);
            }
            assignExecutiveAdapter = new AssignExecutiveAdapter(assignManager_modelList,getApplicationContext());
            recyclerView.setAdapter(assignExecutiveAdapter);
            assignExecutiveAdapter.setOnItemClickListener(AssignPickup_Manager.this);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /*private void assignexecutive(final String ex_name, final String order_count,final String merchant_code)
    {
              database.insert_assignexecutive(ex_name,order_count,merchant_code);
    }*/
    //For assigning executive API into mysql
    private void assignexecutive(final String ex_name, final String order_count,final String merchant_code,final String user, final String currentDateTimeString) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, INSERT_URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //   Log.d("Error",error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("executive_name",ex_name);
                params.put("order_count",order_count);
                params.put("merchant_code",merchant_code);
                params.put("assigned_by",user);
                params.put("created_at",currentDateTimeString);
                database.insert_assignexecutive(ex_name,order_count,merchant_code,user,currentDateTimeString);
                final int total_assign = database.getTotalOfAmount(merchant_code);
                final String strI = String.valueOf(total_assign);
                database.update_row(strI,merchant_code,ex_name);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pickups_today__manager, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeIntent = new Intent(AssignPickup_Manager.this,
                    ManagerCardMenu.class);
            startActivity(homeIntent);
        }
        else if (id == R.id.nav_pickDue) {
            Intent pickupIntent = new Intent(AssignPickup_Manager.this,
                    PickupsToday_Manager.class);
            startActivity(pickupIntent);
        } else if (id == R.id.nav_assign) {
            Intent assignIntent = new Intent(AssignPickup_Manager.this,
                    AssignPickup_Manager.class);
            startActivity(assignIntent);
        } else if (id == R.id.nav_pickCompleted) {
            Intent historyIntent = new Intent(AssignPickup_Manager.this,
                    PickupHistory_Manager.class);
            startActivity(historyIntent);
        } else if (id == R.id.nav_logout) {
            //Creating an alert dialog to confirm logout
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to logout?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            //Getting out sharedpreferences
                            SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                            //Getting editor
                            SharedPreferences.Editor editor = preferences.edit();

                            //Puting the value false for loggedin
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                            //Putting blank value to email
                            editor.putString(Config.EMAIL_SHARED_PREF, "");

                            //Saving the sharedpreferences
                            editor.commit();

                            //Starting login activity
                            Intent intent = new Intent(AssignPickup_Manager.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            //Showing the alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onItemClick(View view,int position) {

        final AssignManager_Model clickeditem = assignManager_modelList.get(position);
      //  final TextView selection1 =findViewById(R.id.selection1);


        AlertDialog.Builder spinnerBuilder = new AlertDialog.Builder(AssignPickup_Manager.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner,null);
        spinnerBuilder.setTitle("Select executive and assign number.");
        final TextView dialog_mName =  mView.findViewById(R.id.dialog_m_name);
        final Spinner mSpinner1 = mView.findViewById(R.id.spinner1);
        final EditText et1 = mView.findViewById(R.id.spinner1num);
        dialog_mName.setText(clickeditem.getM_names());
        final String merchant_code = clickeditem.getM_address();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        final String user = username.toString();

        final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


        List<String> lables = new ArrayList<String>();

        for (int z = 0; z < executiveLists.size(); z++) {
            lables.add(executiveLists.get(z).getExecutive_name());
        }

        ArrayAdapter<String>  adapter = new ArrayAdapter<String>(AssignPickup_Manager.this,
                android.R.layout.simple_spinner_item,
                lables);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner1.setAdapter(adapter);

        spinnerBuilder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i1) {
                assignexecutive(mSpinner1.getSelectedItem().toString(),et1.getText().toString(),merchant_code,user,currentDateTimeString);
                finish();
                startActivity(getIntent());
                if (!mSpinner1.getSelectedItem().toString().equals(null)){
                    Toast.makeText(AssignPickup_Manager.this, mSpinner1.getSelectedItem().toString()
                                    +"("+et1.getText().toString() +")",
                            Toast.LENGTH_SHORT).show();


                   // selection1.setText(mSpinner1.getSelectedItem().toString());
                    // selection1.setTextColor(getResources().getColor(R.color.pfColor));
                    dialog.dismiss();

                }

            }
        });
        spinnerBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i1) {
                dialog.dismiss();
            }
        });
        // vwParentRow2.refreshDrawableState();
        spinnerBuilder.setView(mView);
        AlertDialog dialog2 = spinnerBuilder.create();
        dialog2.show();
    }

    @Override
    public void onRefresh() {
        assignManager_modelList.clear();
        //Fetching email from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        String user = username.toString();
        loadmerchantlist(user);
    }
}