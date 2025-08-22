package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class FilterItem {
    @SerializedName("filter_id")
    private int filterId;

    @SerializedName("name")
    private String name;

    public FilterItem() {}

    public FilterItem(int filterId, String name) {
        this.filterId = filterId;
        this.name = name;
    }

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FilterItem{" +
                "filterId=" + filterId +
                ", name='" + name + '\'' +
                '}';
    }
}
