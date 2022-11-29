package guru.pmvungu.example.com.guru;

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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.SSLContext;

import guru.pmvungu.example.com.includes.ConnectionDetector;
import guru.pmvungu.example.com.includes.apiUrl;
import guru.pmvungu.example.com.includes.base64;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getAntenne;
import static guru.pmvungu.example.com.includes.apiUrl.getDevice;
import static guru.pmvungu.example.com.includes.apiUrl.getSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.getSitename;
import static guru.pmvungu.example.com.includes.apiUrl.getUserId;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public class Activity_comment extends AppCompatActivity {
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

    int pictureNbr;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;
    String[] strSite = "Select;".split(";");
    ConnectionDetector connector;


    final DBAdapter db = new DBAdapter(Activity_comment.this);
    libClass  _libClass = new libClass(Activity_comment.this);

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        SV_Status="500";
        uploaderBtn = (Button)findViewById(R.id.validateButton);
        SV_userid= getUserId();
        SV_SiteName=getSitename();
        SV_IdOperation=getSessionId();

        try {
            if(checkPermission()){
                //  Toast.makeText(Activity_antenna.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                requestPermission();
            }

            addListenerOnSpinnerItemSelection();


         //progressbar
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            textView = (TextView) findViewById(R.id.textView);

            SV_Device= getDevice();
            progressStatus = 0;
            getPourcentActivity();

            progressBar.setProgress(progressStatus);
            textView.setText(progressStatus * 100 / progressBar.getMax() + "%");
          //  textView.setText(progressStatus * 100 / progressBar.getMax() + "%");

            if (progressStatus <= 86){
                uploaderBtn.setEnabled(false);
            }

        } catch (Exception e){
            e.printStackTrace();
            //showAlertDialog(Activity_comment.this, "Error occur :", e.getMessage(), false);

            alertNotification(Activity_comment.this,"Error occur :",e.getMessage(),Activity_site_menu.class,"","",false);

        }

   }




    public void getPourcentActivity(){

        //total panoramic =300*12 =3600, sitedetail=100, antenna=7 * 200 =1400
        Integer totgen,tpanoramic,tantenna,tport,tsitedetail=0;
        Integer panoramic,antenna,port,nant,sitedetail  ;
        String _status="0";
        try {
            port=1;nant=1;
            panoramic=0;antenna=0; sitedetail=0;

            tpanoramic=12;tsitedetail=1; tantenna=11;// 11 rubtriques

            db.open();


            String sqlcmd= " select sitename,count(distinct a_marketing_name) _ant ,sum(a_port) _port from tbl_siteconfiguration " +
            " where sitename = '"+SV_SiteName+"' and idOperation ='"+SV_IdOperation+"'" +
            " group by sitename;";
            Cursor c = db.getsitedetails(sqlcmd);
            if (c.getCount() > 0) {

                c.moveToFirst();
                do {
                    nant=Integer.parseInt(c.getString(1)) ;   //11 rubrique ant
                    port=Integer.parseInt(c.getString(2)); //2 rubrik par pp

                }while (c.moveToNext());
            }

        // panoramic + sitedetail
            sqlcmd= " SELECT count(distinct SV_Description) ct_pano FROM tbl_SiteVisit where SV_IdOperation='"+SV_IdOperation+"' and SV_SiteName='"+SV_SiteName+"' and  SV_Status iN (100,300); ";
            c = db.getsitedetails(sqlcmd);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    panoramic = Integer.parseInt(c.getString(0));

                } while (c.moveToNext());
            }
             // antenna
            sqlcmd= " SELECT  distinct SV_Ports,SV_Description FROM tbl_SiteVisit where SV_IdOperation='"+SV_IdOperation+"' and SV_SiteName='"+SV_SiteName+"' and  SV_Status ='200'; ";
            c = db.getsitedetails(sqlcmd);
            if (c.getCount() > 0) {
                antenna=c.getCount();

            }


                totgen=tpanoramic +tsitedetail + (tantenna * nant) + (2* port);
                float pourcent = (panoramic + antenna )* 100 /totgen;
                 progressStatus=Math.round(pourcent);


            db.close();
        }catch (Exception e){
                e.getMessage();
        }
    }
    public void addListenerOnSpinnerItemSelection() {

        uploaderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getLocation();
                onValidation("finish");
            }
        });


    }
    public void onValidation(String strvalidation){
        connector = new ConnectionDetector(getApplicationContext());

        TextView lblError = (TextView) findViewById(R.id.lblError);
        // boolean isConnected = isNetworkAvailable();

        if (connector.isConnectingToInternet()){
            lblError.setText("connected");
        }else{
            lblError.setText("Ouups!!! probleme de connection.");
            showAlertDialog(Activity_comment.this, "No Internet Connection",
                    "Ouups!!! probleme de connection.", false);
            return;
        }


        if (blnCheckDataBeforeUpload() == true){
            SV_Pictures=strPersonalPhoto;
            strURLInsertRegistration=baseUrl + "InsertSitevisit";
            new  ImageUploadTaskAsync().execute(SV_IdOperation,SV_SiteName, SV_Description, SV_userid,SV_Device,SV_Status );

        }

    }
    public class  ImageUploadTaskAsync extends AsyncTask<String,String,String> {
        private ProgressDialog dialog = new ProgressDialog(Activity_comment.this);
        HttpsURLConnection conn;
        URL url = null;
        SSLContext sslcontext;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            Button btnUpload = (Button)findViewById(R.id.validateButton);
            btnUpload.setVisibility(View.GONE);
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
                conn.setDoInput(true);
                //conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("SV_IdOperation", params[0])
                        .appendQueryParameter("SV_SiteName", params[1])
                        .appendQueryParameter("SV_Description", params[2])
                        .appendQueryParameter("SV_userid", params[3])
                        .appendQueryParameter("SV_Device", params[4])
                        .appendQueryParameter("SV_Status", params[5]);

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
                return "exception try post"+e1.toString();
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
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.e("SmsReceiver",result);

            Button btnUpload = (Button)findViewById(R.id.validateButton);
            btnUpload.setVisibility(View.VISIBLE);

            if (result!=null && result.indexOf("uploaded")>=0){

                // Toast.makeText(getApplicationContext(), "Upload successfully.",Toast.LENGTH_LONG).show();
                try{
                    Boolean iniConfig=false;
                    String MsgIni="";
                    db.open();

                    if (!db.setsitedetails("delete from tbl_siteconfiguration where idoperation='"+getSessionId()+"' and sitename='"+getSitename()+"'")){
                        MsgIni="Sorry,Initialisation Fialed!!!";
                    }

                    if (!db.setsitedetails("DELETE FROM tbl_loading where sessionid='"+getSessionId()+"' and sitename='"+getSitename()+"'")){
                        MsgIni="Sorry,Initialisation Fialed!!!";
                    }
                    if (!db.setsitedetails("DELETE FROM tbl_SiteVisit where SV_IdOperation ='"+getSessionId()+"' and SV_SiteName='"+getSitename()+"'")){
                        MsgIni="Sorry,Initialisation Fialed!!!";
                    }
                    db.close();

                     if (MsgIni!="") {
                         showAlertDialog(Activity_comment.this, "Initialisation",
                                 MsgIni.toString(), false);
                     }

                }catch (Exception e){
                    db.close();

                    showAlertDialog(Activity_comment.this, "Validation failed!",
                            e.toString(), false);
                    return;
                }
                // alertMsg(Activity_panoramic.this,"", "Upload successfully.");
                alertNotification(Activity_comment.this,"Validation ","Operation successfully.\nThanks !",MainActivity.class,"","",false);
                // finish();
                return;
            }else{
                showAlertDialog(Activity_comment.this, "Validation stop",
                        "Ouups!!! error occur.", false);

            }

            TextView lblError = (TextView) findViewById(R.id.lblError);
            lblError.setText(result);

        }
    }
    public boolean blnCheckDataBeforeUpload() {
        try{

            TextView lblError = (TextView) findViewById(R.id.lblError);
            lblError.setText("");

            EditText commet=(EditText)findViewById(R.id.edtcomment);
            SV_Description=commet.getText().toString();
            if (SV_Description.equals("")){
                commet.setError("Please Fill this field !!!");
                return  false;
            }

            if (SV_IdOperation.equals("") || SV_SiteName.equals("")){
                showAlertDialog(Activity_comment.this,"Error occur", "Sorry,Please check your configuration first.",false);
                return  false;
            }

            return true;
        }catch (Exception e){
            showAlertDialog(Activity_comment.this,"Error occur", e.getMessage(),false);
            return  false;
        }
    }
    public String strURLInsertRegistration;
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
    public void alertNotification(Context context, String title, String msg, final Class activity,final String strBtnClicked,final String numero_port,final Boolean isAntenne){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callForm(Activity_comment.this,activity,strBtnClicked,numero_port,isAntenne);

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return;
    }
    public void callForm(Context context,Class activity,String btnClicked,String numero_port,Boolean isAntenna){
        Intent intent = new Intent(context,activity);
        intent.putExtra("btnclicked", btnClicked);
        intent.putExtra("isAntenna", isAntenna);
        intent.putExtra("numero_port", numero_port);
        startActivity(intent);
        this.finish();
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
    private void requestPermission() {
        ActivityCompat.requestPermissions(Activity_comment.this, new String[]
                {
                        CAMERA,
                        READ_CONTACTS,
                        READ_PHONE_STATE,
                        ACCESS_FINE_LOCATION,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }

    public  String SV_IdOperation,SV_SiteName, SV_SiteConfig, SV_DateTime,SV_Coordinates,SV_AuditedBy,SV_Rigger,SV_Owner,
            SV_Vendor,SV_RooftopTower,SV_TowerMastHeight,SV_BuildingHweight,SV_TotNumAntennas,SV_AntennaModel,SV_Remarks,SV_Tech,
            SV_Sector,SV_Description,SV_Plan,SV_Actual,SV_Pictures,SV_userid,SV_Device,SV_Antenne,SV_Ports;




}
