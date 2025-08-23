package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// ContestInformation 클래스에서 사용할 Tag 데이터 모델입니다.
public class Tag implements Serializable {

    @SerializedName("tag_id")
    private int tagId;

    @SerializedName("name")
    private String name;

    // --- Getter 메서드들 ---
    public int getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }
}
