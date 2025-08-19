package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class PersonalityOption {
    @SerializedName("id")
    private int id;
    
    @SerializedName("text")
    private String text;
    
    @SerializedName("value")
    private String value;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
