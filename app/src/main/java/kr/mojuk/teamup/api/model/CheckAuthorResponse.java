package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class CheckAuthorResponse {
    @SerializedName("is_author")
    private boolean isAuthor;

    public boolean isAuthor() {
        return isAuthor;
    }
}
