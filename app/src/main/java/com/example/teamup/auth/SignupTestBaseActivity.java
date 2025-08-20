package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;

public class SignupTestBaseActivity extends AppCompatActivity {

    private static final String TAG = "SignupTestBaseActivity";
    
    private String userId;
    private TextView btnPrevious, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_test_base);
        
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
    }
    
    private void initViews() {
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        
        btnPrevious.setOnClickListener(v -> goToPreviousStep());
        btnNext.setOnClickListener(v -> proceedToNextStep());
    }
    
    private void proceedToNextStep() {
        // 성격 테스트 시작 - MainPersonalityTestActivity로 이동
        Intent intent = new Intent(SignupTestBaseActivity.this, com.example.teamup.personality.MainPersonalityTestActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("fromSignup", true); // 회원가입에서 온 것임을 표시
        startActivity(intent);
    }
    
    private void goToPreviousStep() {
        finish(); // 이전 Activity로 돌아가기
    }
}
