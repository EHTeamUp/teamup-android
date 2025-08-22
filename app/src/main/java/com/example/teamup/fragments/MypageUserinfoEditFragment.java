package com.example.teamup.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.example.teamup.api.model.UserDTO;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.auth.UserManager;

public class MypageUserinfoEditFragment extends Fragment {

    private static final String TAG = "MypageUserinfoEditFragment";
    
    private TextView tvUserId, tvUserEmail;
    private EditText etUserName, etCurrentPassword, etNewPassword, etCheckNewPassword;
    private Button btnCancel, btnSave;
    
    // Manager 인스턴스들
    private TokenManager tokenManager;
    private UserManager userManager;
    private UserDTO currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypage_userinfo_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Manager 초기화
        tokenManager = TokenManager.getInstance(requireContext());
        userManager = UserManager.getInstance(requireContext());

        initViews(view);
        setClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 화면이 다시 활성화될 때마다 사용자 정보 새로고침
        loadUserInfo();
    }

    private void initViews(View view) {
        tvUserId = view.findViewById(R.id.tv_user_id);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        etUserName = view.findViewById(R.id.et_user_name);
        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etCheckNewPassword = view.findViewById(R.id.et_check_new_password);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSave = view.findViewById(R.id.btn_save);
    }

    private void setClickListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원정보 화면으로 돌아가기
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showFragment(new MypageUserinfoFragment());
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
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
        
        userManager.getCurrentUser(requireContext(), new UserManager.UserCallback() {
            @Override
            public void onSuccess(UserDTO user) {
                Log.d(TAG, "사용자 정보 로드 성공: " + user.getUserId());
                currentUser = user;
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
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
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "사용자 정보 로드 실패: " + errorMessage);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 버튼 상태 복원
                        if (btnSave != null) {
                            btnSave.setEnabled(true);
                            btnSave.setText("저장");
                        }
                        
                        Toast.makeText(requireContext(), "사용자 정보를 불러오는데 실패했습니다: " + errorMessage, Toast.LENGTH_SHORT).show();
                        
                        // 에러가 인증 관련이라면 로그인 화면으로 이동
                        if (errorMessage.contains("인증") || errorMessage.contains("로그인") || errorMessage.contains("401")) {
                            // TODO: 로그인 화면으로 이동하는 로직 구현
                        }
                    });
                }
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
            Toast.makeText(requireContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 비밀번호가 입력되었는지 확인
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(requireContext(), "현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 비밀번호가 입력되었을 때만 검증
        if (!TextUtils.isEmpty(newPassword)) {
            // 새 비밀번호 복잡도 검사
            if (newPassword.length() < 8) {
                Toast.makeText(requireContext(), "비밀번호는 8자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "비밀번호에 대문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!hasLowerCase) {
                Toast.makeText(requireContext(), "비밀번호에 소문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!hasDigit) {
                Toast.makeText(requireContext(), "비밀번호에 숫자를 포함해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!hasSpecialChar) {
                Toast.makeText(requireContext(), "비밀번호에 특수문자를 포함해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 새 비밀번호 확인
            if (!newPassword.equals(checkNewPassword)) {
                Toast.makeText(requireContext(), "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
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
        
        userManager.updateUserInfo(requireContext(), name, currentPassword, passwordToUpdate, new UserManager.UserCallback() {
            @Override
            public void onSuccess(UserDTO user) {
                Log.d(TAG, "사용자 정보 업데이트 성공: " + user.getUserId());
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        btnSave.setEnabled(true);
                        btnSave.setText("저장");
                        
                        Toast.makeText(requireContext(), "정보가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                        
                        // 회원정보 화면으로 돌아가기
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).showFragment(new MypageUserinfoFragment());
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "사용자 정보 업데이트 실패: " + errorMessage);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        btnSave.setEnabled(true);
                        btnSave.setText("저장");
                        
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        
                        // 에러가 인증 관련이라면 로그인 화면으로 이동
                        if (errorMessage.contains("인증") || errorMessage.contains("로그인")) {
                            // TODO: 로그인 화면으로 이동하는 로직 구현
                        }
                    });
                }
            }
        });
    }
}
