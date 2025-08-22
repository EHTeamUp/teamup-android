package com.example.teamup.auth;

import android.content.Context;

/**
 * 로그인 상태를 관리하는 클래스
 * TokenManager와 연동하여 실제 토큰 상태를 반영
 */
public class LoginManager {
    
    private static boolean isLoggedIn = false;
    
    /**
     * 로그인 상태 설정
     */
    public static void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
    
    /**
     * 현재 로그인 상태 확인
     */
    public static boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    /**
     * TokenManager를 통한 실제 로그인 상태 확인
     */
    public static boolean isLoggedIn(Context context) {
        TokenManager tokenManager = TokenManager.getInstance(context);
        return tokenManager.isLoggedIn();
    }
    
    /**
     * 로그아웃 처리
     */
    public static void logout() {
        isLoggedIn = false;
    }
    
    /**
     * TokenManager를 통한 완전한 로그아웃 처리
     */
    public static void logout(Context context) {
        isLoggedIn = false;
        TokenManager tokenManager = TokenManager.getInstance(context);
        tokenManager.clearToken();
    }
} 