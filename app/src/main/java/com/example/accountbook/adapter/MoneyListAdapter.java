package com.example.accountbook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountbook.R;
import com.example.accountbook.dto.MoneyDTO;

import java.text.DecimalFormat;
import java.util.List;

public class MoneyListAdapter extends RecyclerView.Adapter<MoneyListAdapter.MyViewHolder> {
    private final List<MoneyDTO> moneyList;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private int type;

    public MoneyListAdapter(List<MoneyDTO> moneyList, int type) {
        this.moneyList = moneyList;
        this.type = type;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTv, moneyTv, memoTv;
        CardView layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.reCaML_layout);
            dateTv = itemView.findViewById(R.id.reCaML_dateTv);
            moneyTv = itemView.findViewById(R.id.reCaML_moneyTv);
            memoTv = itemView.findViewById(R.id.reCaML_memoTv);
        }
    }

    @NonNull
    @Override
    public MoneyListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recyclerview_money_list, parent, false);
        return new MoneyListAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MoneyDTO dto = moneyList.get(position);
        if(!dto.getDate().equals("0")) {
            holder.dateTv.setText(dto.getDate());
            holder.memoTv.setText(dto.getMoneyMemo());
            if(type != 2) {
                holder.moneyTv.setText(decimalFormat.format(Integer.parseInt(dto.getMoney())));
            } else {
                if(dto.getCategory01().equals("99")) {
                    holder.moneyTv.setText("+ " + decimalFormat.format(Integer.parseInt(dto.getMoney())));
                    holder.moneyTv.setTextColor(Color.parseColor("#0000ff"));
                } else if(dto.getCategory01().equals("98")) {
                    holder.moneyTv.setText("- " + decimalFormat.format(Integer.parseInt(dto.getMoney())));
                    holder.moneyTv.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        } else {
            holder.layout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (moneyList != null) ? moneyList.size() : 0;
    }

}
