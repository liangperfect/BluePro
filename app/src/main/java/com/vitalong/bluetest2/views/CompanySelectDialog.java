package com.vitalong.bluetest2.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vitalong.bluetest2.R;

import java.io.File;
import java.util.List;

public class CompanySelectDialog<T> extends Dialog {
    private ImageView imgClose;
    private RecyclerView recyclerView;
    private TextView tvTitle;
    private String dialogTitle;
    private List<T> datas;
    private ChangeComapngeListener listener;

    public CompanySelectDialog(Context context, List<T> d, String title, ChangeComapngeListener l) {
        super(context, R.style.bottom_dialog);
        dialogTitle = title;
        datas = d;
        listener = l;
        init(context);
    }

    public CompanySelectDialog(Context context, int themeResId, List<T> d, String title) {
        super(context, themeResId);
        dialogTitle = title;
        datas = d;
        init(context);
    }

    public CompanySelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener, List<T> d, String title) {
        super(context, cancelable, cancelListener);
        dialogTitle = title;
        datas = d;
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        View view = getLayoutInflater().inflate(R.layout.company_select, null, false);
        setContentView(view);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(dialogTitle);
        imgClose = view.findViewById(R.id.imgClose);
        recyclerView = view.findViewById(R.id.recyclerView);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanySelectDialog.this.dismiss();
            }
        });
        CompanyAdapter companyAdapter = new CompanyAdapter(datas);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(companyAdapter);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = dp2px(context, 500);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }

    class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHodler> {

        private List<T> datas;

        public CompanyAdapter(List<T> d) {
            this.datas = d;
        }

        @NonNull
        @Override
        public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_company, parent, false);
            ViewHodler viewHodler = new ViewHodler(view);
            return viewHodler;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHodler holder, int position) {

            if (datas.get(position) instanceof File) {
                File file = (File) datas.get(position);
                holder.name.setText(file.getName());
            } else if (datas.get(position) instanceof String) {
                String name = (String) datas.get(position);
                holder.name.setText(name);
            }
            holder.cardView.setOnClickListener(v -> {

                listener.changeComapny(datas.get(position));
                CompanySelectDialog.this.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class ViewHodler extends RecyclerView.ViewHolder {

            ImageView img;
            TextView name;
            CardView cardView;

            public ViewHodler(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.imgCompany);
                name = itemView.findViewById(R.id.tvCompanyName);
                cardView = itemView.findViewById(R.id.cardView);
            }
        }
    }

    public interface ChangeComapngeListener<T> {
        public void changeComapny(T file);
    }

    public int dp2px(Context context, float dp) {
        return (int) Math.ceil((double) (context.getResources().getDisplayMetrics().density * dp));
    }
}