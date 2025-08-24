package kr.mojuk.teamup.api.model;

import java.util.List;

public class SynergyAnalysisResponse {
    private List<User> users;

    public static class User {
        private String user_id;
        private List<Skill> skills;
        private List<Role> roles;
        private List<Experience> experiences;
        private Traits traits;

        public String getUserId() {
            return user_id;
        }

        public void setUserId(String user_id) {
            this.user_id = user_id;
        }

        public List<Skill> getSkills() {
            return skills;
        }

        public void setSkills(List<Skill> skills) {
            this.skills = skills;
        }

        public List<Role> getRoles() {
            return roles;
        }

        public void setRoles(List<Role> roles) {
            this.roles = roles;
        }

        public List<Experience> getExperiences() {
            return experiences;
        }

        public void setExperiences(List<Experience> experiences) {
            this.experiences = experiences;
        }

        public Traits getTraits() {
            return traits;
        }

        public void setTraits(Traits traits) {
            this.traits = traits;
        }
    }

    public static class Skill {
        private String skill_name;
        private boolean is_custom;

        public String getSkillName() {
            return skill_name;
        }

        public void setSkillName(String skill_name) {
            this.skill_name = skill_name;
        }

        public boolean isCustom() {
            return is_custom;
        }

        public void setCustom(boolean is_custom) {
            this.is_custom = is_custom;
        }
    }

    public static class Role {
        private String role_name;
        private boolean is_custom;

        public String getRoleName() {
            return role_name;
        }

        public void setRoleName(String role_name) {
            this.role_name = role_name;
        }

        public boolean isCustom() {
            return is_custom;
        }

        public void setCustom(boolean is_custom) {
            this.is_custom = is_custom;
        }
    }

    public static class Experience {
        private String contest_name;
        private int award_status;
        private String award_name;

        public String getContestName() {
            return contest_name;
        }

        public void setContestName(String contest_name) {
            this.contest_name = contest_name;
        }

        public int getAwardStatus() {
            return award_status;
        }

        public void setAwardStatus(int award_status) {
            this.award_status = award_status;
        }

        public String getAwardName() {
            return award_name;
        }

        public void setAwardName(String award_name) {
            this.award_name = award_name;
        }
    }

    public static class Traits {
        private String profile_code;
        private String display_name;

        public String getProfileCode() {
            return profile_code;
        }

        public void setProfileCode(String profile_code) {
            this.profile_code = profile_code;
        }

        public String getDisplayName() {
            return display_name;
        }

        public void setDisplayName(String display_name) {
            this.display_name = display_name;
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

