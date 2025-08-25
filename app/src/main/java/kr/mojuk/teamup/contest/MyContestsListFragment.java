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

import kr.mojuk.teamup.MainActivity;
import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.ApiService;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.ApplicationResponse;
import kr.mojuk.teamup.api.model.ContestInformation;
import kr.mojuk.teamup.api.model.RecruitmentPostResponse;
import kr.mojuk.teamup.api.model.UserActivityApplication;
import kr.mojuk.teamup.api.model.UserActivityPost;
import kr.mojuk.teamup.api.model.UserActivityResponse;
import kr.mojuk.teamup.auth.TokenManager;
import kr.mojuk.teamup.databinding.FragmentContestMycontestListBinding;

import kr.mojuk.teamup.recruitment.ContestRecruitmentDetailFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyContestsListFragment extends Fragment {

    private FragmentContestMycontestListBinding binding;
    private ApiService apiService;
    private MyContestsAdapter adapter;
    private final List<MyContestItem> myContestItems = new ArrayList<>();
    private String currentUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fragment가 생성될 때 TokenManager를 통해 실제 사용자 ID를 가져옵니다.
        currentUserId = TokenManager.getInstance(requireContext()).getUserId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContestMycontestListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getInstance().getApiService();

        setupRecyclerView();
        loadMyActivities();

        // 뒤로가기 버튼 클릭 리스너
        binding.llContestTitleBar.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.btnGoToContest.setOnClickListener(v -> {
            if (getActivity() != null) {
                // ContestListFragment로 교체하고 하단 네비게이션 바 공모전 탭 활성화
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ContestListFragment())
                        .addToBackStack(null) // 뒤로가기 버튼으로 돌아올 수 있도록 스택에 추가
                        .commit();
                
                // 하단 네비게이션 바 공모전 탭 활성화
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).setBottomNavigationItem(R.id.navigation_contest);
                }
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new MyContestsAdapter();
        binding.rvMyContests.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMyContests.setAdapter(adapter);

        // 초기에는 안내 메시지 숨기고 RecyclerView 표시
        hideEmptyMessage();

        adapter.setOnItemClickListener(recruitmentPostId -> {
            if (getActivity() != null) {
                ContestRecruitmentDetailFragment detailFragment = ContestRecruitmentDetailFragment.newInstance(recruitmentPostId);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void loadMyActivities() {
        apiService.getUserActivity(currentUserId).enqueue(new Callback<UserActivityResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserActivityResponse> call, @NonNull Response<UserActivityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processActivityResponse(response.body());
                } else {
                    handleApiError("내 활동 정보 로딩 실패", response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserActivityResponse> call, @NonNull Throwable t) {
                handleApiFailure("getUserActivity", t);
            }
        });
    }

    private void processActivityResponse(UserActivityResponse response) {
        List<MyContestItem> myContestItems = new ArrayList<>();
        List<UserActivityPost> posts = response.getWrittenPosts() != null ? response.getWrittenPosts() : Collections.emptyList();
        List<UserActivityApplication> apps = response.getAcceptedApplications() != null ? response.getAcceptedApplications() : Collections.emptyList();

        int totalItems = posts.size() + apps.size();
        if (totalItems == 0) {
            adapter.submitList(Collections.emptyList());
            showEmptyMessage();
            return;
        }

        AtomicInteger counter = new AtomicInteger(totalItems);

        for (UserActivityPost post : posts) {
            fetchFullContestInfo(myContestItems, post.getRecruitmentPostId(), post.getContestId(), post.getUserId(), counter);
        }

        for (UserActivityApplication app : apps) {
            apiService.getRecruitmentPost(app.getRecruitmentPostId()).enqueue(new Callback<RecruitmentPostResponse>() {
                @Override
                public void onResponse(@NonNull Call<RecruitmentPostResponse> call, @NonNull Response<RecruitmentPostResponse> postResponse) {
                    if (postResponse.isSuccessful() && postResponse.body() != null) {
                        RecruitmentPostResponse postDetail = postResponse.body();
                        fetchFullContestInfo(myContestItems, app.getRecruitmentPostId(), postDetail.getContestId(), postDetail.getUserId(), counter);
                    } else {
                        checkIfAllDone(counter, myContestItems);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RecruitmentPostResponse> call, @NonNull Throwable t) {
                    handleApiFailure("getRecruitmentPost (for app)", t);
                    checkIfAllDone(counter, myContestItems);
                }
            });
        }
    }

    private void fetchFullContestInfo(List<MyContestItem> itemList, int recruitmentPostId, int contestId, String teamLeaderId, AtomicInteger counter) {
        apiService.getContestDetail(contestId).enqueue(new Callback<ContestInformation>() {
            @Override
            public void onResponse(@NonNull Call<ContestInformation> call, @NonNull Response<ContestInformation> contestResponse) {
                if (contestResponse.isSuccessful() && contestResponse.body() != null) {
                    String contestTitle = contestResponse.body().getName();
                    apiService.getAcceptedApplicationsByPost(recruitmentPostId).enqueue(new Callback<List<ApplicationResponse>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<ApplicationResponse>> call, @NonNull Response<List<ApplicationResponse>> membersResponse) {
                            int totalMembers = 0;
                            if (membersResponse.isSuccessful() && membersResponse.body() != null) {
                                totalMembers = membersResponse.body().size();
                            }
                            synchronized (itemList) {
                                itemList.add(new MyContestItem(recruitmentPostId, contestTitle, teamLeaderId, totalMembers));
                            }
                            checkIfAllDone(counter, itemList);
                        }
                        @Override
                        public void onFailure(@NonNull Call<List<ApplicationResponse>> call, @NonNull Throwable t) {
                            handleApiFailure("getAcceptedApplications", t);
                            checkIfAllDone(counter, itemList);
                        }
                    });
                } else {
                    checkIfAllDone(counter, itemList);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ContestInformation> call, @NonNull Throwable t) {
                handleApiFailure("getContestDetail", t);
                checkIfAllDone(counter, itemList);
            }
        });
    }

    private void checkIfAllDone(AtomicInteger counter, List<MyContestItem> items) {
        if (counter.decrementAndGet() == 0) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (items.isEmpty()) {
                        showEmptyMessage();
                    } else {
                        hideEmptyMessage();
                        adapter.submitList(new ArrayList<>(items));
                    }
                });
            }
        }
    }

    private void handleApiError(String message, int code) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message + " (코드: " + code + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleApiFailure(String methodName, Throwable t) {
        Log.e("MyContestsFragment", methodName + " API Call Failed: " + t.getMessage());
        if (getContext() != null) {
            Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 공모전이 없을 때 안내 메시지 표시
     */
    private void showEmptyMessage() {
        if (binding != null) {
            binding.llEmptyMessage.setVisibility(View.VISIBLE);
            binding.rvMyContests.setVisibility(View.GONE);
        }
    }

    /**
     * 안내 메시지 숨기고 RecyclerView 표시
     */
    private void hideEmptyMessage() {
        if (binding != null) {
            binding.llEmptyMessage.setVisibility(View.GONE);
            binding.rvMyContests.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}