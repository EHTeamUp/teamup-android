package com.example.teamup.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.FcmTokenRequest;
import com.example.teamup.auth.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FcmTokenManager {
    private static final String TAG = "FcmTokenManager";
    private static final String PREF_NAME = "fcm_token_prefs";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    
    private static FcmTokenManager instance;
    private final Context context;
    private final SharedPreferences preferences;
    private final TokenManager tokenManager;
    
    private FcmTokenManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.tokenManager = TokenManager.getInstance(context);
    }
    
    public static synchronized FcmTokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new FcmTokenManager(context);
        }
        return instance;
    }
    
    /**
     * FCM 토큰을 로컬에 저장
     */
    public void saveFcmToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_FCM_TOKEN, token);
        editor.apply();
        Log.d(TAG, "FCM 토큰 로컬 저장: " + token);
    }
    
    /**
     * 로컬에서 FCM 토큰 가져오기
     */
    public String getFcmToken() {
        return preferences.getString(KEY_FCM_TOKEN, null);
    }
    
    /**
     * FCM 토큰을 서버에 업데이트 (Firebase 설정 완료 후 주석 해제)
     */
    public void updateFcmToken(String token) {
        // Firebase 설정 완료 후 주석 해제
        Log.d(TAG, "Firebase 설정이 완료되지 않아 FCM 토큰 업데이트를 건너뜁니다.");
        return;
        
        /*
        // 토큰이 null이거나 빈 문자열이면 무시
        if (token == null || token.trim().isEmpty()) {
            Log.w(TAG, "FCM 토큰이 null이거나 빈 문자열입니다.");
            return;
        }
        
        // 로컬에 토큰 저장
        saveFcmToken(token);
        
        // 사용자가 로그인되어 있는지 확인
        if (!tokenManager.isLoggedIn()) {
            Log.d(TAG, "사용자가 로그인되어 있지 않아 토큰 업데이트를 건너뜁니다.");
            return;
        }
        
        // 서버에 토큰 업데이트 요청
        FcmTokenRequest request = new FcmTokenRequest(token);
        
        RetrofitClient.getInstance()
                .getApiService()
                .updateFcmToken(request)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "FCM 토큰 서버 업데이트 성공");
                        } else {
                            Log.e(TAG, "FCM 토큰 서버 업데이트 실패 - HTTP " + response.code());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "FCM 토큰 서버 업데이트 네트워크 오류: " + t.getMessage(), t);
                    }
                });
        */
    }
    
    /**
     * 로그인 시 FCM 토큰을 서버에 전송
     */
    public void sendFcmTokenOnLogin() {
        String token = getFcmToken();
        if (token != null && !token.trim().isEmpty()) {
            Log.d(TAG, "로그인 시 FCM 토큰 전송: " + token);
            updateFcmToken(token);
        } else {
            Log.d(TAG, "저장된 FCM 토큰이 없습니다.");
        }
    }
    
    /**
     * 로그아웃 시 FCM 토큰 초기화
     */
    public void clearFcmToken() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_FCM_TOKEN);
        editor.apply();
        Log.d(TAG, "FCM 토큰 초기화 완료");
    }
}
