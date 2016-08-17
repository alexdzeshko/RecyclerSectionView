package com.sickfuture.android.recyclersectionview.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    public int section;
    public int subSection;
    public String text;

    public Model(int section, int subSection, String text) {
        this.section = section;
        this.subSection = subSection;
        this.text = text;
    }

    public static List<Model> generate() {
        ArrayList<Model> models = new ArrayList<>(200);
        for (int i = 0; i < 100; i++) {
            models.add(new Model(i % 20, i % 10, "item " + i));
        }
        return models;
    }
}
