package com.teamone.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SuggestSearchAdapter extends ArrayAdapter<String> {
    public SuggestSearchAdapter(@NonNull Context context, @NonNull ArrayList<String> suggestions) {
        super(context, android.R.layout.simple_dropdown_item_1line, suggestions);
    }
    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }
}


