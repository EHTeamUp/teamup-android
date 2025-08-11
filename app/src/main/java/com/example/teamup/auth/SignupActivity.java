package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;

public class SignupActivity extends AppCompatActivity {

    private EditText etId, etPassword, etName, etEmail, etEmailCode;
    private Button btnCheckId, btnSendEmail;
    private TextView tvNext, tvIdMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 뷰 초기화
        initViews();
        
        // 클릭 리스너 설정
        setClickListeners();
    }

    private void initViews() {
        etId = findViewById(R.id.et_id);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etEmailCode = findViewById(R.id.et_email_code);
        btnCheckId = findViewById(R.id.btn_check_id);
        btnSendEmail = findViewById(R.id.btn_send_email);
        tvNext = findViewById(R.id.tv_next);
        tvIdMessage = findViewById(R.id.tv_id_message);
    }

    private void setClickListeners() {
        // ID 중복 검사 버튼
        btnCheckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etId.getText().toString().trim();
                if (id.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: 실제 ID 중복 검사 로직 구현
                tvIdMessage.setText("사용 가능한 아이디입니다.");
                tvIdMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        });

        // 이메일 인증 요청 버튼
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: 실제 이메일 인증 로직 구현
                Toast.makeText(SignupActivity.this, "인증 이메일이 발송되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Next 버튼
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // SignupInterestActivity로 이동
                    Intent intent = new Intent(SignupActivity.this, SignupInterestActivity.class);
                    // 입력된 데이터를 다음 액티비티로 전달
                    intent.putExtra("id", etId.getText().toString().trim());
                    intent.putExtra("password", etPassword.getText().toString().trim());
                    intent.putExtra("name", etName.getText().toString().trim());
                    intent.putExtra("email", etEmail.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateInputs() {
        String id = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String emailCode = etEmailCode.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (emailCode.isEmpty()) {
            Toast.makeText(this, "이메일 인증 코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // TODO: 실제 이메일 인증 코드 검증 로직 구현
        // 현재는 임시로 통과시킴

        return true;
    }
}
