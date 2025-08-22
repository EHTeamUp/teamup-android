package com.example.teamup.contest;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.databinding.ItemContestMycontestBinding;
import java.util.Locale;
import java.util.Objects;

public class MyContestsAdapter extends ListAdapter<MyContestItem, MyContestsAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int recruitmentPostId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MyContestsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MyContestItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<MyContestItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull MyContestItem oldItem, @NonNull MyContestItem newItem) {
            return oldItem.getRecruitmentPostId() == newItem.getRecruitmentPostId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MyContestItem oldItem, @NonNull MyContestItem newItem) {
            return Objects.equals(oldItem, newItem); // MyContestItem에 equals() 구현 권장
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContestMycontestBinding binding = ItemContestMycontestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemContestMycontestBinding binding;

        public ViewHolder(ItemContestMycontestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position).getRecruitmentPostId());
                }
            });
        }

        public void bind(MyContestItem item) {
            binding.tvContestTitle.setText(item.getContestTitle());

            int otherMembersCount = item.getOtherMembersCount();
            if (otherMembersCount > 0) {
                String teamInfo = String.format(Locale.getDefault(), "팀원: %s 외 %d명", item.getTeamLeaderId(), otherMembersCount);
                binding.tvTeamMembers.setText(teamInfo);
            } else {
                binding.tvTeamMembers.setText("팀원: " + item.getTeamLeaderId());
            }
        }
    }
}
