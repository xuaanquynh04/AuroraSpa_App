package com.teamone.views;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.teamone.auroraspa.R;
import com.teamone.auroraspa.databinding.InputPasswordBinding;

public class Input_Password extends LinearLayout {
    private InputPasswordBinding binding;
    private boolean isPasswordVisible = false;

    public Input_Password(Context context) {
        super(context);
        init(context);
    }

    public Input_Password(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Input_Password(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        binding = InputPasswordBinding.inflate(LayoutInflater.from(context), this, true);

        EditText edtPassword = binding.txtInput;
        ImageView ivTogglePassword = binding.imvShow;
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        // Đảm bảo icon ban đầu là mắt đóng
        ivTogglePassword.setImageResource(R.drawable.ic_eyeclose);
        isPasswordVisible = false;
        
        ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.ic_eyeclose);
                    isPasswordVisible = false;
                } else {
                    // Đang ẩn → chuyển sang hiện
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.ic_eyeopen);
                    isPasswordVisible = true;
                }

                // Di chuyển con trỏ về cuối
                edtPassword.setSelection(edtPassword.getText().length());
            }
        });
    }

    // Nếu bạn cần lấy text từ bên ngoài
    public String getText() {
        return binding.txtInput.getText().toString();
    }

    // Nếu cần set text từ bên ngoài
    public void setText(String text) {
        binding.txtInput.setText(text);
    }

    // Nếu cần focus hoặc validate từ bên ngoài
    public EditText getEditText() {
        return binding.txtInput;
    }
    public void setLabel(String text) {
        binding.txtLabel.setText(text);
    }
    public void setHint(String hint) {
        binding.txtInput.setHint(hint);
    }
    
    // Lấy text từ EditText
    public String getInputText() {
        return binding.txtInput.getText().toString();
    }
    
    // Lấy EditText để validation
    public EditText getInputView() {
        return binding.txtInput;
    }
    
    // Lấy TextView error để hiển thị lỗi
    public TextView getErrorView() {
        return binding.txtError;
    }

}
