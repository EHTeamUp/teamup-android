package com.example.teamup.contest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // Fragment를 상속
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.teamup.R;
import com.example.teamup.api.ApiService;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.ContestInformation;
import com.example.teamup.api.model.ContestsListResponse; // DTO 이름 수정
import com.example.teamup.databinding.FragmentContestListBinding; // 바인딩 클래스도 Fragment용으로 변경

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 1단계: extends Fragment로 변경
public class ContestListFragment extends Fragment {

    private FragmentContestListBinding binding;
    private ContestListAdapter adapter;
    private List<ContestInformation> allContests = new ArrayList<>();
    private ApiService apiService;

    public ContestListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContestListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getInstance().getApiService();

        setupRecyclerView();
        loadContests();
        setupDropdownMenu();
        setupFilterButtons();
    }

    private void setupRecyclerView() {
        adapter = new ContestListAdapter(); // 생성자에서 리스트 제거
        binding.recyclerviewContests.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerviewContests.setAdapter(adapter);

        adapter.setOnItemClickListener(contestId -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, ContestInformationDetailFragment.newInstance(contestId))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void loadContests() {
        apiService.getContests().enqueue(new Callback<ContestsListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ContestsListResponse> call, @NonNull Response<ContestsListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ContestInformation> contestsFromServer = response.body().getContests();
                    if (contestsFromServer != null) {
                        Collections.sort(contestsFromServer, (c1, c2) -> {
                            LocalDate d1 = c1.getDueDate();
                            LocalDate d2 = c2.getDueDate();
                            if (d1 == null || d2 == null) return 0;
                            return d1.compareTo(d2);
                        });

                        allContests.clear();
                        allContests.addAll(contestsFromServer);
                        adapter.submitList(new ArrayList<>(allContests)); // submitList로 변경
                    }
                } else {
                    Toast.makeText(getContext(), "공모전 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContestsListResponse> call, @NonNull Throwable t) {
                Log.e("ContestListFragment", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- 필터 관련 로직 (내부는 동일) ---
    private void setupDropdownMenu() {
        binding.tvOngoingTitle.setOnClickListener(v -> {
            binding.layoutFilterBox.setVisibility(binding.layoutFilterBox.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
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
            filteredList = allContests.stream()
                    .filter(contest -> contest.getTags() != null && contest.getTags().stream()
                            .anyMatch(tag -> selectedFilters.stream()
                                    .anyMatch(filter -> tag.getName().equalsIgnoreCase(filter))))
                    .collect(Collectors.toList());
        }
        adapter.submitList(filteredList);

        binding.tvOngoingTitle.setText(selectedFilters.isEmpty() ? "전체 공모전 목록" :
                selectedFilters.stream().collect(Collectors.joining(", ")) + " 관련 공모전");
        binding.layoutFilterBox.setVisibility(View.GONE);
    }

    private void resetFilter() {
        uncheckAllFilters();
        adapter.submitList(new ArrayList<>(allContests));
        binding.tvOngoingTitle.setText("전체 공모전 목록");
        binding.layoutFilterBox.setVisibility(View.GONE);
    }

    private List<String> getSelectedFilters() {
        List<String> filters = new ArrayList<>();
        if (binding.checkboxWebApp.isChecked()) filters.add("웹/앱");
        if (binding.checkboxAi.isChecked()) filters.add("AI/데이터 사이언스");
        if (binding.checkboxPlanning.isChecked()) filters.add("아이디어/기획");
        if (binding.checkboxDataAnalysis.isChecked()) filters.add("IoT/임베디드");
        if (binding.checkboxIdea.isChecked()) filters.add("게임");
        if (binding.checkboxIot.isChecked()) filters.add("정보보안/블록체인");
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}