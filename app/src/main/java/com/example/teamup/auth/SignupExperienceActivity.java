package com.example.teamup.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;

public class SignupExperienceActivity extends AppCompatActivity {
    
    private EditText etExperience;
    private TextView btnNext, tvPrevious;
    
    // 이전 액티비티에서 전달받은 데이터
    private String userId, userPassword, userName, userEmail;
    private String[] languages, roles;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_experience);
        
        // 이전 액티비티에서 데이터 받기
        receiveDataFromPreviousActivity();
        
        // 뷰 초기화
        initViews();
        
        // 클릭 리스너 설정
        setClickListeners();
    }
    
    private void receiveDataFromPreviousActivity() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        userPassword = intent.getStringExtra("password");
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");
        languages = intent.getStringArrayExtra("languages");
        roles = intent.getStringArrayExtra("roles");
    }
    
    @SuppressLint("WrongViewCast")
    private void initViews() {
        etExperience = findViewById(R.id.et_award_title);
        btnNext = findViewById(R.id.tv_skip);
        tvPrevious = findViewById(R.id.tv_previous);
        
        // btnNext는 TextView이므로 클릭 가능하도록 설정
        btnNext.setClickable(true);
        btnNext.setFocusable(true);
    }
    
    private void setClickListeners() {
        // Previous 버튼
        tvPrevious.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupTestActivity.class);
            intent.putExtra("id", userId);
            startActivity(intent);
            finish();
        });
        
        // Next 버튼 (Skip으로 표시되어 있음)
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    // 다음 액티비티로 이동 (SignupFinishActivity)
                    Intent intent = new Intent(SignupExperienceActivity.this, SignupFinishActivity.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("password", userPassword);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("languages", languages);
                    intent.putExtra("roles", roles);
                    intent.putExtra("experience", etExperience.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
    }
    
    private boolean validateInput() {
        String experience = etExperience.getText().toString().trim();
        
        if (experience.isEmpty()) {
            Toast.makeText(this, "경험을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
}
