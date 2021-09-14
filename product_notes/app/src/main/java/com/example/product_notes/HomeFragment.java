package com.example.product_notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class HomeFragment extends Fragment {
    public static final String SHARED_PREFS = "sharedPrefs";

    private View view;
    private Context context;

    private ArrayList<Notes> mNotes;
    private ArrayList<Content> mContent;

    private RecyclerView mRecyclerNotes, recyclerViewTagColor;

    private NotesAdapter mNotesAdapter;

    private ImageButton btnAddColorTag, btnMenuSetting;

    private EditText editTextSearch;
    private ImageButton btnCloseSearch;

    private Dialog dialog;
    private SQLite db;
    private ArrayList<Tags> tags;
    private FilterTagAdapter filterTagAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView btnNone, btnText, btnChecklist, btnNewestDay, btnTag, btnReminder;

    //default setting
    private String defaultColor, defaultSort;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_home, container, false);
         context = view.getContext();

        connectView(view);

         db = new SQLite(context);

        mNotes = db.getAllNotes();
        // bind mNotes to mNotesAdapter
        mNotesAdapter = new NotesAdapter(context, mNotes);
        mRecyclerNotes.setAdapter(mNotesAdapter);
        mRecyclerNotes.setLayoutManager(new LinearLayoutManager(context));

        initialize();

        btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonCategoryState(true, false, false, false, false, false);
                onFilterNone();
            }
        });

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonCategoryState(false, true, false, false, false, false);
                onFilterText();
            }
        });

        btnChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonCategoryState(false, false, true, false, false, false);
                onFilterChecklist();
            }
        });

        btnNewestDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonCategoryState(false, false, false, true, false, false);
                onFilterDay();
            }
        });

        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonCategoryState(false, false, false, false, true, false);
                onFilterTag();
            }
        });

        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonCategoryState(false, false, false, false, false, true);
                onFilterReminder();
            }
        });


        //set color tag data for recyclerViewTagColor
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_default_color);

        tags = db.getAllTags();
        recyclerViewTagColor = dialog.findViewById(R.id.rcv_dialog_color);

        filterTagAdapter = new FilterTagAdapter(context, tags, new OnFilterTag() {
            @Override
            public void onFilterTag(String code) {
                ArrayList<Notes> arrayList = new ArrayList();
                for (int i = 0; i < mNotes.size(); i++) {
                    String string1 = mNotes.get(i).getTag().toUpperCase();
                    if (string1.equals(code)) {
                        arrayList.add(mNotes.get(i));
                    }
                }
                mNotesAdapter.replaceData(arrayList);
                dialog.dismiss();
            }
        });

        recyclerViewTagColor.setAdapter(filterTagAdapter);
        recyclerViewTagColor.setLayoutManager(new GridLayoutManager(getContext(), 2));


        // handle click btnAddColorTag
        btnAddColorTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetTags bottomSheetTags = new BottomSheetTags();
                bottomSheetTags.show(getFragmentManager(), "TAG");
            }
        });

        // handle click btnMenuSetting
        btnMenuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Setting", Toast.LENGTH_SHORT).show();
            }
        });

        editTextSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextSearch.setCursorVisible(true);
                editTextSearch.setSelected(true);
                btnCloseSearch.setVisibility(View.VISIBLE);
                return false;
            }
        });

        // handle click search box
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Notes> arrayList = new ArrayList();
                for (int i = 0; i < mNotes.size(); i++) {
                    String string1 = mNotes.get(i).getTitle().toUpperCase();
                    if (string1.indexOf(s.toString().toUpperCase()) >= 0) {
                        arrayList.add(mNotes.get(i));
                    } else {
                        mContent = db.getAllContentByIDNote(mNotes.get(i).getId());
                        for (int j = 0; j < mContent.size(); j++) {
                            String string2 = mContent.get(j).getContent().toUpperCase();
                            if (string2.indexOf(s.toString().toUpperCase()) >= 0) {
                                arrayList.add(mNotes.get(i));
                            }
                        }
                    }
                }
                mNotesAdapter.replaceData(arrayList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        handle btnCloseSearch
        btnCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                //hide the keyboard
//                if (inputManager != null) {
//                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
//                    );
//                }

                //hide the pointer in edit text
                editTextSearch.setText("");
                editTextSearch.setCursorVisible(false);
                editTextSearch.setSelected(false);

                //hide button close
                btnCloseSearch.setVisibility(View.INVISIBLE);

                //restore data
                onFilterNone();
            }
        });

        // swipe refresh screen
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHomeScreen();
            }
        });

        return view;
    }

    private void refreshHomeScreen(){
        setButtonCategoryState(false, false, false, false, false, false);
//        mNotes.clear();
//        mNotes = db.getAllNotes();
//        mNotesAdapter.replaceData(mNotes);
//        mNotesAdapter.notifyDataSetChanged();
        initialize();
        swipeRefreshLayout.setRefreshing(false);
    }


    private void connectView(View view){
        btnAddColorTag = view.findViewById(R.id.btn_add_color_tag);
        btnMenuSetting = view.findViewById(R.id.menu);
        mRecyclerNotes = view.findViewById(R.id.recycler_Notes);

        // search box
        editTextSearch = view.findViewById(R.id.edit_text_search);
        btnCloseSearch = view.findViewById(R.id.btn_close_search);

        // button categories
        btnNone = view.findViewById(R.id.btn_category_none);
        btnText = view.findViewById(R.id.btn_category_text);
        btnChecklist = view.findViewById(R.id.btn_category_checklist);
        btnNewestDay = view.findViewById(R.id.btn_category_newest_day);
        btnTag = view.findViewById(R.id.btn_category_tag);
        btnReminder = view.findViewById(R.id.btn_category_reminder);

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout_refresh);
    }

    private void setButtonCategoryState(Boolean isNoneSelected, Boolean isTextSelected,
                                        Boolean isChecklistSelected, Boolean isDaySelected,
                                        Boolean isTagSelected, Boolean isReminderSelected){
        btnNone.setSelected(isNoneSelected);
        btnText.setSelected(isTextSelected);
        btnChecklist.setSelected(isChecklistSelected);
        btnNewestDay.setSelected(isDaySelected);
        btnTag.setSelected(isTagSelected);
        btnReminder.setSelected(isReminderSelected);
    }

    private void onFilterNone() {
        ArrayList<Notes> arrayList = db.getAllNotes();
        mNotesAdapter.replaceData(arrayList);
    }

    private void onFilterText() {
        ArrayList<Notes> arrayList = db.getAllTextNotes();
//        for (int i = 0; i < mNotes.size(); i++) {
//            String string1 = mNotes.get(i).getType().toUpperCase();
//            if (string1.equals("TEXT")) {
//                arrayList.add(mNotes.get(i));
//            }
//        }

        mNotesAdapter.replaceData(arrayList);
    }


    private void onFilterChecklist() {
//        ArrayList<Notes> arrayList = new ArrayList();
//        for (int i = 0; i < mNotes.size(); i++) {
//            String string1 = mNotes.get(i).getType().toUpperCase();
//            if (string1.equals("CHECKLIST")) {
//                arrayList.add(mNotes.get(i));
//            }
//        }

        ArrayList<Notes> arrayList = db.getAllChecklistNotes();
        mNotesAdapter.replaceData(arrayList);
    }

    private void onFilterDay() {
//        mNotes.clear();
//        mNotes = db.getAllNotesOrderByUpdate();
//        mNotesAdapter.notifyDataSetChanged();

        ArrayList<Notes> arrayList = db.getAllNotesOrderByUpdate();
        mNotesAdapter.replaceData(arrayList);
    }

    private void onFilterTag() {
        dialog.show();
    }

    private void onFilterReminder() {
        ArrayList<Notes> arrayList = db.getAllNotesOrderByReminder();
        mNotesAdapter.replaceData(arrayList);
    }

    private void sortNoteByModifiedTime(){
        ArrayList<Notes> arrayList = db.getAllNotesOrderByUpdate();
        mNotesAdapter.replaceData(arrayList);
//        mNotes.clear();
//        mNotes = db.getAllNotesOrderByUpdate();
//        mNotesAdapter.notifyDataSetChanged();
//        Log.d("myTag", "get default setting: " + defaultSort);
//        Log.d("myTag", "get default setting: " + mNotes.get(0).getTitle());
    }

    private void sortNoteByCreatedTime(){
        Log.d("myTag", "get default setting: " + defaultSort);
//        mNotes.clear();
//        mNotes = db.getAllNotesOrderByCreate();
//        mNotesAdapter.notifyDataSetChanged();

        ArrayList<Notes> arrayList = db.getAllNotesOrderByCreate();
        mNotesAdapter.replaceData(arrayList);
    }

    private void initialize(){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        defaultSort = prefs.getString("defaultSort", "defaultSort");

        if (defaultSort.equals("By modified time")){
            sortNoteByModifiedTime();
        }
        else if (defaultSort.equals("By created time")){
            sortNoteByCreatedTime();
        }
        else {
//            mNotes.clear();
//            mNotes = db.getAllNotes();
//            mNotesAdapter.notifyDataSetChanged();

            ArrayList<Notes> arrayList = db.getAllNotes();
            mNotesAdapter.replaceData(arrayList);
        }
    }


    void setDefaultColorForHeader(View view) {
        SharedPreferences prefs = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        defaultColor = prefs.getString("defaultColor", String.valueOf(R.color.default_color));

        LinearLayout headerHome = view.findViewById(R.id.header_home);
        headerHome.setBackgroundColor(Color.parseColor(defaultColor));

        RelativeLayout header = view.findViewById(R.id.header);
        header.setBackgroundColor(Color.parseColor(defaultColor));
    }

    @Override
    public void onResume() {
        super.onResume();
        initialize();
    }

}