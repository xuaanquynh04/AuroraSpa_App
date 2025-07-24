package com.teamone.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.teamone.auroraspa.R;
import com.teamone.auroraspa.databinding.CollapsibleHeaderBinding;

public class Collapsible_Header extends LinearLayout {

    private CollapsibleHeaderBinding binding;
    private View contentToToggle;
    private boolean isContentVisible = false;

    public Collapsible_Header(Context context) {
        super(context);
        init(context,null);
    }

    public Collapsible_Header(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public Collapsible_Header(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init (Context context, @Nullable AttributeSet attrs){
        // preview tren layout
        if (isInEditMode()) {
            LayoutInflater.from(context).inflate(R.layout.collapsible_header, this, true);
            return;
        }
        binding = CollapsibleHeaderBinding.inflate(LayoutInflater.from(context),this,true);
        binding.collapsibleHeader.setOnClickListener(v -> toggleContent());

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CollapsibleHeaderView,
                    0, 0);
            try {
                String title = a.getString(R.styleable.CollapsibleHeaderView_title);
                if (title != null) {
                    binding.headerTitle.setText(title);
                }
                isContentVisible = a.getBoolean(R.styleable.CollapsibleHeaderView_initialExpanded, false);

            } finally {
                a.recycle();
            }
        }

        updateContentVisibility();
    }

    public void setContentToToggle(View content) {
        this.contentToToggle = content;
        updateContentVisibility();
    }
//
    private void updateContentVisibility() {
        if (contentToToggle == null) return;

        if (isContentVisible) {
            contentToToggle.setVisibility(VISIBLE);
            binding.headerArrow.setRotation(180);
        } else {
            contentToToggle.setVisibility(GONE);
            binding.headerArrow.setRotation(0);
        }
    }

    private void toggleContent() {
        if (contentToToggle == null){
                Toast.makeText(getContext(), "Không có sản phẩm trong giỏ hàng", Toast.LENGTH_LONG).show();
                return;
            }

        if (isContentVisible) {
            contentToToggle.setVisibility(GONE);
            binding.headerArrow.animate().rotation(0).setDuration(200).start();
            isContentVisible = false;
        } else {
            contentToToggle.setVisibility(VISIBLE);
            binding.headerArrow.animate().rotation(180).setDuration(200).start();
            isContentVisible = true;
        }

    }
    public void setTitle(String titleString) {
        binding.headerTitle.setText(titleString);
    }
}
