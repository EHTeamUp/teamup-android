package com.example.teamup.api.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ContestInformation {

    @SerializedName("contest_id")
    private int contestId;

    @SerializedName("name")
    private String name;

    @SerializedName("poster_img_url")
    private String posterImgUrl;

    @SerializedName("due_date")
    private String dueDateString;
    
    @SerializedName("start_date")
    private String startDate;

    @SerializedName("tags")
    private List<Tag> tags;

    @SerializedName("contest_url") // 서버 JSON의 'contest_url' 키와 일치시킴
    private String contestUrl;




    public int getContestId() { return contestId; }

    public String getName() { return name; }
    public String getPosterImgUrl() { return posterImgUrl; }
    public String getStartDate() { return startDate; }
    public List<Tag> getTags() { return tags; }
    public String getContestUrl(){ return contestUrl; }

    /**
     * 서버에서 받은 due_date (String)를 LocalDate 객체로 변환하여 반환합니다.
     * @return 마감일을 나타내는 LocalDate 객체, 파싱 실패 시 null 반환
     */
    public LocalDate getDueDate() {
        try {
            return LocalDate.parse(dueDateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 현재 날짜를 기준으로 D-Day 또는 D+Day 문자열을 동적으로 계산하여 반환합니다.
     * @return "D-7", "D-Day", "D+5" 등의 형식으로 된 문자열
     */
    public String getdDayText() {
        LocalDate dueDate = getDueDate();
        if (dueDate == null) {
            return "날짜 미정";
        }
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