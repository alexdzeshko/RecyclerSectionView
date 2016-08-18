package com.sickfuture.android.recyclersectionview.adapter;

import java.util.ArrayList;
import java.util.List;

public class SectionDataHolder<T> {
    public int code;
    boolean isExpanded;
    List<T> data = new ArrayList<>();
    int sectionPosition;
    List<Integer> itemsPositions = new ArrayList<>();

    @Override
    public String toString() {
        return code + " items ["+data.size()+"] isExp: "+isExpanded;
    }
}
