package kr.mojuk.teamup.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentWithReplies extends CommentResponse {
    @SerializedName("replies")
    private List<CommentResponse> replies;

    public List<CommentResponse> getReplies() { return replies; }
}
