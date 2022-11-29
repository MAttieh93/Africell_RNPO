package guru.pmvungu.example.com.guru;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static  android.Manifest.permission.ACCESS_NETWORK_STATE;
import static  android.Manifest.permission.ACCESS_FINE_LOCATION;
import static  android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static guru.pmvungu.example.com.includes.apiUrl.baseUrl;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import guru.pmvungu.example.com.includes.ConnectionDetector;
import  guru.pmvungu.example.com.includes.base64;
public class Activity_deviceposition extends AppCompatActivity {
   // public static final String EXTRA_MESSAGE = "sl.Africell.afr_sl_registration.MESSAGE";
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
    private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
    private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;
    private static final int SCAN_SIM_BAR_CODE = 49374;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    final int REQUIRED_SIZE = 512;
    private int imgwidth=250;
    private  int imgheight=190;

    private ImageView mPersonalPhoto;
    private String strPersonalPhoto;
    private Uri PersonalPhotoUri;
    private String strPersonalPhotoURL;

    // number of images to select
    private static final int PICK_IMAGE = 1;

    private String strGeoLocation;

    @SuppressWarnings("unused")
    private int intNbrRequestedFace;

    private Bitmap bitmap;
    private String text;

    TelephonyManager device ;
    Button getLocationBtn,uploaderBtn,getPicture1;
    private GpsTracker gpsTracker;
    private TextView tvLocation,tvLatitude,tvLongitude;
    public String strlocation,deviceId,getSubscriberId,Networkcode,getMyCellId;
    public TextView photoflag;

    AutoCompleteTextView inputtext;
    MultiAutoCompleteTextView text1;
    String[] languages={"" +
            "INK001",
            "KIN006",
            "KIN010",

    };

    int pictureNbr;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;
    String[] strSite = "Select Site;Victoire;Gombe".split(";");///{"", "India", "USA", "China", "Japan", "Other"};
    ConnectionDetector connector;
    libClass _libClass ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceposition);

        _libClass = new libClass(Activity_deviceposition.this);
        device = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        uploaderBtn = (Button)findViewById(R.id.validateButton);
        getPicture1 = (Button)findViewById(R.id.btnCapture1);
        tvLocation = (TextView)findViewById(R.id.lblLocation);
        photoflag = (TextView)findViewById(R.id.photoflag);

        strPersonalPhoto = "";
        intNbrRequestedFace = 0;
        strPersonalPhotoURL = "";
        //sEnableDisable("0");


        uploaderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();

                onValidation("insert");
            }
        });

        getPicture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureNbr=1;
                setPictures(pictureNbr);
            }
        });

        /*
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

*/
   try {
        if(checkPermission()){
             Toast.makeText(Activity_deviceposition.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        }
        else {
            requestPermission();
        }

        getMyCellId=getMyCellId();
        getLocation();
       //sFillSite();

    } catch (Exception e){
            e.printStackTrace();
    }


        inputtext=(AutoCompleteTextView)findViewById(R.id.autoCompleteSite);
       // text1=(MultiAutoCompleteTextView)findViewById(R.id.multiAutoCompleteTextView1);

        ArrayAdapter adapter = new
                ArrayAdapter(this,android.R.layout.simple_list_item_1,languages);

        inputtext.setAdapter(adapter);
        inputtext.setThreshold(1);

       /* multi caracter before autocomplete
       text1.setAdapter(adapter);
        text1.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
     */


 }


    public void setPictures(int pictureNbr){
        //set pictures
        CapturePersonalPhoto();
      //  Toast.makeText(Activity_deviceposition.this, "click on "+pictureNbr, Toast.LENGTH_LONG).show();





    }
    public void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE);
    }
    public void CapturePersonalPhoto(){
        try{

            mPersonalPhoto = (ImageView) findViewById(R.id.camera_personal_image);
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

                    mPersonalPhoto.setImageBitmap(bitmap);
                    thumbnail = bitmap;

                    ImageView img;
                    Bitmap bmp;

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
    public double latitude,longitude;
    public void getLocation(){

        gpsTracker = new GpsTracker(Activity_deviceposition.this);
        if(gpsTracker.canGetLocation()){
              latitude = gpsTracker.getLatitude();
              longitude = gpsTracker.getLongitude();

   /*         strlocation="Latitude : "+String.valueOf(latitude)
            + "\nLongitude : " + String.valueOf(longitude)
            + "\nIMEI : " + deviceId
            + "\nIMSI : " + getSubscriberId
            + "\nCell ID : " + getMyCellId
            + "\nNetwork Code : "+ Networkcode;

          tvLocation.setText(strlocation);
*/

            String _strHTML = ""
                    + "<small>"
                    + "<b>Latitude     : </b>" + String.valueOf(latitude) + "<br />"
                    + "<b>Longitude    : </b>" + String.valueOf(longitude) + "<br />"
                    + "<b>IMEI         : </b>" + deviceId + "<br />"
                    + "<b>IMSI         : </b>" + getSubscriberId + "<br />"
                    + "<b>LAC-CELLID   : </b>" + getMyCellId + "<br />"
                    + "</small>";

            tvLocation.setText(Html.fromHtml(_strHTML));


        }else{
            gpsTracker.showSettingsAlert();
        }
    }
    private String getMyCellId()
    {
        String Result=null;

        final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
              //  ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                deviceId=String.valueOf(device.getDeviceId());
                getSubscriberId=device.getSubscriberId().toString();
                Networkcode=String.valueOf(device.getSimOperator());


                if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
                {
                    final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
                    if (location != null)
                    {
                        Result= location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff);
                    }

                }

            }else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            }
        } catch (Exception e){
            e.printStackTrace();
        }


             return  Result;

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(Activity_deviceposition.this, new String[]
                {
                        CAMERA,
                        READ_CONTACTS,
                        READ_PHONE_STATE,
                        ACCESS_FINE_LOCATION,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }

    @Override
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

                        Toast.makeText(Activity_deviceposition.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        //  Toast.makeText(Activity_deviceposition.this,"Permission Denied" +grantResults[2],Toast.LENGTH_LONG).show();

                    }
                }

                break;

        }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Activity_deviceposition.this,"Permission Denied " +e.getMessage(),Toast.LENGTH_LONG).show();
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

/*
fill splinner
    private void sFillSite(){

        Spinner spinnerSite = (Spinner) findViewById(R.id.spSite);
        //  fill splinner mitif with string xml value
        // ArrayAdapter<CharSequence> adapterDevise = ArrayAdapter.createFromResource(this,R.array.Motif_array, android.R.layout.simple_spinner_item);

        ArrayAdapter adapterDevise = new ArrayAdapter(this, android.R.layout.simple_spinner_item,strSite);
        adapterDevise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSite.setAdapter(adapterDevise);
    }
    */
String flagphoto="0";
@Override
protected void onResume() {
    super.onResume();
    //flagphoto="0";

}

    @Override
    protected void onPause() {
      super.onPause();
    }

    private void sEnableDisable(String strShow){
        //lock unlock


        //hide show
        Button validateButton = (Button)findViewById(R.id.validateButton);

        TextView lblBirthDateValue = (TextView) findViewById(R.id.lblBirthDateValue);

        Button btnCapture1 = (Button)findViewById(R.id.btnCapture1);

        //fg="0";

        ImageView camera_personal_image = (ImageView) findViewById(R.id.camera_personal_image);

       // Button btnUpload = (Button)findViewById(R.id.btnUpload);

        if (strShow.equals("1")){

            validateButton.setVisibility(View.GONE);
            btnCapture1.setVisibility(View.VISIBLE);
            //btnEmpreinte.setVisibility(View.VISIBLE);

            camera_personal_image.setVisibility(View.VISIBLE);

        }
        else{

             validateButton.setVisibility(View.VISIBLE);
             btnCapture1.setVisibility(View.GONE);
             camera_personal_image.setVisibility(View.GONE);

            //btnUpload.setVisibility(View.GONE);
        }
    }

public String strURLInsertRegistration;
public void onValidation(String strvalidation){
        connector = new ConnectionDetector(getApplicationContext());

        TextView lblError = (TextView) findViewById(R.id.lblError);
        boolean isConnected = isNetworkAvailable();
        if (connector.isConnectingToInternet()){
            lblError.setText("connected");
        }else{
            lblError.setText("Ouups!!! probleme de connection.");
            showAlertDialog(Activity_deviceposition.this, "No Internet Connection",
                    "Ouups!!! probleme de connection.", false);
            return;
        }


     if (blnCheckDataBeforeUpload() == true){
          strURLInsertRegistration=baseUrl + "InsertRegistration";
           new ImageUploadTaskAsync().execute(_SiteName,strPersonalPhoto,String.valueOf(latitude),String.valueOf(longitude),deviceId,getSubscriberId,getMyCellId);

        }

  }

    private boolean isNetworkAvailable() {
        boolean isconnected=false;

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isconnected=true;

            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch (Exception e){
            Log.e("ERROR ","ERROR :"+e.getMessage());
            return  isconnected;
        }
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
    private String strFinalXML,_SiteName;

   public boolean blnCheckDataBeforeUpload() {
        strFinalXML = "";
        TextView lblError = (TextView) findViewById(R.id.lblError);
        lblError.setText("");

        EditText txtSiteName = (EditText) findViewById(R.id.autoCompleteSite);
          _SiteName = txtSiteName.getText().toString();

        if (_SiteName.equals("")){
            Toast.makeText(this, "le SiteName cannot be empty.", Toast.LENGTH_LONG).show();
            lblError.setError("Veuillez introduire le siteName.");
            return false;
        }


        TextView txtFlagImage = (TextView)findViewById(R.id.photoflag);
        String _FlagImage = txtFlagImage.getText().toString();
        if (!_FlagImage.equals("1")){
           // Toast.makeText(this, "Veuillez prendre la photo avant.", Toast.LENGTH_LONG).show();
            lblError.setText("Veuillez prendre la photo avant.");

            showAlertDialog(Activity_deviceposition.this, "Photo",
                    "Veuillez prendre la photo avant.", false);
            return false;
        }

       /*  strFinalXML = strFinalXML +""+_SiteName;
        strFinalXML = strFinalXML +","+strPersonalPhoto;
        strFinalXML = strFinalXML +","+ String.valueOf(latitude);
        strFinalXML = strFinalXML +","+ String.valueOf(longitude);
        strFinalXML = strFinalXML +","+  deviceId;
        strFinalXML = strFinalXML +","+  getSubscriberId;
        strFinalXML = strFinalXML +","+  getMyCellId;
*/
        return true;

    }

   public class  ImageUploadTaskAsync extends AsyncTask<String,String,String> {
    private ProgressDialog dialog = new ProgressDialog(Activity_deviceposition.this);
    HttpURLConnection conn;
    URL url = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Please wait while uploading...");
        Button btnUpload = (Button)findViewById(R.id.validateButton);
        btnUpload.setVisibility(View.GONE);
        dialog.setCancelable(true);
        dialog.show();
    }

    protected String doInBackground(String... params) {
        try {
            url = new URL(strURLInsertRegistration);
           // return _libClass.parse(strURLInsertRegistration);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "exception doInbackground";
        }

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
                    .appendQueryParameter("SiteName", params[0])
                    .appendQueryParameter("strPersonalPhoto", params[1])
                    .appendQueryParameter("latitude", params[2])
                    .appendQueryParameter("longitude", params[3])
                    .appendQueryParameter("deviceId", params[4])
                    .appendQueryParameter("getSubscriberId", params[5])
                    .appendQueryParameter("getMyCellId", params[6]);
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
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        Log.e("SmsReceiver",result);

        if (result.equals("inserted")){

        }

        Button btnUpload = (Button)findViewById(R.id.validateButton);
        btnUpload.setVisibility(View.VISIBLE);

        TextView lblError = (TextView) findViewById(R.id.lblError);
        lblError.setText(result);
        return;

    }



}


}

