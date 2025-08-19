package com.example.teamup.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.teamup.MainActivity;
import com.example.teamup.MypageActivity;
import com.example.teamup.R;
import com.example.teamup.auth.ProfileManager;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.api.model.Experience;
import com.example.teamup.api.model.UserRolesResponse;
import com.example.teamup.api.model.UserSkillsResponse;

import java.util.List;

public class MypageProfileActivity extends AppCompatActivity {

    private static final String TAG = "MypageProfileActivity";

    private BottomNavigationView bottomNavigationView;
    private TextView tvBackArrow;
    private LinearLayout llUserId;
    private LinearLayout llLanguagesAndRoles;
    private LinearLayout llTeamTendency;
    
    // 프로필 정보 표시용 TextView들
    private TextView tvUserId, tvUserSkills, tvUserRoles, tvUserExperiences;
    
    // Manager 인스턴스들
    private TokenManager tokenManager;
    private ProfileManager profileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_profile);

        // Manager 초기화
        tokenManager = TokenManager.getInstance(this);
        profileManager = ProfileManager.getInstance(this);

        // 로그인 상태 확인
        if (!tokenManager.isLoggedIn()) {
            Log.d(TAG, "로그인되지 않은 상태입니다. MainActivity로 이동합니다.");
            Intent intent = new Intent(MypageProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initViews();
        setClickListeners();
        setupBottomNavigation();
        
        // 프로필 정보 로드
        loadProfileInfo();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        tvBackArrow = findViewById(R.id.tv_back_arrow);
        llUserId = findViewById(R.id.ll_user_id);
        llLanguagesAndRoles = findViewById(R.id.ll_languages_and_roles);
        llTeamTendency = findViewById(R.id.ll_team_tendency);
        
        // 프로필 정보 표시용 TextView들
        tvUserId = findViewById(R.id.tv_user_id);
        tvUserSkills = findViewById(R.id.tv_user_skills);
        tvUserRoles = findViewById(R.id.tv_user_roles);
        tvUserExperiences = findViewById(R.id.tv_user_experiences);
    }

    /**
     * 프로필 정보 로드
     */
    private void loadProfileInfo() {
        // 사용자 ID 표시
        String userId = tokenManager.getUserId();
        if (tvUserId != null && userId != null) {
            tvUserId.setText("사용자 ID: " + userId);
        }
        
        // 스킬 정보 로드
        loadUserSkills();
        
        // 역할 정보 로드
        loadUserRoles();
        
        // 경험 정보 로드
        loadUserExperiences();
    }

    /**
     * 사용자 스킬 정보 로드
     */
    private void loadUserSkills() {
        profileManager.getUserSkills(this, new ProfileManager.UserSkillsCallback() {
            @Override
            public void onSuccess(UserSkillsResponse skills) {
                runOnUiThread(() -> {
                    if (tvUserSkills != null) {
                        StringBuilder skillText = new StringBuilder("스킬: ");
                        if (skills.getSkillIds().isEmpty() && skills.getCustomSkills().isEmpty()) {
                            skillText.append("등록된 스킬이 없습니다");
                        } else {
                            // 기존 스킬과 커스텀 스킬을 모두 표시
                            if (!skills.getSkillIds().isEmpty()) {
                                skillText.append("기존 스킬 ").append(skills.getSkillIds().size()).append("개");
                            }
                            if (!skills.getCustomSkills().isEmpty()) {
                                if (!skills.getSkillIds().isEmpty()) {
                                    skillText.append(", ");
                                }
                                skillText.append("커스텀 스킬 ").append(skills.getCustomSkills().size()).append("개");
                            }
                        }
                        tvUserSkills.setText(skillText.toString());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e(TAG, "스킬 정보 로드 실패: " + errorMessage);
                    if (tvUserSkills != null) {
                        tvUserSkills.setText("스킬: 로드 실패");
                    }
                });
            }
        });
    }

    /**
     * 사용자 역할 정보 로드
     */
    private void loadUserRoles() {
        profileManager.getUserRoles(this, new ProfileManager.UserRolesCallback() {
            @Override
            public void onSuccess(UserRolesResponse roles) {
                runOnUiThread(() -> {
                    if (tvUserRoles != null) {
                        StringBuilder roleText = new StringBuilder("역할: ");
                        if (roles.getRoleIds().isEmpty() && roles.getCustomRoles().isEmpty()) {
                            roleText.append("등록된 역할이 없습니다");
                        } else {
                            // 기존 역할과 커스텀 역할을 모두 표시
                            if (!roles.getRoleIds().isEmpty()) {
                                roleText.append("기존 역할 ").append(roles.getRoleIds().size()).append("개");
                            }
                            if (!roles.getCustomRoles().isEmpty()) {
                                if (!roles.getRoleIds().isEmpty()) {
                                    roleText.append(", ");
                                }
                                roleText.append("커스텀 역할 ").append(roles.getCustomRoles().size()).append("개");
                            }
                        }
                        tvUserRoles.setText(roleText.toString());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e(TAG, "역할 정보 로드 실패: " + errorMessage);
                    if (tvUserRoles != null) {
                        tvUserRoles.setText("역할: 로드 실패");
                    }
                });
            }
        });
    }

    /**
     * 사용자 경험 정보 로드
     */
    private void loadUserExperiences() {
        profileManager.getUserExperiences(this, new ProfileManager.UserExperiencesCallback() {
            @Override
            public void onSuccess(List<Experience> experiences) {
                runOnUiThread(() -> {
                    if (tvUserExperiences != null) {
                        if (experiences.isEmpty()) {
                            tvUserExperiences.setText("경험: 등록된 경험이 없습니다");
                        } else {
                            tvUserExperiences.setText("경험: " + experiences.size() + "개의 공모전 수상 경험");
                        }
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e(TAG, "경험 정보 로드 실패: " + errorMessage);
                    if (tvUserExperiences != null) {
                        tvUserExperiences.setText("경험: 로드 실패");
                    }
                });
            }
        });
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
                // 사용자 정보 수정 화면으로 이동 (나중에 구현)
                Toast.makeText(MypageProfileActivity.this, "사용자 정보 수정 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        llLanguagesAndRoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 스킬/역할 수정 화면으로 이동
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

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 활성화될 때 프로필 정보 새로고침
        if (tokenManager.isLoggedIn()) {
            loadProfileInfo();
        }
    }
}
