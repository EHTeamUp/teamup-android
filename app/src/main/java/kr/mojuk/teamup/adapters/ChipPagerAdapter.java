package kr.mojuk.teamup.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.mojuk.teamup.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import kr.mojuk.teamup.api.model.Role;
import kr.mojuk.teamup.api.model.Skill;

/**
 * ViewPager2용 ChipGroup 어댑터
 */
public class ChipPagerAdapter extends RecyclerView.Adapter<ChipPagerAdapter.ChipPageViewHolder> {

    private List<List<Object>> pages;
    private LayoutInflater inflater;

    public ChipPagerAdapter(LayoutInflater inflater, List<List<Object>> pages) {
        this.inflater = inflater;
        this.pages = pages;
    }

    @NonNull
    @Override
    public ChipPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.page_chip_group, parent, false);
        return new ChipPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipPageViewHolder holder, int position) {
        List<Object> pageItems = pages.get(position);
        holder.bind(pageItems, inflater);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public void updatePages(List<List<Object>> newPages) {
        this.pages = newPages;
        notifyDataSetChanged();
    }

    public static class ChipPageViewHolder extends RecyclerView.ViewHolder {
        private ChipGroup chipGroup;

        public ChipPageViewHolder(@NonNull View itemView) {
            super(itemView);
            chipGroup = itemView.findViewById(R.id.chipGroupPage);
        }

        public void bind(List<Object> items, LayoutInflater inflater) {
            chipGroup.removeAllViews();
            
            for (Object item : items) {
                Chip chip = (Chip) inflater.inflate(R.layout.view_chip_choice, chipGroup, false);
                
                if (item instanceof Skill) {
                    Skill skill = (Skill) item;
                    chip.setText(skill.getName());
                    chip.setTag(skill.getSkillId());
                } else if (item instanceof Role) {
                    Role role = (Role) item;
                    chip.setText(role.getName());
                    chip.setTag(role.getRoleId());
                }
                
                chipGroup.addView(chip);
            }
        }
    }
}
