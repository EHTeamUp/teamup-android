package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PersonalityTraits implements Serializable {
    @SerializedName("role")
    private String role;
    
    @SerializedName("time")
    private String time;
    
    public PersonalityTraits() {}
    
    public PersonalityTraits(String role, String time) {
        this.role = role;
        this.time = time;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "PersonalityTraits{" +
                "role='" + role + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
