package com.paperflywings.user.paperflyv0;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BarcodeDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "WingsDB";
    private static final String TABLE_NAME = "Barcode";
    private static final String TABLE_NAME_1 = "My_pickups";
    private static final String TABLE_NAME_2 = "Barcode_Fulfillment";
    // private static final String TABLE_NAME_3 = "My_pickups_auto";
    private static final String TABLE_NAME_4 = "Insert_Pickup_Action";
    private static final String TABLE_NAME_6 = "pickups_today_executive";
    private static final String TABLE_NAME_7 = "Insert_Delivery_Summary";
    private static final String TABLE_NAME_8 = "Insert_Delivery_Unpicked";
    private static final String KEY_ID = "id";
    private static final String SQL_PRIMARY_ID = "sql_primary_id";
    private static final String MERCHANT_ID = "merchantId";
    private static final String KEY_NAME = "barcodeNumber";
    private static final String MERCHANT_NAME = "merchant_name";
    private static final String EXECUTIVE_NAME = "executive_name";
    private static final String ASSIGNED_QTY = "assined_qty";
    private static final String PICKED_QTY = "picked_qty";
    private static final String SCAN_COUNT = "scan_count";
    private static final String PHONE_NO = "phone_no";
    private static final String ASSIGNED_BY = "assigned_by";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_BY = "updated_by";
    private static final String UPDATED_AT = "updated_at";
    private static final String RECEIVED_QTY = "received_qty";
    private static final String STATE = "state";
    private static final String STATUS = "status";
    private static final String COMPLETE_STATUS = "complete_status";
    private static final String PICK_M_NAME = "p_m_name";
    private static final String PICK_M_ADD = "p_m_add";
    private static final String SUB_MERCHANT_NAME = "sub_merchant_name";
    private static final String ORDER_ID = "order_id";
    private static final String PRODUCT_NAME = "product_name";
    private static final String APIORDERID = "apiOrderID";
    private static final String DEMO = "demo";
    private static final String MERCHANT_CODE = "merchant_code";
    private static final String PICKED_STATUS = "pick_from_merchant_status";
    private static final String RECEIVED_STATUS = "received_from_HQ_status";

    private static final String STATUS_ID = "status_id";
    private static final String STATUS_NAME = "status_name";
    private static final String USERNAME = "username";
    private static final String COMMENT = "comment";


    //delivery landing
    private static final String UNPICKED = "unpicked";
    private static final String WITHOUTSTATUS = "without_status";
    private static final String ONHOLD = "onhold";
    private static final String CASH = "cash";
    private static final String RETURNREQUEST = "return_request";
    private static final String RETURNLIST = "return_list";


    //delivery unpicked

    public static final String BARCODE_NO= "barcode";
    public static final String ORDERID = "orderid";
    public static final String MERCHANT_ORDER_REF = "merOrderRef";
    public static final String MERCHANTS_NAME = "merchantName";
    public static final String PICK_MERCHANTS_NAME = "pickMerchantName";
    public static final String CUSTOMER_NAME = "custname";
    public static final String Phone = "custphone";
    public static final String CUSTOMER_ADDRESS = "custaddress";
    public static final String PACKAGE_PRICE = "packagePrice";
    public static final String PRODUCT_BRIEF= "productBrief";
    public static final String DELIVERY_TIME= "deliveryTime";





    private static final String[] COLUMNS = { KEY_ID, MERCHANT_ID, KEY_NAME };
    private SQLiteDatabase db;

    public BarcodeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Barcode ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "sql_primary_id TEXT, "
                + "merchantId TEXT, "
                + "sub_merchant_name TEXT, "
                + "barcodeNumber TEXT, "
                + "state BOOLEAN, "
                + "status INT, "
                + "updated_by TEXT, "
                + "updated_at TEXT, "
                + " unique(barcodeNumber))" ;

        String CREATION_TABLE1 = "CREATE TABLE My_pickups ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "sql_primary_id TEXT, "
                + "merchantId TEXT, "
                + "merchant_name TEXT, "
                + "executive_name TEXT, "
                + "assined_qty TEXT, "
                + "picked_qty TEXT, "
                + "scan_count TEXT, "
                + "phone_no TEXT, "
                + "assigned_by TEXT, "
                + "created_at TEXT , "
                + "updated_by TEXT, "
                + "updated_at TEXT , "
                + "status INT, "
                + "complete_status TEXT, "
                + "p_m_name TEXT , "
                + "p_m_add TEXT, "
                + "product_name TEXT, "
                + "apiOrderID TEXT, "
                + "demo TEXT, "
                + "pick_from_merchant_status TEXT, "
                + "received_from_HQ_status TEXT, "
                + "unique(sql_primary_id,merchantId, p_m_name, product_name, apiOrderID, created_at))";

        /*String CREATION_TABLE3 = "CREATE TABLE My_pickups_auto ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "merchantName TEXT, "
                + "merchantCode TEXT, "
                + "pickMerName TEXT, "
                + "pickMerAddress TEXT, "
                + "pickPhoneNo TEXT, "
                + "executiveName TEXT, "
                + "executiveCode TEXT, "
                + "manualCountManager TEXT, "
                + "autoCountMerchant TEXT, "
                + "scanCount TEXT , "
                + "pickedQty TEXT, "
                + "productName TEXT , "
                + "productId TEXT, "
                + "productQty TEXT, "
                + "merOrderRef TEXT , "
                + "assignedBy TEXT, "
                + "assignedAt TEXT, "
                + "updatedBy TEXT, "
                + "updatedAt TEXT, "
                + "pickTypeStatus TEXT, "
                + "pickedStatus TEXT, "
                + "receivedStatus TEXT, "
                + "updateSatus TEXT,"
                + "deleteStatus TEXT,"
                + "demo1 TEXT,"
                + "demo2 TEXT,"
                + "status INT,"
                + "unique(merchantCode,pickMerName,executiveName))";*/


        String CREATION_TABLE2 = "CREATE TABLE Barcode_Fulfillment ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "merchantId TEXT, "
                + "sub_merchant_name TEXT, "
                + "barcodeNumber TEXT, "
                + "state BOOLEAN, "
                + "status INT, "
                + "updated_by TEXT, "
                + "updated_at TEXT, "
                + "order_id TEXT, "
                + "picked_qty TEXT, "
                + "merchant_code TEXT, "
                + "sql_primary_id TEXT, "
                + "unique(merchantId,barcodeNumber,order_id,sql_primary_id))" ;

        String CREATION_TABLE5 = "CREATE TABLE Insert_Pickup_Action ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "sql_primary_id TEXT, "
                + "comment TEXT, "
                + "status_id TEXT, "
                + "status_name TEXT, "
                + "username TEXT, "
                + "status INT, "
                + " unique(id))" ;

        String CREATION_TABLE6 = "create table pickups_today_executive(id integer primary key AUTOINCREMENT,sql_primary_id,merchantName text,totalcount int,scan_count integer,created_at text,executive_name text, complete_status text,picked_qty integer, p_m_name text,product_name text,demo text, unique(sql_primary_id, merchantName, p_m_name, product_name))";

        String CREATION_TABLE7 = "CREATE TABLE Insert_Delivery_Summary( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT, "
                + "unpicked TEXT, "
                + "without_status TEXT, "
                + "onhold TEXT, "
                + "cash TEXT, "
                + "return_request TEXT, "
                + "return_list TEXT, "
                + "status INT, "
                + "unique(id))" ;


        String CREATION_TABLE8 = "CREATE TABLE Insert_Delivery_Unpicked( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "barcode TEXT, "
                + "orderid TEXT, "
                + "merOrderRef TEXT, "
                + "merchantName TEXT, "
                + "pickMerchantName TEXT, "
                + "custname TEXT, "
                + "custaddress TEXT, "
                + "custphone TEXT, "
                + "packagePrice TEXT, "
                + "productBrief TEXT, "
                + "deliveryTime TEXT, "
                + "status INT, "
                + "unique(id))" ;


        db.execSQL(CREATION_TABLE);
        db.execSQL(CREATION_TABLE1);
        db.execSQL(CREATION_TABLE2);
        // db.execSQL(CREATION_TABLE3);
        db.execSQL(CREATION_TABLE5);
        db.execSQL(CREATION_TABLE6);
        db.execSQL(CREATION_TABLE7);
        db.execSQL(CREATION_TABLE8);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
//         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_4);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_6);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_7);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_8);
        this.onCreate(db);
    }

    // Insert values on My_pickups
    public void insert_my_assigned_pickups(String sql_primary_id, String executive_name, String assined_qty, String merchant_id, String assigned_by, String created_at, String updated_by, String updated_at, String scan_count, String phone_no, String picked_qty, String merchant_name, String complete_status, String p_m_name, String p_m_add, String product_name,String apiOrderID, String demo,String pick_from_merchant_status, String received_from_HQ_status, int status)
    {
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQL_PRIMARY_ID, sql_primary_id);
        values.put(MERCHANT_ID, merchant_id);
        values.put(MERCHANT_NAME, merchant_name);
        values.put(EXECUTIVE_NAME, executive_name);
        values.put(ASSIGNED_QTY, assined_qty);
        values.put(PICKED_QTY, picked_qty);
        values.put(SCAN_COUNT, scan_count);
        values.put(PHONE_NO, phone_no);
        values.put(ASSIGNED_BY, assigned_by);
        values.put(CREATED_AT, created_at);
        values.put(UPDATED_BY, updated_by);
        values.put(UPDATED_AT, updated_at);
        values.put(COMPLETE_STATUS, complete_status);
        values.put(PICK_M_NAME, p_m_name);
        values.put(PICK_M_ADD, p_m_add);
        values.put(PRODUCT_NAME, product_name);
        values.put(APIORDERID, apiOrderID);
        values.put(DEMO, demo);
        values.put(PICKED_STATUS, pick_from_merchant_status);
        values.put(RECEIVED_STATUS, received_from_HQ_status);
        values.put(STATUS, status);

        db.insert(TABLE_NAME_1,null, values);

        db.close();
    }

    // get the pickup list from My_pickups table
    public Cursor get_mypickups_today(SQLiteDatabase db, String user)
    {
        String[] columns = {KEY_ID,MERCHANT_ID, MERCHANT_NAME, EXECUTIVE_NAME, ASSIGNED_QTY, PICKED_QTY, SCAN_COUNT, PHONE_NO, ASSIGNED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT, COMPLETE_STATUS, PICK_M_NAME, PICK_M_ADD, PRODUCT_NAME, APIORDERID, DEMO, PICKED_STATUS, RECEIVED_STATUS, SQL_PRIMARY_ID};
        String sortOrder = CREATED_AT + " ASC";
        String whereClause = EXECUTIVE_NAME + "=?";
        String[] whereArgs = new String[] {
                user
        };
        return (db.query(TABLE_NAME_1,columns,whereClause,whereArgs,null,null,sortOrder));
    }

    /*public Cursor get_mypickups_today(SQLiteDatabase sqLiteDatabase) {
        String[] columns = {KEY_ID,MERCHANT_ID, MERCHANT_NAME, EXECUTIVE_NAME, ASSIGNED_QTY, PICKED_QTY, SCAN_COUNT, PHONE_NO, ASSIGNED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT};
        return (db.query(TABLE_NAME_1,columns,"executive_name='" + "'",null,null,null,null));
    }*/

    // clear data from My_pickups table
    public void deleteAssignedList(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("delete from "+ TABLE_NAME_1);
    }

    // insert logistic barcode on scan
    public void add(String sql_primary_id, String merchantId, String sub_merchant_name, String barcodeNumber, boolean state, String updated_by, String updated_at,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQL_PRIMARY_ID, sql_primary_id);
        values.put(SUB_MERCHANT_NAME, sub_merchant_name);
        values.put(MERCHANT_ID, merchantId);
        values.put(KEY_NAME, barcodeNumber);
        values.put(String.valueOf(STATE), state);
        values.put(UPDATED_BY, updated_by);
        values.put(UPDATED_AT, updated_at);
        values.put(STATUS,status);

        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    // get scan count
    public int getRowsCount(String sql_primary_id, String merchantId, String sub_merchant_name) {
        String countQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + MERCHANT_ID + "='"+ merchantId +"' AND " + SUB_MERCHANT_NAME + " = '"+ sub_merchant_name +"' AND " + SQL_PRIMARY_ID + " = '"+ sql_primary_id +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // get scan count in auto pickup list
    public int getRowsCountAuto(String merchantId, String sub_merchant_name) {
        String countQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + MERCHANT_ID + "='"+ merchantId +"' AND " + SUB_MERCHANT_NAME + " = '"+ sub_merchant_name +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // insert barcode scan for fulfillment
    public void add_fulfillment(String merchantId, String sub_merchant_name, String barcodeNumber, boolean state, String updated_by, String updated_at,int status, String order_id, String picked_qty, String merchant_code, String sql_primary_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SUB_MERCHANT_NAME, sub_merchant_name);
        values.put(MERCHANT_ID, merchantId);
        values.put(KEY_NAME, barcodeNumber);
        values.put(String.valueOf(STATE), state);
        values.put(UPDATED_BY, updated_by);
        values.put(UPDATED_AT, updated_at);
        values.put(STATUS,status);
        values.put(ORDER_ID,order_id);
        values.put(PICKED_QTY,picked_qty);
        values.put(MERCHANT_CODE,merchant_code);
        values.put(SQL_PRIMARY_ID,sql_primary_id);
        // insert
        db.insert(TABLE_NAME_2,null, values);
        db.close();
    }

    // insert action log in Insert_Pickup_Action table
    public void insert_action_log(String sql_primary_id, String comments, String status_id, String status_name, String username, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQL_PRIMARY_ID, sql_primary_id);
        values.put(COMMENT, comments);
        values.put(STATUS_ID, status_id);
        values.put(STATUS_NAME, status_name);
        values.put(USERNAME, username);
        values.put(STATUS,status);
        // insert
        db.insert(TABLE_NAME_4,null, values);
        db.close();
    }

    public void updatePickedQty(String product_qty,String barcodeNumber, int status ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(String.valueOf(PICKED_QTY), product_qty);
        values.put(STATUS,status);
        String whereClause = KEY_NAME + " = ?";
        String[] whereArgs = new String[] {
                barcodeNumber
        };
        // insert
        // db.update(TABLE_NAME,values,"merchantId='" + merchantId + "'" + "&&" + "sub_merchant_name='" + sub_merchant_name + "'",null);
        db.update(TABLE_NAME_2,values,whereClause, whereArgs );
        db.close();
    }

    public int getRowsCountForFulfillment(String sql_primary_id, String merchantId, String sub_merchant_name,String order_id) {
        String countQuery = "SELECT  * FROM " + TABLE_NAME_2 + " WHERE " + MERCHANT_ID + "='"+ merchantId +"' AND " + SUB_MERCHANT_NAME + " = '"+ sub_merchant_name +"'AND " + SQL_PRIMARY_ID + " = '"+ sql_primary_id +"'AND " + ORDER_ID + " = '"+ order_id +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public int getPickedSumByOrderId(String sql_primary_id,String order_id) {

        int sum=0;
        db = this.getReadableDatabase();
        String sumQuery=String.format("SELECT SUM(" + PICKED_QTY + ") as Total FROM " + TABLE_NAME_2 + " WHERE " + SQL_PRIMARY_ID + " = '"+ sql_primary_id +"'AND " + ORDER_ID + " = '"+ order_id +"'");
        Cursor cursor= db.rawQuery(sumQuery,null);
        if(cursor.moveToFirst())
            sum= cursor.getInt(cursor.getColumnIndex("Total"));
        return sum;
    }

    //For fulfillment end
    public Cursor getUnsyncedBarcode() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedFulfillmentBarcodeData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME_2 + " WHERE " + STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    public Cursor getUnsyncedActionLog() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME_4 + " WHERE " + STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedPickedQtyData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME_1 + " WHERE " + STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    public boolean updateBarcodeStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        db.update(TABLE_NAME, contentValues, KEY_ID + "=" + id, null);
        db.close();
        return true;
    }


    public boolean updateFulfillmentBarcodeStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        db.update(TABLE_NAME_2, contentValues, KEY_ID + "=" + id, null);
        db.close();
        return true;
    }

    public Cursor getUnsyncedData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME_1 + " WHERE " + STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public boolean updateDataStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        db.update(TABLE_NAME_1, contentValues, KEY_ID + "=" + id, null);
        db.close();
        return true;
    }


    public boolean updatePickActionLog(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        db.update(TABLE_NAME_4, contentValues, KEY_ID + "=" + id, null);
        db.close();
        return true;
    }

    // update state on Barcode table when the scanning is done
    public void update_state(boolean state, String merchantId, String sub_merchant_name, String date, String sql_primary_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(String.valueOf(STATE), state);
        String whereClause = MERCHANT_ID + " = ? AND " + SUB_MERCHANT_NAME  + " = ?  AND " + SQL_PRIMARY_ID  + " = ?  AND " + UPDATED_AT  + " = ?";
        String[] whereArgs = new String[] {
                merchantId,
                sub_merchant_name,
                String.valueOf(sql_primary_id),
                date
        };
        // insert
        db.update(TABLE_NAME,values,whereClause, whereArgs );
        db.close();
    }

    // Update Scan count and picked quantity on My_pickups for fulfillment
    public void update_row_for_fulfillment_shop(String scan_count, String picked_qty, String updated_by, String updated_at, String merchantId, String sub_merchant_name, String match_date,String sql_primary_id, int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCAN_COUNT, scan_count);
        values.put(PICKED_QTY, picked_qty);
        values.put(UPDATED_BY, updated_by);
        values.put(UPDATED_AT, updated_at);
        values.put(STATUS, status);

        String whereClause = MERCHANT_ID + " = ? AND " + PICK_M_NAME  + " = ?  AND " + SQL_PRIMARY_ID  + " = ?  AND " + CREATED_AT  + " = ?";
        String[] whereArgs = new String[] {
                merchantId,
                sub_merchant_name,
                sql_primary_id,
                match_date
        };
        // insert
        db.update(TABLE_NAME_1,values,whereClause,whereArgs );
        db.close();
    }

    // Update Scan count on My_pickups for logistic
    public void update_row(String scan_count, String picked_qty, String updated_by, String updated_at, String merchantId, String sub_merchant_name, String match_date, String sql_primary_id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCAN_COUNT, scan_count);
        values.put(PICKED_QTY, picked_qty);
        values.put(UPDATED_BY, updated_by);
        values.put(UPDATED_AT, updated_at);
        // values.put(PICKED_STATUS, pick_status);
        values.put(STATUS, status);

        String whereClause =  MERCHANT_ID + " = ? AND " + PICK_M_NAME  + " = ?  AND " + SQL_PRIMARY_ID  + " = ?  AND "+ CREATED_AT  + " = ?";
        String[] whereArgs = new String[] {
                merchantId,
                sub_merchant_name,
                sql_primary_id,
                match_date
        };

        db.update(TABLE_NAME_1,values,whereClause,whereArgs);
        db.close();
    }

    // Update Scan count and picked quantity on My_pickups for fulfillment
    public void update_row_for_fulfillment(String scan_count, String picked_qty, String updated_by, String updated_at, String merchantId, String sub_merchant_name, String apiorderid, String comments , String match_date, String pick_status, String pause_or_delete,String sql_primary_id,int status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCAN_COUNT, scan_count);
        values.put(PICKED_QTY, picked_qty);
        values.put(UPDATED_BY, updated_by);
        values.put(UPDATED_AT, updated_at);
        values.put(DEMO, comments);
        values.put(PICKED_STATUS, pick_status);
        values.put(RECEIVED_STATUS, pause_or_delete);
        values.put(STATUS, status);

        String whereClause = MERCHANT_ID + " = ? AND " + PICK_M_NAME  + " = ?  AND " + APIORDERID  + " = ?  AND " + SQL_PRIMARY_ID  + " = ?  AND "+ CREATED_AT  + " = ?";
        String[] whereArgs = new String[] {
                merchantId,
                sub_merchant_name,
                apiorderid,
                sql_primary_id,
                match_date
        };
        // insert
        db.update(TABLE_NAME_1,values,whereClause,whereArgs );
        db.close();
    }


/*    public List<PickupList_Model_For_Executive> getAllData(String user) {
        // array of columns to fetch
        String[] columns = {KEY_ID,
                            MERCHANT_ID,
                            MERCHANT_NAME,
                            EXECUTIVE_NAME,
                            ASSIGNED_QTY,
                            PICKED_QTY,
                            SCAN_COUNT,
                            PHONE_NO,
                            ASSIGNED_BY,
                            CREATED_AT,
                            UPDATED_BY,
                            UPDATED_AT,
                            COMPLETE_STATUS };

        // sorting orders
        String sortOrder = CREATED_AT + " DESC";
        List<PickupList_Model_For_Executive> list = new ArrayList<PickupList_Model_For_Executive>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_1, //Table to query
                columns,    //columns to return
                "executive_name='" + user + "'",        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PickupList_Model_For_Executive beneficiary = new PickupList_Model_For_Executive();

//                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setKey_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                beneficiary.setMerchant_id(cursor.getString(cursor.getColumnIndex(MERCHANT_ID)));
                beneficiary.setMerchant_name(cursor.getString(cursor.getColumnIndex(MERCHANT_NAME)));
                beneficiary.setExecutive_name(cursor.getString(cursor.getColumnIndex(EXECUTIVE_NAME)));
                beneficiary.setAssined_qty(cursor.getString(cursor.getColumnIndex(ASSIGNED_QTY)));
                beneficiary.setPicked_qty(cursor.getString(cursor.getColumnIndex(PICKED_QTY)));
                beneficiary.setScan_count(cursor.getString(cursor.getColumnIndex(SCAN_COUNT)));
                beneficiary.setPhone_no(cursor.getString(cursor.getColumnIndex(PHONE_NO)));
                beneficiary.setAssigned_by(cursor.getString(cursor.getColumnIndex(ASSIGNED_BY)));
                beneficiary.setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)));
                beneficiary.setUpdated_by(cursor.getString(cursor.getColumnIndex(UPDATED_BY)));
                beneficiary.setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)));
                beneficiary.setComplete_status(cursor.getString(cursor.getColumnIndex(COMPLETE_STATUS)));
                // Adding user record to list
                list.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return list;
    }

    // Get all executives
    public List<PickupList_Model_For_Executive> getPending(String user) {
        // array of columns to fetch
//        String[] columns = {"id", "empName", "empCode"};
        String[] columns = {KEY_ID,
                MERCHANT_NAME,
                EXECUTIVE_NAME,
                ASSIGNED_QTY,
                PICKED_QTY,
                SCAN_COUNT };
        // sorting orders
        String sortOrder = KEY_ID + " ASC";
        List<PickupList_Model_For_Executive> list = new ArrayList<PickupList_Model_For_Executive>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_1, columns,  "executive_name='" + user + "'", null, null, null,sortOrder);
        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PickupList_Model_For_Executive beneficiary = new PickupList_Model_For_Executive();

//                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setKey_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                beneficiary.setMerchant_name(cursor.getString(cursor.getColumnIndex(MERCHANT_NAME)));
                beneficiary.setExecutive_name(cursor.getString(cursor.getColumnIndex(EXECUTIVE_NAME)));
                beneficiary.setAssined_qty(cursor.getString(cursor.getColumnIndex(ASSIGNED_QTY)));
                beneficiary.setPicked_qty(cursor.getString(cursor.getColumnIndex(PICKED_QTY)));
                beneficiary.setScan_count(cursor.getString(cursor.getColumnIndex(SCAN_COUNT)));

                // Adding user record to list
                list.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user list
        return list;
    }*/

    // Delete data from Sqlite Barcode table upon login
    public void barcode_factory(SQLiteDatabase sqLiteDatabase, String match_date)
    {
         sqLiteDatabase.delete("Barcode", "updated_at<>?", new String[]{match_date});
    }

    // Delete data from Sqlite Barcode_Fulfillment table upon login
    public void barcode_factory_fulfillment(SQLiteDatabase sqLiteDatabase, String match_date)
    {
         sqLiteDatabase.delete("Barcode_Fulfillment", "updated_at<>?", new String[]{match_date});
    }

    // Insert data in pickups_today_executive table
    public void add_pickups_today_executive(String sql_primary_id,String merchantName,int cnt,int scan_count,String created_at,String executive_name, String complete_status, int picked_qty,String p_m_name, String product_name, String demo) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sql_primary_id", sql_primary_id);
        values.put("merchantName", merchantName);
        values.put("totalcount",cnt);
        values.put("scan_count",scan_count);
        values.put("created_at",created_at);
        values.put("executive_name",executive_name);
        values.put("complete_status",complete_status);
        values.put("picked_qty",picked_qty);
        values.put("p_m_name",p_m_name);
        values.put("product_name",product_name);
        values.put("demo",demo);

        sqLiteDatabase.insert(TABLE_NAME_6, null, values);
        sqLiteDatabase.close();
    }

    // get the data to show in recycler view from local storage on pickup today page on pickup executive side, pickups_today_executive table
    public Cursor getdata_pickups_today_executive(SQLiteDatabase db, String user) {
        String[] columns = {"sql_primary_id","merchantName", "totalcount","scan_count","executive_name","created_at","complete_status","picked_qty", "product_name", "p_m_name", "product_name", "demo"};
        return db.query(TABLE_NAME_6, columns, "executive_name='" + user + "'", null, null, null,"scan_count"+" ASC");
    }

    // Delete data from pickups_today_executive table
    public void clearPTMListExec(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("delete from "+ TABLE_NAME_6);
    }

    // Returns the number of total pickup count
    public int totalassigned_order_for_ex(String user, String currentDateTimeString){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor sumQuery = db.rawQuery("SELECT * FROM " + TABLE_NAME_6 + " WHERE " + "executive_name" + " = '" + user + "' AND " + "created_at" + " = '" + currentDateTimeString + "'", null);
        int count = sumQuery.getCount();
        sumQuery.close();
        return count;
    }

    // Returns the number of complete pickup count
    public int complete_order_for_ex(String user, String currentDateTimeString){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor sumQuery = db.rawQuery("SELECT * FROM " + TABLE_NAME_6+ " WHERE " + "executive_name" + " = '" + user + "' AND  " + "picked_qty" + ">=" + "totalcount" + " AND " + "created_at"+ " = '" + currentDateTimeString + "'", null);
        int count = sumQuery.getCount();
        sumQuery.close();
        return count;
    }

    // Returns the number of pending pickup count
    public int pending_order_for_ex(String user, String currentDateTimeString){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor sumQuery = db.rawQuery("SELECT merchantName FROM " + TABLE_NAME_6 + " WHERE " + "executive_name" + " = '" + user + "' AND  " + "picked_qty" + "<" + "totalcount" + " AND " + "created_at"+ " = '" + currentDateTimeString + "'" , null);
        int count = sumQuery.getCount( );
        sumQuery.close();
        return count;
    }

    //////// Delivery Officer Database functionalities ///////

    // insert counts in delivery summary
    public void insert_delivery_summary_count(String username, String unpicked, String without_status, String onhold, String cash, String return_request,String return_list, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USERNAME, username);
        values.put(UNPICKED, unpicked);
        values.put(WITHOUTSTATUS, without_status);
        values.put(ONHOLD, onhold);
        values.put(CASH, cash);
        values.put(RETURNREQUEST, return_request);
        values.put(RETURNLIST, return_list);
        values.put(STATUS,status);

        db.insert(TABLE_NAME_7,null, values);
        db.close();
    }

    // get the delivery summary
    public Cursor get_delivery_summary(SQLiteDatabase db, String user)
    {
        String[] columns = {USERNAME, UNPICKED, WITHOUTSTATUS, ONHOLD, CASH, RETURNREQUEST, RETURNLIST};

        String whereClause = USERNAME + "=?";
        String[] whereArgs = new String[] {
                user
        };
        return (db.query(TABLE_NAME_7,columns,whereClause,whereArgs,null,null,null));
    }


    // insert counts in delivery unpicked
    public void insert_delivery_unpicked_count(String barcode,String orderid, String merOrderRef,String merchantName,String pickMerchantName, String custname, String custaddress,String custphone, String packagePrice, String productBrief, String deliveryTime, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BARCODE_NO, barcode);
        values.put(ORDERID, orderid);
        values.put(MERCHANT_ORDER_REF, merOrderRef);
        values.put(MERCHANTS_NAME, merchantName);
        values.put(PICK_MERCHANTS_NAME, pickMerchantName);
        values.put(CUSTOMER_NAME, custname);
        values.put(CUSTOMER_ADDRESS, custaddress);
        values.put(Phone, custphone);
        values.put(PACKAGE_PRICE, packagePrice);
        values.put(PRODUCT_BRIEF, productBrief);
        values.put(DELIVERY_TIME, deliveryTime);
        values.put(STATUS,status);

        db.insert(TABLE_NAME_8,null, values);
        db.close();
    }

    // get the delivery unpicked
    public Cursor get_delivery_unpicked(SQLiteDatabase db, String user)
    {
        String[] columns = { BARCODE_NO,ORDERID, MERCHANT_ORDER_REF, MERCHANTS_NAME,PICK_MERCHANTS_NAME,CUSTOMER_NAME, CUSTOMER_ADDRESS, Phone,PACKAGE_PRICE,PRODUCT_BRIEF,DELIVERY_TIME };

        String whereClause = USERNAME + "=?";
        String[] whereArgs = new String[] {
                user
        };
        return (db.query(TABLE_NAME_8,columns,whereClause,whereArgs,null,null,null));
    }

}

