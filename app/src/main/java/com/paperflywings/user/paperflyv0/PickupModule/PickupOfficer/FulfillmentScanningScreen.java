package com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.paperflywings.user.paperflyv0.Config;
import com.paperflywings.user.paperflyv0.Databases.BarcodeDbHelper;
import com.paperflywings.user.paperflyv0.NetworkStateChecker;
import com.paperflywings.user.paperflyv0.PickupModule.PickupList_Model_For_Executive;
import com.paperflywings.user.paperflyv0.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.APIORDERID;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.ASSIGNED_QTY;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.CREATED_AT;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.MERCHANT_ID;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.MERCHANT_NAME;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.SQL_PRIMARY_ID;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.SUB_MERCHANT_NAME;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.PRODUCT_ID;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.PICKED_QTY;
import static com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.MyPickupList_Executive.PRODUCT_NAME;

public class FulfillmentScanningScreen extends AppCompatActivity{
    BarcodeDbHelper db;
    public SwipeRefreshLayout swipeRefreshLayout;
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private Button done;
    private TextView merchant_name_textview;
    private TextView sub_merchant_name_textview;
    private TextView product_name_textview;
//    private TextView scan_count1 ;
    private com.paperflywings.user.paperflyv0.PickupModule.PickupOfficer.pickuplistForExecutiveAdapter pickuplistForExecutiveAdapter;
    RecyclerView recyclerView_pul;
    RecyclerView.LayoutManager layoutManager_pul;
    RecyclerView.Adapter adapter_pul;
    android.widget.RelativeLayout vwParentRow;
    private List<PickupList_Model_For_Executive> list;
    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;
    private NotificationManagerCompat notificationManager;

    //a broadcast to know weather the data is synced or not
    // TODO add sql_primary_id to fulfillment barcode factory
    public static final String BARCODE_INSERT_AND_UPDATE_URL = "http://paperflybd.com/insert_barcode_fulfillment.php";
    public static final String UPDATE_SCAN_AND_PICKED_SHOP= "http://paperflybd.com/updateScanCountFulfillment.php";
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.continuous_scan);

        notificationManager = NotificationManagerCompat.from(this);

        // Pass value through intent
        Intent intent = getIntent();
        String merchant_name = intent.getStringExtra(MERCHANT_NAME);
        String sub_merchant_name = intent.getStringExtra(SUB_MERCHANT_NAME);
        String product_name = intent.getStringExtra(PRODUCT_NAME);
//        scan_count1 = (TextView) findViewById(R.id.scan_count);

        merchant_name_textview = (TextView) findViewById(R.id.merchant_name);
        sub_merchant_name_textview = (TextView) findViewById(R.id.sub_merchant_name);
        product_name_textview = (TextView) findViewById(R.id.product_name);

        if( sub_merchant_name.equals("None") || sub_merchant_name.equals("")) {
            merchant_name_textview.setText("Scan started for: " +merchant_name);
        } else {
            sub_merchant_name_textview.setText("Scan started for: " +sub_merchant_name);
        }

        product_name_textview.setText("Product Name: " +product_name);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.UPC_A);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);

        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }
    private BarcodeCallback callback = new BarcodeCallback()  {

        @Override
        public void barcodeResult(BarcodeResult result)  {
            //Fetching email from shared preferences
            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
            final String user = username.toString();

            // current date and time
            final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            db = new BarcodeDbHelper(FulfillmentScanningScreen.this);
//            if(result.getText() == null || result.getText().equals(lastText)) {
            if(result.getText().equals(lastText) || result.getText().trim().length() != 12) {

                // Set the toast and duration
                int toastDurationInMilliSeconds = 1000;
                final Toast mToastToShow = Toast.makeText(FulfillmentScanningScreen.this,
                        result + " already scanned.", Toast.LENGTH_LONG);
                mToastToShow.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                mToastToShow.show();

                // Set the countdown to display the toast
                CountDownTimer toastCountDown;
                toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
                    public void onTick(long millisUntilFinished) {
                        mToastToShow.show();
                    }
                    public void onFinish() {
                        mToastToShow.cancel();
                    }
                };

                // Show the toast and starts the countdown
                mToastToShow.show();
                toastCountDown.start();
                return;
            }

            lastText = result.getText();

            // get merchant id
            Intent intentID = getIntent();
            final String merchant_id = intentID.getStringExtra(MERCHANT_ID);
            final String sub_merchant_name = intentID.getStringExtra(SUB_MERCHANT_NAME);
            final String merchant_code = intentID.getStringExtra(APIORDERID);
            final String order_id = intentID.getStringExtra(PRODUCT_ID);
            final String picked_qty = intentID.getStringExtra(PICKED_QTY);
            final String match_date = intentID.getStringExtra(CREATED_AT);
            final String sql_primary_id = intentID.getStringExtra(SQL_PRIMARY_ID);

            barcodeView.setStatusText("Barcode"+result.getText());

            // TODO: add added-by, current-date , vaiia says to add flag in this table
            final boolean state = true;
            final String updated_by = user;
//            final String updated_at = currentDateTimeString;

            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            final String updated_at = df.format(c);

            // db.add(merchant_id, lastText, state, updated_by, updated_at);

            if ( lastText.trim().length() != 12 ) {

                Toast.makeText(FulfillmentScanningScreen.this, "garbage", Toast.LENGTH_LONG).show();

            } else {
                // TODO: sql_primary_id add
                barcodesave(merchant_id, sub_merchant_name, lastText, state, updated_by, updated_at, order_id, picked_qty, merchant_code, sql_primary_id);

            }

            db.close();

            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

            done = (Button) findViewById(R.id.done);

            done.setOnClickListener(new View.OnClickListener(){

                @Override
                //On click function
                public void onClick(View view) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    String username = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"Not Available");
                    String user1 = username.toString();

                    // current date and time
                    final String currentDateTimeString1 = DateFormat.getDateTimeInstance().format(new Date());
                    final String updated_by1 = user1;

                    Date c = Calendar.getInstance().getTime();
                    System.out.println("Current time => " + c);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    final String updated_at1 = df.format(c);

//                    final String pick_status = "1";
                    boolean state1 = false;
                    db.update_state(state1, merchant_id, sub_merchant_name, updated_at1,sql_primary_id);
                    // TODO: get the total product quantity


                    try{
                        final String strI = String.valueOf(db.getRowsCountForFulfillment(sql_primary_id,merchant_id, sub_merchant_name, order_id));
                        final String picked_product_qty = String.valueOf(db.getPickedSumByOrderId(sql_primary_id, order_id));
                        // TODO: sql_primary_id add
                        updateScanCount(strI, picked_product_qty, updated_by1, updated_at1, merchant_id, sub_merchant_name, match_date, merchant_code, sql_primary_id);
//                        updateScanCount(strI, picked_product_qty, updated_by1, updated_at1, merchant_id, sub_merchant_name, match_date, pick_status, sql_primary_id);


                    } catch (Exception e) {
                        Toast.makeText(FulfillmentScanningScreen.this, "ScanningScreen" +e, Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent(view.getContext(), MyPickupList_Executive.class);
                    startActivity(intent);

                }
            });
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };
    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    //API HIT
    private void barcodesave(final String merchant_id, final String sub_merchant_name, final String lastText, final Boolean state, final String updated_by, final String updated_at, final String order_id, final String picked_qty, final String merchant_code, final String sql_primary_id) {

        // get created date for match
        Intent intentID = getIntent();
        final String match_date = intentID.getStringExtra(CREATED_AT);
        final String product_name = intentID.getStringExtra(PRODUCT_NAME);
        final String assigned_qty = intentID.getStringExtra(ASSIGNED_QTY);

        StringRequest postRequest = new StringRequest(Request.Method.POST, BARCODE_INSERT_AND_UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
                                db.add_fulfillment(merchant_id, sub_merchant_name, lastText, state, updated_by, updated_at, NAME_SYNCED_WITH_SERVER, order_id, picked_qty, merchant_code,sql_primary_id);
                                final String strI = String.valueOf(db.getRowsCountForFulfillment(sql_primary_id,merchant_id,sub_merchant_name,order_id));
//                                scan_count1.setText("Scan count: " + strI);
//                                Toast.makeText(ScanningScreen.this, "Barcode Number Added" ,  Toast.LENGTH_LONG).show();
                                Toast toast = Toast.makeText(FulfillmentScanningScreen.this,
                                        "Barcode Number Added", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();

                                AlertDialog.Builder builder = new AlertDialog.Builder(FulfillmentScanningScreen.this);
                                View mView = getLayoutInflater().inflate(R.layout.insert_fulfilment_quantity, null);
                                builder.setTitle("Enter product quantity...");

                                final EditText et1 = mView.findViewById(R.id.product_qty);
                                final TextView tv1 = mView.findViewById(R.id.textView3);
                                final TextView Product_Name = mView.findViewById(R.id.product_name_dialog);

                                final TextView Assigned_qty = mView.findViewById(R.id.assigned_picked_qty);
                                et1.setText(assigned_qty);
                                Product_Name.setText("Product Name: " +product_name);
                                Assigned_qty.setText("Assigned Quantity: " +assigned_qty);

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        scannerView.resumeCameraPreview(MainActivity.this);
                                        // update quantity

                                    }
                                });


                                builder.setCancelable(false);
                                builder.setView(mView);

                                final AlertDialog alert1 = builder.create();
                                alert1.show();


                                alert1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (et1.getText().toString().trim().isEmpty()) {
                                            tv1.setText("Field can't be empty");
                                            //  dialog.equals("Order count can't be empty");

                                        } else {

                                            final String product_qty = et1.getText().toString().trim();
                                            ///////ekhane update hobe barcode er picked quantity field
//                                            updatePickedQty(product_qty, lastText);
                                            updatePickedQty(merchant_id, sub_merchant_name, lastText, state, updated_by, updated_at, order_id, product_qty);

//                                            db.updatePickedQty(product_qty, merchant_id, sub_merchant_name, match_date, order_id);
//                                            updateScanCount(strI, product_qty, updated_by, updated_at, merchant_id, sub_merchant_name, match_date);
                                            /*assignexecutive(mAutoComplete.getText().toString(), empcode, et1.getText().toString(), merchant_code, user, currentDateTimeString, m_name, contactNumber, pick_merchant_name, pick_merchant_address);

                                            if (!mAutoComplete.getText().toString().isEmpty() || mAutoComplete.getText().toString().equals(null)) {
                                                Toast.makeText(AssignPickup_Manager.this, mAutoComplete.getText().toString()
                                                                + "(" + et1.getText().toString() + ")",
                                                        Toast.LENGTH_LONG).show();
                                                alert1.dismiss();

                                            }*/

                                            /// update the product quantity

                                            alert1.dismiss();
                                        }
                                        onResume();
                                    }
                                });

                                onPause();


                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                db.add_fulfillment(merchant_id, sub_merchant_name, lastText, state, updated_by, updated_at, NAME_NOT_SYNCED_WITH_SERVER,order_id, picked_qty, merchant_code,sql_primary_id);
                                final String strI = String.valueOf(db.getRowsCountForFulfillment(sql_primary_id,merchant_id,sub_merchant_name,order_id));
//                                scan_count1.setText("Scan count: " + strI);
//                                Toast.makeText(ScanningScreen.this, "barcode save with error" +obj.getBoolean("error"),  Toast.LENGTH_LONG).show();


                                AlertDialog.Builder builder = new AlertDialog.Builder(FulfillmentScanningScreen.this);
                                View mView = getLayoutInflater().inflate(R.layout.insert_fulfilment_quantity, null);
                                builder.setTitle("Enter product quantity...");

                                final EditText et1 = mView.findViewById(R.id.product_qty);
                                final TextView tv1 = mView.findViewById(R.id.textView3);
                                final TextView Product_Name = mView.findViewById(R.id.product_name_dialog);

                                final TextView Assigned_qty = mView.findViewById(R.id.assigned_picked_qty);

                                Product_Name.setText("Product Name: " +product_name);
                                Assigned_qty.setText("Assigned Quantity: " +assigned_qty);
                                et1.setText(assigned_qty);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        scannerView.resumeCameraPreview(MainActivity.this);
                                        // update quantity
//                                        onResume();
                                    }
                                });
                                builder.setCancelable(false);
                                builder.setView(mView);
                                final AlertDialog alert1 = builder.create();
                                alert1.show();

                                alert1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (et1.getText().toString().trim().isEmpty()) {
                                            tv1.setText("Field can't be empty");
                                            //  dialog.equals("Order count can't be empty");

                                        } else {
                                            final String product_qty = et1.getText().toString().trim();
                                            ///////ekhane update hobe barcode er picked quantity field
//                                            updatePickedQty(product_qty, lastText);
                                            updatePickedQty(merchant_id, sub_merchant_name, lastText, state, updated_by, updated_at, order_id, product_qty);
//                                            updateScanCount(strI, product_qty, updated_by, updated_at, merchant_id, sub_merchant_name, match_date);
                                            /*assignexecutive(mAutoComplete.getText().toString(), empcode, et1.getText().toString(), merchant_code, user, currentDateTimeString, m_name, contactNumber, pick_merchant_name, pick_merchant_address);

                                            if (!mAutoComplete.getText().toString().isEmpty() || mAutoComplete.getText().toString().equals(null)) {
                                                Toast.makeText(AssignPickup_Manager.this, mAutoComplete.getText().toString()
                                                                + "(" + et1.getText().toString() + ")",
                                                        Toast.LENGTH_LONG).show();
                                                alert1.dismiss();

                                            }*/

                                            /// update the product quantity

                                            alert1.dismiss();
                                        }
                                        onResume();
                                    }
                                });

                                onPause();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.add_fulfillment(merchant_id,sub_merchant_name, lastText, state, updated_by, updated_at,NAME_NOT_SYNCED_WITH_SERVER, order_id, picked_qty, merchant_code,sql_primary_id);
                        final String strI = String.valueOf(db.getRowsCountForFulfillment(sql_primary_id,merchant_id,sub_merchant_name, order_id));
//                        scan_count1.setText("Scan count: " +strI);

                            AlertDialog.Builder builder = new AlertDialog.Builder(FulfillmentScanningScreen.this);

                            View mView = getLayoutInflater().inflate(R.layout.insert_fulfilment_quantity, null);
                            builder.setTitle("Enter product quantity...");

                            final EditText et1 = mView.findViewById(R.id.product_qty);
                            final TextView tv1 = mView.findViewById(R.id.textView3);
                            final TextView Product_Name = mView.findViewById(R.id.product_name_dialog);
                            final TextView Assigned_qty = mView.findViewById(R.id.assigned_picked_qty);

                            Product_Name.setText("Product Name: " +product_name);
                            Assigned_qty.setText("Assigned Quantity: " +assigned_qty);
                            et1.setText(assigned_qty);
                            
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                        scannerView.resumeCameraPreview(MainActivity.this);
                                    // update quantity
//                                onResume();
                                }
                            });

                            builder.setCancelable(false);
                            builder.setView(mView);
                            final AlertDialog alert1 = builder.create();
                            alert1.show();

                            alert1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (et1.getText().toString().trim().isEmpty()) {
                                        tv1.setText("Field can't be empty");
                                        //  dialog.equals("Order count can't be empty");

                                    } else {
                                        final String product_qty = et1.getText().toString().trim();
                                        ///////ekhane update hobe barcode er picked quantity field
//                                        updatePickedQty(product_qty, lastText);
                                        updatePickedQty(merchant_id, sub_merchant_name, lastText, state, updated_by, updated_at, order_id, product_qty);
//                                        updateScanCount(strI, product_qty, updated_by, updated_at, merchant_id, sub_merchant_name, match_date);
                                            /*assignexecutive(mAutoComplete.getText().toString(), empcode, et1.getText().toString(), merchant_code, user, currentDateTimeString, m_name, contactNumber, pick_merchant_name, pick_merchant_address);

                                            if (!mAutoComplete.getText().toString().isEmpty() || mAutoComplete.getText().toString().equals(null)) {
                                                Toast.makeText(AssignPickup_Manager.this, mAutoComplete.getText().toString()
                                                                + "(" + et1.getText().toString() + ")",
                                                        Toast.LENGTH_LONG).show();
                                                alert1.dismiss();

                                            }*/

                                        /// update the product quantity

                                        alert1.dismiss();
                                    }
                                    onResume();
                                }
                            });

                            onPause();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("merchant_code", merchant_id); // holds the unique product id
                params.put("sub_merchant_name", sub_merchant_name);
                params.put("barcodeNumber", lastText);
                params.put("state", String.valueOf(state));
                params.put("updated_by", updated_by);
                params.put("updated_at", updated_at);
                params.put("order_id", order_id);
                params.put("picked_qty", picked_qty);
                params.put("merchant_id", merchant_code); // Holds the merchant code
                params.put("sql_primary_id", sql_primary_id); // Holds the primary key of insertassign

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);
    }

    // API for updating scan count, picked_product_count, updated by and updated at
    public void updatePickedQty(final String merchant_id, final String sub_merchant_name, final String lastText, final Boolean state, final String updated_by, final String updated_at, final String order_id, final String picked_qty) {
        final BarcodeDbHelper db = new BarcodeDbHelper(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BARCODE_INSERT_AND_UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
//                                db.add(merchant_id, lastText, state, updated_by, updated_at,);
//                                db.update_row_for_fulfillment(strI, picked_product_qty, updated_by, updated_at, merchant_id, sub_merchant_name, match_date, NAME_SYNCED_WITH_SERVER);
                                db.updatePickedQty(picked_qty, lastText,NAME_SYNCED_WITH_SERVER);
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
//                                db.update_row_for_fulfillment(strI, picked_product_qty, updated_by, updated_at, merchant_id,sub_merchant_name, match_date, NAME_NOT_SYNCED_WITH_SERVER);
                                db.updatePickedQty(picked_qty, lastText, NAME_NOT_SYNCED_WITH_SERVER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // db.update_row_for_fulfillment(strI, picked_product_qty, updated_by, updated_at, merchant_id, sub_merchant_name, match_date, NAME_NOT_SYNCED_WITH_SERVER);
                        db.updatePickedQty(picked_qty, lastText, NAME_NOT_SYNCED_WITH_SERVER);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("merchant_code", merchant_id);
                params.put("sub_merchant_name", sub_merchant_name);
                params.put("barcodeNumber", lastText);
                params.put("state", String.valueOf(state));
                params.put("updated_by", updated_by);
                params.put("updated_at", updated_at);
                params.put("order_id", order_id);
                params.put("picked_qty", picked_qty);

                return params;
            }
        };
        try { RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Toast.makeText(FulfillmentScanningScreen.this, "Request Queue" +e, Toast.LENGTH_LONG).show();
        }
    }

//     API for updating scan count, picked_product_count, updated by and updated at
    public void updateScanCount(final String strI,final String picked_product_qty, final String updated_by, final String updated_at, final String merchant_id, final String sub_merchant_name, final String match_date, final String merchant_code, final String sql_primary_id) {
        final BarcodeDbHelper db = new BarcodeDbHelper(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, UPDATE_SCAN_AND_PICKED_SHOP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
//                                db.add(merchant_id, lastText, state, updated_by, updated_at,);
                                db.update_row_for_fulfillment_shop(strI, picked_product_qty, updated_by, updated_at, merchant_id, sub_merchant_name, match_date,sql_primary_id, NAME_SYNCED_WITH_SERVER);
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                db.update_row_for_fulfillment_shop(strI, picked_product_qty, updated_by, updated_at, merchant_id,sub_merchant_name, match_date,sql_primary_id, NAME_NOT_SYNCED_WITH_SERVER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.update_row_for_fulfillment_shop(strI, picked_product_qty, updated_by, updated_at, merchant_id, sub_merchant_name, match_date,sql_primary_id, NAME_NOT_SYNCED_WITH_SERVER);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("merchant_code", merchant_id); // holds the product id which is unique
                params.put("p_m_name", sub_merchant_name);
                params.put("created_at", match_date);
                params.put("scan_count", strI);
                params.put("picked_qty", picked_product_qty);
                params.put("api_order_id", merchant_code); // holds the merchant code
//                params.put("pick_from_merchant_status", pick_status);
                params.put("updated_by", updated_by);
                params.put("updated_at", updated_at);
                params.put("sql_primary_id", sql_primary_id); // holds the proiamry key of insertassign table

                return params;
            }
        };
        try { RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Toast.makeText(FulfillmentScanningScreen.this, "Request Queue" +e, Toast.LENGTH_LONG).show();
        }

    }
    // API for updating scan count, picked_product_count, updated by and updated at and comments, pause ,delete
    /*public void updateScanCount(final String strI, final String picked_qty, final String updated_by, final String updated_at, final String merchant_id, final String sub_merchant_name, final String match_date, final String pick_status, final String merchant_code, final String sql_primary_id) {
        final BarcodeDbHelper db = new BarcodeDbHelper(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, UPDATE_SCAN_AND_PICKED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
//                                db.add(merchant_id, lastText, state, updated_by, updated_at,);
                                db.update_row(strI, picked_qty, updated_by, updated_at, merchant_id, sub_merchant_name, match_date,pick_status,sql_primary_id, NAME_SYNCED_WITH_SERVER);
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                db.update_row(strI, picked_qty,updated_by, updated_at, merchant_id,sub_merchant_name, match_date,pick_status,sql_primary_id, NAME_NOT_SYNCED_WITH_SERVER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        db.update_row(strI, picked_qty ,updated_by, updated_at, merchant_id, sub_merchant_name, match_date, pick_status,sql_primary_id, NAME_NOT_SYNCED_WITH_SERVER);

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("merchant_code", merchant_id);
                params.put("p_m_name", sub_merchant_name);
                params.put("scan_count", strI);
                params.put("picked_qty", picked_qty);
                // params.put("api_order_id", "0");
                params.put("pick_from_merchant_status", pick_status);
                params.put("updated_by", updated_by);
                params.put("updated_at", updated_at);
                params.put("sql_primary_id", sql_primary_id);
                params.put("created_at", match_date);
                return params;
            }
        };
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Toast.makeText(FulfillmentScanningScreen.this, "Request Queue" +e, Toast.LENGTH_SHORT).show();
        }
    }*/
}
