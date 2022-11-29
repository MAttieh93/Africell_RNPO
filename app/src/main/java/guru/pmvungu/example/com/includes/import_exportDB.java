package guru.pmvungu.example.com.includes;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class import_exportDB {

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
    public import_exportDB(Context ctx)
    {
        this.context = ctx;


    }


    String COPY_DB = "/databases/AfricellDB.db";
    String SAMPLE_DB_NAME=COPY_DB;
    private void exportDB(){

     //   String dt =  getString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));
       /* if (dt != new SimpleDateFormat("dd-MM-yy").format(new Date())) {
            editor.putString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));

            editor.commit();
        }*/

        String DatabaseName = "AfricellDB.db";
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "guru.pmvungu.example.com" +"/databases/"+DatabaseName ;
        String backupDBPath = SAMPLE_DB_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
             Toast.makeText(context, "Your Database is Exported !!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            Log.e("FILECOPYERROR>>>>",e.toString());
            e.printStackTrace();
        }
    }


    private void importDB(){
        String dir=Environment.getExternalStorageDirectory().getAbsolutePath();
        File sd = new File(dir);
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String backupDBPath = "/data/com.synnlabz.sycryptr/databases/Sycrypter.db";
        String currentDBPath = "Sycrypter.db";
        File currentDB = new File(sd, currentDBPath);
        File backupDB = new File(data, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
             Toast.makeText(context, "Your Database is Imported !!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FILECOPYERROR>>>>",e.toString());
        }
    }
}
