package guru.pmvungu.example.com.guru;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import guru.pmvungu.example.com.includes.ConnectionDetector;

import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getHost;

public class Activity_afficher extends AppCompatActivity {
    //check if wifi or mobile connected and return true
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    final DBAdapter db = new DBAdapter(this);
    String pathUrl ;
    String _ipserver,_port,_name,_page;
    private WebView webView;
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_web_view);
        webView = (WebView) findViewById(R.id.mainWebView);
        //WebView mainWebView = (WebView) findViewById(R.id.mainWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // webView.setWebViewClient(new MyCustomWebViewClient());
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        pathUrl="";
        //getwap parametre
        getIpServer();
        // Toast.makeText(this, "URL=" +pathUrl, Toast.LENGTH_LONG).show();
        //if connected
        if(haveNetworkConnection()){
            if (pathUrl.equals("")){
                webView.loadUrl("file:///android_asset/error.html");
                return;
            }
            startWebView(pathUrl);
            // webView.loadUrl("file:///android_asset/index1.html");
        } else {
            // webView.loadUrl("file:///android_asset/error.html");
            webView.loadUrl("file:///android_asset/error.html");
        }

    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {


            ProgressDialog progressDialog = new ProgressDialog(Activity_afficher.this);
            //If you will not use this method url links are open in new brower not in webview
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
            //If url has "tel:245678" , on clicking the number it will directly call to inbuilt calling feature of phone
            public boolean shouldOverrideUrlLoading(WebView view ,String url){

                if(url.startsWith("tel:")){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                } else {

                    view.loadUrl(url);

                }
                return  true;
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                String message = "SSL Certificate error.";
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = "The certificate authority is not trusted.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "The certificate has expired.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "The certificate Hostname mismatch.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "The certificate is not yet valid.";
                        break;
                }
                message += "\"SSL Certificate Error\" Do you want to continue anyway?.. YES";

                handler.proceed();
            }
            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(Activity_afficher.this);
                    progressDialog.setMessage("Loading...");
                    // progressDialog.setCancelable(false);
                    progressDialog.show();

                }
            }

            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }


        });


        //Load url in webview
        webView.loadUrl(url);
    }

    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
            finish();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

    libClass libclass=new libClass(this);
    ConnectionDetector connectiondetector=new ConnectionDetector(this);

    public void  getIpServer() {
        try {
            pathUrl = "";
            //remove default port
            String[] sHost=getHost().split(":");


            pathUrl="https://"+sHost[0].toString()+":1035/";
           // pathUrl=pathUrl + "Default.aspx";
            if (!connectiondetector.isConnectingToInternet()){

                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(true);
                libclass.popMessage("Connexion","Oups!! please check your connexion internet!!!");
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}













//import com.tutomobile.android.listView.R;
 /* patou Diasivi, ofrael dias
public class Activity_afficher extends AppCompatActivity {

    private ListView maListViewPerso;
    final DBAdapter db = new DBAdapter(Activity_afficher.this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Récupération de la listview créée dans le fichier Activity_Histtory.xml
        maListViewPerso = (ListView) findViewById(R.id.listviewperso);

        //Création de la ArrayList qui nous permettra de remplir la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

        //On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;


        int i=0;

        db.open();  //open connection
        Cursor c = db.getHistory();   //call fonction getHistory and select all record data
        if (c.moveToFirst())           ///check if exist
        {
            do {
                //list.add("Name: " + c.getString(1) + ",Ticket: " + c.getString(2));
                map = new HashMap<String, String>();
                //on insère un élément titre que l'on récupérera dans le textView titre créé dans le fichier main.xml
                map.put("titre", c.getString(2));//  map.put("titre", c.getString(2));
                //on insère un élément description que l'on récupérera dans le textView description créé dans le fichier main.xml
               // String descript=c.getString(1) + "DateTime" + c.getString(3);
                map.put("description", c.getString(1) + " \nDateTime : "+c.getString(3));
                //on insère la référence à l'image (converti en String car normalement c'est un int) que l'on récupérera dans l'imageView créé dans le fichier main.xml
                map.put("img", String.valueOf(R.drawable.icon));
                //enfin on ajoute cette hashMap dans la arrayList
                listItem.add(map);

                i=+1;
            } while (c.moveToNext());
        }
        db.close();


        //Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem , R.layout.main,
                new String[] {"img", "titre", "description"}, new int[] {R.id.img, R.id.titre, R.id.description});

        //On attribue à notre listView l'adapter que l'on vient de créer
        maListViewPerso.setAdapter(mSchedule);

        //Enfin on met un écouteur d'évènement sur notre listView
        maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
              final int idRow=position+1;
                //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
                //on créé une boite de dialogue
                AlertDialog.Builder adb = new AlertDialog.Builder(Activity_afficher.this);

                adb.setTitle("Delete Item");
                adb.setMessage("Supprimer le "+map.get("titre"));
                //on indique que l'on veut le bouton ok à notre boite de dialogue
                 //adb.setPositiveButton("OK", null).toString();

                adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        //delete ticket
                        db.open();
                        db.deleteHistory(idRow);
                        db.close();
                        Toast.makeText(getBaseContext(),"Suppression effectue !!!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                       /// Toast.makeText(getBaseContext(),"Non", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                //on affiche la boite de dialogue
                adb.show();
                //Toast.makeText(getBaseContext(),r, Toast.LENGTH_LONG).show();
            }
        });

    }







   // AlertDialog alert = builder.create();
   // alert.show();



    private void getHistory(){
        int i=0;

        db.open();  //open connection
        Cursor c = db.getHistory();   //call fonction getHistory and select all record data
        if (c.moveToFirst())           ///check if exist
        {
            do {
                displayHistory(c,i);
                i=+1;
            } while (c.moveToNext());
        }
        db.close();
    }

    private void displayHistory(Cursor c, int i){

        String nr=Integer.toString(i).toString();
        String str="Numero :" + nr + "\nCodePin: " + c.getString(1) + "\nTicket: " + c.getString(2) + "\n" + "Date: " + c.getString(3);

        //list.add("Name: " + c.getString(1) + ",Ticket: " + c.getString(2));

        Toast.makeText(getBaseContext(),str, Toast.LENGTH_LONG).show();

    }
}
*/