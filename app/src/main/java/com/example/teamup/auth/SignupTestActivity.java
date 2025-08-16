package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;

import java.util.ArrayList;

public class SignupTestActivity extends AppCompatActivity {

    private static final int STEP_START = 0;
    private static final int STEP_ING = 1;
    private static final int STEP_RESULT = 2;

    private int currentStep = STEP_START;

    // 이전 액티비티에서 전달받은 데이터
    private String userId, userPassword, userName, userEmail;
    private String[] languages, roles;

    // UI 컴포넌트들
    private TextView tvTitle, tvDescription, btnSkip, btnNext, tvPrevious;
    private Button btnStart, btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("SignupTestActivity", "onCreate 시작");

        receiveDataFromPreviousActivity();
        showStep(STEP_START);

        android.util.Log.d("SignupTestActivity", "onCreate 완료");
    }

    private void receiveDataFromPreviousActivity() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        userPassword = intent.getStringExtra("password");
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");

        ArrayList<String> languagesList = intent.getStringArrayListExtra("languages");
        ArrayList<String> rolesList = intent.getStringArrayListExtra("roles");

        languages = (languagesList != null) ? languagesList.toArray(new String[0]) : new String[0];
        roles = (rolesList != null) ? rolesList.toArray(new String[0]) : new String[0];
    }

    private void showStep(int step) {
        currentStep = step;
        android.util.Log.d("SignupTestActivity", "showStep: step=" + step);

        switch (step) {
            case STEP_START:
                setContentView(R.layout.activity_signup_test_start);
                initStartStep();
                break;
            case STEP_ING:
                setContentView(R.layout.activity_signup_test_ing);
                initIngStep();
                break;
            case STEP_RESULT:
                setContentView(R.layout.activity_signup_test_result);
                initResultStep();
                break;
            default:
                setContentView(R.layout.activity_signup_test_start);
                initStartStep();
                break;
        }
    }

    private void initStartStep() {
        try {
            android.util.Log.d("SignupTestActivity", "initStartStep 시작");
            
            tvTitle = findViewById(R.id.tv_test_title);
            tvDescription = findViewById(R.id.tv_app_title);
            btnStart = findViewById(R.id.btn_test_start);
            btnSkip = findViewById(R.id.tv_next);
            tvPrevious = findViewById(R.id.tv_previous);
            
            if (tvTitle == null || tvDescription == null || btnStart == null || btnSkip == null || tvPrevious == null) {
                android.util.Log.e("SignupTestActivity", "필요한 뷰를 찾을 수 없습니다");
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Previous 버튼 처리
            tvPrevious.setClickable(true);
            tvPrevious.setFocusable(true);
            tvPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "Previous 버튼 클릭됨");
                    finish(); // 이전 액티비티로 돌아가기
                }
            });
            
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "시작하기 버튼 클릭됨");
                    showStep(STEP_ING);
                }
            });
            
            // btnSkip은 TextView이므로 클릭 가능하도록 설정
            btnSkip.setClickable(true);
            btnSkip.setFocusable(true);
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "스킵 버튼 클릭됨");
                    // 스킵 시 activity_signup_experience로 이동
                    Intent intent = new Intent(SignupTestActivity.this, SignupExperienceActivity.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("password", userPassword);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("languages", languages);
                    intent.putExtra("roles", roles);
                    startActivity(intent);
                    finish();
                }
            });
            
            android.util.Log.d("SignupTestActivity", "initStartStep 완료");
        } catch (Exception e) {
            android.util.Log.e("SignupTestActivity", "initStartStep 오류: " + e.getMessage());
            Toast.makeText(this, "초기화 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void initIngStep() {
        try {
            android.util.Log.d("SignupTestActivity", "initIngStep 시작");
            
            tvTitle = findViewById(R.id.tv_header_title);
            tvDescription = findViewById(R.id.tv_question1);
            btnNext = findViewById(R.id.tv_next);
            tvPrevious = findViewById(R.id.tv_previous);
            btnSubmit = findViewById(R.id.btn_submit);
            
            if (tvTitle == null || tvDescription == null || btnNext == null || tvPrevious == null || btnSubmit == null) {
                android.util.Log.e("SignupTestActivity", "initIngStep: 필요한 뷰를 찾을 수 없습니다");
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Previous 버튼 처리
            tvPrevious.setClickable(true);
            tvPrevious.setFocusable(true);
            tvPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "Previous 버튼 클릭됨 (STEP_ING)");
                    showStep(STEP_START); // 이전 단계로 돌아가기
                }
            });
            
            // 제출하기 버튼 처리
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "제출하기 버튼 클릭됨");
                    
                    // 모든 질문에 답변했는지 확인
                    if (validateTestAnswers()) {
                        showStep(STEP_RESULT);
                    } else {
                        Toast.makeText(SignupTestActivity.this, "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            
            // btnNext는 TextView이므로 클릭 가능하도록 설정
            btnNext.setClickable(true);
            btnNext.setFocusable(true);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "다음 버튼 클릭됨 (STEP_ING)");
                    // STEP_ING에서 Next 버튼 클릭 시 SignupExperienceActivity로 바로 이동
                    Intent intent = new Intent(SignupTestActivity.this, SignupExperienceActivity.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("password", userPassword);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("languages", languages);
                    intent.putExtra("roles", roles);
                    startActivity(intent);
                    finish();
                }
            });
            
            android.util.Log.d("SignupTestActivity", "initIngStep 완료");
        } catch (Exception e) {
            android.util.Log.e("SignupTestActivity", "initIngStep 오류: " + e.getMessage());
            Toast.makeText(this, "초기화 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void initResultStep() {
        try {
            android.util.Log.d("SignupTestActivity", "initResultStep 시작");
            
            tvTitle = findViewById(R.id.tv_header_title);
            tvDescription = findViewById(R.id.tv_result_title);
            btnNext = findViewById(R.id.tv_next);
            tvPrevious = findViewById(R.id.tv_previous);
            
            if (tvTitle == null || tvDescription == null || btnNext == null || tvPrevious == null) {
                android.util.Log.e("SignupTestActivity", "initResultStep: 필요한 뷰를 찾을 수 없습니다");
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Previous 버튼 처리
            tvPrevious.setClickable(true);
            tvPrevious.setFocusable(true);
            tvPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "Previous 버튼 클릭됨 (STEP_RESULT)");
                    showStep(STEP_ING); // 이전 단계로 돌아가기
                }
            });
            
            // btnNext는 TextView이므로 클릭 가능하도록 설정
            btnNext.setClickable(true);
            btnNext.setFocusable(true);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "다음 버튼 클릭됨 (STEP_RESULT)");
                    // 결과 단계 완료 후 activity_signup_experience로 이동
                    Intent intent = new Intent(SignupTestActivity.this, SignupExperienceActivity.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("password", userPassword);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("languages", languages);
                    intent.putExtra("roles", roles);
                    startActivity(intent);
                    finish();
                }
            });
            
            android.util.Log.d("SignupTestActivity", "initResultStep 완료");
        } catch (Exception e) {
            android.util.Log.e("SignupTestActivity", "initResultStep 오류: " + e.getMessage());
            Toast.makeText(this, "초기화 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    /** 다음 액티비티 이동 공통 메서드 */
    private void goToExperience() {
        Intent intent = new Intent(this, SignupExperienceActivity.class);
        intent.putExtra("id", userId);
        intent.putExtra("password", userPassword);
        intent.putExtra("name", userName);
        intent.putExtra("email", userEmail);
        intent.putExtra("languages", languages);
        intent.putExtra("roles", roles);
        startActivity(intent);
        finish();
    }

    /** 뷰 null 체크 */
    private boolean checkViews(View... views) {
        for (View v : views) {
            if (v == null) {
                android.util.Log.e("SignupTestActivity", "뷰 초기화 실패");
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean validateTestAnswers() {
        try {
            // RadioGroup들을 찾기
            RadioGroup rgQuestion1 = findViewById(R.id.rg_question1);
            RadioGroup rgQuestion2 = findViewById(R.id.rg_question2);
            RadioGroup rgQuestion3 = findViewById(R.id.rg_question3);
            
            // 각 질문에 답변했는지 확인
            boolean question1Answered = rgQuestion1 != null && rgQuestion1.getCheckedRadioButtonId() != -1;
            boolean question2Answered = rgQuestion2 != null && rgQuestion2.getCheckedRadioButtonId() != -1;
            boolean question3Answered = rgQuestion3 != null && rgQuestion3.getCheckedRadioButtonId() != -1;
            
            android.util.Log.d("SignupTestActivity", "질문1 답변: " + question1Answered);
            android.util.Log.d("SignupTestActivity", "질문2 답변: " + question2Answered);
            android.util.Log.d("SignupTestActivity", "질문3 답변: " + question3Answered);
            
            return question1Answered && question2Answered && question3Answered;
        } catch (Exception e) {
            android.util.Log.e("SignupTestActivity", "validateTestAnswers 오류: " + e.getMessage());
            return false;
        }
    }
}
