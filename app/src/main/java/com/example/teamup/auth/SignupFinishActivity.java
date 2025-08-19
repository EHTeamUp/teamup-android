package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;




public class SignupFinishActivity extends AppCompatActivity {
    
    private Button btnStart;
    
    
    // 이전 액티비티에서 전달받은 데이터
    private String userId, userPassword, userName, userEmail;
    private String[] languages, roles;
    private String contestName, awardTitle, experience;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_finish);
        

        // 이전 액티비티에서 데이터 받기
        receiveDataFromPreviousActivity();
        
        // 데이터베이스에 저장
        saveUserDataToDatabase();
        
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
        contestName = intent.getStringExtra("contestName");
        awardTitle = intent.getStringExtra("awardTitle");
        experience = intent.getStringExtra("experience");
    }
    
    private void initViews() {
        btnStart = findViewById(R.id.btn_login);
        
    }
    
    private void setClickListeners() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 완료 후 MainActivity로 이동
                Intent intent = new Intent(SignupFinishActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    
  

    private void saveUserDataToDatabase() {

        try {

            
            // 언어 배열을 문자열로 변환
            if (languages != null) {
                StringBuilder languagesStr = new StringBuilder();
                for (String language : languages) {
                    if (languagesStr.length() > 0) {
                        languagesStr.append(",");
                    }
                    languagesStr.append(language);
                }
            }
            
            // 역할 배열을 문자열로 변환
            if (roles != null) {
                StringBuilder rolesStr = new StringBuilder();
                for (String role : roles) {
                    if (rolesStr.length() > 0) {
                        rolesStr.append(",");
                    }
                    rolesStr.append(role);
                }
            }
            

            
        } catch (Exception e) {
            // 오류 발생 시에도 사용자에게는 표시하지 않음
        }

    }
}
