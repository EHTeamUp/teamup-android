package com.example.teamup.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.example.teamup.personality.PersonalityTestResultActivity;

public class MypageTestIngActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout cvNavigation;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_personality_test_question);

        initViews();
        setClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        cvNavigation = findViewById(R.id.cv_navigation);
        btnSubmit = findViewById(R.id.btn_result);
    }

    private void setClickListeners() {
        cvNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageTestIngActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 성향 테스트 결과 화면으로 이동
                Intent intent = new Intent(MypageTestIngActivity.this, PersonalityTestResultActivity.class);
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
                Intent intent = new Intent(MypageTestIngActivity.this, MainActivity.class);
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
                Intent intent = new Intent(MypageTestIngActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
