package kr.mojuk.teamup.contest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.model.ContestInformation;

import kr.mojuk.teamup.databinding.ItemContestInformationBinding;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContestListAdapter extends ListAdapter<ContestInformation, ContestListAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int contestId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ContestListAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ContestInformation> DIFF_CALLBACK = new DiffUtil.ItemCallback<ContestInformation>() {
        @Override
        public boolean areItemsTheSame(@NonNull ContestInformation oldItem, @NonNull ContestInformation newItem) {
            return oldItem.getContestId() == newItem.getContestId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ContestInformation oldItem, @NonNull ContestInformation newItem) {
            return Objects.equals(oldItem, newItem); // DTO에 equals() 구현 권장
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContestInformationBinding binding = ItemContestInformationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemContestInformationBinding binding;

        public ViewHolder(ItemContestInformationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getItem(position).getContestId());
                }
            });
        }

        public void bind(ContestInformation contest) {
            Context context = itemView.getContext();

            Glide.with(context)
                    .load(contest.getPosterImgUrl())
                    .placeholder(R.drawable.poster_sample1)
                    .error(R.drawable.poster_sample2)
                    .into(binding.imageThumbnail);

            binding.title.setText(contest.getName());

            // --- 수정된 D-day 로직 ---
            LocalDate dueDate = contest.getDueDate();
            if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
                // 마감일이 오늘보다 이전이면 "마감됨"으로 표시
                binding.dday.setText("마감됨");
                binding.dday.setTextColor(Color.GRAY);
            } else {
                // 마감되지 않았으면 기존 dDayText 표시
                binding.dday.setText(contest.getdDayText());
                binding.dday.setTextColor(Color.parseColor("#FF5722"));
            }


            if (contest.getTags() != null && !contest.getTags().isEmpty()) {
                String tags = contest.getTags().stream()
                        .map(tag -> "#" + tag.getName())
                        .collect(Collectors.joining(" "));
                binding.hashtags.setText(tags);
            } else {
                binding.hashtags.setText("");
            }
        }
    }
}
