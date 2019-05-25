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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.ar.sceneform.rendering.ViewRenderable;
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

    private List<SightModel> models;
    private List<ViewRenderable> speechBubbles = new ArrayList<>();

    {
        models = new ArrayList<>();
        models.add(new SightModel("Empire State Building", "TODO", R.raw.empire));
        models.add(new SightModel("One World Trade Center", "TODO", R.raw.building));
        models.add(new SightModel("Statue of Liberty", "TODO", R.raw.libertstatue));
    }

    List<TransformableNode> loadedNodes = new ArrayList<>();

    private List<View> indicators;

    int currentModel = 0;

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
                        anchorIsSet = true;
                    }
                    if (anchorIsSet) {
                        // create all nodes
                        for (SightModel model : models) {
                            createNode(model);
                        }

                        List<Vector3> speechBubblePositions = distributeEvenly(speechBubbles.size(), anchorNode.getWorldPosition());
                        for (int i = 0; i < speechBubbles.size(); i++) {
                            addSpeechBubble(speechBubbles.get(i), speechBubblePositions.get(i));
                        }

                        loadedNodes.get(currentModel).setEnabled(true);
                        indicators.get(currentModel).setVisibility(View.VISIBLE);

                        anchorIsSet = true;
                    }
                });

        findViewById(R.id.to_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(HelloSceneformActivity.this, ForumOverviewActivity.class));
                startActivity(intent);
            }
        });

        indicators = new ArrayList<>();
        indicators.add(findViewById(R.id.circle1));
        indicators.add(findViewById(R.id.circle2));
        indicators.add(findViewById(R.id.circle3));

        for (String s : new String[]{"How to get in past 6pm?", "How much is it?", "Is it family-friendly?",
                "I like gandolf cause he", "Is a chill wizard", "P=NP", "Benajs djgjdds", "sdgagsjkagag", "dsgjkhaskghaskjgashgkjga"}) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.speech_bubble, null);
            ((TextView) view.findViewById(R.id.bubble_text)).setText(s);
            ViewRenderable.builder()
                    .setView(this, view)
                    .build()
                    .thenAccept(renderable -> speechBubbles.add(renderable));
        }

        for (int id : new int[] {R.id.img1, R.id.img2, R.id.img3}) {
            View v = findViewById(id);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int target = Integer.parseInt((String) v.getTag()) - 1;
                    setModel(target);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (anchorIsSet) {
            // don't show pan animation again when model has already been placed
            arFragment.getPlaneDiscoveryController().hide();
            arFragment.getPlaneDiscoveryController().setInstructionView(null);
        }
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
        if (!anchorIsSet) {
            return;
        }
        int oldModel = currentModel;
        currentModel = (currentModel + 1) % models.size();

        loadedNodes.get(oldModel).setEnabled(false);
        indicators.get(oldModel).setVisibility(View.GONE);
        loadedNodes.get(currentModel).setEnabled(true);
        indicators.get(currentModel).setVisibility(View.VISIBLE);
    }

    public void prevModel() {
        if (!anchorIsSet) {
            return;
        }
        int oldModel = currentModel;
        currentModel = (currentModel - 1 + models.size()) % models.size();

        loadedNodes.get(oldModel).setEnabled(false);
        indicators.get(oldModel).setVisibility(View.GONE);
        loadedNodes.get(currentModel).setEnabled(true);
        indicators.get(currentModel).setVisibility(View.VISIBLE);
    }

    public void setModel(int index) {
        if (!anchorIsSet) {
            return;
        }
        int oldModel = currentModel;
        currentModel = index;

        loadedNodes.get(oldModel).setEnabled(false);
        indicators.get(oldModel).setVisibility(View.GONE);
        loadedNodes.get(currentModel).setEnabled(true);
        indicators.get(currentModel).setVisibility(View.VISIBLE);
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
            node = new MyTransformableNode(arFragment.getTransformationSystem(), this);
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

            node = new MyTransformableNode(arFragment.getTransformationSystem(), this);
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
            Quaternion q1 = anchorNode.getLocalRotation();
            Vector3 rotationVector = new Vector3(-90, 0, 0);
            Quaternion q2 = Quaternion.eulerAngles(rotationVector);
            Vector3 currentLocation = anchorNode.getWorldPosition();
            Vector3 transformationVector = new Vector3(currentLocation.x - 0.0f, currentLocation.y + 0.12f, currentLocation.z - 0.0f);

            node = new MyTransformableNode(arFragment.getTransformationSystem(), this);
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
        }

        node.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                Intent intent = new Intent(new Intent(HelloSceneformActivity.this, ForumOverviewActivity.class));
                intent.putExtra("MODEL_ID", model.getRes());
                intent.putExtra("MODEL_TITLE", model.getTitle());
                startActivity(intent);
            }
        });

        node.setEnabled(false);

        loadedNodes.add(node);
    }

    private void addSpeechBubble(ViewRenderable vr, Vector3 location) {
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(vr);
        node.getRotationController().setEnabled(false);
        node.getScaleController().setEnabled(false);
        node.getTranslationController().setEnabled(false);
        node.setWorldPosition(location);
        final float scale = 0.15f;
        node.setLocalScale(new Vector3(scale, scale, scale));
        node.select();
        node.setEnabled(true);
    }

    private List<Vector3> distributeEvenly(int n, Vector3 base) {
        // assumption: x,z plane is the ground
        final int cardsPerSlice = 5;
        final float sliceDist = 0.07f;
        final float distFromCenter = 0.1f;
        List<Vector3> points = new ArrayList<>();
        // Distribute into slices along y-axis. In each slice, they are distributed along a circle.
        for (int i = 0; i < n; i += cardsPerSlice) {
            int nInCircle = Math.min(n - i, cardsPerSlice);
            float y = base.y + sliceDist * i / cardsPerSlice;
            // slices are perpendicular to z-axis
            float x0 = base.x;
            float z0 = base.z;
            for (int j = 0; j < nInCircle; j++) {
                // radians
                float angle = (float) ((float) j / nInCircle * 2 * Math.PI);
                float radius = distFromCenter;
                float x1 = (float) (x0 + radius * Math.cos(angle));
                float z1 = (float) (z0 + radius * Math.sin(angle));
                points.add(new Vector3(x1, y, z1));
            }
        }

        return points;
    }

}
