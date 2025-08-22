package com.example.teamup.auth;

import android.content.Context;
import android.util.Log;

import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.LoginRequest;
import com.example.teamup.api.model.UserDTO;
import com.example.teamup.api.model.UserUpdateRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 사용자 정보를 관리하는 클래스
 * API를 통해 사용자 정보를 가져오고 업데이트하는 기능 제공
 */
public class UserManager {
    
    private static final String TAG = "UserManager";
    private static UserManager instance;
    private TokenManager tokenManager;
    private UserDTO currentUser;
    
    private UserManager(Context context) {
        tokenManager = TokenManager.getInstance(context);
    }
    
    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 현재 사용자 정보 가져오기
     */
    public void getCurrentUser(Context context, UserCallback callback) {
        if (!tokenManager.isLoggedIn()) {
            callback.onError("로그인이 필요합니다.");
            return;
        }
        
        String fullToken = tokenManager.getFullToken();
        if (fullToken == null) {
            callback.onError("토큰이 유효하지 않습니다.");
            return;
        }
        
        RetrofitClient.getInstance()
                .getApiService()
                .getCurrentUser(fullToken)
                .enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentUser = response.body();
                            Log.d(TAG, "사용자 정보 조회 성공: " + currentUser.getUserId());
                            callback.onSuccess(currentUser);
                        } else {
                            String errorMessage = "사용자 정보 조회 실패";
                            if (response.code() == 401) {
                                errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요.";
                                // 토큰 만료 시 로그아웃 처리
                                LoginManager.logout(context);
                            }
                            Log.e(TAG, "사용자 정보 조회 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "사용자 정보 조회 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 사용자 정보 업데이트
     */
    public void updateUserInfo(Context context, String name, String currentPassword, String newPassword, UserCallback callback) {
        if (!tokenManager.isLoggedIn()) {
            callback.onError("로그인이 필요합니다.");
            return;
        }
        
        String fullToken = tokenManager.getFullToken();
        if (fullToken == null) {
            callback.onError("토큰이 유효하지 않습니다.");
            return;
        }
        
        // 업데이트할 사용자 정보 생성
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName(name);
        updateRequest.setCurrentPassword(currentPassword);
        if (newPassword != null && !newPassword.isEmpty()) {
            updateRequest.setNewPassword(newPassword);
        }
        
        RetrofitClient.getInstance()
                .getApiService()
                .updateUserInfo(fullToken, updateRequest)
                .enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentUser = response.body();
                            Log.d(TAG, "사용자 정보 업데이트 성공: " + currentUser.getUserId());
                            callback.onSuccess(currentUser);
                        } else {
                            String errorMessage = "사용자 정보 업데이트 실패";
                            if (response.code() == 401) {
                                errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요.";
                                LoginManager.logout(context);
                            } else if (response.code() == 400) {
                                errorMessage = "입력 정보가 올바르지 않습니다.";
                            }
                            Log.e(TAG, "사용자 정보 업데이트 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "사용자 정보 업데이트 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 현재 캐시된 사용자 정보 가져오기
     */
    public UserDTO getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 사용자 정보 캐시 초기화
     */
    public void clearUserCache() {
        currentUser = null;
    }
    
    /**
     * 사용자 정보 콜백 인터페이스
     */
    public interface UserCallback {
        void onSuccess(UserDTO user);
        void onError(String errorMessage);
    }
}
