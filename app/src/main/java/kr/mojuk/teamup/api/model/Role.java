package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 역할 정보 DTO
 */
public class Role {
    @SerializedName("role_id")
    private int roleId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("is_custom")
    private boolean isCustom;
    
    public int getRoleId() {
        return roleId;
    }
    
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isCustom() {
        return isCustom;
    }
    
    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
