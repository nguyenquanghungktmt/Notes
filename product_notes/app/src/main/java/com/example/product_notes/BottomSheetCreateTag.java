package com.example.product_notes;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetCreateTag extends BottomSheetDialogFragment {
    private SQLite db = null;
    private TextView txtColor;
    private EditText editTitle;

    public BottomSheetCreateTag() {

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

        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_create_tag, null);
        bottomSheetDialog.setContentView(view);
        View viewColor = view.findViewById(R.id.box_color);
        txtColor = view.findViewById(R.id.txt_color);
        editTitle = view.findViewById(R.id.txt_title_tag);
        viewColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetCreateColorTag bottomSheetCreateColorTag = new BottomSheetCreateColorTag(new OnDataCallback() {
                    @Override
                    public void onColorBackgroundChange(int color) {

                    }

                    @Override
                    public void onmNotesChange() {

                    }

                    @Override
                    public void onColorChange(String code) {
//                        System.out.println("--ahihi " + txtColor.getText());
                        txtColor.setText(code);
//                        System.out.println("--ahihi " + txtColor.getText());
                        Drawable drawable = viewColor.getBackground();
                        if (drawable instanceof GradientDrawable) {
                            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                            gradientDrawable.setColor(Color.parseColor(code));
                            viewColor.setBackground(gradientDrawable);
                        }
                    }
                });
                bottomSheetCreateColorTag.show(getFragmentManager(), "TAG");
            }
        });
        db = new SQLite(getContext());
        Button btnSave = view.findViewById(R.id.save_tag);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = txtColor.getText().toString();
                String title = editTitle.getText().toString();
                Log.d("myTag","code: " + code + "title" + title);
                db.addTag(new Tags(title, code));
                BottomSheetTags bottomSheetTags = new BottomSheetTags();
                bottomSheetTags.show(getFragmentManager(), "TAG");
                Drawable drawable = viewColor.getBackground();
                if (drawable instanceof GradientDrawable) {
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                    gradientDrawable.setColor(Color.parseColor("#ffffff"));
                    viewColor.setBackground(gradientDrawable);
                }
                dismiss();
            }
        });

        Button cancelCreateTag = view.findViewById(R.id.cancel_create_tag);
        cancelCreateTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return bottomSheetDialog;
    }
}