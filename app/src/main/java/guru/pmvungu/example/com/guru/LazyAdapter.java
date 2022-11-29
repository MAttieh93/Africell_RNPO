package guru.pmvungu.example.com.guru;

import android.app.ListActivity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
public class LazyAdapter extends ListActivity {

    static final String[] Android =
            new String[] { "CupCake", "Donut", "Froyo", "GingerBread",
                    "HoneyComb","Ice-Cream Sandwich","Jelly-Bean"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ListAct(this, Android));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
    }

}
