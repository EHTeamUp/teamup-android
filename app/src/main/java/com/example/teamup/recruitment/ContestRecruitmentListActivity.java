package com.example.teamup.recruitment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.teamup.databinding.ActivityContestRecruitmentListBinding;
import java.util.Arrays;
import java.util.List;

public class ContestRecruitmentListActivity extends AppCompatActivity {

    private ActivityContestRecruitmentListBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContestRecruitmentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 임시 데이터 생성
        List<RecruitmentPost> dummyPosts = Arrays.asList(
                new RecruitmentPost(1, "배리어프리 앱 개발 콘테스트", "D-10", 2, 6, "hong_gil", "100만원", Arrays.asList("Android", "iOS", "UI/UX")),
                new RecruitmentPost(2, "정부 데이터 활용 해커톤", "D-5", 3, 4, "dev_master", "500만원", Arrays.asList("Web", "AI", "BigData"))
        );

        // 어댑터 설정
        TeamRecruitmentAdapter adapter = new TeamRecruitmentAdapter(dummyPosts);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 아이템 클릭 시 상세 화면으로 이동
        adapter.setOnItemClickListener(postId -> {
            Intent intent = new Intent(this, ContestRecruitmentDetailActivity.class);
            intent.putExtra("POST_ID", postId);
            startActivity(intent);
        });
    }
}
