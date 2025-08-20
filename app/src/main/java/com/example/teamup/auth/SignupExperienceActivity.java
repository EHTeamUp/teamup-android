package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;
import com.example.teamup.api.model.Experience;
import com.example.teamup.api.model.RegistrationStep3;
import com.example.teamup.api.model.StepResponse;
import com.example.teamup.fragments.ExperienceFragment;

import java.util.ArrayList;
import java.util.List;

public class SignupExperienceActivity extends AppCompatActivity implements ExperienceFragment.ExperienceFragmentListener {
    
    private static final String TAG = "SignupExperienceActivity";

    private String userId;
    private RegistrationManager registrationManager;

    private TextView btnPrevious, btnNext;
    private ExperienceFragment experienceFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_experience);
        
        // userId 받기
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        registrationManager = RegistrationManager.getInstance();
        initViews();
        
        // ExperienceFragment 생성 (회원가입 모드)
        experienceFragment = ExperienceFragment.newInstance(false, userId);
        
        // Fragment 추가
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, experienceFragment)
                .commit();
    }

    private void initViews() {
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);

        btnPrevious.setOnClickListener(v -> goToPreviousStep());
        btnNext.setOnClickListener(v -> handleNextButtonClick());
    }

    // 이 메서드는 ExperienceFragment에서 처리하므로 제거

    // 이 메서드는 ExperienceFragment에서 처리하므로 제거
    
    // 이 메서드들은 ExperienceFragment에서 처리하므로 제거
    
    private void handleNextButtonClick() {
        // ExperienceFragment에서 선택된 경험들 확인
        List<Experience> selectedExperiences = experienceFragment.getSelectedExperiences();
        
        if (!selectedExperiences.isEmpty()) {
            // 경험이 있으면 API 호출
            proceedToNextStep();
        } else {
            // 경험이 없으면 Skip
            goToFinishStep();
        }
    }
    
    private void goToFinishStep() {
        Intent intent = new Intent(SignupExperienceActivity.this, SignupTestBaseActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void proceedToNextStep() {
        // ExperienceFragment에서 선택된 경험들 가져오기
        List<Experience> selectedExperiences = experienceFragment.getSelectedExperiences();
        
        // Step 3 등록 - API 호출
        if (selectedExperiences.isEmpty()) {
            // 경험이 없으면 바로 finish로
            goToFinishStep();
            return;
        }
        
        registrationManager.completeStep3(userId, selectedExperiences, new RegistrationManager.StepCallback() {
            @Override
            public void onSuccess(StepResponse response) {
                runOnUiThread(() -> {
                    Toast.makeText(SignupExperienceActivity.this, "경험 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    
                    // 다음 단계로 이동 (성향 테스트)
                    Intent intent = new Intent(SignupExperienceActivity.this, SignupTestBaseActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(SignupExperienceActivity.this, "경험 등록 중 오류가 발생했습니다: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void goToPreviousStep() {
        finish(); // 이전 Activity로 돌아가기
    }
    
    // ===== ExperienceFragmentListener 구현 =====
    
    @Override
    public void onBackPressed() {
        // 이전 단계로 돌아가기
        goToPreviousStep();
        super.onBackPressed();
    }
    
    @Override
    public void onExperienceUpdated() {
        // 회원가입에서는 사용하지 않음
    }
}
