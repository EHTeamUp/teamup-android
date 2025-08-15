package com.example.teamup.contest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ContestInformation {
    private final int id;
    private final int thumbnailResourceId;
    private final String title;
    private final String hashtags;
    private final LocalDate dueDate; // ◀◀◀ D-Day를 String에서 LocalDate로 변경

    public ContestInformation(int id, int thumbnailResourceId, String title, LocalDate dueDate, String hashtags) {
        this.id = id;
        this.thumbnailResourceId = thumbnailResourceId;
        this.title = title;
        this.dueDate = dueDate; // ◀◀◀ 생성자 수정
        this.hashtags = hashtags;
    }

    // --- Getter 메서드들 ---
    public int getId() { return id; }
    public int getThumbnailResourceId() { return thumbnailResourceId; }
    public String getTitle() { return title; }
    public String getHashtags() { return hashtags; }
    public LocalDate getDueDate() { return dueDate; } // ◀◀◀ 정렬을 위한 Getter

    /**
     * 현재 날짜를 기준으로 D-Day 또는 D+Day 문자열을 동적으로 계산하여 반환합니다.
     * @return "D-7", "D-Day", "D+5" 등의 형식으로 된 문자열
     */
    public String getdDayText() {
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, dueDate);

        if (daysBetween == 0) {
            return "D-Day";
        } else if (daysBetween > 0) {
            return "D-" + daysBetween;
        } else {
            return "D+" + Math.abs(daysBetween);
        }
    }
}