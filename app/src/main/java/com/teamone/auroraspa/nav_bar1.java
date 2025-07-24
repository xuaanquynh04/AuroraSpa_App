package com.teamone.auroraspa;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class nav_bar1 extends ConstraintLayout {

    private ImageView imgTrangchu, imgLichhen, imgTuvan, imgTaikhoan;
    private TextView txtTrangchu, txtLichhen, txtTuvan, txtTaikhoan;
    private OnNavBar1ItemSelected listener;

    public nav_bar1(@NonNull Context context) {
        super(context);
        init(context);
    }

    public nav_bar1(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public nav_bar1(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.nav_bar1, this, true);

        // Layout containers
        LinearLayout navTrangchu = findViewById(R.id.nav_trangchu);
        LinearLayout navLichhen = findViewById(R.id.nav_lichhen);
        LinearLayout navTuvan = findViewById(R.id.nav_tuvan);
        LinearLayout navTaikhoan = findViewById(R.id.nav_taikhoan);

        // ImageViews
        imgTrangchu = findViewById(R.id.imgTrangchu);
        imgLichhen = findViewById(R.id.imgLichhen);
        imgTuvan = findViewById(R.id.imgTuvan);
        imgTaikhoan = findViewById(R.id.imgTaikhoan);

        // TextViews
        txtTrangchu = findViewById(R.id.txtTrangchu);
        txtLichhen = findViewById(R.id.txtLichhen);
        txtTuvan = findViewById(R.id.txtTuvan);
        txtTaikhoan = findViewById(R.id.txtTaikhoan);

        // Click listeners
        navTrangchu.setOnClickListener(v -> {
            setSelected(imgTrangchu, txtTrangchu);
            if (listener != null) listener.onTrangChuClicked();
        });
        navLichhen.setOnClickListener(v -> {
            setSelected(imgLichhen, txtLichhen);
            if (listener != null) listener.onLichHenClicked();
        });
        navTuvan.setOnClickListener(v -> setSelected(imgTuvan, txtTuvan));
        navTaikhoan.setOnClickListener(v -> {
            setSelected(imgTaikhoan, txtTaikhoan);
            if (listener != null) listener.onTaiKhoanClicked();
        });
    }

    private void setSelected(ImageView selectedImg, TextView selectedTxt) {
        clearSelection();

        if (selectedImg != null) {
            selectedImg.setBackgroundResource(R.drawable.bg_round_orange);
        }
        if (selectedTxt != null) {
            selectedTxt.setTextAppearance(getContext(), R.style.fontInter12Bold);
        }
    }
    public void setTrangChuSelected() {
        clearSelection();

        imgTrangchu.setBackgroundResource(R.drawable.bg_round_orange);
        txtTrangchu.setTextAppearance(getContext(), R.style.fontInter12Bold);

    }

    public void clearSelection() {
        imgTrangchu.setBackgroundResource(0);
        imgLichhen.setBackgroundResource(0);
        imgTuvan.setBackgroundResource(0);
        imgTaikhoan.setBackgroundResource(0);

        txtTrangchu.setTextAppearance(getContext(), R.style.fontInter12Reg);
        txtLichhen.setTextAppearance(getContext(), R.style.fontInter12Reg);
        txtTuvan.setTextAppearance(getContext(), R.style.fontInter12Reg);
        txtTaikhoan.setTextAppearance(getContext(), R.style.fontInter12Reg);
    }

    public interface OnNavBar1ItemSelected {
        void onTrangChuClicked();
        void onLichHenClicked();
        void onTaiKhoanClicked();
    }
    public void setOnNavBar1ItemSelected(OnNavBar1ItemSelected listener) {
        this.listener = listener;
    }

}
