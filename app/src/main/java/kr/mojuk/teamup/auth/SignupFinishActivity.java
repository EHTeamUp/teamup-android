package kr.mojuk.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kr.mojuk.teamup.R;

public class SignupFinishActivity extends AppCompatActivity {

    private Button btnGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_finish);

        initViews();
    }

    private void initViews() {
        btnGoToLogin = findViewById(R.id.btn_go_to_login);
        
        btnGoToLogin.setOnClickListener(v -> {
            Toast.makeText(this, "로그인 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
            
            // LoginActivity로 이동
            Intent intent = new Intent(SignupFinishActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
