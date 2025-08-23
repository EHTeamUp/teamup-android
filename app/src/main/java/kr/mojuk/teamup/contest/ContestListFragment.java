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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestListFragment extends Fragment {

    private FragmentContestListBinding binding;
    private ContestListAdapter adapter;
    private List<FilterItem> availableFilters = new ArrayList<>(); // 서버에서 받아온 필터 목록
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
        loadFilters(); // 먼저 필터 목록을 로드
        loadAllContests(); // 전체 공모전 로드
        setupDropdownMenu();
        setupFilterButtons();
    }

    private void setupRecyclerView() {
        adapter = new ContestListAdapter();
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

    // 서버에서 사용 가능한 필터 목록을 가져옴
    private void loadFilters() {
        apiService.getFilters().enqueue(new Callback<List<FilterItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<FilterItem>> call, @NonNull Response<List<FilterItem>> response) {
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

    // 전체 공모전을 로드 (필터 없음)
    private void loadAllContests() {
        apiService.getContests().enqueue(new Callback<ContestsListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ContestsListResponse> call, @NonNull Response<ContestsListResponse> response) {
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

                        adapter.submitList(new ArrayList<>(contestsFromServer));
                        if (binding != null) {
                            binding.tvOngoingTitle.setText("전체 공모전 목록");
                        }
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

    // 특정 필터로 공모전을 로드
    private void loadContestsByFilter(int filterId) {
        apiService.getContestsByFilter(filterId).enqueue(new Callback<ContestsListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ContestsListResponse> call, @NonNull Response<ContestsListResponse> response) {
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

        List<ContestInformation> combinedContests = new ArrayList<>();
        final int totalFilters = filterIds.size();
        final int[] completedRequests = {0}; // 완료된 요청 수를 추적

        for (int filterId : filterIds) {
            apiService.getContestsByFilter(filterId).enqueue(new Callback<ContestsListResponse>() {
                @Override
                public void onResponse(@NonNull Call<ContestsListResponse> call, @NonNull Response<ContestsListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ContestInformation> contests = response.body().getContests();
                        if (contests != null) {
                            // 중복 제거를 위해 ID 기준으로 합치기
                            for (ContestInformation contest : contests) {
                                boolean isDuplicate = false;
                                for (ContestInformation existing : combinedContests) {
                                    if (existing.getContestId() == contest.getContestId()) {
                                        isDuplicate = true;
                                        break;
                                    }
                                }
                                if (!isDuplicate) {
                                    combinedContests.add(contest);
                                }
                            }
                        }
                    }

                    completedRequests[0]++;

                    // 모든 요청이 완료되면 결과 표시
                    if (completedRequests[0] == totalFilters) {
                        // 날짜순 정렬
                        Collections.sort(combinedContests, (c1, c2) -> {
                            LocalDate d1 = c1.getDueDate();
                            LocalDate d2 = c2.getDueDate();
                            if (d1 == null || d2 == null) return 0;
                            return d1.compareTo(d2);
                        });

                        if (binding != null) {
                            adapter.submitList(new ArrayList<>(combinedContests));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ContestsListResponse> call, @NonNull Throwable t) {
                    Log.e("ContestListFragment", "Multi Filter API Call Failed: " + t.getMessage());
                    completedRequests[0]++;

                    // 모든 요청이 완료되면 (실패한 것들 포함) 현재까지의 결과 표시
                    if (completedRequests[0] == totalFilters) {
                        if (!combinedContests.isEmpty()) {
                            Collections.sort(combinedContests, (c1, c2) -> {
                                LocalDate d1 = c1.getDueDate();
                                LocalDate d2 = c2.getDueDate();
                                if (d1 == null || d2 == null) return 0;
                                return d1.compareTo(d2);
                            });
                            if (binding != null) {
                                adapter.submitList(new ArrayList<>(combinedContests));
                            }
                        } else {
                            Toast.makeText(getContext(), "필터된 공모전 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
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