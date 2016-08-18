package com.sickfuture.android.recyclersectionview.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MapAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private static final String TAG = MapAdapter.class.getSimpleName();

    Map<Integer, SectionExpandableRecyclerAdapter.SectionData<T>> dataSet = new HashMap<>();
    List<Integer> viewTypes = new ArrayList<>();
    Map<Integer, Integer> sectionPositionToCodeMap = new HashMap<>();
    Map<Integer, T> positionToItemMap = new HashMap<>();
    Map<Integer, VH> headers = new HashMap<>();
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
        if(headers.containsKey(viewType)) {
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
        Log.d(TAG, "onBindViewHolder() called with: " + "holder = [" + holder + "], position = [" + position + "]");

        if (sectionPositionToCodeMap.get(position) != null) {
            onBindSectionViewHolder(holder, dataSet.get(sectionPositionToCodeMap.get(position)));
        } else {
            onBindItemViewHolder(holder, positionToItemMap.get(position));
        }
    }

    protected abstract void onBindItemViewHolder(VH holder, T item);

    protected abstract void onBindSectionViewHolder(VH holder, SectionExpandableRecyclerAdapter.SectionData<T> sectionData);

    private void onSectionClick(int adapterPosition) {
        Integer code = sectionPositionToCodeMap.get(adapterPosition);
        SectionExpandableRecyclerAdapter.SectionData<T> sectionData = dataSet.get(code);
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
        Log.d(TAG, "onSectionClick() called with: adapterPosition = [" + adapterPosition + "] section " + sectionData.code);
    }

    protected abstract VH itemViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract VH sectionViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    @Override
    public int getItemCount() {
        int count = headers.size() + dataSet.keySet().size() + countItems();
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    private int countItems() {
        int count = 0;
        for (Integer code : dataSet.keySet()) {
            SectionExpandableRecyclerAdapter.SectionData<T> sectionData = dataSet.get(code);
            if (!isSectionsCollapsible || sectionData.isExpanded) {
                count += sectionData.data.size();
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        Integer type = viewTypes.get(position);
        Log.d(TAG, "getItemViewType() : position = [" + position + "] type = " + type);
        return type;
    }

    @Override
    public long getItemId(int position) {
        if (sectionPositionToCodeMap.get(position) != null) {
            return sectionPositionToCodeMap.get(position).hashCode();
        } else {
            return super.getItemId(position);
        }
    }

    public void setItems(List<? extends T> items) {
        for (T item : items) {
            int sectionCode = sectionCode(item);
            SectionExpandableRecyclerAdapter.SectionData<T> sectionData = dataSet.get(sectionCode);
            if (sectionData == null) {
                sectionData = new SectionExpandableRecyclerAdapter.SectionData<>();
                sectionData.code = sectionCode;
                dataSet.put(sectionCode, sectionData);
            }
            sectionData.data.add(item);
        }
        updateInternalStructures();
    }

    private void updateInternalStructures() {
        int position = 0;
        for (Integer headerViewType : headers.keySet()) {
            viewTypes.add(position, headerViewType);
            position++;
        }
        for (Integer code : dataSet.keySet()) {
            viewTypes.add(position, sectionViewType());
            sectionPositionToCodeMap.put(position, code);
            SectionExpandableRecyclerAdapter.SectionData<T> sectionData = dataSet.get(code);
            position++;
            if (!isSectionsCollapsible || sectionData.isExpanded) {
                for (T t : sectionData.data) {
                    viewTypes.add(position, itemViewType(t));
                    positionToItemMap.put(position, t);
                    position++;
                }
                Log.d(TAG, code + ": " + position);
            }
        }
    }

    protected abstract int itemViewType(T item);

    protected abstract int sectionViewType();

    public abstract int sectionCode(T item);

}
