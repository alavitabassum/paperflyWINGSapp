package com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryOfficerCTS;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paperflywings.user.paperflyv0.Config;
import com.paperflywings.user.paperflyv0.Databases.BarcodeDbHelper;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryBankDepositInfoUpdate.DeliveryOfficerBankInfoAdd;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryOfficerCRS.DeliveryCashRS;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryOfficerLandingPageTabLayout.DeliveryTablayout;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryOfficerOnHold.DeliveryOnHold;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryOfficerPettyCash.DeliveryAddNewExpense;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryOfficerPreReturn.ReturnRequest;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryOfficerRTS.Delivery_ReturnToSupervisor;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveryOfficerUnpicked.DeliveryOfficerUnpicked;
import com.paperflywings.user.paperflyv0.DeliveryApp.DeliveryOfficer.DeliveyrOfficerWithoutStatus.DeliveryWithoutStatus;
import com.paperflywings.user.paperflyv0.LoginActivity;
import com.paperflywings.user.paperflyv0.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryCTS extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DeliveryCTSAdapter.OnItemClickListtener, SwipeRefreshLayout.OnRefreshListener {

    BarcodeDbHelper db;
    private long mLastClickTime = 0;
    public SwipeRefreshLayout swipeRefreshLayout;
    private TextView CashCount_text,totalCollection, btnselect, btndeselect;
    private DeliveryCTSAdapter DeliveryCTSAdapter;
    RecyclerView recyclerView_pul;
    RecyclerView.LayoutManager layoutManager_pul;
    private RequestQueue requestQueue;
    private Button btnnext, delivery_cts_recieved;
    ProgressDialog progressDialog;

    String itemOrders = "";
    String itemPrimaryIds = "";
    String totalCash = "";
    String totalCash1 = "";
    public final String TOTAL_CASH = "total_cash";
    public final String TOTAL_ORDER= "total_order";

    public static final String DELIVERY_CTS_UPDATE_All = "http://paperflybd.com/DeliverySupervisorAPI.php";

    public static final String CTS_LIST = "http://paperflybd.com/DeliveryCashToSuperVisor.php";
    //public static final String DELIVERY_CTS_UPDATE = "http://paperflybd.com/DeliverySupervisorCTSinBatch.php";

    private ArrayList<DeliveryCTSModel> list;
    private ArrayList<DeliveryCTSModel> eList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=new BarcodeDbHelper(getApplicationContext());
        db.getWritableDatabase();

        setContentView(R.layout.activity_delivery_cts);
        btnselect = (TextView) findViewById(R.id.select);
        btndeselect = (TextView) findViewById(R.id.deselect);
        btnnext = (Button) findViewById(R.id.next);
        CashCount_text = (TextView)findViewById(R.id.CTS_id_);
        totalCollection = (TextView)findViewById(R.id.CashCollection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView_pul = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView_pul.setAdapter(DeliveryCTSAdapter);
        list = new ArrayList<DeliveryCTSModel>();
        eList = new ArrayList<DeliveryCTSModel>();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");

        ConnectivityManager cManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

        layoutManager_pul = new LinearLayoutManager(this);
        recyclerView_pul.setLayoutManager(layoutManager_pul);

        delivery_cts_recieved = (Button) findViewById(R.id.cash_recieved_by_supervisor);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        list.clear();
        eList.clear();

        swipeRefreshLayout.setRefreshing(true);

        if(nInfo!= null && nInfo.isConnected())
        {
            loadRecyclerView(username);
        }
        else{
            getData(username);
            Toast.makeText(this,"Check Your Internet Connection",Toast.LENGTH_LONG).show();
        }

        delivery_cts_recieved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveryCTS.this,
                        DeliveryCashRS.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout_CTS);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.delivery_officer_name);
        navUsername.setText(username);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getData(String user){
        try{
            list.clear();
            SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
            Cursor c = db.get_delivery_CTS(sqLiteDatabase,user, "cts", "Y");

            while (c.moveToNext()){
                int id = c.getInt(0);
                String dropPointCode = c.getString(1);
                String barcode = c.getString(2);
                String orderid = c.getString(3);
                String merOrderRef = c.getString(4);
                String merchantName = c.getString(5);
                String pickMerchantName = c.getString(6);
                String custname = c.getString(7);
                String custphone = c.getString(8);
                String custaddress = c.getString(9);
                String packagePrice = c.getString(10);
                String productBrief = c.getString(11);
                String deliveryTime = c.getString(12);
                String username = c.getString(13);
                String empCode = c.getString(14);
                String cash = c.getString(15);
                String cashType = c.getString(16);
                String cashTime = c.getString(17);
                String cashBy = c.getString(18);
                String cashAmt = c.getString(19);
                String cashComment = c.getString(20);
                String partial = c.getString(21);
                String partialTime = c.getString(22);
                String partialBy = c.getString(23);
                String partialReceive = c.getString(24);
                String partialReturn = c.getString(25);
                String partialReason = c.getString(26);
                String onHoldSchedule = c.getString(27);
                String onHoldReason = c.getString(28);
                String rea = c.getString(29);
                String reaTime = c.getString(30);
                String reaBy = c.getString(31);
                String ret = c.getString(32);
                String retTime = c.getString(33);
                String retBy = c.getString(34);
                String retReason = c.getString(35);
                String rts = c.getString(36);
                String rtsTime = c.getString(37);
                String rtsBy = c.getString(38);
                String preRet = c.getString(39);
                String preRetTime = c.getString(40);
                String preRetBy = c.getString(41);
                String cts = c.getString(42);
                String ctsTime = c.getString(43);
                String ctsBy = c.getString(44);
                String slaMiss = c.getString(45);
                String flagReq = c.getString(46);
                int status = c.getInt(47);

                DeliveryCTSModel withoutStatus_model = new DeliveryCTSModel(id,dropPointCode,barcode,orderid,merOrderRef,merchantName,pickMerchantName,custname,custaddress,custphone,packagePrice,productBrief,deliveryTime,username,empCode,cash,cashType,cashTime,cashBy,cashAmt,cashComment,partial,partialTime,partialBy,partialReceive,partialReturn,partialReason,onHoldSchedule,onHoldReason,rea,reaTime,reaBy,ret,retTime,retBy,retReason,rts,rtsTime,rtsBy,preRet,preRetTime,preRetBy,cts,ctsTime,ctsBy,slaMiss,flagReq, status);

                list.add(withoutStatus_model);
            }

            DeliveryCTSAdapter = new DeliveryCTSAdapter(list,getApplicationContext());
            recyclerView_pul.setAdapter(DeliveryCTSAdapter);
            DeliveryCTSAdapter.notifyDataSetChanged();
            DeliveryCTSAdapter.setOnItemClickListener(DeliveryCTS.this);
            swipeRefreshLayout.setRefreshing(false);

            String str = String.valueOf(db.getCashCount("cts", "Y"));
            CashCount_text.setText(str);
            swipeRefreshLayout.setRefreshing(false);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadRecyclerView (final String user){
        list.clear();
        list = getModel(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CTS_LIST,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        int i1 =0;
                        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                        db.deleteListCTS(sqLiteDatabase, "cts");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("summary");

                            for(i1 =0;i1<array.length();i1++)
                            {
                                JSONObject o = array.getJSONObject(i1);
                                DeliveryCTSModel withoutStatus_model = new  DeliveryCTSModel(
                                        o.getInt("sql_primary_id"),
                                        o.getString("username"),
                                        o.getString("merchEmpCode"),
                                        o.getString("dropPointCode"),
                                        o.getString("barcode"),
                                        o.getString("orderid"),
                                        o.getString("merOrderRef"),
                                        o.getString("merchantName"),
                                        o.getString("pickMerchantName"),
                                        o.getString("custname"),
                                        o.getString("custaddress"),
                                        o.getString("custphone"),
                                        o.getString("packagePrice"),
                                        o.getString("productBrief"),
                                        o.getString("deliveryTime"),
                                        o.getString("Cash"),
                                        o.getString("cashType"),
                                        o.getString("CashTime"),
                                        o.getString("CashBy"),
                                        o.getString("CashAmt"),
                                        o.getString("CashComment"),
                                        o.getString("partial"),
                                        o.getString("partialTime"),
                                        o.getString("partialBy"),
                                        o.getString("partialReceive"),
                                        o.getString("partialReturn"),
                                        o.getString("partialReason"),
                                        o.getString("onHoldReason"),
                                        o.getString("onHoldSchedule"),
                                        o.getString("Rea"),
                                        o.getString("ReaTime"),
                                        o.getString("ReaBy"),
                                        o.getString("Ret"),
                                        o.getString("RetTime"),
                                        o.getString("RetBy"),
                                        o.getString("retRem"),
                                        o.getString("retReason"),
                                        o.getString("RTS"),
                                        o.getString("RTSTime"),
                                        o.getString("RTSBy"),
                                        o.getString("PreRet"),
                                        o.getString("PreRetTime"),
                                        o.getString("PreRetBy"),
                                        o.getString("CTS"),
                                        o.getString("CTSTime"),
                                        o.getString("CTSBy"),
                                        o.getString("slaMiss"));

                                db.insert_delivery_CTS(
                                        o.getInt("sql_primary_id"),
                                        o.getString("username"),
                                        o.getString("merchEmpCode"),
                                        o.getString("barcode"),
                                        o.getString("orderid"),
                                        o.getString("merOrderRef"),
                                        o.getString("merchantName"),
                                        o.getString("pickMerchantName"),
                                        o.getString("custname"),
                                        o.getString("custaddress"),
                                        o.getString("custphone"),
                                        o.getString("packagePrice"),
                                        o.getString("productBrief"),
                                        o.getString("deliveryTime"),
                                        o.getString("dropPointCode"),
                                        o.getString("Cash"),
                                        o.getString("cashType"),
                                        o.getString("CashTime"),
                                        o.getString("CashBy"),
                                        o.getString("CashAmt"),
                                        o.getString("CashComment"),
                                        o.getString("partial"),
                                        o.getString("partialTime"),
                                        o.getString("partialBy"),
                                        o.getString("partialReceive"),
                                        o.getString("partialReturn"),
                                        o.getString("partialReason"),
                                        o.getString("onHoldSchedule"),
                                        o.getString("onHoldReason"),
                                        o.getString("Rea"),
                                        o.getString("ReaTime"),
                                        o.getString("ReaBy"),
                                        o.getString("Ret"),
                                        o.getString("RetTime"),
                                        o.getString("RetBy"),
                                        o.getString("retRem"),
                                        o.getString("retReason"),
                                        o.getString("RTS"),
                                        o.getString("RTSTime"),
                                        o.getString("RTSBy"),
                                        o.getString("PreRet"),
                                        o.getString("PreRetTime"),
                                        o.getString("PreRetBy"),
                                        o.getString("CTS"),
                                        o.getString("CTSTime"),
                                        o.getString("CTSBy"),
                                        o.getInt("slaMiss"),
                                        "cts"
                                        , 1);
                                list.add(withoutStatus_model);
                            }

                            DeliveryCTSAdapter = new DeliveryCTSAdapter(list,getApplicationContext());
                            recyclerView_pul.setAdapter(DeliveryCTSAdapter);
                            swipeRefreshLayout.setRefreshing(false);
                            DeliveryCTSAdapter.setOnItemClickListener(DeliveryCTS.this);

                            btnselect.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    list = getModel(true);
                                    DeliveryCTSAdapter = new DeliveryCTSAdapter(list,getApplicationContext());
                                    recyclerView_pul.setAdapter(DeliveryCTSAdapter);
                                    DeliveryCTSAdapter.setOnItemClickListener(DeliveryCTS.this);
                                }
                            });

                            btndeselect.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    list = getModel(false);
                                    DeliveryCTSAdapter = new DeliveryCTSAdapter(list,getApplicationContext());
                                    recyclerView_pul.setAdapter(DeliveryCTSAdapter);
                                    DeliveryCTSAdapter.setOnItemClickListener(DeliveryCTS.this);
                                }
                            });

                            btnnext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // mis-clicking prevention, using threshold of 500 ms
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();

                                    final CharSequence [] values = {"Cash To Supervisor","Bank Deposite"};
                                    int count = 0;
                                    SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                    final String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
                                    final Intent intent = new Intent(DeliveryCTS.this, DeliveryCTS.class);
                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeliveryCTS.this);
                                    final View mView = getLayoutInflater().inflate(R.layout.activity_next, null);
                                    final TextView tv = mView.findViewById(R.id.tv);
                                    final TextView tv1 = mView.findViewById(R.id.tv1);
                                    final EditText totalcashes = mView.findViewById(R.id.cash_Collection_text);
                                    final EditText cashComment = mView.findViewById(R.id.cash_comment_text);
                                    final TextView  orderIds = mView.findViewById(R.id.cash_amount);

                                    // Employee List
                                    getEmployeeList();
                                    final Spinner mEmployeeSpinner = (Spinner) mView.findViewById(R.id.employee_name);
                                    List<String> empList = new ArrayList<String>();
                                    empList.add(0,"Select employee...");
                                    for (int x = 0; x < eList.size(); x++) {
                                        empList.add(eList.get(x).getEmpName());
                                    }
                                    ArrayAdapter<String> adapterEmpListR = new ArrayAdapter<String>(DeliveryCTS.this,
                                            android.R.layout.simple_spinner_item,
                                            empList);
                                    adapterEmpListR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    mEmployeeSpinner.setAdapter(adapterEmpListR);


                                    for (int i = 0; i < DeliveryCTSAdapter.imageModelArrayList.size(); i++){
                                        if(DeliveryCTSAdapter.imageModelArrayList.get(i).getSelected()) {
                                            count++;
                                            itemOrders = itemOrders + "," + DeliveryCTSAdapter.imageModelArrayList.get(i).getOrderid();
                                            itemPrimaryIds = itemPrimaryIds + "," + DeliveryCTSAdapter.imageModelArrayList.get(i).getSql_primary_id();
                                        }
                                        tv.setText(count + " Orders have been selected for cash.");
                                    }
                                    //orderIds.setText(itemOrders);
                                    count = 0;
                                    float CashCount = 0.0f;
                                    for (int i = 0; i <DeliveryCTSAdapter.imageModelArrayList.size(); i++) {
                                        if(DeliveryCTSAdapter.list.get(i).getSelected()) {
                                            totalCash = String.valueOf(db.getTotalCash("cts"));
                                            CashCount = Float.parseFloat(totalCash);
                                            tv1.setText(""+CashCount);
                                            totalcashes.setText(""+CashCount);
                                        }
                                    }

                                    final AlertDialog.Builder spinnerBuilder = new AlertDialog.Builder(DeliveryCTS.this);
                                    spinnerBuilder.setTitle("Select Action: ");
                                    spinnerBuilder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, int item) {

                                                    switch (item) {
                                                        case 0:
                                                            // mis-clicking prevention, using threshold of 500 ms
                                                            if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                                                                return;
                                                            }
                                                            mLastClickTime = SystemClock.elapsedRealtime();

                                                           // int count=0;

                                                           AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeliveryCTS.this);


                                                            alertDialogBuilder.setPositiveButton("Submit",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface arg0, int arg1) {

                                                                        }
                                                                    });

                                                            alertDialogBuilder.setNegativeButton("Cancel",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                                            itemOrders = "";
                                                                            itemPrimaryIds = "";
                                                                        }
                                                                    });
                                                            alertDialogBuilder.setCancelable(false);
                                                            alertDialogBuilder.setView(mView);

                                                            final AlertDialog alertDialog = alertDialogBuilder.create();
                                                            alertDialog.show();

                                                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    // mis-clicking prevention, using threshold of 500 ms
                                                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                                                                        return;
                                                                    }
                                                                    mLastClickTime = SystemClock.elapsedRealtime();

                                                                    String totalCashs = tv1.getText().toString().trim();
                                                                    String CashComments = cashComment.getText().toString().trim();
                                                                    String CashCollected = totalcashes.getText().toString().trim();

                                                                    String empName = mEmployeeSpinner.getSelectedItem().toString();
                                                                    String empCode = db.getSelectedEmpCode(empName);

                                                                    if(tv.getText().equals("0 Orders have been selected for cash.") || tv.getText().equals("Please select orders first") ){
                                                                        orderIds.setText("Please Select Orders First!!");
                                                                    }
                                                                    else if(totalCashs.isEmpty()){
                                                                        orderIds.setText("Please enter required fields First!!");
                                                                    } else if(empName.equals("Select employee...")){
                                                                        orderIds.setText("Please select employee!!");
                                                                    }
                                                                    else if(CashComments.isEmpty()){
                                                                        orderIds.setText("Please enter required fields First!!");
                                                                    }
                                                                    else if(CashCollected.isEmpty()){
                                                                        orderIds.setText("Please enter required fields First!!");
                                                                    } else if (!totalCashs.equals(""+CashCollected)){
                                                                        orderIds.setText("Total cash and cash-collection amount mismatch! Remove the dispute order and try again!");
                                                                    }
                                                                    else {
                                                                        UpdateCashInfo(username,empCode,itemPrimaryIds,itemOrders,totalCashs,CashCollected,CashComments, "P");
                                                                        alertDialog.dismiss();
                                                                        startActivity(intent);
                                                                        //loadRecyclerView(username);
                                                                        itemOrders = "";
                                                                        itemPrimaryIds = "";
                                                                    }
                                                                }
                                                            });

                                                            break;
                                                        case 1:

                                                            dialog.dismiss();

                                                            AlertDialog.Builder alertDialogBuilderBank = new AlertDialog.Builder(DeliveryCTS.this);

                                                            float CashCount = 0.0f;
                                                            totalCash1 = String.valueOf(db.getTotalCash("cts"));
                                                            CashCount = Float.parseFloat(totalCash1);
                                                            alertDialogBuilderBank.setMessage("Are you sure you want to bank these orders: "+itemOrders);
                                                            final float finalCashCount = CashCount;
                                                            alertDialogBuilderBank.setPositiveButton("Yes",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                                            //itemOrders = "";
                                                                            if (itemOrders.equals("")){
                                                                                Toast.makeText(DeliveryCTS.this, "Select all orders first!", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                UpdateBankInfo(username,itemPrimaryIds,itemOrders, String.valueOf(finalCashCount), String.valueOf(finalCashCount),"","P");
                                                                            }
                                                                            dialog.dismiss();
                                                                        }
                                                                    });

                                                            alertDialogBuilderBank.setNegativeButton("No",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                                            itemOrders = "";
                                                                        }
                                                                    });
                                                            AlertDialog alertDialogBank = alertDialogBuilderBank.create();
                                                            alertDialogBank.show();

                                                            /*if (itemOrders.equals("")){
                                                                Toast.makeText(DeliveryCTS.this, "Select all orders!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                UpdateBankInfo(username,itemPrimaryIds,itemOrders, String.valueOf(CashCount), String.valueOf(CashCount),"","P");
                                                            }*/
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                }
                                            }
                                    );
                                    spinnerBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i1) {
                                            dialog.dismiss();
                                        }
                                    });

                                    spinnerBuilder.setCancelable(false);
                                    final AlertDialog dialog2 = spinnerBuilder.create();
                                    dialog2.show();
                                }
                            });

                            //String str = String.valueOf(db.getCashCount("cts", "Y"));
                            String str = String.valueOf(i1);
                            CashCount_text.setText(str);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // progress.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "Server not connected" ,Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params1 = new HashMap<String,String>();
                params1.put("username",user);
                return params1;
            }
        };

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(stringRequest);
    }

    private void getEmployeeList() {
        try {
            eList.clear();
            SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
            Cursor c = db.get_employee_list(sqLiteDatabase);
            while (c.moveToNext()) {
                Integer empId = c.getInt(0);
                String empCode = c.getString(1);
                String empName = c.getString(2);
                DeliveryCTSModel employeeList = new DeliveryCTSModel(empId,empCode,empName);
                eList.add(employeeList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_CTS);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent homeIntent = new Intent(DeliveryCTS.this,
                    DeliveryTablayout.class);
            startActivity(homeIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        try{  searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                DeliveryCTSAdapter.getFilter().filter(newText);
                return false;
            }
        });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Intent intent_stay = new Intent(DeliveryCTS.this, DeliveryWithoutStatus.class);
            Toast.makeText(this, "Page Loading...", Toast.LENGTH_SHORT).show();
            startActivity(intent_stay);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
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
            Intent homeIntent = new Intent(DeliveryCTS.this,
                    DeliveryTablayout.class);
            startActivity(homeIntent);
            // Handle the camera action
        } else if (id == R.id.nav_unpicked) {
            Intent homeIntent = new Intent(DeliveryCTS.this,
                    DeliveryOfficerUnpicked.class);
            startActivity(homeIntent);
            // Handle the camera action
        } else if (id == R.id.nav_without_status) {
            Intent homeIntent = new Intent(DeliveryCTS.this,
                    DeliveryWithoutStatus.class);
            startActivity(homeIntent);
            // Handle the camera action
        } else if (id == R.id.nav_on_hold) {
            Intent homeIntent = new Intent(DeliveryCTS.this,
                    DeliveryOnHold.class);
            startActivity(homeIntent);
            // Handle the camera action
        } else if (id == R.id.nav_return_request) {
            Intent homeIntent = new Intent(DeliveryCTS.this,
                    ReturnRequest.class);
            startActivity(homeIntent);
            // Handle the camera action
        } else if (id == R.id.nav_return) {
            Intent homeIntent = new Intent(DeliveryCTS.this,
                    Delivery_ReturnToSupervisor.class);
            startActivity(homeIntent);
            // Handle the camera action
        } else if (id == R.id.nav_new_expense) {
            Intent expenseIntent = new Intent(DeliveryCTS.this,
                    DeliveryAddNewExpense.class);
            startActivity(expenseIntent);
        } else if (id == R.id.nav_cash) {
            Intent homeIntent = new Intent(DeliveryCTS.this,
                    DeliveryCTS.class);
            startActivity(homeIntent);
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to logout?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            //Getting out sharedpreferences
                            SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            //Getting editor
                            SharedPreferences.Editor editor = preferences.edit();

                            //Puting the value false for loggedin
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                            //Putting blank value to email
                            editor.putString(Config.EMAIL_SHARED_PREF, "");

                            //Saving the sharedpreferences
                            editor.commit();

                            //Starting login activity
                            Intent intent = new Intent(DeliveryCTS.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_CTS);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRefresh() {
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        list.clear();

        DeliveryCTSAdapter.notifyDataSetChanged();
        if(nInfo!= null && nInfo.isConnected())
        {
            loadRecyclerView(username);
        }
        else{
            getData(username);
        }
    }

    private void UpdateCashInfo(final String createdBy,final String empCode,final String sqlPrimaryIds,final String items,final String totalCashAmt,final String submittedCashAmt,final String CashComment, final String type) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, DELIVERY_CTS_UPDATE_All,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(DeliveryCTS.this, "Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DeliveryCTS.this, "UnSuccessful", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DeliveryCTS.this, "Server disconnected!", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", createdBy);
                // This is the employee code to whom the CTS is transferred
                params.put("assignedToEmp", empCode);
                params.put("cashSubmissionType", type);
                params.put("sqlPrimaryId", sqlPrimaryIds);
                params.put("flagreq", "delivery_officer_CTS");
                params.put("orderid", items);
                params.put("totalCashAmt", totalCashAmt);
                params.put("submittedCashAmt", submittedCashAmt);
                params.put("comment", CashComment);
                return params;
            }
        };
        try {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Toast.makeText(DeliveryCTS.this, "Server Error! cts", Toast.LENGTH_LONG).show();
        }
    }

    private void UpdateBankInfo(final String createdBy,final String sqlPrimaryIds,final String items,final String totalCashAmt,final String submittedCashAmt,final String CashComment, final String type) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, DELIVERY_CTS_UPDATE_All,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Intent intentBankDeposite = new Intent(DeliveryCTS.this, DeliveryOfficerBankInfoAdd.class);
                                startActivity(intentBankDeposite);
                                Toast.makeText(DeliveryCTS.this, "Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DeliveryCTS.this, "UnSuccessful", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DeliveryCTS.this, "Server disconnected! "+error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", createdBy);
                params.put("cashSubmissionType", type);
                params.put("sqlPrimaryId", sqlPrimaryIds);
                params.put("flagreq", "delivery_officer_bank_orders");
                params.put("orderid", items);
                params.put("totalCashAmt", totalCashAmt);
                params.put("submittedCashAmt", submittedCashAmt);
                params.put("comment", CashComment);
                return params;
            }
        };
        try {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Toast.makeText(DeliveryCTS.this, "Server Error! cts", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<DeliveryCTSModel> getModel(boolean isSelect){
        ArrayList<DeliveryCTSModel> listOfOrders = new ArrayList<>();
        if(isSelect == true){
            String totalCash = String.valueOf(db.getTotalCash("cts"));
            totalCollection.setText(totalCash+" Taka");

            for(int i = 0; i < list.size(); i++){
                DeliveryCTSModel model = new DeliveryCTSModel();

                model.setSelected(isSelect);
                model.setOrderid(list.get(i).getOrderid());
                model.setSql_primary_id(list.get(i).getSql_primary_id());
                model.setMerOrderRef(list.get(i).getMerOrderRef());
                model.setCashAmt(list.get(i).getCashAmt());
                model.setPackagePrice(list.get(i).getPackagePrice());
                model.setCustname(list.get(i).getCustname());
                model.setCustaddress(list.get(i).getCustaddress());
                listOfOrders.add(model);
            }

        } else if(isSelect == false){
            totalCollection.setText("0 Taka");

            for(int i = 0; i < list.size(); i++){
                DeliveryCTSModel model = new DeliveryCTSModel();

                model.setSelected(isSelect);
                model.setOrderid(list.get(i).getOrderid());
                model.setSql_primary_id(list.get(i).getSql_primary_id());
                model.setMerOrderRef(list.get(i).getMerOrderRef());
                model.setCashAmt(list.get(i).getCashAmt());
                model.setPackagePrice(list.get(i).getPackagePrice());
                model.setCustname(list.get(i).getCustname());
                model.setCustaddress(list.get(i).getCustaddress());
                listOfOrders.add(model);
            }
        }
        return listOfOrders;
    }

    // Click listner for details
    @Override
    public void onItemClick_view_details(View view1, int position1) {
        DeliveryCTSModel clickedItem = list.get(position1);

        String orderId = clickedItem.getOrderid();
        String merRef = clickedItem.getMerOrderRef();
        String cashType = clickedItem.getCashType();
        String packagePrice = clickedItem.getPackagePrice();
        String cashAmt = clickedItem.getCashAmt();
        String dateCollection = clickedItem.getCashTime();
        String collectionRemarks = clickedItem.getCashComment();
        String customerName = clickedItem.getCustname();
        String customerAddress = clickedItem.getCustaddress();

        final View mViewDetails = getLayoutInflater().inflate(R.layout.delivery_cash_details, null);
        final TextView merOrderRef = mViewDetails.findViewById(R.id.merorderRef);
        final TextView transactionType = mViewDetails.findViewById(R.id.transactionType);
        final TextView packagePriceTitle = mViewDetails.findViewById(R.id.packagePrice);
        final TextView collectionAmt = mViewDetails.findViewById(R.id.collectionAmt);
        final TextView collectionDate = mViewDetails.findViewById(R.id.collectionDate);
        final TextView  remarks = mViewDetails.findViewById(R.id.remarks);
        final TextView  custName = mViewDetails.findViewById(R.id.custName);
        final TextView  custAddress = mViewDetails.findViewById(R.id.custAddress);

        merOrderRef.setText(merRef);
        transactionType.setText(cashType);
        packagePriceTitle.setText(packagePrice+ " Taka");
        collectionAmt.setText(cashAmt+ " Taka");
        collectionDate.setText(dateCollection);
        remarks.setText(collectionRemarks);
        custName.setText(customerName);
        custAddress.setText(customerAddress);

        //String
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Order Id: "+orderId);
        alertDialogBuilder.setNegativeButton("Close",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(mViewDetails);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // Dispute raise functionality
    @Override
    public void onItemClick_view(View view, int position) {
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
        final Intent intent = new Intent(DeliveryCTS.this, DeliveryCTS.class);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeliveryCTS.this);
        View mView1 = getLayoutInflater().inflate(R.layout.dispute_cash_from_delivery_officer, null);

        DeliveryCTSModel clickedItem = list.get(position);

        final int sql_primary_id = clickedItem.getSql_primary_id();
        String orderId = clickedItem.getOrderid();
        String cashAmt = clickedItem.getCashAmt();

        final TextView tv = mView1.findViewById(R.id.tv);
        final TextView tv1 = mView1.findViewById(R.id.tv1);
        final EditText disputeComment = mView1.findViewById(R.id.dispute_comment_text);
        final TextView disputeError = mView1.findViewById(R.id.dispute_error);

        tv.setText("Dispute Order Id: "+orderId);
        tv1.setText(cashAmt+" Taka");

        alertDialogBuilder.setPositiveButton("Dispute",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(mView1);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String DisputeComments = disputeComment.getText().toString().trim();

                if(DisputeComments.isEmpty()){
                    disputeError.setText("Please write dispute reason!!");
                }
                else {
                    disputeForCash(username, DisputeComments, sql_primary_id);
                    alertDialog.dismiss();
                    //startActivity(intent);
                }
            }
        });
    }

    private void disputeForCash (final String username, final String disputeComment, final int sql_primary_id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELIVERY_CTS_UPDATE_All,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        int i = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("summary");
                            for (i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                String statusCode = o.getString("responseCode");
                                if(statusCode.equals("200")){
                                    loadRecyclerView(username);
                                    Toast.makeText(DeliveryCTS.this, o.getString("success"), Toast.LENGTH_SHORT).show();

                                } else if(statusCode.equals("404")){
                                    Toast.makeText(DeliveryCTS.this, o.getString("unsuccess"), Toast.LENGTH_SHORT).show();
                                }
                            }

                            //String str = String.valueOf(db.getCashCount("cts", "Y"));
                            String str = String.valueOf(i);
                            CashCount_text.setText(str);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // progress.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "Server Error!!" ,Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params1 = new HashMap<String,String>();
                params1.put("username",username);
                params1.put("disputeComment",disputeComment);
                params1.put("sqlPrimaryId", String.valueOf(sql_primary_id));
                params1.put("flagreq","dispute_raised_by_DO");
                return params1;
            }
        };

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        requestQueue.add(stringRequest);
    }
}
