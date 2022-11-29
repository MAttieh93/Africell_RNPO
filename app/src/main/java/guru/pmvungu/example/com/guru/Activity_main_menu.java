package guru.pmvungu.example.com.guru;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import guru.pmvungu.example.com.includes.ConnectionDetector;
import guru.pmvungu.example.com.includes.apiUrl;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.getSitename;
import static guru.pmvungu.example.com.includes.apiUrl.getUserId;
import static guru.pmvungu.example.com.includes.apiUrl.setDeviceId;
import static guru.pmvungu.example.com.includes.apiUrl.setSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.setSitename;

public class Activity_main_menu extends AppCompatActivity {
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
    private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
    private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;
    private static final int SCAN_SIM_BAR_CODE = 49374;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    TelephonyManager device ;
    private GpsTracker gpsTracker;
    private libClass libclass;
    public String  deviceId,getSubscriberId,Networkcode,NetLac,SV_userName;
    public String SV_siteName,SV_Audit,SV_siteConfiguration,SV_owner,SV_vender,SV_antNumber,SV_roottop,SV_tower,SV_strlocation,SV_building,SV_Device;
    String SV_userid,SV_DateTime ,SV_IdOperation;
    Button btnNewaudit,btnAudit;
    AutoCompleteTextView edvSitename;
    ListView lstview;
    GridView gvSite;
    final DBAdapter db = new DBAdapter(Activity_main_menu.this);
    ConnectionDetector connectiondetector=new ConnectionDetector(Activity_main_menu.this);
    MyListAdapter mylistadapter;
    String[] languages={""};

    int pictureNbr;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;
    String[] listItem;
    String slist;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        SV_userid =getUserId();
        lstview=(ListView)findViewById(R.id.listview);
        SV_IdOperation="";

        try {
            libclass = new libClass(Activity_main_menu.this);
            if( libclass.checkPermission()){
               // Toast.makeText(Activity_main_menu.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                libclass.requestPermission(Activity_main_menu.this);

            }

         //get the configuration from dbb KIN190
         // checkconfigexist();

            edvSitename=(AutoCompleteTextView)findViewById(R.id.edtSitename);
             ArrayAdapter adapter = new
                    ArrayAdapter(this,android.R.layout.simple_list_item_1,apiUrl.strListOfSite);

            edvSitename.setAdapter(adapter);
            edvSitename.setThreshold(1);

            btnNewaudit = (Button) findViewById(R.id.btnNewaudit);
            btnNewaudit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // onClickbtnLogin();
                    SV_Audit="Newaudit";
                    Toast.makeText(getApplication(),"start clikc",Toast.LENGTH_LONG).show();
                    onClickbtnLogin();
                }
            });


            // FillCellId();
            getLocation();
            // FillSitename();
            FillGridview();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public  void checkconfigexist(){
     //Boolean isConfiguration=false;
     if (isConfiguration()){
          //callForm(Activity_main_menu.this,Activity_site_menu.class);
     }
    }
    public class DetailsAsyncTask extends AsyncTask<String, String, String> {
        URL url = null;
        private ProgressDialog dialog = new ProgressDialog(Activity_main_menu.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait one minute....");
            dialog.setCancelable(true);
            dialog.show();
        }
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(strURL);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception doInbackground";
            }

            return libclass.getStringForUrl(strURL);
        }

        @Override
        protected void onPostExecute(String result) {
            // String xmisdn=result.toString();
            try {

                if (result.indexOf("Error") > 0 || result==null){
                    libclass.popMessage ("Error occur",result);
                    // finish();
                    return;
                }
                FillDetails (result);

            } catch (Exception e) {
                e.printStackTrace();

            }
             dialog.dismiss();

        }

    }
    public Boolean isConfiguration(){
        Boolean bl=false;
        try{
            int i=0;
            db.open();  //open connection
           // Cursor c = db.getsitedetails("select count(*) ct from tbl_configuration");
            Cursor c = db.getsiteconfig();   //call fonction getHistory and select all record data
            if (c.getCount()>0) {
                bl= true;
            }else{
                bl= false;
            }

            db.close();

        }catch (Exception e){
            e.printStackTrace();
            db.close();

        }
        return  bl;

    }
    public double latitude,longitude;
    public void getLocation(){

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                // Toast.makeText(getBaseContext(),"gps",Toast.LENGTH_SHORT).show();

            }else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                callForm(Activity_main_menu.this,Activity_login.class,"","");
                finish();
            }
        } catch (Exception e){
            e.printStackTrace();

        }


        gpsTracker = new GpsTracker(Activity_main_menu.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            String _strHTML = ""
                    + ""
                    + "<b>Latitude GPS   : </b>" + String.valueOf(latitude) + "<br />"
                    + "<b>Longitude GPS  : </b>" + String.valueOf(longitude) + "<br />"
                    + "";

            TextView tvconfiguration =(TextView)findViewById(R.id.tvConfiguration) ;
            tvconfiguration.setText(Html.fromHtml(_strHTML));

        }else{
            gpsTracker.showSettingsAlert();
        }
    }
    private void FillCellId() {
     final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

      try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                //  ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                deviceId=String.valueOf(telephony.getDeviceId());
                getSubscriberId=telephony.getSubscriberId().toString();
                Networkcode=String.valueOf(telephony.getSimOperator());

                if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
                {
                    final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
                    if (location != null)
                    {
                       NetLac= location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff);
                    }

                }

            }else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            }
        } catch (Exception e){
            e.printStackTrace();
        }
 }
    public void callForm(Context context,Class activity,String session,String sitename){

         Intent intent = new Intent(context,activity);
         intent.putExtra("btnclicked", "continue");
        // intent.putExtra("isAntenna", true);
         intent.putExtra("sitename", sitename);
         intent.putExtra("session", session);
         startActivity(intent);
         // this.finish();


        }
    private void onClickbtnLogin() {
        TextView lblError = (TextView) findViewById(R.id.lblError);
        try {
            if (blnCheckDataBeforeUpload() == true) {
                if (connectiondetector.isConnectingToInternet()){
                    popMessage("New site :"+SV_siteName,"Are you sure to start a new site ?");
                }else{
                    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(true);
                    libclass.popMessage("Connexion","Oups!! please check your connexion internet!!!");
                    return;
                }
            }
        } catch (Exception e) {
                lblError.setText(e.getMessage());
        }

    }

    private Boolean blnCheckDataBeforeUpload() {
        TextView lblError = (TextView) findViewById(R.id.lblError);
        lblError.setText("");

        AutoCompleteTextView siteName = (AutoCompleteTextView) findViewById(R.id.edtSitename);
        SV_siteName = siteName.getText().toString();
        if (SV_siteName.equals("")) {
            Toast.makeText(this, "Please entry the sitename.", Toast.LENGTH_LONG).show();
            siteName.setError("Please entry the sitename.");
            siteName.findFocus();
            //lblError.setText("Please select the SiteName.");
            return false;
        }

        Integer antNumber=4;

        return true;
    }
    public class DownSiteNameTask extends AsyncTask<String, String, String> {
        URL url = null;
    private ProgressDialog dialog = new ProgressDialog(Activity_main_menu.this);
        @Override
    protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait one minute....");
            dialog.setCancelable(true);
            dialog.show();
        }
    protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(strURL);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception doInbackground";
            }

            return libclass.getStringForUrl(strURL);
        }
        @Override
    protected void onPostExecute(String result) {
            // String xmisdn=result.toString();
            try {

                if (result.indexOf("Error") > 0 || result==null){
                    libclass.popMessage ("Error occur",result);
                    // finish();
                    return;
                }

                FillConfig(result);

            } catch (Exception e) {
                e.printStackTrace();

            }

            dialog.dismiss();
        }

    }
    private void FillConfig(String listinfo){
        //SV_IdOperation="";
        SV_DateTime=libclass.getDateTime();
        if (SV_IdOperation.equals("") || SV_IdOperation==null){
            SV_IdOperation=((SV_DateTime.replaceAll(":","")).replaceAll("-","")).replace(" ","");
            SV_IdOperation= (SV_IdOperation +"0000").substring(2,13);
        }

        try {

            db.open();

            JSONArray jsonArray = new JSONArray(listinfo);
            String[] tabsite = new String[jsonArray.length()];
              if (tabsite.length<=0){
                alertMsg(this,"Warring:","The sitename "+SV_siteName+" is not yet configured." +
                          "!\n Please contact your system Admin.",MainActivity.class,null);
                 //this.finish();
                 return;
              }

            for (int l = 0; l < jsonArray.length(); l++) {
                JSONObject obj = jsonArray.getJSONObject(l);
                String sqlcmd="INSERT INTO tbl_siteconfiguration(sitename_id,sitename,s_latitude,s_longitude,s_configuration,a_technology,a_model,a_port,idoperation,username,a_marketing_name,datecreated) VALUES("
                        + "'"+ obj.getString("Site_id").toString()  + "'"
                        + ",'"+obj.getString("Site_Name").toString()  + "'"
                        + ",'"+obj.getString("latitude").toString() + "'"
                        + ",'"+obj.getString("longitude").toString() + "'"
                        + ",'"+obj.getString("configuration").toString() + "'"
                        + ",'"+obj.getString("Ant_technology").toString() + "'"
                        + ",'"+obj.getString("Ant_model").toString() + "'"
                        + ",'"+obj.getString("Ant_port").toString() + "'"
                        + ",'"+SV_IdOperation + "'"
                        + ",'"+ SV_userid + "'"
                        + ",'"+obj.getString("Ant_fullname").toString() + "'"
                        + ",'"+SV_DateTime + "'"
                        + ");";

               db.setsitedetails(sqlcmd);


            }

            db.close();
            setSitename(SV_siteName);
            setSessionId(SV_IdOperation);
            //setDeviceId(SV_Device);

            Intent intent = new Intent(Activity_main_menu.this,Activity_site_menu.class);
            intent.putExtra("sitename", SV_siteName);
            intent.putExtra("action", SV_Audit);
            startActivity(intent);
            //finish();

        }

        catch (JSONException e) {
            e.printStackTrace();

        }

        finally
        {

            // End the transaction.
            db.close();
            // Close database
        }
    }
    String strURL;
    private void FillDetails(String listinfo){

        db.open();
        db.deleteTable(db.TABLE_SITEDETAILS);
        db.deleteTable(db.TABLE_LOADING);
        db.insertloading("1");
          try {
            // listinfo= listinfo.substring(75).replace("</string>","");
            JSONArray jsonArray = new JSONArray(listinfo);
            String[] subs = new String[jsonArray.length()];

            for (int l = 0; l < jsonArray.length(); l++) {
                JSONObject obj = jsonArray.getJSONObject(l);

                //insert to dbb

                db.insertsitedetails(obj.getString("sitename").toString()
                        ,obj.getString("latitude_gps").toString()
                        ,obj.getString("longitude_gps").toString()
                        ,obj.getString("owner").toString()
                        , obj.getString("vendor_2g").toString()
                        ,obj.getString("vendor_3g").toString()
                        ,obj.getString("vendor_4g").toString()
                        ,obj.getString("owerbuildHeight").toString()
                        ,obj.getString("tower_pictures").toString()
                );

                // if (obj.getString("Name").equals("Roaming")) {
               /* map = new HashMap<String, String>();
                map.put("titre", ""); //empty
                map.put("description", obj.getString("Site_Name").toString());
                map.put("values", obj.getString("Ant_model").toString());
                map.put("header", ""); //empty
                listItem.add(map);*/
            }

            db.close();
            //SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem , R.layout.listbuton,
            //       new String[] { "titre","description", "values","header"}, new int[] { R.id.titre,R.id.description, R.id.values,R.id.header});
            // myListviewbutton.setAdapter(mSchedule);
        }

        catch (JSONException e) {
            e.printStackTrace();
            db.close();

        }
    }
    private  void FillGridview(){

        try{
      // Reference to TableLayout
      //  TableLayout tableLayout=(TableLayout)findViewById(R.id.tablelayout);
       // Add header row

      //  TableRow rowHeader = new TableRow(Activity_main_menu.this);
            // rowHeader.setBackgroundColor(Color.parseColor("#a01775"));
      //  rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
        //        TableLayout.LayoutParams.WRAP_CONTENT));
       // String[] headerText={"Session ID","SITENAME","TECHNICIAN","DATE"};

      /*  for(String c:headerText) {
            TextView tv = new TextView(Activity_main_menu.this);
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.LEFT);
            tv.setTextSize(15);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(c);
            rowHeader.addView(tv);
        }*/
       // tableLayout.addView(rowHeader);
            ArrayList<String> itmList = new ArrayList<String>();
            db.open();
           String sqlcmd="SELECT sitename,username, datecreated,idoperation FROM tbl_siteconfiguration GROUP BY sitename,username order by datecreated,sitename";

            Cursor lstSite=db.getsitedetails(sqlcmd);
            lstSite.moveToFirst();

            int i=1;
            slist="";
            if(lstSite.getCount() >0) {
                do {
                    String idoperation= lstSite.getString(lstSite.getColumnIndex("idoperation"));
                    String sitename= lstSite.getString(lstSite.getColumnIndex("sitename"));
                    String username= lstSite.getString(lstSite.getColumnIndex("username"));
                    String datecreated= lstSite.getString(lstSite.getColumnIndex("datecreated"));
                    slist=slist + idoperation +";" + sitename +";" +username +";" +datecreated +"--";

                    // dara rows
                /*  TableRow row = new TableRow(Activity_main_menu.this);
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                    String[] colText={idoperation ,sitename,username,datecreated};

                    for(String text:colText) {
                        TextView tv = new TextView(this);
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.LEFT);
                        tv.setTextSize(12);
                        tv.setPadding(5, 5, 5, 5);
                        tv.setText(text);
                        row.addView(tv);
                    }
                    tableLayout.addView(row);
                */
                    i +=1;

                } while (lstSite.moveToNext());

                listItem=slist.split("--");

            }

            db.close();

            ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.header, lstview,false);
            // Add header view to the ListView
           lstview.addHeaderView(headerView);
           LstViewAdapter adapter=new LstViewAdapter(this,R.layout.lvsitename,R.id.tvidoperation,listItem);
           lstview.setAdapter(adapter);

           lstview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                @SuppressWarnings("unchecked")
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    final int idRow=position+1;
                    String sLigne=lstview.getItemAtPosition(position).toString();

                    String[] items=sLigne.split(";");
                    if(items[0] !=null) SV_IdOperation =items[0];
                    if(items[1] !=null) SV_siteName=items[1];
                    if(items[2] !=null) SV_userName=items[2];
                    //String vdate=items[3];
                    // Toast.makeText(Activity_main_menu.this,sLigne,Toast.LENGTH_SHORT).show();
                     showMessage("Audit "+SV_siteName,"What action do you want to do ?");

                }});

        }catch (Exception e){
                    e.printStackTrace();
         }
        finally
        {
             // End the transaction.
            db.close();
            // Close database
        }
    }
    public void popMessage(String strTitle,String strMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_main_menu.this);
        builder.setMessage(strMessage);
        builder.setTitle(strTitle);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try{
                    db.open();
                    Cursor c=db.getsitedetails("SELECT count(*) site from tbl_siteconfiguration where sitename='"+SV_siteName+"'");
                    //c.moveToFirst();
                    int val=Integer.parseInt(c.getString(0));

                    if ( val > 0){
                        libclass.popMessage("New site","Sorry,This sitename already exist!!!\nPlease select from the list");
                        return;
                    }

                    strURL=baseUrl + "getSiteconfiguration?sitename="+SV_siteName;
                    new  DownSiteNameTask().execute("sitename");
                }catch (Exception e){
                    libclass.popMessage("New site",e.getMessage());
                    return;
                }

            }
        });

         builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //callForm(MainActivity.this,MainActivity.class);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return;

    }
    public void showMessage(String strTitle,String strMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_main_menu.this);
        builder.setMessage(strMessage);
        builder.setTitle(strTitle);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
        try{
            setSitename(SV_siteName);
            setSessionId(SV_IdOperation);

             db.open();
             String cmdstr="SELECT * FROM  tbl_loading where  sessionid='"+SV_IdOperation+"' and sitename='"+SV_siteName+"';";
             Cursor cursor = db.getsitedetails(cmdstr);

            if (cursor.getCount() == 0) {
                //refresh tbl_SiteVisit where session and sitename
                        String listofSite=SV_siteName+":"+SV_IdOperation;
                        strURL = baseUrl + "getSiteVisit?strlistofSite=" + listofSite;
                        new LoadinSitVist().execute("strlistofSite");

            }else{
                callForm(Activity_main_menu.this,Activity_site_menu.class,SV_IdOperation,SV_siteName);
            }
             db.close();


        }catch (Exception e){
            e.printStackTrace();
        }

        }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //callForm(MainActivity.this,MainActivity.class);
              String user=getUserId();
                if (user.trim().toString().equals(SV_userName.trim())){
                    //delete config
                    try{
                        db.open();
                        db.setsitedetails("delete from tbl_siteconfiguration where idoperation='"+SV_IdOperation+"' and sitename='"+SV_siteName+"'");
                        db.close();

                        callForm(Activity_main_menu.this,Activity_main_menu.class,SV_IdOperation,SV_siteName);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    libclass.popMessage("DELETE","Permision denied to delete "+SV_siteName);
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return;

    }
    public class LoadinSitVist extends AsyncTask<String, String, String> {
        URL url = null;
        private ProgressDialog dialog = new ProgressDialog(Activity_main_menu.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait the loading data....");
            dialog.setCancelable(false);
            dialog.show();
        }
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(strURL);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception doInbackground";
            }

            return libclass.getStringForUrl(strURL);
        }
        @Override
        protected void onPostExecute(String result) {
            // String xmisdn=result.toString();
            try {

                if (result.indexOf("unsucess") >= 0 || result==null || result.indexOf("Error") >= 0){
                    new  MainActivity().alertMsg(Activity_main_menu.this,"Error occur","loading fail." +result.toString(),Activity_main_menu.class,Activity_main_menu.class);
                    dialog.dismiss();
                    return;
                }
                if (result.indexOf("timeout") >= 0){
                    new  MainActivity().alertMsg(Activity_main_menu.this,"","Connection failled: "+result.toString(),Activity_main_menu.class,Activity_main_menu.class);
                    dialog.dismiss();
                    return;
                }
                //load data json
                loadSiteVisit (result,getSitename(),getSessionId());

            } catch (Exception e) {
                e.printStackTrace();
                new  MainActivity().alertMsg(Activity_main_menu.this,"Error occur",e.getMessage(),Activity_main_menu.class,Activity_main_menu.class);
            }

            dialog.dismiss();
        }

    }
    private void loadSiteVisit(String listinfo,String siteName,String sessionId){
        try {

            db.open();
            db.setsitedetails("DELETE FROM tbl_SiteVisit WHERE SV_IdOperation='"+sessionId+"' and SV_SiteName='"+siteName+"'");
            JSONArray jsonArray = new JSONArray(listinfo);
            String[] subs = new String[jsonArray.length()];
            for (int l = 0; l < jsonArray.length(); l++) {
                JSONObject obj = jsonArray.getJSONObject(l);

                // String Sqlcmd="INSERT INTO tbl_SiteVisit ( SV_IdOperation , SV_SiteName , SV_SiteConfig , SV_DateTime , SV_Coordinates , SV_AuditedBy , SV_Rigger , SV_Owner , SV_Vendor , SV_Vendor_2G , SV_Vendor_3G , SV_Vendor_4G , SV_RooftopTower , SV_BuildingHweight , SV_TotNumAntennas , SV_Tech ,  SV_Sector , SV_Description , SV_Pictures ,   SV_Plan , SV_Actual , SV_AntennaModel , SV_Remarks , SV_Device , SV_Status , SV_Latitude , SV_Longitude ) VALUES("
                String Sqlcmd="INSERT INTO tbl_SiteVisit ( SV_IdOperation , SV_SiteName , SV_SiteConfig , SV_DateTime  , SV_Owner   , SV_Vendor_2G , SV_Vendor_3G , SV_Vendor_4G ,SV_BuildingHweight , SV_Tech   , SV_Description , SV_Pictures , SV_Actual , SV_Remarks , SV_Device , SV_Status , SV_Latitude , SV_Longitude, SV_Antenne , SV_Ports ) VALUES("
                        +  "'" +  obj.getString("SV_IdOperation").toString()  + "'," +
                        "'" + obj.getString("SV_SiteName").toString()  + "'," +
                        "'" + obj.getString("SV_SiteConfig").toString()  + "'," +
                        "'" + obj.getString("SV_DateTime").toString()  + "'," +
                        //  "'" + obj.getString("SV_Coordinates").toString()  + "'," +
                        // "'" + obj.getString("SV_AuditedBy").toString()  + "'," +
                        // "'" + obj.getString("SV_Rigger").toString()  + "'," +
                        "'" + obj.getString("SV_Owner").toString()  + "'," +
                        // "'" + obj.getString("SV_Vendor").toString()  + "'," +
                        "'" + obj.getString("SV_Vendor_2G").toString()  + "'," +
                        "'" + obj.getString("SV_Vendor_3G").toString()  + "'," +
                        "'" + obj.getString("SV_Vendor_4G").toString()  + "'," +
                        // "'" + obj.getString("SV_RooftopTower").toString()  + "'," +
                        "'" + obj.getString("SV_Actual").toString()  + "'," +
                        // "'" + obj.getString("SV_TotNumAntennas").toString()  + "'," +
                        "'" + obj.getString("SV_Tech").toString()  + "', " +
                        // "'" + obj.getString("SV_Sector").toString()  + "'," +
                        "'" + obj.getString("SV_Description").toString()  + "'," +
                        "'" + obj.getString("SV_Pictures").toString()  + "'," +
                        // "'" + obj.getString("strPersonalPhotoName").toString()  + "', " +
                        // "'" + obj.getString("SV_Plan").toString()  + "'," +
                        "'" + obj.getString("SV_Actual").toString()  + "'," +
                        // "'" + obj.getString("SV_AntennaModel").toString()  + "'," +
                        "'" + obj.getString("SV_Remarks").toString()  + "'," +
                        "'" + obj.getString("SV_Device").toString()  + "'," +
                        "'" + obj.getString("SV_Status").toString()  + "'," +
                        "'" + obj.getString("SV_Latitude_GPS").toString()  + "'," +
                        "'" + obj.getString("SV_Longitude_GPS").toString()  + "'," +
                        "'" + obj.getString("SV_Antenne").toString()  + "'," +
                        "'" + obj.getString("SV_Ports").toString()  + "'" +
                        ");";

                db.setsitedetails(Sqlcmd);

            }

            db.setsitedetails("INSERT INTO tbl_loading(status,sessionid,sitename) values('1','"+sessionId+"','" + siteName+"');");
            db.close();
            callForm(Activity_main_menu.this,Activity_site_menu.class,SV_IdOperation,SV_siteName);
        }

        catch (JSONException e) {
            e.printStackTrace();
            db.close();

        }
    }
    public void alertMsg(Context context, String title, String msg,final Class activityT,final Class ActivityF){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    Intent intent = new Intent(getBaseContext(), activityT);
                    intent.putExtra("mainActivity", "");
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // callForm(MainActivity.this,ActivityF);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return;
    }
}
