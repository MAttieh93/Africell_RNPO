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
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import guru.pmvungu.example.com.includes.ConnectionDetector;
import guru.pmvungu.example.com.includes.apiUrl;
import guru.pmvungu.example.com.includes.base64;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getAntModel;
import static guru.pmvungu.example.com.includes.apiUrl.getAntPorts;
import static guru.pmvungu.example.com.includes.apiUrl.getAntenne;
import static guru.pmvungu.example.com.includes.apiUrl.getDevice;
import static guru.pmvungu.example.com.includes.apiUrl.getRoleId;
import static guru.pmvungu.example.com.includes.apiUrl.getSessionId;
import static guru.pmvungu.example.com.includes.apiUrl.getSitename;
import static guru.pmvungu.example.com.includes.apiUrl.getUserId;
import static guru.pmvungu.example.com.includes.apiUrl.setAntPorts;
import static guru.pmvungu.example.com.includes.apiUrl.setUserId;
import static guru.pmvungu.example.com.includes.apiUrl.strListOfAntennaModel;
import static guru.pmvungu.example.com.includes.apiUrl.strListOfSite;

import android.widget.LinearLayout.LayoutParams;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

//public class Activity_antenna extends AppCompatActivity {
public class Activity_antenna extends AppCompatActivity  implements View.OnClickListener {

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

    public TextView photoflag,vsiteflag;
    Button btnUpload ;
    String SV_tech,SV_port,SV_antinfo,SV_portN,SV_antModel,ant_fullname;
    EditText E_portTechnology,E_value, edtCurrPort;
    Spinner S_antinfo,S_ports,S_description;
    NumberPicker numberPicker ;
    final DBAdapter db = new DBAdapter(Activity_antenna.this);
    libClass  _libClass = new libClass(Activity_antenna.this);
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antenna);

        SV_portN=apiUrl.getAntPorts();
        bport=SV_portN.trim();
        SV_antModel=apiUrl.getAntModel();

        SV_Status="200";
        device = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //uploaderBtn = (Button)findViewById(R.id.validateButton);
        //uploaderBtn.setVisibility(View.GONE);
        getPicture1 = (Button)findViewById(R.id.btnCapture1);

        //tvLocation = (TextView)findViewById(R.id.lblLocation);
        photoflag = (TextView)findViewById(R.id.photoflag);
        vsiteflag  = (TextView)findViewById(R.id.siteflag);

        edtCurrPort=(EditText)findViewById(R.id.edtports);
        AutoTextview=(AutoCompleteTextView)findViewById(R.id.edtantennamodel);

      //  numberPicker =(NumberPicker) findViewById(R.id.numberPicker);

        strPersonalPhoto = "";
        intNbrRequestedFace = 0;
        strPersonalPhotoURL = "";
        // sEnableDisable("0");
        photoflag.setText("0");
        vsiteflag.setText("0");
        SV_userid= getUserId();


        try {
            if(checkPermission()){
                //  Toast.makeText(Activity_antenna.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                requestPermission();
            }

            SetInitialize();
           /// pickernumber();

            addListenerOnSpinnerItemSelection();
            getconfig();

            Bundle extras = getIntent().getExtras();
            String title="Antenna";

            if (extras != null) {
                if (extras.containsKey("btnclicked")) {
                    title=getIntent().getStringExtra("btnclicked");
                }

                title=title.toLowerCase().replace("ant","Ant");
                setTitle(title);
                ant_fullname =title;

                // apiUrl.setAntenne(title);
               /* if (extras.containsKey("port")) {
                    tbPort=(getIntent().getStringExtra("port")).split(";");
                    xport=(tbPort[1]).toLowerCase().trim().replace("ports","");
                    apiUrl.setAntPorts(xport);
                    SV_portN=xport; //global ports
                }*/

                if (extras.containsKey("isAntenna")) {
                    boolean isAntenna = extras.getBoolean("isAntenna");
                    if (isAntenna){
                        //antenna
                        isEnable("antenna");
                    }else{
                        isEnable("port");
                        if (extras.containsKey("numero_port")) {
                            SV_port=getIntent().getStringExtra("numero_port");
                            SV_port=SV_port.toLowerCase().replace("port","").trim();
                            int portId=Integer.parseInt(SV_port) ;
                        }
                        // setTitle("Ports");

                    }
                }
            }
            //  fillPorts

        } catch (Exception e){
            e.printStackTrace();
        }
    }

 public void pickernumber(){
    if (numberPicker != null) {
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String text = "Changed from " + oldVal + " to " + newVal;
                //Toast.makeText(Activity_antenna.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
 }
 public void fillPorts(int r) {
        try{


            List<String>listTech=new ArrayList<String>();
            List<String>listElect=new ArrayList<String>();
            List<String>listRrutype=new ArrayList<String>();

            listTech.add("select technology item.");
            listElect.add("select electrical item.");
            listRrutype.add("select TRX type item.");
            db.open();
            Cursor cursor=db.getsitedetails("SELECT distinct label FROM tbl_menu Where objects like 'technology%' and parent like 'antenna%'");
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {
                    listTech.add(cursor.getString(cursor.getColumnIndex("label")));

                }while (cursor.moveToNext());

            }

            cursor=db.getsitedetails("SELECT distinct label FROM tbl_menu Where objects  like 'electrical%' and parent like 'antenna%'");
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {
                    listElect.add(cursor.getString(cursor.getColumnIndex("label")));

                }while (cursor.moveToNext());

            }

            cursor=db.getsitedetails("SELECT distinct label FROM tbl_menu Where objects like 'rrutype%' and parent like 'antenna%'");
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {
                    listRrutype.add(cursor.getString(cursor.getColumnIndex("label")));

                }while (cursor.moveToNext());

            }
            db.close();

            Random rnd = new Random();


            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.lnPort);

            String istrPort="Select Port number";

            int prevTextViewId = 0;
            for (int i = 0; i < r; i++) {
                //check db get portinfo
                int n=i+1;
                String techValues="";
                String rruValues="";
                String elecValues="";
                istrPort =istrPort +";Port "+n;

                int curTextViewId = prevTextViewId + 1;

                int ColorValue = (rnd.nextInt() | 0xff000000);
                //LinearLayout l = new LinearLayout(this);
                // l.setOrientation(LinearLayout.VERTICAL);
                // l.setId(i);

                String nPort="port"+n;
                String xantenne=getAntenne();


//create dynamic control ports
//change port by paire de port
                TextView tvPort = new TextView(this);
                tvPort.setId(100+n);
                tvPort.setText("Paire de Port :"+n);
               // tvPort.setText("Port :"+n);
                tvPort.setTextColor(Color.parseColor("#ffffff"));
                tvPort.setBackgroundColor(Color.parseColor("#870F5C"));
                // tvPort.setPadding(10, 5, 0, 5);// in pixels (left, top, right, bottom)
                tvPort.setTypeface(tvPort.getTypeface(), Typeface.BOLD);


                TextView tvTech = new TextView(this);
                tvTech.setId(400+n);
                tvTech.setText("Technology : " + techValues);
                tvTech.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvTech.setPadding(0,5,0,0);

                Spinner spTech = new Spinner(this);
                spTech.setId(500+n);
                //
                ArrayAdapter<String> adapterTech=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listTech);
                adapterTech.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spTech.setAdapter(adapterTech);

                TextView tvRrutype = new TextView(this);
                tvRrutype.setId(300+n);
                tvRrutype.setText("TRX Type : " + rruValues);
                tvRrutype.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvRrutype.setPadding(0,5,0,0);

                Spinner spRrutype = new Spinner(this);
                spRrutype.setId(600+n);


                ArrayAdapter<String> adapterRrutype=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listRrutype);
                adapterRrutype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spRrutype.setAdapter(adapterRrutype);

                ImageView ivRrutype = new ImageView(Activity_antenna.this);
                ivRrutype.setId(1300+n);
                ivRrutype.setImageResource(R.drawable.addcamera);

                if (!getRoleId().equals("1")){
                    ivRrutype.setOnClickListener(Activity_antenna.this);
                }


                TextView tvEletri = new TextView(this);
                tvEletri.setId(200+n);
                tvEletri.setText("Electrical Titl : " +elecValues);
                tvEletri.setPadding(0,8,0,0);

                Spinner spEletri = new Spinner(this);
                spEletri.setId(700+n);
                //  spEletri.setPadding(0,0,0,20);
                ArrayAdapter<String> adapterEletri=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listElect);
                adapterEletri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spEletri.setAdapter(adapterEletri);

                ImageView ivEletri = new ImageView(Activity_antenna.this);
                ivEletri.setId(1200+n);
                // ivEletri=(ImageView)findViewById(ivRrutype.getId());
                ivEletri.setImageResource(R.drawable.addcamera);
                if (!getRoleId().equals("1")) {
                    ivEletri.setOnClickListener(Activity_antenna.this);
                }


                int width = 600; //RelativeLayout.LayoutParams.WRAP_CONTENT;
                int height =  RelativeLayout.LayoutParams.WRAP_CONTENT;


                RelativeLayout.LayoutParams rlparamPort = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlparamPort.setMargins(0,10,0,0);

                // rlparamPort.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                RelativeLayout.LayoutParams rlparamTextView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlparamTextView.setMargins(0,5,0,0);

                RelativeLayout.LayoutParams rlparamSpinner = new RelativeLayout.LayoutParams(  width, height);
                rlparamSpinner.setMargins(170,5,0,0);


                RelativeLayout.LayoutParams rlparamRRU = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlparamRRU.setMargins(0,10,0,0);

                RelativeLayout.LayoutParams rlparamSpinnerRRU = new RelativeLayout.LayoutParams(width, height);
                rlparamSpinnerRRU.setMargins(170,10,0,0);

                RelativeLayout.LayoutParams rlparamImageViewRRU = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlparamImageViewRRU.setMargins(0,10,0,0);


                RelativeLayout.LayoutParams rlparamElectri = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlparamElectri.setMargins(0,10,0,0);

                RelativeLayout.LayoutParams rlparamSpinnerElectri = new RelativeLayout.LayoutParams(width, height);
                rlparamSpinnerElectri.setMargins(190,10,0,0);

                RelativeLayout.LayoutParams rlparamImageViewElectri = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlparamImageViewElectri.setMargins(0,10,0,0);

                if (i == 0) {
                    // rlparamPort.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    //rlparamPort.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    tvPort.setLayoutParams(rlparamPort);
                    relativeLayout.addView(tvPort);

                    rlparamTextView.addRule(RelativeLayout.BELOW,tvPort.getId());
                    rlparamTextView.addRule(RelativeLayout.ALIGN_LEFT);
                    tvTech.setLayoutParams(rlparamTextView);
                    relativeLayout.addView(tvTech);

                    //rlparamSpinner.setMargins(250,0,0,0);
                    rlparamSpinner.addRule(RelativeLayout.BELOW,tvPort.getId());
                    //rlparamSpinner.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    rlparamSpinner.addRule(RelativeLayout.ALIGN_RIGHT);
                    spTech.setLayoutParams(rlparamSpinner);
                    relativeLayout.addView(spTech);


                    rlparamRRU.addRule(RelativeLayout.BELOW,spTech.getId());
                    rlparamRRU.addRule(RelativeLayout.ALIGN_LEFT);
                    tvRrutype.setLayoutParams(rlparamRRU);
                    relativeLayout.addView(tvRrutype);

                    //rlparamSpinner.setMargins(250,0,0,0);
                    rlparamSpinnerRRU.addRule(RelativeLayout.BELOW,spTech.getId());
                    //rlparamSpinner.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    rlparamSpinnerRRU.addRule(RelativeLayout.ALIGN_RIGHT);
                    spRrutype.setLayoutParams(rlparamSpinnerRRU);
                    relativeLayout.addView(spRrutype);


                    rlparamImageViewRRU.addRule(RelativeLayout.BELOW,spRrutype.getId());
                    // rlparamImageViewRRU.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
                    ivRrutype.setLayoutParams(rlparamImageViewRRU);
                    relativeLayout.addView(ivRrutype);

                    rlparamElectri.addRule(RelativeLayout.BELOW,ivRrutype.getId());
                    rlparamElectri.addRule(RelativeLayout.ALIGN_LEFT);
                    tvEletri.setLayoutParams(rlparamElectri);
                    relativeLayout.addView(tvEletri);


                    rlparamSpinnerElectri.addRule(RelativeLayout.BELOW,ivRrutype.getId());
                    //rlparamSpinner.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    rlparamSpinnerElectri.addRule(RelativeLayout.ALIGN_RIGHT);
                    spEletri.setLayoutParams(rlparamSpinnerElectri);
                    relativeLayout.addView(spEletri);

                    rlparamImageViewElectri.addRule(RelativeLayout.BELOW,spEletri.getId());
                    //rlparamImageViewElectri.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
                    ivEletri.setLayoutParams(rlparamImageViewElectri);
                    relativeLayout.addView(ivEletri);



                } else {
                    //rlparamPort.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    rlparamPort.addRule(RelativeLayout.BELOW, 1200+n-1);
                    tvPort.setLayoutParams(rlparamPort);
                    relativeLayout.addView(tvPort);

                    rlparamTextView.addRule(RelativeLayout.BELOW, 100+n);
                    rlparamTextView.addRule(RelativeLayout.ALIGN_LEFT);
                    tvTech.setLayoutParams(rlparamTextView);
                    relativeLayout.addView(tvTech);

                    //rlparamSpinner.setMargins(250,0,0,0);
                    //rlparamSpinner.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    rlparamSpinner.addRule(RelativeLayout.ALIGN_RIGHT);
                    rlparamSpinner.addRule(RelativeLayout.BELOW, 100+n);
                    spTech.setLayoutParams(rlparamSpinner);
                    relativeLayout.addView(spTech);


                    rlparamRRU.addRule(RelativeLayout.BELOW, 500+n);
                    rlparamRRU.addRule(RelativeLayout.ALIGN_LEFT);
                    tvRrutype.setLayoutParams(rlparamRRU);
                    relativeLayout.addView(tvRrutype);

                    //rlparamSpinner.setMargins(250,0,0,0);
                    //rlparamSpinner.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    rlparamSpinnerRRU.addRule(RelativeLayout.ALIGN_RIGHT);
                    rlparamSpinnerRRU.addRule(RelativeLayout.BELOW, 500+n);
                    spRrutype.setLayoutParams(rlparamSpinnerRRU);
                    relativeLayout.addView(spRrutype);


                    rlparamImageViewRRU.addRule(RelativeLayout.BELOW,600+n);
                    //rlparamImageViewRRU.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
                    ivRrutype.setLayoutParams(rlparamImageViewRRU);
                    relativeLayout.addView(ivRrutype);


                    rlparamElectri.addRule(RelativeLayout.BELOW, 1300+n);
                    rlparamElectri.addRule(RelativeLayout.ALIGN_LEFT);
                    tvEletri.setLayoutParams(rlparamElectri);
                    relativeLayout.addView(tvEletri);


                    //rlparamSpinner.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    rlparamSpinnerElectri.addRule(RelativeLayout.ALIGN_RIGHT);
                    rlparamSpinnerElectri.addRule(RelativeLayout.BELOW, 1300+n);
                    spEletri.setLayoutParams(rlparamSpinnerElectri);
                    relativeLayout.addView(spEletri);

                    rlparamImageViewElectri.addRule(RelativeLayout.BELOW,700+n);
                    // rlparamImageViewElectri.addRule(RelativeLayout.ALIGN_LEFT);
                    ivEletri.setLayoutParams(rlparamImageViewElectri);
                    relativeLayout.addView(ivEletri);

                }
// end

                prevTextViewId = curTextViewId;
                //layout.addView(l,params);
                String sTech="";
                Bitmap bmp;
                byte[] imageAsBytes;

                try{
                    String sqlPort = " SELECT SV_IdOperation,SV_SiteName,SV_Description,SV_Actual,SV_Pictures,SV_Antenne,SV_Ports,SV_Tech FROM tbl_SiteVisit "
                            + " where SV_SiteName='"+getSitename()+"' and SV_IdOperation='"+getSessionId()+"' and SV_Status='200' and LOWER(SV_Antenne)='" + xantenne + "' and replace(LOWER(SV_Ports),' ','') like '"+nPort+"%' order by id,SV_Ports, SV_Description asc;";

                    db.open();
                    Cursor curPort=db.getsitedetails(sqlPort);
                    if (curPort.moveToFirst()) {

                        do {
                            ImageView image ;

                            String sRRu="";
                            String iRRu="";
                            String sElect="";
                            String iElect="";

                            if (sTech ==""){
                                sTech = curPort.getString(7);
                               // tvTech.setText("Technology : " + sTech);
                                spTech.setSelection(adapterTech.getPosition(sTech));
                            }

                            String sdesc=curPort.getString(curPort.getColumnIndex("SV_Description"));
                            String sval=curPort.getString(curPort.getColumnIndex("SV_Actual"));
                            if(sdesc.toLowerCase().indexOf("trx")>=0){
                                //if(sdesc.toLowerCase().indexOf("rru")>=0){
                                //tvRrutype.setText("RRU Type : " + sval);
                                spRrutype.setSelection(adapterRrutype.getPosition(sval));

                                iRRu= curPort.getString(curPort.getColumnIndex("SV_Pictures"));
                                imageAsBytes = Base64.decode(iRRu.getBytes(), Base64.DEFAULT);
                                bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                                image=(ImageView)findViewById(ivRrutype.getId());
                                image.setAdjustViewBounds(true);
                                image.setImageBitmap(bmp);
                                image.setPadding(0, 5, 0, 0);

                            }else{
                               // tvEletri.setText("Electrical Tilt : " + sval);
                                spEletri.setSelection(adapterEletri.getPosition(sval));

                                iElect=curPort.getString(4);
                                tvEletri.setPadding(0, 5, 0, 0);// in pixels (left, top, right, bottom)
                                imageAsBytes = Base64.decode(iElect.getBytes(), Base64.DEFAULT);
                                bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                                image=(ImageView)findViewById(ivEletri.getId());
                                image.setAdjustViewBounds(true) ;
                                image.setImageBitmap(bmp);
                            }

                        } while (curPort.moveToNext());
                    }
                    db.close();
                    //rru
                }
                catch (Exception e){
                    db.close();
                    e.printStackTrace();
                }
            }
            strPort=istrPort.split(";");

         /*
          int prevTextViewId = 0;
            int idport= 0;
            int idtech= 0;

            for (int i = 0; i < r; i++) {

               int ColorValue = (rnd.nextInt() | 0xff000000);

                final TextView tv_port = new TextView(this);
                final TextView tv_technology = new TextView(this);
                final TextView tv_item = new TextView(this);
                final TextView tv_itemval = new TextView(this);
                final ImageView imageview = new ImageView(this);

                tv_port.setText("Port " + i);
                tv_technology.setText("Technology " + i);

                tv_port.setTextColor(Color.parseColor("#ffffff"));
              //  tv_port.setBackgroundColor(Color.parseColor("#870F5C"));
                tv_port.setBackgroundColor(rnd.nextInt() | 0xff000000);

                tv_port.setGravity(Gravity.CENTER);
                tv_port.setTextSize(18);
                tv_port.setTypeface(tv_port.getTypeface(), Typeface.BOLD);
               // tv_port.setTextColor(rnd.nextInt() | 0xff000000);

                int curTextViewId = prevTextViewId + 1;
                  idport= idtech+1;
                  idtech= idport+1;

                tv_port.setId(curTextViewId);
                tv_technology.setId(idtech);

              //  textView.setId(curTextViewId);

                final RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);


                final RelativeLayout.LayoutParams params1 =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                    //if(i==0) {
                      // params.addRule(RelativeLayout.BELOW, idport);
                  //  }else{
                        params.addRule(RelativeLayout.BELOW,idport-1);
                  //  }

               // tv_port.setPadding(0, (i * 10), 0, 0);
                params.addRule(RelativeLayout.BELOW, prevTextViewId);
                tv_port.setLayoutParams(params);

                prevTextViewId = curTextViewId;

                layout.addView(tv_port, params);

              //  params1.addRule(RelativeLayout.BELOW, idport );
               // tv_technology.setLayoutParams(params1);
               // layout.addView(tv_technology, params1);


               // params.addRule(RelativeLayout.BELOW, prevTextViewId);
               // textView.setLayoutParams(params);

            }
        */

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    String[] strPort = "select ports;Port 1;Port 2;Port 3;Port 4".split(";");
    public String strURLInsertRegistration;
    public void isEnable(String strValue){
      /*if (strValue =="antenna"){
           E_portTechnology.setVisibility(View.GONE);
           S_ports.setVisibility(View.GONE);

           S_description.setVisibility(View.VISIBLE);
           E_value.setVisibility(View.VISIBLE);
           S_antinfo.setVisibility(View.VISIBLE);

           addItemsOnSpinner2(S_description, "antenna",  R.array.array_ant_description);

      }else{
          E_portTechnology.setVisibility(View.VISIBLE);
          S_ports.setVisibility(View.VISIBLE);

          S_description.setVisibility(View.VISIBLE);
          E_value.setVisibility(View.VISIBLE);
          S_antinfo.setVisibility(View.VISIBLE);
          addItemsOnSpinner2(S_description, "antenna",  R.array.array_port_description);

      }
      */
    }
    public void callForm(Context context,Class activity,String btnClicked,String numero_port,Boolean isAntenna){
        Intent intent = new Intent(context,activity);
        intent.putExtra("btnclicked", btnClicked);
        intent.putExtra("isAntenna", isAntenna);
        intent.putExtra("numero_port", numero_port);
        startActivity(intent);
        this.finish();
    }
    public void addItemsOnSpinner2(Spinner spinnerAdapter,String itemSelected, int ArrayRes) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, ArrayRes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(adapter);

    }
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
    public void addItemsOnSpinner3(Spinner spinnerAdapter,String itemSelected, String[] ArrayRes) {
        ArrayAdapter  adapterDevise = new ArrayAdapter(this, android.R.layout.simple_spinner_item,ArrayRes);
        adapterDevise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setAdapter(adapterDevise);

    }
    public void addListenerOnSpinnerItemSelection() {

    ///after select spinner forfait
        /*
        S_antinfo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Spinner txtSelected = (Spinner) findViewById(R.id.txtForfatid);
                String item =parent.getItemAtPosition(position).toString();

                if (item.toLowerCase().indexOf("antenna")>=0) {
                      isEnable("antenna");
                     addEmptyItemsOnSpinner(S_ports,"Items",0);
                }else if (item.toLowerCase().indexOf("ports")>=0) {
                    isEnable("port");
                    addItemsOnSpinner3(S_ports,"ports",strPort);
                }else{
                   String empt[]={"items"};
                    addItemsOnSpinner3(S_ports,"select items",empt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        S_description.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Spinner txtSelected = (Spinner) findViewById(R.id.txtForfatid);
                String item =parent.getItemAtPosition(position).toString();
               String info= S_antinfo.getSelectedItem().toString();
                if (item.toLowerCase().indexOf("items")>=0) {
                    E_value.setHint("Value");

                }else {
                    E_value.setHint(item + " value");
                    if (item.toLowerCase().indexOf("ports") >=0){
                        E_value.setText(SV_portN.trim());
                        E_value.setEnabled(false);
                    }

                   // addItemsOnSpinner3(S_ports,"ports",strPort);



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

 */
     // EditText edtCurrPort=(EditText)findViewById(R.id.edtports);

  //editonchange
/*
        edtCurrPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 bport=(edtCurrPort.getText()).toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.equals("") && s.length() > 0 && !s.equals("0") ) {
                    if(!s.equals(bport)) {
                    fillPorts(Integer.parseInt(s.toString().trim()));

                }else{
                          Toast.makeText(getApplication(),"pas de chanegement " +s.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

*/
        spsector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Spinner txtSelected = (Spinner) findViewById(R.id.txtForfatid);
                String item =parent.getItemAtPosition(position).toString();
                if (item.toLowerCase().indexOf("oui")>=0) {
                   enabled(true);
                }else{
                    enabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getPicture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureNbr=1;
                setPictures(pictureNbr);
            }
        });

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
    public void onValidation(String strvalidation){
        connector = new ConnectionDetector(getApplicationContext());

        // TextView lblError = (TextView) findViewById(R.id.lblError);
        // boolean isConnected = isNetworkAvailable();

        if (connector.isConnectingToInternet()){
            //lblError.setText("connected");
        }else{
            //lblError.setText("Ouups!!! probleme de connection.");
            showAlertDialog(Activity_antenna.this, "No Internet Connection",
                    "Ouups!!! probleme de connection.", false);
            return;
        }


        if (blnCheckDataBeforeUpload() == true){
            SV_Pictures=strPersonalPhoto;
            strURLInsertRegistration=baseUrl + "InsertSitevisit";
            new  ImageUploadTaskAsync().execute(SV_IdOperation,SV_SiteName, SV_Description,SV_Pictures,SV_userid,SV_Device,SV_Status,SV_Actual,SV_SiteConfig,SV_Antenne,SV_Ports);

        }

    }
    public String strBtnClick,portnumber;
    public  Boolean isAnt;
    public boolean blnCheckDataBeforeUpload() {

        try{
            SV_Description = strAntenne.toString(); //ruutype,electrical tilt
            SV_Actual="";

            TextView txtFlagImage = (TextView)findViewById(R.id.photoflag);
            String _FlagImage = txtFlagImage.getText().toString();
            if (!_FlagImage.equals("1")){
                // Toast.makeText(this, "Veuillez prendre la photo avant.", Toast.LENGTH_LONG).show();
                //lblError.setText("Please take a picture before upload!");

                showAlertDialog(Activity_antenna.this, "Photo",
                        "Please take a picture before upload!", false);
                return false;
            }

            if ( strPersonalPhoto.equals("")){
                showAlertDialog(Activity_antenna.this, "Photo",
                        "Please take a picture before upload!", false);
                return  false;
            }
            SV_Pictures=strPersonalPhoto;

            //only for ports
            SV_Antenne=apiUrl.getAntenne();
            strBtnClick=SV_Antenne;
            portnumber="1";
            isAnt=true;

            if(sPort.toLowerCase().indexOf("port") >=0){
                SV_port=sPort;
                if (SV_port.equals("")){
                    showAlertDialog(Activity_antenna.this, "Antenna:" +sPort,
                            "Please fill port!", false);
                    return  false;
                }

                int id=Integer.parseInt(sPort.toLowerCase().replace("port","").trim());
                int idTech=500 + id;
                Spinner technology=(Spinner)findViewById(idTech);

                SV_SiteConfig=technology.getSelectedItem().toString();
                SV_Tech=technology.getSelectedItem().toString();
                if (SV_SiteConfig.equals("")|| SV_SiteConfig.indexOf("select") >=0 ){
                    showAlertDialog(Activity_antenna.this, "Antenna:" +sPort,"Please select the Technology!", false);
                    return  false;
                }

                int idSpinner=0;
                String strText="";
                if (String.valueOf(ivId).startsWith("30")){
                    //600
                    idSpinner=600+id;
                    strText ="Please select the RRU Type !";
                }else  if (String.valueOf(ivId).startsWith("20")){
                    //700
                    idSpinner=700+id;
                    strText ="Please select Electrical Tilt.!";
                }

                Spinner RrutypeElect=(Spinner)findViewById(idSpinner);
                SV_Actual=RrutypeElect.getSelectedItem().toString();
                if (SV_Actual.equals("") || SV_Actual.indexOf("select") >=0){
                    showAlertDialog(Activity_antenna.this, "Antenna:" +sPort, strText, false);
                    return  false;
                }
                SV_Ports=SV_port;

                // strBtnClick="port";
                portnumber=SV_port;
                isAnt=false;
            }else {
                if (edId==1){ //
                    SV_Actual="";
                }else if (edId==2){//spinner
                    Spinner actual  =(Spinner)findViewById(spId);
                    SV_Actual=actual.getSelectedItem().toString();
                    if (SV_Actual.equals("") || SV_Actual.equals("-")){
                       /// actual.setError("Please Fill "+strAntenne+" field !!!");
                        showAlertDialog(Activity_antenna.this, "Antenna view :" , "Please Fill "+strAntenne+" field !", false);
                        return  false;
                    }

                }else if(edId==3){//AutoCompleteTextview
                    AutoCompleteTextView actual =(AutoCompleteTextView)findViewById(spId);
                    SV_Actual=actual.getText().toString();
                    if (SV_Actual.equals("")){
                        actual.setError("Please Fill "+strAntenne+" field !!!");
                        showAlertDialog(Activity_antenna.this, "Antenna view :" , "Please Fill "+strAntenne+" field !", false);

                        return  false;
                    }

                }else{//edittext
                     EditText actual  =(EditText)findViewById(edId);
                    SV_Actual=actual.getText().toString();
                    if (SV_Actual.equals("")){
                        actual.setError("Please Fill "+strAntenne+" field !!!");
                        showAlertDialog(Activity_antenna.this, "Antenna view :" , "Please Fill "+strAntenne+" field !", false);
                        return  false;
                    }
                }

            }

            return true;
        }catch (Exception e){
            showAlertDialog(Activity_antenna.this,"error occur", e.getMessage(),false);
            return  false;
        }
    }
    public class  ImageUploadTaskAsync extends AsyncTask<String,String,String> {
        private ProgressDialog dialog = new ProgressDialog(Activity_antenna.this);
        HttpsURLConnection conn;
        URL url = null;
        SSLContext sslcontext;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait while uploading...");
            //  Button btnUpload = (Button)findViewById(R.id.validateButton);
            //  btnUpload.setVisibility(View.GONE);
            dialog.setCancelable(false);
            dialog.show();
        }

        protected String doInBackground(String... params) {
            try {
                url = new URL(strURLInsertRegistration);
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
               // conn.setDoInput(true);
                //conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("SV_IdOperation", params[0])
                        .appendQueryParameter("SV_SiteName", params[1])
                        .appendQueryParameter("SV_Description", params[2])
                        .appendQueryParameter("SV_Pictures", params[3])
                        .appendQueryParameter("SV_userid", params[4])
                        .appendQueryParameter("SV_Device", params[5])
                        .appendQueryParameter("SV_Status", params[6])
                        .appendQueryParameter("SV_Actual", params[7])
                        .appendQueryParameter("SV_SiteConfig", params[8])
                        .appendQueryParameter("SV_Antenne", params[9])
                        .appendQueryParameter("SV_Ports", params[10]);
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
                return "error occur : "+e.getMessage();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {
            // Toast.makeText(Activity_login.this, result, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.e("SmsReceiver",result);

            // Button btnUpload = (Button)findViewById(R.id.validateButton);
            // btnUpload.setVisibility(View.VISIBLE);

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
                    String sqlcmd="INSERT INTO tbl_SiteVisit (SV_IdOperation,SV_SiteName,SV_Description,SV_Pictures,SV_UserId,SV_Device,SV_Status,SV_Actual,SV_SiteConfig,SV_Antenne,SV_Ports,SV_Tech) values ('"+SV_IdOperation+"','"+SV_SiteName+"', '"+SV_Description+"','"+SV_Pictures+"','"+SV_userid+"','"+SV_Device+"','"+SV_Status+"','"+SV_Actual+"','"+SV_SiteConfig+"' , '"+SV_Antenne+"','"+SV_Ports+"','"+SV_Tech+"')";
                    db.setsitedetails(sqlcmd);
                    db.close();

                }catch (Exception e){
                    db.close();

                    showAlertDialog(Activity_antenna.this, "Antenna view",
                            e.toString(), false);
                    return;
                }
                // alertMsg(Activity_panoramic.this,"", "Upload successfully.");
                alertNotification(Activity_antenna.this,"Antenna view ","Upload successfully.\n",Activity_antenna.class,strBtnClick,portnumber,isAnt);
                // finish();
                return;
            }

            showAlertDialog(Activity_antenna.this, "Antenna view : "+getAntenne(),
                    result.toString(), false);
            return;

        }
    }
    public void alertNotification(Context context, String title, String msg, final Class activity,final String strBtnClicked,final String numero_port,final Boolean isAntenne){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
         builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callForm(Activity_antenna.this,activity,strBtnClicked,numero_port,isAntenne);

            }
        });


        AlertDialog alert = builder.create();
        alert.show();
        return;
    }
    public void getconfig(){
        try{
           //  db.open();
           /* Cursor curSiteDetails = db.getsitedetails("SELECT distinct SV_IdOperation,SV_SiteName,SV_UserId,SV_Device from tbl_SiteVisit " +
                    " where SV_Status='100' and SV_IdOperation='"+getSessionId() +"' and SV_SiteName='"+getSitename()+"';");

           if (curSiteDetails.getCount() >0){
                curSiteDetails.moveToFirst();
                SV_IdOperation=curSiteDetails.getString(0);
                SV_SiteName=curSiteDetails.getString(1);
                SV_userid=curSiteDetails.getString(2);
               // SV_userid =getUserId();
                SV_Device=curSiteDetails.getString(3);
            }else{
                String msg="Please fill site details !!! ";
               //active alertNotification(Activity_antenna.this,"Antenna view :",msg.toString(),Activity_site_menu.class,"","",false);
*/
            SV_IdOperation=getSessionId();
            SV_SiteName=getSitename();
            SV_userid =getUserId();
            SV_Device=getDevice();
         /*   }

           db.close();*/

            fillAntennaView();

        }catch (Exception e){
            e.printStackTrace();
            alertNotification(Activity_antenna.this,"Antenna view :",e.toString(),Activity_site_menu.class,"","",false);
        }
    }
    public ImageView image;
    public void fillAntennaView(){

        try {
            //get current antanna port
            String xantenne = getAntenne();
            EditText edtport = (EditText) findViewById(R.id.edtports);
            edtport.setText(getAntPorts());
            //get current antanna model
            AutoCompleteTextView edtAnt=(AutoCompleteTextView)findViewById(R.id.edtantennamodel);
            edtAnt.setText(getAntModel());

            db.open();
            Cursor curAnt;
            String sqlinfo = " SELECT SV_IdOperation,SV_SiteName,SV_Description,SV_Actual,SV_Pictures,SV_Antenne,SV_Ports FROM tbl_SiteVisit "
                    + " where SV_Status='200' and SV_IdOperation='"+SV_IdOperation+"' and  SV_SiteName='"+getSitename()+"'  and  LOWER(SV_Antenne)='" + xantenne + "' and SV_ports not like 'Port%' order by id, SV_Description asc;";

            curAnt = db.getsitedetails(sqlinfo);

            if (curAnt.moveToFirst()) {

                do {

                    SV_IdOperation = curAnt.getString(0);
                    SV_SiteName = curAnt.getString(1);
                    SV_Description = curAnt.getString(2);
                    SV_Actual = curAnt.getString(3);
                    SV_Pictures = curAnt.getString(4);
                    SV_Antenne = curAnt.getString(5);
                    SV_Ports = curAnt.getString(6);

                    String base = SV_Pictures;
                    String imgId = SV_Description.toLowerCase().trim().replace(" ","");
                    TextView tview;
                    EditText eview;
                    int color=Color.parseColor("#331f00");

                    if (imgId.indexOf("antennamodel") >= 0) {
                        image = (ImageView) findViewById(R.id.ivAntmodel);
                        tview = (TextView) findViewById(R.id.antennamodel);
                        tview.setTextColor(color);
                       // eview = (EditText) findViewById(R.id.edtantennamodel);
                        AutoCompleteTextView aview = (AutoCompleteTextView)findViewById(R.id.edtantennamodel);
                        aview.setText(SV_Actual);

                    } else if (imgId.indexOf("antennalabel") >= 0) {
                        image = (ImageView) findViewById(R.id.ivAntlabel);
                        tview = (TextView) findViewById(R.id.labelafricell);
                        tview.setTextColor(color);
                        eview = (EditText) findViewById(R.id.edtlabelafricell);
                        eview.setText(SV_Actual);
                    } else if (imgId.indexOf("pairedeport") >= 0) {
                        image = (ImageView) findViewById(R.id.ivPorts);
                        tview = (TextView) findViewById(R.id.ports);
                        tview.setTextColor(color);
                        eview = (EditText) findViewById(R.id.edtports);
                        int oPorts=Integer.parseInt(SV_Actual.trim());
                        /*int opp=0 ;
                        if (oPorts  > 1 ){
                            opp=oPorts/2;
                        }else{
                            opp =oPorts;
                        }*/

                        eview.setText(String.valueOf(oPorts));


                    } else if (imgId.indexOf("azimuth") >= 0) {
                        image = (ImageView) findViewById(R.id.ivAzimuth);
                        tview = (TextView) findViewById(R.id.azmuth);
                        eview = (EditText) findViewById(R.id.edtazmuth);
                        eview.setText(SV_Actual);
                        tview.setTextColor(color);
                    } else if (imgId.indexOf("height") >= 0) {
                        image = (ImageView) findViewById(R.id.ivHeight);
                        tview = (TextView) findViewById(R.id.height);
                        eview = (EditText) findViewById(R.id.edtheight);
                        eview.setText(SV_Actual);
                        tview.setTextColor(color);
                    } else if (imgId.indexOf("antennaview") >= 0) {
                        image = (ImageView) findViewById(R.id.ivAntview);
                        tview = (TextView) findViewById(R.id.antennaview);
                        tview.setTextColor(color);
                        // tview.setText(tview.getText());
                    } else if (imgId.indexOf("sectorobstruction") >= 0) {
                        image = (ImageView) findViewById(R.id.iostruction);
                        tview = (TextView) findViewById(R.id.sectorobstruction);
                        tview.setTextColor(color);

                        if (SV_Actual.toLowerCase().equals("oui"))  {
                           enabled(true);
                        }else{
                            enabled(false);
                        }

                        // tview.setText(tview.getText());
                    } else if (imgId.indexOf("mechanical") >= 0) {
                        image = (ImageView) findViewById(R.id.ivmechanicaltilt);
                        tview = (TextView) findViewById(R.id.mechanicaltilt);
                        spmechanicaltilt.setSelection(getIndex(spmechanicaltilt,SV_Actual));
                        tview.setTextColor(color);
                        // tview.setText(tview.getText());

                    }  else if (imgId.indexOf("brachet") >= 0) {
                        image = (ImageView) findViewById(R.id.ivbracket);
                        tview = (TextView) findViewById(R.id.bracket);
                        Spinner  spbracket = (Spinner)findViewById(R.id.spbracket);
                        spbracket.setSelection(getIndex(spbracket,SV_Actual));
                        tview.setTextColor(color);
                    // tview.setText(tview.getText());
                }   else if (imgId.indexOf("ground") >= 0) {
                        image = (ImageView) findViewById(R.id.ivground);
                        tview = (TextView) findViewById(R.id.sectorobstructionground);
                        tview.setTextColor(color);
                    // tview.setText(tview.getText());
                } else if (imgId.indexOf("tower") >= 0) {
                        image = (ImageView) findViewById(R.id.ivsectortower);
                        tview = (TextView) findViewById(R.id.sectorobstructiontower);
                        tview.setTextColor(color);
                        // tview.setText(tview.getText());
                }

                    byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
                    Bitmap bmp;
                    bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    image.setPadding(15,0,15,0);
                    image.setAdjustViewBounds(true);
                    image.setImageBitmap(bmp);

                } while (curAnt.moveToNext());
            }
            db.close();
            int por=Integer.parseInt(SV_portN.trim()); //paire de port
            if (por > 0 ){
               fillPorts(por);
            }

        }catch (Exception e){
            showAlertDialog(Activity_antenna.this,"Antenne view","error occur"+e.getMessage(),false);
        }

    }
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
    public void enabled(Boolean bln){
    try {
        TextView separator = (TextView) findViewById(R.id.separator);

        RelativeLayout.LayoutParams lyparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        ImageView itower = (ImageView) findViewById(R.id.ivsectortower);
        ImageView obstruction = (ImageView) findViewById(R.id.iostruction);


        if (bln) {
            tvgrounglevel.setVisibility(View.VISIBLE);
            tvtowerlevel.setVisibility(View.VISIBLE);
            ivgrounglevel.setVisibility(View.VISIBLE);
            ivtowerlevel.setVisibility(View.VISIBLE);
            ((View) findViewById(R.id.Linea7)).setVisibility(View.VISIBLE);
            ((View) findViewById(R.id.Linea8)).setVisibility(View.VISIBLE);
            spsector.setSelection(new libClass(this).getIndex(spsector, "Oui"));
            lyparams.addRule(RelativeLayout.BELOW, itower.getId());
            // lyparams.setMargins(0,10,0,0);

        } else {
            tvgrounglevel.setVisibility(View.GONE);
            tvtowerlevel.setVisibility(View.GONE);
            ivgrounglevel.setVisibility(View.GONE);
            ivtowerlevel.setVisibility(View.GONE);
            ((View) findViewById(R.id.Linea7)).setVisibility(View.GONE);
            ((View) findViewById(R.id.Linea8)).setVisibility(View.GONE);
            spsector.setSelection(getIndex(spsector, "Non"));

            lyparams.addRule(RelativeLayout.BELOW, obstruction.getId());
        }

        separator.setLayoutParams(lyparams);

            }catch (Exception e){
                 Log.e("Error",e.getMessage());
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
    private void requestPermission() {
        ActivityCompat.requestPermissions(Activity_antenna.this, new String[]
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

                            Toast.makeText(Activity_antenna.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        } else {
                            //  Toast.makeText(Activity_deviceposition.this,"Permission Denied" +grantResults[2],Toast.LENGTH_LONG).show();

                        }
                    }

                    break;

            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Activity_antenna.this,"Permission Denied " +e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    String flagphoto="0";
    public void setPictures(int pictureNbr){
        //set pictures
        CapturePersonalPhoto();
        //  Toast.makeText(Activity_deviceposition.this, "click on "+pictureNbr, Toast.LENGTH_LONG).show();

    }
    public void CapturePersonalPhoto(){
        try{
            //  Button btn = (Button) findViewById(R.id.validateButton);
            //btn.setVisibility(View.GONE);

            mPersonalPhoto = (ImageView) findViewById(R.id.imgpicture);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            PersonalPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, PersonalPhotoUri);
            startActivityForResult(intent, CAMERA_PIC_REQUEST_PersonalPhoto);


        } catch (Exception e){
            e.printStackTrace();
        }
    }
    protected  void onActivityResult(int requestCode , int resultCode, Intent data){
        super.onActivityResult(resultCode,resultCode,data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            android.database.Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
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


                    ExifInterface exifReader = new ExifInterface(strPersonalPhotoURL);
                    int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                    int rotate = 0;
                    bitmap=libClass.rotateBitmap(bitmap,orientation);

                    thumbnail = bitmap;


                    //  ImageView img;
                    // Bitmap bmp;

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
                    onValidation("antenne");
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
    public void SetInitialize() {
        tvantennamodel =(TextView)findViewById(R.id.antennamodel);
        tvantlabel =(TextView)findViewById(R.id.labelafricell);
        tvports =(TextView)findViewById(R.id.ports);
        tvazimuth =(TextView)findViewById(R.id.azmuth);
        tvHeight =(TextView)findViewById(R.id.height);
        tvantview =(TextView)findViewById(R.id.antennaview);

        tvsectorobstruction =(TextView)findViewById(R.id.sectorobstruction);
        tvmechanical=(TextView)findViewById(R.id.mechanicaltilt);
        tvbracket=(TextView)findViewById(R.id.bracket);
        tvgrounglevel=(TextView)findViewById(R.id.sectorobstructionground);
        tvtowerlevel=(TextView)findViewById(R.id.sectorobstructiontower);

        ivantennamodel =(ImageView)findViewById(R.id.ivAntmodel);
        ivantlabel =(ImageView)findViewById(R.id.ivAntlabel);
        ivports =(ImageView)findViewById(R.id.ivPorts);
        ivazimuth =(ImageView)findViewById(R.id.ivAzimuth);
        ivHeight =(ImageView)findViewById(R.id.ivHeight);
        ivantview =(ImageView)findViewById(R.id.ivAntview);
        ivsectorobstruction =(ImageView)findViewById(R.id.iostruction);
        ivmechanical=(ImageView)findViewById(R.id.ivmechanicaltilt);
        ivbracket=(ImageView)findViewById(R.id.ivbracket);
        ivgrounglevel=(ImageView)findViewById(R.id.ivground);
        ivtowerlevel=(ImageView)findViewById(R.id.ivsectortower);

        spsector=(Spinner)findViewById(R.id.spsectorobstruction);
       // spsector.setEnabled(false);

        ivantennamodel.setImageResource(R.drawable.addcamera);
        ivantlabel.setImageResource(R.drawable.addcamera);
        ivports.setImageResource(R.drawable.addcamera);
        ivazimuth.setImageResource(R.drawable.addcamera);
        ivHeight.setImageResource(R.drawable.addcamera);
        ivantview.setImageResource(R.drawable.addcamera);
        ivsectorobstruction.setImageResource(R.drawable.addcamera);
        ivmechanical.setImageResource(R.drawable.addcamera);
        ivbracket.setImageResource(R.drawable.addcamera);
        ivgrounglevel.setImageResource(R.drawable.addcamera);
        ivtowerlevel.setImageResource(R.drawable.addcamera);

            switch (getRoleId()){
                case "0": case "2":
                    ivantennamodel.setOnClickListener(this);
                    ivantlabel.setOnClickListener(this);
                    ivports.setOnClickListener(this);
                    ivazimuth.setOnClickListener(this);
                    ivHeight.setOnClickListener(this);
                    ivantview.setOnClickListener(this);
                    ivsectorobstruction.setOnClickListener(this);
                    ivmechanical.setOnClickListener(this);
                    ivbracket.setOnClickListener(this);
                    ivgrounglevel.setOnClickListener(this);
                    ivtowerlevel.setOnClickListener(this);
                    break;
                case "1": //tech

                default:


            }

        /*    if (!getRoleId().equals("1")){
            ivantennamodel.setOnClickListener(this);
            ivantlabel.setOnClickListener(this);
            ivports.setOnClickListener(this);
            ivazimuth.setOnClickListener(this);
            ivHeight.setOnClickListener(this);
            ivantview.setOnClickListener(this);
            ivsectorobstruction.setOnClickListener(this);
            ivmechanical.setOnClickListener(this);
            ivbracket.setOnClickListener(this);
            ivgrounglevel.setOnClickListener(this);
            ivtowerlevel.setOnClickListener(this);

            //spsector.setOnClickListener(this);
        };
     */
       // enabled(false);
        spmechanicaltilt=(Spinner)findViewById(R.id.spmechanicaltilt);
        fillspinner(spmechanicaltilt,"mechanical","antenna");

        new libClass(this).fillAutoComplete(AutoTextview,strListOfAntennaModel);
        //new libClass(this).fillAutoComplete(AutoTextview,strListAntenna);
    }
    public void fillspinner(Spinner spinner,String obj,String parent){
        try {
            List<String> listspinner = new ArrayList<String>();
            listspinner.add("-");
            db.open();
            Cursor cursor = db.getsitedetails("SELECT distinct label FROM tbl_menu Where objects like '" + obj + "%' and parent like '" + parent + "%'");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    listspinner.add(cursor.getString(cursor.getColumnIndex("label")));

                } while (cursor.moveToNext());

            }
            db.close();
            ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listspinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

        }catch (Exception e){
            Log.e("Error",e.getMessage());
        }
    }
    @Override
    public void onClick(View v) {
        try {
            int nPort=Integer.parseInt(SV_portN.trim());
            ivId=0;
            edId=0;
            spId=0;

            sPort="";
            TextView tv;
            ImageView iv;
            strAntenne="";

            int tvId = v.getId();
            //Toast.makeText(getApplication(), String.valueOf(tvId),Toast.LENGTH_LONG).show();

            switch(v.getId()) {
                case R.id.ivmechanicaltilt:
                    strAntenne=tvmechanical.getText().toString();
                    intAntenne=tvmechanical.getId();
                    ivId=tvmechanical.getId();
                    edId=2;
                    spId=R.id.spmechanicaltilt;
                    break;
                case R.id.ivbracket:
                    strAntenne=tvbracket.getText().toString();
                    intAntenne=tvbracket.getId();
                    ivId=tvbracket.getId();
                    edId=2;
                    spId=R.id.spbracket;
                    break;
                case R.id.ivground:
                    strAntenne=tvgrounglevel.getText().toString();
                    intAntenne=tvgrounglevel.getId();
                    ivId=ivgrounglevel.getId();
                    edId=1;
                    break;
                case R.id.ivsectortower:
                    strAntenne=tvtowerlevel.getText().toString();
                    intAntenne=tvtowerlevel.getId();
                    ivId=ivtowerlevel.getId();
                    edId=1;
                    break;
                case R.id.ivAntmodel:
                    strAntenne=tvantennamodel.getText().toString();
                    intAntenne=tvantennamodel.getId();
                    ivId=ivantennamodel.getId();
                    spId=R.id.edtantennamodel ;//((EditText)findViewById(R.id.edtantennamodel)).getId();
                    edId=3;
                    break;
                case R.id.ivAntlabel:
                    strAntenne=tvantlabel.getText().toString();
                    intAntenne=tvantlabel.getId();
                    ivId=tvantlabel.getId();
                    edId=R.id.edtlabelafricell;
                    break;
                case R.id.ivPorts:
                    strAntenne=tvports.getText().toString();
                    intAntenne=tvports.getId();
                    ivId=tvports.getId();
                    edId=R.id.edtports;
                    break;
                case R.id.ivAzimuth:
                    strAntenne=tvazimuth.getText().toString();
                    intAntenne=tvazimuth.getId();
                    ivId=tvazimuth.getId();
                    edId=R.id.edtazmuth;
                    break;
                case R.id.ivHeight:
                    strAntenne=tvHeight.getText().toString();
                    intAntenne=tvHeight.getId();
                    ivId=tvHeight.getId();
                    edId=R.id.edtheight;
                    break;
                case R.id.ivAntview:
                    strAntenne=tvantview.getText().toString();
                    intAntenne=tvantview.getId();
                    ivId=tvantview.getId();
                    edId=1;
                    break;
                case R.id.iostruction:
                    strAntenne=tvsectorobstruction.getText().toString();
                    intAntenne=tvsectorobstruction.getId();
                    ivId=tvsectorobstruction.getId();
                    edId=2;
                    spId=R.id.spsectorobstruction;
                    break;

                default :
                    Log.e("image clicked : ",tvId + " " +strAntenne);
                    try{
                        //dynamic port imageview
                        if (tvId  > 1200 && tvId  < 1301 + nPort){
                            int txId=Integer.parseInt(String.valueOf(tvId).substring(1,4));
                           // Toast.makeText(getApplication(), String.valueOf(txId),Toast.LENGTH_LONG).show();

                            strAntenne = ((TextView)findViewById(txId)).getText().toString(); // tvantennamodel.getText().toString();
                            intAntenne=txId;//  Integer.parseInt(String.valueOf(tvId).substring(1,4));
                            ivId=intAntenne;
                            sPort="Port " +String.valueOf(tvId).substring(3,4);

                            // Toast.makeText(getApplication(), sPort,Toast.LENGTH_LONG).show();
                        }else{
                            //static textview
                            // tv = (TextView)v;
                            //   strAntenne = tv.getText().toString();
                            //intAntenne=v.getId();

                        }


                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error occur:"+e.toString()+"\n session failed.." , Toast.LENGTH_LONG).show();
                        return;
                    }

                    // mPersonalPhoto=(ImageView)findViewById(intPanoramic);

            }


            Log.e("image clicked : ",tvId + " " +strAntenne);

            /* upload record
               popupTakepictures(Activity_antenna.this,"Pictures","Would you like to take a new picture \n"+strAntenne +"?");
             */
            pictureNbr = 1;
            setPictures(pictureNbr);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        switch (getRoleId()){  //0 : admin  2 :user
            case "0": case "2":
                getMenuInflater().inflate(R.menu.menu_addnewsite, menu);
                break;
            case "1": //tech
                break;
            default:

        }

       /* if (!getRoleId().equals("1")) {
            getMenuInflater().inflate(R.menu.menu_addnewsite, menu);
        }*/
        //return super.onCreateOptionsMenu(menu);
        // myMenu=menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_addnewsite) {
                   Intent intent = new Intent(Activity_antenna.this, Activity_addnewsite.class);
                   //String message= getTitle().toString();
                   intent.putExtra("ant_fullname", ant_fullname);
                   intent.putExtra("nodedesc", "add");

                   startActivity(intent);

                //this.finish();
                return  true;
            }else

            if (id == R.id.action_editantenna) {
                Intent intent = new Intent(Activity_antenna.this, Activity_editantenna.class);
                //String message= getTitle().toString();
                intent.putExtra("ant_fullname", ant_fullname);
                intent.putExtra("nodedesc", "edit");
                intent.putExtra("antId", 0);
                startActivity(intent);

               this.finish();
                return  true;
            }
        }catch (Exception e){
            e.printStackTrace();
           return  false;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public void popupTakepictures(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pictureNbr = 1;
                setPictures(pictureNbr);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "operation canceled !!!", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }
    TextView  tvantennamodel,tvantlabel,tvports,tvazimuth,tvHeight,tvantview,tvsectorobstruction,tvmechanical,tvbracket,tvgrounglevel,tvtowerlevel;
    ImageView ivantennamodel ,ivantlabel,ivports,ivazimuth,ivHeight,ivantview,ivsectorobstruction,ivmechanical,ivbracket,ivgrounglevel,ivtowerlevel;
    String strAntenne,sPort;
    Spinner spmechanicaltilt,spbrachet,spsector;
    int intAntenne,ivId,edId,spId,autotvId;
    public  String SV_IdOperation,SV_SiteName, SV_SiteConfig, SV_DateTime,SV_Coordinates,SV_AuditedBy,SV_Rigger,SV_Owner,
            SV_Vendor,SV_RooftopTower,SV_TowerMastHeight,SV_BuildingHweight,SV_TotNumAntennas,SV_AntennaModel,SV_Remarks,SV_Tech,
            SV_Sector,SV_Description,SV_Plan,SV_Actual,SV_Pictures,SV_userid,SV_Device,SV_Antenne,SV_Ports,bport;
    public AutoCompleteTextView AutoTextview;
    public String[] strListAntenna={"Kathrein","Andrew: DBXLH-6565C-VTM"};


}
