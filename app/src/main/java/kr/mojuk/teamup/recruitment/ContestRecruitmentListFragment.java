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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.ApiService;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.FilterItem;
import kr.mojuk.teamup.api.model.RecruitmentPostDTO;
import kr.mojuk.teamup.databinding.FragmentContestRecruitmentListBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestRecruitmentListFragment extends Fragment {

    private FragmentContestRecruitmentListBinding binding;
    private ApiService apiService;
    private TeamRecruitmentAdapter adapter;

    private List<RecruitmentPostDTO> allPosts = new ArrayList<>();
    private List<RecruitmentPostDTO> currentlyDisplayedPosts = new ArrayList<>();
    private List<FilterItem> availableFilters = new ArrayList<>();

    private List<Integer> lastAppliedFilterIds = new ArrayList<>();

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

        // 초기 로딩 상태 설정 - 모든 UI 요소를 숨김
        showLoading(true);
        
        // 드롭다운 필터 박스도 명시적으로 숨김
        if (binding != null) {
            binding.layoutCheckboxFilterBox.setVisibility(View.GONE);
            binding.spinnerSortFilter.setVisibility(View.GONE);
        }
        
        setupRecyclerView();
        setupClickListeners();
        loadFilters();
        loadRecruitmentPosts();

        if (lastAppliedFilterIds.isEmpty()) {
            binding.tvFilterTitle.setText("전체 게시글 목록");
        }
    }

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

    private void setupClickListeners() {
        binding.tvCategoryFilterTitle.setOnClickListener(v -> {
            if (binding.layoutCheckboxFilterBox.getVisibility() == View.GONE) {
                binding.layoutCheckboxFilterBox.setVisibility(View.VISIBLE);
            } else {
                binding.layoutCheckboxFilterBox.setVisibility(View.GONE);
            }
        });

        binding.btnApply.setOnClickListener(v -> {
            List<Integer> selectedFilterIds = getSelectedFilterIds();
            lastAppliedFilterIds.clear();
            lastAppliedFilterIds.addAll(selectedFilterIds);
            updateFilterTitle(selectedFilterIds);
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
            sortAndDisplayPosts(filteredList); // 정렬 메서드 호출로 변경
            updateContestSpinner(filteredList);
            binding.layoutCheckboxFilterBox.setVisibility(View.GONE);

            binding.spinnerSortFilter.setVisibility(View.VISIBLE);
        });

        binding.btnReset.setOnClickListener(v -> {
            binding.checkboxWebApp.setChecked(false);
            binding.checkboxAi.setChecked(false);
            binding.checkboxGame.setChecked(false);
            binding.checkboxIot.setChecked(false);
            binding.checkboxPlanning.setChecked(false);
            binding.checkboxSecurity.setChecked(false);
            lastAppliedFilterIds.clear();
            updateFilterTitle(new ArrayList<>());
            binding.spinnerSortFilter.setAdapter(null);
            sortAndDisplayPosts(allPosts); // 정렬 메서드 호출로 변경
            binding.spinnerSortFilter.setVisibility(View.GONE);
            binding.layoutCheckboxFilterBox.setVisibility(View.GONE);
        });

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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showLoading(boolean isLoading) {
        if (binding == null) return;
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.recyclerViewBoard.setVisibility(View.GONE);
            binding.tvNoPosts.setVisibility(View.GONE);
            binding.tvCategoryFilterTitle.setVisibility(View.GONE); // 필터 제목 숨김
            binding.layoutCheckboxFilterBox.setVisibility(View.GONE); // 필터 박스 숨김
            binding.spinnerSortFilter.setVisibility(View.GONE); // 스피너 숨김
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerViewBoard.setVisibility(View.VISIBLE);
            binding.tvCategoryFilterTitle.setVisibility(View.VISIBLE); // 필터 제목 다시 표시
            // 필터 박스와 스피너는 조건부로 표시 (기본적으로는 숨김)
            binding.layoutCheckboxFilterBox.setVisibility(View.GONE); // 기본적으로 숨김
            binding.spinnerSortFilter.setVisibility(View.GONE); // 기본적으로 숨김
        }
    }

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

    private void loadRecruitmentPosts() {
        apiService.getAllRecruitmentPosts().enqueue(new Callback<List<RecruitmentPostDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecruitmentPostDTO>> call, @NonNull Response<List<RecruitmentPostDTO>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    allPosts.clear();
                    allPosts.addAll(response.body());
                    // ▼▼▼ 수정된 부분 ▼▼▼
                    // 뷰가 다시 생성되었을 때를 대비해 필터 상태를 복원합니다.
                    if (!lastAppliedFilterIds.isEmpty()) {
                        // 저장된 필터 ID가 있으면 필터링을 다시 적용합니다.
                        applySavedFilters();
                    } else {
                        // 저장된 필터가 없으면 전체 목록을 정렬하여 보여줍니다.
                        sortAndDisplayPosts(allPosts);
                    }
                    // ▲▲▲ 여기까지 ▲▲▲
                } else {
                    Toast.makeText(getContext(), "모집글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecruitmentPostDTO>> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e("RecruitmentListFragment", "API Call Failed: " + t.getMessage());
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void applySavedFilters() {
        List<RecruitmentPostDTO> filteredList = new ArrayList<>();

        if (lastAppliedFilterIds.isEmpty()) {
            filteredList.addAll(allPosts);
        } else {
            for (RecruitmentPostDTO post : allPosts) {
                if (lastAppliedFilterIds.contains(post.getFilterId())) {
                    filteredList.add(post);
                }
            }
        }

        updateFilterTitle(lastAppliedFilterIds);
        sortAndDisplayPosts(filteredList);
        updateContestSpinner(filteredList);
        binding.spinnerSortFilter.setVisibility(View.VISIBLE);
        updateCheckboxes();
    }
    private void updateCheckboxes() {
        if (binding == null) return;
        binding.checkboxWebApp.setChecked(lastAppliedFilterIds.contains(getFilterIdByName("웹/앱")));
        binding.checkboxAi.setChecked(lastAppliedFilterIds.contains(getFilterIdByName("AI/데이터 사이언스")));
        binding.checkboxPlanning.setChecked(lastAppliedFilterIds.contains(getFilterIdByName("아이디어/기획")));
        binding.checkboxIot.setChecked(lastAppliedFilterIds.contains(getFilterIdByName("IoT/임베디드")));
        binding.checkboxGame.setChecked(lastAppliedFilterIds.contains(getFilterIdByName("게임")));
        binding.checkboxSecurity.setChecked(lastAppliedFilterIds.contains(getFilterIdByName("정보보안/블록체인")));
    }

    // ▼▼▼ 새로 추가된 정렬 메서드 ▼▼▼
    private void sortAndDisplayPosts(List<RecruitmentPostDTO> posts) {
        if (posts == null || posts.isEmpty()) {
            showEmptyMessage(true);
            updateRecyclerView(new ArrayList<>());
            return;
        }

        List<RecruitmentPostDTO> ongoingPosts = new ArrayList<>();
        List<RecruitmentPostDTO> finishedPosts = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (RecruitmentPostDTO post : posts) {
            LocalDate dueDate = post.getDueDateAsDate();
            if (dueDate != null && dueDate.isBefore(today)) {
                finishedPosts.add(post);
            } else {
                ongoingPosts.add(post);
            }
        }

        Collections.sort(ongoingPosts, (p1, p2) -> {
            LocalDate d1 = p1.getDueDateAsDate();
            LocalDate d2 = p2.getDueDateAsDate();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d1.compareTo(d2);
        });

        ongoingPosts.addAll(finishedPosts);

        // 최종 리스트가 비어있는지 확인
        if (ongoingPosts.isEmpty()) {
            showEmptyMessage(true);
        } else {
            showEmptyMessage(false);
        }

        updateRecyclerView(ongoingPosts);
    }

    private void showEmptyMessage(boolean isEmpty) {
        if (binding == null) return;

        if (isEmpty) {
            binding.tvNoPosts.setVisibility(View.VISIBLE);
            binding.recyclerViewBoard.setVisibility(View.GONE);
        } else {
            binding.tvNoPosts.setVisibility(View.GONE);
            binding.recyclerViewBoard.setVisibility(View.VISIBLE);
        }
    }

    //필터 ID로 필터 이름을 찾기
    private String getFilterNameById(int filterId) {
        for (FilterItem filter : availableFilters) {
            if (filter.getFilterId() == filterId) {
                return filter.getName();
            }
        }
        return "필터"; // 찾지 못한 경우 기본값
    }

    // 선택된 필터 ID 목록을 기반으로 드롭다운 제목을 업데이트
    private void updateFilterTitle(List<Integer> selectedIds) {
        if (binding == null) return;

        if (selectedIds.isEmpty()) {
            binding.tvFilterTitle.setText("전체 게시글 목록");
        } else if (selectedIds.size() == 1) {
            String filterName = getFilterNameById(selectedIds.get(0));
            binding.tvFilterTitle.setText(filterName);
        } else {
            List<Integer> sortedIds = new ArrayList<>(selectedIds);
            Collections.sort(sortedIds);

            String firstFilterName = getFilterNameById(sortedIds.get(0));
            int otherFiltersCount = sortedIds.size() - 1;
            String title = firstFilterName + " 외 " + otherFiltersCount + "개";
            binding.tvFilterTitle.setText(title);
        }
    }

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

    private void updateRecyclerView(List<RecruitmentPostDTO> postsToShow) {
        currentlyDisplayedPosts.clear();
        currentlyDisplayedPosts.addAll(postsToShow);
        adapter.submitList(new ArrayList<>(currentlyDisplayedPosts));

        if (postsToShow.isEmpty()) {
            binding.spinnerSortFilter.setVisibility(View.GONE);
        } else if (!lastAppliedFilterIds.isEmpty()) {
            binding.spinnerSortFilter.setVisibility(View.VISIBLE);
        }
    }

    private List<Integer> getSelectedFilterIds() {
        List<Integer> selectedIds = new ArrayList<>();
        if (binding.checkboxWebApp.isChecked()) selectedIds.add(getFilterIdByName("웹/앱"));
        if (binding.checkboxAi.isChecked()) selectedIds.add(getFilterIdByName("AI/데이터 사이언스"));
        if (binding.checkboxPlanning.isChecked()) selectedIds.add(getFilterIdByName("아이디어/기획"));
        if (binding.checkboxIot.isChecked()) selectedIds.add(getFilterIdByName("IoT/임베디드"));
        if (binding.checkboxGame.isChecked()) selectedIds.add(getFilterIdByName("게임"));
        if (binding.checkboxSecurity.isChecked()) selectedIds.add(getFilterIdByName("정보보안/블록체인"));

        selectedIds.removeIf(id -> id == -1);
        return selectedIds;
    }

    private int getFilterIdByName(String filterName) {
        for (FilterItem filter : availableFilters) {
            if (filter.getName().equals(filterName)) {
                return filter.getFilterId();
            }
        }
        return -1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}