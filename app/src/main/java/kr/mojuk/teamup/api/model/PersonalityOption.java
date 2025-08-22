package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class PersonalityOption {
    @SerializedName("id")
    private int id;
    
    @SerializedName("text")
    private String text;
    
    @SerializedName("value")
    private String value;

    @SerializedName("type")
    private String type;

    public PersonalityOption(int id, String text, String type) {
        this.id = id;
        this.text = text;
        this.type = type;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
