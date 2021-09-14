package com.example.product_notes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

public class BottomSheetCreateColorTag extends BottomSheetDialogFragment {
    private SQLite db = null;
    private ColorPickerView colorPickerDialog;
    private OnDataCallback onDataCallback;

    public BottomSheetCreateColorTag(OnDataCallback onDataCallback) {
        this.onDataCallback = onDataCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_color, null);
        bottomSheetDialog.setContentView(view);
        db = new SQLite(getContext());
        TextView textView = view.findViewById(R.id.txtMamau);
        View view1 = view.findViewById(R.id.viewColor);
        Button btnSave = view.findViewById(R.id.save_tag);
        colorPickerDialog = view.findViewById(R.id.colorPickerView);
        colorPickerDialog.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                view1.setBackgroundColor(envelope.getColor());
                textView.setText("#" + envelope.getHexCode());
            }
        });
        AlphaSlideBar alphaSlideBar = view.findViewById(R.id.alphaSlideBar);
        colorPickerDialog.attachAlphaSlider(alphaSlideBar);
        BrightnessSlideBar brightnessSlideBar = view.findViewById(R.id.brightnessSlide);
        colorPickerDialog.attachBrightnessSlider(brightnessSlideBar);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = textView.getText().toString();
                onDataCallback.onColorChange(code);
                dismiss();
            }
        });

        Button cancelCreateColor = view.findViewById(R.id.cancel_create_color);
        cancelCreateColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return bottomSheetDialog;
    }
}