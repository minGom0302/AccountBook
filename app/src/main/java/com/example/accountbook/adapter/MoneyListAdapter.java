package com.example.accountbook.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountbook.R;
import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.viewmodel.SaveMoneyViewModel;

import java.text.DecimalFormat;
import java.util.List;

public class MoneyListAdapter extends RecyclerView.Adapter<MoneyListAdapter.MyViewHolder> {
    private final List<MoneyDTO> moneyList;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private final int type;
    private final SaveMoneyViewModel saveMoneyViewModel;
    private final Activity activity;

    public MoneyListAdapter(List<MoneyDTO> moneyList, int type, SaveMoneyViewModel saveMoneyViewModel, Activity activity) {
        this.moneyList = moneyList;
        this.type = type;
        this.activity = activity;
        this.saveMoneyViewModel = saveMoneyViewModel;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTv, moneyTv, memoTv;
        CardView layout;
        AppCompatImageButton deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.reCaML_layout);
            dateTv = itemView.findViewById(R.id.reCaML_dateTv);
            moneyTv = itemView.findViewById(R.id.reCaML_moneyTv);
            memoTv = itemView.findViewById(R.id.reCaML_memoTv);
            deleteBtn = itemView.findViewById(R.id.reCaML_deleteBtn);
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

        holder.dateTv.setText(dto.getDate());
        if(dto.getMoneyMemo().equals("0")) {
            holder.memoTv.setText("");
        } else {
            holder.memoTv.setText(dto.getMoneyMemo());
        }

        if(type == 2){
            if(dto.getCategory01().equals("99")) {
                holder.moneyTv.setText("+ " + decimalFormat.format(Integer.parseInt(dto.getMoney())));
                holder.moneyTv.setTextColor(Color.parseColor("#0000ff"));
            } else if(dto.getCategory01().equals("98")) {
                holder.moneyTv.setText("- " + decimalFormat.format(Integer.parseInt(dto.getMoney())));
                holder.moneyTv.setTextColor(Color.parseColor("#ff0000"));
            }
        } else {
            holder.moneyTv.setText(decimalFormat.format(Integer.parseInt(dto.getMoney())));
        }

        if(dto.getDate().equals("0")) {
            holder.layout.setVisibility(View.GONE);
        }

        if(!dto.getCategory02().equals("01")) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DialogTheme);
                builder.setTitle("안내").setMessage("해당 내역을 삭제하시겠습니까?");
                builder.setPositiveButton("예", ((dialogInterface, i) -> saveMoneyViewModel.deleteSaveMoneyInfo(dto.getMoneySeq())));
                builder.setNegativeButton("아니오", ((dialogInterface, i) -> {
                }));

                AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(dialogInterface -> {
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                });
                alertDialog.show();
            });
        } else {
            holder.deleteBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (moneyList != null) ? moneyList.size() : 0;
    }

}
