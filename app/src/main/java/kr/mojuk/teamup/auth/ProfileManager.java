package kr.mojuk.teamup.auth;

import android.content.Context;
import android.util.Log;

import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.*;

import java.util.List;
import java.util.ArrayList;

import kr.mojuk.teamup.api.model.Experience;
import kr.mojuk.teamup.api.model.ExperienceCreate;
import kr.mojuk.teamup.api.model.ProfileUpdateResponse;
import kr.mojuk.teamup.api.model.RoleUpdate;
import kr.mojuk.teamup.api.model.SkillUpdate;
import kr.mojuk.teamup.api.model.UserRolesResponse;
import kr.mojuk.teamup.api.model.UserSkillsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 프로필 관리를 위한 클래스
 * 스킬, 역할, 경험 정보의 조회 및 수정을 처리
 */
public class ProfileManager {
    
    private static final String TAG = "ProfileManager";
    private static ProfileManager instance;
    private TokenManager tokenManager;
    
    private ProfileManager(Context context) {
        tokenManager = TokenManager.getInstance(context);
    }
    
    public static synchronized ProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileManager(context.getApplicationContext());
        }
        return instance;
    }
    
    // ===== 스킬 관리 =====
    
    /**
     * 현재 사용자의 스킬 조회
     */
    public void getUserSkills(Context context, UserSkillsCallback callback) {
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
                .getUserSkills(fullToken)
                .enqueue(new Callback<UserSkillsResponse>() {
                    @Override
                    public void onResponse(Call<UserSkillsResponse> call, Response<UserSkillsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserSkillsResponse result = response.body();
                            Log.d(TAG, "사용자 스킬 조회 성공: " + (result.getSkillIds() != null ? result.getSkillIds().size() : 0) + "개");
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "스킬 조회 실패";
                            if (response.code() == 401) {
                                errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요.";
                                LoginManager.logout(context);
                            }
                            Log.e(TAG, "스킬 조회 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserSkillsResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "스킬 조회 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 사용자 스킬 수정
     */
    public void updateUserSkills(Context context, List<Integer> skillIds, ProfileUpdateCallback callback) {
        if (!tokenManager.isLoggedIn()) {
            callback.onError("로그인이 필요합니다.");
            return;
        }
        
        String fullToken = tokenManager.getFullToken();
        if (fullToken == null) {
            callback.onError("토큰이 유효하지 않습니다.");
            return;
        }
        
        SkillUpdate skillUpdate = new SkillUpdate(skillIds, new ArrayList<>());
        
        RetrofitClient.getInstance()
                .getApiService()
                .updateUserSkills(fullToken, skillUpdate)
                .enqueue(new Callback<ProfileUpdateResponse>() {
                    @Override
                    public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ProfileUpdateResponse result = response.body();
                            Log.d(TAG, "스킬 수정 성공: " + result.getMessage());
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "스킬 수정 실패";
                            if (response.code() == 400) {
                                errorMessage = "스킬을 최소 1개 이상 선택하거나 입력해주세요.";
                            } else if (response.code() == 401) {
                                errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요.";
                                LoginManager.logout(context);
                            }
                            Log.e(TAG, "스킬 수정 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "스킬 수정 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // ===== 역할 관리 =====
    
    /**
     * 현재 사용자의 역할 조회
     */
    public void getUserRoles(Context context, UserRolesCallback callback) {
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
                .getUserRoles(fullToken)
                .enqueue(new Callback<UserRolesResponse>() {
                    @Override
                    public void onResponse(Call<UserRolesResponse> call, Response<UserRolesResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserRolesResponse result = response.body();
                            Log.d(TAG, "사용자 역할 조회 성공: " + (result.getRoleIds() != null ? result.getRoleIds().size() : 0) + "개");
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "역할 조회 실패";
                            if (response.code() == 401) {
                                errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요.";
                                LoginManager.logout(context);
                            }
                            Log.e(TAG, "역할 조회 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRolesResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "역할 조회 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 사용자 역할 수정
     */
    public void updateUserRoles(Context context, List<Integer> roleIds, ProfileUpdateCallback callback) {
        if (!tokenManager.isLoggedIn()) {
            callback.onError("로그인이 필요합니다.");
            return;
        }
        
        String fullToken = tokenManager.getFullToken();
        if (fullToken == null) {
            callback.onError("토큰이 유효하지 않습니다.");
            return;
        }
        
        RoleUpdate roleUpdate = new RoleUpdate(roleIds, new ArrayList<>());
        
        RetrofitClient.getInstance()
                .getApiService()
                .updateUserRoles(fullToken, roleUpdate)
                .enqueue(new Callback<ProfileUpdateResponse>() {
                    @Override
                    public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ProfileUpdateResponse result = response.body();
                            Log.d(TAG, "역할 수정 성공: " + result.getMessage());
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "역할 수정 실패";
                            if (response.code() == 400) {
                                errorMessage = "역할을 최소 1개 이상 선택하거나 입력해주세요.";
                            } else if (response.code() == 401) {
                                errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요.";
                                LoginManager.logout(context);
                            }
                            Log.e(TAG, "역할 수정 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "역할 수정 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // ===== 경험 관리 =====
    
    /**
     * 현재 사용자의 경험 조회
     */
    public void getUserExperiences(Context context, UserExperiencesCallback callback) {
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
                .getUserExperiences(fullToken)
                .enqueue(new Callback<List<Experience>>() {
                    @Override
                    public void onResponse(Call<List<Experience>> call, Response<List<Experience>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Experience> experiences = response.body();
                            Log.d(TAG, "사용자 경험 조회 성공: " + experiences.size() + "개");
                            callback.onSuccess(experiences);
                        } else {
                            String errorMessage = "경험 조회 실패";
                            if (response.code() == 401) {
                                errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요.";
                                LoginManager.logout(context);
                            }
                            Log.e(TAG, "경험 조회 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Experience>> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "경험 조회 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 사용자 경험 수정
     */
    public void updateUserExperiences(Context context, List<Experience> experiences, ProfileUpdateCallback callback) {
        if (!tokenManager.isLoggedIn()) {
            callback.onError("로그인이 필요합니다.");
            return;
        }
        
        String fullToken = tokenManager.getFullToken();
        if (fullToken == null) {
            callback.onError("토큰이 유효하지 않습니다.");
            return;
        }
        
        ExperienceCreate experienceCreate = new ExperienceCreate(experiences);
        
        RetrofitClient.getInstance()
                .getApiService()
                .updateUserExperiences(fullToken, experienceCreate)
                .enqueue(new Callback<ProfileUpdateResponse>() {
                    @Override
                    public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ProfileUpdateResponse result = response.body();
                            Log.d(TAG, "경험 수정 성공: " + result.getMessage());
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "경험 수정 실패";
                            if (response.code() == 401) {
                                errorMessage = "인증이 만료되었습니다. 다시 로그인해주세요.";
                                LoginManager.logout(context);
                            }
                            Log.e(TAG, "경험 수정 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "경험 수정 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // ===== 콜백 인터페이스들 =====
    
    public interface UserSkillsCallback {
        void onSuccess(UserSkillsResponse skills);
        void onError(String errorMessage);
    }
    
    public interface UserRolesCallback {
        void onSuccess(UserRolesResponse roles);
        void onError(String errorMessage);
    }
    
    public interface UserExperiencesCallback {
        void onSuccess(List<Experience> experiences);
        void onError(String errorMessage);
    }
    
    public interface ProfileUpdateCallback {
        void onSuccess(ProfileUpdateResponse response);
        void onError(String errorMessage);
    }
}
