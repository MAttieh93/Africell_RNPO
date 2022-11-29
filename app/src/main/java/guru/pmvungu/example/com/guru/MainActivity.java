package guru.pmvungu.example.com.guru;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.pdf417.PDF417Writer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getRoleId;
import static guru.pmvungu.example.com.includes.apiUrl.getUserId;
import static guru.pmvungu.example.com.includes.apiUrl.setSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.setSitename;
import static guru.pmvungu.example.com.includes.apiUrl.setUserId;
import guru.pmvungu.example.com.includes.apiUrl;

import guru.pmvungu.example.com.includes.ConnectionDetector;

public class MainActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {
    final DBAdapter db = new DBAdapter(MainActivity.this);
    ConnectionDetector connectiondetector = new ConnectionDetector(MainActivity.this);
    libClass libclass = new libClass(MainActivity.this);
    String paramPictures, SV_userId, SV_userRole;
    Context context;
    String[] lstsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //buton sms
                Snackbar.make(view, "Hi Mr Diasivi !", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            Bundle extras = getIntent().getExtras();
            SV_userId = getUserId();
            SV_userRole = getRoleId();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            //inflate header layout
            View navheaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);

            //reference to views
            TextView tvUsername = (TextView) navheaderView.findViewById(R.id.textView);
            tvUsername.setText(SV_userId + "  Connected");


            //ImageView imgvwUser = (ImageView)navheaderView.findViewById(R.id.imageView);
            //imgvwUser .setImageResource();

            navigationView.setNavigationItemSelectedListener(this);


        } catch (Exception e) {
            e.printStackTrace();
        }

        /* check role user*/
        try {
            if (!SV_userRole.equals("1") && !SV_userRole.equals("0")) {
                setItemVisible(false, R.id.nav_Modification);
                setItemVisible(false, R.id.nav_parametre);
                setItemVisible(false, R.id.nav_New_Site);
                // setItemVisible(false,R.id.nav_validation);

                // setItemVisible(false,R.id.nav_share);
                // setItemVisible(false,R.id.nav_send);
            }

        } catch (Exception e) {
            libclass.popMessage("orror occur", e.getMessage());
        }
       /* if (extras != null) {
            SV_userId =getUserId(); //getIntent().getStringExtra("userId");
            // setUserId(SV_userId);
        }else{
            SV_userId="";
        }
       */
        paramPictures = "";
        //call siteName process
        if (apiUrl.strListOfSite == null) {
            getSiteName();
        }
        //loading
        if (!isLoading()) {
            String SV_siteName = "all";
            strURL = baseUrl + "getSiteconfiguration?sitename=" + SV_siteName;
            new DownConfigurationTask().execute("sitename");
        }

      /* if(!isLoading()){
            if (connectiondetector.isConnectingToInternet()){
                try {
                    db.open();  //open connection
                   // db.setsitedetails("delete from tbl_SiteVisit");
                     Cursor c = db.getsitedetails("select distinct sitename, idoperation from tbl_siteconfiguration");
                     String listofSite="";
                     int i=0;
                     if (c.getCount() > 0) {
                        c.moveToFirst();
                        do {

                            String SV_idoperator = c.getString(1).toString();
                            String SV_sitename = c.getString(0).toString();
                            if (i==0){
                                listofSite =SV_sitename + ":" +SV_idoperator;
                            }else{
                              listofSite +=";"+SV_sitename + ":" +SV_idoperator;
                            }
                              i +=1;

                        }while(c.moveToNext());

                         //strURL = baseUrl + "getSiteDetailsById?sitename=" + SV_sitename + "&idoperation=" + SV_idoperator;
                          strURL = baseUrl + "getSiteVisit?strlistofSite=" + listofSite;
                          new LoadinSitVist().execute("strlistofSite");
                    }

                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
           }else{

               WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
               wifi.setWifiEnabled(true);
               libclass.popMessage("Connexion","Oups!! please check your connexion internet!!!");
               return;
           }
       }*/

    }

    public class DownConfigurationTask extends AsyncTask<String, String, String> {
        URL url = null;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

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

                if (result.indexOf("Error") > 0 || result == null) {
                    libclass.popMessage("Error occur", result);
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

    //hide men nav
    public void setItemVisible(boolean visible, @IdRes int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        toggleVisibility(navigationView.getMenu(), id, visible);
    }

    private void toggleVisibility(Menu menu, @IdRes int id, boolean visible) {
        menu.findItem(id).setVisible(visible);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    Menu myMenu;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

/*
      if(myMenu != null) {
            myMenu.findItem(R.id.nav_New_Site).setEnabled(false);
        }*/

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //return super.onCreateOptionsMenu(menu);
        // myMenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(MainActivity.this, Activity_config.class);
            startActivity(intent);
            // Toast.makeText(this,"this is main activity",Toast.LENGTH_LONG).show();
            //return true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_Radio_audit) {
            callForm(MainActivity.this, Activity_main_menu.class);

        } else if (id == R.id.nav_parametre) {
            callForm(MainActivity.this, Activity_config.class);

        } else if (id == R.id.nav_history) {
            callForm(MainActivity.this, Activity_afficher.class);
        } else if (id == R.id.nav_close) {
            this.finish();
        } else if (id == R.id.nav_New_Site) {
            callForm(MainActivity.this, Activity_addnewsite.class);

        }
        /*
        if (id == R.id.nav_newsite) {
            //check if operation is running
            if(!isOperatorRunning()){
                callForm(MainActivity.this, Activity_report.class);
                paramPictures="Activity_report";
            }else{
                alertMsg(MainActivity.this,"Africell","another operation is being processed,\n please click ok to stop it !!!");
               //callForm(MainActivity.this, Activity_synchronize.class);
            }

        }  if (id == R.id.nav_rfdata) {
            callForm(MainActivity.this, Activity_rfdata.class);

        } else if (id == R.id.nav_parametre) {
            callForm(MainActivity.this, Activity_config.class);

        } else if (id == R.id.nav_photosite) {
            callForm(MainActivity.this, Activity_report_pictures.class);
            paramPictures="pictures";


      //  }  else if (id == R.id.nav_photoantenne) {

        }else if(id==R.id.nav_synchronize){
            callForm(MainActivity.this, Activity_synchronize.class);
        } else if (id == R.id.nav_send) {

        }
      */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //import data
    String strURL;

    public void getSiteName() {

        strURL = baseUrl + "getAllsiteName?sitename=";
        if (connectiondetector.isConnectingToInternet()) {
            new DownSiteNameTask().execute("sitename");
        } else {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            libclass.popMessage("Connexion", "Oups!! please check your connexion internet!!!");
            return;
        }
    }

    public class LoadinSitVist extends AsyncTask<String, String, String> {
        URL url = null;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait the configuration....");
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

                if (result.indexOf("unsucess") >= 0 || result == null || result.indexOf("Error") >= 0) {

                    alertMsg(MainActivity.this, "Error occur", "loading fail !!!", Activity_login.class, Activity_login.class);
                    return;
                }

                //load data json
                loadSiteVisit(result);

            } catch (Exception e) {
                e.printStackTrace();
                alertMsg(MainActivity.this, "Error occur", e.getMessage(), Activity_login.class, Activity_login.class);

            }

            dialog.dismiss();
        }

    }

    private void loadSiteVisit(String listinfo) {
        try {

            db.open();
            db.setsitedetails("delete from tbl_SiteVisit");
            JSONArray jsonArray = new JSONArray(listinfo);
            String[] subs = new String[jsonArray.length()];

            for (int l = 0; l < jsonArray.length(); l++) {
                JSONObject obj = jsonArray.getJSONObject(l);

                // String Sqlcmd="INSERT INTO tbl_SiteVisit ( SV_IdOperation , SV_SiteName , SV_SiteConfig , SV_DateTime , SV_Coordinates , SV_AuditedBy , SV_Rigger , SV_Owner , SV_Vendor , SV_Vendor_2G , SV_Vendor_3G , SV_Vendor_4G , SV_RooftopTower , SV_BuildingHweight , SV_TotNumAntennas , SV_Tech ,  SV_Sector , SV_Description , SV_Pictures ,   SV_Plan , SV_Actual , SV_AntennaModel , SV_Remarks , SV_Device , SV_Status , SV_Latitude , SV_Longitude ) VALUES("
                String Sqlcmd = "INSERT INTO tbl_SiteVisit ( SV_IdOperation , SV_SiteName , SV_SiteConfig , SV_DateTime  , SV_Owner   , SV_Vendor_2G , SV_Vendor_3G , SV_Vendor_4G ,SV_BuildingHweight , SV_Tech   , SV_Description , SV_Pictures , SV_Actual , SV_Remarks , SV_Device , SV_Status , SV_Latitude , SV_Longitude, SV_Antenne , SV_Ports ) VALUES("

                        + "'" + obj.getString("SV_IdOperation").toString() + "'," +
                        "'" + obj.getString("SV_SiteName").toString() + "'," +
                        "'" + obj.getString("SV_SiteConfig").toString() + "'," +
                        "'" + obj.getString("SV_DateTime").toString() + "'," +
                        //  "'" + obj.getString("SV_Coordinates").toString()  + "'," +
                        // "'" + obj.getString("SV_AuditedBy").toString()  + "'," +
                        // "'" + obj.getString("SV_Rigger").toString()  + "'," +
                        "'" + obj.getString("SV_Owner").toString() + "'," +
                        // "'" + obj.getString("SV_Vendor").toString()  + "'," +
                        "'" + obj.getString("SV_Vendor_2G").toString() + "'," +
                        "'" + obj.getString("SV_Vendor_3G").toString() + "'," +
                        "'" + obj.getString("SV_Vendor_4G").toString() + "'," +
                        // "'" + obj.getString("SV_RooftopTower").toString()  + "'," +
                        "'" + obj.getString("SV_Actual").toString() + "'," +
                        // "'" + obj.getString("SV_TotNumAntennas").toString()  + "'," +
                        "'" + obj.getString("SV_Tech").toString() + "', " +
                        // "'" + obj.getString("SV_Sector").toString()  + "'," +
                        "'" + obj.getString("SV_Description").toString() + "'," +
                        "'" + obj.getString("SV_Pictures").toString() + "'," +
                        // "'" + obj.getString("strPersonalPhotoName").toString()  + "', " +
                        // "'" + obj.getString("SV_Plan").toString()  + "'," +
                        "'" + obj.getString("SV_Actual").toString() + "'," +
                        // "'" + obj.getString("SV_AntennaModel").toString()  + "'," +
                        "'" + obj.getString("SV_Remarks").toString() + "'," +
                        "'" + obj.getString("SV_Device").toString() + "'," +
                        "'" + obj.getString("SV_Status").toString() + "'," +
                        "'" + obj.getString("SV_Latitude_GPS").toString() + "'," +
                        "'" + obj.getString("SV_Longitude_GPS").toString() + "'," +
                        "'" + obj.getString("SV_Antenne").toString() + "'," +
                        "'" + obj.getString("SV_Ports").toString() + "'" +
                        ");";

                db.setsitedetails(Sqlcmd);

            }

            db.setsitedetails("insert into tbl_loading(status) values('1');");
            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
            db.close();

        }
    }

    public class DownSiteNameTask extends AsyncTask<String, String, String> {
        URL url = null;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

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

                if (result.indexOf("tbl_SiteName") < 0 || result == null) {
                    // libclass.popMessage ("Error occur",result);
                    // finish();
                    alertMsg(MainActivity.this, "Error occur", "loading fail !!!", Activity_login.class, Activity_login.class);
                    return;
                }
                String strSite = parseXmltoString(result);
                String[] splitSite = strSite.split(";");
                apiUrl.setListOfSite(splitSite);

                //add antennaModel here
                       /* for (int i = 0; i < splitSite.length-1; i++) {

                           // insertSite(splitSite[i]);
                        }*/


            } catch (Exception e) {
                e.printStackTrace();
                alertMsg(MainActivity.this, "Error occur", e.getMessage(), Activity_login.class, Activity_login.class);

            }

            dialog.dismiss();
        }

    }

    public void insertSite(String InSiteName) {
        try {
            db.open();
            db.deleteSiteAll();
            db.insertSite(InSiteName);

            // Toast.makeText(getBaseContext(), "Insert Successfull...", Toast.LENGTH_SHORT).show();
            db.close();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "error:" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("Error", "error occur \n" + e.getMessage());
        }

    }
    public String parseXmltoString(String strXML) {
        Document doc = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        String resultstrText = "";
        try {

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource ipsource = new InputSource();
            ipsource.setCharacterStream(new StringReader(strXML));
            doc = dBuilder.parse(ipsource);

            NodeList nl = doc.getElementsByTagName("siteRecord");  //Tag RowItem

            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);
                String siteName = libclass.getValue(e, "SITENAME"); // siteName child value
                // String xcost = getValue(e, "BirthDate"); // xcost child value
                if (!siteName.trim().equals("")) {
                    if (!resultstrText.trim().equals("")) {
                        // resultstrText = resultstrText +"\n"+ siteName ;
                        resultstrText = resultstrText + ";" + siteName;
                    } else {
                        resultstrText = siteName;
                    }
                }
            }

            return resultstrText;

        } catch (Exception e) {
            e.printStackTrace();
            return "unsucess request.";
        }
    }
    public Boolean isLoading() {
        Boolean bl = false;
        try {
            int i = 0;
            db.open();  //open connection
            Cursor c = db.getsitedetails("select count(*) ct from tbl_siteconfiguration");   //call fonction getHistory and select all record data
            //c.getCount() >0
            if (Integer.parseInt(c.getString(0)) > 0) {
                bl = true;
            } else {
                bl = false;
            }

            db.close();

        } catch (Exception e) {
            e.printStackTrace();
            db.close();

        }
        return bl;

    }
    public void callForm(Context context, Class activity) {
        try {
            Intent intent = new Intent(context, activity);
            intent.putExtra("mainActivity", "");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void alertMsg(Context context, String title, String msg, final Class activityT, final Class ActivityF) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callForm(MainActivity.this, activityT);
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
    private void FillConfig(String listinfo) {
        //SV_IdOperation="";
        try {

            db.open();

            JSONArray jsonArray = new JSONArray(listinfo);
            String[] subs = new String[jsonArray.length()];

            for (int l = 0; l < jsonArray.length(); l++) {
                JSONObject obj = jsonArray.getJSONObject(l);
                String sqlcmd = "INSERT INTO tbl_siteconfiguration(sitename_id,sitename,s_latitude,s_longitude,s_configuration,a_technology,a_model,a_port,idoperation,username,a_marketing_name,datecreated) VALUES("
                        + "'" + obj.getString("Site_id").toString() + "'"
                        + ",'" + obj.getString("Site_Name").toString() + "'"
                        + ",'" + obj.getString("latitude").toString() + "'"
                        + ",'" + obj.getString("longitude").toString() + "'"
                        + ",'" + obj.getString("configuration").toString() + "'"
                        + ",'" + obj.getString("Ant_technology").toString() + "'"
                        + ",'" + obj.getString("Ant_model").toString() + "'"
                        + ",'" + obj.getString("Ant_port").toString() + "'"
                        + ",'" + obj.getString("SV_IdOperation").toString() + "'"
                        + ",'" + obj.getString("SV_userid").toString() + "'"
                        + ",'" + obj.getString("Ant_fullname").toString() + "'"
                        + ",'" + obj.getString("Datetime").toString() + "'"
                        + ");";

                db.setsitedetails(sqlcmd);

            }

            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
            alertMsg(this, "Loading failed!", e.getMessage(), MainActivity.class, null);
            callForm(MainActivity.this, Activity_login.class);
            finish();

        } finally {

            // End the transaction.
            db.close();
            // Close database
        }
    }
    private void sendEmail(String strpPath, String FromAdresse, String ToAdresse[], String xBody, String xSubject) {

        String[] mailto = ToAdresse;//{"me@gmail.com"};
        Uri uri = Uri.parse(strpPath);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, xSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, xBody);
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send email using:"));

    }

}
