package com.example.accountbook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.accountbook.R;

public class SpinnerAdapter extends BaseAdapter {
    private final String[] strCode;
    private final String[] strValue;
    private final Context context;

    public SpinnerAdapter(String[] strCode, String[] strValue, Context context) {
        this.strCode = strCode;
        this.strValue = strValue;
        this.context = context;
    }

    @Override
    public int getCount() {
        return strCode.length;
    }

    @Override
    public Object getItem(int i) {
        return strCode[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_frame01, viewGroup, false);
        TextView code = view.findViewById(R.id.spinner_code);
        TextView value = view.findViewById(R.id.spinner_value);

        code.setText(strCode[i]);
        value.setText(strValue[i]);

        return view;
    }
}
