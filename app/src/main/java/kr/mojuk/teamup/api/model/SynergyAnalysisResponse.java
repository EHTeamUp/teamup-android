package kr.mojuk.teamup.api.model;

import java.util.List;

public class SynergyAnalysisResponse {
    private List<User> users;
    private SynergyResult synergy_result;
    private String filter_name;

    public static class User {
        private String user_id;
        private List<Skill> skills;
        private List<Role> roles;
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

        public Traits getTraits() {
            return traits;
        }

        public void setTraits(Traits traits) {
            this.traits = traits;
        }
    }

    public static class Skill {
        private String skill_name;

        public String getSkillName() {
            return skill_name;
        }

        public void setSkillName(String skill_name) {
            this.skill_name = skill_name;
        }
    }

    public static class Role {
        private String role_name;

        public String getRoleName() {
            return role_name;
        }

        public void setRoleName(String role_name) {
            this.role_name = role_name;
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

    public static class SynergyResult {
        private double synergy_score;
        private Explanation explanation;

        public double getSynergyScore() {
            return synergy_score;
        }

        public void setSynergyScore(double synergy_score) {
            this.synergy_score = synergy_score;
        }

        public Explanation getExplanation() {
            return explanation;
        }

        public void setExplanation(Explanation explanation) {
            this.explanation = explanation;
        }
    }

    public static class Explanation {
        private double baseline;
        private List<Point> good_points;
        private List<Point> bad_points;

        public double getBaseline() {
            return baseline;
        }

        public void setBaseline(double baseline) {
            this.baseline = baseline;
        }

        public List<Point> getGoodPoints() {
            return good_points;
        }

        public void setGoodPoints(List<Point> good_points) {
            this.good_points = good_points;
        }

        public List<Point> getBadPoints() {
            return bad_points;
        }

        public void setBadPoints(List<Point> bad_points) {
            this.bad_points = bad_points;
        }
    }

    public static class Point {
        private String feature;
        private double value;
        private double contribution;
        private String message;

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getContribution() {
            return contribution;
        }

        public void setContribution(double contribution) {
            this.contribution = contribution;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public SynergyResult getSynergyResult() {
        return synergy_result;
    }

    public void setSynergyResult(SynergyResult synergy_result) {
        this.synergy_result = synergy_result;
    }

    public String getFilterName() {
        return filter_name;
    }

    public void setFilterName(String filter_name) {
        this.filter_name = filter_name;
    }
}

