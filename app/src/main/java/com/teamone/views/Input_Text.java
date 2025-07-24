package com.teamone.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.teamone.auroraspa.R;

public class Input_Text extends LinearLayout {
    private TextView label;
    private EditText input;
    public Input_Text(Context context) {
        super(context);
    }

    public Input_Text(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Input_Text(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Input_Text(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.input_text,this,true);
        label = findViewById(R.id.txtLabel);
        input = findViewById(R.id.txtInput);
    }
}
