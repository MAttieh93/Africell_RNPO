package guru.pmvungu.example.com.guru;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import guru.pmvungu.example.com.includes.NoSSLv3SocketFactory;
import guru.pmvungu.example.com.includes.apiUrl;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


public class Activity_synchronize extends AppCompatActivity {
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
    private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
    private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;
    private static final int SCAN_SIM_BAR_CODE = 49374;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    TelephonyManager device ;
    private GpsTracker gpsTracker;


    public String  deviceId,getSubscriberId,Networkcode,NetLac;
    public String SV_siteName,SV_siteConfiguration,SV_owner,SV_vender,SV_antNumber,SV_roottop,SV_tower,SV_strlocation,SV_building,SV_Audited,SV_Rugger;
    String SV_userid,SV_DateTime ,SV_IdOperation,SV_Device;
    Button BtnSynchronize;
    String token;
    final DBAdapter db = new DBAdapter(Activity_synchronize.this);
    private libClass libclass=new libClass(Activity_synchronize.this);

    int pictureNbr;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;

    EditText edtAudited,edtRugger;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);

        TextView rfdataheader=(TextView)findViewById(R.id.rfdataheader);
        BtnSynchronize=(Button)findViewById(R.id.validateBtnSynchronize) ;

        edtAudited=(EditText)findViewById(R.id.edtAudited);
        edtRugger=(EditText)findViewById(R.id.edtRigger);

        token="123456";
        String _strHTML = "";
        try{
            db.open();
            Cursor cursor=db.getRfdata();
            if (cursor.getCount() >0){
                cursor.moveToFirst();
                _strHTML = ""
                        + "<small>"
                        + "<b>OPID  : </b>"+ cursor.getString(0) +"<br />"
                        + "<b>Site Name  : </b>"+ cursor.getString(1) +"<br />"
                        + "<b>Configuration : </b> "+ cursor.getString(2) +" <br/><br/>"
                        + "<b>RootTop/Tower:  </b> "+ cursor.getString(7) + "<br />"
                        + "<b>Tower/mast height :</b>"+ cursor.getString(8) +"<br />"
                        + "<b>Building height:</b>"+ cursor.getString(9) +"<br />"
                        + "<b>Total number of Africell antennas: </b> "+ cursor.getString(10) +"<br /><br/>"
                        + "<b>Owner (Africell/Helios): </b>"+ cursor.getString(5) +"<br />"
                        + "<b>Vendor(Ericsson/Huawei): </b> "+ cursor.getString(6) +"<br />"
                        + "<b>Coordinates: </b>"+ cursor.getString(4) +"<br />"
                        + "</small>";

                SV_userid=cursor.getString(11);
                SV_DateTime=cursor.getString(3);
                SV_IdOperation=cursor.getString(0);
                SV_Device=cursor.getString(12);
            }else{

                _strHTML = ""
                        + "<small>Synchronize :0 data</small>";
                BtnSynchronize.setVisibility(View.GONE);
            }

            db.close();
            rfdataheader.setText(Html.fromHtml(_strHTML));
        }catch (Exception e){
            rfdataheader.setText(Html.fromHtml(_strHTML));
            db.close();
        }

        BtnSynchronize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         onSynchronize();
                    }});


    }
public String strURLInsertRegistration="";
private  void onSynchronize(){

    SV_Audited=edtAudited.getText().toString();
    if(SV_Audited.equals("")){
        edtAudited.setError("Please Fill All fields...");
        edtAudited.findFocus();
        return;
    }

    SV_Rugger=edtRugger.getText().toString();
    if(SV_Rugger.equals("")){
        edtRugger.setError("Please Fill All fields");
        return;
    }
    strURLInsertRegistration=baseUrl + "setSynchronize";
    new AsyncSynchronize().execute(SV_IdOperation,SV_Audited,SV_Rugger,SV_userid,SV_DateTime,SV_Device);
}

    public class  AsyncSynchronize extends AsyncTask<String,String,String> {
        private ProgressDialog dialog = new ProgressDialog(Activity_synchronize.this);
        //HttpsURLConnection httspconn;
        HttpURLConnection conn;

        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait while Synchronize...");

            dialog.setCancelable(false);
            dialog.show();
        }

        protected String doInBackground(String... params) {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLSv1");
                sslcontext.init(null, null, null);
                SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
              //  HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);

                url = new URL(strURLInsertRegistration);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception doInbackground";
            }

            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
               //  conn = (HttpsURLConnection) url.openConnection();
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
                        .appendQueryParameter("SV_Audited", params[1])
                        .appendQueryParameter("SV_Rugger", params[2])
                        .appendQueryParameter("SV_userid", params[3])
                        .appendQueryParameter("SV_DateTime", params[4])
                        .appendQueryParameter("SV_Device", params[5]);

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

        }

        @Override
        protected void onPostExecute(String result) {
            // Toast.makeText(Activity_login.this, result, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            try{
              //  Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                // Log.e("SmsReceiver",result);
              if(result !=null){
                  int pos=result.toLowerCase().indexOf("synchronized") ;

                    if (pos >=0){
                        db.open();
                        db.truncateRFData();
                        db.close();
                       // finish();
                        alertMsgOkCancel(Activity_synchronize.this,"synchronized","Operation Successfully !!!");
                        return;


                     }

                }else{
                  libclass.popMessage("ERROR OCCUR","Synchronization Failed : \n"+result);
              }
            }catch (Exception e){
                libclass.popMessage("ERROR OCCUR","Synchronization Failed : \n"+ e.getMessage());
            }

        }
    }
    public void alertMsgOkCancel(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                callForm(Activity_synchronize.this,MainActivity.class);
            }
        });

       builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                callForm(Activity_synchronize.this,Activity_synchronize.class);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }


    public void callForm(Context context,Class activity){
        Intent intent = new Intent(context,activity);
        startActivity(intent);
    }

}
