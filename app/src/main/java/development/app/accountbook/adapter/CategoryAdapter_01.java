package development.app.accountbook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import development.app.accountbook.R;
import development.app.accountbook.dto.CategoryDTO;

import java.util.List;

public class CategoryAdapter_01 extends RecyclerView.Adapter<CategoryAdapter_01.MyViewHolder> {
    private final List<CategoryDTO> categoryList;
    private OnItemClickListener itemClickListener;
    private OnItemDeleteListener deleteListener;

    // interface 선언
    public interface OnItemClickListener {
        // 클릭 시 동작할 함수
        void onItemClick(View v, CategoryDTO categoryDTO);
    }
    public interface OnItemDeleteListener {
        void onItemClick(View v, CategoryDTO categoryDTO);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    public CategoryAdapter_01(List<CategoryDTO> categoryList) {
        this.categoryList = categoryList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        AppCompatImageButton deleteBtn;
        TextView category01, category02, contents, endDay;

        public MyViewHolder(@NonNull View view) {
            super(view);
            layout = view.findViewById(R.id.reCa_layout);
            deleteBtn = view.findViewById(R.id.reCa_deleteBtn);
            category01 = view.findViewById(R.id.reCa_category01);
            category02 = view.findViewById(R.id.reCa_category02);
            contents = view.findViewById(R.id.reCa_contentsTv);
            endDay = view.findViewById(R.id.reCa_endDayTv);

            // 아이템 클릭 시 위에 선언한 함수 실행
            layout.setOnClickListener(clickView -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    if(itemClickListener != null) {
                        itemClickListener.onItemClick(clickView, categoryList.get(position));
                    }
                }
            });
            // 삭제 버튼 클릭 시 선언한 함수 실행
            deleteBtn.setOnClickListener(clickView -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    if(deleteListener != null) {
                        deleteListener.onItemClick(clickView, categoryList.get(position));
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
        View view = inflater.inflate(R.layout.recyclerview_category_01, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoryDTO dto = categoryList.get(position);
        holder.contents.setText(dto.getContents());
        holder.endDay.setText(dto.getStrEndDay());

        switch (dto.getCategory01()) {
            case "99":
                holder.category01.setText("수입");
                break;
            case "98":
                holder.category01.setText("지출");
                break;
            case "97":
                holder.category01.setText("계좌");
                break;
            case "96":
                holder.category01.setText("카드");
                break;
        }
        switch (dto.getCategory02()) {
            case "90":
                holder.category02.setText("고정비");
                break;
            case "89":
                holder.category02.setText("변동비");
                break;
            case "88":
                holder.category02.setText("준변동비");
                break;
            default:
                holder.category02.setText("");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
