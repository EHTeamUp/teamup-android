package kr.mojuk.teamup.recruitment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.mojuk.teamup.api.model.CommentResponse;
import kr.mojuk.teamup.databinding.ItemCommentBinding;
import kr.mojuk.teamup.databinding.ItemReplyBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    private String formatTimestamp(Date date) {
        if (date == null) return "";

        long timeDiff = new Date().getTime() - date.getTime();

        // 미래 시간인 경우 (서버 시간과 클라이언트 시간 차이로 발생 가능)
        if (timeDiff < 0) {
            return "방금 전"; // 미래 시간은 "방금 전"으로 처리
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";

        long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
        if (hours < 24) return hours + "시간 전";

        long days = TimeUnit.MILLISECONDS.toDays(timeDiff);
        if (days < 7) return days + "일 전";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MM. dd.", Locale.KOREA);
        return sdf.format(date);
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

            binding.tvCommentUser.setText(comment.getUserId());
            binding.tvCommentContent.setText(comment.getContent());
            binding.tvCommentTimestamp.setText(formatTimestamp(comment.getCreatedAt()));

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

            binding.tvReplyUser.setText(comment.getUserId());
            binding.tvReplyContent.setText(comment.getContent());
            binding.tvCommentTimestamp.setText(formatTimestamp(comment.getCreatedAt()));

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
