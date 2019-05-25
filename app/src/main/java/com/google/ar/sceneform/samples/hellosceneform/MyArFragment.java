package com.google.ar.sceneform.samples.hellosceneform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.ux.ArFragment;



public class MyArFragment extends ArFragment {

    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SwipeListener gestureListener = new SwipeListener();
        gestureDetectorCompat = new GestureDetectorCompat(getActivity(), gestureListener);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
        // they did it like this: https://github.com/swarmnyc/arcore-augmented-image-swarm
        gestureDetectorCompat.onTouchEvent(motionEvent);

        super.onPeekTouch(hitTestResult, motionEvent);
    }
}
