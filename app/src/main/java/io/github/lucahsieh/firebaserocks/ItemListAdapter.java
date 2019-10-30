package io.github.lucahsieh.firebaserocks;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;

public class ItemListAdapter extends ArrayAdapter<ToDoItem> {
    private Activity context;
    private List<ToDoItem> itemList;

    public ItemListAdapter(Activity context, List<ToDoItem> itemList) {
        super(context, R.layout.list_layout, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    public ItemListAdapter(Context context, int resource, List<ToDoItem> objects, Activity context1, List<ToDoItem> itemList) {
        super(context, resource, objects);
        this.context = context1;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView tvTask = listViewItem.findViewById(R.id.textViewTask);
        TextView tvWho = listViewItem.findViewById(R.id.textViewWho);
        TextView tvDate = listViewItem.findViewById(R.id.textViewDue);
        TextView tvDone = listViewItem.findViewById(R.id.textViewDone);
//        CheckBox tvCheck = listViewItem.findViewById(R.id.checkboxDone);



        ToDoItem item = itemList.get(position);
        tvTask.setText(item.getTask());
        tvWho.setText(item.getWho());
        tvDate.setText(item.getDue().toString());
        tvDone.setText(item.isDone()?"Done":"Not Done");

        return listViewItem;
    }

}
