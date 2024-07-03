// CustomAdapter.java
package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Item> {

    public CustomAdapter(@NonNull Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @Override
    public int getItemViewType(int position) {
        // Return 1 if it's a header, 0 otherwise
        if (position == 0 || getItem(position).getListId() != getItem(position - 1).getListId()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2; // One type for headers and one for regular items
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item item = getItem(position);
        int viewType = getItemViewType(position);

        if (viewType == 1) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_header, parent, false);
            }
            TextView headerText = convertView.findViewById(R.id.headerText);
            headerText.setText("List ID: " + item.getListId());
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
            }
            TextView idText = convertView.findViewById(R.id.idText);
            TextView listIdText = convertView.findViewById(R.id.listIdText);
            TextView nameText = convertView.findViewById(R.id.nameText);

            idText.setText("ID: " + item.getId());
            listIdText.setText("List ID: " + item.getListId());
            nameText.setText("Name: " + item.getName());
        }

        return convertView;
    }
}
