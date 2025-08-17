package com.example.teamup.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.teamup.MainActivity;
import com.example.teamup.MypageActivity;
import com.example.teamup.R;

public class MypageUserinfoEditActivity extends AppCompatActivity {

    private TextView tvHeaderTitle, tvUserId, tvUserEmail, etUserName;
    private EditText etCurrentPassword, etNewPassword, etCheckNewPassword;
    private Button btnCancel, btnSave;
    private BottomNavigationView bottomNavigationView;
    
    // 임시로 현재 비밀번호 설정 (실제로는 데이터베이스에서 가져와야 함)
    private String currentUserPassword = "Test1234!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_userinfo_edit);

        initViews();
        setClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvUserId = findViewById(R.id.tv_mypage_id_value);
        tvUserEmail = findViewById(R.id.tv_mypage_email_value);
        etUserName = findViewById(R.id.et_user_name);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etCheckNewPassword = findViewById(R.id.et_check_new_password);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setClickListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 돌아가기
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
    }

    private void validateInputs() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String checkNewPassword = etCheckNewPassword.getText().toString().trim();

        // 현재 비밀번호 확인
        if (!currentPassword.equals(currentUserPassword)) {
            Toast.makeText(this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 비밀번호가 입력되었는지 확인
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "새 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 비밀번호 복잡도 검사
        if (newPassword.length() < 8) {
            Toast.makeText(this, "비밀번호는 8자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        for (char c : newPassword.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecialChar = true;
        }
        if (!hasUpperCase) {
            Toast.makeText(this, "비밀번호에 대문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasLowerCase) {
            Toast.makeText(this, "비밀번호에 소문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasDigit) {
            Toast.makeText(this, "비밀번호에 숫자를 포함해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasSpecialChar) {
            Toast.makeText(this, "비밀번호에 특수문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 비밀번호와 현재 비밀번호가 다른지 확인
        if (newPassword.equals(currentUserPassword)) {
            Toast.makeText(this, "새 비밀번호는 현재 비밀번호와 달라야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 비밀번호 확인
        if (!newPassword.equals(checkNewPassword)) {
            Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 모든 검증 통과 - 비밀번호 변경 성공
        Toast.makeText(this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setupBottomNavigation() {
        // 마이페이지 탭을 선택된 상태로 설정
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        
        // 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(MypageUserinfoEditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_contest) {
                // 공모전 화면으로 이동
                return true;
            } else if (itemId == R.id.navigation_board) {
                // 게시판 화면으로 이동
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // 마이페이지 메인으로 이동
                Intent intent = new Intent(MypageUserinfoEditActivity.this, MypageActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
