package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.MainActivity;
import com.example.teamup.R;

public class SignupFinishActivity extends AppCompatActivity {
    
    private Button btnStart;
    private TextView tvWelcome;
    
    // 이전 액티비티에서 전달받은 데이터
    private String userId, userPassword, userName, userEmail;
    private String[] languages, roles;
    private String experience;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_finish);
        
        // 이전 액티비티에서 데이터 받기
        receiveDataFromPreviousActivity();
        
        // 뷰 초기화
        initViews();
        
        // 클릭 리스너 설정
        setClickListeners();
        
        // 환영 메시지 설정
        setWelcomeMessage();
    }
    
    private void receiveDataFromPreviousActivity() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        userPassword = intent.getStringExtra("password");
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");
        languages = intent.getStringArrayExtra("languages");
        roles = intent.getStringArrayExtra("roles");
        experience = intent.getStringExtra("experience");
    }
    
    private void initViews() {
        btnStart = findViewById(R.id.btn_login);
        tvWelcome = findViewById(R.id.tv_complete_message);
    }
    
    private void setClickListeners() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 완료 후 MainActivity로 이동
                Intent intent = new Intent(SignupFinishActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    
    private void setWelcomeMessage() {
        tvWelcome.setText(userName + "님, TeamUp에 오신 것을 환영합니다!");
    }
}
