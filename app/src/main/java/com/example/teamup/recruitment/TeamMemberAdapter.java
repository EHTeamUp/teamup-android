package com.example.teamup.recruitment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.databinding.ItemContestRecruitmentMemberBinding;
import java.util.List;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.ViewHolder> {

    private final List<TeamMember> members;

    public TeamMemberAdapter(List<TeamMember> members) {
        this.members = members;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemContestRecruitmentMemberBinding binding;
        public ViewHolder(ItemContestRecruitmentMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(TeamMember member) {
            binding.tvButtonText.setText(member.getName());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContestRecruitmentMemberBinding binding = ItemContestRecruitmentMemberBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
