package com.google.ar.sceneform.samples.hellosceneform;


import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Jerry on 4/18/2018.
 */

public class SwipeListener extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG = "SwipeListener";

    // Minimal x and y axis swipe distance.
    private static int MIN_SWIPE_DISTANCE_X = 10;
    private static int MIN_SWIPE_DISTANCE_Y = 10;

    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;
    private final HelloSceneformActivity activity;

    public SwipeListener(HelloSceneformActivity activity) {
        this.activity = activity;
    }


    /* This method is invoked when a swipe gesture happened. */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if ((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X)) {
            if (deltaX > 0) {
                Log.d(TAG, "Swipe to left");
                activity.prevModel();
            } else {
                Log.d(TAG, "Swipe to right");
                activity.nextModel();
            }
        }

        if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
            if (deltaY > 0) {
                Log.d(TAG, "Swipe to up");
            } else {
                Log.d(TAG, "Swipe to down");
            }
        }

        return true;
    }

}
