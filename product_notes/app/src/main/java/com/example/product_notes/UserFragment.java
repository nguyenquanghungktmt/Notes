package com.example.product_notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UserFragment extends Fragment {

    private LinearLayout layoutTrash, layoutArchive, layoutSetting, layoutTheme;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        layoutTrash = view.findViewById(R.id.toTrash);
        layoutTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), TrashActivity.class);
                startActivity(intent);
            }
        });
        layoutArchive = view.findViewById(R.id.toArchive);
        layoutArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ArchiveActivity.class);
                startActivity(intent);
            }
        });
        layoutSetting = view.findViewById(R.id.toSetting);
        layoutSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        layoutTheme = view.findViewById(R.id.toTheme);
        layoutTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTheme();
            }
        });

        return view;
    }

    private void showDialogTheme() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Choose Theme");
        alertDialog.setCancelable(true);
//        alertDialog.setIcon(R.drawable.ic_baseline_add_24);
//        alertDialog.setIcon(R.drawable.img_notes);

        String[] items = {"Light Theme", "Dark Theme", "Default"};
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case 0:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case 1:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case 2:
                        Toast.makeText(getContext(), "Clicked on Default Theme", Toast.LENGTH_LONG).show();
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                }
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }
}