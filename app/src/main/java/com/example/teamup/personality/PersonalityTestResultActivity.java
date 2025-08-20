package com.example.teamup.personality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.google.android.material.button.MaterialButton;

public class PersonalityTestResultActivity extends AppCompatActivity {

    private MaterialButton btnFinishTest;
    private String userId;
    private boolean fromSignup;
    private String personalityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personality_test_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 회원가입에서 온 경우 userId와 fromSignup 플래그 받기
        userId = getIntent().getStringExtra("userId");
        fromSignup = getIntent().getBooleanExtra("fromSignup", false);
        personalityType = getIntent().getStringExtra("personalityType");

        // 뷰 초기화
        btnFinishTest = findViewById(R.id.btn_finish_test);
        
        // 성향 결과 표시
        displayPersonalityResult();

        // 버튼 클릭 리스너 설정
        btnFinishTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromSignup && userId != null) {
                    // 회원가입에서 온 경우: 회원가입 완료 단계로 이동
                    Intent intent = new Intent(PersonalityTestResultActivity.this, com.example.teamup.auth.SignupFinishActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    // 일반적인 경우: 메인 화면으로 이동
                    Intent intent = new Intent(PersonalityTestResultActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                finish();
            }
        });
    }
    
    private void displayPersonalityResult() {
        TextView tvResultTitle = findViewById(R.id.tv_result_title);
        
        if (personalityType != null && !personalityType.isEmpty()) {
            tvResultTitle.setText(personalityType);
        } else {
            tvResultTitle.setText("분석형"); // 기본값
        }
    }
} 