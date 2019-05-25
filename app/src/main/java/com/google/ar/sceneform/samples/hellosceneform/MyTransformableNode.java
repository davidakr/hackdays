package com.google.ar.sceneform.samples.hellosceneform;

import android.util.Log;

import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class MyTransformableNode extends TransformableNode {
    private final HelloSceneformActivity activity;

    public MyTransformableNode(TransformationSystem transformationSystem, HelloSceneformActivity activity) {
        super(transformationSystem);

        this.activity = activity;
    }

    @Override
    public void onDeactivate() {
        if (activity.loadedNodes.size() > 0 && this == activity.loadedNodes.get(activity.currentModel)) {
            setEnabled(true);
            Log.d("MyTransformableNode", "Hack: onDeactivate prevented");
        } else {
            super.onDeactivate();
        }
    }

    @Override
    public void onActivate() {
        if (activity.loadedNodes.size() > 0 && this != activity.loadedNodes.get(activity.currentModel)) {
            setEnabled(false);
            Log.d("MyTransformableNode", "Hack: onActivate prevented");
        } else {
            super.onActivate();
        }
    }
}
