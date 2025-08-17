package com.example.teamup.contest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.teamup.R;
import com.example.teamup.databinding.ActivityContestInformationDetailBinding;
import com.example.teamup.recruitment.RecruitmentPostActivity; // 모집글 작성 화면 import

public class ContestInformationDetailActivity extends AppCompatActivity {

    private ActivityContestInformationDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContestInformationDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int contestId = getIntent().getIntExtra("CONTEST_ID", -1);

        if (contestId != -1) {
            // 1. ID에 맞는 임시 상세 데이터 불러오기
            ContestInformationDetail contestDetail = loadContestDetails(contestId);
            // 2. 불러온 데이터로 UI 업데이트
            updateUi(contestDetail);
        } else {
            Toast.makeText(this, "공모전 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 뒤로가기 버튼
        binding.btnBackTitle.setOnClickListener(v -> finish());
    }

    /**
     * 불러온 상세 데이터로 화면의 각 View를 채우는 메서드
     */
    private void updateUi(ContestInformationDetail contestDetail) {
        // 뒤로가기 버튼 텍스트 설정
        binding.btnBackTitle.setText("◀ " + contestDetail.getTitle());
        // 포스터 이미지 설정
        binding.imgPoster.setImageResource(contestDetail.getPosterResourceId());
        // 마감일 텍스트 설정
        binding.tvDeadline.setText(contestDetail.getDeadlineText());
        // 해시태그 설정
        binding.tvHashtag.setText(contestDetail.getHashtags());

        // --- 버튼 기능 구현 ---

        // '사이트 바로가기' 버튼 클릭 리스너
        binding.btnGotoSite.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contestDetail.getSiteUrl()));
            startActivity(browserIntent);
        });

        // '이 공모전으로 팀 꾸리기' 버튼 클릭 리스너
        binding.btnApply.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecruitmentPostActivity.class);
            // 모집글 작성 화면에 공모전 이름을 전달
            intent.putExtra("CONTEST_NAME", contestDetail.getTitle());
            startActivity(intent);
        });
    }

    /**
     * contestId에 따라 임시 상세 데이터를 생성하는 메서드
     * (나중에는 이 부분이 서버와 통신하는 코드로 대체됩니다)
     */
    private ContestInformationDetail loadContestDetails(int contestId) {
        // 테스트를 위해 ID에 따라 다른 데이터를 반환
        if (contestId == 1) {
            return new ContestInformationDetail(
                    "배리어프리 앱 개발 콘테스트",
                    R.drawable.poster_sample1,
                    "모집마감 D-13",
                    "#앱 #배리어프리",
                    "https://www.barrierfree.or.kr/" // 예시 URL
            );
        } else { // contestId == 2 또는 기타
            return new ContestInformationDetail(
                    "정부 데이터 활용 해커톤",
                    R.drawable.poster_sample2,
                    "모집마감 D-25",
                    "#데이터 #AI #정부",
                    "https://www.data.go.kr/" // 예시 URL
            );
        }
    }
}