package com.example.teamup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.R;
import com.example.teamup.contest.ContestInformation;
import com.example.teamup.contest.ContestInformationDetailActivity;
import com.example.teamup.contest.ContestListAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContestFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContestListAdapter adapter;
    private List<ContestInformation> allContests;

    public ContestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recycler_view_contest);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // 1. 임시 데이터 생성
        List<ContestInformation> dummyContests = new ArrayList<>(Arrays.asList(
                new ContestInformation(1, R.drawable.poster_sample1, "배리어프리 앱 개발 콘테스트", LocalDate.now().plusDays(7), "#해커톤 #AI"),
                new ContestInformation(2, R.drawable.poster_sample2, "정부 데이터 활용 해커톤", LocalDate.now().minusDays(10), "#데이터분석 #Web"),
                new ContestInformation(3, R.drawable.poster_sample1, "대학생 게임 개발 대회", LocalDate.now().plusDays(30), "#게임 #기획")
        ));

        // 2. 마감일 기준으로 정렬
        Collections.sort(dummyContests, Comparator.comparing(ContestInformation::getDueDate));

        // 3. 정렬된 원본 데이터를 allContests에 저장
        this.allContests = new ArrayList<>(dummyContests);

        // 4. 어댑터를 한 번만 생성
        adapter = new ContestListAdapter(dummyContests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(contestId -> {
            Intent intent = new Intent(getActivity(), ContestInformationDetailActivity.class);
            intent.putExtra("CONTEST_ID", contestId);
            startActivity(intent);
        });
    }
}
