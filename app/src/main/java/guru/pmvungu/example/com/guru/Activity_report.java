package guru.pmvungu.example.com.guru;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import guru.pmvungu.example.com.includes.apiUrl;

public class Activity_report extends AppCompatActivity {
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
    private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
    private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;
    private static final int SCAN_SIM_BAR_CODE = 49374;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    TelephonyManager device ;


    public String  deviceId,getSubscriberId,Networkcode,NetLac;
    public String SV_siteName,SV_siteConfiguration,SV_owner,SV_vender,SV_antNumber,SV_roottop,SV_tower,SV_strlocation,SV_building,SV_Device;
    String SV_userid,SV_DateTime ,SV_IdOperation;
    Button BtnSave;
    AutoCompleteTextView inputtext;
    MultiAutoCompleteTextView text1;
    final DBAdapter db = new DBAdapter(Activity_report.this);
    private GpsTracker gpsTracker;
    private libClass libclass;

    String[] languages={""};


    int pictureNbr;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        SV_userid="";
        SV_IdOperation="";

        try {
            libclass = new libClass(Activity_report.this);
            if( libclass.checkPermission()){
                Toast.makeText(Activity_report.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                libclass.requestPermission(Activity_report.this);

            }

             FillCellId();
             getLocation();
           // FillSitename();

            ((EditText)findViewById(R.id.edtcoordonates)).setText(String.valueOf(latitude) + ","+String.valueOf(longitude));

            inputtext=(AutoCompleteTextView)findViewById(R.id.autoCompleteSite);
            // text1=(MultiAutoCompleteTextView)findViewById(R.id.multiAutoCompleteTextView1);

            ArrayAdapter adapter = new
                    ArrayAdapter(this,android.R.layout.simple_list_item_1,apiUrl.strListOfSite);

            inputtext.setAdapter(adapter);
            inputtext.setThreshold(1);

            String[] listowner={"Africell","Helios"};
            inputtext=(AutoCompleteTextView)findViewById(R.id.edtowner);
            adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1,listowner);
            inputtext.setAdapter(adapter);
            inputtext.setThreshold(1);

            String[] listvender={"Ericson","Huawei","Huawei et Ericson"};
            inputtext=(AutoCompleteTextView)findViewById(R.id.edtvender);
            adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1,listvender);
            inputtext.setAdapter(adapter);
            inputtext.setThreshold(1);

            BtnSave=(Button)findViewById(R.id.validateButtonSave);
            BtnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickbtnSave();
                }
            });


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public double latitude,longitude;
    public void getLocation(){

        gpsTracker = new GpsTracker(Activity_report.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            String _strHTML = ""
                    + "<small>"
                    + "<b>Latitude     : </b>" + String.valueOf(latitude) + "<br />"
                    + "<b>Longitude    : </b>" + String.valueOf(longitude) + "<br />"
                    + "<b>IMEI         : </b>" + deviceId + "<br />"
                    + "<b>IMSI         : </b>" + getSubscriberId + "<br />"
                    + "<b>LAC-CELLID   : </b>" + NetLac + "<br />"
                    + "</small>";

           // tvLocation.setText(Html.fromHtml(_strHTML));
             //  Toast.makeText(getApplicationContext(),_strHTML,Toast.LENGTH_LONG).show();

        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private void FillCellId() {
     final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

      try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                //  ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                deviceId=String.valueOf(telephony.getDeviceId());
                getSubscriberId=telephony.getSubscriberId().toString();
                Networkcode=String.valueOf(telephony.getSimOperator());

                if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
                {
                    final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
                    if (location != null)
                    {
                       NetLac= location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff);
                    }

                }

            }else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            }
        } catch (Exception e){
            e.printStackTrace();
        }
 }


   /* public void FillSitename(){
        try{
            db.open();
             int i=1;
            Cursor c = db.getSite();
            if (c.moveToFirst()) {

                do {
                   // languages[i] = c.getString(0).toString();
                  Log.e("vl", String.valueOf(i) );
                  //  Toast.makeText(getApplicationContext(),c.getString(0).toString(),Toast.LENGTH_LONG).show();
                    i = i+1;
                } while (c.moveToNext());

            }
            db.close();
        }
        catch (Exception e){
           // Log.e("Errro",e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

*/

  private  void onClickbtnSave(){
     TextView lblError = (TextView) findViewById(R.id.lblError);
     try{

         if (blnCheckDataBeforeUpload() == true){
             insertRfdata();
          }
         }catch (Exception e){
             lblError.setText(e.getMessage());
         }

  }

private  Boolean  blnCheckDataBeforeUpload(){
        TextView lblError = (TextView) findViewById(R.id.lblError);
        lblError.setText("");

        AutoCompleteTextView  siteName = (AutoCompleteTextView) findViewById(R.id.autoCompleteSite);
        SV_siteName = siteName.getText().toString();
        if (SV_siteName.equals("")){
            Toast.makeText(this, "Please select the SiteName.", Toast.LENGTH_LONG).show();
            siteName.setError("Please insert the Tech field.");
            siteName.findFocus();
            //lblError.setText("Please select the SiteName.");
            return false;
        }


        Spinner siteConfiguration = (Spinner) findViewById(R.id.edtsiteconfiguration);
        SV_siteConfiguration = siteConfiguration.getSelectedItem().toString();
        if ((SV_siteConfiguration.equals("Configuration")) || (SV_siteConfiguration.equals(""))){
            Toast.makeText(this, "Please select the Site Configuration.", Toast.LENGTH_LONG).show();
            //SV_siteName.setError("Veuillez introduire le champs Tech.");
            lblError.setText("Please select the Site Configuration.");
            siteConfiguration.findFocus();
            return false;
        }


        AutoCompleteTextView owner = (AutoCompleteTextView) findViewById(R.id.edtowner);
        SV_owner = owner.getText().toString();
        if (SV_owner.equals("")){
            Toast.makeText(this, "Please insert the Owner (Africell Or Helios).", Toast.LENGTH_LONG).show();
             owner.setError("Please insert the Owner (Africell Or Helios).");
             owner.findFocus();
            //lblError.setText("Please insert the Owner (Africell Or Helios).");
            return false;
        }

        AutoCompleteTextView vender = (AutoCompleteTextView) findViewById(R.id.edtvender);
        SV_vender = vender.getText().toString();
        if (SV_vender.equals("")){
            Toast.makeText(this, "Please insert the Vender (Ericsson/Huawei).", Toast.LENGTH_LONG).show();
            vender.setError("Please insert the Owner Ericsson/Huawei)");
            //lblError.setText("Please insert the Owner Ericsson/Huawei)");
            vender.findFocus();
            return false;
        }

        EditText roottop = (EditText) findViewById(R.id.edtroottoptower);
        SV_roottop = roottop.getText().toString();
        if (SV_roottop.equals("")){
            Toast.makeText(this, "Please fill the champs RootTop.", Toast.LENGTH_LONG).show();
            roottop.setError("Please fill the champs RootTop.");
            roottop.findFocus();
           // lblError.setText("Please insert the Owner Ericsson/Huawei)");
            return false;
        }

        EditText tower = (EditText) findViewById(R.id.edtowner);
        SV_tower = tower.getText().toString();
        if (SV_tower.equals("")){
            Toast.makeText(this, "Please fill Tower/Mast Height.", Toast.LENGTH_LONG).show();
            tower.setError("Please fill Tower/Mast Height.");
            tower.findFocus();
            // lblError.setText("Please insert the Owner Ericsson/Huawei)");
            return false;
        }


        EditText building = (EditText) findViewById(R.id.edtbuildingheight);
        SV_building = building.getText().toString();
        if (SV_building.equals("")){
            Toast.makeText(this, "Please fill.", Toast.LENGTH_LONG).show();
            building.setError("Please fill.");
            building.findFocus();
            // lblError.setText("Please insert the Owner Ericsson/Huawei)");
            return false;
        }


        EditText antNumber = (EditText) findViewById(R.id.edtnbreantennas);
        SV_antNumber = antNumber.getText().toString();
        if (SV_antNumber.equals("")){
            Toast.makeText(this, "Please fill Antennas Total numbers.", Toast.LENGTH_LONG).show();
            antNumber.setError("Please fill Antennas Total numbers.");
            // lblError.setText("Please insert the Owner Ericsson/Huawei)");
            antNumber.findFocus();
            return false;
        }

        EditText location = (EditText) findViewById(R.id.edtcoordonates);
        SV_strlocation = location.getText().toString();
        if (SV_strlocation.equals("") || !SV_strlocation.startsWith("-")){
            Toast.makeText(this, "Please fill the location \n check tourn on your permission settings.", Toast.LENGTH_LONG).show();
            location.setError("Please fill the location \n check tourn on your permission settings");
            // lblError.setText("Please insert the Owner Ericsson/Huawei)");
            location.findFocus();
            return false;
        }


         if (SV_userid.equals("")){
            SV_userid="0";
        }


        SV_Device=deviceId;

        return  true;
    }

private void insertRfdata(){
      try{
          long res=0;
          SV_DateTime=libclass.getDateTime();
          if (SV_IdOperation.equals("") || SV_IdOperation==null){
              SV_IdOperation=((SV_DateTime.replaceAll(":","")).replaceAll("-","")).replace(" ","");
          }

          db.open();


          res=db.insertRfdata(SV_IdOperation,SV_siteName,SV_siteConfiguration,SV_DateTime
                  ,SV_strlocation,SV_owner,SV_vender,SV_vender,SV_vender,SV_vender,SV_roottop,SV_tower,SV_building,SV_antNumber,SV_userid,SV_Device);

          if (res >0){
               libclass.popMessage("insertion","operation successfully");
               callForm(Activity_report.this,MainActivity.class);

          }
          else{
              libclass.popMessage("insertion","operation failed...");
          }

      }catch (Exception e){
          Log.e("error occur",e.getMessage());
      }


}

 public void callForm(Context context,Class activity){
        Intent intent = new Intent(context,activity);
        startActivity(intent);
    }






}
