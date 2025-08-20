package com.example.teamup.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.teamup.MainActivity;
import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.auth.UserManager;
import com.example.teamup.auth.LoginActivity;
import com.example.teamup.api.model.UserDTO;

public class MypageUserinfoEditActivity extends AppCompatActivity {

    private static final String TAG = "MypageUserinfoEditActivity";
    
    private TextView tvHeaderTitle, tvUserId, tvUserEmail, etUserName;
    private EditText etCurrentPassword, etNewPassword, etCheckNewPassword;
    private Button btnCancel, btnSave;
    private BottomNavigationView bottomNavigationView;
    
    // Manager 인스턴스들
    private TokenManager tokenManager;
    private UserManager userManager;
    private UserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_userinfo_edit);

        // Manager 초기화
        tokenManager = TokenManager.getInstance(this);
        userManager = UserManager.getInstance(this);

        // 로그인 상태 확인
        if (!tokenManager.isLoggedIn()) {
            Log.d(TAG, "로그인되지 않은 상태입니다. LoginActivity로 이동합니다.");
            Intent intent = new Intent(MypageUserinfoEditActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initViews();
        setClickListeners();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 활성화될 때마다 사용자 정보 새로고침
        loadUserInfo();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvUserId = findViewById(R.id.tv_user_id);
        tvUserEmail = findViewById(R.id.tv_user_email);
        etUserName = findViewById(R.id.et_user_name);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etCheckNewPassword = findViewById(R.id.et_check_new_password);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    /**
     * 사용자 정보 로드
     */
    private void loadUserInfo() {
        Log.d(TAG, "사용자 정보 로드 시작");
        
        // 로딩 상태 표시 (선택사항)
        if (btnSave != null) {
            btnSave.setEnabled(false);
            btnSave.setText("로딩 중...");
        }
        
        userManager.getCurrentUser(this, new UserManager.UserCallback() {
            @Override
            public void onSuccess(UserDTO user) {
                Log.d(TAG, "사용자 정보 로드 성공: " + user.getUserId());
                currentUser = user;
                
                runOnUiThread(() -> {
                    // UI 업데이트
                    if (tvUserId != null) {
                        tvUserId.setText(user.getUserId() != null ? user.getUserId() : "");
                    }
                    if (tvUserEmail != null) {
                        tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                    }
                    if (etUserName != null) {
                        etUserName.setText(user.getName() != null ? user.getName() : "");
                    }
                    
                    // 버튼 상태 복원
                    if (btnSave != null) {
                        btnSave.setEnabled(true);
                        btnSave.setText("저장");
                    }
                    
                    Log.d(TAG, "사용자 정보 UI 업데이트 완료");
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "사용자 정보 로드 실패: " + errorMessage);
                
                runOnUiThread(() -> {
                    // 버튼 상태 복원
                    if (btnSave != null) {
                        btnSave.setEnabled(true);
                        btnSave.setText("저장");
                    }
                    
                    Toast.makeText(MypageUserinfoEditActivity.this, "사용자 정보를 불러오는데 실패했습니다: " + errorMessage, Toast.LENGTH_SHORT).show();
                    
                    // 에러가 인증 관련이라면 로그인 화면으로 이동
                    if (errorMessage.contains("인증") || errorMessage.contains("로그인") || errorMessage.contains("401")) {
                        Intent intent = new Intent(MypageUserinfoEditActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
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
        String userName = etUserName.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String checkNewPassword = etCheckNewPassword.getText().toString().trim();

        // 이름 검증
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 비밀번호가 입력되었는지 확인
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 비밀번호가 입력되었을 때만 검증
        if (!TextUtils.isEmpty(newPassword)) {
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

            // 새 비밀번호 확인
            if (!newPassword.equals(checkNewPassword)) {
                Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 모든 검증 통과 - API 호출
        updateUserInfo(userName, currentPassword, newPassword);
    }

    /**
     * 사용자 정보 업데이트
     */
    private void updateUserInfo(String name, String currentPassword, String newPassword) {
        // 버튼 비활성화
        btnSave.setEnabled(false);
        btnSave.setText("저장 중...");

        String passwordToUpdate = TextUtils.isEmpty(newPassword) ? null : newPassword;
        
        userManager.updateUserInfo(this, name, currentPassword, passwordToUpdate, new UserManager.UserCallback() {
            @Override
            public void onSuccess(UserDTO user) {
                Log.d(TAG, "사용자 정보 업데이트 성공: " + user.getUserId());
                
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("저장");
                    
                    Toast.makeText(MypageUserinfoEditActivity.this, "정보가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "사용자 정보 업데이트 실패: " + errorMessage);
                
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("저장");
                    
                    Toast.makeText(MypageUserinfoEditActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    
                    // 에러가 인증 관련이라면 로그인 화면으로 이동
                    if (errorMessage.contains("인증") || errorMessage.contains("로그인")) {
                        Intent intent = new Intent(MypageUserinfoEditActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
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
                Intent intent = new Intent(MypageUserinfoEditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
