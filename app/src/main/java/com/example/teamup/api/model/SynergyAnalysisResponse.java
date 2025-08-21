package com.example.teamup.api.model;

import java.util.List;

public class SynergyAnalysisResponse {
    private List<User> users;
    private List<IndividualScore> individual_scores;
    private TeamAnalysis team_analysis;
    private String message;

    public static class User {
        private String user_id;
        private String name;
        private String email;

        public String getUserId() {
            return user_id;
        }

        public void setUserId(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class IndividualScore {
        private String user_id;
        private String name;
        private double synergy_score;
        private String compatibility;

        public String getUserId() {
            return user_id;
        }

        public void setUserId(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getSynergyScore() {
            return synergy_score;
        }

        public void setSynergyScore(double synergy_score) {
            this.synergy_score = synergy_score;
        }

        public String getCompatibility() {
            return compatibility;
        }

        public void setCompatibility(String compatibility) {
            this.compatibility = compatibility;
        }
    }

    public static class TeamAnalysis {
        private Double team_synergy_score;
        private String analysis_summary;
        private List<String> recommendations;

        public Double getTeamSynergyScore() {
            return team_synergy_score;
        }

        public void setTeamSynergyScore(Double team_synergy_score) {
            this.team_synergy_score = team_synergy_score;
        }

        public String getAnalysisSummary() {
            return analysis_summary;
        }

        public void setAnalysisSummary(String analysis_summary) {
            this.analysis_summary = analysis_summary;
        }

        public List<String> getRecommendations() {
            return recommendations;
        }

        public void setRecommendations(List<String> recommendations) {
            this.recommendations = recommendations;
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<IndividualScore> getIndividualScores() {
        return individual_scores;
    }

    public void setIndividualScores(List<IndividualScore> individual_scores) {
        this.individual_scores = individual_scores;
    }

    public TeamAnalysis getTeamAnalysis() {
        return team_analysis;
    }

    public void setTeamAnalysis(TeamAnalysis team_analysis) {
        this.team_analysis = team_analysis;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
