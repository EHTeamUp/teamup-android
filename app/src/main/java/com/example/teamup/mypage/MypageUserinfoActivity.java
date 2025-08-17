package com.example.teamup.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.teamup.MainActivity;
import com.example.teamup.MypageActivity;
import com.example.teamup.R;

public class MypageUserinfoActivity extends AppCompatActivity {

    private TextView tvHeaderTitle, tvUserId, tvUserEmail, tvUserName, tvMypageBackArrow, btnEdit;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout cvNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_userinfo);

        initViews();
        setClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvUserId = findViewById(R.id.tv_mypage_id_value);
        tvUserEmail = findViewById(R.id.tv_mypage_email_value);
        tvUserName = findViewById(R.id.tv_mypage_name_value);
        tvMypageBackArrow = findViewById(R.id.tv_mypage_back_arrow);
        btnEdit = findViewById(R.id.btn_mypage_edit);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        cvNavigation = findViewById(R.id.cv_navigation);
    }

    private void setClickListeners() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageUserinfoActivity.this, MypageUserinfoEditActivity.class);
                startActivity(intent);
            }
        });

        tvMypageBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // MypageActivity로 돌아가기
            }
        });

        cvNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageUserinfoActivity.this, MypageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupBottomNavigation() {
        // 마이페이지 탭을 선택된 상태로 설정
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        
        // 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(MypageUserinfoActivity.this, MainActivity.class);
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
                Intent intent = new Intent(MypageUserinfoActivity.this, MypageActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
