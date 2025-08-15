package com.example.teamup.contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.teamup.R;
import com.example.teamup.databinding.ActivityContestListBinding;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ContestListActivity extends AppCompatActivity {

    private ActivityContestListBinding binding;
    private ContestListAdapter adapter;
    private List<ContestInformation> allContests; // 필터링을 위한 원본 데이터 리스트

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContestListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupDropdownMenu();
        setupFilterButtons();
    }

    private void setupDropdownMenu() {
        binding.tvOngoingTitle.setOnClickListener(v -> {
            if (binding.layoutFilterBox.getVisibility() == View.GONE) {
                binding.layoutFilterBox.setVisibility(View.VISIBLE);
            } else {
                binding.layoutFilterBox.setVisibility(View.GONE);
            }
        });
    }

    private void setupRecyclerView() {
        // 1. 임시 데이터 생성
        List<ContestInformation> dummyContests = new ArrayList<>(Arrays.asList(
                new ContestInformation(1, R.drawable.poster_sample1, "배리어프리 앱 개발 콘테스트", LocalDate.now().plusDays(7), "#해커톤 #AI"),
                new ContestInformation(2, R.drawable.poster_sample2, "정부 데이터 활용 해커톤", LocalDate.now().minusDays(10), "#데이터분석 #Web"),
                new ContestInformation(3, R.drawable.poster_sample1, "대학생 게임 개발 대회", LocalDate.now().plusDays(30), "#게임 #기획")
        ));

        // 2. 마감일 기준으로 정렬
        Collections.sort(dummyContests, Comparator.comparing(ContestInformation::getDueDate));

        // 3. 정렬된 원본 데이터를 allContests에 저장
        this.allContests = new ArrayList<>(dummyContests);

        // 4. 어댑터를 한 번만 생성
        adapter = new ContestListAdapter(dummyContests);
        binding.recyclerviewContests.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewContests.setAdapter(adapter);

        adapter.setOnItemClickListener(contestId -> {
            Intent intent = new Intent(this, ContestInformationDetailActivity.class);
            intent.putExtra("CONTEST_ID", contestId);
            startActivity(intent);
        });
    }

    private void setupFilterButtons() {
        binding.btnApply.setOnClickListener(v -> applyFilter());
        binding.btnReset.setOnClickListener(v -> resetFilter());
    }

    private void applyFilter() {
        List<String> selectedFilters = getSelectedFilters();
        List<ContestInformation> filteredList;

        if (selectedFilters.isEmpty()) {
            filteredList = new ArrayList<>(allContests);
        } else {
            filteredList = new ArrayList<>();
            for (ContestInformation contest : allContests) {
                for (String filter : selectedFilters) {
                    if (contest.getHashtags().toLowerCase().contains(filter.toLowerCase())) {
                        filteredList.add(contest);
                        break;
                    }
                }
            }
        }

        // 어댑터의 리스트만 갱신 (새로 생성 X)
        adapter.filterList(filteredList);

        if (selectedFilters.isEmpty()) {
            binding.tvOngoingTitle.setText("전체 공모전 목록");
        } else {
            String title = selectedFilters.stream().collect(Collectors.joining(", ")) + " 관련 공모전";
            binding.tvOngoingTitle.setText(title);
        }
        binding.layoutFilterBox.setVisibility(View.GONE);
    }

    private void resetFilter() {
        uncheckAllFilters();
        adapter.filterList(new ArrayList<>(allContests));
        binding.tvOngoingTitle.setText("전체 공모전 목록");
        binding.layoutFilterBox.setVisibility(View.GONE);
    }

    private List<String> getSelectedFilters() {
        List<String> filters = new ArrayList<>();
        if (binding.checkboxWebApp.isChecked()) filters.add("웹/앱");
        if (binding.checkboxAi.isChecked()) filters.add("AI");
        if (binding.checkboxPlanning.isChecked()) filters.add("기획");
        if (binding.checkboxDataAnalysis.isChecked()) filters.add("데이터분석");
        if (binding.checkboxIdea.isChecked()) filters.add("아이디어");
        if (binding.checkboxIot.isChecked()) filters.add("IoT");
        return filters;
    }

    private void uncheckAllFilters() {
        binding.checkboxWebApp.setChecked(false);
        binding.checkboxAi.setChecked(false);
        binding.checkboxPlanning.setChecked(false);
        binding.checkboxDataAnalysis.setChecked(false);
        binding.checkboxIdea.setChecked(false);
        binding.checkboxIot.setChecked(false);
    }
}

