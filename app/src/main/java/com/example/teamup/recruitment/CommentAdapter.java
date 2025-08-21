package com.example.teamup.recruitment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.api.model.CommentResponse;
import com.example.teamup.databinding.ItemCommentBinding;
import com.example.teamup.databinding.ItemReplyBinding;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_COMMENT = 1;
    public static final int VIEW_TYPE_REPLY = 2;

    private final List<CommentResponse> flatCommentList;
    private final String currentUserId;
    private CommentActionListener actionListener;
    private int editingPosition = -1; // 현재 수정 중인 아이템의 위치, -1이면 수정 중 아님

    // Activity와 상호작용하기 위한 리스너 인터페이스
    public interface CommentActionListener {
        void onReplyClick(CommentResponse comment);
        void onSaveClick(CommentResponse comment, String newContent);
        void onDeleteClick(CommentResponse comment);
    }

    public CommentAdapter(List<CommentResponse> flatCommentList, String currentUserId, CommentActionListener listener) {
        this.flatCommentList = flatCommentList;
        this.currentUserId = currentUserId;
        this.actionListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        // parentCommentId가 null이면 최상위 댓글, 아니면 대댓글
        return flatCommentList.get(position).getParentCommentId() == null ? VIEW_TYPE_COMMENT : VIEW_TYPE_REPLY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_COMMENT) {
            return new CommentViewHolder(ItemCommentBinding.inflate(inflater, parent, false));
        } else {
            return new ReplyViewHolder(ItemReplyBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentResponse comment = flatCommentList.get(position);
        boolean isEditing = position == editingPosition;

        if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).bind(comment, isEditing);
        } else if (holder instanceof ReplyViewHolder) {
            ((ReplyViewHolder) holder).bind(comment, isEditing);
        }
    }

    @Override
    public int getItemCount() {
        return flatCommentList.size();
    }

    // --- ViewHolder들 ---

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;

        CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CommentResponse comment, boolean isEditing) {
            // 모드 전환
            binding.layoutReadMode.setVisibility(isEditing ? View.GONE : View.VISIBLE);
            binding.layoutEditMode.setVisibility(isEditing ? View.VISIBLE : View.GONE);

            // 데이터 바인딩
            binding.tvCommentUser.setText(comment.getUserId());
            binding.tvCommentContent.setText(comment.getContent());

            // 수정 모드일 때 EditText에 기존 내용 채우기
            if (isEditing) {
                binding.etCommentEdit.setText(comment.getContent());
                binding.etCommentEdit.requestFocus();
            }

            // 본인 댓글인 경우에만 수정/삭제 버튼 표시
            boolean isAuthor = currentUserId.equals(comment.getUserId());
            binding.tvSeparator1.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvEditButton.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvSeparator2.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvDeleteButton.setVisibility(isAuthor ? View.VISIBLE : View.GONE);

            // --- 리스너 설정 ---
            binding.tvReplyButton.setOnClickListener(v -> actionListener.onReplyClick(comment));
            binding.tvDeleteButton.setOnClickListener(v -> actionListener.onDeleteClick(comment));

            // 수정 시작
            binding.tvEditButton.setOnClickListener(v -> {
                editingPosition = getAdapterPosition();
                notifyItemChanged(editingPosition);
            });

            // 수정 취소
            binding.tvCancelButton.setOnClickListener(v -> {
                int position = editingPosition;
                editingPosition = -1;
                notifyItemChanged(position);
            });

            // 수정 저장
            binding.tvSaveButton.setOnClickListener(v -> {
                String newContent = binding.etCommentEdit.getText().toString().trim();
                actionListener.onSaveClick(comment, newContent);
                int position = editingPosition;
                editingPosition = -1;
                // Activity에서 API 호출 성공 후 데이터를 갱신하고 notifyItemChanged를 호출해줄 것임
            });
        }
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {
        private final ItemReplyBinding binding;

        ReplyViewHolder(ItemReplyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CommentResponse comment, boolean isEditing) {
            // CommentViewHolder와 거의 동일한 로직
            binding.layoutReadModeReply.setVisibility(isEditing ? View.GONE : View.VISIBLE);
            binding.layoutEditModeReply.setVisibility(isEditing ? View.VISIBLE : View.GONE);

            binding.tvReplyUser.setText(comment.getUserId());
            binding.tvReplyContent.setText(comment.getContent());

            if (isEditing) {
                binding.etReplyEdit.setText(comment.getContent());
                binding.etReplyEdit.requestFocus();
            }

            boolean isAuthor = currentUserId.equals(comment.getUserId());
            binding.tvSeparatorReply1.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvEditButtonReply.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvSeparatorReply2.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvDeleteButtonReply.setVisibility(isAuthor ? View.VISIBLE : View.GONE);

            // --- 리스너 설정 ---
            binding.tvReplyButtonReply.setOnClickListener(v -> actionListener.onReplyClick(comment));
            binding.tvDeleteButtonReply.setOnClickListener(v -> actionListener.onDeleteClick(comment));
            binding.tvEditButtonReply.setOnClickListener(v -> {
                editingPosition = getAdapterPosition();
                notifyItemChanged(editingPosition);
            });
            binding.tvCancelButtonReply.setOnClickListener(v -> {
                int position = editingPosition;
                editingPosition = -1;
                notifyItemChanged(position);
            });
            binding.tvSaveButtonReply.setOnClickListener(v -> {
                String newContent = binding.etReplyEdit.getText().toString().trim();
                actionListener.onSaveClick(comment, newContent);
                int position = editingPosition;
                editingPosition = -1;
            });
        }
    }
}