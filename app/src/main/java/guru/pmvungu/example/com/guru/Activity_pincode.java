package guru.pmvungu.example.com.guru;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import  android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;
public class Activity_pincode extends AppCompatActivity {

    final DBAdapter db = new DBAdapter(Activity_pincode.this);
    public void getdeviseinfo(){
        /*TelephonyManager devise = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

    String strDevise="Operator Code : " + devise.getSimOperator().toString()
            + "\nOperator Name : " + devise.getSimOperatorName().toString()
            + "\nNetwork Type : " + devise.getNetworkType()
            + "\nCountry ISO : " + devise.getSimCountryIso().toString()
            + "\nPhone Number : " + devise.getLine1Number()
            + "\nIMSI : " + devise.getSubscriberId()
            + "\nImei : " + devise.getDeviceId();
       // Toast.makeText(getBaseContext(), strDevise.toString(),Toast.LENGTH_SHORT).show();*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode);
       /// getdeviseinfo();

        TextView tCodepPin = (TextView) findViewById(R.id.tvCodepin);
        TextView tNTicket = (TextView) findViewById(R.id.tvTicket);
        TextView tNTicketBefore = (TextView) findViewById(R.id.tvTicketb);
        TextView twlc = (TextView) findViewById(R.id.tvwlc);
        TextView tmotifdevise = (TextView) findViewById(R.id.txtmotifdevise);


       Intent intent = getIntent();
       String _codepin = intent.getStringExtra("codepin");

        String[] strMessage=_codepin.split("<br/>");

        String motif=strMessage[0];
        String CodepPin=strMessage[1];
        String NTicket=strMessage[2];
        String NTicketBefore=strMessage[3];
         //String wlc =strMessage[4];
        String strDatetime=strMessage[5];
        String wlc=strMessage[4] + "\n" +strMessage[5];

        tmotifdevise.setText(motif);
        tCodepPin.setText(CodepPin.toString());
        tNTicket.setText(NTicket.toString());
        tNTicketBefore.setText(NTicketBefore.toString());
        twlc.setText(wlc.toString());
        //tdatetime.setText(strDatetime.toString());
      //insert History to memory android phone
       InsertHistory(CodepPin,NTicket,strDatetime);
    }
    private void InsertHistory(String CodepPin,String NTicket,String strDatetime){
        db.open();
        db.insertHistory(CodepPin,NTicket,strDatetime);
        db.close();
        //Toast.makeText(getBaseContext(), "succesfull...", Toast.LENGTH_LONG).show();

    }
}
