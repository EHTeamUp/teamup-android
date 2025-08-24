package kr.mojuk.teamup.contest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.ApiService;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.ContestInformation;
import kr.mojuk.teamup.api.model.ContestsListResponse;
import kr.mojuk.teamup.api.model.FilterItem; // 새로 추가된 필터 모델
import kr.mojuk.teamup.databinding.FragmentContestListBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestListFragment extends Fragment {

    private FragmentContestListBinding binding;
    private ContestListAdapter adapter;
    private List<FilterItem> availableFilters = new ArrayList<>();
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
        loadFilters();
        loadAllContests();
        setupDropdownMenu();
        setupFilterButtons();
    }

    private void setupRecyclerView() {
        adapter = new ContestListAdapter();
        binding.recyclerviewContests.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerviewContests.setAdapter(adapter);

        adapter.setOnItemClickListener(contest -> {
            if (getActivity() != null) {
                ContestInformationDetailFragment detailFragment = ContestInformationDetailFragment.newInstance(contest);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // --- 수정: 정렬 로직을 처리하는 공통 메서드 ---
    private void sortAndDisplayContests(List<ContestInformation> contests) {
        if (contests == null || contests.isEmpty()) {
            adapter.submitList(new ArrayList<>());
            return;
        }

        List<ContestInformation> ongoingContests = new ArrayList<>();
        List<ContestInformation> finishedContests = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 1. 진행 중인 공모전과 마감된 공모전을 분리
        for (ContestInformation contest : contests) {
            LocalDate dueDate = contest.getDueDate();
            if (dueDate != null && dueDate.isBefore(today)) {
                finishedContests.add(contest);
            } else {
                ongoingContests.add(contest);
            }
        }

        // 2. 진행 중인 공모전을 D-day가 적게 남은 순으로 정렬
        Collections.sort(ongoingContests, (c1, c2) -> {
            LocalDate d1 = c1.getDueDate();
            LocalDate d2 = c2.getDueDate();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1; // 날짜 없는 항목은 뒤로
            if (d2 == null) return -1;
            return d1.compareTo(d2);
        });

        // 3. 두 리스트를 합침 (진행 중 -> 마감 순)
        ongoingContests.addAll(finishedContests);

        // 4. 어댑터에 최종 리스트 전달
        adapter.submitList(ongoingContests);
    }

    // 전체 공모전을 로드 (필터 없음)
    private void loadAllContests() {
        apiService.getContests().enqueue(new Callback<ContestsListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ContestsListResponse> call, @NonNull Response<ContestsListResponse> response) {
                if (binding == null) return; // Fragment가 destroy된 경우 처리하지 않음
                
                if (response.isSuccessful() && response.body() != null) {
                    sortAndDisplayContests(response.body().getContests());
                    binding.tvOngoingTitle.setText("전체 공모전 목록");

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

    // 특정 필터로 공모전을 로드
    private void loadContestsByFilter(int filterId) {
        apiService.getContestsByFilter(filterId).enqueue(new Callback<ContestsListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ContestsListResponse> call, @NonNull Response<ContestsListResponse> response) {
                if (binding == null) return; // Fragment가 destroy된 경우 처리하지 않음
                
                if (response.isSuccessful() && response.body() != null) {
                    List<ContestInformation> contestsFromServer = response.body().getContests();
                    if (contestsFromServer != null) {
                        // 날짜순 정렬
                        Collections.sort(contestsFromServer, (c1, c2) -> {
                            LocalDate d1 = c1.getDueDate();
                            LocalDate d2 = c2.getDueDate();
                            if (d1 == null || d2 == null) return 0;
                            return d1.compareTo(d2);
                        });

                        if (binding != null) {
                            adapter.submitList(new ArrayList<>(contestsFromServer));
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "필터된 공모전 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContestsListResponse> call, @NonNull Throwable t) {
                Log.e("ContestListFragment", "Filtered API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 여러 필터로 공모전을 로드 (여러 API 호출 후 결과 합치기)
    private void loadContestsByMultipleFilters(List<Integer> filterIds) {
        if (filterIds.isEmpty()) {
            loadAllContests();
            return;
        }

        // 동시성 환경에서 안전한 Map을 사용하여 중복을 제거하고 결과를 저장
        final Map<Integer, ContestInformation> combinedContestsMap = new ConcurrentHashMap<>();
        // --- 수정: int[] 대신 AtomicInteger 사용 ---
        final AtomicInteger completedRequests = new AtomicInteger(0);
        final int totalFilters = filterIds.size();

        for (int filterId : filterIds) {
            apiService.getContestsByFilter(filterId).enqueue(new Callback<ContestsListResponse>() {
                @Override
                public void onResponse(@NonNull Call<ContestsListResponse> call, @NonNull Response<ContestsListResponse> response) {
                    if (binding == null) return; // Fragment가 destroy된 경우 처리하지 않음
                    
                    if (response.isSuccessful() && response.body() != null && response.body().getContests() != null) {
                        for (ContestInformation contest : response.body().getContests()) {
                            combinedContestsMap.put(contest.getContestId(), contest);
                        }
                    }
                    checkIfAllRequestsFinished();

                }

                @Override
                public void onFailure(@NonNull Call<ContestsListResponse> call, @NonNull Throwable t) {
                    Log.e("ContestListFragment", "Multi Filter API Call Failed: " + t.getMessage());
                    checkIfAllRequestsFinished();
                }

                private void checkIfAllRequestsFinished() {
                    // --- 수정: incrementAndGet() 메서드 사용 ---
                    if (completedRequests.incrementAndGet() == totalFilters) {
                        List<ContestInformation> finalList = new ArrayList<>(combinedContestsMap.values());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> sortAndDisplayContests(finalList));

                        }
                    }
                }
            });
        }
    }

    // 서버에서 사용 가능한 필터 목록을 가져옴
    private void loadFilters() {
        apiService.getFilters().enqueue(new Callback<List<FilterItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<FilterItem>> call, @NonNull Response<List<FilterItem>> response) {
                if (binding == null) return; // Fragment가 destroy된 경우 처리하지 않음
                
                if (response.isSuccessful() && response.body() != null) {
                    availableFilters.clear();
                    availableFilters.addAll(response.body());
                    Log.d("ContestListFragment", "Loaded " + availableFilters.size() + " filters");
                } else {
                    Log.e("ContestListFragment", "Failed to load filters");
                    Toast.makeText(getContext(), "필터 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FilterItem>> call, @NonNull Throwable t) {
                Log.e("ContestListFragment", "Filter API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "필터 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDropdownMenu() {
        if (binding != null) {
            binding.tvOngoingTitle.setOnClickListener(v -> {
                if (binding != null) {
                    binding.layoutFilterBox.setVisibility(binding.layoutFilterBox.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    private void setupFilterButtons() {
        if (binding != null) {
            binding.btnApply.setOnClickListener(v -> applyFilter());
            binding.btnReset.setOnClickListener(v -> resetFilter());
        }
    }

    private void applyFilter() {
        List<Integer> selectedFilterIds = getSelectedFilterIds();

        if (selectedFilterIds.isEmpty()) {
            // 필터가 선택되지 않으면 전체 공모전 로드
            loadAllContests();
        } else if (selectedFilterIds.size() == 1) {
            // 단일 필터 선택
            loadContestsByFilter(selectedFilterIds.get(0));
        } else {
            // 여러 필터 선택 (현재는 첫 번째 필터만 사용, 추후 API 확장시 수정)
            loadContestsByMultipleFilters(selectedFilterIds);
        }

        if (binding != null) {
            binding.layoutFilterBox.setVisibility(View.GONE);
        }
    }

    private void resetFilter() {
        uncheckAllFilters();
        loadAllContests();
        if (binding != null) {
            binding.layoutFilterBox.setVisibility(View.GONE);
        }
    }

    // 체크박스 상태를 확인하여 선택된 필터 ID들을 반환
    private List<Integer> getSelectedFilterIds() {
        List<Integer> filterIds = new ArrayList<>();

        if (binding != null) {
            // 체크박스와 필터 ID 매핑
            if (binding.checkboxWebApp.isChecked()) {
                filterIds.add(getFilterIdByName("웹/앱"));
            }
            if (binding.checkboxAi.isChecked()) {
                filterIds.add(getFilterIdByName("AI/데이터 사이언스"));
            }
            if (binding.checkboxPlanning.isChecked()) {
                filterIds.add(getFilterIdByName("아이디어/기획"));
            }
            if (binding.checkboxDataAnalysis.isChecked()) {
                filterIds.add(getFilterIdByName("IoT/임베디드"));
            }
            if (binding.checkboxIdea.isChecked()) {
                filterIds.add(getFilterIdByName("게임"));
            }
            if (binding.checkboxIot.isChecked()) {
                filterIds.add(getFilterIdByName("정보보안/블록체인"));
            }
        }

        // 유효하지 않은 ID(-1) 제거
        filterIds.removeIf(id -> id == -1);

        return filterIds;
    }

    // 필터명으로 필터 ID를 찾음
    private int getFilterIdByName(String filterName) {
        for (FilterItem filter : availableFilters) {
            if (filter.getName().equals(filterName)) {
                return filter.getFilterId();
            }
        }
        return -1; // 찾지 못한 경우
    }

    // 필터 ID로 필터명을 찾음
    private String getFilterNameById(int filterId) {
        for (FilterItem filter : availableFilters) {
            if (filter.getFilterId() == filterId) {
                return filter.getName();
            }
        }
        return "필터";
    }

    private void uncheckAllFilters() {
        if (binding != null) {
            binding.checkboxWebApp.setChecked(false);
            binding.checkboxAi.setChecked(false);
            binding.checkboxPlanning.setChecked(false);
            binding.checkboxDataAnalysis.setChecked(false);
            binding.checkboxIdea.setChecked(false);
            binding.checkboxIot.setChecked(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}