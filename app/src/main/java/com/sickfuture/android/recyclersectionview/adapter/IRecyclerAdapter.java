package com.sickfuture.android.recyclersectionview.adapter;

import java.util.List;

public interface IRecyclerAdapter<T> {
    void add(int position, T item);

    void add(T item);

    void addAll(List<? extends T> items);

    void setItems(List<? extends T> items);

    void set(int position, T item);

    void removeChild(int position);

    void removeChildrenRange(int position, int count);

    void clear();
}
