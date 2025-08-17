package com.example.teamup.auth;

/**
 * 로그인 상태를 관리하는 클래스
 * 현재는 간단한 static 변수로 관리
 * 나중에 SharedPreferences나 다른 방법으로 변경 가능
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
     * 로그아웃 처리
     */
    public static void logout() {
        isLoggedIn = false;
    }
} 