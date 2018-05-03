package com.example.lambert.hemodialysis.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.lambert.hemodialysis.R;

import java.util.Calendar;


public class Setting extends Fragment {

    TimePickerDialog picker;
    Button btnGet;
    EditText eText;
    TextView tvw;

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
        eText=(EditText) view.findViewById(R.id.editText1);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                eText.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
//        final EditText editText = view.findViewById(R.id.editText);
//        final String strTime = editText.getText().toString();
        tvw=(TextView)view.findViewById(R.id.textView1);
        btnGet=(Button)view.findViewById(R.id.button1);
        btnGet.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {

                tvw.setText("將在: "+ eText.getText() + " 提醒家人出門");

                //傳值給Patient fragment
                State patient = new State();

                Bundle bundle = new Bundle( );
                bundle.putString( "time",eText.getText().toString());
                patient.setArguments( bundle );

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.screen_area,patient).commit();

                //Toast.makeText(getActivity(),editText.getText(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
