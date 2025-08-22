package kr.mojuk.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.model.Experience;
import kr.mojuk.teamup.api.model.StepResponse;
import kr.mojuk.teamup.fragments.ExperienceFragment;

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
        
        // 초기에는 Skip 버튼으로 설정
        btnNext.setText("Skip");
    }

    // 이 메서드는 ExperienceFragment에서 처리하므로 제거

    // 이 메서드는 ExperienceFragment에서 처리하므로 제거
    
    // 이 메서드들은 ExperienceFragment에서 처리하므로 제거
    
    private void handleNextButtonClick() {
        // ExperienceFragment에서 선택된 경험들 확인
        List<Experience> selectedExperiences = experienceFragment.getSelectedExperiences();
        
        // 모든 form을 검사하여 내용이 있는지 확인
        boolean hasAnyContent = experienceFragment.hasAnyFormContent();
        
        if (hasAnyContent) {
            // form에 내용이 있으면 유효성 검사 후 API 호출
            if (validateExperiences(selectedExperiences)) {
                proceedToNextStep();
            }
        } else {
            // form에 내용이 없으면 Skip
            goToFinishStep();
        }
    }
    
    /**
     * 경험 데이터 유효성 검사
     */
    private boolean validateExperiences(List<Experience> experiences) {
        // 모든 form을 검사하여 부분적으로 입력된 form이 있는지 확인
        List<View> formViews = experienceFragment.getExperienceFormViews();
        
        for (int i = 0; i < formViews.size(); i++) {
            View formView = formViews.get(i);
            EditText etContestName = formView.findViewById(R.id.et_contest_name_additional);
            Spinner spinnerCategory = formView.findViewById(R.id.spinner_category);
            EditText etDate = formView.findViewById(R.id.et_date_additional);
            EditText etDescription = formView.findViewById(R.id.et_description_additional);
            
            String contestName = etContestName.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String date = etDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            // 하나라도 내용이 있으면 모든 필수 필드 검사
            if (!contestName.isEmpty() || 
                !category.equals("카테고리 선택") || 
                !date.isEmpty() || 
                !description.isEmpty()) {
                
                // 공모전명 필수
                if (contestName.isEmpty()) {
                    Toast.makeText(this, "공모전명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                
                // 카테고리 필수
                if (category.equals("카테고리 선택")) {
                    Toast.makeText(this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                
                // 날짜 필수
                if (date.isEmpty()) {
                    Toast.makeText(this, "날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                
                // 설명 필수
                if (description.isEmpty()) {
                    Toast.makeText(this, "설명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        
        return true;
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
    
    @Override
    public void onFormContentChanged(boolean hasContent) {
        runOnUiThread(() -> {
            if (hasContent) {
                // form에 내용이 있으면 Next 버튼으로 변경
                btnNext.setText("Next");
            } else {
                // form에 내용이 없으면 Skip 버튼으로 변경
                btnNext.setText("Skip");
            }
        });
    }
}
