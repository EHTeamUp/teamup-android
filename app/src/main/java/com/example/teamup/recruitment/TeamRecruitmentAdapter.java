package com.example.teamup.recruitment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.R;
import com.example.teamup.databinding.ItemTeamRecruitmentBinding;
import java.util.List;

public class TeamRecruitmentAdapter extends RecyclerView.Adapter<TeamRecruitmentAdapter.ViewHolder> {

    private final List<RecruitmentPost> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int postId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TeamRecruitmentAdapter(List<RecruitmentPost> items) {
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTeamRecruitmentBinding binding;
        public ViewHolder(ItemTeamRecruitmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(items.get(position).getId());
                }
            });
        }
        public void bind(RecruitmentPost item) {
            binding.titleText.setText(item.getTitle());
            binding.dDayText.setText(item.getdDay());
            binding.peopleText.setText("모집 인원: " + item.getCurrentMembers() + " / " + item.getTotalMembers());
            binding.organizerText.setText("모집자: " + item.getOrganizer());
            binding.prizeText.setText("상금: " + item.getPrize());
            binding.tagsLayout.removeAllViews();
            Context context = itemView.getContext();
            for (String tagName : item.getTags()) {
                TextView tagView = createTagTextView(context, tagName);
                binding.tagsLayout.addView(tagView);
            }
        }
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTeamRecruitmentBinding binding = ItemTeamRecruitmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    private TextView createTagTextView(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(12f);
        textView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_gray_box)); // drawable이 없다면 임시로 다른 drawable 사용
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        int paddingHorizontal = (int) (12 * context.getResources().getDisplayMetrics().density);
        int paddingVertical = (int) (6 * context.getResources().getDisplayMetrics().density);
        textView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = (int) (8 * context.getResources().getDisplayMetrics().density);
        textView.setLayoutParams(params);
        return textView;
    }
}

