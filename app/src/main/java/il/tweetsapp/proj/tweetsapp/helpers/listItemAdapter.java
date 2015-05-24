package il.tweetsapp.proj.tweetsapp.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import il.tweetsapp.proj.tweetsapp.R;

/**
 * Created by Haim on 5/17/2015.
 */
public class ListItemAdapter extends BaseAdapter {

    Context context;
    List<String> itemsNames;
    TextView tView;
    private static LayoutInflater inflater = null;

    public ListItemAdapter(Context context, List<String> conversationsNames) {
        this.context = context;
        this.itemsNames = conversationsNames;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemsNames.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.row_list_item_layout, null);
        tView = (TextView) view.findViewById(R.id.cnvRowTextView);
        tView.setText(itemsNames.get(position));
        return view;
    }
}
