package com.inuker.bluetooth.library.search.response;

import com.inuker.bluetooth.library.search.SearchResult;

/**
 * 搜索响应 - 空实现版本
 */
public abstract class SearchResponse {
    public abstract void onSearchStarted();
    public abstract void onDeviceFounded(SearchResult device);
    public abstract void onSearchStopped();
    public abstract void onSearchCanceled();
}