package kr.mojuk.teamup;

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

import kr.mojuk.teamup.auth.LoginActivity;
import kr.mojuk.teamup.auth.TokenManager;

import kr.mojuk.teamup.contest.ContestListFragment;
import kr.mojuk.teamup.contest.ContestInformationDetailFragment;
import kr.mojuk.teamup.recruitment.ContestRecruitmentListFragment;
import kr.mojuk.teamup.recruitment.ContestRecruitmentDetailFragment;
import kr.mojuk.teamup.fragments.HomeFragment;
import kr.mojuk.teamup.fragments.MypageFragment;
import kr.mojuk.teamup.fragments.ExperienceFragment;
import kr.mojuk.teamup.fragments.MypageProfileFragment;
import kr.mojuk.teamup.applicant.ApplicantListFragment;
import kr.mojuk.teamup.recruitment.TeamSynergyScoreFragment;
import kr.mojuk.teamup.notification.FcmTokenManager;
import kr.mojuk.teamup.notification.NotificationPermissionHelper;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ExperienceFragment.ExperienceFragmentListener {

    private static final String TAG = "MainActivity";
    private TokenManager tokenManager;

    // Fragment 태그들
    private static final String FRAGMENT_HOME = "home";
    private static final String FRAGMENT_MYPAGE = "mypage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 로그인 상태 확인
        tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isLoggedIn()) {
            // 로그인되지 않은 경우 LoginActivity로 이동
            Log.d(TAG, "로그인되지 않은 상태, LoginActivity로 이동");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        // 로그인된 경우 MainActivity 계속 진행
        Log.d(TAG, "로그인된 상태, MainActivity 계속 진행");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
      
        // FCM 토큰 매니저 초기화 및 토큰 상태 확인
        FcmTokenManager fcmTokenManager = FcmTokenManager.getInstance(this);
        fcmTokenManager.initializeTokenOnAppStart();

        // 알림 권한 요청
        NotificationPermissionHelper.requestNotificationPermission(this);

        // Setup Bottom Navigation
        setupBottomNavigation(savedInstanceState);
    }

    /**
     * 하단 네비게이션 설정
     */
    private void setupBottomNavigation(Bundle savedInstanceState) {
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
            
            // 현재 Fragment 확인
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            
            // DetailFragment가 현재 표시되어 있으면 네비게이션 무시
            if (currentFragment instanceof ContestInformationDetailFragment ||
                currentFragment instanceof ContestRecruitmentDetailFragment) {
                Log.d(TAG, "DetailFragment가 표시 중이므로 네비게이션 무시: " + currentFragment.getClass().getSimpleName());
                return false;
            }
            
            Fragment selectedFragment = null;

            if (itemId == R.id.navigation_profile) {
                // 마이페이지는 MypageFragment로 처리
                selectedFragment = new MypageFragment();
            } else if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_contest) {
                selectedFragment = new ContestListFragment();
            } else if (itemId == R.id.navigation_board) {
                selectedFragment = new ContestRecruitmentListFragment();
            }

            if (selectedFragment != null) {
                Log.d(TAG, "네비게이션 Fragment 전환: " + selectedFragment.getClass().getSimpleName());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
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
        transaction.replace(R.id.fragment_container, fragment, tag);

        transaction.commit();

        Log.d(TAG, "Fragment 교체: " + tag);
    }

    /**
     * 외부에서 Fragment를 표시할 수 있는 public 메서드
     */
    public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Log.d(TAG, "showFragment 시작 - Fragment: " + fragment.getClass().getSimpleName());
        Log.d(TAG, "현재 FragmentManager: " + fragmentManager.getClass().getSimpleName());

        // 기존 Fragment들을 모두 숨기기
        List<Fragment> fragments = fragmentManager.getFragments();
        Log.d(TAG, "현재 Fragment 개수: " + fragments.size());
        for (Fragment existingFragment : fragments) {
            if (existingFragment != null && existingFragment.isVisible()) {
                transaction.hide(existingFragment);
                Log.d(TAG, "기존 Fragment 숨김: " + existingFragment.getClass().getSimpleName());
            }
        }

        // 새로운 Fragment 추가
        Log.d(TAG, "새 Fragment 추가: " + fragment.getClass().getSimpleName() + " to container: " + R.id.fragment_container);
        transaction.add(R.id.fragment_container, fragment, "DetailFragment");
        transaction.show(fragment);

        // 백 스택에 추가 (뒤로가기 버튼으로 이전 Fragment로 돌아갈 수 있도록)
        transaction.addToBackStack(null);

        transaction.commit();

        Log.d(TAG, "Fragment 표시 완료: " + fragment.getClass().getSimpleName());
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
        // 폼 내용 변경 감지 - MainActivity에서는 특별한 처리가 필요하지 않음
        Log.d(TAG, "폼 내용 변경 감지: " + hasContent);
    }

    /**
     * MypageProfileFragment 표시
     */
    public void showMypageProfileFragment() {
        Fragment fragment = new MypageProfileFragment();
        showFragment(fragment);
    }

    /**
     * 하단 네비게이션 바 활성화
     */
    public void setBottomNavigationItem(int itemId) {
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setSelectedItemId(itemId);
    }
}