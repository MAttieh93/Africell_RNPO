package guru.pmvungu.example.com.guru;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.VectorEnabledTintResources;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import guru.pmvungu.example.com.includes.ConnectionDetector;
import  guru.pmvungu.example.com.includes.base64;

import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;
import static guru.pmvungu.example.com.includes.apiUrl.getAntenne;

public class Activity_rfdata  extends AppCompatActivity  implements View.OnClickListener {
    ConnectionDetector connector;
    TextView  dataheader;
    public String  deviceId,getSubscriberId,Networkcode,NetLac;
    public String SV_siteName,SV_siteConfiguration,SV_owner,SV_vender,SV_antNumber,SV_roottop,SV_tower,SV_strlocation,SV_building,SV_Audited,SV_Rugger;
    String SV_userid ,SV_IdOperation, SV_cordanates,SV_dateTime;

      final DBAdapter db=new DBAdapter(Activity_rfdata.this);
      final libClass libclass =new libClass(Activity_rfdata.this);
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    String SV_Status="400";


    Button Btnvalide;
    Spinner v_spinnertech,v_spinnersector,v_spinnernomphoto;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfdata);


     try{

        List<String>listTech=new ArrayList<String>();
        List<String>listElect=new ArrayList<String>();
        List<String>listRrutype=new ArrayList<String>();

        listTech.add("select technology item.");
         listTech.add("GSM 900");
         listTech.add("DCS 1800");
         listTech.add("UMTS 900");
         listTech.add("UMTS 2100");
         listTech.add("LTE 800");
         listTech.add("LTE 1800");



        listElect.add("select electrical item.");

         listElect.add("1");
         listElect.add("2");
         listElect.add("3");
         listElect.add("4");
         listElect.add("5");
         listElect.add("6");

        listRrutype.add("select RRU Type item.");
         listRrutype.add("500");
         listRrutype.add("600");
        db.open();
        Cursor cursor=db.getsitedetails("SELECT distinct label FROM tbl_menu Where  objects  like 'technology%' and parent like 'antenna%'");
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                listTech.add(cursor.getString(cursor.getColumnIndex("label")));

            }while (cursor.moveToNext());

        }



        String istrPort="Select Port number";
        int r=4;
        int prevTextViewId = 0;


        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.lnPort);
        int prevId=1;
        for (int i = 0; i < 4; i++) {

            int n = i + 1;
            String techValues = "";
            String rruValues = "";
            String elecValues = "";
            istrPort = istrPort + ";Port " + n;

            String nPort="port"+n;
            String xantenne=getAntenne();


            //lparams.setMargins(0, 10, 0, 0); // (left, top, right, bottom)

            TextView tvPort = new TextView(this);
            tvPort.setId(100+n);
            tvPort.setText("Port :"+n);
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
            tvRrutype.setText("RRU Type : " + rruValues);
            tvRrutype.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvRrutype.setPadding(0,5,0,0);

            Spinner spRrutype = new Spinner(this);
            spRrutype.setId(600+n);


            ArrayAdapter<String> adapterRrutype=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listRrutype);
            adapterRrutype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRrutype.setAdapter(adapterRrutype);

            ImageView ivRrutype = new ImageView(Activity_rfdata.this);
            ivRrutype.setId(1300+n);
            ivRrutype.setImageResource(R.drawable.addcamera);
            ivRrutype.setOnClickListener(Activity_rfdata.this);

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

            ImageView ivEletri = new ImageView(Activity_rfdata.this);
            ivEletri.setId(1200+n);
            // ivEletri=(ImageView)findViewById(ivRrutype.getId());
            ivEletri.setImageResource(R.drawable.addcamera);
            ivEletri.setOnClickListener(Activity_rfdata.this);



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

                //rlparamSpinner.setMargins(250,0,0,0);
                rlparamSpinnerElectri.addRule(RelativeLayout.BELOW,ivRrutype.getId());
                //rlparamSpinner.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rlparamSpinnerElectri.addRule(RelativeLayout.ALIGN_RIGHT);
                spEletri.setLayoutParams(rlparamSpinnerElectri);
                relativeLayout.addView(spEletri);

                rlparamImageViewElectri.addRule(RelativeLayout.BELOW,spEletri.getId());
                //rlparamImageViewElectri.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
                ivEletri.setLayoutParams(rlparamImageViewElectri);
                relativeLayout.addView(ivEletri);


   /* if (listTech.indexOf("DCS 1800")>=0){
         spTech.setSelection(listTech.indexOf("DCS 1800"));

    }*/
               spEletri.setSelection(adapterEletri.getPosition("6"));
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

                //rlparamSpinner.setMargins(250,0,0,0);
                //rlparamSpinner.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rlparamSpinnerElectri.addRule(RelativeLayout.ALIGN_RIGHT);
                rlparamSpinnerElectri.addRule(RelativeLayout.BELOW, 1300+n);
                spEletri.setLayoutParams(rlparamSpinnerElectri);
                relativeLayout.addView(spEletri);

                rlparamImageViewElectri.addRule(RelativeLayout.BELOW,700+n);
                rlparamImageViewElectri.addRule(RelativeLayout.CENTER_VERTICAL);
                ivEletri.setLayoutParams(rlparamImageViewElectri);
                relativeLayout.addView(ivEletri);

            }
        }





        }catch (Exception e){

            e.toString();
        }




 }


    @Override
    public void onClick(View v) {


    }







}





