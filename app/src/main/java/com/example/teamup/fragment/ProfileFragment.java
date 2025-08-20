package com.example.teamup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.teamup.R;
import com.example.teamup.auth.LoginActivity;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.mypage.MypageUserinfoActivity;
import com.example.teamup.mypage.MypageProfileActivity;

public class ProfileFragment extends Fragment {

    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        tokenManager = TokenManager.getInstance(requireContext());
        
        // 로그인 상태 확인
        if (!tokenManager.isLoggedIn()) {
            // 로그인되지 않은 경우 로그인 페이지로 이동
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        
        setupClickListeners(view);
    }
    
    private void setupClickListeners(View view) {
        // 회원정보 클릭
        LinearLayout llMemberInfo = view.findViewById(R.id.ll_member_info);
        llMemberInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MypageUserinfoActivity.class);
            startActivity(intent);
        });
        
        // 프로필 클릭
        LinearLayout llProfile = view.findViewById(R.id.ll_profile);
        llProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MypageProfileActivity.class);
            startActivity(intent);
        });
        
        // 내 참여 공모전 목록 클릭
        LinearLayout llContestList = view.findViewById(R.id.ll_contest_list);
        llContestList.setOnClickListener(v -> {
            // TODO: 내 참여 공모전 목록 Activity로 이동
            Toast.makeText(requireContext(), "내 참여 공모전 목록", Toast.LENGTH_SHORT).show();
        });
        
        // 로그아웃 클릭
        LinearLayout llLogout = view.findViewById(R.id.ll_logout);
        llLogout.setOnClickListener(v -> {
            tokenManager.clearToken();
            Toast.makeText(requireContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT).show();
            // 로그인 페이지로 이동
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
