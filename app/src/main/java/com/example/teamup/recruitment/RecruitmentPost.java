package com.example.teamup.recruitment;

import java.util.List;

public class RecruitmentPost {
    private final int id;
    private final String title;
    private final String dDay;
    private final int currentMembers;
    private final int totalMembers;
    private final String organizer;
    private final String prize;
    private final List<String> tags;

    public RecruitmentPost(int id, String title, String dDay, int currentMembers, int totalMembers, String organizer, String prize, List<String> tags) {
        this.id = id;
        this.title = title;
        this.dDay = dDay;
        this.currentMembers = currentMembers;
        this.totalMembers = totalMembers;
        this.organizer = organizer;
        this.prize = prize;
        this.tags = tags;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getdDay() { return dDay; }
    public int getCurrentMembers() { return currentMembers; }
    public int getTotalMembers() { return totalMembers; }
    public String getOrganizer() { return organizer; }
    public String getPrize() { return prize; }
    public List<String> getTags() { return tags; }
}

