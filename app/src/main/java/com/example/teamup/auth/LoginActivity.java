package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.google.android.material.button.MaterialButton;
import com.example.teamup.auth.LoginManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etId, etPassword;
    private MaterialButton btnLogin, btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etId = findViewById(R.id.et_id);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignIn = findViewById(R.id.btn_sign_in);

        // Setup button click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 회원가입 화면으로 이동
                Toast.makeText(LoginActivity.this, "회원가입 페이지 이동", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 로그인 수행
     * 임시로 true를 반환하도록 구현 (나중에 API 연동 시 변경)
     */
    private void performLogin() {
        String id = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // 입력값 검증
        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.please_enter_id_password, Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: 나중에 FastAPI 백엔드와 연동하여 실제 로그인 검증 수행
        // 현재는 임시로 true 반환
        boolean isLoginSuccess = checkLoginWithAPI(id, password);
        
        if (isLoginSuccess) {
            // 로그인 성공
            Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
            
            // 로그인 상태 업데이트
            LoginManager.setLoggedIn(true);
            
            // MainActivity로 이동
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // 로그인 실패
            Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * API를 통한 로그인 검증 (임시 구현)
     * 나중에 FastAPI 백엔드와 연동 시 이 메서드를 수정
     */
    private boolean checkLoginWithAPI(String id, String password) {
        // TODO: FastAPI 백엔드 연동
        // 임시로 true 반환
        
        return true;
    }
} 