package com.example.teamup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.R;
import com.example.teamup.recruitment.ContestRecruitmentDetailActivity;
import com.example.teamup.recruitment.RecruitmentPost;
import com.example.teamup.recruitment.TeamRecruitmentAdapter;

import java.util.Arrays;
import java.util.List;

public class BoardFragment extends Fragment {

    private RecyclerView recyclerView;

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recycler_view_board);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // 임시 데이터 생성
        List<RecruitmentPost> dummyPosts = Arrays.asList(
                new RecruitmentPost(1, "배리어프리 앱 개발 콘테스트", "D-10", 2, 6, "hong_gil", "100만원", Arrays.asList("Android", "iOS", "UI/UX")),
                new RecruitmentPost(2, "정부 데이터 활용 해커톤", "D-5", 3, 4, "dev_master", "500만원", Arrays.asList("Web", "AI", "BigData"))
        );

        // 어댑터 설정
        TeamRecruitmentAdapter adapter = new TeamRecruitmentAdapter(dummyPosts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 아이템 클릭 시 상세 화면으로 이동
        adapter.setOnItemClickListener(postId -> {
            Intent intent = new Intent(getActivity(), ContestRecruitmentDetailActivity.class);
            intent.putExtra("POST_ID", postId);
            startActivity(intent);
        });
    }
}
