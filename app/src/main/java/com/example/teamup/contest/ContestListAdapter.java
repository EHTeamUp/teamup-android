package com.example.teamup.contest;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.databinding.ItemContestInformationBinding;
import java.time.LocalDate;
import java.util.List;

public class ContestListAdapter extends RecyclerView.Adapter<ContestListAdapter.ViewHolder> {

    private List<ContestInformation> contestList; // final 키워드 제거
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int contestId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ContestListAdapter(List<ContestInformation> contestList) {
        this.contestList = contestList;
    }

    // RecyclerView의 목록을 갱신하는 메서드
    public void filterList(List<ContestInformation> filteredList) {
        this.contestList = filteredList;
        notifyDataSetChanged(); // 리스트가 변경되었음을 알림
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemContestInformationBinding binding;

        public ViewHolder(ItemContestInformationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(contestList.get(position).getId());
                }
            });
        }

        public void bind(ContestInformation contest) {
            binding.imageThumbnail.setImageResource(contest.getThumbnailResourceId());
            binding.title.setText(contest.getTitle());
            binding.dday.setText(contest.getdDayText());
            binding.hashtags.setText(contest.getHashtags());

            if (contest.getDueDate().isBefore(LocalDate.now())) {
                binding.dday.setTextColor(Color.GRAY);
            } else {
                binding.dday.setTextColor(Color.BLACK);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContestInformationBinding binding = ItemContestInformationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(contestList.get(position));
    }

    @Override
    public int getItemCount() {
        return contestList.size();
    }
}
