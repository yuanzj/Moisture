package com.drt.moisture.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 模拟的搜索结果类
 */
public class MockSearchResult implements Parcelable {
    private String address;
    private String name;
    
    public MockSearchResult(String address, String name) {
        this.address = address;
        this.name = name;
    }
    
    protected MockSearchResult(Parcel in) {
        address = in.readString();
        name = in.readString();
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getName() {
        return name;
    }
    
    public static final Creator<MockSearchResult> CREATOR = new Creator<MockSearchResult>() {
        @Override
        public MockSearchResult createFromParcel(Parcel in) {
            return new MockSearchResult(in);
        }
        
        @Override
        public MockSearchResult[] newArray(int size) {
            return new MockSearchResult[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(name);
    }
}