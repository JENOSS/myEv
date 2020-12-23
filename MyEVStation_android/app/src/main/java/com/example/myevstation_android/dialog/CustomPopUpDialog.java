package com.example.myevstation_android.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.myevstation_android.R;
import com.example.myevstation_android.UtilFunction;

import java.util.Objects;

public class CustomPopUpDialog {
    private Context context;
    private AlertDialog alertDialog;
    private ButtonListener buttonListener;
    private TextView button;

    public interface ButtonListener{
        void clickButton(AlertDialog alertDialog);
    }

    public CustomPopUpDialog(Context context, ButtonListener buttonListener){
        this.context = context;
        this.buttonListener = buttonListener;

        LayoutInflater li = LayoutInflater.from(context);
        View modalView = li.inflate(R.layout.custom_popup_dialog, null);

        GradientDrawable shape =  new GradientDrawable();
        shape.setColor(Color.WHITE);
        int radius = UtilFunction.dpToPx(9);
        float[] rad = {radius, radius,radius, radius, radius, radius,radius, radius};
        shape.setCornerRadii(rad);
        modalView.setBackground(shape);

        button = modalView.findViewById(R.id.button);
        button.setOnClickListener(v-> buttonListener.clickButton(alertDialog));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(modalView);
        alertDialogBuilder.setCancelable(false);

        alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void execute(){
        alertDialog.show();
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setLayout((int) (UtilFunction.getScreenWidth((Activity) context) * 0.87), alertDialog.getWindow().getAttributes().height);
        }
    }


}
