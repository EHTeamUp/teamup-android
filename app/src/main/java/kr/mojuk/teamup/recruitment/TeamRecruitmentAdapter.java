package kr.mojuk.teamup.recruitment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import kr.mojuk.teamup.api.model.RecruitmentPostDTO;
import kr.mojuk.teamup.databinding.ItemTeamRecruitmentBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class TeamRecruitmentAdapter extends ListAdapter<RecruitmentPostDTO, TeamRecruitmentAdapter.ViewHolder> {

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

    private static final DiffUtil.ItemCallback<RecruitmentPostDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecruitmentPostDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecruitmentPostDTO oldItem, @NonNull RecruitmentPostDTO newItem) {
            return oldItem.getRecruitmentPostId() == newItem.getRecruitmentPostId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecruitmentPostDTO oldItem, @NonNull RecruitmentPostDTO newItem) {
            // DTO 클래스에 equals()가 구현되어 있어야 합니다.
            return oldItem.equals(newItem);
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

        public void bind(RecruitmentPostDTO post) {
            binding.titleText.setText(post.getTitle());
            binding.organizerText.setText("모집자: " + post.getUserId());

            String peopleInfo = String.format(Locale.getDefault(), "모집 인원: %d / %d",
                    post.getAcceptedCount(), post.getRecruitmentCount());
            binding.peopleText.setText(peopleInfo);

            // D-Day 계산 로직을 ViewHolder 안으로 가져옵니다.
            binding.dDayText.setText(calculateDday(post.getDueDate()));
        }

        private String calculateDday(String dueDateString) {
            if (dueDateString == null || dueDateString.isEmpty()) {
                return "-";
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dueDate = LocalDate.parse(dueDateString, formatter);
                LocalDate today = LocalDate.now();
                long daysRemaining = ChronoUnit.DAYS.between(today, dueDate);

                if (daysRemaining < 0) {
                    return "마감";
                } else if (daysRemaining == 0) {
                    return "D-Day";
                } else {
                    return "D-" + daysRemaining;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "-";
            }
        }
    }
}
