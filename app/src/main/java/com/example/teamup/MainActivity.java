package com.example.teamup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.teamup.auth.TokenManager;
import com.example.teamup.contest.ContestListFragment;
import com.example.teamup.recruitment.ContestRecruitmentListFragment;
import com.example.teamup.util.PlaceholderFragment; // 임시 프래그먼트 import
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
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        // 기본 화면 설정 (임시 홈 화면)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, PlaceholderFragment.newInstance("홈"))
                    .commit();
        }

        // 하단 탭 선택 리스너 설정
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.navigation_profile) {
                // 마이페이지는 임시 프래그먼트로 처리
                selectedFragment = PlaceholderFragment.newInstance("마이페이지");
            } else if (itemId == R.id.navigation_home) {
                // 홈은 임시 프래그먼트로 처리
                selectedFragment = PlaceholderFragment.newInstance("홈");
            } else if (itemId == R.id.navigation_contest) {
                // 공모전은 ContestListFragment로 처리
                selectedFragment = new ContestListFragment();
            } else if (itemId == R.id.navigation_board) {
                // 게시판은 ContestRecruitmentListFragment로 처리
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