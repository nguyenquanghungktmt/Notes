package com.example.product_notes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ReminderActivity extends AppCompatActivity {

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    private NotificationManager mNotificationManager;

    private static final String TAG = "myTag";
    private Spinner spinner1, spinner2;
    private LinearLayout iconBack, layoutNone, layoutTimeAlarm, layoutStartDay, layoutEndDay, layoutTimeStart, layoutTimeEnd;
    private TextView txtStartDay, txtEndDay, txtTimeStart, txtTimeEnd;

    private int noteID = 0;
    private String title, content;
    private int incomingTime = 2;
    private int duringTime = 10;

    private Notes recvNote;

    AlarmManager alarmManager;
    PendingIntent notifyPendingIntent;
    Intent sendIntent;

    Calendar calendar, calendarStart, calendarEnd;


    boolean isTimeAlarm = false, isDaily = false, isWeekly = false, isPinBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //get data Note from intent
        Intent intent = getIntent();
        noteID = intent.getIntExtra("noteID", 0);
        title = intent.getStringExtra("noteTitle");
        content = intent.getStringExtra("noteContent");


        //khoi tao notify manager
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        //khoi tao alarm manager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //khai bao 1 intent de truyen info sang alarm receive
        sendIntent = new Intent(ReminderActivity.this, AlarmReceiver.class);

        //get current day and hour
        calendar = Calendar.getInstance();
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);

        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        layoutNone = findViewById(R.id.layout_None);
        layoutTimeAlarm = findViewById(R.id.layout_TimeAlarm);

//        spinner1.setOnItemSelectedListener(this);
//        spinner2.setOnItemSelectedListener(this);

        String[] txtStr1 = getResources().getStringArray(R.array.times);
        String[] txtStr2 = getResources().getStringArray(R.array.types);

        ArrayAdapter adapter1 = new ArrayAdapter(this, R.layout.spinner_item, txtStr1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter adapter2 = new ArrayAdapter(this, R.layout.spinner_item, txtStr2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                if (value.equals("Time alarm")) {
                    layoutNone.setVisibility(View.GONE);
                    layoutTimeAlarm.setVisibility(View.VISIBLE);
                } else if (value.equals("None")) {
                    layoutNone.setVisibility(View.VISIBLE);
                    layoutTimeAlarm.setVisibility(View.GONE);
                } else isPinBar = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                if (value.equals("Daily")) {
                    isDaily = true;
//                    Toast.makeText(ReminderActivity.this, "Choose Daily", Toast.LENGTH_SHORT).show();
                } else if (value.equals("Weekly")) {
                    isWeekly = true;
//                    Toast.makeText(ReminderActivity.this, "Choose Weekly", Toast.LENGTH_SHORT).show();
                } else {
                    isDaily = false;
                    isWeekly = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Create notification
//        createNotificationChannel();
//        registerReceiver(mReceiver,new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        iconBack = findViewById(R.id.back);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        // choose start day
        txtStartDay = findViewById(R.id.txt_day_start_reminder);
        txtStartDay.setText(simpleDateFormat.format(calendar.getTime()));
        layoutStartDay = findViewById(R.id.layout_day_start_reminder);
        layoutStartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendarStart.set(year, month, dayOfMonth);
                        txtStartDay.setText(simpleDateFormat.format(calendarStart.getTime()));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });

        //choose time start
        txtTimeStart = findViewById(R.id.txt_time_start_reminder);
        txtTimeStart.setText(simpleTimeFormat.format(calendarStart.getTime()));
        layoutTimeStart = findViewById(R.id.layout_time_start_reminder);
        layoutTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int hour = calendar.get(Calendar.HOUR);
//                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendarStart.set(Calendar.MINUTE, minute);
                        txtTimeStart.setText(simpleTimeFormat.format(calendarStart.getTime()));
                    }
                }, currentHour, currentMin, true);
                timePickerDialog.show();
            }
        });


        // choose end day
        txtEndDay = findViewById(R.id.txt_day_end_reminder);
        txtEndDay.setText(simpleDateFormat.format(calendarEnd.getTime()));
        layoutEndDay = findViewById(R.id.layout_day_end_reminder);
        layoutEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendarEnd.set(year, month, dayOfMonth);
                        txtEndDay.setText(simpleDateFormat.format(calendarEnd.getTime()));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });

        //choose time end
        txtTimeEnd = findViewById(R.id.txt_time_end_reminder);
        txtTimeEnd.setText(simpleTimeFormat.format(calendarEnd.getTime()));
        layoutTimeEnd = findViewById(R.id.layout_time_end_reminder);
        layoutTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int hour = calendar.get(Calendar.HOUR);
//                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendarEnd.set(Calendar.MINUTE, minute);
                        txtTimeEnd.setText(simpleTimeFormat.format(calendarEnd.getTime()));
                    }
                }, currentHour, currentMin, true);
                timePickerDialog.show();
            }
        });

    } // end onCreate

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pinStatusBar(View view) {
        // make reminder
        makeReminder(noteID, title, content, 0L);
        finish();
        Toast.makeText(this, "Pin to Status bar", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void reminderToday(View view) {
        // make reminder
        makeReminder(noteID, title, content, 0L);

        // cancel reminder
        cancelReminder(noteID, AlarmManager.INTERVAL_DAY);

        finish();
        Toast.makeText(this, "Reminder today", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void reminder30Min(View view) {
        // make reminder
        makeReminder(noteID, title, content, AlarmManager.INTERVAL_HALF_HOUR);

        finish();
        Toast.makeText(this, "Reminder in 30 min", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void reminder15Min(View view) {
        //fix here
        // make a reminder
        makeReminder(noteID, title, content, AlarmManager.INTERVAL_FIFTEEN_MINUTES);

        finish();
        Toast.makeText(this, "Reminder in 15 min", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveReminder(View view) {

        if (isPinBar) {
            Toast.makeText(this, "Pin to status bar", Toast.LENGTH_SHORT).show();
            makeReminder(noteID, title, content, 0L);
            finish();
        }

        if (isDaily) {
            makeRepetition(noteID, title, content, 1);
            Toast.makeText(this, "Reminder Daily", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (isWeekly) {
            makeRepetition(noteID, title, content, 2);
            Toast.makeText(this, "Reminder Weekly", Toast.LENGTH_SHORT).show();
            finish();
        }


        long intervalIncoming = calendarStart.getTimeInMillis() - calendar.getTimeInMillis();
        long intervalCanceling = calendarEnd.getTimeInMillis() - calendar.getTimeInMillis();
        Log.d(TAG, "saveReminder: " + intervalIncoming + " " + intervalCanceling);
        if (intervalIncoming >= intervalCanceling) {
            Toast.makeText(this, "ERROR with time to start and finish", Toast.LENGTH_SHORT).show();
        } else isTimeAlarm = true;

        if (isTimeAlarm) {
            makeReminder(noteID, title, content, intervalIncoming);
            cancelReminder(noteID, intervalCanceling);
            Toast.makeText(this, "Set Time Alarm", Toast.LENGTH_SHORT).show();
//            Log.d("myTag", "picked: " + intervalIncoming
//                    + " " + intervalCanceling);
            finish();
        }
    }


//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void startJob(int knoteID) {
//        ComponentName cn = new ComponentName(this, NotificationService.class);
//
//        PersistableBundle bundle = new PersistableBundle();
//        bundle.putInt("noteID", knoteID);
//        bundle.putInt("incomingTime", incomingTime);
//        bundle.putInt("duringTime", duringTime);
//        bundle.putString("title", title);
//        bundle.putString("content", content);
//
//        JobInfo jobInfo = new JobInfo.Builder(knoteID, cn)
//                .setRequiresDeviceIdle(false)
//                .setPersisted(true)
//                .setExtras(bundle)
//                .build();
//
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        jobScheduler.schedule(jobInfo);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void cancelJob(int knoteID) {
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        jobScheduler.cancel(knoteID);
////        Log.d("myTag", "click cancel job " + knoteID);
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dismissReminder(View view) {
        mNotificationManager.cancel(noteID);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void makeReminder(int ID, String sendTitle, String sendContent, long interval) {

        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        notifyIntent.putExtra("NOTIFICATION_ID", ID);
        notifyIntent.putExtra("title", sendTitle);
        notifyIntent.putExtra("content", sendContent);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerTime = System.currentTimeMillis() + interval;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, notifyPendingIntent);

        // Create the notification channel.
        createNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void cancelReminder(int ID, long interval) {

        Intent cancelNotifyIntent = new Intent(this, CancelNotify.class);
        cancelNotifyIntent.putExtra("NOTIFICATION_ID", ID);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, ID, cancelNotifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerTime = System.currentTimeMillis() + interval;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, notifyPendingIntent);
        // Create the notification channel.
        createNotificationChannel();
    }

    public void makeRepetition(int ID, String sendTitle, String sendContent, int flag) {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        notifyIntent.putExtra("NOTIFICATION_ID", ID);
        notifyIntent.putExtra("title", sendTitle);
        notifyIntent.putExtra("content", sendContent);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        long repeatInterval = AlarmManager.INTERVAL_DAY;

        long triggerTime = SystemClock.elapsedRealtime();
        // flag 1 is daily, flag 2 is weekly
        switch (flag) {
            case 1:
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime, repeatInterval,
                        notifyPendingIntent);
                break;
            case 2:
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime, 7 * repeatInterval,
                        notifyPendingIntent);
                break;

            default:
                // do nothing
        }

        // Create the notification channel.
        createNotificationChannel();
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Stand up notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Note notify");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

}