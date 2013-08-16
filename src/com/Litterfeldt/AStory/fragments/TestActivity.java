package com.Litterfeldt.AStory.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Litterfeldt.AStory.R;
import com.Litterfeldt.AStory.scrollBar.ColorPicker;

public class TestActivity extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onPause(){

        super.onPause();



    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.testactivity, container, false);

        // ################### EXPERIMENTAL #########################################
        ColorPicker picker = (ColorPicker)view.findViewById(R.id.cseekbar);
        picker.setProgress(80);
        picker.invalidate();


        // ##########################################################################




        return view;
    }
}
