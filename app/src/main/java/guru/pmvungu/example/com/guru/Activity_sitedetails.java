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
import android.location.LocationManager;
import android.location.LocationListener;

import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static  android.Manifest.permission.ACCESS_NETWORK_STATE;
import static  android.Manifest.permission.ACCESS_FINE_LOCATION;
import static  android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.text.TextUtils.isEmpty;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.getSitename;
import static guru.pmvungu.example.com.includes.apiUrl.getUserId;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import guru.pmvungu.example.com.includes.ConnectionDetector;
import guru.pmvungu.example.com.includes.apiUrl;
import  guru.pmvungu.example.com.includes.base64;
import guru.pmvungu.example.com.includes.apiUrl;
import javax.net.ssl.HttpsURLConnection;

public class Activity_sitedetails extends AppCompatActivity implements LocationListener{
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
    private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
    private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;
    private static final int SCAN_SIM_BAR_CODE = 49374;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    final int REQUIRED_SIZE = 512;
    private int imgwidth=250;
    private int imgheight=190;

    private ImageView mPersonalPhoto;
    private String strPersonalPhoto;
    private Uri PersonalPhotoUri;
    private String strPersonalPhotoURL;

    // number of images to select
    private static final int PICK_IMAGE = 1;

    private String strGeoLocation,SV_Status;

    @SuppressWarnings("unused")
    private int intNbrRequestedFace;

    private Bitmap bitmap;
    private String text;

    TelephonyManager device ;
    Button getLocationBtn,uploaderBtn,getPicture1 ;

    int pictureNbr;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;

    ConnectionDetector connector;
    libClass _libClass ;
    public TextView photoflag,vsiteflag,editSite;
    Button btnUpload ;
     EditText edtlatlonggps,edtlatlongsite,edttowerheight,edtdistance;
    Spinner inputtext, edtOwner,edtvender2g,edtvender3g,edtvender4g,edtvender5g;

    final DBAdapter db = new DBAdapter(Activity_sitedetails.this);
    private GpsTracker gpsTracker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitedetails);

        _libClass = new libClass(Activity_sitedetails.this);
        device = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        edtOwner = (Spinner) findViewById(R.id.edtowner);
        edtvender2g = (Spinner) findViewById(R.id.edtvender2g);
        edtvender3g = (Spinner) findViewById(R.id.edtvender3g);
        edtvender4g = (Spinner) findViewById(R.id.edtvender4g);

        edtlatlonggps = (EditText) findViewById(R.id.edtlatlonggps);
       // edtlongitude = (EditText) findViewById(R.id.edtlongitudegps);

        edttowerheight = (EditText) findViewById(R.id.edttowerheight);
        uploaderBtn = (Button)findViewById(R.id.validateButton);
        uploaderBtn.setVisibility(View.GONE);
        getPicture1 = (Button)findViewById(R.id.btnCapture1);

        //tvLocation = (TextView)findViewById(R.id.lblLocation);
        photoflag = (TextView)findViewById(R.id.photoflag);
        vsiteflag  = (TextView)findViewById(R.id.siteflag);
        editSite =(TextView)findViewById(R.id.txtedit);

        strPersonalPhoto = "";
        intNbrRequestedFace = 0;
        strPersonalPhotoURL = "";
        // sEnableDisable("0");
        photoflag.setText("0");
        vsiteflag.setText("0");
        SV_Flag="0";

        uploaderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getLocation();
                onValidation("insert");
            }
        });

        getPicture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureNbr=1;
                setPictures(pictureNbr);
            }
        });


        editSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SV_Flag="1";
               enabled(false);
            }
        });


        try {
            if(checkPermission()){
                //  Toast.makeText(Activity_sitedetails.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                requestPermission();
            }


           /*
            String[] listVar={"Africell","Helios" ,"Ericson","Huawei","Huawei et Ericson"};
            ArrayAdapter  adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1,listVar);
            edtOwner.setAdapter(adapter);

            adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1,listVar);
            edtvender2g.setAdapter(adapter);

            adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1,listVar);
            edtvender3g.setAdapter(adapter);

            adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1,listVar);
            edtvender4g.setAdapter(adapter);

          */
            tmenu(); //fill splinner owner and vender
            enabled(false);
            getLocation();
            getSiteDetails();
            FillCellId();


           /* if(isConfiguration()){
                uploaderBtn.setText("Updated site detail !");
            }*/

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void tmenu(){
        //get vender list
        List<String>listvender=new ArrayList<String>();
        List<String>listowner=new ArrayList<String>();

        listowner.add("");
        listvender.add("");

        db.open();
        Cursor cursor=db.getsitedetails("SELECT distinct label FROM tbl_menu Where objects like 'vender%' and parent like 'detail%'");
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                listvender.add(cursor.getString(cursor.getColumnIndex("label")));

            }while (cursor.moveToNext());

        }
//get owner list
        cursor=db.getsitedetails("SELECT distinct label FROM tbl_menu Where objects like 'owner%' and parent like 'detail%'");
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                listowner.add(cursor.getString(cursor.getColumnIndex("label")));

            }while (cursor.moveToNext());

        }


        ArrayAdapter<String> adapterOwner=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listowner);
        adapterOwner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtOwner.setAdapter(adapterOwner);


        ArrayAdapter<String> adapterVender=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listvender);
        adapterVender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtvender2g.setAdapter(adapterVender);
        edtvender3g.setAdapter(adapterVender);
        edtvender4g.setAdapter(adapterVender);



    }

    public void getLocation(){
        try {
            gpsTracker = new GpsTracker(Activity_sitedetails.this);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

                // Toast.makeText(getApplicationContext(),_strHTML,Toast.LENGTH_LONG).show();

                if ((latitude != 0) && (longitude != 0)) {
                    edtlatlonggps.setEnabled(false);
                    //edtlongitude.setEnabled(false);
                }

                String postiongps = String.valueOf(latitude) + "," + String.valueOf(longitude);
                edtlatlonggps.setText(postiongps);
                // edtlongitude.setText(String.valueOf(longitude));

            } else {
                gpsTracker.showSettingsAlert();
            }

            db.open();
            Cursor c=db.getsitedetails("SELECT sitename,s_latitude,s_longitude,s_configuration,a_technology,a_model,a_port,idoperation,username FROM tbl_siteconfiguration where idoperation='"+getSessionId()+"' and LOWER(sitename)=LOWER('"+getSitename()+"')");
            c.moveToFirst();
            lat2 = (c.getString(c.getColumnIndex("s_latitude"))).equals("")? 0: Float.parseFloat(c.getString(c.getColumnIndex("s_latitude"))) ;
            long2 = (c.getString(c.getColumnIndex("s_longitude"))).equals("")? 0: Float.parseFloat(c.getString(c.getColumnIndex("s_longitude"))) ;

             //lat2=  Float.parseFloat(c.getString(c.getColumnIndex("s_latitude")));
           // long2=Float.parseFloat(c.getString(c.getColumnIndex("s_longitude")));

            //Toast.makeText(Activity_sitedetails.this,c.getString(c.getColumnIndex("s_latitude")),Toast.LENGTH_LONG).show();

            db.close();

            EditText edtlatlongsite = (EditText) findViewById(R.id.edtlatlongsite);
            edtlatlongsite.setText(String.valueOf(lat2) +','+String.valueOf(long2));
            if (long2 != 0 && lat2 !=0){
                edtlatlongsite.setEnabled(false);
            }

            edtdistance = (EditText) findViewById(R.id.edtdistance);
            float distance=0;
             distance = getDistance(latitude, longitude, lat2, long2);

            DecimalFormat df = new DecimalFormat("0.00");
            double number = distance;
            if (distance <=10){
                edtdistance.setTextColor(Color.GREEN);
            }else{
                edtdistance.setTextColor(Color.RED);
            }
            String sdistance = String.valueOf(df.format(number) + " m");


            edtdistance.setText(sdistance);
           // vgetLocation();


        }catch (Exception e){
            e.printStackTrace();
           // Toast.makeText(Activity_sitedetails.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }


    LocationManager locationManager;
    public  void vgetLocation() {
        try {

            if (ActivityCompat.checkSelfPermission( Activity_sitedetails.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( Activity_sitedetails.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

       Toast.makeText( Activity_sitedetails.this,
               "Current Location: " + location.getLatitude() + ", " + location.getLongitude(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(Activity_sitedetails.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
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
    public void getSiteDetails(){
        try{
            SV_userid = getUserId();
            vsiteflag  = (TextView)findViewById(R.id.siteflag);
            db.open();
            String sqlcmd="SELECT SV_IdOperation,SV_SiteName,SV_Owner,SV_Vendor_2G,SV_Vendor_3G,SV_Vendor_4G,SV_BuildingHweight,SV_Device, SV_Status,SV_Pictures FROM tbl_SiteVisit where lower(SV_SiteName)=lower('"+getSitename()+"')    and SV_IdOperation='"+getSessionId()+"' and SV_Status='100' order by id asc";
            Cursor cursor=db.getsitedetails(sqlcmd);
            cursor.moveToFirst();
            int ct= cursor.getCount();
            if (ct >0){
                enabled(true);
                cursor.moveToFirst();

                SV_IdOperation = cursor.getString(0);
                SV_SiteName = cursor.getString(1);
                SV_Owner = cursor.getString(2);
                SV_Vendor_2G = cursor.getString(3);
                SV_Vendor_3G = cursor.getString(4);
                SV_Vendor_4G = cursor.getString(5);
                SV_BuildingHweight = cursor.getString(6);
               // SV_userid =cursor.getString(7);
                SV_Device= cursor.getString(7);
                SV_Status = cursor.getString(8);
                SV_Pictures=cursor.getString(9);

                TextView tsitedetails=(TextView)findViewById(R.id.txtSitedetails);
                tsitedetails.setVisibility(View.VISIBLE);
                 String tv="" +
                         "Owner : "+SV_Owner+"<br/>" +
                         "Vender 2G : "+SV_Vendor_2G+"<br/>" +
                         "Vender 3G : "+SV_Vendor_3G+"<br/>" +
                         "Vender 4G : "+SV_Vendor_4G+"<br/>" +
                         "Tower Height : "+SV_BuildingHweight+"<br/>";

                tsitedetails.setText(Html.fromHtml(tv));
                TextView tedit=(TextView)findViewById(R.id.txtedit);
                tedit.setVisibility(View.VISIBLE);

                Bitmap bmp;
                byte[] imageAsBytes;
                imageAsBytes = Base64.decode(SV_Pictures.getBytes(), Base64.DEFAULT);
                bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                ImageView  image=(ImageView)findViewById(R.id.imgpicture);
                image.setAdjustViewBounds(true) ;
                image.setImageBitmap(bmp);
                strPersonalPhoto=SV_Pictures;
                vsiteflag.setText("1");
                photoflag.setText("1");

               // uploaderBtn.setText("update site details");
               // uploaderBtn.setVisibility(View.VISIBLE);


            }
        } catch (Exception e){
            e.printStackTrace();
            vsiteflag.setText("0");
        }

    }
    public void enabled(Boolean bl){
        if (bl){
            ((Spinner)findViewById(R.id.edtowner)).setVisibility(View.GONE);
            ((Spinner)findViewById(R.id.edtvender2g)).setVisibility(View.GONE);
            ((Spinner)findViewById(R.id.edtvender3g)).setVisibility(View.GONE);
            ((Spinner)findViewById(R.id.edtvender4g)).setVisibility(View.GONE);
            ((EditText)findViewById(R.id.edttowerheight)).setVisibility(View.GONE);
            ((EditText)findViewById(R.id.edtlatlonggps)).setVisibility(View.GONE);
            ((EditText)findViewById(R.id.edtlatlongsite)).setVisibility(View.GONE);
            ((EditText)findViewById(R.id.edtdistance)).setVisibility(View.GONE);

            ((TextView)findViewById(R.id.tvowner)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvvender2g)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvvender3g)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvvender4g)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvtowerheight)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvlatlonggps)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvlatlongsite)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tvdistance)).setVisibility(View.GONE);
            ((Button)findViewById(R.id.btnCapture1)).setVisibility(View.GONE);

            ((TextView)findViewById(R.id.txtedit)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.txtSitedetails)).setVisibility(View.VISIBLE);
            ((ImageView)findViewById(R.id.imgpicture)).setVisibility(View.VISIBLE);

        }else{
            ((Spinner)findViewById(R.id.edtowner)).setVisibility(View.VISIBLE);
            ((Spinner)findViewById(R.id.edtvender2g)).setVisibility(View.VISIBLE);
            ((Spinner)findViewById(R.id.edtvender3g)).setVisibility(View.VISIBLE);
            ((Spinner)findViewById(R.id.edtvender4g)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.edttowerheight)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.edtlatlonggps)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.edtlatlongsite)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.edtdistance)).setVisibility(View.VISIBLE);

            ((TextView)findViewById(R.id.tvowner)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvvender2g)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvvender3g)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvvender4g)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvtowerheight)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvlatlonggps)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvlatlongsite)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvdistance)).setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.btnCapture1)).setVisibility(View.VISIBLE);

            ((ImageView)findViewById(R.id.imgpicture)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.txtedit)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.txtSitedetails)).setVisibility(View.GONE);


        }
    }
    public void onValidation(String strvalidation){
        connector = new ConnectionDetector(getApplicationContext());
        TextView lblError = (TextView) findViewById(R.id.lblError);
        // boolean isConnected = isNetworkAvailable();
        if (connector.isConnectingToInternet()){
            lblError.setText("connected");
        }else{
            lblError.setText("Ouups!!! probleme de connection.");
            showAlertDialog(Activity_sitedetails.this, "No Internet Connection",
                    "Ouups!!! probleme de connection.", false);
            return;
        }

        if (blnCheckDataBeforeUpload() == true){
            //  getrfdata();
            SV_Pictures=strPersonalPhoto;
            strURLInsertRegistration=baseUrl + "InsertSitevisit";
            new ImageUploadTaskAsync().execute(SV_IdOperation,SV_SiteName, SV_Owner,SV_Vendor_2G,SV_Vendor_3G,SV_Vendor_4G,
                    SV_BuildingHweight, SV_Pictures ,SV_Device,SV_Status,SV_Latitude_GPS,SV_Longitude_GPS,SV_userid,SV_Flag);

        }else{
            //lblError.setText("Ouups!!! probleme de connection.");
            showAlertDialog(Activity_sitedetails.this, "Error occur",
                    "Please fill all fields !!!", false);
            return;
        }
    }
    public class  ImageUploadTaskAsync extends AsyncTask<String,String,String> {
        private ProgressDialog dialog = new ProgressDialog(Activity_sitedetails.this);
        HttpsURLConnection conn;
        URL url = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait while uploading...");
            Button btnUpload = (Button)findViewById(R.id.validateButton);
            btnUpload.setVisibility(View.GONE);
            dialog.setCancelable(false);
            dialog.show();
        }
        protected String doInBackground(String... params) {

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
                     .appendQueryParameter("SV_IdOperation", params[0])
                     .appendQueryParameter("SV_SiteName", params[1])
                     .appendQueryParameter("SV_Owner", params[2])
                     .appendQueryParameter("SV_Vendor_2G", params[3])
                     .appendQueryParameter("SV_Vendor_3G", params[4])
                     .appendQueryParameter("SV_Vendor_4G", params[5])
                     .appendQueryParameter("SV_BuildingHweight", params[6])
                     .appendQueryParameter("SV_Pictures", params[7])
                     .appendQueryParameter("SV_Device", params[8])
                     .appendQueryParameter("SV_Status", params[9])
                     .appendQueryParameter("SV_Latitude_GPS", params[10])
                     .appendQueryParameter("SV_Longitude_GPS", params[11])
                     .appendQueryParameter("SV_userid", params[12])
                     .appendQueryParameter("SV_Flag", params[13]);
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

             return "Error occur:"+e.getMessage();
        }
        finally {
             conn.disconnect();
        }

    //return(Response.toString());






/* http connexion
 try {
                url = new URL(strURLInsertRegistration);
                // return _libClass.parse(strURLInsertRegistration);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "Error Occur:"+ e.toString();
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                //conn.setDoOutput(true);
                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("SV_IdOperation", params[0])
                        .appendQueryParameter("SV_SiteName", params[1])
                        .appendQueryParameter("SV_Owner", params[2])
                        .appendQueryParameter("SV_Vendor_2G", params[3])
                        .appendQueryParameter("SV_Vendor_3G", params[4])
                        .appendQueryParameter("SV_Vendor_4G", params[5])
                        .appendQueryParameter("SV_BuildingHweight", params[6])
                        .appendQueryParameter("SV_Pictures", params[7])
                        .appendQueryParameter("SV_Device", params[8])
                        .appendQueryParameter("SV_Status", params[9])
                        .appendQueryParameter("SV_Latitude_GPS", params[10])
                        .appendQueryParameter("SV_Longitude_GPS", params[11])
                        .appendQueryParameter("SV_userid", params[12])
                        .appendQueryParameter("SV_Flag", params[13]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception "+e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
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

            } catch (IOException e) {
                e.printStackTrace();
                return "error occur:"+e.getMessage();
            } finally {
                conn.disconnect();
            }

* */

        }
        @Override
        protected void onPostExecute(String result) {
            // Toast.makeText(Activity_login.this, result, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.e("SmsReceiver",result);

           // Button btnUpload = (Button)findViewById(R.id.validateButton);
           // btnUpload.setVisibility(View.VISIBLE);

            if (result!=null && result.indexOf("uploaded")>=0){
                insertDetails();
                //delete de pictures on the phone
                //clean PersonalPhoto
                if (!strPersonalPhotoURL.equals("")) {
                    File file1 = new File(strPersonalPhotoURL);
                    file1.delete();
                    getContentResolver().delete(PersonalPhotoUri, null, null);
                }

                alertMsg(Activity_sitedetails.this,"", "Upload successfully.");
                // Toast.makeText(getApplicationContext(), "Upload successfully.",Toast.LENGTH_LONG).show();
                callForm(Activity_sitedetails.this,Activity_site_menu.class);
                finish();
                return;
            }else{
                showAlertDialog(Activity_sitedetails.this,"Site details",result.toString(),true);
            }
            TextView lblError = (TextView) findViewById(R.id.lblError);
            lblError.setText(result);
        }
    }
    public String  deviceId,getSubscriberId,Networkcode,NetLac;
    private void FillCellId() {
        final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
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
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void insertDetails(){
        try {
            String Sqlcmd="INSERT INTO tbl_SiteVisit (SV_IdOperation,SV_SiteName, SV_Owner,SV_Vendor_2G,SV_Vendor_3G,SV_Vendor_4G, SV_BuildingHweight,SV_Pictures ,SV_Device,SV_Status ,SV_Latitude,SV_Longitude,SV_Description)VALUES("
                    + "'" + SV_IdOperation + "'"
                    + ",'" + SV_SiteName + "'"
                    + ",'" + SV_Owner + "'"
                    + ",'" + SV_Vendor_2G + "'"
                    + ",'" + SV_Vendor_3G + "'"
                    + ",'" + SV_Vendor_4G + "'"
                    + ",'" + SV_BuildingHweight + "'"
                    + ",'" + SV_Pictures + "'"
                    + ",'" + SV_Device + "'"
                    + ",'" + SV_Status + "'"
                    + ",'" + SV_Latitude_GPS + "'"
                    + ",'" + SV_Longitude_GPS  + "'"
                    + ",'" + SV_Description  + "'"
                    + ");" ;

            db.open();
            db.setsitedetails(Sqlcmd);
            db.close();
        }catch (Exception e){
            db.close();
            e.printStackTrace();
        }
    }
    public void alertMsg(Context context,String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), Activity_site_menu.class);
                // intent.putExtra(EXTRA_MESSAGE, strMessage);
                startActivity(intent);
            }
        });

        /* builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
    public boolean blnCheckDataBeforeTakepicture() {
        try {
            strFinalXML = "";
            TextView lblError = (TextView) findViewById(R.id.lblError);
            lblError.setText("");
            String vtype = "";

            Spinner edtOwner = (Spinner) findViewById(R.id.edtowner);
            SV_Owner = edtOwner.getSelectedItem().toString();
            if (SV_Owner.equals("") || SV_Owner.indexOf("select")>=0) {
                Toast.makeText(this, "Please fill Owner field.", Toast.LENGTH_LONG).show();
                // edtOwner.setError("Please fill Owner field.");
                //lblError.setText("Please fill Owner field.");
                return false;
            }

            Spinner edtvender2 = (Spinner) findViewById(R.id.edtvender2g);
            SV_Vendor_2G = edtvender2.getSelectedItem().toString();
            if (SV_Vendor_2G.equals("")|| SV_Vendor_2G.indexOf("select")>=0) {
                Toast.makeText(this, "Please fill Vender 2G field.", Toast.LENGTH_LONG).show();
                //edtvender2.setError("Please fill this field.");
                //lblError.setText("Please fill Owner field.");
                return false;
            }

            Spinner edtvender3 = (Spinner) findViewById(R.id.edtvender3g);
            SV_Vendor_3G = edtvender3.getSelectedItem().toString();
            if (SV_Vendor_3G.equals("")|| SV_Vendor_3G.indexOf("select")>=0) {
                Toast.makeText(this, "Please fill Vender 3G field.", Toast.LENGTH_LONG).show();
                // edtvender3.setError("Please fill this field.");
                return false;
            }

            Spinner edtvender4 = (Spinner) findViewById(R.id.edtvender4g);
            SV_Vendor_4G = edtvender4.getSelectedItem().toString();
            if (SV_Vendor_4G.equals("") || SV_Vendor_4G.indexOf("select")>=0) {
                Toast.makeText(this, "Please fill Vender 4G field.", Toast.LENGTH_LONG).show();
                //  edtvender4.setError("Please fill this field.");
                return false;
            }

            EditText edtlat = (EditText) findViewById(R.id.edtlatlonggps);
            SV_Latitude_GPS = edtlat.getText().toString();
            if (SV_Latitude_GPS.equals("")) {
                Toast.makeText(this, "Please fill this field.", Toast.LENGTH_LONG).show();
                edtlat.setError("Please fill this field.");
                return false;
            }

            EditText edtlong = (EditText) findViewById(R.id.edtlatlonggps);
            SV_Longitude_GPS = edtlong.getText().toString();
            if (SV_Longitude_GPS.equals("")) {
                Toast.makeText(this, "Please fill this field.", Toast.LENGTH_LONG).show();
                edtlong.setError("Please fill this field.");
                return false;
            }

            EditText edttowerheight = (EditText) findViewById(R.id.edttowerheight);
            SV_BuildingHweight = edttowerheight.getText().toString();
            if (SV_BuildingHweight.equals("")) {
                Toast.makeText(this, "Please fill this field.", Toast.LENGTH_LONG).show();
                edttowerheight.setError("Please fill this field.");
                return false;
            }


            SV_Pictures = strPersonalPhoto;
            SV_userid = getUserId();
            SV_Status = "100";
            SV_Description="Site details";
            SV_IdOperation=getSessionId();
            SV_SiteName=getSitename();

            return true;
        }catch (Exception e){
            return false;
        }
    }
    public boolean blnCheckDataBeforeUpload() {
        try {
            strFinalXML = "";
            TextView lblError = (TextView) findViewById(R.id.lblError);
            lblError.setText("");

            if (!blnCheckDataBeforeTakepicture()){
               return false;
            }

            TextView txtFlagImage = (TextView) findViewById(R.id.photoflag);
            String _FlagImage = txtFlagImage.getText().toString();
            if (!_FlagImage.equals("1")) {
                // Toast.makeText(this, "Veuillez prendre la photo avant.", Toast.LENGTH_LONG).show();
                lblError.setText("Please take a picture before upload!");

                showAlertDialog(Activity_sitedetails.this, "Photo",
                        "Please take a picture before upload!", false);
                return false;
            }

            if (strPersonalPhoto.equals("")) {
                showAlertDialog(Activity_sitedetails.this, "Photo",
                        "Please take a picture before upload!", false);
                return false;
            }
            //SV_Flag=uploaderBtn.getText().toString();

            SV_Pictures = strPersonalPhoto;
            SV_userid = getUserId();
            SV_Status = "100";

            //get siteconfig
           /* db.open();
            Cursor cursor  =db.getsitedetails("SELECT DISTINCT sitename,idoperation from tbl_siteconfiguration ");
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                SV_IdOperation = cursor.getString(1);
                SV_SiteName =cursor.getString(0);
            }

            db.close();*/

            SV_IdOperation=getSessionId();
            SV_SiteName=getSitename();

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
    protected  void onActivityResult(int requestCode , int resultCode, Intent data){
        super.onActivityResult(resultCode,resultCode,data);
        String picturePath="";
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            android.database.Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
              picturePath = cursor.getString(columnIndex);
            cursor.close();
            decodeFile(picturePath, REQUIRED_SIZE);

        }

        if (requestCode == CAMERA_PIC_REQUEST_PersonalPhoto) {
            if (resultCode == Activity.RESULT_OK) {
                try {

                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), PersonalPhotoUri);
                    strPersonalPhotoURL = getRealPathFromURI(PersonalPhotoUri);
                    decodeFile(strPersonalPhotoURL, REQUIRED_SIZE);

                    //if (intNbrOfFaces == 1) {
                    intNbrRequestedFace = 1;
                    mPersonalPhoto.setVisibility(View.VISIBLE);

                    //* rotation pictures
                    String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                    Cursor cur = getContentResolver().query(PersonalPhotoUri, orientationColumn, null, null, null);
                    orientation = -1;
                    if (cur != null && cur.moveToFirst()) {
                        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                    }


                    ExifInterface exifReader = new ExifInterface(strPersonalPhotoURL);
                    int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

                    bitmap=libClass.rotateBitmap(bitmap,orientation);
                    mPersonalPhoto.setImageBitmap(bitmap);
                    thumbnail = bitmap;

                    ImageView img;
                    Bitmap bmp;

                    // //  bmp=BitmapFactory.decodeResource(getResources(),R.id.camera_personal_image);//image is your image

                    //    bmp=Bitmap.createScaledBitmap(bitmap, imgwidth,imgheight, true);
                    //   mPersonalPhoto.setImageBitmap(bmp);

                    //3
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    //3.5 prepare the image to be uploaded
                    byte[] bytedata = bytes.toByteArray();
                    strPersonalPhoto = base64.encodeBytes(bytedata);
                    photoflag.setText("1");

                   // Button btn = (Button)findViewById(R.id.validateButton);
                   // btn.setVisibility(View.VISIBLE);

                    onValidation("insert");
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }
    public void decodeFile(String filePath, int pictsize) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        // final int REQUIRED_SIZE = 512;
        //final int REQUIRED_SIZE = 1024;
        //final int REQUIRED_SIZE = 2048;

        final int REQUIRED_SIZE = pictsize;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp <= REQUIRED_SIZE || height_tmp <= REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        //image.setImageBitmap(bitmap);
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        android.database.Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public double latitude,longitude;
    private void requestPermission() {

        ActivityCompat.requestPermissions(Activity_sitedetails.this, new String[]
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

                            Toast.makeText(Activity_sitedetails.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        } else {
                            //  Toast.makeText(Activity_deviceposition.this,"Permission Denied" +grantResults[2],Toast.LENGTH_LONG).show();

                        }
                    }

                    break;

            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Activity_sitedetails.this,"Permission Denied " +e.getMessage(),Toast.LENGTH_LONG).show();
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
    String flagphoto="0";
    public void setPictures(int pictureNbr){
        //set pictures
        if (blnCheckDataBeforeTakepicture()){
            CapturePersonalPhoto();
        }

        //  Toast.makeText(Activity_deviceposition.this, "click on "+pictureNbr, Toast.LENGTH_LONG).show();
    }
    public void CapturePersonalPhoto(){
        try{
            Button btn = (Button) findViewById(R.id.validateButton);
            btn.setVisibility(View.GONE);

            mPersonalPhoto = (ImageView) findViewById(R.id.imgpicture);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

            PersonalPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, PersonalPhotoUri);
            startActivityForResult(intent, CAMERA_PIC_REQUEST_PersonalPhoto);

            //TextView txtview= (TextView) findViewById(R.id.photoflag);
            //String txtp=txtview.getText().toString();

            /// if (txtp.equals("1")){

            //  uploaderBtn.setVisibility(View.VISIBLE);
            // }




        } catch (Exception e){
            e.printStackTrace();
        }
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
                callForm(Activity_sitedetails.this,MainActivity.class);
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


    public  String SV_IdOperation,SV_SiteName, SV_SiteConfig, SV_DateTime,SV_Coordinates,SV_AuditedBy,SV_Rigger,SV_Owner,SV_Flag,
            SV_Vendor_2G,SV_Vendor_3G,SV_Vendor_4G,SV_Vendor_5G,SV_Vendor,SV_RooftopTower,SV_TowerMastHeight,SV_BuildingHweight,SV_TotNumAntennas,SV_AntennaModel,SV_Remarks,SV_Tech,
            SV_Sector,SV_Description,SV_Plan,SV_Actual,SV_Pictures,SV_userid,SV_Device,SV_Latitude_GPS,SV_Longitude_GPS;

    //end class






}
