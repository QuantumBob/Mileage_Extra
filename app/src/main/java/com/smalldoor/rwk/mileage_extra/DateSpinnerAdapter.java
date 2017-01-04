package com.smalldoor.rwk.mileage_extra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by quant on 21/12/2016.
 * extends arrayadapter for a date spinner view
 */

public class DateSpinnerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> mList;


    public DateSpinnerAdapter(Context context, int resource, ArrayList<String> list) {

        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;
        if (v == null){
            LayoutInflater.from(getContext()).inflate(R.layout.date_spinner_item, null);
        }


        return v;
    }
}
