package guru.pmvungu.example.com.guru;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAct extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public ListAct(Context context, String[] values) {
        super(context, R.layout.activity_main, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.listbuton, parent, false);
        TextView tvValues = (TextView) rowView.findViewById(R.id.values);
        TextView tvDescription = (TextView) rowView.findViewById(R.id.description);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        tvValues.setText(values[position]);

        // Change icon based on name
        String s = values[position];

        //System.out.println(s);

        if (s.equals("CupCake")) {
            imageView.setImageResource(R.drawable.icon);
        } else if (s.equals("Donut")) {
            imageView.setImageResource(R.drawable.icon);
        } else if (s.equals("Froyo")) {
            imageView.setImageResource(R.drawable.afr_icn);
        }


        return rowView;
    }
}
