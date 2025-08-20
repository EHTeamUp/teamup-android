package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.teamup.R;
import com.example.teamup.api.model.StepResponse;
import com.example.teamup.fragments.MypageInterestFragment;

public class SignupInterestActivity extends AppCompatActivity {
    private static final String TAG = "SignupInterestActivity";
    
    private String userId;
    private RegistrationManager registrationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_interest);
        
        userId = getIntent().getStringExtra("userId");
        registrationManager = RegistrationManager.getInstance();
        
        // Fragment 로드
        loadFragment();
        
        // 버튼 설정
        TextView btnPrevious = findViewById(R.id.btn_previous);
        TextView btnNext = findViewById(R.id.btn_next);
        
        btnPrevious.setOnClickListener(v -> goToPreviousStep());
        btnNext.setOnClickListener(v -> proceedToNextStep());
    }
    
    private void loadFragment() {
        MypageInterestFragment fragment = MypageInterestFragment.newInstance("signup", userId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void proceedToNextStep() {
        // Fragment에서 선택된 데이터 가져오기
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof MypageInterestFragment) {
            MypageInterestFragment interestFragment = (MypageInterestFragment) currentFragment;
            
            // 선택된 기술과 역할 가져오기
            java.util.List<Integer> selectedSkillIds = interestFragment.getSelectedSkillIds();
            java.util.List<String> selectedCustomSkills = interestFragment.getSelectedCustomSkills();
            java.util.List<Integer> selectedRoleIds = interestFragment.getSelectedRoleIds();
            java.util.List<String> selectedCustomRoles = interestFragment.getSelectedCustomRoles();
            
            // 디버깅을 위한 로그 추가
            Log.d(TAG, "=== API 요청 데이터 ===");
            Log.d(TAG, "userId: " + userId);
            Log.d(TAG, "selectedSkillIds: " + selectedSkillIds.toString());
            Log.d(TAG, "selectedCustomSkills: " + selectedCustomSkills.toString());
            Log.d(TAG, "selectedRoleIds: " + selectedRoleIds.toString());
            Log.d(TAG, "selectedCustomRoles: " + selectedCustomRoles.toString());
            Log.d(TAG, "========================");
            
            // 유효성 검사
            if (selectedSkillIds.isEmpty() && selectedCustomSkills.isEmpty()) {
                Toast.makeText(this, "기술과 역할은 1개 이상 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedRoleIds.isEmpty() && selectedCustomRoles.isEmpty()) {
                Toast.makeText(this, "기술과 역할은 1개 이상 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 2단계 완료 API 호출
            registrationManager.completeStep2(userId, selectedSkillIds, selectedCustomSkills, selectedRoleIds, selectedCustomRoles, new RegistrationManager.StepCallback() {
                @Override
                public void onSuccess(StepResponse response) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignupInterestActivity.this, "관심사 등록이 완료되었습니다", Toast.LENGTH_SHORT).show();
                        
                        // 다음 단계로 이동 (경험 입력)
                        Intent intent = new Intent(SignupInterestActivity.this, SignupExperienceActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignupInterestActivity.this, "관심사 등록에 실패했습니다: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            Toast.makeText(this, "기술과 역할은 1개 이상 선택해야 합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToPreviousStep() {
        finish(); // 이전 Activity로 돌아가기
    }
}
