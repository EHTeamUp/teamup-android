package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 역할 업데이트 요청 DTO
 */
public class RoleUpdate {
    @SerializedName("role_ids")
    private List<Integer> roleIds;
    
    @SerializedName("custom_roles")
    private List<String> customRoles;
    
    public RoleUpdate(List<Integer> roleIds, List<String> customRoles) {
        this.roleIds = roleIds;
        this.customRoles = customRoles;
    }
    
    public List<Integer> getRoleIds() {
        return roleIds;
    }
    
    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
    
    public List<String> getCustomRoles() {
        return customRoles;
    }
    
    public void setCustomRoles(List<String> customRoles) {
        this.customRoles = customRoles;
    }
}
