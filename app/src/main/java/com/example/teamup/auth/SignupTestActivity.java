package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private TextView tvTitle, tvDescription, btnSkip, btnNext;
    private Button btnStart;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 로그 추가
        android.util.Log.d("SignupTestActivity", "onCreate 시작");
        
        // 이전 액티비티에서 데이터 받기
        receiveDataFromPreviousActivity();
        
        // 첫 번째 단계 표시
        showStep(STEP_START);
        
        android.util.Log.d("SignupTestActivity", "onCreate 완료");
    }
    
    private void receiveDataFromPreviousActivity() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        userPassword = intent.getStringExtra("password");
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");
        
        // ArrayList로 전달된 데이터를 String 배열로 변환
        ArrayList<String> languagesList = intent.getStringArrayListExtra("languages");
        ArrayList<String> rolesList = intent.getStringArrayListExtra("roles");
        
        if (languagesList != null) {
            languages = languagesList.toArray(new String[0]);
        } else {
            languages = new String[0];
        }
        
        if (rolesList != null) {
            roles = rolesList.toArray(new String[0]);
        } else {
            roles = new String[0];
        }
    }
    
    private void showStep(int step) {
        currentStep = step;
        android.util.Log.d("SignupTestActivity", "showStep 호출됨: step=" + step);
        
        try {
            switch (step) {
                case STEP_START:
                    android.util.Log.d("SignupTestActivity", "STEP_START 레이아웃 로드");
                    setContentView(R.layout.activity_signup_test_start);
                    initStartStep();
                    break;
                case STEP_ING:
                    android.util.Log.d("SignupTestActivity", "STEP_ING 레이아웃 로드");
                    setContentView(R.layout.activity_signup_test_ing);
                    initIngStep();
                    break;
                case STEP_RESULT:
                    android.util.Log.d("SignupTestActivity", "STEP_RESULT 레이아웃 로드");
                    setContentView(R.layout.activity_signup_test_result);
                    initResultStep();
                    break;
                default:
                    android.util.Log.d("SignupTestActivity", "기본 레이아웃 설정");
                    setContentView(R.layout.activity_signup_test_start);
                    initStartStep();
                    break;
            }
        } catch (Exception e) {
            android.util.Log.e("SignupTestActivity", "showStep 오류: " + e.getMessage());
            // 오류 발생 시 기본 레이아웃으로 설정
            Toast.makeText(this, "레이아웃 로드 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_signup_test_start);
            initStartStep();
        }
    }
    
    private void initStartStep() {
        try {
            android.util.Log.d("SignupTestActivity", "initStartStep 시작");
            
            tvTitle = findViewById(R.id.tv_test_title);
            tvDescription = findViewById(R.id.tv_app_title);
            btnStart = findViewById(R.id.btn_test_start);
            btnSkip = findViewById(R.id.tv_next);
            
            if (tvTitle == null || tvDescription == null || btnStart == null || btnSkip == null) {
                android.util.Log.e("SignupTestActivity", "필요한 뷰를 찾을 수 없습니다");
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_SHORT).show();
                return;
            }
            
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
            
            if (tvTitle == null || tvDescription == null || btnNext == null) {
                android.util.Log.e("SignupTestActivity", "initIngStep: 필요한 뷰를 찾을 수 없습니다");
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // btnNext는 TextView이므로 클릭 가능하도록 설정
            btnNext.setClickable(true);
            btnNext.setFocusable(true);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("SignupTestActivity", "다음 버튼 클릭됨 (STEP_ING)");
                    showStep(STEP_RESULT);
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
            
            if (tvTitle == null || tvDescription == null || btnNext == null) {
                android.util.Log.e("SignupTestActivity", "initResultStep: 필요한 뷰를 찾을 수 없습니다");
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_SHORT).show();
                return;
            }
            
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
}
