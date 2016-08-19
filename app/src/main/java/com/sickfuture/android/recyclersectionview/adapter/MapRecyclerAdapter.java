package com.sickfuture.android.recyclersectionview.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class MapRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private static final String TAG = MapRecyclerAdapter.class.getSimpleName();

    SparseArray<SectionDataHolder<T>> dataSet = new SparseArray<>();
    List<Integer> viewTypes = new ArrayList<>();
    SparseIntArray positionToSectionCodeMap = new SparseIntArray();
    SparseArray<T> positionToItemMap = new SparseArray<>();
    SparseArray<VH> headers = new SparseArray<>();
    boolean isSectionsCollapsible = true;

    public boolean isSectionsCollapsible() {
        return isSectionsCollapsible;
    }

    public void setSectionsCollapsible(boolean sectionsCollapsible) {
        if (isSectionsCollapsible != sectionsCollapsible) {
            isSectionsCollapsible = sectionsCollapsible;
            updateInternalStructures();
            notifyDataSetChanged();
        }
    }

    public void addHeaderView(@NonNull VH viewHolder) {
        headers.put(viewHolder.hashCode(), viewHolder);
        updateInternalStructures();
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (headers.indexOfKey(viewType) >= 0) {
            return headers.get(viewType);
        } else if (viewType == sectionViewType()) {
            final VH vh = sectionViewHolder(LayoutInflater.from(parent.getContext()), parent, viewType);
            if (isSectionsCollapsible) {
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onSectionClick(vh.getAdapterPosition());
                    }
                });
            }
            return vh;
        } else {
            return itemViewHolder(LayoutInflater.from(parent.getContext()), parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (positionToSectionCodeMap.indexOfKey(position) >= 0) {
            onBindSectionViewHolder(holder, dataSet.get(positionToSectionCodeMap.get(position)));
        } else {
            onBindItemViewHolder(holder, positionToItemMap.get(position));
        }
    }

    private void onSectionClick(int adapterPosition) {
        Integer code = positionToSectionCodeMap.get(adapterPosition);
        SectionDataHolder<T> sectionData = dataSet.get(code);
        if (sectionData.isExpanded) {
            //collapse
            sectionData.isExpanded = false;
            updateInternalStructures();
            notifyItemRangeRemoved(adapterPosition + 1, sectionData.data.size());

        } else {
            //expand
            sectionData.isExpanded = true;
            updateInternalStructures();
            notifyItemRangeInserted(adapterPosition + 1, sectionData.data.size());
        }
    }

    @Override
    public int getItemCount() {
        int count = headers.size() + dataSet.size() + countItems();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    private int countItems() {
        int count = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            Integer code = dataSet.keyAt(i);
            SectionDataHolder<T> sectionData = dataSet.get(code);
            if (!isSectionsCollapsible || sectionData.isExpanded) {
                count += sectionData.data.size();
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return viewTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (positionToSectionCodeMap.indexOfKey(position) >= 0) {
            return Integer.valueOf(positionToSectionCodeMap.get(position)).hashCode();
        } else {
            return super.getItemId(position);
        }
    }

    public void setItems(List<? extends T> items) {
        dataSet.clear();
        viewTypes.clear();
        positionToSectionCodeMap.clear();
        positionToItemMap.clear();
        for (T item : items) {
            int sectionCode = sectionCode(item);
            SectionDataHolder<T> sectionData = dataSet.get(sectionCode);
            if (sectionData == null) {
                sectionData = new SectionDataHolder<>();
                sectionData.code = sectionCode;
                dataSet.put(sectionCode, sectionData);
            }
            sectionData.data.add(item);
        }
        updateInternalStructures();
    }

    private void updateInternalStructures() {
        viewTypes.clear();
        positionToSectionCodeMap.clear();
        positionToItemMap.clear();
        int position = 0;
        for (int i = 0; i < headers.size(); i++) {
            Integer headerViewType = headers.keyAt(i);
            viewTypes.add(position, headerViewType);
            position++;
        }
        for (int i = 0; i < dataSet.size(); i++) {
            Integer code = dataSet.keyAt(i);
            viewTypes.add(position, sectionViewType());
            positionToSectionCodeMap.put(position, code);
            SectionDataHolder<T> sectionData = dataSet.get(code);
            sectionData.sectionPosition = position;
            position++;
            if (!isSectionsCollapsible || sectionData.isExpanded) {
                for (T t : sectionData.data) {
                    viewTypes.add(position, itemViewType(t));
                    positionToItemMap.put(position, t);
                    sectionData.itemsPositions.add(position);
                    position++;
                }
            }
        }
    }

    protected abstract int itemViewType(T item);

    protected abstract int sectionViewType();

    protected abstract int sectionCode(T item);

    protected abstract VH itemViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract VH sectionViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract void onBindItemViewHolder(VH holder, T item);

    protected abstract void onBindSectionViewHolder(VH holder, SectionDataHolder<T> sectionData);

}
