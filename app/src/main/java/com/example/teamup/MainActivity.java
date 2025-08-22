package com.example.teamup;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.teamup.auth.TokenManager;

import com.example.teamup.contest.ContestListFragment;
import com.example.teamup.recruitment.ContestRecruitmentListFragment;
import com.example.teamup.fragment.HomeFragment;
import com.example.teamup.fragment.ProfileFragment;
import com.example.teamup.applicant.ApplicantListFragment;
import com.example.teamup.recruitment.TeamSynergyScoreFragment;
import com.example.teamup.notification.FcmTokenManager;
import com.example.teamup.notification.NotificationPermissionHelper;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenManager = TokenManager.getInstance(this);
      
        // FCM 토큰 매니저 초기화
        FcmTokenManager.getInstance(this);

        // 알림 권한 요청
        NotificationPermissionHelper.requestNotificationPermission(this);

        // Setup Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        // Intent에서 Fragment 로드 요청 확인
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("FRAGMENT_TO_LOAD")) {
            String fragmentToLoad = intent.getStringExtra("FRAGMENT_TO_LOAD");
            if ("ApplicantListFragment".equals(fragmentToLoad)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ApplicantListFragment())
                        .commit();
            } else if ("TeamSynergyScoreFragment".equals(fragmentToLoad)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TeamSynergyScoreFragment())
                        .commit();
            }
        } else {
            // 기본적으로 Home Fragment 표시
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        }
      
        // 하단 탭 선택 리스너 설정
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_contest) {
                selectedFragment = new ContestListFragment();
            } else if (itemId == R.id.navigation_board) {
                selectedFragment = new ContestRecruitmentListFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });
    }
}