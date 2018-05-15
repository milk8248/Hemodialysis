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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lambert.hemodialysis.R;
import com.example.lambert.hemodialysis.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pl.pawelkleczkowski.customgauge.CustomGauge;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class State extends Fragment {

    private View view;

    private TextView tvBed;
    private TextView tvName;
    private TextView tvVP;
    private TextView tvProgress;
    private TextView tvTMP;
    private TextView tvBFLW;
    private TextView tvNAC;
    private TextView tvUFV;
    private TextView tvUFG;
    private TextView tvUFR;
    private TextView tvPulse;
    private TextView tvSYS;
    private TextView tvDIA;
    private CustomGauge gauge2;

    private Handler mThreadHandler;
    private HandlerThread mThread;

    volatile boolean activityStopped = false;

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

//        getBedInfo();
        mThread = new HandlerThread("name");
        mThread.start();
        mThreadHandler=new Handler(mThread.getLooper());
        mThreadHandler.post(r1);

        Bundle bundle = getArguments();
        if(bundle!=null){
            String stime = bundle.getString( "time" );


            int sHour = getHour(stime);
            int sMinute = getMinute(stime);
            int totalMinute = sHour*60+sMinute;

          //  Toast.makeText( getContext(),"time"+stime,Toast.LENGTH_LONG ).show();
            Toast toast = Toast.makeText(view.getContext(), "已設定時間，預計療程完成前"+sHour+"小時"+sMinute+"分鐘提醒", Toast.LENGTH_LONG);
            toast.show();

            scheduleNotification(getNotification("距離療程完成時間"+sHour+"小時"+sMinute+"分鐘"),  Integer.valueOf(totalMinute));

        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        activityStopped = true;
        mThread.quit();
    }

    private static final String VALIDATION_EXPRESSION = "[0-2]*[0-9]:[0-5]*[0-9]";

    private int getHour(String time) {
        if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
            return -1;
        }
        return Integer.valueOf(time.split(":")[0]);
    }

    private int getMinute(String time) {
        if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
            return -1;
        }
        return Integer.valueOf(time.split(":")[1]);
    }

    private Runnable r1=new Runnable () {
        public void run() {
            while (!activityStopped){
                try {
                    getBedInfo();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void getBedInfo() {
        // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_BED_INFO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                tvBed = (TextView) view.findViewById(R.id.tv_bed);
                tvName = (TextView) view.findViewById(R.id.tv_name);
                tvProgress = (TextView) view.findViewById(R.id.tv_progress);
                tvVP = (TextView) view.findViewById(R.id.tv_VP);
                tvTMP = (TextView) view.findViewById(R.id.tv_TMP);
                tvBFLW = (TextView) view.findViewById(R.id.tv_BFLW);
                tvNAC = (TextView) view.findViewById(R.id.tv_NAC);
                tvUFV = (TextView) view.findViewById(R.id.tv_UFV);
                tvUFG = (TextView) view.findViewById(R.id.tv_UFG);
                tvUFR = (TextView) view.findViewById(R.id.tv_UFR);
                tvPulse = (TextView) view.findViewById(R.id.tv_pulse);
                tvSYS = (TextView) view.findViewById(R.id.tv_SYS);
                tvDIA = (TextView) view.findViewById(R.id.tv_DIA);
                gauge2 = view.findViewById(R.id.gauge2);

                try {
                    JSONObject jObj = new JSONObject(response);
                    // User successfully stored in MySQL
                    // Now store the user in sqlite
                    String bedno = Integer.toString(jObj.getInt("bedno"));

                    JSONObject member = jObj.getJSONObject("member");
                    String name = member.getString("name");
                    String mrd = member.getString("mrd");

                    JSONObject process = jObj.getJSONObject("process");
                    String percent = Integer.toString(process.getInt("percent"));
                    JSONArray machinerecord = jObj.getJSONArray("machinerecord");

                    String VEN_PRESS = Integer.toString(machinerecord.getJSONObject(0).getInt("VEN_PRESS"));
                    String TMP = Integer.toString(machinerecord.getJSONObject(0).getInt("TMP"));
                    String B_FLW = Integer.toString(machinerecord.getJSONObject(0).getInt("B_FLW"));
                    String Na_CONDUCE = Integer.toString(machinerecord.getJSONObject(0).getInt("Na_CONDUCE"));
                    String UF_VOLUME = Double.toString(machinerecord.getJSONObject(0).getDouble("UF_VOLUME"));
                    String UF_GOAL = Double.toString(machinerecord.getJSONObject(0).getDouble("UF_GOAL"));
                    String UF_Rate = Double.toString(machinerecord.getJSONObject(0).getDouble("UF_Rate"));
                    String SYS = Integer.toString(machinerecord.getJSONObject(0).getInt("SYS"));
                    String DIA = Integer.toString(machinerecord.getJSONObject(0).getInt("DIA"));
                    String Pulse = Integer.toString(machinerecord.getJSONObject(0).getInt("Pulse"));

                    tvBed.setText("第"+bedno+"床");
                    tvName.setText(name);
                    tvProgress.setText(percent);
                    gauge2.setValue(process.getInt("percent"));
                    tvVP.setText(VEN_PRESS);
                    tvTMP.setText(TMP);
                    tvBFLW.setText(B_FLW);
                    tvNAC.setText(Na_CONDUCE);
                    tvUFV.setText(UF_VOLUME);
                    tvUFG.setText(UF_GOAL);
                    tvUFR.setText(UF_Rate);
                    tvPulse.setText(Pulse);
                    tvSYS.setText(SYS);
                    tvDIA.setText(DIA);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };
        // Adding request to request queue
        Volley.newRequestQueue(view.getContext()).add(strReq);
    }
}
