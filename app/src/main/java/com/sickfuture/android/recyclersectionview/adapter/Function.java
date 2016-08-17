package com.sickfuture.android.recyclersectionview.adapter;

public interface Function<Input, Result> {
    Result apply(Input inputValue);
}
