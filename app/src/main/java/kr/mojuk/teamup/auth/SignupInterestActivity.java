package kr.mojuk.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.model.StepResponse;
import kr.mojuk.teamup.fragments.MypageInterestFragment;

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
            java.util.List<Integer> selectedRoleIds = interestFragment.getSelectedRoleIds();
            
            // 디버깅을 위한 로그 추가
            Log.d(TAG, "=== API 요청 데이터 ===");
            Log.d(TAG, "selectedSkillIds: " + selectedSkillIds.toString());
            Log.d(TAG, "selectedRoleIds: " + selectedRoleIds.toString());
            Log.d(TAG, "========================");
            
            // 유효성 검사
            if (selectedSkillIds.isEmpty()) {
                Toast.makeText(this, "기술은 1개 이상 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedRoleIds.isEmpty()) {
                Toast.makeText(this, "역할은 1개 이상 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 선택된 데이터를 RegistrationManager에 저장
            registrationManager.saveSkills(interestFragment.getSelectedSkills());
            registrationManager.saveRoles(interestFragment.getSelectedRoles());
            
            // 2단계 완료 API 호출
            registrationManager.completeStep2(userId, selectedSkillIds, new ArrayList<>(), selectedRoleIds, new ArrayList<>(), new RegistrationManager.StepCallback() {
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
