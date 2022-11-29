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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static  android.Manifest.permission.ACCESS_NETWORK_STATE;
import static  android.Manifest.permission.ACCESS_FINE_LOCATION;
import static  android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getDevice;
import static guru.pmvungu.example.com.includes.apiUrl.getRoleId;
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
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import guru.pmvungu.example.com.includes.ConnectionDetector;
import  guru.pmvungu.example.com.includes.base64;
//public class Activity_panoramic extends AppCompatActivity  {

public class Activity_panoramic extends AppCompatActivity implements View.OnClickListener {
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
    Button getLocationBtn,uploaderBtn,getPicture1,ButtonAntenna,ButtonPanora;
    Spinner v_spinnerphototype,v_spinnertech,v_spinnersector,v_spinnernomphoto;

    int pictureNbr;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;
    String[] strSite = "Select;".split(";");
    ConnectionDetector connector;
    libClass _libClass ;
    public TextView photoflag,vsiteflag,tvOpenCamera;
    Button btnUpload ;

    final DBAdapter db = new DBAdapter(Activity_panoramic.this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panoramic);
        _libClass = new libClass(Activity_panoramic.this);
        device = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //tvLocation = (TextView)findViewById(R.id.lblLocation);
        photoflag = (TextView)findViewById(R.id.photoflag);
        vsiteflag  = (TextView)findViewById(R.id.siteflag);

        strPersonalPhoto = "";
        intNbrRequestedFace = 0;
        strPersonalPhotoURL = "";
        SV_Status="300";
        // sEnableDisable("0");
        photoflag.setText("0");
        vsiteflag.setText("0");
        SV_userid= getUserId();
        SV_SiteName=getSitename();

         try {
            if(checkPermission()){
                //  Toast.makeText(Activity_panoramic.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                requestPermission();
            }
            getconfig();
            SetInitialize();

        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void getconfig(){
        try{
            db.open();
          Cursor curSiteDetails = db.getsitedetails("SELECT distinct SV_IdOperation,SV_SiteName,SV_UserId,SV_Device from tbl_SiteVisit where SV_IdOperation='"+getSessionId()+"' and SV_Status='100';");
          if (curSiteDetails.getCount() >0){
              curSiteDetails.moveToFirst();
              SV_IdOperation=curSiteDetails.getString(0);
              SV_SiteName=curSiteDetails.getString(1);
             // SV_userid=curSiteDetails.getString(2);
              SV_Device=curSiteDetails.getString(3);
          }else{
              SV_IdOperation=getSessionId();
              SV_SiteName=getSitename();
              SV_userid =getUserId();
              SV_Device=getDevice();
             // String msg="Please fill site details, first !!! ";
              //alertNotification(Activity_panoramic.this,"Panoramic view :",msg.toString(),Activity_site_menu.class);


          }



              curSiteDetails = db.getsitedetails("SELECT DISTINCT SV_Pictures,SV_Description from tbl_SiteVisit where SV_Status='300' and SV_IdOperation='"+getSessionId()+"' and SV_SiteName ='"+getSitename() +"'  order by SV_Description,id asc;");

            if (curSiteDetails.moveToFirst())
            {

                do {

                    SV_Pictures=curSiteDetails.getString(0);
                    SV_Description=curSiteDetails.getString(1);

                    String base=SV_Pictures;
                    TextView tv;
                    int blaokColor= Color.parseColor("#331f00");

                    if (SV_Description.startsWith("0")){
                        image = (ImageView) findViewById(R.id.iv0);
                        tv = (TextView) findViewById(R.id.tv0);
                        tv.setTextColor(blaokColor);
                    }else   if (SV_Description.startsWith("300")){
                        image = (ImageView) findViewById(R.id.iv300);
                        tv = (TextView) findViewById(R.id.tv300);
                        tv.setTextColor(blaokColor);
                    }else   if (SV_Description.startsWith("60")){
                        image = (ImageView) findViewById(R.id.iv60);
                        tv = (TextView) findViewById(R.id.tv60);
                        tv.setTextColor(blaokColor);
                    }else   if (SV_Description.startsWith("90")){
                        image = (ImageView) findViewById(R.id.iv90);
                        tv = (TextView) findViewById(R.id.tv90);
                        tv.setTextColor(blaokColor);
                    }else   if (SV_Description.startsWith("120")){
                        image = (ImageView) findViewById(R.id.iv120);
                        tv = (TextView) findViewById(R.id.tv120);
                        tv.setTextColor(blaokColor);
                    }
                    else   if (SV_Description.startsWith("150")){
                        image = (ImageView) findViewById(R.id.iv150);
                        tv = (TextView) findViewById(R.id.tv150);
                        tv.setTextColor(blaokColor);
                    }else   if (SV_Description.startsWith("180")){
                        image = (ImageView) findViewById(R.id.iv180);
                        tv = (TextView) findViewById(R.id.tv180);
                        tv.setTextColor(blaokColor);
                    }
                    else   if (SV_Description.startsWith("210")){
                        image = (ImageView) findViewById(R.id.iv210);
                        tv = (TextView) findViewById(R.id.tv210);
                        tv.setTextColor(blaokColor);
                    }else   if (SV_Description.startsWith("240")){
                        image = (ImageView) findViewById(R.id.iv240);
                        tv = (TextView) findViewById(R.id.tv240);
                        tv.setTextColor(blaokColor);
                    }else   if (SV_Description.startsWith("270")){
                        image = (ImageView) findViewById(R.id.iv270);
                        tv = (TextView) findViewById(R.id.tv270);
                        tv.setTextColor(blaokColor);

                    }else   if (SV_Description.startsWith("30")){
                        image = (ImageView) findViewById(R.id.iv30);
                        tv = (TextView) findViewById(R.id.tv30);
                        tv.setTextColor(blaokColor);
                    }
                    else   if (SV_Description.startsWith("330")){
                        image = (ImageView) findViewById(R.id.iv330);
                        tv = (TextView) findViewById(R.id.tv330);
                        tv.setTextColor(blaokColor);
                    }else   if (SV_Description.startsWith("360")){
                        image = (ImageView) findViewById(R.id.iv360);
                        tv = (TextView) findViewById(R.id.tv360);
                        tv.setTextColor(blaokColor);
                    }

                    byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
                    Bitmap bmp;
                    bmp= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                   // image.setAdjustViewBounds(true);
                    image.setImageBitmap(bmp);

                } while (curSiteDetails.moveToNext());
            }

        db.close();

        }catch (Exception e){
            e.printStackTrace();
            alertNotification(Activity_panoramic.this,"Panoramic view :",e.toString(),Activity_site_menu.class);
        }
}
    public ImageView image;
    public void addEmptyItemsOnSpinner(Spinner spinnerAdapter,String itemSelected, int ArrayRes) {
        try
        {
            List<String> list = new ArrayList<String>();
            list.add(itemSelected);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAdapter.setAdapter(dataAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void addItemsOnSpinner2(Spinner spinnerAdapter,String itemSelected, int ArrayRes) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, ArrayRes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(adapter);

    }
    public void addListenerOnSpinnerItemSelection() {



      /*  v_spinnerphototype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Spinner txtSelected = (Spinner) findViewById(R.id.txtForfatid);
                String item =parent.getItemAtPosition(position).toString();
                int p =item.toLowerCase().indexOf("antenna photo");

                 if (item.toLowerCase().indexOf("panoramic")>=0) {
                    SV_Status="300";
                    v_spinnertech.setVisibility(View.GONE);
                    v_spinnersector.setVisibility(View.GONE);
                    v_spinnernomphoto.setVisibility(View.VISIBLE);

                    addItemsOnSpinner2(v_spinnernomphoto,"Panoramic degre",R.array.cbopanorama);
                    addEmptyItemsOnSpinner(v_spinnersector,"",0);
                    addEmptyItemsOnSpinner(v_spinnertech,"",0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        v_spinnertech.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Spinner txtSelected = (Spinner) findViewById(R.id.txtForfatid);
                String item =parent.getItemAtPosition(position).toString();

                if (item.toLowerCase().indexOf("2g")>=0) {
                    addItemsOnSpinner2(v_spinnersector,"sector",R.array.cbosector);
                    addItemsOnSpinner2(v_spinnernomphoto,"photo name",R.array.cbodescriptionphoto);

                }else  if (item.toLowerCase().indexOf("3g")>=0) {

                    addItemsOnSpinner2(v_spinnersector,"sector",R.array.cbosector3G);
                    addItemsOnSpinner2(v_spinnernomphoto,"photo name",R.array.cbodescriptionphoto);
                } else  if (item.toLowerCase().indexOf("4g")>=0) {
                    addItemsOnSpinner2(v_spinnernomphoto,"photo name",R.array.cbodescriptionphoto);
                    addItemsOnSpinner2(v_spinnersector,"sector",R.array.cbosector4G);
                }else{
                    addEmptyItemsOnSpinner(v_spinnersector,"select item sector",R.array.cbosectorautre);
                    addEmptyItemsOnSpinner(v_spinnernomphoto,"photo name",R.array.cbodescriptionphoto);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    */

    }
    public void onValidation(String strvalidation){
        connector = new ConnectionDetector(getApplicationContext());

        //TextView lblError = (TextView) findViewById(R.id.lblError);
        // boolean isConnected = isNetworkAvailable();

        if (connector.isConnectingToInternet()){
            //lblError.setText("connected");
        }else{
            //lblError.setText("Ouups!!! probleme de connection.");
            showAlertDialog(Activity_panoramic.this, "No Internet Connection",
                    "Ouups!!! probleme de connection.", false);
            return;
        }


        if (blnCheckDataBeforeUpload() == true){

            //  getrfdata();
             SV_Pictures=strPersonalPhoto;
             strURLInsertRegistration=baseUrl + "InsertSitevisit";
            new ImageUploadTaskAsync().execute(SV_IdOperation,SV_SiteName, SV_Description,SV_Pictures,SV_userid,SV_Device,SV_Status);

        }

    }
    public class  ImageUploadTaskAsync extends AsyncTask<String,String,String> {
        private ProgressDialog dialog = new ProgressDialog(Activity_panoramic.this);
        HttpsURLConnection conn;
        URL url = null;
        SSLContext sslcontext;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait while uploading...");

            dialog.setCancelable(false);
            dialog.show();
        }

        protected String doInBackground(String... params) {
            try {
                url = new URL(strURLInsertRegistration);
                // return _libClass.parse(strURLInsertRegistration);
                sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null,new TrustManager[]{ new X509ExtendedTrustManager(){
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

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception doInbackground";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
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

                // setDoInput and setDoOutput method depict handling of both send and receive
                //conn.setDoInput(true);
                //conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("SV_IdOperation", params[0])
                        .appendQueryParameter("SV_SiteName", params[1])
                        .appendQueryParameter("SV_Description", params[2])
                        .appendQueryParameter("SV_Pictures", params[3])
                        .appendQueryParameter("SV_userid", params[4])
                        .appendQueryParameter("SV_Device", params[5])
                        .appendQueryParameter("SV_Status", params[6]);
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

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception try post"+e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpsURLConnection.HTTP_OK) {
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
                    return("Error occur: operation failed !!");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception response";
            } finally {
                conn.disconnect();
            }


        }
        @Override
        protected void onPostExecute(String result) {
            // Toast.makeText(Activity_login.this, result, Toast.LENGTH_LONG).show();
            dialog.dismiss();
           // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.e("SmsReceiver",result);

            if (result!=null && result.indexOf("uploaded")>=0){

                //delete de pictures on the phone
                //clean PersonalPhoto
                if (!strPersonalPhotoURL.equals("")) {
                    File file1 = new File(strPersonalPhotoURL);
                    file1.delete();
                    getContentResolver().delete(PersonalPhotoUri, null, null);
                }

                // Toast.makeText(getApplicationContext(), "Upload successfully.",Toast.LENGTH_LONG).show();
               try{
                   db.open();
                   String sqlcmd="INSERT INTO tbl_SiteVisit (SV_IdOperation,SV_SiteName,SV_Description,SV_Pictures,SV_UserId,SV_Device,SV_Status) values ('"+SV_IdOperation+"','"+SV_SiteName+"', '"+SV_Description+"','"+SV_Pictures+"','"+SV_userid+"','"+SV_Device+"','"+SV_Status+"')";
                   db.setsitedetails(sqlcmd);
                   db.close();


                   ((TextView)findViewById(intPanoramic)).setTextColor(Color.parseColor("#331f00"));
                   // mPersonalPhoto=(ImageView)findViewById(ivId) ;
                   //  mPersonalPhoto.setImageBitmap(bitmap);
                   SV_Description=Integer.toString(Integer.parseInt(SV_Description.replace(" degre","").trim()) + 30)+" degre";
                   Integer intPanoramic=Integer.parseInt(SV_Description.replace(" degre","").trim());
                   if(intPanoramic < 360){
                       NextPanoramic(Activity_panoramic.this,"Panoramic view ","Upload successfully.\nWould you like to take the Next Panoramic "+SV_Description +" ?",Activity_panoramic.class,SV_Description);
                       // finish();
                   }else{
                       callForm(Activity_panoramic.this,Activity_panoramic.class);
                   }


                   return;

               }catch (Exception e){
                   db.close();
                   alertNotification(Activity_panoramic.this,"Error orcur : ", e.getMessage(),Activity_panoramic.class);
                   return;
               }


            }

           // TextView lblError = (TextView) findViewById(R.id.lblError);
            //lblError.setText(result);

        }
    }
    public void alertMsg(Context context,String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // intent.putExtra(EXTRA_MESSAGE, strMessage);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });
 /*
       builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener()     {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });*/
        AlertDialog alert = builder.create();
        alert.show();
        return;
    }
    public void NextPanoramic(Context context, String title, String msg, final Class activity,String Description){


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        strPanoramic=Description;
        builder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pictureNbr = 1;
                setPictures(pictureNbr);
            }
        });

       /*builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //callForm(Activity_panoramic.this,activity);
            }
        });
  */
         builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener()     {
            public void onClick(DialogInterface dialog, int id) {

               callForm(Activity_panoramic.this,activity);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return;
    }
    public void alertNotification(Context context, String title, String msg, final Class activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callForm(Activity_panoramic.this,activity);

            }
        });

      /* builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //callForm(Activity_panoramic.this,activity);
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
    /*
 public  class ImageUploadTaskAsync extends AsyncTask<Void, Void, String> {
     private String webAddressToPost = strURLInsertRegistration;

     private ProgressDialog dialog = new ProgressDialog(Activity_panoramic.this);

     //  HttpURLConnection conn;
     //  URL url = null;

     @Override
     protected void onPreExecute() {
         super.onPreExecute();


         //uploaderBtn.setVisibility(View.GONE);

         dialog.setMessage("Please wait while uploading...");
         dialog.setCancelable(true);
         dialog.show();
     }

     protected String doInBackground(Void... params) {
         String strResponse = null;
         webAddressToPost = strURLInsertRegistration;
         try {
             String xmlString = strFinalXML;
             DefaultHttpClient httpClient = new DefaultHttpClient();
             HttpPost httppost = new HttpPost(webAddressToPost);
             httppost.addHeader("Accept", "text/xml");
             httppost.addHeader("Content-Type", "application/xml");
             try {
                 StringEntity entity = new StringEntity(xmlString, "UTF-8");
                 entity.setContentType("text/xml");
                 httppost.setEntity(entity);
                 HttpResponse response = httpClient.execute(httppost);
                 BasicResponseHandler responseHandler = new BasicResponseHandler();

                 if (response != null) {
                     try {
                         strResponse = responseHandler.handleResponse(response);
                     } catch (HttpResponseException e) {
                         e.printStackTrace();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }

             } catch (Exception ex) {
                 ex.printStackTrace();
             }

         } catch (Exception e) {
             // something went wrong. connection with the server error
         }
         return strResponse;


     }

     @Override
     protected void onPostExecute(String result) {
         dialog.dismiss();
         if (result != null) {

             if (result.equals("<Reply><MESSAGE>Successful</MESSAGE></Reply>")) {

                 TextView lblError = (TextView) findViewById(R.id.lblError);
                 Toast.makeText(getApplicationContext(), "Registered successfully.", Toast.LENGTH_LONG).show();
                 lblError.setText("Registered successfully.");


                 String strMessage = "";
                 // Intent intent = new Intent(getApplicationContext(), secondactivity.class);
                 // intent.putExtra(EXTRA_MESSAGE, strMessage);
                 // startActivity(intent);
                 //clean PersonalPhoto
                 if (!strPersonalPhotoURL.equals("")) {
                     File file1 = new File(strPersonalPhotoURL);
                     file1.delete();
                     getContentResolver().delete(PersonalPhotoUri, null, null);
                 }


             } else {
                 TextView lblError = (TextView) findViewById(R.id.lblError);
                 Toast.makeText(getApplicationContext(), "Registration failed.", Toast.LENGTH_LONG).show();
                 lblError.setText("Registration failed. Contact administrator.");
             }
         }else{

             TextView lblError = (TextView) findViewById(R.id.lblError);
             Toast.makeText(getApplicationContext(), "Upload failed. result null.", Toast.LENGTH_LONG).show();
             lblError.setText("Upload failed. result null.");
         }



      }

    }

*/


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
        strFinalXML = "";
       // TextView lblError = (TextView) findViewById(R.id.lblError);
        //lblError.setText("");

        SV_Description = strPanoramic;
        if (SV_Description.equals("")){
            showAlertDialog(Activity_panoramic.this, "Photo",
                    "Please take a picture before upload!", false);
            return false;
        }


        TextView txtFlagImage = (TextView)findViewById(R.id.photoflag);
        String _FlagImage = txtFlagImage.getText().toString();
        if (!_FlagImage.equals("1")){
            // Toast.makeText(this, "Veuillez prendre la photo avant.", Toast.LENGTH_LONG).show();

            showAlertDialog(Activity_panoramic.this, "Photo",
                    "Please take a picture before upload!", false);
            return false;
        }

        if ( strPersonalPhoto.equals("")){
            showAlertDialog(Activity_panoramic.this, "Photo",
                    "Please take a picture before upload!", false);
            return  false;
        }

        SV_Pictures=strPersonalPhoto;
        SV_SiteName=getSitename();
        SV_IdOperation=getSessionId();
        SV_userid=getUserId();
        return true;

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

                    // active to set ficture
                    //  mPersonalPhoto.setImageBitmap(bitmap);

                     // Bitmap bmp;
                    // //  bmp=BitmapFactory.decodeResource(getResources(),R.id.camera_personal_image);//image is your image
                    //    bmp=Bitmap.createScaledBitmap(bitmap, imgwidth,imgheight, true);
                    //   mPersonalPhoto.setImageBitmap(bmp);

                    //3

                    ExifInterface exifReader = new ExifInterface(strPersonalPhotoURL);
                    int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                    int rotate = 0;
                    bitmap=libClass.rotateBitmap(bitmap,orientation);

                    thumbnail = bitmap;

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    //3.5 prepare the image to be uploaded
                    byte[] bytedata = bytes.toByteArray();
                    strPersonalPhoto = base64.encodeBytes(bytedata);
                    photoflag.setText("1");

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

        ActivityCompat.requestPermissions(Activity_panoramic.this, new String[]
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

                            Toast.makeText(Activity_panoramic.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        } else {
                            //  Toast.makeText(Activity_deviceposition.this,"Permission Denied" +grantResults[2],Toast.LENGTH_LONG).show();

                        }
                    }

                    break;

            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Activity_panoramic.this,"Permission Denied " +e.getMessage(),Toast.LENGTH_LONG).show();
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
        CapturePersonalPhoto();
        //  Toast.makeText(Activity_deviceposition.this, "click on "+pictureNbr, Toast.LENGTH_LONG).show();

    }
    public void CapturePersonalPhoto(){
        try{

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            PersonalPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, PersonalPhotoUri);
            startActivityForResult(intent, CAMERA_PIC_REQUEST_PersonalPhoto);

        } catch (Exception e){
            e.printStackTrace();
            showAlertDialog(Activity_panoramic.this,"Capture fail",e.getMessage(),false);
        }
    }
    public void callForm(Context context,Class activity){
        Intent intent = new Intent(context,activity);
        startActivity(intent);
        this.finish();
    }
    @Override
    public void onClick(View v) {
        try {
            TextView tv;
            ImageView iv;
            strPanoramic="";

            int tvId = v.getId();

            switch(v.getId()) {
                case R.id.iv0:
                    strPanoramic=tv0.getText().toString();
                    intPanoramic=tv0.getId();
                    ivId=iv0.getId();
                    Log.e("image clicked : ",ivId + " " +intPanoramic);
                    break;
                case R.id.iv30:
                    strPanoramic=tv30.getText().toString();
                    intPanoramic=tv30.getId();
                    break;
                case R.id.iv60:
                    strPanoramic=tv60.getText().toString();
                    intPanoramic=tv60.getId();
                    break;
                case R.id.iv90:
                    strPanoramic=tv90.getText().toString();
                    intPanoramic=tv90.getId();
                    break;
                case R.id.iv120:
                    strPanoramic=tv120.getText().toString();
                    intPanoramic=tv120.getId();
                    break;
                case R.id.iv150:
                    strPanoramic=tv150.getText().toString();
                    intPanoramic=tv150.getId();
                    break;
                case R.id.iv180:
                    strPanoramic=tv180.getText().toString();
                    intPanoramic=tv180.getId();
                    break;
                case R.id.iv210:
                    strPanoramic=tv210.getText().toString();
                    intPanoramic=tv210.getId();
                    break;
                case R.id.iv240:
                    strPanoramic=tv240.getText().toString();
                    intPanoramic=tv240.getId();
                    break;
                case R.id.iv270:
                    strPanoramic=tv270.getText().toString();
                    intPanoramic=tv270.getId();
                    break;
                case R.id.iv300:
                    strPanoramic=tv300.getText().toString();
                    intPanoramic=tv300.getId();
                    break;
                case R.id.iv330:
                    strPanoramic=tv330.getText().toString();
                    intPanoramic=tv330.getId();
                    break;

                case R.id.iv360:
                    strPanoramic=tv360.getText().toString();
                    intPanoramic=tv360.getId();
                    Log.e("image clicked : ",String.valueOf(R.id.iv360) +intPanoramic);
                    break;
                default :
                    try{
                        tv = (TextView)v;
                        strPanoramic = tv.getText().toString();
                        intPanoramic=v.getId();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error occur:"+e.toString()+"\n session failed.." , Toast.LENGTH_LONG).show();
                        return;
                    }

                   // mPersonalPhoto=(ImageView)findViewById(intPanoramic);


            }

            pictureNbr = 1;
            setPictures(pictureNbr);

           // popupTakepictures(Activity_panoramic.this,"Pictures","Would you like to take a new picture \n"+strPanoramic +"?");



        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void SetInitialize(){
         tv0 =(TextView)findViewById(R.id.tv0);
         iv0 =(ImageView)findViewById(R.id.iv0);
         tv30 =(TextView)findViewById(R.id.tv30);
         iv30 =(ImageView)findViewById(R.id.iv30);
         tv60 =(TextView)findViewById(R.id.tv60);
         iv60 =(ImageView)findViewById(R.id.iv60);
         tv90 =(TextView)findViewById(R.id.tv90);
         iv90 =(ImageView)findViewById(R.id.iv90);
         tv120 =(TextView)findViewById(R.id.tv120);
         iv120 =(ImageView)findViewById(R.id.iv120);
         tv150 =(TextView)findViewById(R.id.tv150);
         iv150 =(ImageView)findViewById(R.id.iv150);
         tv180 =(TextView)findViewById(R.id.tv180);
         iv180 =(ImageView)findViewById(R.id.iv180);
         tv210 =(TextView)findViewById(R.id.tv210);
         iv210 =(ImageView)findViewById(R.id.iv210);
         tv240 =(TextView)findViewById(R.id.tv240);
         iv240 =(ImageView)findViewById(R.id.iv240);

         tv270 =(TextView)findViewById(R.id.tv270);
         iv270 =(ImageView)findViewById(R.id.iv270);
         tv300 =(TextView)findViewById(R.id.tv300);
         iv300 =(ImageView)findViewById(R.id.iv300);

         tv330 =(TextView)findViewById(R.id.tv330);
         iv330 =(ImageView)findViewById(R.id.iv330);
         tv360 =(TextView)findViewById(R.id.tv360);
         iv360 =(ImageView)findViewById(R.id.iv360);

         switch (getRoleId()){

             case "2": case "0":
       // if (!getRoleId().equals("1")) {
            tv0.setOnClickListener(this);
            tv30.setOnClickListener(this);
            tv60.setOnClickListener(this);
            tv90.setOnClickListener(this);
            tv120.setOnClickListener(this);
            tv150.setOnClickListener(this);
            tv180.setOnClickListener(this);
            tv210.setOnClickListener(this);
            tv240.setOnClickListener(this);
            tv270.setOnClickListener(this);
            tv300.setOnClickListener(this);
            tv330.setOnClickListener(this);
            tv360.setOnClickListener(this);

            iv0.setOnClickListener(this);
            iv30.setOnClickListener(this);
            iv60.setOnClickListener(this);
            iv90.setOnClickListener(this);
            iv120.setOnClickListener(this);
            iv150.setOnClickListener(this);
            iv180.setOnClickListener(this);
            iv210.setOnClickListener(this);
            iv240.setOnClickListener(this);
            iv270.setOnClickListener(this);
            iv300.setOnClickListener(this);
            iv330.setOnClickListener(this);
            iv360.setOnClickListener(this);
            break;
             case "1":
            default:
        }
    }
    public void popupTakepictures(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pictureNbr = 1;
                strPanoramic=SV_Description;
                setPictures(pictureNbr);
            }
        });

        builder.setNegativeButton("NON", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "operation canceled !!!", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }



    TextView  tv0,tv30,tv60,tv90,tv120,tv150,tv180,tv210,tv240,tv270,tv300,tv330,tv360;
    ImageView iv0,iv30,iv60,iv90,iv120,iv150,iv180,iv210,iv240,iv270,iv300,iv330,iv360;
    String strPanoramic;
    int intPanoramic,ivId;
    public  String SV_IdOperation,SV_SiteName, SV_SiteConfig, SV_DateTime,SV_Coordinates,SV_AuditedBy,SV_Rigger,SV_Owner,
            SV_Vendor,SV_RooftopTower,SV_TowerMastHeight,SV_BuildingHweight,SV_TotNumAntennas,SV_AntennaModel,SV_Remarks,SV_Tech,
            SV_Sector,SV_Description,SV_Plan,SV_Actual,SV_Pictures,SV_userid,SV_Device;

    //end class






}
