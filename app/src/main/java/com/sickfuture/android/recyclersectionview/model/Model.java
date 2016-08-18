package com.sickfuture.android.recyclersectionview.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                models.add(new Model(i, 0, "item " + j));
            }
        }
        Collections.sort(models, new Comparator<Model>() {
            @Override
            public int compare(Model l, Model r) {
                return Integer.valueOf(l.section).compareTo(r.section) * 100 + l.text.compareTo(r.text);
            }
        });
        return models;
    }

    @Override
    public String toString() {
        return section+ " "+ text;
    }
}
