package com.google.ar.sceneform.samples.hellosceneform;

import com.google.ar.sceneform.rendering.ModelRenderable;

public class SightModel {
    private final String[] hardcodedQs;
    private String title;
    private String info;
    private ModelRenderable renderable;
    private int res;

    public SightModel(String title, String info, int res, String[] hardcodedQs) {
        this.title = title;
        this.info = info;
        this.res = res;
        this.hardcodedQs = hardcodedQs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ModelRenderable getRenderable() {
        return renderable;
    }

    public void setRenderable(ModelRenderable renderable) {
        this.renderable = renderable;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String[] getHardcodedQs() {
        return hardcodedQs;
    }
}
