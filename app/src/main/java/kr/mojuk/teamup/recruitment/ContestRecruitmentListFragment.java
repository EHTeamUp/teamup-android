package kr.mojuk.teamup.recruitment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.ApiService;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.FilterItem; // 필터 모델 import 추가
import kr.mojuk.teamup.api.model.RecruitmentPostDTO;
import kr.mojuk.teamup.databinding.FragmentContestRecruitmentListBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestRecruitmentListFragment extends Fragment {

    private FragmentContestRecruitmentListBinding binding;
    private ApiService apiService;
    private TeamRecruitmentAdapter adapter;

    // 원본 및 필터링된 데이터 저장을 위한 리스트
    private List<RecruitmentPostDTO> allPosts = new ArrayList<>();
    private List<RecruitmentPostDTO> currentlyDisplayedPosts = new ArrayList<>();
    private List<FilterItem> availableFilters = new ArrayList<>(); // 서버에서 받아올 필터 목록

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContestRecruitmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getInstance().getApiService();

        setupRecyclerView();
        setupClickListeners();
        loadFilters(); // 필터 목록 먼저 불러오기
        loadRecruitmentPosts();
    }

    // RecyclerView 설정
    private void setupRecyclerView() {
        adapter = new TeamRecruitmentAdapter();
        binding.recyclerViewBoard.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewBoard.setAdapter(adapter);

        adapter.setOnItemClickListener(postId -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, ContestRecruitmentDetailFragment.newInstance(postId))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // 모든 클릭 리스너 설정
    private void setupClickListeners() {
        // ▼▼▼ 추가된 부분: 드롭다운 메뉴 토글 ▼▼▼
        binding.tvCategoryFilterTitle.setOnClickListener(v -> {
            if (binding.layoutCheckboxFilterBox.getVisibility() == View.GONE) {
                binding.layoutCheckboxFilterBox.setVisibility(View.VISIBLE);
            } else {
                binding.layoutCheckboxFilterBox.setVisibility(View.GONE);
            }
        });
        // ▲▲▲ 추가된 부분 ▲▲▲

        // '적용' 버튼 리스너 (1차 필터링)
        binding.btnApply.setOnClickListener(v -> {
            List<Integer> selectedFilterIds = getSelectedFilterIds();
            List<RecruitmentPostDTO> filteredList = new ArrayList<>();

            if (selectedFilterIds.isEmpty()) {
                filteredList.addAll(allPosts);
            } else {
                for (RecruitmentPostDTO post : allPosts) {
                    if (selectedFilterIds.contains(post.getFilterId())) {
                        filteredList.add(post);
                    }
                }
            }
            updateRecyclerView(filteredList);
            updateContestSpinner(filteredList);
            binding.layoutCheckboxFilterBox.setVisibility(View.GONE); // 적용 후 메뉴 닫기
        });

        // '초기화' 버튼 리스너
        binding.btnReset.setOnClickListener(v -> {
            binding.checkboxWebApp.setChecked(false);
            binding.checkboxAi.setChecked(false);
            binding.checkboxGame.setChecked(false);
            binding.checkboxIot.setChecked(false);
            binding.checkboxPlanning.setChecked(false);
            binding.checkboxSecurity.setChecked(false);

            binding.spinnerSortFilter.setAdapter(null);
            updateRecyclerView(allPosts);
            binding.layoutCheckboxFilterBox.setVisibility(View.GONE); // 초기화 후 메뉴 닫기
        });

        // Spinner 아이템 선택 리스너 (2차 필터링)
        binding.spinnerSortFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedContest = (String) parent.getItemAtPosition(position);
                List<RecruitmentPostDTO> finalFilteredList = new ArrayList<>();

                if (selectedContest.equals("전체 공모전")) {
                    finalFilteredList.addAll(currentlyDisplayedPosts);
                } else {
                    for (RecruitmentPostDTO post : currentlyDisplayedPosts) {
                        if (post.getContestName().equals(selectedContest)) {
                            finalFilteredList.add(post);
                        }
                    }
                }
                adapter.submitList(finalFilteredList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 필수 구현 메서드
            }
        });
    }

    // ▼▼▼ 추가된 부분: 서버에서 필터 목록 불러오기 ▼▼▼
    private void loadFilters() {
        apiService.getFilters().enqueue(new Callback<List<FilterItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<FilterItem>> call, @NonNull Response<List<FilterItem>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    availableFilters.clear();
                    availableFilters.addAll(response.body());
                } else {
                    if (isAdded()) Toast.makeText(getContext(), "필터 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FilterItem>> call, @NonNull Throwable t) {
                if (isAdded()) Log.e("ContestRecruitmentList", "Failed to load filters", t);
            }
        });
    }
    // ▲▲▲ 추가된 부분 ▲▲▲

    // 서버에서 모든 게시글 데이터 불러오기
    private void loadRecruitmentPosts() {
        apiService.getAllRecruitmentPosts().enqueue(new Callback<List<RecruitmentPostDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecruitmentPostDTO>> call, @NonNull Response<List<RecruitmentPostDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPosts.clear();
                    allPosts.addAll(response.body());
                    updateRecyclerView(allPosts); // 초기 화면 설정
                } else {
                    Toast.makeText(getContext(), "모집글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecruitmentPostDTO>> call, @NonNull Throwable t) {
                Log.e("RecruitmentListFragment", "API Call Failed: " + t.getMessage());
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Spinner 내용 업데이트
    private void updateContestSpinner(List<RecruitmentPostDTO> posts) {
        Set<String> contestNamesSet = new HashSet<>();
        for (RecruitmentPostDTO post : posts) {
            contestNamesSet.add(post.getContestName());
        }

        List<String> contestNamesList = new ArrayList<>(contestNamesSet);
        Collections.sort(contestNamesList);
        contestNamesList.add(0, "전체 공모전");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, contestNamesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSortFilter.setAdapter(spinnerAdapter);
    }

    // RecyclerView와 현재 표시 목록 업데이트
    private void updateRecyclerView(List<RecruitmentPostDTO> postsToShow) {
        currentlyDisplayedPosts.clear();
        currentlyDisplayedPosts.addAll(postsToShow);
        adapter.submitList(new ArrayList<>(currentlyDisplayedPosts));
    }

    // ▼▼▼ 수정된 부분: 동적으로 필터 ID를 찾도록 변경 ▼▼▼
    private List<Integer> getSelectedFilterIds() {
        List<Integer> selectedIds = new ArrayList<>();
        if (binding.checkboxWebApp.isChecked()) selectedIds.add(getFilterIdByName("웹/앱"));
        if (binding.checkboxAi.isChecked()) selectedIds.add(getFilterIdByName("AI/데이터"));
        if (binding.checkboxPlanning.isChecked()) selectedIds.add(getFilterIdByName("아이디어/기획"));
        if (binding.checkboxIot.isChecked()) selectedIds.add(getFilterIdByName("IoT/임베디드"));
        if (binding.checkboxGame.isChecked()) selectedIds.add(getFilterIdByName("게임"));
        if (binding.checkboxSecurity.isChecked()) selectedIds.add(getFilterIdByName("정보보안/블록체인"));

        selectedIds.removeIf(id -> id == -1); // 찾지 못한 ID는 제거
        return selectedIds;
    }

    // 필터 이름으로 ID를 찾는 헬퍼 메서드
    private int getFilterIdByName(String filterName) {
        for (FilterItem filter : availableFilters) {
            if (filter.getName().equals(filterName)) {
                return filter.getFilterId();
            }
        }
        return -1; // 해당 이름의 필터를 찾지 못한 경우
    }
    // ▲▲▲ 수정된 부분 ▲▲▲

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
