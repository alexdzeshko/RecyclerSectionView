package com.sickfuture.android.recyclersectionview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sickfuture.android.recyclersectionview.adapter.BaseViewHolder;
import com.sickfuture.android.recyclersectionview.adapter.MapRecyclerAdapter;
import com.sickfuture.android.recyclersectionview.adapter.SectionDataHolder;
import com.sickfuture.android.recyclersectionview.model.Model;

import java.util.List;

public class RecyclerFragment extends Fragment {
    private static final String TAG = RecyclerFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Model> generate = Model.generate();

        MapRecyclerAdapter<Model, BaseViewHolder> adapter = new MapRecyclerAdapter<Model, BaseViewHolder>() {

            @Override
            protected void onBindItemViewHolder(BaseViewHolder holder, Model item) {
                TextView textView = (TextView) holder.get(R.id.title);
                if (textView != null) {
                    textView.setText(item.text);
                } else {
                    Log.e(TAG, "onBindItemViewHolder: "+item);
                }
            }

            @Override
            protected void onBindSectionViewHolder(BaseViewHolder holder, SectionDataHolder<Model> sectionData) {
                TextView textView = (TextView) holder.get(R.id.section_title);
                if (textView != null) {
                    textView.setText("Section "+sectionData.code);
                } else {
                    Log.e(TAG, "onBindSectionViewHolder: " + sectionData);
                }
            }

            @Override
            protected BaseViewHolder itemViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return new BaseViewHolder(R.layout.item, inflater, parent);
            }

            @Override
            protected BaseViewHolder sectionViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return new BaseViewHolder(R.layout.section, inflater, parent);
            }

            @Override
            protected int itemViewType(Model item) {
                return 1;
            }

            @Override
            protected int sectionViewType() {
                return 0;
            }

            @Override
            public int sectionCode(Model item) {
                return item.section;
            }
        };
        adapter.setItems(generate);
//        adapter.setSectionsCollapsible(false);

        adapter.addHeaderView(new BaseViewHolder(R.layout.header, LayoutInflater.from(getContext()), null));
        adapter.addHeaderView(new BaseViewHolder(R.layout.header, LayoutInflater.from(getContext()), null));
        recyclerView.setAdapter(adapter);
    }
}
