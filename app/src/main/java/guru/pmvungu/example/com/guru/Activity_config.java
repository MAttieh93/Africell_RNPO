package guru.pmvungu.example.com.guru;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_config extends AppCompatActivity {
    EditText eduid, edname, edapn, edserver, edproxy, edport, edusername, edpwd;
    String mnc, mcc;
    final DBAdapter db = new DBAdapter(Activity_config.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        eduid = (EditText) findViewById(R.id.eduid);
        edname = (EditText) findViewById(R.id.edName);
        edapn = (EditText) findViewById(R.id.edApn);
        edserver = (EditText) findViewById(R.id.edServer);
        edproxy = (EditText) findViewById(R.id.edProxy);
        edport = (EditText) findViewById(R.id.edPort);
        edusername = (EditText) findViewById(R.id.edUserName);
        edpwd = (EditText) findViewById(R.id.edPwd);
        mnc = "630";
        mcc = "90";
        getdeviseinfo();
        getWap();
        //getBatery power
        //GetBattery();

    }
public void GetBattery(){

    libClass libclass=new libClass(Activity_config.this);
    if( libclass.checkPermission()){
        // Toast.makeText(Activity_main_menu.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
    }
    else {
        libclass.requestPermission(Activity_config.this);

    }

    //float battery=libclass.getBatteryLevel(Activity_config.this,getIntent());
   // Toast.makeText(getApplicationContext(),"Batery Pourcentage : "+battery +"%",Toast.LENGTH_LONG).show();
}
    public void getdeviseinfo() {
        //TelephonyManager devise = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
       // mnc = devise.getDeviceId().toString();
        //mcc = devise.getSubscriberId().toString();
        eduid.setText("1");

/*
    String strDevise="Operator Code : " + devise.getSimOperator().toString()
            + "\nOperator Name : " + devise.getSimOperatorName().toString()
            + "\nNetwork Type : " + devise.getNetworkType()
            + "\nCountry ISO : " + devise.getSimCountryIso().toString()
            + "\nPhone Number : " + devise.getLine1Number()
            + "\nIMEI : " + devise.getDeviceId().toString()
            + "\nIMSI : " + devise.getSubscriberId().toString();
           Toast.makeText(getApplication(),strDevise,Toast.LENGTH_LONG).show();
*/

    }

    private void getWap(){
        db.open();
        //Cursor c = db.getInfo(et1.getText().toString());
        Cursor c = db.getWAP();
        if (c.moveToFirst())
            DisplayWAP(c);
        else
            Toast.makeText(getBaseContext(), "No item found", Toast.LENGTH_LONG).show();
        db.close();
    }
    private void DisplayWAP(Cursor c) {
        // TODO Auto-generated method stub

        eduid.setText(c.getString(0).toString());
        edname.setText(c.getString(1).toString());
        edapn.setText(c.getString(2).toString());
        edserver.setText(c.getString(3).toString());
        edproxy.setText(c.getString(4).toString());
        edport.setText(c.getString(5).toString());
        edusername.setText(c.getString(6).toString());
        edpwd.setText(c.getString(7).toString());

        // TODO Auto-generated method stub
        String str="id: " + c.getString(0) + " \n" +"ismi: " + c.getString(1) + "\n" + "imei: " + c.getString(2)+ "\n" + "msisdn: " + c.getString(3)
                + "\n" + "IP Serveur: " + c.getString(5) + "\n" + "message: " + c.getString(4);
        //Toast.makeText(getBaseContext(),str, Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.apn_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

      /* int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/

        switch (item.getItemId()) {
            case R.id.main:
                Intent intent = new Intent(Activity_config.this, MainActivity.class);
                startActivity(intent);
                // Toast.makeText(this,"this is main activity",Toast.LENGTH_LONG).show();
                //return true;
        }

        switch (item.getItemId()) {
            case R.id.addnewapn:
                //insert code here
                if (eduid.toString().trim().equals("")) {
                    Toast.makeText(getBaseContext(), "le uid entrez est incorrect!!!", Toast.LENGTH_SHORT).show();
                } else {
                    if (edserver.toString().trim().equals("")) {
                        Toast.makeText(getBaseContext(), "le server entrez est incorrect!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        //check if existe
                        db.open();
                        db.deleteWAP(eduid.getText().toString());
                        db.insertWAP(eduid.getText().toString(),edname.getText().toString(), edapn.getText().toString(),  edserver.getText().toString()
                                , edproxy.getText().toString(), edport.getText().toString(), edusername.getText().toString(), edpwd.getText().toString(), mnc, mcc);

                        Toast.makeText(getBaseContext(), "Insert Successfull...", Toast.LENGTH_SHORT).show();

                        db.close();


                    }
                }
                case R.id.refresh:
                    db.open();
                    db.setsitedetails ("DELETE FROM tbl_loading");
                    db.setsitedetails ("DELETE FROM tbl_siteconfiguration");
                    db.setsitedetails ("DELETE FROM tbl_SiteVisit");
                    Toast.makeText(getBaseContext(), " Refresh Successfull...", Toast.LENGTH_SHORT).show();

                     new MainActivity().alertMsg(Activity_config.this,"","Refresh Successfull...",Activity_login.class,Activity_login.class);
                      //finish();

                     break;
        }
        return super.onOptionsItemSelected(item);
    }
    }
/*
public class Main extends Activity {
    private TextView contentTxt;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            int level = intent.getIntExtra("level", 0);
            contentTxt.setText(String.valueOf(level) + "%");
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        contentTxt = (TextView) this.findViewById(R.id.monospaceTxt);
        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
}
*/