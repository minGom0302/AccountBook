package development.app.accountbook.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import development.app.accountbook.dto.MoneyDTO;
import development.app.accountbook.viewmodel.SaveMoneyViewModel;
import development.app.accountbook.R;

import java.text.DecimalFormat;
import java.util.List;

public class MoneyListAdapter extends RecyclerView.Adapter<MoneyListAdapter.MyViewHolder> {
    private final List<MoneyDTO> moneyList;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private final int type;
    private final SaveMoneyViewModel saveMoneyViewModel;
    private final Activity activity;
    private modifyItemClickListener modifyItemClickListener;

    public MoneyListAdapter(List<MoneyDTO> moneyList, int type, SaveMoneyViewModel saveMoneyViewModel, Activity activity) {
        this.moneyList = moneyList;
        this.type = type;
        this.activity = activity;
        this.saveMoneyViewModel = saveMoneyViewModel;
    }

    public interface modifyItemClickListener {
        void onItemClick(View v, MoneyDTO dto);
    }

    public void setModifyItemClickListener(MoneyListAdapter.modifyItemClickListener modifyItemClickListener) {
        this.modifyItemClickListener = modifyItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
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

            // 아이템 클릭 시 수정 화면으로 연결하기 위해 외부에서 클릭 이벤트 설정
            layout.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if(modifyItemClickListener != null) {
                        modifyItemClickListener.onItemClick(view, moneyList.get(position));
                    }
                }
            });
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

        holder.dateTv.setText(dto.getDate()); // 날짜 셋팅
        // 메모 셋팅 > 자료를 가져올 때 null 은 0으로 대체했기에 "" 처리
        if(dto.getMoneyMemo().equals("0")) {
            holder.memoTv.setText("");
        } else {
            holder.memoTv.setText(dto.getMoneyMemo());
        }

        // 리스트화면에서 잔액 부분에서만 +, - 구분해서 표현 그 외는 그냥 금액만 표기
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

        // 값이 없는 것들 > 목록을 가져오기 위해 전부 null 값은 0으로 대체해서 다 가져온 다음 0인 경우 화면에서 표현 안하기
        if(dto.getDate().equals("0")) {
            holder.layout.setVisibility(View.GONE);
        }

        // 계좌이체인 경우 삭제 버튼을 없에서 계좌 이채 내역에서 삭제하게 함
        holder.deleteBtn.setOnClickListener(v -> {
            if(!dto.getCategory02().equals("01")) {
                showDialog(0, dto, "해당 내역을 삭제하시겠습니까?");
            } else {
                showDialog(1, null, "계좌 이체는 이체 내역에서 삭제하시기 바랍니다.\n※ 달력화면 → 계좌이체 → 이체내역");
            }
        });
    }

    @Override
    public int getItemCount() {
        return (moneyList != null) ? moneyList.size() : 0;
    }

    private void showDialog(int cnd, MoneyDTO dto, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DialogTheme);

        if(cnd == 0) {
            builder.setTitle("경고").setMessage(msg);
            builder.setPositiveButton("예", ((dialogInterface, i) -> saveMoneyViewModel.deleteSaveMoneyInfo(dto.getMoneySeq())));
            builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));
        } else if(cnd == 1) {
            builder.setTitle("안내").setMessage(msg);
            builder.setPositiveButton("확인", ((dialogInterface, i) -> { }));
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        });

        alertDialog.show();
    }
}
