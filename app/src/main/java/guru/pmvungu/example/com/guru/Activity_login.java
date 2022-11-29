package guru.pmvungu.example.com.guru;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import guru.pmvungu.example.com.includes.ConnectionDetector;
import guru.pmvungu.example.com.includes.apiUrl;

import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getUserId;
import static guru.pmvungu.example.com.includes.apiUrl.setDeviceId;
import static guru.pmvungu.example.com.includes.apiUrl.setHost;
import static guru.pmvungu.example.com.includes.apiUrl.setRoleId;
import static guru.pmvungu.example.com.includes.apiUrl.setUserId;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;


public class Activity_login extends AppCompatActivity {
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
    private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
    private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;
    private static final int SCAN_SIM_BAR_CODE = 49374;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    TelephonyManager device;
    private GpsTracker gpsTracker;
    private libClass libclass;
    ConnectionDetector connector;
    public String deviceId, getSubscriberId, Networkcode, NetLac;
    public String SV_Device, sVersion;
    String SV_userName, SV_IdOperation, SV_userid, SV_passWord;
    Button btnLogin;
    AutoCompleteTextView inputtext;

    final DBAdapter db = new DBAdapter(Activity_login.this);
    String[] languages = {""};

    int pictureNbr;
    private static final int REQUEST_CODE = 101;
    public static final int RequestPermissionCode = 1;
    public String SV_Role;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SV_userid = "";
        SV_Role="";
        SV_IdOperation = "";
        sVersion = "Beta.1.0";
        try {
            libclass = new libClass(Activity_login.this);


            if (libclass.checkPermission()) {
              //  Toast.makeText(Activity_login.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            } else {
                libclass.requestPermission(Activity_login.this);
            }

            try {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                   // Toast.makeText(getBaseContext(),"gps",Toast.LENGTH_SHORT).show();

                }else{
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                    return;
                }
            } catch (Exception e){
                e.printStackTrace();
                popMessage("Error occur",e.getMessage());
            }

            getIpServer();
            FillCellId();
            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickbtnLogin();
                }
            });

            //DownloadAPK();
            db.open();

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            popMessage("Error occur",e.getMessage());

        }




    }
    private void FillCellId() {
        final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                //  ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                deviceId =getUniqueID(); //String.valueOf(telephony.getDeviceId());
                getSubscriberId = getIMSI(getBaseContext());//telephony.getSubscriberId().toString();
                Networkcode = String.valueOf(telephony.getSimOperator());
                SV_Device=deviceId;

                setDeviceId(deviceId);

                if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                    final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
                    if (location != null) {
                        NetLac = location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff);
                        TextView lblVersion = (TextView) findViewById(R.id.lblVersion);
                        lblVersion.setVisibility(View.VISIBLE);
                        String strlblVersion = "<small> Cell ID : " + NetLac + "<br>Version : " + sVersion + "</small>";
                        lblVersion.setText(Html.fromHtml(strlblVersion));

                    }

                }

            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);

            }
        } catch (Exception e) {
            e.printStackTrace();
            popMessage("Error occur",e.getMessage());
        }
    }

    public  String getIMSI(Context context) {
        String IMSI="";
        String IMEI="";
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            IMSI = telephonyManager.getSubscriberId();
            IMEI = telephonyManager.getDeviceId();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);

        }
        return IMSI;
    }

    public String getUniqueID(){

        String myAndroidDeviceId = "123456";
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null){
            myAndroidDeviceId = mTelephony.getDeviceId();
        }else{
            myAndroidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);

        }
        return myAndroidDeviceId;
    }
    private void onClickbtnLogin() {
        TextView lblError = (TextView) findViewById(R.id.lblError);
        try {

            if (blnCheckDataBeforeUpload() == true) {
                onLogin();
            }
        } catch (Exception e) {
            lblError.setText(e.getMessage());
        }

    }
    private Boolean blnCheckDataBeforeUpload() {
        TextView lblError = (TextView) findViewById(R.id.lblError);
        lblError.setText("");

        AutoCompleteTextView userName = (AutoCompleteTextView) findViewById(R.id.edtUserName);
        SV_userName = userName.getText().toString();
        if (SV_userName.equals("")) {
            Toast.makeText(this, "Please entry the username.", Toast.LENGTH_LONG).show();
            userName.setError("Please entry the username.");
            userName.findFocus();
            //lblError.setText("Please select the SiteName.");
            return false;
        }
        EditText edtpassword = (EditText) findViewById(R.id.edtpassword);
        SV_passWord = edtpassword.getText().toString();
        if (SV_passWord.equals("")) {
            Toast.makeText(this, "Invalid password.", Toast.LENGTH_LONG).show();
            edtpassword.setError("Invalid password.");
            edtpassword.findFocus();
            //lblError.setText("Please select the SiteName.");
            return false;
        }

        return true;
    }
    public void popMessage(String strTitle, String strMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_login.this);
        builder.setMessage(strMessage);
        builder.setTitle(strTitle);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                callForm(Activity_login.this, Splashscreen.class,SV_userName);
            }
        });

     /*  builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //callForm(MainActivity.this,MainActivity.class);
            }
        });*/

        AlertDialog alert = builder.create();
        alert.show();
        return;

    }
    public void callForm(Context context, Class activity,String userId) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }
    public void onLogin() {
        ((TextView)findViewById(R.id.lblError)).setText("");
        connector =new ConnectionDetector(getApplicationContext());
        TextView lblError = (TextView) findViewById(R.id.lblError);
        lblError.setText("");
    // boolean isConnected = isNetworkAvailable();

  if(connector.isConnectingToInternet())

    {
         lblError.setText("connected");
    }else

    {
         lblError.setText("Ouups!!! probleme de connection.");
        libclass.popMessage("No Internet Connection", "Ouups!!! probleme de connection.");
        return;
    }


 if(blnCheckDataBeforeUpload() ==true)
    {
         strURLInsertRegistration = baseUrl + "login?SV_userName="+SV_userName+"&SV_passWord="+SV_passWord+"&SV_Device="+SV_Device;
         new loginTaskAsync().execute(strURLInsertRegistration);
    }

}
    public class  loginTaskAsync extends AsyncTask<String,String,String> {
            private ProgressDialog dialog = new ProgressDialog(Activity_login.this);
            HttpURLConnection conn;
            URL url = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Please wait while...");
                Button btnUpload = (Button)findViewById(R.id.btnLogin);
                btnUpload.setVisibility(View.GONE);
                dialog.setCancelable(false);
                dialog.show();
            }

            protected String doInBackground(String... params) {
                try {
                   // url = new URL(strURLInsertRegistration);
                     return libclass.parse(strURLInsertRegistration);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "exception doInbackground";
                }
    /*
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
                            .appendQueryParameter("SV_userName", params[0])
                            .appendQueryParameter("SV_passWord", params[1])
                            .appendQueryParameter("SV_Device", params[2]);

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
                        return("unsuccessful");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return "exception response";
                } finally {
                    conn.disconnect();
                }

                */

            }

            @Override
            protected void onPostExecute(String result) {

                try {
                    // Toast.makeText(Activity_login.this, result, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

                    Log.e("Login connection :", result);
                    Button btnUpload = (Button) findViewById(R.id.btnLogin);
                    btnUpload.setVisibility(View.VISIBLE);
                    if (result != null && result.toLowerCase().indexOf("login:") >= 0) {

                        String user[] = result.split(":");
                        if (user != null && user.length >= 1){
                            setUserId(user[1]);
                        }
                        if (user != null && user.length >=3){
                            setRoleId(user[2]);
                        }else{
                             setRoleId("0");
                        }

                        Toast.makeText(getApplicationContext(), "Success connexion !!!", Toast.LENGTH_LONG).show();
                        //chek if succ get config main
                        //callForm(Activity_login.this,MainActivity.class,user[1].toString());
                        //finish();
                        strURLInsertRegistration = baseUrl + "getMenuArray?sitename=" + SV_Device;
                        new getMenuTaskAsync().execute(strURLInsertRegistration);


                    }else   if (result != null && result.toLowerCase().indexOf("invalide") >= 0) {

                        ((TextView) findViewById(R.id.lblError)).setText(result);

                    } else {
                        ((TextView) findViewById(R.id.lblError)).setText("Ouups !!! error de connexion.");
                        return;
                    }

                }catch (Exception e){
                   Log.e("error",e.getMessage());

                    if (result.contains("timeout")){
                        ((TextView) findViewById(R.id.lblError)).setText("Connexion timeout: please check your internet connexion and try again..." );
                        return;
                    }

                     ((TextView) findViewById(R.id.lblError)).setText("Error occur: please try again... \ncontact administror system." );
                    return;

                }
            }
        }
    public class  getMenuTaskAsync extends AsyncTask<String,String,String> {
            private ProgressDialog dialog = new ProgressDialog(Activity_login.this);
            HttpURLConnection conn;
            URL url = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Please wait while...");
                Button btnUpload = (Button)findViewById(R.id.btnLogin);
                btnUpload.setVisibility(View.GONE);
                dialog.setCancelable(false);
                dialog.show();
            }

            protected String doInBackground(String... params) {
                try {
                    // url = new URL(strURLInsertRegistration);
                    return libclass.parse(strURLInsertRegistration);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "exception doInbackground";
                }

    /*
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
                            .appendQueryParameter("SV_userName", params[0])
                            .appendQueryParameter("SV_passWord", params[1])
                            .appendQueryParameter("SV_Device", params[2]);

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
                        return("unsuccessful");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return "exception response";
                } finally {
                    conn.disconnect();
                }

                */

            }

            @Override
            protected void onPostExecute(String result) {
            try {

                dialog.dismiss();
                Log.e("SmsReceiver", result);
                Button btnUpload = (Button) findViewById(R.id.btnLogin);
                btnUpload.setVisibility(View.VISIBLE);
                if (result != null) {
                    if (result.toLowerCase().indexOf("error") >= 0) {
                        ((TextView) findViewById(R.id.lblError)).setText(result);
                        return;
                    }

                    db.open();

                   // db.setsitedetails("delete from tbl_loading");
                    db.setsitedetails("delete from tbl_menu");
                  //  db.setsitedetails("delete from tbl_siteconfiguration");

                    JSONArray jsonArray = new JSONArray(result);
                    String[] subs = new String[jsonArray.length()];

                    for (int l = 0; l < jsonArray.length(); l++) {
                        JSONObject obj = jsonArray.getJSONObject(l);
                        String sqlcmd = "INSERT INTO tbl_menu (idmenu,fmnu,label,objects,parent) VALUES("
                                + "'" + obj.getString("idmenu").toString() + "'"
                                + ",'" + obj.getString("fmnu").toString() + "'"
                                + ",'" + obj.getString("label").toString() + "'"
                                + ",'" + obj.getString("objects").toString() + "'"
                                + ",'" + obj.getString("parent").toString() + "'"
                                + ");";

                        db.setsitedetails(sqlcmd);


                    }

                    db.close();

                    strURLInsertRegistration = baseUrl + "getAntenne";
                    new getAntenneTaskAsync().execute(strURLInsertRegistration);

                } else {
                    ((TextView) findViewById(R.id.lblError)).setText(result);
                    return;
                }
            }catch (Exception e){
               e.printStackTrace();
                ((TextView) findViewById(R.id.lblError)).setText(e.getMessage());
                return;
            }
            }
        }
    public class  getAntenneTaskAsync extends AsyncTask<String,String,String> {
        private ProgressDialog dialog = new ProgressDialog(Activity_login.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait while...");
           // Button btnUpload = (Button)findViewById(R.id.btnLogin);
           // btnUpload.setVisibility(View.GONE);
          // dialog.setCancelable(false);
           // dialog.show();
        }
        protected String doInBackground(String... params) {
            try {
                // url = new URL(strURLInsertRegistration);
                return libclass.parse(strURLInsertRegistration);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception doInbackground";
            }



        }
        @Override
        protected void onPostExecute(String result) {
            try {

                dialog.dismiss();
                Log.e("SmsReceiver", result);
               // Button btnUpload = (Button) findViewById(R.id.btnLogin);
               // btnUpload.setVisibility(View.VISIBLE);
                if (result != null) {
                    if (result.toLowerCase().indexOf("error") >= 0) {
                        ((TextView) findViewById(R.id.lblError)).setText(result);
                        return;
                    }


                    JSONArray jsonArray = new JSONArray(result);
                    String[] subs = new String[jsonArray.length()];

                    for (int l = 0; l < jsonArray.length(); l++) {
                        JSONObject obj = jsonArray.getJSONObject(l);

                               // + "'" + obj.getString("antenne").toString() + "'"
                               // + ",'" + obj.getString("technology").toString() + "'"

                        //db.setsitedetails(sqlcmd);
                        subs[l]=obj.getString("antenne").toString();

                    }

                    apiUrl.setListOfAntennaModel(subs);

                       db.open();
                       db.setsitedetails("delete from tbl_siteconfiguration");
                     db.close();
                    callForm(Activity_login.this, MainActivity.class, getUserId());
                    finish();

                } else {
                    ((TextView) findViewById(R.id.lblError)).setText(result);
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                ((TextView) findViewById(R.id.lblError)).setText(e.getMessage());
                return;
            }
        }
    }
    public void getIpServer(){
        try {
            String _ipserver="";
            String _port="";
            String _name="", _apn="";
            db.open();
            Cursor c = db.getsitedetails("SELECT _id, name,apn,ipserver,port FROM tbl_server where ipserver <> '' order by _id desc");
            if (c.getCount()>0) {
                c.moveToFirst();
                _name=c.getString(1).toString();
                _apn=c.getString(2).toString();
                _ipserver = c.getString(3).toString();
                _port = c.getString(4).toString();

                if (!_port.equals("")){
                    _port=":"+_port;
                }
           /* if (!_ipserver.equals("")){
                _ipserver="http://"+_ipserver;
            }
            if (!_apn.equals("")){
                _apn="/"+_apn;

            }
            if (!_name.equals("")){
                _name="/"+_name+".php";

            }
            pathUrl=_ipserver+ _port +_apn+_name ;
            txtURLIP=pathUrl;
    */

              //  Toast.makeText(getBaseContext(), _ipserver+ "!!!", Toast.LENGTH_LONG).show();

            }else {

                Toast.makeText(getBaseContext(), "Server no found. configure your APN!!!", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(Activity_login.this,Activity_config.class);
                // startActivity(intent);
               // _ipserver="mobileapp.africell.cd";
                _ipserver="rnpo.africell.cd";
                _port="";
               // _port=":8090";
                popupMessage(Activity_login.this,"","You use the default parametre. \n make sure to change your APN.");

            }

            db.close();
            apiUrl.setHost(_ipserver+_port);
            apiUrl.getBaseUrl();
            //  test    p://localhost:81/apn/name.php
            // Toast.makeText(getBaseContext(), " url  "+baseUrl, Toast.LENGTH_LONG).show();

           // popupMessage(Activity_login.this,"",baseUrl );

        }catch (Exception e){
            e.printStackTrace();
            popMessage("Loading Failled","Error occur :" +e.getMessage());
        }

        }
    public void popupMessage(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pictureNbr = 1;

            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

          /*  if(myMenu != null) {
                myMenu.findItem(R.id.nav_newsite)
                        .setEnabled(false);
            }
    */
            return true;
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
              switch (item.getItemId()) {
                case R.id.action_settings:
                    Intent intent = new Intent(Activity_login.this, Activity_config.class);
                    startActivity(intent);
                    // Toast.makeText(this,"this is main activity",Toast.LENGTH_LONG).show();
                    //return true;
            }


            return super.onOptionsItemSelected(item);
        }

    String strURLInsertRegistration="";
    public String[] strListAntenna=null;//{"Kathrein","Andrew: DBXLH-6565C-VTM"};
}
