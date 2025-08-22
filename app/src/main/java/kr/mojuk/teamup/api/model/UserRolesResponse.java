package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 역할 조회 응답 DTO
 */
public class UserRolesResponse {
    @SerializedName("role_ids")
    private List<Integer> roleIds;
    
    @SerializedName("custom_roles")
    private List<String> customRoles;
    
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
