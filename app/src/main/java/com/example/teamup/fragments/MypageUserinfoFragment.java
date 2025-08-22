package com.example.teamup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.auth.UserManager;
import com.example.teamup.api.model.LoginRequest;

public class MypageUserinfoFragment extends Fragment {

    private static final String TAG = "MypageUserinfoFragment";
    
    private TextView tvUserId, tvUserEmail, tvUserName, tvBackArrow;
    private Button btnEdit;
    private View cvNavigation;
    
    // Manager 인스턴스들
    private TokenManager tokenManager;
    private UserManager userManager;
    private LoginRequest currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypage_userinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Manager 초기화
        tokenManager = TokenManager.getInstance(requireContext());
        userManager = UserManager.getInstance(requireContext());

        initViews(view);
        setClickListeners();
        loadUserInfo();
    }

    private void initViews(View view) {
        cvNavigation = view.findViewById(R.id.cv_navigation);
        tvBackArrow = view.findViewById(R.id.tv_mypage_back_arrow);
        tvUserId = view.findViewById(R.id.tv_user_id);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserName = view.findViewById(R.id.tv_user_name);
        btnEdit = view.findViewById(R.id.btn_edit);
    }

    private void setClickListeners() {
        cvNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지로 돌아가기
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showFragment(new MypageFragment());
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 편집 화면으로 이동
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showFragment(new MypageUserinfoEditFragment());
                }
            }
        });
    }

    /**
     * 사용자 정보 로드
     */
    private void loadUserInfo() {
        userManager.getCurrentUser(requireContext(), new UserManager.UserCallback() {
            @Override
            public void onSuccess(LoginRequest user) {
                Log.d(TAG, "사용자 정보 로드 성공: " + user.getUserId());
                currentUser = user;
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // UI 업데이트
                        if (tvUserId != null) {
                            tvUserId.setText(user.getUserId() != null ? user.getUserId() : "");
                        }

                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "사용자 정보 로드 실패: " + errorMessage);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
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

    @Override
    public void onResume() {
        super.onResume();
        // 화면이 다시 활성화될 때마다 사용자 정보 새로고침
        if (tokenManager.isLoggedIn()) {
            loadUserInfo();
        }
    }
}
