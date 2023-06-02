package development.app.accountbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import development.app.accountbook.dto.MoneyDTO;
import development.app.accountbook.R;

import java.text.DecimalFormat;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder>{
    private final List<MoneyDTO> moneyList;
    private OnItemClickListener itemClickListener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private final int type;


    // interface 선언
    public interface OnItemClickListener {
        void onItemClick(View v, String settingsCode);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }


    public CategoryListAdapter(List<MoneyDTO> moneyList, int type) {
        this.moneyList = moneyList;
        this.type = type;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView contentsTv, moneyTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.reCaLi_layout);
            contentsTv = itemView.findViewById(R.id.reCaLi_contents);
            moneyTv = itemView.findViewById(R.id.reCaLi_money);

            // 클릭 명령어
            moneyTv.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String code = null;
                    if(type == 0) code = moneyList.get(position).getSettingsCode();
                    else if(type == 1) code = moneyList.get(position).getBankCode();
                    else if(type == 2) code = moneyList.get(position).getSettingsCode();
                    if(itemClickListener != null) {
                        itemClickListener.onItemClick(view, code);
                    }

                }
            });
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recyclerview_category_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MoneyDTO dto = moneyList.get(position);

        if(type == 0 || type == 2) {
            holder.contentsTv.setText(dto.getSettingsContents());
        } else if(type == 1){
            holder.contentsTv.setText(dto.getBankContents());
        }

        if(type == 0 || type == 2) {
            holder.moneyTv.setText(decimalFormat.format(Integer.parseInt(dto.getMoney())));
        } else if(type == 1) {
            holder.moneyTv.setText(decimalFormat.format(dto.getIntMoney()));
        }
    }

    @Override
    public int getItemCount() {
        return moneyList.size();
    }
}