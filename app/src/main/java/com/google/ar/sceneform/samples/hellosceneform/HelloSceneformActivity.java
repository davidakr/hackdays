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
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable libertyRenderable;
    private ModelRenderable buildingRenderable;
    private ModelRenderable empireRenderable;
    private boolean modelIsSet = false;
    private boolean anchorIsSet = false;
    private boolean showStatue = true;
    private boolean showEmpire = false;
    private AnchorNode anchorNode;
    Button Button;

    private TransformableNode building;
    private TransformableNode statue;


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
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
                .setSource(this, R.raw.libertstatue)
                .build()
                .thenAccept(renderable -> libertyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load liberty renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, R.raw.building)
                .build()
                .thenAccept(renderable -> buildingRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load building renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, R.raw.empire)
                .build()
                .thenAccept(renderable -> empireRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load empire renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (libertyRenderable == null || buildingRenderable == null) {
                        return;
                    }
                    if (!anchorIsSet) {
                        // Create the Anchor.
                        Anchor anchor = hitResult.createAnchor();
                        anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());
                        anchorIsSet = true;
                        arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);

                    }
                    if (anchorIsSet) {
                        // Create the transformable andy and add it to the anchor.
                        setStatue();
                        setEmpire();
                        anchorIsSet = true;
                    }
                });
        addListenerOnEmpireButton();
        addListenerOnStatueButton();
    }

    public void addListenerOnEmpireButton() {
        Button = (Button) findViewById(R.id.links);
        Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                statue.setEnabled(true);
                building.setEnabled(false);
            }
        });
    }

    public void addListenerOnStatueButton() {
        Button = (Button) findViewById(R.id.mitte);
        Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                statue.setEnabled(false);
                building.setEnabled(true);
            }
        });
        ImageView toForum = findViewById(R.id.to_forum);
            toForum.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HelloSceneformActivity.this, ForumActivity.class));
        }
    });
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

    public void setStatue() {
        statue = new TransformableNode(arFragment.getTransformationSystem());
        statue.setParent(anchorNode);
        statue.setRenderable(libertyRenderable);
        statue.getRotationController().setEnabled(false);
        statue.getScaleController().setEnabled(false);
        statue.getTranslationController().setEnabled(false);
        statue.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
        statue.select();
    }

    private void setEmpire() {
        Quaternion q1 = anchorNode.getLocalRotation();
        Vector3 rotationVector = new Vector3(-90, 0, 0);
        Quaternion q2 = Quaternion.eulerAngles(rotationVector);
        Vector3 currentLocation = anchorNode.getWorldPosition();
        Vector3 transformationVector = new Vector3(currentLocation.x - 0.0f, currentLocation.y + 0.12f, currentLocation.z - 0.0f);

        building = new TransformableNode(arFragment.getTransformationSystem());
        building.setParent(anchorNode);
        building.setRenderable(empireRenderable);
        building.getRotationController().setEnabled(false);
        building.getScaleController().setEnabled(false);
        building.getTranslationController().setEnabled(false);
        building.setWorldPosition(transformationVector);
        building.setLocalScale(new Vector3(0.04f, 0.04f, 0.04f));
        building.setLocalRotation(Quaternion.multiply(q1, q2));
        //building.setLocalScale(new Vector3(0.03f, 0.03f, 0.03f));
        building.setEnabled(false);
        building.select();

    }
}
