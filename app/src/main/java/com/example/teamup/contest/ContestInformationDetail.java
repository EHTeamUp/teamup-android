package com.example.teamup.contest;

public class ContestInformationDetail {
    private final String title;
    private final int posterResourceId;
    private final String deadlineText;
    private final String hashtags;
    private final String siteUrl; // 사이트 바로가기를 위한 URL

    public ContestInformationDetail(String title, int posterResourceId, String deadlineText, String hashtags, String siteUrl) {
        this.title = title;
        this.posterResourceId = posterResourceId;
        this.deadlineText = deadlineText;
        this.hashtags = hashtags;
        this.siteUrl = siteUrl;
    }

    // Getter 메서드들
    public String getTitle() { return title; }
    public int getPosterResourceId() { return posterResourceId; }
    public String getDeadlineText() { return deadlineText; }
    public String getHashtags() { return hashtags; }
    public String getSiteUrl() { return siteUrl; }
}
