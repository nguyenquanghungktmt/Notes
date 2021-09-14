package com.example.product_notes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.product_notes.ColorDefaultAdapter.SHARED_PREFS;

public class SettingActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";

    private LinearLayout btnBack;
    private LinearLayout layoutSetDefaultColor, layoutSetDefaultFont, layoutSetDefaultItemHeight, layoutSetDefaultSort;
    private View boxColor;
    private TextView txtSize, txtItemHeight;

    private Dialog dialogColor;
    private RecyclerView recyclerColor;
    private ColorDefaultAdapter colorDefaultAdapter;
    private ArrayList<Tags> tags = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String defaultColor, defaultSize, defaultItemHeight, defaultSort;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        connectView();

        init();


        // onclick btn Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //connect dialog color
        dialogColor = new Dialog(this);
        dialogColor.setContentView(R.layout.dialog_default_color);

        //create tags with 9 colors
        String[] colorsList = getResources()
                .getStringArray(R.array.setting_colors);
        for (int i = 0; i < colorsList.length; i++)
            tags.add(new Tags("color" + i, colorsList[i].toString()));

        recyclerColor = dialogColor.findViewById(R.id.rcv_dialog_color);
        colorDefaultAdapter = new ColorDefaultAdapter(this, tags, dialogColor, new onDefaultColor() {
            @Override
            public void onChangeColor(String code) {
                boxColor.setBackgroundColor(Color.parseColor(code));
            }
        });

        recyclerColor.setAdapter(colorDefaultAdapter);
        recyclerColor.setLayoutManager(new GridLayoutManager(this, 3));

        // onclick layout set default color
        layoutSetDefaultColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogColor.show();
            }
        });


//        dialogFontSize = new Dialog(this);
//        dialogFontSize.setContentView(R.layout.dialog_default_fontsize);
//        recyclerFontSize = dialogFontSize.findViewById(R.id.rcv_dialog_fontsize);
//        fontSizeAdapter = new FontSizeAdapter(this, fontSizes, dialogFontSize, new onDefaultSize() {
//            @Override
//            public void onChangeSize(String str) {
//                txtSize.setText(str);
//            }
//        });
//        recyclerFontSize.setAdapter(fontSizeAdapter);
//        recyclerFontSize.setLayoutManager(new LinearLayoutManager(this));
//        layoutSetDefaultFont = findViewById(R.id.set_default_font_size);

        // onclick set default font size
        layoutSetDefaultFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogFontSize.setTitle("Default font size");
//                dialogFontSize.show();
                showDialogSetDefaultFontSize();
            }
        });

        // onclick set default item height
        layoutSetDefaultItemHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSetDefaultItemHeight();
            }
        });

        // onclick set default sort
        layoutSetDefaultSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSetDefaultSort();
            }
        });
    }

    private void showDialogSetDefaultSort() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Default sort notes");
        alertDialog.setCancelable(true);

        final int[] option = {0};
        String[] items = {"By modified time", "By created time"};

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editor.putString("defaultSort", items[option[0]]);
                editor.apply();
                dialog.cancel();
            }
        });

        for (int i = 0; i < items.length; i++){
            if (defaultSort.equals(items[i])) option[0] = i;
        }

        alertDialog.setSingleChoiceItems(items, option[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                option[0] = choice;
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void showDialogSetDefaultFontSize() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("List font size");
        alertDialog.setCancelable(true);

        final int[] option = {0};
        String[] items = {"Tiny", "Small", "Medium", "Large", "Huge"};

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                txtSize.setText(items[option[0]]);
                editor.putString("defaultSize", items[option[0]]);
                editor.apply();
                dialog.cancel();
            }
        });

        for (int i = 0; i < items.length; i++){
            if (defaultSize.equals(items[i])) option[0] = i;
        }

        alertDialog.setSingleChoiceItems(items, option[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                option[0] = choice;
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void showDialogSetDefaultItemHeight() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("List item height");
        alertDialog.setCancelable(true);

        final int[] option = {0};
        String[] items = {"Tiny", "Small", "Normal"};

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                txtItemHeight.setText(items[option[0]]);
                editor.putString("defaultItemHeight", items[option[0]]);
                editor.apply();
                dialog.cancel();
            }
        });

        for (int i = 0; i < items.length; i++){
            if (defaultItemHeight.equals(items[i])) option[0] = i;
        }

        alertDialog.setSingleChoiceItems(items, option[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                option[0] = choice;
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void connectView() {
        btnBack = findViewById(R.id.back);

        //layout
        layoutSetDefaultColor = findViewById(R.id.set_default_color);
        layoutSetDefaultFont = findViewById(R.id.set_default_font_size);
        layoutSetDefaultItemHeight = findViewById(R.id.set_default_item_height);
        layoutSetDefaultSort = findViewById(R.id.layout_set_default_sort);

        //view and text view
        boxColor = findViewById(R.id.box_color);
        txtSize = findViewById(R.id.txt_size);
        txtItemHeight = findViewById(R.id.txt_item_height);

        //connect share preference
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public void init() {
        defaultColor = sharedPreferences.getString("defaultColor", getResources().getString(R.color.background_color));
        defaultSize = sharedPreferences.getString("defaultSize", "Tiny");
        defaultItemHeight = sharedPreferences.getString("defaultItemHeight", "Tiny");
        defaultSort = sharedPreferences.getString("defaultSort", "By modified time");

        boxColor.setBackgroundColor(Color.parseColor(defaultColor));
        txtSize.setText(defaultSize);
        txtItemHeight.setText(defaultItemHeight);

        Log.d("myTag", "defaultSize: " + defaultSize);
    }

//    public void createListSize() {
//        fontSizes = new ArrayList<>();
//        fontSizes.add(new FontSize("Tiny"));
//        fontSizes.add(new FontSize("Small"));
//        fontSizes.add(new FontSize("Medium"));
//        fontSizes.add(new FontSize("Large"));
//        fontSizes.add(new FontSize("Huge"));
//    }
}