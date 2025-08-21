package com.example.teamup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.teamup.auth.LoginActivity;
import com.example.teamup.auth.LoginManager;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.contest.ContestListActivity;
import com.example.teamup.recruitment.ContestRecruitmentListActivity;
import com.example.teamup.fragments.HomeFragment;
import com.example.teamup.fragments.MypageFragment;
import com.example.teamup.fragments.ExperienceFragment;
import com.example.teamup.fragments.MypageProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements ExperienceFragment.ExperienceFragmentListener {

    private static final String TAG = "MainActivity";
    private TokenManager tokenManager;

    // Fragment 태그들
    private static final String FRAGMENT_HOME = "home";
    private static final String FRAGMENT_MYPAGE = "mypage";

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

        // TokenManager 초기화
        tokenManager = TokenManager.getInstance(this);

        // 로그인 상태 확인
        checkLoginStatus();

        // Setup Bottom Navigation
        setupBottomNavigation();

        // 기본적으로 홈 Fragment 표시
        showHomeFragment();
    }

    /**
     * 로그인 상태 확인 및 처리
     */
    private void checkLoginStatus() {
        if (!tokenManager.isLoggedIn()) {
            Log.d(TAG, "로그인되지 않은 상태입니다. LoginActivity로 이동합니다.");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        Log.d(TAG, "로그인된 상태입니다. 사용자 ID: " + tokenManager.getUserId());
    }

    /**
     * 하단 네비게이션 설정
     */
    private void setupBottomNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.navigation_home) {
                showHomeFragment();
                return true;
            } else if (itemId == R.id.navigation_contest) {
                // 공모전 목록 Activity로 이동
                Intent intent = new Intent(MainActivity.this, ContestListActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_board) {
                // 팀원 모집 게시판 Activity로 이동
                Intent intent = new Intent(MainActivity.this, ContestRecruitmentListActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // 로그인 상태에 따라 다르게 동작
                if (tokenManager.isLoggedIn()) {
                    showMypageFragment();
                } else {
                    // 로그인되지 않은 경우 로그인 페이지로 이동
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });
    }

    /**
     * 홈 Fragment 표시
     */
    private void showHomeFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_HOME);
        if (fragment == null) {
            fragment = new HomeFragment();
        }
        replaceFragment(fragment, FRAGMENT_HOME);
    }

    /**
     * 마이페이지 Fragment 표시
     */
    private void showMypageFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_MYPAGE);
        if (fragment == null) {
            fragment = new MypageFragment();
        }
        replaceFragment(fragment, FRAGMENT_MYPAGE);
    }

    /**
     * Fragment 교체 메서드
     */
    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // 기존 Fragment들을 모두 숨기고 새로운 Fragment 추가
        transaction.replace(R.id.content_container, fragment, tag);
        
        transaction.commit();
        
        Log.d(TAG, "Fragment 교체: " + tag);
    }

    /**
     * 외부에서 Fragment를 표시할 수 있는 public 메서드
     */
    public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // 기존 Fragment들을 모두 숨기고 새로운 Fragment 추가
        transaction.replace(R.id.content_container, fragment);
        
        // 백 스택에 추가 (뒤로가기 버튼으로 이전 Fragment로 돌아갈 수 있도록)
        transaction.addToBackStack(null);
        
        transaction.commit();
        
        Log.d(TAG, "Fragment 표시: " + fragment.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 앱이 다시 활성화될 때 로그인 상태 재확인
        if (!tokenManager.isLoggedIn()) {
            Log.d(TAG, "로그인 상태가 변경되었습니다. LoginActivity로 이동합니다.");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    
    // ===== ExperienceFragmentListener 구현 =====
    
    @Override
    public void onBackPressed() {
        // 프래그먼트 백 스택에 항목이 있는지 확인
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // 백 스택의 최상위 프래그먼트를 팝 (이전 프래그먼트로 돌아감)
            getSupportFragmentManager().popBackStack();
        } else {
            // 백 스택이 비어있으면, 액티비티의 기본 뒤로가기 동작을 수행 (앱 종료 또는 이전 액티비티로 이동)
            super.onBackPressed();
        }
    }
    
    @Override
    public void onExperienceUpdated() {
        // 경험 정보가 업데이트되면 MypageProfileFragment로 돌아가기
        showMypageProfileFragment();
    }
    
    @Override
    public void onFormContentChanged(boolean hasContent) {
        // MainActivity에서는 사용하지 않음 (마이페이지 모드에서만 사용)
        // 회원가입 모드에서만 사용되는 기능
    }
    
    /**
     * MypageProfileFragment 표시
     */
    public void showMypageProfileFragment() {
        Fragment fragment = new MypageProfileFragment();
        showFragment(fragment);
    }
}