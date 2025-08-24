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

import com.google.firebase.messaging.FirebaseMessaging;

public class FcmTokenManager {
    private static final String TAG = "FcmTokenManager";
    private static final String PREF_NAME = "fcm_token_prefs";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_LAST_USER_ID = "last_user_id"; // 마지막으로 토큰을 전송한 사용자 ID
    private static final String KEY_TOKEN_TIMESTAMP = "token_timestamp"; // 토큰 생성 시간
    private static final String KEY_TOKEN_REFRESH_INTERVAL = "token_refresh_interval"; // 토큰 갱신 간격 (밀리초)
    
    // 토큰 갱신 간격 (7일)
    private static final long DEFAULT_TOKEN_REFRESH_INTERVAL = 7 * 24 * 60 * 60 * 1000L;
    
    private static FcmTokenManager instance;
    private final Context context;
    private final SharedPreferences preferences;
    private final TokenManager tokenManager;
    private boolean isUpdatingToken = false; // 토큰 업데이트 중복 방지 플래그
    
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
        editor.putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis());
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
        Log.d(TAG, "updateFcmToken 호출됨 - 토큰: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        
        // 토큰이 null이거나 빈 문자열이면 무시
        if (token == null || token.trim().isEmpty()) {
            Log.w(TAG, "FCM 토큰이 null이거나 빈 문자열입니다.");
            return;
        }
        
        // 사용자가 로그인되어 있는지 확인
        if (!tokenManager.isLoggedIn()) {
            Log.d(TAG, "로그인 안됨, 토큰 저장만 진행");
            saveFcmToken(token);
            return;
        }
        
        // 현재 로그인한 사용자 ID 가져오기
        String currentUserId = tokenManager.getUserId();
        
        Log.d(TAG, "updateFcmToken - 현재 사용자 ID: " + currentUserId);
        Log.d(TAG, "updateFcmToken - 현재 토큰: " + token.substring(0, Math.min(20, token.length())) + "...");
        
        // 중복 호출 방지
        if (isUpdatingToken) {
            Log.d(TAG, "토큰 업데이트 진행 중, 중복 호출 방지");
            return;
        }
        
        // 로컬에 토큰 저장
        saveFcmToken(token);
        
        // 서버에 토큰 업데이트 요청
        isUpdatingToken = true; // 업데이트 시작 플래그 설정
        
        FcmTokenRequest request = new FcmTokenRequest(token);
        Log.d(TAG, "FCM 토큰 서버 전송 중... 사용자: " + currentUserId + ", 토큰: " + token.substring(0, Math.min(20, token.length())) + "...");
        
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
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        isUpdatingToken = false; // 업데이트 완료 플래그 해제
                        Log.e(TAG, "FCM 토큰 서버 업데이트 네트워크 오류: " + t.getMessage());
                    }
                });
    }
    
    /**
     * 로그인 시 FCM 토큰을 서버에 전송
     */
    public void sendFcmTokenOnLogin() {
        try {
            Log.d(TAG, "로그인 시 FCM 토큰 전송 시작");
            String currentUserId = tokenManager.getUserId();
            
            Log.d(TAG, "현재 사용자 ID: " + currentUserId);
            Log.d(TAG, "TokenManager 로그인 상태: " + tokenManager.isLoggedIn());
            
            String token = getFcmToken();
            Log.d(TAG, "저장된 FCM 토큰: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
            
            if (token != null && !token.trim().isEmpty()) {
                Log.d(TAG, "저장된 토큰으로 서버 업데이트 진행");
                updateFcmToken(token);
            } else {
                Log.d(TAG, "저장된 FCM 토큰 없음, 새로 요청");
                requestFcmToken();
            }
        } catch (Exception e) {
            Log.w(TAG, "로그인 시 FCM 토큰 전송 중 오류 발생, 건너뜀: " + e.getMessage());
        }
    }
    
    /**
     * FCM 토큰을 강제로 요청
     */
    public void requestFcmToken() {
        try {
            // Firebase가 초기화되었는지 확인
            com.google.firebase.FirebaseApp app = com.google.firebase.FirebaseApp.getInstance();
            if (app == null) {
                Log.w(TAG, "Firebase가 초기화되지 않음, FCM 토큰 요청 건너뜀");
                return;
            }
            
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
        } catch (IllegalStateException e) {
            Log.w(TAG, "Firebase 초기화 오류, FCM 토큰 요청 건너뜀: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "FCM 토큰 요청 중 예상치 못한 오류: " + e.getMessage());
        }
    }
    
    /**
     * 토큰 신선도 확인 (토큰이 오래되었는지 체크)
     */
    public boolean isTokenFresh() {
        long tokenTimestamp = preferences.getLong(KEY_TOKEN_TIMESTAMP, 0);
        long refreshInterval = preferences.getLong(KEY_TOKEN_REFRESH_INTERVAL, DEFAULT_TOKEN_REFRESH_INTERVAL);
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - tokenTimestamp) < refreshInterval;
    }
    
    /**
     * 토큰 갱신 간격 설정
     */
    public void setTokenRefreshInterval(long intervalMillis) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(KEY_TOKEN_REFRESH_INTERVAL, intervalMillis);
        editor.apply();
        Log.d(TAG, "토큰 갱신 간격 설정: " + intervalMillis + "ms");
    }
    
    /**
     * 토큰 자동 갱신 (필요시에만)
     */
    public void refreshTokenIfNeeded() {
        if (!isTokenFresh()) {
            Log.d(TAG, "토큰이 오래되어 자동 갱신 시작");
            requestFcmToken();
        } else {
            Log.d(TAG, "토큰이 신선함, 갱신 불필요");
        }
    }
    
    /**
     * 로그아웃 시 FCM 토큰 초기화 (로컬만)
     */
    public void clearFcmToken() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_FCM_TOKEN);
        editor.remove(KEY_LAST_USER_ID); // 사용자 정보도 초기화
        editor.remove(KEY_TOKEN_TIMESTAMP); // 토큰 타임스탬프도 초기화
        editor.apply();
        Log.d(TAG, "FCM 토큰 및 사용자 정보 초기화 완료");
    }
    
    /**
     * 회원가입 완료 시 FCM 토큰 초기화
     */
    public void clearFcmTokenOnSignup() {
        Log.d(TAG, "회원가입 완료 시 FCM 토큰 초기화");
        clearFcmToken();
    }
    
    /**
     * 앱 시작 시 토큰 상태 확인 및 갱신
     */
    public void initializeTokenOnAppStart() {
        try {
            Log.d(TAG, "앱 시작 시 FCM 토큰 상태 확인");
            
            // 토큰이 없으면 새로 요청
            String token = getFcmToken();
            if (token == null || token.trim().isEmpty()) {
                Log.d(TAG, "저장된 FCM 토큰 없음, 새로 요청");
                requestFcmToken();
                return;
            }
            
            // 토큰이 오래되었으면 갱신
            if (!isTokenFresh()) {
                Log.d(TAG, "FCM 토큰이 오래됨, 갱신 필요");
                requestFcmToken();
                return;
            }
            
            // 로그인된 상태이고 토큰이 신선하면 서버에 전송
            if (tokenManager.isLoggedIn()) {
                Log.d(TAG, "로그인 상태에서 신선한 토큰 확인, 서버 전송");
                updateFcmToken(token);
            } else {
                Log.d(TAG, "로그인되지 않은 상태, 토큰만 로컬 저장");
            }
        } catch (Exception e) {
            Log.w(TAG, "앱 시작 시 FCM 토큰 초기화 중 오류 발생, 건너뜀: " + e.getMessage());
        }
    }
}
