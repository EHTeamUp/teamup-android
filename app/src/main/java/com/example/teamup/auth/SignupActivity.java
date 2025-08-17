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

    // ==================== UI 컴포넌트 ====================
    /** 사용자 입력 필드들 */
    private EditText etId, etPassword, etName, etEmail, etEmailCode;
    
    /** 기능 버튼들 */
    private Button btnCheckId, btnSendEmail;
    
    /** 네비게이션 및 메시지 표시 */
    private TextView tvNext, tvIdMessage;
    
    // ==================== 상태 관리 플래그 ====================
    /** ID 중복 검사 완료 여부를 추적하는 플래그 */
    private boolean isIdChecked = false;
    
    /** 이메일 인증 요청 완료 여부를 추적하는 플래그 */
    private boolean isEmailSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 뷰 초기화
        initViews();
        
        // 클릭 리스너 설정
        setClickListeners();
        
        // 텍스트 변경 감지 설정
        setupTextWatchers();
    }

    /**
     * UI 컴포넌트들을 초기화하고 findViewById로 연결
     */
    private void initViews() {
        // 사용자 입력 필드 초기화
        etId = findViewById(R.id.et_id);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etEmailCode = findViewById(R.id.et_email_code);
        
        // 기능 버튼 초기화
        btnCheckId = findViewById(R.id.btn_check_id);
        btnSendEmail = findViewById(R.id.btn_send_email);
        
        // 네비게이션 및 메시지 초기화
        tvNext = findViewById(R.id.tv_next);
        tvIdMessage = findViewById(R.id.tv_id_message);
    }

    /**
     * 각 UI 컴포넌트의 클릭 이벤트 리스너를 설정
     */
    private void setClickListeners() {
        // ==================== ID 중복 검사 버튼 ====================
        btnCheckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etId.getText().toString().trim();
                
                // ID 입력 여부 확인
                if (id.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // TODO: 실제 ID 중복 검사 로직 구현
                // 현재는 임시로 성공 처리
                tvIdMessage.setText("사용 가능한 아이디입니다.");
                tvIdMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                isIdChecked = true; // ID 중복 검사 완료 플래그 설정
            }
        });

        // ==================== 이메일 인증 요청 버튼 ====================
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                
                // 이메일 입력 여부 확인
                if (email.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 이메일 형식 검증 (기본 이메일 패턴)
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignupActivity.this, "올바른 이메일 형식을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 이메일 도메인 검증 (@itc.ac.kr만 허용)
                if (!email.endsWith("@itc.ac.kr")) {
                    Toast.makeText(SignupActivity.this, "@itc.ac.kr 도메인의 이메일만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // TODO: 실제 이메일 인증 로직 구현
                // 현재는 임시로 성공 처리
                Toast.makeText(SignupActivity.this, "인증 이메일이 발송되었습니다.", Toast.LENGTH_SHORT).show();
                isEmailSent = true; // 이메일 인증 요청 완료 플래그 설정
            }
        });

        // ==================== Next 버튼 (다음 단계로 이동) ====================
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 입력 데이터의 유효성 검사 수행
                if (validateInputs()) {
                    // SignupInterestActivity로 이동하여 관심사 선택 단계 진행
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

    /**
     * 모든 입력 필드의 유효성을 검사하는 메서드
     * 
     * 검사 항목:
     * - ID: 필수 입력, 최소 4자, 중복 검사 완료
     * - 비밀번호: 필수 입력, 최소 6자
     * - 비밀번호 확인: 비밀번호와 일치
     * - 이름: 필수 입력
     * - 이메일: 필수 입력, 올바른 형식, @itc.ac.kr 도메인, 인증 요청 완료
     * - 이메일 인증 코드: 필수 입력
     * 
     * @return 모든 검증을 통과하면 true, 실패하면 false
     */
    private boolean validateInputs() {
        // ==================== 입력 데이터 추출 ====================
        String id = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = ((EditText) findViewById(R.id.et_confirm_password)).getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String emailCode = etEmailCode.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (id.length() < 4) {
            Toast.makeText(this, "ID는 4자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // ID 중복 검사 완료 여부 확인
        if (!isIdChecked) {
            Toast.makeText(this, "ID 중복 검사를 완료해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 비밀번호 검증
        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (password.length() < 8) {
            Toast.makeText(this, "비밀번호는 8자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 비밀번호 복잡성 검증 (대문자, 소문자, 숫자, 특수문자 포함)
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecialChar = true;
        }
        
        if (!hasUpperCase) {
            Toast.makeText(this, "비밀번호에 대문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!hasLowerCase) {
            Toast.makeText(this, "비밀번호에 소문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!hasDigit) {
            Toast.makeText(this, "비밀번호에 숫자를 포함해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!hasSpecialChar) {
            Toast.makeText(this, "비밀번호에 특수문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
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
        
        // 이메일 형식 검증 (기본 이메일 패턴)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "올바른 이메일 형식을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 이메일 도메인 검증 (@itc.ac.kr만 허용)
        if (!email.endsWith("@itc.ac.kr")) {
            Toast.makeText(this, "@itc.ac.kr 도메인의 이메일만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // 이메일 인증 요청 완료 여부 확인
        if (!isEmailSent) {
            Toast.makeText(this, "이메일 인증 요청을 완료해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (emailCode.isEmpty()) {
            Toast.makeText(this, "이메일 인증 코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // TODO: 실제 이메일 인증 코드 검증 로직 구현
        // 현재는 임시로 통과시킴

        return true; // 모든 검증 통과
    }
    
    /**
     * 텍스트 필드의 변경을 감지하여 관련 플래그를 리셋하는 메서드
     * 
     * 목적:
     * - ID 변경 시 중복 검사 플래그 리셋
     * - 이메일 변경 시 인증 요청 플래그 리셋
     * - 사용자가 정보를 변경했을 때 다시 검증하도록 유도
     */
    private void setupTextWatchers() {
        // ID 필드 변경 감지
        etId.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ID가 변경되면 중복 검사 플래그 리셋
                isIdChecked = false;
                tvIdMessage.setText(""); // 중복 검사 메시지 초기화
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // 이메일 필드 변경 감지
        etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 이메일이 변경되면 인증 요청 플래그 리셋
                isEmailSent = false;
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
}
