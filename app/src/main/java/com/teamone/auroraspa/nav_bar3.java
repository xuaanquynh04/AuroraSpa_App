package com.teamone.auroraspa;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.teamone.interfaces.OpenCustomize;

public class nav_bar3 extends ConstraintLayout {

    private ImageView imgTuvan, imgGiohang;
    private TextView txtTuvan, txtGiohang;
    private OpenCustomize openCustomize;
    private Button btnDatLich;

    public nav_bar3(@NonNull Context context) {
        super(context);
        initContext(context);
    }

    public nav_bar3(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initContext(context);
    }

    public nav_bar3(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContext(context);
    }


    private void initContext(Context context) {
        LayoutInflater.from(context).inflate(R.layout.nav_bar3, this, true);

        // Layout containers
        LinearLayout navTuVan = findViewById(R.id.nav_tuvan);
        LinearLayout navGioHang = findViewById(R.id.nav_giohang);

        // ImageViews
        imgTuvan = findViewById(R.id.imgTuvan);
        imgGiohang = findViewById(R.id.imgGiohang);

        // TextViews
        txtTuvan = findViewById(R.id.txtTuvan);
        txtGiohang = findViewById(R.id.txtGiohang);

        //Button
        btnDatLich = findViewById(R.id.btnDatLich);

        // Click
        navTuVan.setOnClickListener(v -> setSelected(imgTuvan, txtTuvan));
        navGioHang.setOnClickListener(v -> setSelected(imgGiohang, txtGiohang));

        btnDatLich.setOnClickListener(v -> {
            if (openCustomize != null) {
                openCustomize.onBookNowClicked();
            }
        });

        navGioHang.setOnClickListener(v -> {
            setSelected(imgGiohang, txtGiohang);
            if (openCustomize != null) {
                openCustomize.onAddToCartClicked();
            }
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

    public void clearSelection() {
        imgTuvan.setBackgroundResource(0);
        imgGiohang.setBackgroundResource(0);

        txtTuvan.setTextAppearance(getContext(), R.style.fontInter12Reg);
        txtGiohang.setTextAppearance(getContext(), R.style.fontInter12Reg);
    }

    public void setOpenCustomize(OpenCustomize openCustomize) {
        this.openCustomize = openCustomize;
    }

}
