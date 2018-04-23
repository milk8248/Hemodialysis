package com.example.lambert.hemodialysis.activity;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lambert.hemodialysis.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class State extends Fragment {

    private View view;
    public State() {
        // Required empty public constructor
    }
   // String NOTIFICATION_CHANNEL_ID = "01";
    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(getActivity(), AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        long futureInMillis = SystemClock.elapsedRealtime() + delay*1000;
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService( Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification(String content) {

        String NOTIFICATION_CHANNEL_ID = "timesup";
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("提醒通知_該出門接家人囉");
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true); //角標
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle("提醒通知：該出門接家人囉");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        return builder.build();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_state, container, false);
        Bundle bundle = getArguments();
        if(bundle!=null){
            String stime = bundle.getString( "time" );
          //  Toast.makeText( getContext(),"time"+stime,Toast.LENGTH_LONG ).show();

            scheduleNotification(getNotification("距離療程完成時間"+stime+"分鐘"),  Integer.valueOf(stime));

        }
        return view;
    }



}
