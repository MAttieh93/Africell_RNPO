package guru.pmvungu.example.com.guru;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by pmvungu on 8/24/2017.
 */

public class DBAdapter  {

    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "Afr_rnpo";

    // private static final String TABLE_INFOS = "infomations";
    private static final String TABLE_SERVER = "tbl_server";
    public static final String TABLE_HISTORY = "tbl_history";

    public static final String KEY_ROWID = "_id";

    public static final String KEY_CODEPIN = "codepin";
    public static final String KEY_TICKET = "ticket";
    public static final String KEY_DATE= "datesend";
    public static final String KEY_MESSAGE = "message";

    public static final String KEY_IPSERVER = "ipserver";
    public static final String KEY_NAME = "name";
    public static final String KEY_APN = "apn";
    public static final String KEY_PROXY= "proxy";
    public static final String KEY_PORT = "port";
    public static final String KEY_USERNAME= "username";
    public static final String KEY_PWD = "pwd";
    public static final String KEY_MCC = "mcc";
    public static final String KEY_MNC= "mnc";
    public static final String KEY_ROWUID ="uid";

    public static final String KEY_SITENAME="SV_SiteName";
    public static final String TABLE_SITENAME="tbl_Site";
    public  static final String KEY_DEFAULTPAGE ="page";

    public static final String TABLE_SITECONFIG="tbl_siteconfiguration";
    public static final String TABLE_SITEDETAILS="tbl_details";
    public static final String TABLE_SITEVISIT="tbl_SiteVisit";
    public static final String TABLE_LOADING="tbl_loading";
    public static final String TABLE_ARRAY_MENU="tbl_menu";

    private static final int DATABASE_VERSION = 3;
   // private static final String TABLE_CREATE_INFOS ="create table infomations (_id integer primary key autoincrement, "
    //     + "imsi text not null, imei text not null,msisdn text null,message text null,ip text null);";

    private static final String TABLE_CREATE_TABLE_ARRAY_MENU ="create table tbl_menu (idmenu text , "
            + " fmnu text , label text ,objects text ,parent text );";

      private static final String TABLE_CREATE_SERVER ="create table tbl_server (_id integer primary key autoincrement,uid text , "
              + " ipserver text not null, name text ,apn text ,proxy text ,port text ,username text ,"
              + " pwd text ,mcc text ,mnc text );";

    private static final String TABLE_CREATE_HISTORY ="create table tbl_history (_id integer primary key autoincrement, "
            + "codepin text , ticket text not null,datesend text );";

    private static final String TABLE_CREATE_TBL_SITE=" create table tbl_Site (_id integer primary key autoincrement,SV_SiteName text); ";
    private static final String TABLE_CREATE_TBL_SITEVISIT="create table tbl_SiteVisit (id integer primary key autoincrement " +
            "      ,SV_IdOperation text" +
            "      ,SV_SiteName text" +
            "      ,SV_SiteConfig text" +
            "      ,SV_DateTime text" +
            "      ,SV_Coordinates text" +
            "      ,SV_AuditedBy text" +
            "      ,SV_Rigger text" +
            "      ,SV_Owner text" +
            "      ,SV_Vendor text" +
            "      ,SV_Vendor_2G text" +
            "      ,SV_Vendor_3G text" +
            "      ,SV_Vendor_4G text" +
            "      ,SV_RooftopTower text" +
            "      ,SV_Tower text" +
            "      ,SV_BuildingHweight text" +
            "      ,SV_TotNumAntennas text" +
            "      ,SV_Tech text" +
            "      ,SV_Sector text" +
            "      ,SV_Description text" +
            "      ,SV_Pictures text" +
            "      ,SV_Path_Pictures text" +
            "      ,SV_Plan text" +
            "      ,SV_Actual text" +
            "      ,SV_AntennaModel text" +
            "      ,SV_Remarks text" +
            "      ,SV_UserId text "+
            "      ,SV_Device text" +
            "      ,SV_Status text" +
            "      ,SV_Latitude text " +
            "      ,SV_Longitude text" +
            "      ,SV_Antenne text " +
            "      ,SV_Ports text" +
            " );";


    private static final String TABLE_CREATE_SITECONFIGURATION ="create table " +TABLE_SITECONFIG + " (_id integer primary key autoincrement,sitename_id text ,sitename text , "
            + " s_latitude text , s_longitude text ,s_configuration text ,a_technology text ,a_model text ,a_port text ,"
            + " a_marketing_name text,idoperation text,username text,datecreated date default CURRENT_DATE,flag text default 0);";

    private static final String TABLE_CREATE_TABLE_SITEDETAILS ="create table " +TABLE_SITEDETAILS + " (_id integer primary key autoincrement,sitename text , "
            + " s_latitude text , s_longitude text ,s_owner text ,s_vendor_2G text ,s_vendor_3G text ,s_vendor_4G text ,"
            + " s_buildingHweight text,s_pictures text  );";


    private static final String TABLE_CREATE_LOAD ="create table " +TABLE_LOADING + " ( status text,sessionid text,sitename text  );";

    private final Context context;
   // private DatabaseHelper DBHelper;
    public DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                // db.execSQL(TABLE_CREATE_INFOS);
                db.execSQL(TABLE_CREATE_HISTORY);
                //db.execSQL(TABLE_CREATE_SERVER);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {

              // db.execSQL(TABLE_CREATE_HISTORY);
                db.execSQL(TABLE_CREATE_SERVER);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {

                // db.execSQL(TABLE_CREATE_HISTORY);
                db.execSQL(TABLE_CREATE_TBL_SITE);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {

                // db.execSQL(TABLE_CREATE_HISTORY);
                db.execSQL(TABLE_CREATE_TBL_SITEVISIT);
                db.execSQL(TABLE_CREATE_TABLE_SITEDETAILS);
                db.execSQL(TABLE_CREATE_LOAD);

                db.execSQL(TABLE_CREATE_TABLE_ARRAY_MENU);
                db.execSQL(TABLE_CREATE_SITECONFIGURATION);

            } catch (SQLException e) {
                e.printStackTrace();
            }




        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

            db.execSQL("DROP TABLE IF EXISTS tbl_history");
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS tbl_SiteVisit");
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS tbl_Site");
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SITECONFIG);
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SITEDETAILS);
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LOADING);
            onCreate(db);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ARRAY_MENU);
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SERVER);
            onCreate(db);
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }


    public long insertsiteconfig(String sitename ,String s_latitude,String s_longitude,String s_configuration,String a_technology,String a_model,String a_port,
                             String a_marketing_name, String idoperation)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("sitename",sitename);
        initialValues.put("s_latitude",s_latitude);
        initialValues.put("s_longitude", s_longitude);
        initialValues.put("s_configuration", s_configuration);
        initialValues.put("a_technology", a_technology);
        initialValues.put("a_model", a_model);
        initialValues.put("a_port", a_port);
        initialValues.put("a_marketing_name", a_marketing_name);
        initialValues.put("idoperation", idoperation);

        return db.insert(TABLE_SITECONFIG, null, initialValues);
    }



    public Cursor getsiteconfig()
    {
        return db.query(TABLE_SITECONFIG, new String[] {"sitename_id","sitename","s_latitude","s_longitude","s_configuration",
                        "a_technology","a_model","a_port","idoperation"},
                null, null, null, null, "sitename");
    }

    public Cursor getAntmodel()
    {
       // return db.query(TABLE_SITECONFIG, new String[] {"sitename", "s_configuration","a_model"  },
        //        null, null, "sitename", null, null);


        String query ="SELECT DISTINCT sitename ,s_configuration ,a_model,a_port FROM "+TABLE_SITECONFIG ;
        Cursor  cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }


    public Boolean deleteTable(String xtable)
    {
        Boolean result=false;

        try{
            db.execSQL("delete from "+ xtable);

            result=true;
        } catch (Exception e){
            result=false;
        }
        return  result;

    }

    public Boolean deleteAll()
    {
        Boolean result=false;

        try{
            db.execSQL("delete from "+ TABLE_SITECONFIG);
            db.execSQL("delete from "+ TABLE_SITEDETAILS);
            result=true;
        } catch (Exception e){
            result=false;
        }
        return  result;

    }



 public long insertloading(String status  )
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("status",status);
        return db.insert(TABLE_SITECONFIG, null, initialValues);
    }



public long insertsitedetails(String sitename ,String s_latitude,String s_longitude,String s_owner,String s_vendor_2G,String s_vendor_3G,String s_vendor_4G,
                                 String s_buildingHweight, String s_pictures)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("sitename",sitename);
        initialValues.put("s_latitude",s_latitude);
        initialValues.put("s_longitude", s_longitude);
        initialValues.put("s_owner", s_owner);
        initialValues.put("s_vendor_2G", s_vendor_2G);
        initialValues.put("s_vendor_3G", s_vendor_3G);
        initialValues.put("s_vendor_4G", s_vendor_4G);
        initialValues.put("s_buildingHweight", s_buildingHweight);
        initialValues.put("s_pictures", s_pictures);
       // initialValues.put("idoperation", idoperation);
        return db.insert(TABLE_SITECONFIG, null, initialValues);
    }



 /*   public Cursor getsitedetails()
    {
        return db.query(TABLE_SITEDETAILS, new String[] {"sitename","s_latitude","s_longitude","s_owner",
                        "s_vendor_2G","s_vendor_3G","s_vendor_4G","s_vendor_4G" ,"s_buildingHweight","s_pictures"},
                null, null, null, null, "sitename");
    }

*/


    public Cursor getsitedetails(String sqlCmd)
    {
        // return db.query(TABLE_SITECONFIG, new String[] {"sitename", "s_configuration","a_model"  },
        //        null, null, "sitename", null, null);

        //String query ="SELECT DISTINCT sitename ,s_configuration ,a_model FROM "+TABLE_SITECONFIG ;
        Cursor cursor = db.rawQuery(sqlCmd, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }





    public Boolean setsitedetails(String sqlCmd)
    {
        Boolean result=false;

        try{
            db.execSQL(sqlCmd);
            result=true;
        } catch (Exception e){
            result=false;
        }
        return  result;

    }
















    //---insert a History into the database---
    public long insertHistory(String codepin, String ticket,String datesend)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CODEPIN, codepin);
        initialValues.put(KEY_TICKET, ticket);
        initialValues.put(KEY_DATE, datesend);
        return db.insert(TABLE_HISTORY, null, initialValues);
    }

    public long insertSite(String sitename)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SITENAME, sitename);
        return db.insert(TABLE_SITENAME, null, initialValues);
    }
    public Cursor getSite()
    {
        return db.query(TABLE_SITENAME, new String[] {KEY_ROWID,KEY_SITENAME},
                null, null, null, null, null);
    }

    public boolean deleteSite(String rowSitename)
    {
       return db.delete(TABLE_SITENAME, KEY_SITENAME + " like '=" + rowSitename +"'", null) > 0;

    }

    public Boolean deleteSiteAll()
    {
        Boolean result=false;

        try{
            db.execSQL("delete from "+ TABLE_SITENAME);
            result=true;
        } catch (Exception e){
            result=false;
        }
        return  result;

    }

public Boolean truncateRFData(){
    Boolean result=false;
    try{
        db.execSQL("delete from "+ TABLE_SITEVISIT);

        result=true;
    } catch (Exception e){
        result=false;
    }
    return  result;
}
public long insertRfdata(String SV_IdOperation ,String SV_siteName,String SV_SiteConfig,String SV_DateTime,String SV_Coordinates,String SV_owner,String SV_vender,
                        String SV_vender_2G, String SV_vender_3G, String SV_vender_4G, String SV_roottop,String SV_tower,String SV_building,String SV_antNumber,String SV_userid,String SV_device)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("SV_IdOperation",SV_IdOperation);
        initialValues.put("SV_UserId",SV_userid);
        initialValues.put("SV_siteName", SV_siteName);
        initialValues.put("SV_SiteConfig", SV_SiteConfig);
        initialValues.put("SV_DateTime", SV_DateTime);
        initialValues.put("SV_Coordinates", SV_Coordinates);
        initialValues.put("SV_Owner", SV_owner);
        initialValues.put("SV_Vendor", SV_vender);
        initialValues.put("SV_Vendor_2G", SV_vender_2G);
        initialValues.put("SV_Vendor_3G", SV_vender_3G);
        initialValues.put("SV_Vendor_4G", SV_vender_4G);
        initialValues.put("SV_RooftopTower", SV_roottop);
        initialValues.put("SV_Tower", SV_tower);
        initialValues.put("SV_BuildingHweight", SV_building);
        initialValues.put("SV_TotNumAntennas", SV_antNumber);
        initialValues.put("SV_Device", SV_device);
      //  initialValues.put("SV_Status", SV_status);

        return db.insert(TABLE_SITEVISIT, null, initialValues);
    }


    public Cursor getRfdata()
    {
        return db.query(TABLE_SITEVISIT, new String[] {"SV_IdOperation","SV_siteName","SV_SiteConfig","SV_DateTime",
                        "SV_Coordinates","SV_Owner","SV_Vendor","SV_RooftopTower","SV_Tower","SV_BuildingHweight","SV_TotNumAntennas","SV_UserId","SV_Device"},
                null, null, null, null, null);
    }

    //---retrieves all the information History-------------------------*
    public Cursor getHistory()
    {
        Cursor cursorReslt;
        cursorReslt=  db.query(TABLE_HISTORY, new String[] {KEY_ROWID, KEY_CODEPIN,KEY_TICKET,KEY_DATE},
                null, null, null, null, null);
        return cursorReslt;
    }



    //---deletes a particular History---
    public boolean deleteHistory(long rowId)
    {
        return db.delete(TABLE_HISTORY, KEY_ROWID + "=" + rowId, null) > 0;
    }

//parametre wap/apn
    public long insertWAP(String uID, String name,String apn,String ipserver,String proxy, String port,String username,String pwd, String mcc,String mnc)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWUID,uID);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_APN, apn);
        initialValues.put(KEY_IPSERVER, ipserver);
        initialValues.put(KEY_PROXY, proxy);
        initialValues.put(KEY_PORT, port);
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_PWD, pwd);
        initialValues.put(KEY_MCC, mcc);
        initialValues.put(KEY_MNC, mnc);
        return db.insert(TABLE_SERVER, null, initialValues);
    }
    //---retrieves all the information History-------------------------*
    public Cursor getWAP()
    {
        return db.query(TABLE_SERVER, new String[] {KEY_ROWUID, KEY_NAME,KEY_APN,KEY_IPSERVER,KEY_PROXY,KEY_PORT,KEY_USERNAME,KEY_PWD,KEY_MCC,KEY_MNC},
                null, null, null, null, null);
    }

    //---deletes a particular History---
    public boolean deleteWAP(String rowuId)
    {

        return db.delete(TABLE_SERVER, KEY_ROWUID + "='" + rowuId + "'", null) > 0;
    }


  /*
    //---retrieves a particular contact---
    public Cursor getInfo(String rowId) throws SQLException
    {
        Cursor mCursor =db.query(true, TABLE_INFOS, new String[] {KEY_ROWID, KEY_IMSI, KEY_IMEI,KEY_MSISDN,KEY_MESSAGE,KEY_IP},
                KEY_IMSI + "='" + rowId + "'",
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    */
}