package com.example.teamup.recruitment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.api.model.ApplicationResponse;
import com.example.teamup.databinding.ItemContestRecruitmentMemberBinding;
import java.util.Objects;

public class TeamMemberAdapter extends ListAdapter<ApplicationResponse, TeamMemberAdapter.ViewHolder> {

    private OnMemberClickListener listener;

    public interface OnMemberClickListener {
        void onMemberClick(String userId);
    }

    public void setOnMemberClickListener(OnMemberClickListener listener) {
        this.listener = listener;
    }

    // ListAdapter는 생성자에서 리스트를 받지 않습니다.
    public TeamMemberAdapter() {
        super(DIFF_CALLBACK);
    }

    // DiffUtil.ItemCallback 구현
    private static final DiffUtil.ItemCallback<ApplicationResponse> DIFF_CALLBACK = new DiffUtil.ItemCallback<ApplicationResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull ApplicationResponse oldItem, @NonNull ApplicationResponse newItem) {
            // 각 아이템의 고유 ID를 비교합니다. 여기서는 userId를 사용합니다.
            return oldItem.getUserId().equals(newItem.getUserId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ApplicationResponse oldItem, @NonNull ApplicationResponse newItem) {
            // 아이템의 내용이 같은지 비교합니다.
            return Objects.equals(oldItem, newItem); // ApplicationResponse에 equals()가 구현되어 있어야 최적입니다.
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContestRecruitmentMemberBinding binding = ItemContestRecruitmentMemberBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemContestRecruitmentMemberBinding binding;

        public ViewHolder(ItemContestRecruitmentMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onMemberClick(getItem(position).getUserId());
                }
            });
        }

        public void bind(ApplicationResponse member) {
            binding.tvButtonText.setText(member.getUserId());
        }
    }
}
