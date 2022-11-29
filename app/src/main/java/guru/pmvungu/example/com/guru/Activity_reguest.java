package guru.pmvungu.example.com.guru;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.text.TextUtils;
import android.util.Log;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import java.util.Calendar;
import android.widget.AdapterView;
import android.widget.ImageButton;

public class Activity_reguest extends AppCompatActivity {

    final DBAdapter db = new DBAdapter(Activity_reguest.this);
    private String strFinalXML;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    String pathUrl = "http://localhost:81/" ;
    String txtURLIP,txtPort;
    String _BirthDate,_montant,_accountnumber,_agence,_code,_ipserver,_username,_devise,_motif,_port;

    Button validateButton;
    ImageButton btnBirthDatePick;
    Spinner spinnerEducation;
    Spinner spinnerDevise;
    TextView lblBirthDateValue;


   // final DBAdapter db = new DBAdapter(Activity_request.this);
   String[] strmotif = ";Depot espece;Retrait espece;Releve de transaction".split(";");///{"", "India", "USA", "China", "Japan", "Other"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reguest);

        _ipserver ="";   // default IP server
        _port=""; //default port 80 or 81
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);

        getIpServer();
       //setupActionBar();
        sFillCBOAgence();
        sFillCBODevise();
        sFillCBOMotif();

        //Calendar CurrentDateTime = Calendar.getInstance();
        //TextView lblBirthDateValue = (TextView) findViewById(R.id.lblBirthDateValue);


    }
    public void  getIpServer(){
    _ipserver ="";_port="";
        String _name="", _apn="";
    db.open();
    Cursor c = db.getWAP();
    if (c.moveToFirst()) {
        _name=c.getString(1).toString();
        _apn=c.getString(2).toString();
        _ipserver = c.getString(3).toString();
        _port = c.getString(5).toString();


        //Toast.makeText(getBaseContext(), " server  "+_ipserver, Toast.LENGTH_LONG).show();
    }else {
        Toast.makeText(getBaseContext(), "Server no found. configure your APN!!!", Toast.LENGTH_LONG).show();
    }

    db.close();

      //  http://localhost:81/apn/name.php
        if (!_ipserver.equals("")){
            _ipserver="http://"+_ipserver;
        }
        if (!_port.equals("")){
            _port=":"+_port;
        }

        if (!_apn.equals("")){
            _apn="/"+_apn;

        }
        if (!_name.equals("")){
            _name="/"+_name+".php";

        }

        pathUrl=_ipserver+ _port +_apn+_name ;
        txtURLIP=pathUrl;

}
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        Toast.makeText(getApplicationContext(),strmotif[position] ,Toast.LENGTH_LONG).show();
    }
    public void onValidateClick(View view) {

        TextView lblError = (TextView) findViewById(R.id.lblError);
        lblError.setTextColor(Color.RED);

       if (blnCheckDataBeforeUpload() == true){
           //active new sync  for test
         /*   String result ="Depot espece, USD.<br/>" +
                    "Code pin : 305-180511113353<br/>" +
                   "Nr Ticket : 5<br/>" +
                   "Personne avant : 3<br/>" +
                   "Thank you for your time...<br/>" +
                   "2018-05-30 11:33:53<br/>";

           Intent intent = new Intent(Activity_reguest.this, Activity_pincode.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           intent.putExtra("codepin", result.toString());
           startActivity(intent);

*/
         String _api="monticketreq";
           new AsyncInsert().execute(_code,_accountnumber,_montant,_agence,_BirthDate,_devise,_motif,_api);
        }
    }

    private class AsyncInsert extends AsyncTask<String, String, String>{
        ProgressDialog pdLoading;
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
              pdLoading = new ProgressDialog(Activity_reguest.this);
            //this method will be running on UI thread
            pdLoading.setMessage("\tVeuillez patientez SVP!!!");
            pdLoading.setCancelable(true);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(pathUrl);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception doInbackground";
            }
////try post
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
                            .appendQueryParameter("code", params[0])
                            .appendQueryParameter("accountnumber", params[1])
                            .appendQueryParameter("montant", params[2])
                            .appendQueryParameter("agence", params[3])
                            .appendQueryParameter("date", params[4])
                            .appendQueryParameter("devise", params[5])
                            .appendQueryParameter("motif", params[6])
                            .appendQueryParameter("api", params[7]);
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
            pdLoading.dismiss();

         // Toast.makeText(getBaseContext(),"Result: "+result.toString(),Toast.LENGTH_LONG).show();

            //this method will be running on UI thread
            // pdLoading.dismiss();
            int position =result.indexOf("exception");
            int falied=result.indexOf("Failed");
            if ((position>0)|| result.equalsIgnoreCase("unsuccessful") || (falied >0)){
                //system return error
                Toast.makeText(Activity_reguest.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
                 Log.e("SmsReceiver","OOPs! Something went wrong. Connection Problem.");

                TextView lblError = (TextView) findViewById(R.id.lblError);
                lblError.setText("OOPs! Something went wrong. Connection Problem.");

                return;
            }
              position=result.indexOf("invalide user");
            if (position>0){
                TextView lblError = (TextView) findViewById(R.id.lblError);
                lblError.setText("le numero compte ne pas enregistre.\nveullez vous inscrire et passer a la agence pour la confirmation. Merci!!!");
                Toast.makeText(Activity_reguest.this, "le numero compte ne pas enregistre\nveullez vous inscrire et passer a la agence pour la confirmation.Merci!!!", Toast.LENGTH_LONG).show();
                return;
            }

            if(!result.equalsIgnoreCase(""))
            {
                position =result.indexOf("pin");
                if (position>0){
                    TextView lblError = (TextView) findViewById(R.id.lblError);
                    lblError.setTextColor(Color.DKGRAY);
                    lblError.setText(result);

                    Intent intent = new Intent(Activity_reguest.this, Activity_pincode.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("codepin", result.toString());
                    startActivity(intent);

                   // Toast.makeText(Activity_reguest.this, "transfert activity" + result.toString(), Toast.LENGTH_LONG).show();
                    Activity_reguest.this.finish();

                    return;
                }
                // success full insert



            }else if (result.equalsIgnoreCase("")){
               // Log.e("SmsReceiver","OOPs! sorry Invalid reguest!!!!");

                TextView lblError = (TextView) findViewById(R.id.lblError);
                lblError.setText("OOPs! sorry Invalid reguest!!!!");
            }

        }

    }


    /* send an alert
    @SuppressWarnings("deprecation")
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
    */
    public boolean blnCheckDataBeforeUpload() {
        TextView lblError = (TextView) findViewById(R.id.lblError);
        lblError.setText("");

        _code="1112";  //1112 code send with android app
        EditText txtaccount = (EditText) findViewById(R.id.txtACCOUNT);
        _accountnumber = txtaccount.getText().toString();

        if (_accountnumber.equals("")){
            Toast.makeText(this, "account Name cannot be empty.", Toast.LENGTH_LONG).show();
            lblError.setText("Numero Compte cannot be empty.");
            return false;
        }
        else
        {
            if (_accountnumber.length() < 16){
                Toast.makeText(this, "account is not valid.", Toast.LENGTH_LONG).show();
                lblError.setText("account number is not valid.");
                return false;
            }
        }

        //get and check agence
        Spinner txtagence = (Spinner) findViewById(R.id.txtAgence);
        _agence = txtagence.getSelectedItem().toString();

        //get and check Devise
        Spinner txtdevise = (Spinner) findViewById(R.id.txtDevise);
        _devise = txtdevise.getSelectedItem().toString();

        if (_devise.equals("")){
            Toast.makeText(this, "Veuillez selectionner la devise.", Toast.LENGTH_LONG).show();
            lblError.setText("Veuillez selectionner la devise.");
            return false;
        }

        //get and check Devise
        Spinner txtmotif = (Spinner) findViewById(R.id.txtMotif);
        _motif = txtmotif.getSelectedItem().toString();

        if (_motif.equals("")){
            Toast.makeText(this, "Veuillez selectionner le motif.", Toast.LENGTH_LONG).show();
            lblError.setText("Veuillez selectionner la devise.");
            return false;
        }


        //get and check the montant
        EditText txtmontant = (EditText) findViewById(R.id.txtMONTANT);
        _montant  = txtmontant.getText().toString();
        if (_montant.equals("")){
            Toast.makeText(this, "Montant cannot be empty.", Toast.LENGTH_LONG).show();
            lblError.setText("Montant cannot be empty.");
            return false;
        }

        //get  Date
        TextView lblBirthDateValue = (TextView) findViewById(R.id.lblBirthDateValue);
        _BirthDate = lblBirthDateValue.getText().toString();

        if (_BirthDate.equals("") || (_BirthDate.equals("AAAA-mm-dd"))){
            Toast.makeText(this, "Date cannot be empty.", Toast.LENGTH_LONG).show();
            lblError.setText("Date cannot be empty.");
            return false;
        }

        //  Intent intent = getIntent();
        //  _username = intent.getStringExtra("username");
        // _ipserver ="10.101.1.15";// intent.getStringExtra("ipserver");

       // txtURLIP= _ipserver;
       // txtPort=_port;

        if (txtURLIP.equals("")){
            lblError.setText("impossible de connecte dans le server...");
            return false;
        }

            //  Toast.makeText(this, "URL : "+ pathUrl, Toast.LENGTH_LONG).show();
        return true;
    }

    private  void getValuesFromDB(String xvalue){
        //select xvalue=motif from dbb

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    private void sFillCBOAgence(){
        Spinner spinnerEducation = (Spinner) findViewById(R.id.txtAgence);
        ArrayAdapter<CharSequence> adapterEducation = ArrayAdapter.createFromResource(this,R.array.Agence_array, android.R.layout.simple_spinner_item);
        adapterEducation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerEducation.setAdapter(adapterEducation);
    }
    private void sFillCBODevise(){
        Spinner spinnerDevise = (Spinner) findViewById(R.id.txtDevise);
        ArrayAdapter<CharSequence> adapterDevise = ArrayAdapter.createFromResource(this,R.array.Devise_array, android.R.layout.simple_spinner_item);
        adapterDevise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevise.setAdapter(adapterDevise);
    }
    private void sFillCBOMotif(){

       Spinner spinnerDevise = (Spinner) findViewById(R.id.txtMotif);
        //  fill splinner mitif with string xml value
       // ArrayAdapter<CharSequence> adapterDevise = ArrayAdapter.createFromResource(this,R.array.Motif_array, android.R.layout.simple_spinner_item);

        ArrayAdapter  adapterDevise = new ArrayAdapter(this, android.R.layout.simple_spinner_item,strmotif);
        adapterDevise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevise.setAdapter(adapterDevise);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    @SuppressLint("ValidFragment")
    private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hours= c.get(Calendar.HOUR);
            int minutes= c.get(Calendar.MINUTE);
            int seconds= c.get(Calendar.SECOND);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
            //return new DatePickerDialog(getActivity(), this, year, month, day,hours,minutes,seconds);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String strDay = "";
            String strMonth = "";
            if (day < 10) {
                strDay = "0" + day;
            } else {
                strDay = "" + day;
            }
            month = month + 1;
            if (month < 10) {
                strMonth = "0" + month;
            } else {
                strMonth = "" + month;
            }
            // sSetBirthDate(strMonth+"/"+strDay+"/"+year);
            //+" " + hours+":" + minutes+":" + seconds
            sSetBirthDate(year + "-" + strMonth + "-" + strDay);
        }
    }

    private void sSetBirthDate(String strDate){
        if (!strDate.equals("")) {
            final TextView textViewToChange = (TextView) findViewById(R.id.lblBirthDateValue);
            textViewToChange.setText(strDate);
        }
    }
}
