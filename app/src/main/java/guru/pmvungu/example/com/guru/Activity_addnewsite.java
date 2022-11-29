package guru.pmvungu.example.com.guru;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import guru.pmvungu.example.com.includes.ConnectionDetector;
import guru.pmvungu.example.com.includes.base64;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getAntModel;
import static guru.pmvungu.example.com.includes.apiUrl.getDevice;
import static guru.pmvungu.example.com.includes.apiUrl.getSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.getSitename;
import static guru.pmvungu.example.com.includes.apiUrl.getUserId;
import static guru.pmvungu.example.com.includes.apiUrl.strListOfAntennaModel;
import static guru.pmvungu.example.com.includes.apiUrl.strListOfSite;

public class Activity_addnewsite extends AppCompatActivity implements LocationListener{
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
    private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
    private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;
    private static final int SCAN_SIM_BAR_CODE = 49374;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    final int REQUIRED_SIZE = 512;

    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;
    TelephonyManager device ;
    ConnectionDetector connector;
    libClass _libClass ;

     EditText edsitename;
     Spinner spnport;
     AutoCompleteTextView antmodel,acsitename;
     TextView idsite,antfullname;
     Button btnvalidation;
     String SV_AntfullName;

    final DBAdapter db = new DBAdapter(Activity_addnewsite.this);
    //private GpsTracker gpsTracker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewsite);

        _libClass = new libClass(Activity_addnewsite.this);
        idsite = (TextView) findViewById(R.id.tvid);
        antfullname = (TextView) findViewById(R.id.tvAntfullname);
       // edsitename = (EditText) findViewById(R.id.edtsitename);
        acsitename = (AutoCompleteTextView) findViewById(R.id.edtsitename);

        antmodel = (AutoCompleteTextView) findViewById(R.id.edantennamodel);
        spnport = (Spinner) findViewById(R.id.spnport);
        btnvalidation = (Button)findViewById(R.id.validateButton);

      //  edsitename.setEnabled(true);
        acsitename.setEnabled(true);
        SV_Id="0";
        SV_AntfullName="Antenna";
        SV_Nodedesc="";

        try {
          ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
                 setTitle("CREATE NEW SITE");
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                         //if (extras.containsKey("ant_fullname")) {
                        if (getIntent().hasExtra("ant_fullname")) {
                            SV_AntfullName = getIntent().getStringExtra("ant_fullname");
                            // edsitename.setEnabled(false);
                             acsitename.setEnabled(false);
                           //  parentActivityName=".MainActivity";
                             setTitle("ADD ANTENNA");
                        }

                        if (getIntent().hasExtra("nodedesc")) {
                            SV_Nodedesc = getIntent().getStringExtra("nodedesc");
                        }
                    }
                     else{
                        btnvalidation.setEnabled(false);
                        return;
                    }

        } catch (Exception e){
               e.printStackTrace();
            }

            //SV_AntfullName=(extras.containsKey("ant_fullname"))? getIntent().getStringExtra("ant_fullname"):"Antenna" ;

        try {

            SV_Device=getDevice();
            acsitename.setText(getSitename());
            //edsitename.setText(getSitename());
            SV_Id=getCurrentIdsitename(getAntModel(),getSitename(),getSessionId(),SV_AntfullName);
            idsite.setText(SV_Id);

           String Antennalabel[]=SV_AntfullName.trim().split(".");
           /* if (Antennalabel.length <=0){
               antfullname.setText(SV_AntfullName);
           }else{
               antfullname.setText(Antennalabel[0]);
           }*/


            int posInt=SV_AntfullName.trim().indexOf(".");
            if(posInt >=0){
                SV_AntfullName=SV_AntfullName.substring(0,posInt);
            }
            antfullname.setText(SV_AntfullName);
        //set antenna model and port
        //antmodel.setText(getAntModel());//set antenna model and pair port
        //spnport.setSelection(new libClass(this).getIndex(spnport, SV_Port));

        new libClass(this).fillAutoComplete(antmodel,strListOfAntennaModel);
        new libClass(this).fillAutoComplete(acsitename,strListOfSite);
       //set sitename_id
         btnvalidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getLocation();
                onValidation("insert");
            }
        });



            if(checkPermission()){
                //  Toast.makeText(Activity_sitedetails.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                requestPermission();
            }

           /* if(isConfiguration()){
                uploaderBtn.setText("Updated site detail !");
            }*/

        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public String getCurrentIdsitename(String AntModel,String Antsitename,String AntIdOps,String strAntenna){
         String id ="";
     try {

        String query = " select DISTINCT sitename_id,a_port from tbl_siteconfiguration where lower(a_marketing_name)=lower('" + strAntenna + "')" +
                " and lower(sitename)=lower('" + Antsitename + "') ;";
        db.open();
        Cursor cursor = db.getsitedetails(query);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                id = cursor.getString(0);
                SV_Id=cursor.getString(0);
                SV_Port=cursor.getString(1);

            } while (cursor.moveToNext());

        }
          return id;
        }catch (Exception e){
             e.printStackTrace();
           return "";
        }
    }
    LocationManager locationManager;
    public  void vgetLocation() {
        try {

            if (ActivityCompat.checkSelfPermission( Activity_addnewsite.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( Activity_addnewsite.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions((Activity)  Activity_sitedetails.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,this);


            }


        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
         latitude=location.getLatitude();
         longitude=location.getLatitude();

       Toast.makeText( Activity_addnewsite.this,
               "Current Location: " + location.getLatitude() + ", " + location.getLongitude(),Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(Activity_addnewsite.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    float lat2,long2;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1; // 30 second
    private float getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
        return distance[0];
    }
    public void onValidation(String strvalidation){
        connector = new ConnectionDetector(getApplicationContext());
        TextView lblError = (TextView) findViewById(R.id.lblError);
        // boolean isConnected = isNetworkAvailable();
        if (connector.isConnectingToInternet()){
            lblError.setText("connected");
        }else{
            lblError.setText("Ouups!!! probleme de connection.");
            showAlertDialog(Activity_addnewsite.this, "No Internet Connection",
                    "Ouups!!! probleme de connection.", false);
            return;
        }

        if (blnCheckDataBeforeUpload() == true){
             strURLInsertRegistration = baseUrl + "addNewsiteId" ;
                     //"id="+SV_Id+"&Ant_model="+SV_AntennaModel+"&Site_Name="+SV_Sitename+"&id_ant="+SV_idAnt+"&Ant_port="+SV_Port+"&imei="+SV_Device+"&users="+SV_userid;
            new ImageUploadTaskAsync().execute(SV_Id,SV_AntennaModel,SV_Sitename,SV_idAnt,SV_Port,SV_Device,SV_userid,SV_AntfullName,SV_Nodedesc );
        }else{
            //lblError.setText("Ouups!!! probleme de connection.");
            showAlertDialog(Activity_addnewsite.this, "Error occur",
                    "Please fill all fields !!!", false);
            return;
        }
    }
    private libClass libclass;
    public class  ImageUploadTaskAsync extends AsyncTask<String,String,String> {
        private ProgressDialog dialog = new ProgressDialog(Activity_addnewsite.this);
        HttpsURLConnection conn;

        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait while uploading...");
            Button btnvalidation = (Button)findViewById(R.id.validateButton);
            btnvalidation.setVisibility(View.GONE);
            dialog.setCancelable(false);
            dialog.show();
        }

        protected String doInBackground(String... params) {

        /*    try {
                // url = new URL(strURLInsertRegistration);


                strURLInsertRegistration=Uri.parse(strURLInsertRegistration).toString();
                return libclass.parse(strURLInsertRegistration);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "unsuccessful";
            }
*/

            InputStream is = null;
         try{

             URL url = new URL(strURLInsertRegistration);
             SSLContext sslcontext = SSLContext.getInstance("TLS");
             sslcontext.init(null,new TrustManager[]{new X509ExtendedTrustManager() {
                 @Override
                 public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

                 }

                 @Override
                 public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

                 }

                 @Override
                 public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

                 }

                 @Override
                 public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

                 }

                 @Override
                 public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                 }

                 @Override
                 public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                 }

                 @Override
                 public X509Certificate[] getAcceptedIssuers() {
                     return new X509Certificate[0];
                 }
             }},null);

             conn = (HttpsURLConnection) url.openConnection();
             conn.setHostnameVerifier(new HostnameVerifier() {
                 @Override
                 public boolean verify(String hostname, SSLSession session) {
                     return true;
                 }
             });
             conn.setSSLSocketFactory(sslcontext.getSocketFactory());
             conn.setReadTimeout(READ_TIMEOUT);
             conn.setConnectTimeout(CONNECTION_TIMEOUT);
             conn.setRequestMethod("POST");

             Uri.Builder builder = new Uri.Builder()
                     .appendQueryParameter("SV_Id", params[0])
                     .appendQueryParameter("SV_AntennaModel", params[1])
                     .appendQueryParameter("SV_Sitename", params[2])
                     .appendQueryParameter("SV_idAnt", params[3])
                     .appendQueryParameter("SV_Port", params[4])
                     .appendQueryParameter("SV_Device", params[5])
                     .appendQueryParameter("SV_userid", params[6])
                     .appendQueryParameter("SV_AntfullName", params[7])
                     .appendQueryParameter("SV_Nodedesc", params[8]);

             String query = builder.build().getEncodedQuery();

             // Open connection for sending data
             OutputStream os = conn.getOutputStream();
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(os, "UTF-8"));
             writer.write(query);
             writer.flush();
             writer.close();
             os.close();
            // conn.connect();
             //HttpsURLConnection urlConnection=(HttpsURLConnection) url.openConnection();
             int response_code = conn.getResponseCode();
                 // Check if successful connection made
                 if (response_code == HttpsURLConnection.HTTP_OK) {
                     Certificate[] certificates=conn.getServerCertificates();
                     Log.e("connexion","connexion :");
                     // Read data sent from server
                     InputStream input = conn.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                     StringBuilder result = new StringBuilder();
                     String line;

                     while ((line = reader.readLine()) != null) {
                         result.append(line);
                     }
                     // Pass data to onPostExecute method
                     return(result.toString());

                 }else{
                     Log.e("connexion","not connexion....");
                     return("unsuccessful");
                 }

            // }

        } catch (Exception e) {

             return "error occur:"+e.getMessage();
        }
        finally {
             conn.disconnect();
        }

    //return(Response.toString());

        }

        @Override
        protected void onPostExecute(String result) {
            // Toast.makeText(Activity_login.this, result, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            btnvalidation.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.e("SmsReceiver",result);

           // Button btnUpload = (Button)findViewById(R.id.validateButton);


            if (result!=null) {
                if (!result.equals("unsuccessful") || result.indexOf("error") < 0) {

                    loadJSonSiteConfiguration(result);
                     alertMsg(Activity_addnewsite.this, "", "Sitename added successfully.",this.getClass());
                    // Toast.makeText(getApplicationContext(), "Upload successfully.",Toast.LENGTH_LONG).show();
                   // callForm(Activity_addnewsite.this, Activity_site_menu.class);
                   // finish();
                    return;
                } else {
                    showAlertDialog(Activity_addnewsite.this, "Antenna", "error occur: " + result.toString(), true);
                }
                result="sorry operation failed !!!";
            }

            TextView lblError = (TextView) findViewById(R.id.lblError);
            lblError.setText(result);

        }
    }
    public String  deviceId,getSubscriberId,Networkcode,NetLac;
    private void FillCellId() {
        final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                //  ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                SV_Device=String.valueOf(telephony.getDeviceId());
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void alertMsg(Context context,String title,String msg,Class activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Intent intent = new Intent(getApplicationContext(), Class.class);
                // intent.putExtra(EXTRA_MESSAGE, strMessage);
               // startActivity(intent);
                finish();
            }
        });

    /*     builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });

       builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener()     {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });*/
        AlertDialog alert = builder.create();
        alert.show();
        return;
    }
    public String strURLInsertRegistration;
    private boolean isNetworkAvailable() {
        boolean isconnected=false;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isconnected=true;

            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch (Exception e){
            Log.e("ERROR ","ERROR :"+e.getMessage());
            return  isconnected;
        }
    }
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // Setting alert dialog icon
        // alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    private String strFinalXML,_SiteName;
    public boolean blnCheckDataBeforeUpload() {
        try {

            TextView lblError = (TextView) findViewById(R.id.lblError);
            lblError.setText("");

            SV_Port = spnport.getSelectedItem().toString();
            if (SV_Port.equals("") || SV_Port.equals("0")) {
                Toast.makeText(this, "Please select port paire.", Toast.LENGTH_LONG).show();
                // edtOwner.setError("Please fill Owner field.");
                //lblError.setText("Please fill Owner field.");
                return false;
            }

            SV_Sitename = acsitename.getText().toString();
            //SV_Sitename = edsitename.getText().toString();
            if (SV_Sitename.equals("")) {
                Toast.makeText(this, "Please fill the Sitename.", Toast.LENGTH_LONG).show();
                acsitename.setError("Please fill this field.");
                //return to site_menu form
                return false;
            }
            SV_Sitename=SV_Sitename.toUpperCase();




          //  String Antennalabel[]=SV_AntfullName.trim().split(".");

            String strAntfullName = antfullname.getText().toString();

            int posInt=strAntfullName.trim().indexOf(".");
            if(posInt >=0){
                SV_AntfullName=SV_AntfullName.substring(0,posInt);
             }else{
                SV_AntfullName=strAntfullName;
            }

            if (SV_AntfullName.equals("")) {
                Toast.makeText(this, "Some things is wrong!!!.", Toast.LENGTH_LONG).show();
                //return to antenna site_menu form
                return false;
            }

            SV_AntennaModel = antmodel.getText().toString();
            if (SV_AntennaModel.equals("")) {
                Toast.makeText(this, "Please fill this field.", Toast.LENGTH_LONG).show();
                antmodel.setError("Please fill this field.");
                return false;
            }

            SV_userid = getUserId();
            SV_Device = getDevice();
            //SV_Sitename=getSitename();
            SV_idAnt="0";

            SV_Id = idsite.getText().toString();
           if (SV_Nodedesc.equals("")) {
               SV_Id="0";
           }

            if (SV_Id.equals("")) {
                Toast.makeText(this, "Some things is wrong!!!.", Toast.LENGTH_LONG).show();
                //return to antenna site_menu form
                return false;
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }
    public String getDateTime(){
        String strDatetime="";
        try{
            Calendar calendar = Calendar.getInstance();
            int Year = calendar.get(Calendar.YEAR);
            int Month = calendar.get(Calendar.MONTH);
            int Day = calendar.get(Calendar.DAY_OF_MONTH);
            int Hour=calendar.get(Calendar.HOUR_OF_DAY);
            int Minute=calendar.get(Calendar.MINUTE);
            int Second=calendar.get(Calendar.SECOND);

            strDatetime=String.valueOf(Year).toString()
                    + "-"+ String.valueOf(Month).toString()
                    + "-"+String.valueOf(Day)
                    + " "+String.valueOf(Hour)
                    + ":"+String.valueOf(Minute)
                    + ":"+String.valueOf(Second);

        }catch (Exception e){

        }
        return  strDatetime;
    }
    public double latitude,longitude;
    private void requestPermission() {

        ActivityCompat.requestPermissions(Activity_addnewsite.this, new String[]
                {
                        CAMERA,
                        READ_CONTACTS,
                        READ_PHONE_STATE,
                        ACCESS_FINE_LOCATION,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        try {
            switch (requestCode) {
                case RequestPermissionCode:

                    if (grantResults.length > 0) {

                        boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean ReadContactsPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        boolean ReadPhoneStatePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                        boolean AccessFindLocationPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                        boolean WriteExternalPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                        if (CameraPermission && ReadContactsPermission && ReadPhoneStatePermission && AccessFindLocationPermission && WriteExternalPermission) {

                            Toast.makeText(Activity_addnewsite.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        } else {
                            //  Toast.makeText(Activity_deviceposition.this,"Permission Denied" +grantResults[2],Toast.LENGTH_LONG).show();

                        }
                    }

                    break;

            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Activity_addnewsite.this,"Permission Denied " +e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int fivethPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                fivethPermissionResult== PackageManager.PERMISSION_GRANTED;
    }
    int orientation ;
    public void callForm(Context context,Class activity){
        Intent intent = new Intent(context,activity);
        startActivity(intent);

    }
    public void alertMsgOkCancel(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callForm(Activity_addnewsite.this,MainActivity.class);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //callForm(MainActivity.this,MainActivity.class);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    private void loadJSonSiteConfiguration(String Strsite){
        try {

            db.open();
            //db.setsitedetails("DELETE FROM tbl_loading");
           db.setsitedetails("DELETE FROM tbl_siteconfiguration where sitename='"+SV_Sitename+"'");

            JSONArray jsonArray = new JSONArray(Strsite);
            String[] subs = new String[jsonArray.length()];

            for (int l = 0; l < jsonArray.length(); l++) {
                JSONObject obj = jsonArray.getJSONObject(l);
                 String Sqlcmd="INSERT INTO tbl_siteconfiguration (sitename_id,sitename,s_latitude,s_longitude,s_configuration, a_technology, a_model,a_port,username,idoperation,a_marketing_name) VALUES(" +
                         "'" + obj.getString("Site_id").toString()  + "'," +
                         "'" + obj.getString("Site_Name").toString()  + "'," +
                         "'" + obj.getString("latitude").toString()  + "'," +
                         "'" + obj.getString("longitude").toString()  + "'," +
                         "'" + obj.getString("configuration").toString()  + "'," +
                         "'" + obj.getString("Ant_technology").toString()  + "'," +
                         "'" + obj.getString("Ant_model").toString()  + "'," +
                         "'" + obj.getString("Ant_port").toString()  + "'," +
                         "'" + getUserId()  + "'," +
                         "'" + getSessionId()  + "'," +
                         "'"+ obj.getString("Ant_fullname").toString() + "'" +
                        ");";

                db.setsitedetails(Sqlcmd);

            }

           // db.setsitedetails("insert into tbl_loading(status)values('1');");
            db.close();

        }

        catch (JSONException e) {
            e.printStackTrace();
            db.close();
            showAlertDialog(Activity_addnewsite.this,"Antenna",e.toString(),true);

        }
    }
    public String  SV_Id,SV_Sitename,SV_AntennaModel,SV_idAnt,SV_Port,SV_userid,SV_Device,SV_Nodedesc;
    //end class






}
