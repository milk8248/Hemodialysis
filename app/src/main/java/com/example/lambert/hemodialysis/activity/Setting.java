package com.example.lambert.hemodialysis.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.lambert.hemodialysis.R;


public class Setting extends Fragment {

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
        final EditText editText = view.findViewById(R.id.editText);
        final String strTime = editText.getText().toString();
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {

                //傳值給Patient fragment
                State patient = new State();

                Bundle bundle = new Bundle( );
                bundle.putString( "time",editText.getText().toString() );
                patient.setArguments( bundle );

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.screen_area,patient).commit();

                //Toast.makeText(getActivity(),editText.getText(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
