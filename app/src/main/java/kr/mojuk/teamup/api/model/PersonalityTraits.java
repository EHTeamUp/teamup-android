package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PersonalityTraits implements Serializable {
    @SerializedName("role")
    private String role;
    
    @SerializedName("time")
    private String time;
    
    @SerializedName("goal")
    private String goal;
    
    @SerializedName("problem")
    private String problem;
    
    public PersonalityTraits() {}
    
    public PersonalityTraits(String role, String time) {
        this.role = role;
        this.time = time;
    }
    
    public PersonalityTraits(String role, String time, String goal, String problem) {
        this.role = role;
        this.time = time;
        this.goal = goal;
        this.problem = problem;
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
    
    public String getGoal() {
        return goal;
    }
    
    public void setGoal(String goal) {
        this.goal = goal;
    }
    
    public String getProblem() {
        return problem;
    }
    
    public void setProblem(String problem) {
        this.problem = problem;
    }
    
    @Override
    public String toString() {
        return "PersonalityTraits{" +
                "role='" + role + '\'' +
                ", time='" + time + '\'' +
                ", goal='" + goal + '\'' +
                ", problem='" + problem + '\'' +
                '}';
    }
}
