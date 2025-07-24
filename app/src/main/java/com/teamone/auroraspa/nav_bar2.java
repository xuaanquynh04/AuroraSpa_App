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
import com.teamone.interfaces.OpenTry;

public class nav_bar2 extends ConstraintLayout {

    private ImageView imgTuvan, imgThungay, imgGiohang;
    private TextView txtTuvan, txtThungay, txtGiohang;
    private Button btnDatLich;
    private OpenCustomize openCustomize;
    private OpenTry openTry;

    public nav_bar2(@NonNull Context context) {
        super(context);
        initContext(context);
    }

    public nav_bar2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initContext(context);
    }

    public nav_bar2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContext(context);
    }

    public void setOpenCustomize(OpenCustomize openCustomize) {
        this.openCustomize = openCustomize;
    }
    public void setOpenTryNow(OpenTry openTry) {
        this.openTry= openTry;
    }

    private void initContext(Context context) {
        LayoutInflater.from(context).inflate(R.layout.nav_bar2, this, true);

        // Layout containers
        LinearLayout navTuVan = findViewById(R.id.nav_tuvan);
        LinearLayout navThuNgay = findViewById(R.id.nav_thungay);
        LinearLayout navGioHang = findViewById(R.id.nav_giohang);

        // ImageViews
        imgTuvan = findViewById(R.id.imgTuvan);
        imgThungay = findViewById(R.id.imgThungay);
        imgGiohang = findViewById(R.id.imgGiohang);

        // TextViews
        txtTuvan = findViewById(R.id.txtTuvan);
        txtThungay = findViewById(R.id.txtThungay);
        txtGiohang = findViewById(R.id.txtGiohang);

        //Button
        btnDatLich = findViewById(R.id.btnDatLich);

        // Click
        navTuVan.setOnClickListener(v -> setSelected(imgTuvan, txtTuvan));
        navThuNgay.setOnClickListener(v -> setSelected(imgThungay, txtThungay));
        navGioHang.setOnClickListener(v -> setSelected(imgGiohang, txtGiohang));

        //Mo bottom sheet customize
        btnDatLich.setOnClickListener(v -> {
            if (openCustomize != null) openCustomize.onBookNowClicked(); // gọi callback
        });

        navGioHang.setOnClickListener(v -> {
            setSelected(imgGiohang, txtGiohang);
            if (openCustomize != null) openCustomize.onAddToCartClicked(); // gọi callback
        });
        navThuNgay.setOnClickListener(v -> {
            setSelected(imgThungay, txtThungay);
            if (openTry != null) openTry.onTryNowClicked();
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
        imgThungay.setBackgroundResource(0);
        imgGiohang.setBackgroundResource(0);

        txtTuvan.setTextAppearance(getContext(), R.style.fontInter12Reg);
        txtThungay.setTextAppearance(getContext(), R.style.fontInter12Reg);
        txtGiohang.setTextAppearance(getContext(), R.style.fontInter12Reg);
    }
}
