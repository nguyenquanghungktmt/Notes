package com.example.product_notes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class BottomSheetTags  extends BottomSheetDialogFragment {

    private RelativeLayout btnAddTag;
    private RecyclerView recyclerView;
    private SQLite db = null;
    private ArrayList<Tags> tags;
    private TagsViewAdapter tagsAdapter;
    public BottomSheetTags() {
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

        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_tag,null);
        bottomSheetDialog.setContentView(view);
        btnAddTag = view.findViewById(R.id.btnAddTag);
        recyclerView = view.findViewById(R.id.rcv_Tags);
        db = new SQLite(getContext());
        tags = db.getAllTags();
        tagsAdapter = new TagsViewAdapter(getContext(),tags);
        recyclerView.setAdapter(tagsAdapter);
        recyclerView.setLayoutManager( new GridLayoutManager(getContext(),2));
        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetCreateTag bottomSheetCreateTag = new BottomSheetCreateTag();
                bottomSheetCreateTag.show(getFragmentManager(), "TAG");
                dismiss();
            }
        });
        return bottomSheetDialog;
    }
}