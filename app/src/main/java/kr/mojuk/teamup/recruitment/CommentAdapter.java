package kr.mojuk.teamup.recruitment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.mojuk.teamup.api.model.CommentResponse;
import kr.mojuk.teamup.databinding.ItemCommentBinding;
import kr.mojuk.teamup.databinding.ItemReplyBinding;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_COMMENT = 1;
    public static final int VIEW_TYPE_REPLY = 2;

    private final List<CommentResponse> flatCommentList;
    private final String currentUserId;
    private CommentActionListener actionListener;
    private int editingPosition = -1;

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

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;

        CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CommentResponse comment, boolean isEditing) {
            binding.layoutReadMode.setVisibility(isEditing ? View.GONE : View.VISIBLE);
            binding.layoutEditMode.setVisibility(isEditing ? View.VISIBLE : View.GONE);

            binding.tvCommentContent.setText(comment.getContent());

            // ▼▼▼ 수정된 부분 ▼▼▼
            // (수정됨) 표시 로직을 제거하고, 사용자 ID만 표시합니다.
            binding.tvCommentUser.setText(comment.getUserId());
            // ▲▲▲ 수정된 부분 ▲▲▲

            if (isEditing) {
                binding.etCommentEdit.setText(comment.getContent());
                binding.etCommentEdit.requestFocus();
            }

            boolean isAuthor = currentUserId.equals(comment.getUserId());
            binding.tvSeparator1.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvEditButton.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvSeparator2.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvDeleteButton.setVisibility(isAuthor ? View.VISIBLE : View.GONE);

            binding.tvReplyButton.setOnClickListener(v -> actionListener.onReplyClick(comment));
            binding.tvDeleteButton.setOnClickListener(v -> actionListener.onDeleteClick(comment));

            binding.tvEditButton.setOnClickListener(v -> {
                editingPosition = getAdapterPosition();
                notifyItemChanged(editingPosition);
            });

            binding.tvCancelButton.setOnClickListener(v -> {
                int position = editingPosition;
                editingPosition = -1;
                notifyItemChanged(position);
            });

            binding.tvSaveButton.setOnClickListener(v -> {
                String newContent = binding.etCommentEdit.getText().toString().trim();
                actionListener.onSaveClick(comment, newContent);
                editingPosition = -1;
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
            binding.layoutReadModeReply.setVisibility(isEditing ? View.GONE : View.VISIBLE);
            binding.layoutEditModeReply.setVisibility(isEditing ? View.VISIBLE : View.GONE);

            binding.tvReplyContent.setText(comment.getContent());

            // ▼▼▼ 수정된 부분 ▼▼▼
            // (수정됨) 표시 로직을 제거하고, 사용자 ID만 표시합니다.
            binding.tvReplyUser.setText(comment.getUserId());
            // ▲▲▲ 수정된 부분 ▲▲▲

            if (isEditing) {
                binding.etReplyEdit.setText(comment.getContent());
                binding.etReplyEdit.requestFocus();
            }

            boolean isAuthor = currentUserId.equals(comment.getUserId());
            binding.tvSeparatorReply1.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvEditButtonReply.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvSeparatorReply2.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            binding.tvDeleteButtonReply.setVisibility(isAuthor ? View.VISIBLE : View.GONE);

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
                editingPosition = -1;
            });
        }
    }
}
