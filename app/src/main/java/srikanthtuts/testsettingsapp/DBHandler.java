package srikanthtuts.testsettingsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {

    private static DBHandler dbHandlerInstance;

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "pmdb";

    // Customers table name
    private static final String CUSTOMER_TABLE_NAME = "tblcustomers";
    private static final String COL_CC_ID = "id";
    private static final String COL_CC_USERID = "userid";
    private static final String COL_CC_PASSWORD = "password";
    private static final String COL_CC_CXNAME = "cxname";
    private static final String COL_CC_PHONE = "phone";
    private static final String COL_CC_ADDRESS = "address";
    private static final String COL_CC_USERTYPE = "usertype";
    private static final String COL_CC_LASTLOGIN = "lastlogin";


    // Customers Orders table name
    private static final String CUSTOMER_ORDERS_TABLE_NAME = "tblcustomersorders";
    private static final String COL_CO_ID = "id";
    private static final String COL_CO_ORDERID = "orderid";
    private static final String COL_CO_ORDERDATE = "orderdate";
    private static final String COL_CO_STORETYPE = "storetype";
    private static final String COL_CO_STOREEMAILID = "storeemailid";
    private static final String COL_CO_USERID = "userid";
    private static final String COL_CO_PRODUCTSKU = "productsku";
    private static final String COL_CO_PRODUCTNAME = "productname";
    private static final String COL_CO_PRODUCTDESC = "productdesc";
    private static final String COL_CO_PRODUCTIMAGE = "productimage";
    private static final String COL_CO_OLDPRICE = "oldprice";
    private static final String COL_CO_NEWPRICE = "newprice";
    private static final String COL_CO_TAX = "tax";
    private static final String COL_CO_PRICEMATCH = "pricematch";
    private static final String COL_CO_PMDATE = "pmdate";


    String strQuery = "";


    public static DBHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (dbHandlerInstance == null) {
            dbHandlerInstance = new DBHandler(context.getApplicationContext());
        }
        return dbHandlerInstance;
    }

    private DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + CUSTOMER_TABLE_NAME
                + "(" + COL_CC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CC_USERID + " TEXT," + COL_CC_PASSWORD + " TEXT,"
                + COL_CC_CXNAME + " TEXT," + COL_CC_PHONE + " TEXT,"
                + COL_CC_ADDRESS + " TEXT," + COL_CC_USERTYPE + " TEXT," + COL_CC_LASTLOGIN + " TEXT" + ")";

        String CREATE_CUSTOMER_ORDERS_TABLE = "CREATE TABLE " + CUSTOMER_ORDERS_TABLE_NAME
                + "(" + COL_CO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CO_ORDERID + " TEXT," + COL_CO_ORDERDATE + " TEXT,"
                + COL_CO_STORETYPE + " TEXT," + COL_CO_STOREEMAILID + " TEXT,"
                + COL_CO_USERID + " TEXT," + COL_CO_PRODUCTSKU + " TEXT," + COL_CO_PRODUCTNAME + " TEXT,"
                + COL_CO_PRODUCTDESC + " TEXT," + COL_CO_PRODUCTIMAGE + " TEXT," + COL_CO_OLDPRICE + " TEXT,"
                + COL_CO_NEWPRICE + " TEXT," + COL_CO_TAX + " TEXT" + COL_CO_PRICEMATCH + " TEXT," + COL_CO_PMDATE + " TEXT" + ")";

        db.execSQL(CREATE_CUSTOMER_TABLE);
        db.execSQL(CREATE_CUSTOMER_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOMER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOMER_ORDERS_TABLE_NAME);
        onCreate(db);
    }

    public void addCustomer(String cxUserID, String cxPassword, String cxName, String cxPhone,
                            String cxAddress, String cxUserType) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_CC_USERID, cxUserID);
            values.put(COL_CC_PASSWORD, cxPassword);
            values.put(COL_CC_CXNAME, cxName);
            values.put(COL_CC_PHONE, cxPhone);
            values.put(COL_CC_ADDRESS, cxAddress);
            values.put(COL_CC_USERTYPE, cxUserType);
            db.insert(CUSTOMER_TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    public void addCustomerOrders(String orderID, String orderDate, String storeType,
                                  String storeEmailId, String userid, String productsku,
                                  String productName, String productDesc, String oldPrice,
                                  String newPrice, String tax, String pricematch, String pmdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_CO_ORDERID, orderID);
            values.put(COL_CO_ORDERDATE, orderDate);
            values.put(COL_CO_STORETYPE, storeType);
            values.put(COL_CO_STOREEMAILID, storeEmailId);
            values.put(COL_CO_USERID, userid);
            values.put(COL_CO_PRODUCTSKU, productsku);
            values.put(COL_CO_PRODUCTNAME, productName);
            values.put(COL_CO_PRODUCTDESC, productDesc);
            values.put(COL_CO_OLDPRICE, oldPrice);
            values.put(COL_CO_NEWPRICE, newPrice);
            values.put(COL_CO_TAX, tax);
            values.put(COL_CO_PRICEMATCH, pricematch);
            values.put(COL_CO_PMDATE, pmdate);
            db.insert(CUSTOMER_ORDERS_TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }


    public CustomerOrders getCustomerOrderByOrderID(String orderID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        strQuery = "Select * from " + CUSTOMER_ORDERS_TABLE_NAME + " where " + COL_CO_ORDERID + "='" + orderID + "'";
        cursor = db.rawQuery(strQuery, null);
        CustomerOrders orders = new CustomerOrders();
        try {
            if (cursor.moveToFirst()) {
                orders.setOrderid(cursor.getString(1));
                orders.setOrderdate(cursor.getString(2));
                orders.setStoretype(cursor.getString(3));
                orders.setStoreemailid(cursor.getString(4));
                orders.setUserid(cursor.getString(5));
                orders.setProductsku(cursor.getString(6));
                orders.setProductname(cursor.getString(7));
                orders.setProductdesc(cursor.getString(8));
                orders.setOldprice(cursor.getString(9));
                orders.setNewprice(cursor.getString(10));
                orders.setTax(cursor.getString(11));
                orders.setPricematch(cursor.getString(12));
                orders.setPmdate(cursor.getString(13));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return orders;
    }

    public Customers getCustomerByUserID(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        strQuery = "Select * from " + CUSTOMER_TABLE_NAME + " where " + COL_CC_USERID + "='" + userID + "'";
        cursor = db.rawQuery(strQuery, null);
        Customers customers = new Customers();
        try {
            if (cursor.moveToFirst()) {
                customers.setUserid(cursor.getString(1));
                customers.setPassword(cursor.getString(2));
                customers.setCxname(cursor.getString(3));
                customers.setPhone(cursor.getString(4));
                customers.setAddress(cursor.getString(5));
            }

        } finally {
            cursor.close();
            db.close();
        }
        return customers;
    }


    public List<CustomerOrders> getCustomerOrdersByUserID(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        List<CustomerOrders> ordersList = new ArrayList<CustomerOrders>();
        strQuery = "Select * from " + CUSTOMER_ORDERS_TABLE_NAME + " where " + COL_CO_ORDERID + "='" + userID + "'";
        cursor = db.rawQuery(strQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CustomerOrders order = new CustomerOrders();
                order.setOrderid(cursor.getString(1));
                order.setOrderdate(cursor.getString(2));
                order.setStoretype(cursor.getString(3));
                order.setStoreemailid(cursor.getString(4));
                order.setUserid(cursor.getString(5));
                order.setProductsku(cursor.getString(6));
                order.setProductname(cursor.getString(7));
                order.setProductdesc(cursor.getString(8));
                order.setOldprice(cursor.getString(9));
                order.setNewprice(cursor.getString(10));
                order.setTax(cursor.getString(11));
                order.setPricematch(cursor.getString(12));
                order.setPmdate(cursor.getString(13));
                ordersList.add(order);
            } while (cursor.moveToNext());
        }
        return ordersList;
    }


}