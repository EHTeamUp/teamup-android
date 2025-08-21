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
import com.example.teamup.api.model.RecruitmentPostDTO;
import com.example.teamup.databinding.FragmentContestRecruitmentListBinding; // ViewBinding 클래스

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
        // 서버에서 모든 모집글 데이터를 가져옵니다.
        apiService.getAllRecruitmentPosts().enqueue(new Callback<List<RecruitmentPostDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecruitmentPostDTO>> call, @NonNull Response<List<RecruitmentPostDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공 시 어댑터에 데이터 리스트를 전달
                    adapter.submitList(response.body());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}