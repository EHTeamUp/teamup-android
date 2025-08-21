package com.example.teamup.personality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teamup.R;
import com.google.android.material.button.MaterialButton;

public class MainPersonalityTestActivity extends AppCompatActivity {

    private String userId;
    private boolean fromSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_main_personality_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 회원가입에서 온 경우 userId와 fromSignup 플래그 받기
        userId = getIntent().getStringExtra("userId");
        fromSignup = getIntent().getBooleanExtra("fromSignup", false);

        // 시작 버튼 클릭 리스너 설정
        setupStartButton();
    }

    private void setupStartButton() {
        MaterialButton btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 성향테스트 질문 화면으로 이동
                Intent intent = new Intent(MainPersonalityTestActivity.this, PersonalityTestQuestionActivity.class);
                if (fromSignup && userId != null) {
                    intent.putExtra("userId", userId);
                    intent.putExtra("fromSignup", true);
                }
                startActivity(intent);
            }
        });
    }
} 