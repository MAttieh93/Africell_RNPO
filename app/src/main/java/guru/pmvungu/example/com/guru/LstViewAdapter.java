package guru.pmvungu.example.com.guru;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
public class LstViewAdapter extends ArrayAdapter<String> {
    int groupid;
    String[] item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewAdapter(Context context, int vg, int id, String[] item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }
    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView vidoperation;
        public TextView vsitename;
        public TextView vusername;
         public TextView vdate;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.vidoperation= (TextView) rowView.findViewById(R.id.tvidoperation);
            viewHolder.vsitename= (TextView) rowView.findViewById(R.id.tvsitename);
            viewHolder.vusername= (TextView) rowView.findViewById(R.id.tvusername);
            viewHolder.vdate= (TextView) rowView.findViewById(R.id.tvdate);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        String[] items=item_list[position].split(";");
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.vidoperation.setText(items[0]);
        holder.vsitename.setText(items[1]);
        holder.vusername.setText(items[2]);
        holder.vdate.setText(items[3]);
        return rowView;
    }

}