package development.app.accountbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import development.app.accountbook.dto.TransferMoneyDTO;
import development.app.accountbook.R;

import java.text.DecimalFormat;
import java.util.List;

public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.MyViewHolder> {
    private final List<TransferMoneyDTO> transferList;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onItemClick(View v, TransferMoneyDTO transferDTO);
    }
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    public BankListAdapter(List<TransferMoneyDTO> transferList) {
        this.transferList = transferList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTv, moneyTv, expendingTv, incomeTv;
        AppCompatImageButton bankTransferDeleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTv = itemView.findViewById(R.id.reBank_dateTv);
            moneyTv = itemView.findViewById(R.id.reBank_moneyTv);
            expendingTv = itemView.findViewById(R.id.reBank_expendingBankTv);
            incomeTv = itemView.findViewById(R.id.reBank_incomeBankTv);
            bankTransferDeleteBtn = itemView.findViewById(R.id.reBank_deleteBtn);

            // 삭제 버튼 클릭 시 선언한 함수 실행
            bankTransferDeleteBtn.setOnClickListener(clickView -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    if(deleteListener != null) {
                        deleteListener.onItemClick(clickView, transferList.get(position));
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
        View view = inflater.inflate(R.layout.recyclerview_bank_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TransferMoneyDTO dto = transferList.get(position);
        holder.dateTv.setText(dto.getDate());
        holder.moneyTv.setText(decimalFormat.format(Integer.parseInt(dto.getMoney())));
        holder.expendingTv.setText(dto.getExpandingBank());
        holder.incomeTv.setText(String.valueOf(dto.getIncomeBank()));
    }

    @Override
    public int getItemCount() {
        return transferList.size();
    }
}
