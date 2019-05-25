/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.hellosceneform;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private MyArFragment arFragment;
    private boolean anchorIsSet = false;
    private AnchorNode anchorNode;
    private View toForumSpecial;

    private List<SightModel> models;
    {
        models = new ArrayList<>();
        models.add(new SightModel("Empire State Building", "TODO", R.raw.empire));
        models.add(new SightModel("One World Trade Center", "TODO", R.raw.building));
        models.add(new SightModel("Statue of Liberty", "TODO", R.raw.libertstatue));
    }

    private List<TransformableNode> loadedNodes = new ArrayList<>();

    private int currentModel = 0;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ux);
        arFragment = (MyArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        for (SightModel model : models) {
            loadModel(model);
        }

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (!anchorIsSet) {
                        // Create the Anchor.
                        Anchor anchor = hitResult.createAnchor();
                        anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());
                        arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);
                        toForumSpecial.setVisibility(View.VISIBLE);
                        anchorIsSet = true;
                    }
                    if (anchorIsSet) {
                        // create all nodes
                        for (SightModel model : models) {
                            createNode(model);
                        }

                        loadedNodes.get(0).setEnabled(true);

                        anchorIsSet = true;
                    }
                });

        toForumSpecial = findViewById(R.id.view);
        toForumSpecial.setVisibility(View.GONE);
        toForumSpecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(anchorIsSet) {
                    startActivity(new Intent(HelloSceneformActivity.this, ForumActivity.class));
                }
            }
        });
    }

    private void loadModel(SightModel model) {
        ModelRenderable.builder()
                .setSource(this, model.getRes())
                .build()
                .thenAccept(model::setRenderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    public void nextModel() {
        if (currentModel >= loadedNodes.size() - 1) {
            return;
        }
        loadedNodes.get(currentModel).setEnabled(false);
        loadedNodes.get(currentModel + 1).setEnabled(true);

        currentModel++;
    }

    public void prevModel() {
        if (currentModel == 0) {
            return;
        }
        loadedNodes.get(currentModel).setEnabled(false);
        loadedNodes.get(currentModel - 1).setEnabled(true);

        currentModel--;
    }


    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    public void createNode(SightModel model) {
        if (model.getRenderable() == null) {
            Log.e(TAG, "Renderable of " + model.getTitle() + " is null");
            return;
        }

        TransformableNode node;

        // custom offsets for each model
        if (model.getRes() == R.raw.libertstatue) {
            node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(model.getRenderable());
            node.getRotationController().setEnabled(false);
            node.getScaleController().setEnabled(false);
            node.getTranslationController().setEnabled(false);
            node.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
            node.select();
        } else if (model.getRes() == R.raw.empire) {
            Quaternion q1 = anchorNode.getLocalRotation();
            Vector3 rotationVector = new Vector3(-90, 0, 0);
            Quaternion q2 = Quaternion.eulerAngles(rotationVector);
            Vector3 currentLocation = anchorNode.getWorldPosition();
            Vector3 transformationVector = new Vector3(currentLocation.x - 0.0f, currentLocation.y + 0.12f, currentLocation.z - 0.0f);

            node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(model.getRenderable());
            node.getRotationController().setEnabled(false);
            node.getScaleController().setEnabled(false);
            node.getTranslationController().setEnabled(false);
            node.setWorldPosition(transformationVector);
            node.setLocalScale(new Vector3(0.04f, 0.04f, 0.04f));
            node.setLocalRotation(Quaternion.multiply(q1, q2));
            //building.setLocalScale(new Vector3(0.03f, 0.03f, 0.03f));
            node.select();
        } else {
            // TODO one world trade center scale
            node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(model.getRenderable());
            node.getRotationController().setEnabled(false);
            node.getScaleController().setEnabled(false);
            node.getTranslationController().setEnabled(false);
            node.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
            node.select();
        }

        node.setEnabled(false);

        loadedNodes.add(node);
    }

}
