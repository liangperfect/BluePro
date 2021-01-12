package com.vitalong.bluetest2.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vitalong.bluetest2.R;
import com.vitalong.bluetest2.bean.HoleBean;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HoleMultiSelectDialog extends Dialog {
    private ImageView imgClose;
    private ImageView imgAllSelected;
    private ConstraintLayout constraSelectAll;
    private RecyclerView recyclerView;
    private TextView tvTitle;
    private TextView tvConfirm;
    private String dialogTitle;
    private List<HoleBean> datas;
    private ChangeHoleListener listener;
    private boolean isAllSelected = false;

    public HoleMultiSelectDialog(Context context, List<HoleBean> d, String title, ChangeHoleListener l) {
        super(context, R.style.bottom_dialog);
        dialogTitle = title;
        datas = d;
        listener = l;
        init(context);
    }

    public HoleMultiSelectDialog(Context context, int themeResId, List<HoleBean> d, String title) {
        super(context, themeResId);
        dialogTitle = title;
        datas = d;
        init(context);
    }

    public HoleMultiSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener, List<HoleBean> d, String title) {
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
        View view = getLayoutInflater().inflate(R.layout.hole_multi_select, null, false);
        setContentView(view);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(dialogTitle);
        imgClose = view.findViewById(R.id.imgClose);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        imgAllSelected = view.findViewById(R.id.imgAllSelected);
        constraSelectAll = view.findViewById(R.id.constraSelectAll);

        constraSelectAll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                isAllSelected = !isAllSelected;
                imgAllSelected.setImageResource(isAllSelected ? R.drawable.check_box_selected : R.drawable.check_box_unselected);
                datas.forEach(holeBean -> holeBean.setChecked(isAllSelected));
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HoleMultiSelectDialog.this.dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                List<HoleBean> confirmHoles = new ArrayList<>();
                datas.forEach(new Consumer<HoleBean>() {
                    @Override
                    public void accept(HoleBean b) {

                        if (b.isChecked()) {
                            confirmHoles.add(b);
                        }
                    }
                });
                listener.selectMultiHoles(confirmHoles);
                HoleMultiSelectDialog.this.dismiss();
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

        private List<HoleBean> datas;

        public CompanyAdapter(List<HoleBean> d) {
            this.datas = d;
        }

        @NonNull
        @Override
        public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_hole, parent, false);
            ViewHodler viewHodler = new ViewHodler(view);
            return viewHodler;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHodler holder, int position) {
            HoleBean hole = datas.get(position);
            String holeName = hole.getHoleName();
            holder.name.setText(holeName);
            holder.imgCheckState.setImageResource(hole.isChecked() ? R.drawable.check_box_selected : R.drawable.check_box_unselected);
            holder.cardView.setOnClickListener(v -> {

                hole.setChecked(!hole.isChecked());
                int resId = hole.isChecked() ? R.drawable.check_box_selected : R.drawable.check_box_unselected;
                holder.imgCheckState.setImageResource(resId);
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
            ImageView imgCheckState;
            TextView tvConfirm;

            public ViewHodler(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.imgCompany);
                name = itemView.findViewById(R.id.tvCompanyName);
                cardView = itemView.findViewById(R.id.cardView);
                imgCheckState = itemView.findViewById(R.id.imgCheckBoxHole);
            }
        }
    }

    public interface ChangeHoleListener {
        public void selectHole(HoleBean hole);

        public void selectMultiHoles(List<HoleBean> holes);
    }

    public int dp2px(Context context, float dp) {
        return (int) Math.ceil((double) (context.getResources().getDisplayMetrics().density * dp));
    }
}