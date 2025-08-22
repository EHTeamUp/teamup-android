package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

public class CommentUpdate {
    @SerializedName("content")
    private String content;

    public CommentUpdate(String content) {
        this.content = content;
    }
}
