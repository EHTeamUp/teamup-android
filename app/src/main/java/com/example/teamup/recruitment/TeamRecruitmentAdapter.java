package com.example.teamup.recruitment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.api.model.ContestInformation;
import com.example.teamup.databinding.ItemTeamRecruitmentBinding;
import java.util.Locale;
import java.util.Objects;

public class TeamRecruitmentAdapter extends ListAdapter<RecruitmentPostItem, TeamRecruitmentAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int postId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TeamRecruitmentAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<RecruitmentPostItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecruitmentPostItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecruitmentPostItem oldItem, @NonNull RecruitmentPostItem newItem) {
            return oldItem.getRecruitmentPostId() == newItem.getRecruitmentPostId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecruitmentPostItem oldItem, @NonNull RecruitmentPostItem newItem) {
            // RecruitmentPostItem에 equals()를 구현하거나, 모든 필드를 수동으로 비교합니다.
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    Objects.equals(oldItem.getUserId(), newItem.getUserId()) &&
                    oldItem.getCurrentMembers() == newItem.getCurrentMembers() &&
                    oldItem.getRecruitmentCount() == newItem.getRecruitmentCount();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTeamRecruitmentBinding binding = ItemTeamRecruitmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTeamRecruitmentBinding binding;

        public ViewHolder(ItemTeamRecruitmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getItem(position).getRecruitmentPostId());
                }
            });
        }

        public void bind(RecruitmentPostItem item) {
            ContestInformation contestInfo = item.getContestInformation();
            if (contestInfo != null) {
                binding.titleText.setText(contestInfo.getName());
                binding.dDayText.setText(contestInfo.getdDayText());
            } else {
                binding.titleText.setText("공모전 정보 로딩 중...");
                binding.dDayText.setText("D-?");
            }

            binding.organizerText.setText("모집자: " + item.getUserId());
            String peopleInfo = String.format(Locale.getDefault(), "모집 인원: %d / %d", item.getCurrentMembers(), item.getRecruitmentCount());
            binding.peopleText.setText(peopleInfo);
        }
    }
}
