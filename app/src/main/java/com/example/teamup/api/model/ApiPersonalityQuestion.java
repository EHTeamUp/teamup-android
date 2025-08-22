package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiPersonalityQuestion {
    @SerializedName("id")
    private int id;

    @SerializedName("order_no")
    private int orderNo;
    
    @SerializedName("key_name")
    private String keyName;
    
    @SerializedName("text")
    private String text;
    
    @SerializedName("options")
    private List<PersonalityOption> options;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getKeyName() {
        return keyName;
    }
    
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public List<PersonalityOption> getOptions() {
        return options;
    }
    
    public void setOptions(List<PersonalityOption> options) {
        this.options = options;
    }

    public ApiPersonalityQuestion(int orderNo, String keyName, String text, int id, List<PersonalityOption> options) {
        this.orderNo = orderNo;
        this.keyName = keyName;
        this.text = text;
        this.id = id;
        this.options = options;
    }

}
