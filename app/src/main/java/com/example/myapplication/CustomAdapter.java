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

public class CustomAdapter extends ArrayAdapter<Item> {

    public CustomAdapter(@NonNull Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
        }

        TextView idText = convertView.findViewById(R.id.idText);
        TextView listIdText = convertView.findViewById(R.id.listIdText);
        TextView nameText = convertView.findViewById(R.id.nameText);

        idText.setText("ID: " + item.getId());
        listIdText.setText("List ID: " + item.getListId());
        nameText.setText("Name: " + item.getName());

        return convertView;
    }
}
