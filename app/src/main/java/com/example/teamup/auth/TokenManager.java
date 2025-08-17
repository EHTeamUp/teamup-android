package com.example.teamup.auth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * JWT 토큰을 안전하게 저장하고 관리하는 클래스
 */
public class TokenManager {
    private static final String PREF_NAME = "TeamUpPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    
    private static TokenManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    
    private TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 토큰과 사용자 정보 저장
     */
    public void saveTokenAndUser(String accessToken, String tokenType, String userId) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_TOKEN_TYPE, tokenType);
        editor.putString(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    /**
     * 토큰만 저장하고 JWT에서 사용자 정보 자동 추출
     */
    public void saveToken(String accessToken, String tokenType) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_TOKEN_TYPE, tokenType);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
        
        // JWT에서 사용자 ID 자동 추출 및 저장
        String userId = JwtUtils.getUserIdFromToken(accessToken);
        if (userId != null) {
            editor.putString(KEY_USER_ID, userId);
            editor.apply();
        }
    }
    
    /**
     * 액세스 토큰 가져오기
     */
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }
    
    /**
     * 토큰 타입 가져오기
     */
    public String getTokenType() {
        return sharedPreferences.getString(KEY_TOKEN_TYPE, "bearer");
    }
    
    /**
     * 전체 토큰 문자열을 반환 (Bearer + 토큰)
     */
    public String getFullToken() {
        String accessToken = getAccessToken();
        String tokenType = getTokenType();
        if (accessToken != null) {
            return tokenType + " " + accessToken;
        }
        return null;
    }
    
    /**
     * 로그인 상태 확인
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && getAccessToken() != null;
    }
    
    /**
     * 토큰 정보 삭제 (로그아웃)
     */
    public void clearToken() {
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_TOKEN_TYPE);
        editor.remove(KEY_USER_ID);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }
    
    /**
     * 토큰이 유효한지 확인
     */
    public boolean isTokenValid() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }
    
    /**
     * 사용자 ID 가져오기
     */
    public String getUserId() {
        // 먼저 저장된 사용자 ID 확인
        String storedUserId = sharedPreferences.getString(KEY_USER_ID, null);
        if (storedUserId != null) {
            return storedUserId;
        }
        
        // 저장된 사용자 ID가 없으면 JWT에서 추출
        String accessToken = getAccessToken();
        if (accessToken != null) {
            String userId = JwtUtils.getUserIdFromToken(accessToken);
            if (userId != null) {
                // 추출한 사용자 ID 저장
                editor.putString(KEY_USER_ID, userId);
                editor.apply();
                return userId;
            }
        }
        
        return null;
    }
}

