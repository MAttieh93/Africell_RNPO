package guru.pmvungu.example.com.guru;

import java.util.ArrayList;
import java.util.HashMap;

//import com.tutomobile.android.listView.R;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;


import android.database.Cursor;
import android.widget.Toast;

import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.content.DialogInterface;

public class Activity_history extends AppCompatActivity {

    private ListView maListViewPerso;
    final DBAdapter db = new DBAdapter(Activity_history.this);
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Récupération de la listview créée dans le fichier Activity_Histtory.xml
        maListViewPerso = (ListView) findViewById(R.id.listviewperso);

        //Création de la ArrayList qui nous permettra de remplir la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

        //On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;


        int i=0;

        db.open();  //open connection
        Cursor c = db.getHistory();   //call fonction getHistory and select all record data
        if (c.moveToFirst())           ///check if exist
        {
            do {
                //list.add("Name: " + c.getString(1) + ",Ticket: " + c.getString(2));
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
            } while (c.moveToNext());
        }
        db.close();


        //Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem , R.layout.main,
                new String[] {"img", "titre", "description"}, new int[] {R.id.img, R.id.titre, R.id.description});

        //On attribue à notre listView l'adapter que l'on vient de créer
        maListViewPerso.setAdapter(mSchedule);

        //Enfin on met un écouteur d'évènement sur notre listView
        maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
              final int idRow=position+1;
                //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
                //on créé une boite de dialogue
                AlertDialog.Builder adb = new AlertDialog.Builder(Activity_history.this);

                adb.setTitle("Delete Item");
                adb.setMessage("Supprimer le "+map.get("titre"));
                //on indique que l'on veut le bouton ok à notre boite de dialogue
                 //adb.setPositiveButton("OK", null).toString();

                adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        //delete ticket
                        db.open();
                        db.deleteHistory(idRow);
                        db.close();
                        Toast.makeText(getBaseContext(),"Suppression effectue !!!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                       /// Toast.makeText(getBaseContext(),"Non", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                //on affiche la boite de dialogue
                adb.show();
                //Toast.makeText(getBaseContext(),r, Toast.LENGTH_LONG).show();
            }
        });

    }







   // AlertDialog alert = builder.create();
   // alert.show();



    private void getHistory(){
        int i=0;

        db.open();  //open connection
        Cursor c = db.getHistory();   //call fonction getHistory and select all record data
        if (c.moveToFirst())           ///check if exist
        {
            do {
                displayHistory(c,i);
                i=+1;
            } while (c.moveToNext());
        }
        db.close();
    }

    private void displayHistory(Cursor c, int i){

        String nr=Integer.toString(i).toString();
        String str="Numero :" + nr + "\nCodePin: " + c.getString(1) + "\nTicket: " + c.getString(2) + "\n" + "Date: " + c.getString(3);

        //list.add("Name: " + c.getString(1) + ",Ticket: " + c.getString(2));

        Toast.makeText(getBaseContext(),str, Toast.LENGTH_LONG).show();

    }
}
/*
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ListView;

public class Activity_history extends AppCompatActivity {

    final DBAdapter db = new DBAdapter(Activity_history.this);
    final ArrayList<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
       // ListView lview=(ListView)findViewById(R.id.lvHistory);

        //show histiry from android sqlllite

        getHistory();
    }

    private void getHistory(){
      int i=0;

        db.open();  //open connection
        Cursor c = db.getHistory();   //call fonction getHistory and select all record data
        if (c.moveToFirst())           ///check if exist
        {
            do {
                displayHistory(c,i);
                i=+1;
            } while (c.moveToNext());
        }
        db.close();
    }

    private void displayHistory(Cursor c, int i){

        String nr=Integer.toString(i).toString();
        String str="Numero :" + nr + "\nCodePin: " + c.getString(1) + "\nTicket: " + c.getString(2) + "\n" + "Date: " + c.getString(3);

        list.add("Name: " + c.getString(1) + ",Ticket: " + c.getString(2));

        Toast.makeText(getBaseContext(),str, Toast.LENGTH_LONG).show();

    }


}
*/