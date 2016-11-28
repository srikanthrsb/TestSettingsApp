package srikanthtuts.testsettingsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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
    private static final String COL_CC_LASTLOGIN = "lastlogin";


    // Customers Orders table name
    private static final String CUSTOMER_ORDERS_TABLE_NAME = "tblcustomersorders";
    private static final String COL_CO_ID = "id";
    private static final String COL_CO_ORDERID = "orderid";
    private static final String COL_CO_ORDERDATE = "orderdate";
    private static final String COL_CO_STORETYPE = "storetype";
    private static final String COL_CO_STOREEMAILID = "storeemailid";
    private static final String COL_CO_PRODUCTSKU = "productsku";
    private static final String COL_CO_PRODUCTNAME = "productname";
    private static final String COL_CO_PRODUCTDESC = "productdesc";
    private static final String COL_CO_OLDPRICE = "oldprice";
    private static final String COL_CO_NEWPRICE = "newprice";
    private static final String COL_CO_TAX = "tax";


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
                + COL_CC_ADDRESS + " TEXT," + COL_CC_LASTLOGIN + " TEXT" + ")";

        String CREATE_CUSTOMER_ORDERS_TABLE = "CREATE TABLE " + CUSTOMER_ORDERS_TABLE_NAME
                + "(" + COL_CO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CO_ORDERID + " TEXT," + COL_CO_ORDERDATE + " TEXT,"
                + COL_CO_STORETYPE + " TEXT," + COL_CO_STOREEMAILID + " TEXT,"
                + COL_CO_PRODUCTSKU + " TEXT," + COL_CO_PRODUCTNAME + " TEXT,"
                + COL_CO_PRODUCTDESC + " TEXT," + COL_CO_OLDPRICE + " TEXT,"
                + COL_CO_NEWPRICE + " TEXT," + COL_CO_TAX + " TEXT" + ")";

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
                            String cxAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_CC_USERID, cxUserID);
            values.put(COL_CC_PASSWORD, cxPassword);
            values.put(COL_CC_CXNAME, cxName);
            values.put(COL_CC_PHONE, cxPhone);
            values.put(COL_CC_ADDRESS, cxAddress);
            db.insert(CUSTOMER_TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    public void addCustomerOrders(String orderID, String orderDate, String storeType, String storeEmailId,String productsku, String productName, String productDesc, String oldPrice, String newPrice, String tax) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_CO_ORDERID, orderID);
            values.put(COL_CO_ORDERDATE, orderDate);
            values.put(COL_CO_STORETYPE, storeType);
            values.put(COL_CO_STOREEMAILID, storeEmailId);
            values.put(COL_CO_PRODUCTSKU, productsku);
            values.put(COL_CO_PRODUCTNAME, productName);
            values.put(COL_CO_PRODUCTDESC, productDesc);
            values.put(COL_CO_OLDPRICE, oldPrice);
            values.put(COL_CO_NEWPRICE, newPrice);
            values.put(COL_CO_TAX, tax);
            db.insert(CUSTOMER_ORDERS_TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }



}