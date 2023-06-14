package development.app.accountbook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import development.app.accountbook.dto.MoneyDTO;
import development.app.accountbook.R;

import java.text.DecimalFormat;
import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder>{
    private final List<MoneyDTO> moneyList;
    private OnItemClickListener itemClickListener;
    private InfoOnItemClickListener infoOnItemClickListener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private final int type;
    private String settingsCode = null;

    // interface 선언
    public interface OnItemClickListener {
        void onItemClick(View v, String settingsCode);
    }

    public interface InfoOnItemClickListener {
        void onItemClick(View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setInfoOnItemClickListener(InfoOnItemClickListener infoOnItemClickListener) {
        this.infoOnItemClickListener = infoOnItemClickListener;
    }

    public CategoryListAdapter(List<MoneyDTO> moneyList, int type) {
        this.moneyList = moneyList;
        this.type = type;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView contentsTv, moneyTv;
        AppCompatButton infoBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.reCaLi_layout);
            contentsTv = itemView.findViewById(R.id.reCaLi_contents);
            moneyTv = itemView.findViewById(R.id.reCaLi_money);
            infoBtn = itemView.findViewById(R.id.reCali_infoBtn);

            // 클릭 명령어
            moneyTv.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String code = null;
                    if(type == 0) code = moneyList.get(position).getSettingsCode();
                    else if(type == 1) code = moneyList.get(position).getBankCode();
                    if(itemClickListener != null) {
                        itemClickListener.onItemClick(view, code);
                    }

                }
            });

            infoBtn.setOnClickListener(view -> {
                if(infoOnItemClickListener != null) {
                    infoOnItemClickListener.onItemClick(view);
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

        if(type == 0) {
            holder.contentsTv.setText(dto.getSettingsContents());
        } else if(type == 1){
            holder.contentsTv.setText(dto.getBankContents());
        }

        if(type == 0) {
            holder.moneyTv.setText(decimalFormat.format(Integer.parseInt(dto.getMoney())));
        } else if(type == 1) {
            holder.moneyTv.setText(decimalFormat.format(dto.getIntMoney()));
        }

        if(dto.getSettingsContents() != null) {
            if (dto.getSettingsContents().equals("이월(+)") || dto.getSettingsContents().equals("이월(-)")) {
                holder.infoBtn.setVisibility(View.VISIBLE);
            } else {
                holder.infoBtn.setVisibility(View.GONE);
            }
        }

        if(settingsCode != null) {
            if(type == 0) {
                if (dto.getSettingsCode().equals(settingsCode)) {
                    holder.moneyTv.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.moneyTv.setBackgroundColor(Color.parseColor("#99CCFF"));
                    settingsCode = null;
                }
            } else if(type == 1) {
                if (dto.getBankCode().equals(settingsCode)) {
                    holder.moneyTv.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.moneyTv.setBackgroundColor(Color.parseColor("#99CCFF"));
                    settingsCode = null;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return moneyList.size();
    }

    public void setClickColor(String settingsCode) {
        this.settingsCode = settingsCode;
    }
}
