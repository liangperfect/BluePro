package com.vitalong.inclinometer.views;

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

import com.vitalong.inclinometer.R;

import java.io.File;
import java.util.List;

public class CompanySelectDialog extends Dialog {
    private ImageView imgClose;
    private RecyclerView recyclerView;
    private List<File> datas;
    private ChangeComapngeListener listener;

    public CompanySelectDialog(Context context, List<File> d, ChangeComapngeListener l) {
        super(context, R.style.bottom_dialog);
        datas = d;
        listener = l;
        init(context);
    }

    public CompanySelectDialog(Context context, int themeResId, List<File> d) {
        super(context, themeResId);
        datas = d;
        init(context);
    }

    public CompanySelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener, List<File> d) {
        super(context, cancelable, cancelListener);
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

        private List<File> datas;

        public CompanyAdapter(List<File> d) {
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

            holder.name.setText(datas.get(position).getName());
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

    public interface ChangeComapngeListener {
        public void changeComapny(File file);

    }

    public int dp2px(Context context, float dp) {
        return (int) Math.ceil((double) (context.getResources().getDisplayMetrics().density * dp));
    }
}