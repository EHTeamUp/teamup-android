package com.example.teamup.recruitment;

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

import com.example.teamup.R;
import com.example.teamup.api.ApiService;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.ApplicationResponse;
import com.example.teamup.api.model.ContestInformation;
import com.example.teamup.api.model.RecruitmentPostDTO;
import com.example.teamup.databinding.FragmentContestRecruitmentListBinding; // XML 파일명에 맞는 바인딩 클래스

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestRecruitmentListFragment extends Fragment {

    private FragmentContestRecruitmentListBinding binding;
    private ApiService apiService;
    private TeamRecruitmentAdapter adapter;
    private final List<RecruitmentPostItem> recruitmentItems = new ArrayList<>();

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
        loadAllRecruitmentPosts();
    }

    private void setupRecyclerView() {
        adapter = new TeamRecruitmentAdapter(); // 생성자에서 리스트 제거
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

    private void loadAllRecruitmentPosts() {
        apiService.getAllRecruitmentPosts().enqueue(new Callback<List<RecruitmentPostDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecruitmentPostDTO>> call, @NonNull Response<List<RecruitmentPostDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processRecruitmentPosts(response.body());
                } else {
                    Toast.makeText(getContext(), "모집글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecruitmentPostDTO>> call, @NonNull Throwable t) {
                handleApiFailure("loadAllRecruitmentPosts", t);
            }
        });
    }

    private void processRecruitmentPosts(List<RecruitmentPostDTO> posts) {
        if (posts.isEmpty()) {
            Toast.makeText(getContext(), "등록된 모집글이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<RecruitmentPostItem> items = new ArrayList<>();
        for (RecruitmentPostDTO post : posts) {
            items.add(new RecruitmentPostItem(
                    post.getRecruitmentPostId(),
                    post.getTitle(),
                    post.getUserId(),
                    post.getRecruitmentCount()
            ));
        }
        adapter.submitList(items);

        // 추가 정보를 비동기적으로 불러와 각 아이템을 업데이트
        fetchExtraInfoForList(items);
    }


    private void fetchExtraInfoForList(List<RecruitmentPostItem> items) {
        AtomicInteger counter = new AtomicInteger(items.size());

        for (RecruitmentPostItem item : items) {
            // 각 아이템에 대해 공모전 정보와 현재 팀원 수를 가져옵니다.
            apiService.getContestDetail(item.getContestInformation().getContestId()).enqueue(new Callback<ContestInformation>() {
                @Override
                public void onResponse(@NonNull Call<ContestInformation> call, @NonNull Response<ContestInformation> response) {
                    if (response.isSuccessful()) {
                        item.setContestInformation(response.body());
                    }
                    checkIfAllDone(counter, items);
                }
                @Override
                public void onFailure(@NonNull Call<ContestInformation> call, @NonNull Throwable t) {
                    handleApiFailure("getContestDetail", t);
                    checkIfAllDone(counter, items);
                }
            });

            apiService.getAcceptedApplicationsByPost(item.getRecruitmentPostId()).enqueue(new Callback<List<ApplicationResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<ApplicationResponse>> call, @NonNull Response<List<ApplicationResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        item.setCurrentMembers(response.body().size());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<ApplicationResponse>> call, @NonNull Throwable t) {
                    handleApiFailure("getAcceptedApplications", t);
                }
            });
        }
    }

    private void checkIfAllDone(AtomicInteger counter, List<RecruitmentPostItem> items) {
        // 모든 API 호출이 완료되면, 리스트를 다시 제출하여 화면을 갱신합니다.
        if (counter.decrementAndGet() == 0) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.submitList(new ArrayList<>(items)));
            }
        }
    }

    private void fetchExtraInfoForItem(RecruitmentPostItem item, int contestId, AtomicInteger counter) {
        apiService.getContestDetail(contestId).enqueue(new Callback<ContestInformation>() {
            @Override
            public void onResponse(@NonNull Call<ContestInformation> call, @NonNull Response<ContestInformation> response) {
                if (response.isSuccessful()) {
                    item.setContestInformation(response.body());
                }
                checkIfAllDone(counter);
            }
            @Override
            public void onFailure(@NonNull Call<ContestInformation> call, @NonNull Throwable t) {
                handleApiFailure("getContestDetail", t);
                checkIfAllDone(counter);
            }
        });

        apiService.getAcceptedApplicationsByPost(item.getRecruitmentPostId()).enqueue(new Callback<List<ApplicationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ApplicationResponse>> call, @NonNull Response<List<ApplicationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    item.setCurrentMembers(response.body().size());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<ApplicationResponse>> call, @NonNull Throwable t) {
                handleApiFailure("getAcceptedApplications", t);
            }
        });
    }

    private void checkIfAllDone(AtomicInteger counter) {
        if (counter.decrementAndGet() == 0) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
        }
    }

    private void handleApiFailure(String methodName, Throwable t) {
        Log.e("RecruitmentListFragment", methodName + " API Call Failed: " + t.getMessage());
        if (getContext() != null) {
            Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}