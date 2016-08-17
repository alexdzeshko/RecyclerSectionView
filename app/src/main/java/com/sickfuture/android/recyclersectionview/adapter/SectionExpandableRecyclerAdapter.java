package com.sickfuture.android.recyclersectionview.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sickfuture.android.recyclersectionview.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class SectionExpandableRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final List<T> originalItems;
    protected BaseRecyclerAdapter<T, VH> linkedAdapter;
    protected Map<Integer, Integer> sectionPositions = new LinkedHashMap<>();
    protected Map<Integer, SectionData<T>> sectionChildrenPositions = new LinkedHashMap<>();
    protected Map<Integer, Integer> itemPositions = new LinkedHashMap<>();
    private int sectionViewType = hashCode();

    static class SectionData<T> {
        int code;
        boolean isExpanded;
        List<T> data = new ArrayList<>();
        List<Integer> positions = new ArrayList<>();
    }

    public abstract int sectionCode(T item);

    protected abstract void onBindItemViewHolder(VH viewHolder, int sectionCode, int position);

//    protected abstract VH viewHolder(LayoutInflater inflater, ViewGroup parent, int type);

    public SectionExpandableRecyclerAdapter(BaseRecyclerAdapter<T, VH> linkedAdapter) {
        this.linkedAdapter = linkedAdapter;
        originalItems = linkedAdapter.getItems();
        updateObjectsCache();
        linkedAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateSessionCache();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                updateSessionCache();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateSessionCache();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateSessionCache();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                updateSessionCache();
                notifyDataSetChanged();
            }
        });
    }

    protected synchronized void updateSessionCache() {
        int currentPosition = 0; //this is actual position in adapter
        sectionPositions.clear();
        itemPositions.clear();
        int currentSection = Integer.MIN_VALUE;
        final int count = linkedAdapter.getItemCount();
        for (int i = 0; i < count; i++) {

            final T item = linkedAdapter.getItem(i);

            int sectionCode = sectionCode(item);
            if (currentSection != sectionCode) {
                sectionPositions.put(currentPosition, sectionCode);
                currentSection = sectionCode;
                currentPosition++;
            }
            itemPositions.put(currentPosition, i);

            currentPosition++;
        }
    }

    protected synchronized void updateObjectsCache() {
        int currentPosition = 0; //this is actual position in adapter
        sectionChildrenPositions.clear();
        int currentSection = Integer.MIN_VALUE;
        final int count = originalItems.size();
        for (int i = 0; i < count; i++) {

            final T item = originalItems.get(i);

            int sectionCode = sectionCode(item);
            if (currentSection != sectionCode) {
                currentPosition++;
            }

            SectionData<T> sectionData = sectionChildrenPositions.get(sectionCode);
            if (sectionData == null) {
                sectionData = new SectionData<>();
                sectionData.code = sectionCode;
                sectionChildrenPositions.put(sectionCode, sectionData);
            }
            sectionData.positions.add(currentPosition);
            sectionData.data.add(item);

            currentPosition++;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) (recyclerView.getLayoutManager());
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isSection(position)) ? layoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    public synchronized boolean isSection(final int position) {
        return sectionPositions.containsKey(position);
    }

    public synchronized int getSectionPosition(final int position) {
        return sectionPositions.get(position);
    }

    public Integer getLinkedPosition(final int position) {
        return itemPositions.get(position);
    }

    @Override
    public long getItemId(final int position) {
        if (isSection(position)) {
            return sectionPositions.get(position).hashCode();
        } else {
            return linkedAdapter.getItemId(getLinkedPosition(position));
        }
    }

    @Override
    public int getItemViewType(final int position) {
        if (isSection(position)) {
            return sectionViewType();
        }
        return linkedAdapter.getItemViewType(getLinkedPosition(position));
    }

    protected int sectionViewType() {
        return sectionViewType;
    }

    @Override
    public int getItemCount() {
        return sectionPositions.size() + itemPositions.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == sectionViewType()) {
            return (VH) new BaseRecyclerAdapter.BaseViewHolder(R.layout.section, LayoutInflater.from(parent.getContext()), parent) {
                @Override
                protected void addClicks(ViewMap views) {
                    views.click(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onSectionClick(getAdapterPosition());
                        }
                    });
                }
            };
        } else {
            return linkedAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    protected void onSectionClick(int adapterPosition) {
        Integer sectionCode = sectionPositions.get(adapterPosition);
        if (sectionCode != null) {
            toggleSection(sectionCode);
        }
    }

    private void toggleSection(Integer sectionCode) {
        SectionData<T> sectionData = sectionChildrenPositions.get(sectionCode);
        if (sectionData.isExpanded) {
            //collapse
            sectionData.isExpanded = false;
            linkedAdapter.removeChildrenRange(sectionData.positions.get(0), sectionData.positions.size());
        } else {
            //expand
            sectionData.isExpanded = true;
            linkedAdapter.insertItems(sectionData.positions.get(0), sectionData.data);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (isSection(position)) {
            onBindItemViewHolder(holder, sectionPositions.get(position), position);
        } else {
            linkedAdapter.onBindViewHolder(holder, getLinkedPosition(position));
        }
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        linkedAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        linkedAdapter.unregisterAdapterDataObserver(observer);
    }

    public void setItems(List<? extends T> items) {
        originalItems.clear();
        originalItems.addAll(items);
        updateObjectsCache();
        linkedAdapter.setItems(items);
    }

    public T getItem(int position) {
        if (!isSection(position)) {
            return linkedAdapter.getItem(getLinkedPosition(position));
        } else {
            return null;
        }
    }

    public void remove(int position) {
        if (!isSection(position)) {
            linkedAdapter.removeChild(getLinkedPosition(position));
        }
    }

    public void removeRange(int start, int count) {
        linkedAdapter.removeChildrenRange(start, count);
    }

    public void appendItems(List<T> items) {
        linkedAdapter.addAll(items);
    }
}
