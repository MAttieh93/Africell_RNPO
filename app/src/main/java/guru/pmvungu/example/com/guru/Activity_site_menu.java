package guru.pmvungu.example.com.guru;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import guru.pmvungu.example.com.includes.apiUrl;

import static guru.pmvungu.example.com.guru.DBAdapter.TABLE_SITECONFIG;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getRoleId;
import static guru.pmvungu.example.com.includes.apiUrl.getSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.getSitename;
import static guru.pmvungu.example.com.includes.apiUrl.getUserId;
import static guru.pmvungu.example.com.includes.apiUrl.setSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.setSitename;

import guru.pmvungu.example.com.includes.ConnectionDetector;


public class Activity_site_menu extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    TelephonyManager device;
    private GpsTracker gpsTracker;
   // private libClass libclass;

    public String deviceId, getSubscriberId, Networkcode, NetLac;
    public String SV_siteName, SV_siteConfiguration, SV_owner, SV_vender, SV_antNumber, SV_roottop, SV_tower, SV_strlocation, SV_building, SV_Device;
    String SV_Audit,SV_userid, SV_DateTime, SV_IdOperation;
    Button btnNewaudit, btnAudit;
    AutoCompleteTextView edvSitename;
    ListView lvAction;
    final DBAdapter db = new DBAdapter(Activity_site_menu.this);
    ConnectionDetector connectiondetector=new ConnectionDetector(Activity_site_menu.this);

    libClass libclass = new libClass(Activity_site_menu.this);

    private static final int REQUEST_CODE = 101;
    public static final int RequestPermissionCode = 1;
    private ProgressBar progressBar;
    private int progressStatus = 0;

    ListView myListviewbutton;
     ArrayList<String> arrayList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_menu);
       // SV_userid = "";
         SV_IdOperation = "";
       // LazyAdapter lz=new LazyAdapter();

try{
        //check if new
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SV_Audit = getIntent().getStringExtra("btnclicked");
            SV_siteName = getIntent().getStringExtra("sitename");
            SV_IdOperation = getIntent().getStringExtra("session");
        }else{
            SV_Audit="Newaudit";
            SV_siteName=getSitename();
            SV_IdOperation=getSessionId();
        }

        setTitle(SV_siteName);
      //if SV_Audit creer table

        // Get the reference of menu
            myListviewbutton = (ListView) findViewById(R.id.lvBbutton);
            myListviewbutton.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                @SuppressWarnings("unchecked")
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    String strButton,strPort;
                    final int idRow=position+1;
                    //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                     HashMap<String, String> map = (HashMap<String, String>) myListviewbutton.getItemAtPosition(position);
                     strButton= map.get("description");
                     strPort=map.get("values");
                     // String item =parent.getItemAtPosition(position).toString();

                    if (strButton.toLowerCase().indexOf("details")>=0) {
                        Toast.makeText(getApplicationContext(), "wait a few minutes ...", Toast.LENGTH_LONG).show();
                        callForm(Activity_site_menu.this,Activity_sitedetails.class,"details",strPort);
                    }else  if (strButton.toLowerCase().indexOf("panoramic")>=0) {
                        Toast.makeText(getApplicationContext(), "wait a few minutes ...", Toast.LENGTH_LONG).show();
                        callForm(Activity_site_menu.this,Activity_panoramic.class,"panoramic",strPort);
                    }
                    else  if (strButton.toLowerCase().indexOf("antenna")>=0) {
                        Toast.makeText(getApplicationContext(), "wait a few minutes ...", Toast.LENGTH_LONG).show();
                          String tbPort[]=strPort.split(";");
                          apiUrl.setAntModel((tbPort[0]).toLowerCase().trim());
                          String sPPort=(tbPort[1]).toLowerCase().trim().replace("p.ports","").trim();
                          apiUrl.setAntPorts(sPPort.trim());
                          // apiUrl.setAntPorts((tbPort[1]).toLowerCase().trim().replace("ports",""));
                          apiUrl.setAntenne(strButton.toLowerCase().trim());

                        callForm(Activity_site_menu.this,Activity_antenna.class,strButton,strPort);
                    }
                    else if (strButton.toLowerCase().indexOf("validation")>=0) {
                        //String tbPort[]=strPort.split(";");
                        //apiUrl.setAntModel(strButton.toLowerCase().trim());
                        //apiUrl.setAntPorts((tbPort[1]).toLowerCase().trim().replace("ports",""));
                        apiUrl.setAntenne(strButton.toLowerCase().trim());
                        callForm(Activity_site_menu.this,Activity_comment.class,strButton,strPort);
                    }

                     Toast.makeText(Activity_site_menu.this,strButton , Toast.LENGTH_LONG).show();

                }
            });

            if (!isConfiguration()){
                //first times
                 getSiteName();
            }
            else{

                FillListview();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public Boolean isloading(){
        Boolean bl=false;
        try{
            int i=0;
            db.open();  //open connection
            Cursor c = db.getsitedetails("select status from tbl_loading");   //call fonction getHistory and select all record data
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
    public Boolean isConfiguration(){
        Boolean bl=false;
        try{
            int i=0;
            db.open();  //open connection
             Cursor c=db.getsitedetails("SELECT count(*) ct FROM tbl_siteconfiguration where idoperation='"+getSessionId()+"' and LOWER(sitename)=LOWER('"+getSitename()+"')");
             c.moveToFirst();
             int nbre=Integer.parseInt(c.getString(c.getColumnIndex("ct")));
            if (nbre>0) {
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


    public void callForm(Context context, Class activity,String btnClicked,String strPort){
        Intent intent = new Intent(context,activity);
        intent.putExtra("btnclicked", btnClicked);
        intent.putExtra("isAntenna", true);
        intent.putExtra("port", strPort);
        startActivity(intent);
        //this.finish();
    }
    public  void FillListview() {
        Integer totgen,tpanoramic,tantenna,tport,tsitedetail=0;
        Float panoramic,antenna,port,nant,sitedetail  ;
        String _status="0";
        panoramic=Float.parseFloat("0");
        antenna=Float.parseFloat("0");
        sitedetail=Float.parseFloat("0");

        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;


        try {
            progressStatus = 5;
            String strPour="0 %";
            db.open();
            String query ="SELECT SV_Status, count(distinct SV_Description)*100 as _values "
                    + " FROM tbl_SiteVisit where SV_IdOperation = '"+getSessionId()+"' and SV_Status in (100,200,300) "
                    + " and SV_SiteName = '"+SV_siteName+"' group by SV_Status ";

            Cursor cursor = db.getsitedetails(query);
            int i = 1;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    _status = cursor.getString(0).toString();
                    switch (_status) {
                        case "100": //100*1 :200
                            sitedetail = Float.parseFloat(cursor.getString(1));
                            break;
                         case  "200" :// 11 + (np*2)
                            antenna = Float.parseFloat(cursor.getString(1));
                           // port=Integer.parseInt(cursor.getString(2)) ;
                           // nant=Integer.parseInt(cursor.getString(3))   ;
                            break;
                        case "300": //nbParon/12 (0 to 330) :
                            panoramic = Float.parseFloat(cursor.getString(1))/12;
                            break;
                    }
                } while (cursor.moveToNext());

            }

            map = new HashMap<String, String>();
            map.put("titre", ""); //empty
            map.put("description", "Site Details");
            map.put("values", getSitename()) ;//done or none add

             if (sitedetail==100){
                map.put("header","upload done : "+ Float.toString(sitedetail)+"%"); //empty
            }else{
                map.put("header", "upload inprogress : "+ sitedetail +"%"); //empty
            }

            listItem.add(map);

            map = new HashMap<String, String>();
            map.put("titre", ""); //empty
            map.put("description", "Panoramic");
            map.put("values","0 to 330°") ;

           if (panoramic.equals(100)){
                map.put("header", "upload done : "+ panoramic +"%"); //empty
            }else{
                map.put("header", "upload inprogress : "+ panoramic +"%"); //empty
            }

            listItem.add(map);

            query ="SELECT DISTINCT sitename_id, sitename ,s_configuration ,a_model,a_port,a_marketing_name FROM tbl_siteconfiguration "
                   + " where idoperation='"+getSessionId() +"' and sitename='"+getSitename()+"' order by _id, sitename_id ;";

              cursor = db.getsitedetails(query);
              i = 1;
                if (cursor.moveToFirst())           ///check if exist
                {
                    do {
                    //call functions
                    //antenna
                    int progress=getProgressStatusAntenna(cursor.getString(5).toUpperCase()
                    , Integer.parseInt(cursor.getString(4)),getSitename(),getSessionId());

                    map = new HashMap<String, String>();
                    map.put("titre", ""); //empty
                    //map.put("description", "ANTENNA " + Integer.toString(i));
                    map.put("description", cursor.getString(5));
                    map.put("values",cursor.getString(3) + "; " + cursor.getString(4) + " P.Ports");

                    if (progress==100){
                        map.put("header", "upload done : "+ Integer.toString(progress) +"%"); //empty
                    }else{
                        map.put("header","Upload inprogress : " + Integer.toString(progress) +"%" ); //empty
                    }

                    listItem.add(map);
                            //call antenna prime : where parent=id
                    i++;
                } while (cursor.moveToNext());
            }
            db.close();

           /*     it's move to mainActivity*/
           if (getRoleId().equals("1") || getRoleId().equals("0")) {
                    map = new HashMap<String, String>();
                    map.put("titre", ""); //empty
                    map.put("description", "Validation");
                    map.put("values", "Please give us your comment at the end.");//done or none add
                    map.put("header", ""); //empty
                    listItem.add(map);
          }
                    //((ProgressBar)findViewById(R.id.progressBar)).setProgress(10);
                    SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(), listItem, R.layout.listbuton,
                            new String[]{"titre", "description", "values", "header"}, new int[]{R.id.titre, R.id.description, R.id.values, R.id.header});
                    myListviewbutton.setAdapter(mSchedule);


        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public void getSiteName(){
       strURL=baseUrl + "getSiteconfiguration?sitename="+SV_siteName;
        if (connectiondetector.isConnectingToInternet()){
            new  DownSiteNameTask().execute("sitename");

        }else{
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            libclass.popMessage("Connexion","Oups!! please check your connexion internet!!!");
            return;
        }
    }
    public  void loadingSitedetails(){
      db.open();
        Cursor cursor  =db.getsitedetails("SELECT DISTINCT sitename,idoperation from tbl_siteconfiguration");
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            SV_siteName =cursor.getString(0);
            SV_IdOperation = cursor.getString(1);

        }

        db.close();

        strURL=baseUrl + "getSiteDetailsById?sitename="+SV_siteName+"&idoperation="+SV_IdOperation;
        if (connectiondetector.isConnectingToInternet()){
            new  loadingSitedetailsAsyncTask().execute("sitename");

        }else{

            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            libclass.popMessage("Connexion","Oups!! please check your connexion internet!!!");
            return;
        }
        db.open();
        db.setsitedetails("insert into tbl_loading(status) values('1');");
        db.close();


    }
    public class loadingSitedetailsAsyncTask extends AsyncTask<String, String, String> {
        URL url = null;
        private ProgressDialog dialog = new ProgressDialog(Activity_site_menu.this);

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

                if (result.indexOf("Error") >= 0 || result==null){
                    libclass.popMessage ("Error occur",result);
                    // finish();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            dialog.dismiss();
        }

    }
    public class DownSiteNameTask extends AsyncTask<String, String, String> {
        URL url = null;
        private ProgressDialog dialog = new ProgressDialog(Activity_site_menu.this);

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


                FillConfig (result);
                ///String strSite=parseXmltoString(result);
               // String[] splitSite = strSite.split(";");
               // apiUrl.setListOfSite(splitSite); stockage config dans un var global



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
        }

        // Toast.makeText(getBaseContext(), "Insert Successfull...", Toast.LENGTH_SHORT).show();
        //myListviewbutton = (ListView) findViewById(R.id.lvBbutton);
       //ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
      // HashMap<String, String> map;

        try {

            db.open();

            // listinfo= listinfo.substring(75).replace("</string>","");
            JSONArray jsonArray = new JSONArray(listinfo);
            String[] subs = new String[jsonArray.length()];

            for (int l = 0; l < jsonArray.length(); l++) {
                JSONObject obj = jsonArray.getJSONObject(l);

                String sqlcmd="INSERT INTO tbl_siteconfiguration(sitename_id,sitename,s_latitude,s_longitude,s_configuration,a_technology,a_model,a_port,idoperation,username,a_marketing_name,datecreated) VALUES("
                        + "'"+ obj.getString("Site_id").toString()  + "'"
                        + ",'"+ obj.getString("Site_Name").toString()  + "'"
                        + ",'"+ obj.getString("latitude").toString() + "'"
                        + ",'"+ obj.getString("longitude").toString() + "'"
                        + ",'"+ obj.getString("configuration").toString() + "'"
                        + ",'"+ obj.getString("Ant_technology").toString() + "'"
                        + ",'"+ obj.getString("Ant_model").toString() + "'"
                        + ",'"+ obj.getString("Ant_port").toString() + "'"
                        + ",'"+ SV_IdOperation + "'"
                        + ",'"+ SV_userid + "'"
                        + ",'"+ obj.getString("Ant_fullname").toString() + "'"
                        + ",'"+ SV_DateTime + "'"
                        + ");";

                db.setsitedetails(sqlcmd);

             /*   // if (obj.getString("Name").equals("Roaming")) {
                map = new HashMap<String, String>();
                map.put("titre", ""); //empty
                map.put("description", obj.getString("Site_Name").toString());
                map.put("values", obj.getString("Ant_model").toString());
                map.put("header", ""); //empty
                listItem.add(map);*/
            }

            db.close();

            FillListview();
           // SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem , R.layout.listbuton,
              //     new String[] { "titre","description", "values","header"}, new int[] { R.id.titre,R.id.description, R.id.values,R.id.header});
            // myListviewbutton.setAdapter(mSchedule);
        }

        catch (JSONException e) {
            e.printStackTrace();

        }
    }
    private ArrayList<String>  defaultButton() {
        ArrayList<String> btnlist = new ArrayList<String>();
        btnlist.add("Site Details");
        btnlist.add("Panoramic");
        btnlist.add("Antenna 1");
        btnlist.add("Finish");
        return btnlist;
    }
    private void createLayoutDynamically(Integer n) {
      /*  ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        for (int i = 0; i < n; i++) {

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



            Button myButton = new Button(this);
            myButton.setText("Button :" + i);
            myButton.setId(i);
            // myButton.setTextSize(20);
            myButton.setGravity(Gravity.CENTER);
            myButton.setBackgroundResource(R.drawable.rounded_button);

            final int id_ = myButton.getId();

            LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayour);
            layout.addView(myButton);

            myButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast.makeText(getApplication(),
                            "Button clicked index = " + id_, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

*/
    /* for (int i = 0; i < n; i++) {
            Button myButton = new Button(this);
            myButton.setText("Button :" + i);
            myButton.setId(i);
           // myButton.setTextSize(20);
            myButton.setGravity(Gravity.CENTER);
            myButton.setBackgroundResource(R.drawable.rounded_button);

            final int id_ = myButton.getId();

            LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayour);
            layout.addView(myButton);

            myButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast.makeText(getApplication(),
                            "Button clicked index = " + id_, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
*/

    }
    private  int getProgressStatusAntenna(String strAntenne,int nport ,String siteName , String IdOperation){
        try{
            int progressStatus=0;
            strAntenne=strAntenne.toLowerCase();
            String strcmd=" select  distinct SV_Ports,SV_Description "
            + " from tbl_SiteVisit where SV_IdOperation='"+IdOperation+"' "
            + "  and SV_SiteName='"+siteName+"' and  SV_Status=200 and lower(SV_Antenne)='"+strAntenne+"'";


           Cursor cursor = db.getsitedetails(strcmd);
            if (cursor.getCount() >0){
                progressStatus=cursor.getCount();
                progressStatus=(progressStatus)*100/(11 + (nport*2));

            }

            return  progressStatus;
        }
        catch ( Exception e){
            Log.d("error occure",e.getMessage());
            return  progressStatus;
        }

    }

    String strURL;






}