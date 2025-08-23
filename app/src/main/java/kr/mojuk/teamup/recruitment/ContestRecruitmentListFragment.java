package kr.mojuk.teamup.recruitment;

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
import kr.mojuk.teamup.api.model.RecruitmentPostDTO;
import kr.mojuk.teamup.databinding.FragmentContestRecruitmentListBinding; // ViewBinding 클래스

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestRecruitmentListFragment extends Fragment {

    private FragmentContestRecruitmentListBinding binding;
    private ApiService apiService;
    private TeamRecruitmentAdapter adapter; // 새로 만든 어댑터

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
        loadRecruitmentPosts();
    }

    private void setupRecyclerView() {
        adapter = new TeamRecruitmentAdapter(); // 어댑터 생성
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

    private void loadRecruitmentPosts() {
        apiService.getAllRecruitmentPosts().enqueue(new Callback<List<RecruitmentPostDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecruitmentPostDTO>> call, @NonNull Response<List<RecruitmentPostDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // --- 수정: 정렬 로직 추가 ---
                    List<RecruitmentPostDTO> sortedList = sortPostsByDday(response.body());
                    adapter.submitList(sortedList);
                    // --- 수정 끝 ---
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
    private List<RecruitmentPostDTO> sortPostsByDday(List<RecruitmentPostDTO> posts) {
        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        List<RecruitmentPostDTO> ongoingPosts = new ArrayList<>();
        List<RecruitmentPostDTO> finishedPosts = new ArrayList<>();

        // 1. 진행 중인 글과 마감된 글을 분리
        for (RecruitmentPostDTO post : posts) {
            try {
                LocalDate dueDate = LocalDate.parse(post.getDueDate(), formatter);
                if (dueDate.isBefore(today)) {
                    finishedPosts.add(post);
                } else {
                    ongoingPosts.add(post);
                }
            } catch (Exception e) {
                ongoingPosts.add(post); // 날짜 정보가 없거나 잘못되면 진행 중으로 간주
            }
        }

        // 2. 진행 중인 글을 D-day가 적게 남은 순(오름차순)으로 정렬
        Collections.sort(ongoingPosts, (p1, p2) -> {
            try {
                LocalDate date1 = LocalDate.parse(p1.getDueDate(), formatter);
                LocalDate date2 = LocalDate.parse(p2.getDueDate(), formatter);
                return date1.compareTo(date2);
            } catch (Exception e) {
                return 0;
            }
        });

        // 3. 두 리스트를 합침
        ongoingPosts.addAll(finishedPosts);
        return ongoingPosts;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}