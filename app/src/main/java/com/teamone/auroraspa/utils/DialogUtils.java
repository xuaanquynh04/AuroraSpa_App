package com.teamone.auroraspa.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamone.auroraspa.R;

public class DialogUtils {
    private static AlertDialog loadingDialog;
    private static AlertDialog successDialog;

    public static void autoShowDialogs(Context context, String message,int image) {
        showLoading(context);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            hideLoading();
            showSuccess(context, message,image);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                hideSuccess();
            }, 1500);

        }, 700);
    }

    private static void showLoading(Context context) {
        if (loadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
            builder.setView(view);
            builder.setCancelable(false);
            loadingDialog = builder.create();

            if (loadingDialog.getWindow() != null) {
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private static void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private static void showSuccess(Context context, String message, int icon) {
        if (successDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_announcement, null);

            TextView tvMessage = view.findViewById(R.id.success_message);
            ImageView ivIcon = view.findViewById(R.id.success_icon);
            tvMessage.setText(message);
            ivIcon.setImageResource(icon);
            builder.setView(view);
            successDialog = builder.create();
            successDialog.setCancelable(true);

            if (successDialog.getWindow() != null) {
                successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        if (!successDialog.isShowing()) {
            successDialog.show();
        }
    }

    private static void hideSuccess() {
        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
            successDialog = null;
        }
    }
}
