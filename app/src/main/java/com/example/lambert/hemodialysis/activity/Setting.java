package com.example.lambert.hemodialysis.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.lambert.hemodialysis.R;

import java.util.Calendar;


public class Setting extends Fragment {

    TimePickerDialog picker;
    Button btnGet;

    private View view;
    public Setting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnGet=(Button)view.findViewById(R.id.button1);
        btnGet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View theView = inflater.inflate(R.layout.datedialog, null);
                final NumberPicker np_hour = (NumberPicker) theView.findViewById(R.id.np_hour);
                final NumberPicker np_minute = (NumberPicker) theView.findViewById(R.id.np_minute);
                builder.setView(theView)
                        .setPositiveButton("設定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("DBG","mTime is: "+np_hour.getValue() + ":"+np_minute.getValue());
                                String mTime = np_hour.getValue() + ":" + np_minute.getValue();
                                //傳值給Patient fragment
                                State patient = new State();

                                Bundle bundle = new Bundle( );
                                bundle.putString( "time",mTime.toString());
                                patient.setArguments( bundle );

                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.replace(R.id.screen_area,patient).commit();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                np_hour.setMinValue(0);
                np_hour.setMaxValue(5);
                np_minute.setMinValue(0);
                np_minute.setMaxValue(59);
                builder.show();



//                picker = new AlertDialog.Builder(getActivity(),
//                        new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
//                                eText.setText(sHour + ":" + sMinute);
//                            }
//                        }, hour, minutes, true);
//                picker.show();
            }
        });
//        final EditText editText = view.findViewById(R.id.editText);
//        final String strTime = editText.getText().toString();


    }


}
