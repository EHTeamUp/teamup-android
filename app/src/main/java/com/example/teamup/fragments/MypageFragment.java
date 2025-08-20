package com.example.teamup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.teamup.R;
import com.example.teamup.MainActivity;
import com.example.teamup.auth.LoginActivity;
import com.example.teamup.auth.LoginManager;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.auth.UserManager;
import com.example.teamup.fragments.MypageProfileFragment;
import com.example.teamup.fragments.MypageUserinfoFragment;

/**
 * 마이페이지 화면 Fragment
 */
public class MypageFragment extends Fragment {

    private static final String TAG = "MypageFragment";

    private LinearLayout llMemberInfo, llProfile, llContestList, llLogout;
    
    private TokenManager tokenManager;
    private UserManager userManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Manager 초기화
        tokenManager = TokenManager.getInstance(requireContext());
        userManager = UserManager.getInstance(requireContext());
        
        // 뷰 초기화
        initViews(view);
        
        // 클릭 리스너 설정
        setClickListeners();
    }

    private void initViews(View view) {
        llMemberInfo = view.findViewById(R.id.ll_member_info);
        llProfile = view.findViewById(R.id.ll_profile);
        llContestList = view.findViewById(R.id.ll_contest_list);
        llLogout = view.findViewById(R.id.ll_logout);
    }

    private void setClickListeners() {
        // 회원정보 메뉴
        llMemberInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showFragment(new MypageUserinfoFragment());
                }
            }
        });

        // 프로필 메뉴
        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showFragment(new MypageProfileFragment());
                }
            }
        });

        // 내 참여 공모전 목록 메뉴
        llContestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "내 참여 공모전 목록 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 로그아웃 메뉴
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmDialog();
            }
        });
    }



    /**
     * 로그아웃 확인 다이얼로그 표시
     */
    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    /**
     * 로그아웃 실행
     */
    private void performLogout() {
        // 토큰 삭제
        tokenManager.clearToken();
        
        // 로그인 상태 업데이트
        LoginManager.setLoggedIn(false);
        
        // 사용자 캐시 삭제
        userManager.clearUserCache();
        
        // 로그인 화면으로 이동
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
        
        Toast.makeText(getActivity(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fragment가 다시 활성화될 때마다 상태 확인
        if (!tokenManager.isLoggedIn()) {
            // 로그인되지 않은 상태라면 로그인 화면으로 이동
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }
}
