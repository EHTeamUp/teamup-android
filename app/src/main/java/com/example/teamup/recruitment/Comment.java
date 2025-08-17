package com.example.teamup.recruitment;

import java.util.List;

public class Comment {
    private final int id;
    private final String user;
    private final String content;
    private final List<Comment> replies; // 대댓글 목록
    private final int viewType; // 뷰 타입을 구분하기 위한 변수

    // 생성자
    public Comment(int id, String user, String content, List<Comment> replies, int viewType) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.replies = replies;
        this.viewType = viewType;
    }

    // Getter
    public int getId() { return id; }
    public String getUser() { return user; }
    public String getContent() { return content; }
    public List<Comment> getReplies() { return replies; }
    public int getViewType() { return viewType; }
}