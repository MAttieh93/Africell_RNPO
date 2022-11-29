package guru.pmvungu.example.com.guru;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.net.HttpURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;

/**
 * Created by pmvungu on 7/10/2018.
 */
import guru.pmvungu.example.com.includes.ConnectionDetector;
import guru.pmvungu.example.com.includes.NoSSLv3SocketFactory;
import info.guardianproject.netcipher.NetCipher;


import java.security.cert.Certificate;
import java.security.cert.CertificateException;


public class libClass {
    private final Context context;
    // the timeout until a connection is established
    public static final int CONNECTION_TIMEOUT=30000;  /*30 secnd*/
    public static final int READ_TIMEOUT=30000;
    // the timeout for waiting for data
    private static final int SOCKET_TIMEOUT = 30000;
    private static final long MCC_TIMEOUT = 30000;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;


     private String pathUrl;
     public String strResult;
    public libClass(Context ctx)
    {
        this.context = ctx;


    }



    private static void setTimeouts(HttpParams params) {
        params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                CONNECTION_TIMEOUT);
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);
        params.setLongParameter(ConnManagerPNames.TIMEOUT, MCC_TIMEOUT);
    }


public Boolean strToBoolean(String xvalues){
        Boolean res=false;

        if (xvalues.equals("true")){
            res=true;
        }else if(xvalues.equals("false")){
            res=false;
        }

        return  res;
    }
public void popMessage(String strTitle,String strMessage){
   AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
    builder.setMessage(strMessage);
    builder.setTitle(strTitle);
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {

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
public static int getBatteryLevel(Context context, Intent intent) {
        Intent batteryStatus = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryLevel = -1;
        int batteryScale = 1;
        if (batteryStatus != null) {
            batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, batteryLevel);
            batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, batteryScale);
        }
        return (int)(batteryLevel / (float) batteryScale * 100D);

    }
public static  String PareXml(String urlString){
        //demain corriger
return "";
}


public String parse(String strURL) {
        String  ResultConnection;
        try {

            ResultConnection = getStringForUrl(strURL);
            return ResultConnection;
        } catch (Exception e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
            return "Error occur :"+e1.getMessage();
        }

    }
public String getStringForUrl(String strUrl){
 /* https SSL connexion */
    String result = null;
    StringBuffer sb = new StringBuffer();
    InputStream is = null;

try{
     URL url = new URL(strUrl);
     SSLContext sslcontext = SSLContext.getInstance("TLS");
     sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
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
     }}, null);
     //SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
     //HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);

    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
    urlConnection.setHostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    });

    urlConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
    urlConnection.setReadTimeout(READ_TIMEOUT);
    urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
    urlConnection.setRequestMethod("GET");
   // urlConnection.connect();
    if (urlConnection.getResponseCode()==200){
        Certificate[] certificates=urlConnection.getServerCertificates();
        //ca=certificates[0];
    }

    is = new BufferedInputStream(urlConnection.getInputStream());
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    String inputLine = "";
    while ((inputLine = br.readLine()) != null) {
        sb.append(inputLine);
    }
    result = sb.toString();
}
        catch (Exception e) {

        result = e.getMessage();
    }
        finally {
        if (is != null) {
            try {
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

         return(result.toString());

    }
   // Create an HostnameVerifier that hardwires the expected hostname.
// Note that is different than the URL's hostname:
// example.com versus example.org
  public HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            HostnameVerifier hv =
                    HttpsURLConnection.getDefaultHostnameVerifier();
            return hv.verify("example.com", session);
        }
    };

    protected Boolean isActivityRunning(Class activityClass)
    {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }
    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(context, CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(context, READ_CONTACTS);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(context, READ_PHONE_STATE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        int fivethPermissionResult = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                fivethPermissionResult== PackageManager.PERMISSION_GRANTED;
    }
    public void requestPermission(Activity activity) {

        ActivityCompat.requestPermissions(activity, new String[]
                {
                        CAMERA,
                        READ_CONTACTS,
                        READ_PHONE_STATE,
                        ACCESS_FINE_LOCATION,
                        WRITE_EXTERNAL_STORAGE

                }, RequestPermissionCode);

    }
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

                            Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show();
                        } else {
                            //  Toast.makeText(Activity_deviceposition.this,"Permission Denied" +grantResults[2],Toast.LENGTH_LONG).show();

                        }
                    }

                    break;

            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"Permission Denied " +e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private InputStream OpenHttpsConnection(String urlString) throws IOException
    {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpsURLConnection))
            throw new IOException("Not an HTTP connection");
        try
        {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            response = httpsConn.getResponseCode();
            if (response == HttpsURLConnection.HTTP_OK)
            {
                in = httpsConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }

        return in;
    }

    private InputStream OpenHttpConnection(String urlString) throws IOException
    {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try
        {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }

        return in;
    }
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
    public final String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
    public String getDateTime(){
        String strDatetime="";
        try{
            Calendar calendar = Calendar.getInstance();
            String Year = String.valueOf(calendar.get(Calendar.YEAR));
            String Month = String.valueOf(calendar.get(Calendar.MONTH) +1);
            String Day =  String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String Hour= String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            String Minute=String.valueOf(calendar.get(Calendar.MINUTE));
            String Second=String.valueOf(calendar.get(Calendar.SECOND));

           // strDatetime= Year + "-"+ Month.substring(Month.length()-2,2)  + "-"+  Day.substring(Day.length()-1,2)
            //       + " "+ Hour.substring(Hour.length()-2,2) + ":"+ Minute.substring(Minute.length()-1,2) + ":"+ Second.substring(Second.length()-1,2);
            strDatetime= Year + "-"+ Month   + "-"+  Day  + " "+ Hour  + ":"+ Minute  + ":"+ Second ;


        }catch (Exception e){
            e.printStackTrace();
        }
        return  strDatetime;
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

            matrix.postRotate(orientation);
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    public void fillAutoComplete(AutoCompleteTextView tv, String[] str){
    try{
        ArrayAdapter adapter = new
                ArrayAdapter(context,android.R.layout.simple_list_item_1, str);

        tv.setAdapter(adapter);
        tv.setThreshold(1);
    }catch (Exception e){
        popMessage("error orcur","loading failed...");
    }
        // edvSitename=(AutoCompleteTextView)findViewById(R.id.edtSitename);

    }

    public String parseXmltoString(String strXML,String TagRowItem,String eltChildvalue){
        Document doc = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        String resultstrText="";
        try {

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource ipsource = new InputSource();
            ipsource.setCharacterStream(new StringReader(strXML));
            doc = dBuilder.parse(ipsource);

            NodeList nl = doc.getElementsByTagName(TagRowItem);  //Tag RowItem : siteRecord

            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);
                String strObj = getValue(e, eltChildvalue); // SITENAME : siteName child value
                // String xcost = getValue(e, "BirthDate"); // xcost child value
                if (!strObj.trim().equals("")){
                    if (!resultstrText.trim().equals("")){
                        // resultstrText = resultstrText +"\n"+ siteName ;
                        resultstrText = resultstrText +";"+ strObj ;
                    }else{
                        resultstrText =  strObj  ;
                    }
                }
            }

            return resultstrText;

        }catch (Exception e) {
            e.printStackTrace();
            return "unsucess request.";
        }
    }
    public int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

}



