package com.example.teamup.recruitment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.databinding.ItemCommentBinding;
import com.example.teamup.databinding.ItemReplyBinding;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_COMMENT = 1;
    public static final int VIEW_TYPE_REPLY = 2;

    private final List<Comment> commentList;
    private OnReplyClickListener replyClickListener; // ◀◀◀ 리스너 멤버 변수 추가

    public interface OnReplyClickListener {
        void onReplyClick(String username);
    }

    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.replyClickListener = listener;
    }

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int getItemViewType(int position) {
        return commentList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_COMMENT) {
            ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new CommentViewHolder(binding);
        } else {
            ItemReplyBinding binding = ItemReplyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ReplyViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).bind(comment);
        } else if (holder instanceof ReplyViewHolder) {
            ((ReplyViewHolder) holder).bind(comment);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // 일반 댓글 ViewHolder
    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;
        public CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(Comment comment) {
            binding.tvCommentUser.setText(comment.getUser());
            binding.tvCommentContent.setText(comment.getContent());

            binding.tvReplyButton.setOnClickListener(v -> {
                if (replyClickListener != null) {
                    replyClickListener.onReplyClick(comment.getUser());
                }
            });
        }
    }

    // 대댓글 ViewHolder
    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        private final ItemReplyBinding binding;
        public ReplyViewHolder(ItemReplyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(Comment comment) {
            binding.tvReplyUser.setText(comment.getUser());
            binding.tvReplyContent.setText(comment.getContent());
        }
    }
}
