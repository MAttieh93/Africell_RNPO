package guru.pmvungu.example.com.guru;

import android.content.Intent;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
 import android.os.HandlerThread;
import android.widget.Toast;

public class Splashscreen extends AppCompatActivity {
private static int SPLASH_SCREEN_TIME_OUT=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        setupActionBar();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setHomeButtonEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

               //Intent intent = new Intent(Splashscreen.this, MainActivity.class);
             Intent intent = new Intent(Splashscreen.this, Activity_login.class);
               // Intent intent = new Intent(Splashscreen.this, Activity_rfdata.class);
                startActivity(intent);

                //Toast.makeText(getApplicationContext(),"time ok",Toast.LENGTH_LONG).show();
                Splashscreen.this.finish();
            }
        },SPLASH_SCREEN_TIME_OUT);
/*
        Thread myThread=new Thread(){
       @Override
       public void run(){
            try{
                sleep(SPLASH_SCREEN_TIME_OUT);
                Intent myIntent =new Intent(getApplicationContext(),MainActivity.class);
                startActivity(myIntent);
                 finish();

            }
            catch (InterruptedException e){
                e.printStackTrace();

            }

       }

   };
     myThread.start();

*/
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
           // actionBar.setDisplayHomeAsUpEnabled(true);
        }else{
            actionBar.setDisplayHomeAsUpEnabled(false);

        }
    }
}
