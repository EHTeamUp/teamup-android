package com.example.teamup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.teamup.auth.LoginActivity;
import com.example.teamup.auth.LoginManager;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.mypage.MypageUserinfoActivity;
import com.example.teamup.R;
import com.example.teamup.mypage.MypageProfileActivity;
import com.example.teamup.notification.FcmTokenManager;


public class MypageActivity extends AppCompatActivity {

    //현재 임시로 로그인 가정하고 마이페이지 띄우지만 나중에 로그인 여부에 따라 띄우도록 수정 필요

    // UI 컴포넌트들
    private LinearLayout llMemberInfo, llProfile, llContestList, llLogout;
    private TextView tvHeaderTitle;
    private BottomNavigationView bottomNavigationView;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // TokenManager 초기화
        tokenManager = TokenManager.getInstance(this);

        // 뷰 초기화
        initViews();
        
        // 클릭 리스너 설정
        setClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        llMemberInfo = findViewById(R.id.ll_member_info);
        llProfile = findViewById(R.id.ll_profile);
        llContestList = findViewById(R.id.ll_contest_list);
        llLogout = findViewById(R.id.ll_logout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    /**
     * 각 메뉴 아이템의 클릭 이벤트 리스너를 설정
     */
    private void setClickListeners() {
        // 회원정보 메뉴
        llMemberInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, MypageUserinfoActivity.class);
                startActivity(intent);
            }
        });

        // 프로필 메뉴
        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, MypageProfileActivity.class);
                startActivity(intent);
            }
        });

        // 참여 공모전 목록 메뉴
        llContestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 공모전 목록 화면으로 이동
            }
        });

        // 로그아웃 메뉴
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmDialog();
            }
        });
    }

    /**
     * 로그아웃 확인 다이얼로그 표시
     */
    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    /**
     * 로그아웃 수행
     */
    private void performLogout() {
        // 토큰 삭제
        tokenManager.clearToken();
        
        // 로그인 상태 업데이트
        LoginManager.setLoggedIn(false);
        
        // 로그아웃 메시지 표시
        Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
        
        // 로그인 화면으로 이동
        Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        // 마이페이지 탭을 선택된 상태로 설정
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        
        // 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(MypageActivity.this, MainActivity.class);
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
                // 이미 마이페이지에 있으므로 아무것도 하지 않음
                return true;
            }
            return false;
        });
    }
}
