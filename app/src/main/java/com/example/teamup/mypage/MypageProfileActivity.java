package com.example.teamup.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.teamup.MainActivity;
import com.example.teamup.MypageActivity;
import com.example.teamup.R;

public class MypageProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvBackArrow;
    private LinearLayout llUserId;
    private LinearLayout llLanguagesAndRoles;
    private LinearLayout llTeamTendency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_profile);

        initViews();
        setClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        tvBackArrow = findViewById(R.id.tv_back_arrow);
        llUserId = findViewById(R.id.ll_user_id);
        llLanguagesAndRoles = findViewById(R.id.ll_languages_and_roles);
        llTeamTendency = findViewById(R.id.ll_team_tendency);
    }

    private void setClickListeners() {
        tvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageProfileActivity.this, MypageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        llUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageProfileActivity.this, MypageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        llLanguagesAndRoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageProfileActivity.this, MypageInterestActivity.class);
                startActivity(intent);
            }
        });

        llTeamTendency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 성향 테스트 이력 확인
                if (hasPersonalityTestResult()) {
                    // 성향 테스트 결과가 있으면 결과 화면으로 이동
                    Intent intent = new Intent(MypageProfileActivity.this, MypageTestResultActivity.class);
                    startActivity(intent);
                } else {
                    // 성향 테스트 결과가 없으면 테스트 화면으로 이동
                    Intent intent = new Intent(MypageProfileActivity.this, MypageTestIngActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean hasPersonalityTestResult() {
        // TODO: 실제로는 데이터베이스나 SharedPreferences에서 사용자의 성향 테스트 결과 여부를 확인
        // 현재는 임시로 false를 반환 (테스트를 위해 필요시 true로 변경)
        return false;
    }

    private void setupBottomNavigation() {
        // 마이페이지 탭을 선택된 상태로 설정
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        
        // 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(MypageProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_contest) {
                // 공모전 화면으로 이동
                return true;
            } else if (itemId == R.id.navigation_board) {
                // 게시판 화면으로 이동
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // 마이페이지 메인으로 이동
                Intent intent = new Intent(MypageProfileActivity.this, MypageActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
