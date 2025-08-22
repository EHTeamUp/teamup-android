package kr.mojuk.teamup.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import kr.mojuk.teamup.auth.TokenManager;
import kr.mojuk.teamup.api.model.FcmTokenRequest;
import kr.mojuk.teamup.api.model.ApiResponse;
import kr.mojuk.teamup.api.RetrofitClient;
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
    private boolean isUpdatingToken = false; // 토큰 업데이트 중복 방지 플래그
    private String lastSentToken = null; // 마지막으로 전송한 토큰 저장
    
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
        Log.d(TAG, "FCM 토큰 로컬 저장 완료");
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
        // 토큰이 null이거나 빈 문자열이면 무시
        if (token == null || token.trim().isEmpty()) {
            Log.w(TAG, "FCM 토큰이 null이거나 빈 문자열입니다.");
            return;
        }
        
        // 이미 같은 토큰을 전송했으면 건너뛰기
        if (token.equals(lastSentToken)) {
            Log.d(TAG, "같은 토큰 중복 전송 방지");
            return;
        }
        
        // 중복 호출 방지
        if (isUpdatingToken) {
            Log.d(TAG, "토큰 업데이트 진행 중, 중복 호출 방지");
            return;
        }
        
        // 사용자가 로그인되어 있는지 확인
        if (!tokenManager.isLoggedIn()) {
            Log.d(TAG, "로그인 안됨, 토큰 저장만 진행");
            saveFcmToken(token);
            return;
        }
        
        // 로컬에 토큰 저장
        saveFcmToken(token);
        
        // 서버에 토큰 업데이트 요청
        isUpdatingToken = true; // 업데이트 시작 플래그 설정
        lastSentToken = token; // 전송한 토큰 저장
        FcmTokenRequest request = new FcmTokenRequest(token);
        Log.d(TAG, "FCM 토큰 서버 전송 중...");
        
        RetrofitClient.getInstance(context)
                .getApiService()
                .updateFcmToken(request)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        isUpdatingToken = false; // 업데이트 완료 플래그 해제
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "FCM 토큰 서버 업데이트 성공");
                        } else {
                            Log.e(TAG, "FCM 토큰 서버 업데이트 실패 - HTTP " + response.code());
                            lastSentToken = null; // 실패 시 토큰 초기화
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        isUpdatingToken = false; // 업데이트 완료 플래그 해제
                        lastSentToken = null; // 실패 시 토큰 초기화
                        Log.e(TAG, "FCM 토큰 서버 업데이트 네트워크 오류: " + t.getMessage());
                    }
                });
    }
    
    /**
     * 로그인 시 FCM 토큰을 서버에 전송
     */
    public void sendFcmTokenOnLogin() {
        Log.d(TAG, "로그인 시 FCM 토큰 전송 시작");
        String token = getFcmToken();
        if (token != null && !token.trim().isEmpty()) {
            updateFcmToken(token);
        } else {
            Log.d(TAG, "저장된 FCM 토큰 없음, 새로 요청");
            requestFcmToken();
        }
    }
    
    /**
     * FCM 토큰을 강제로 요청
     */
    public void requestFcmToken() {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "FCM 토큰 가져오기 실패", task.getException());
                    return;
                }

                String token = task.getResult();
                if (token != null && !token.trim().isEmpty()) {
                    Log.d(TAG, "새 FCM 토큰 요청 성공");
                    updateFcmToken(token);
                } else {
                    Log.w(TAG, "FCM 토큰이 null이거나 빈 값");
                }
            });
    }
    
    /**
     * 로그아웃 시 FCM 토큰 초기화
     */
    public void clearFcmToken() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_FCM_TOKEN);
        editor.apply();
        lastSentToken = null; // 전송한 토큰도 초기화
        Log.d(TAG, "FCM 토큰 초기화 완료");
    }
}
