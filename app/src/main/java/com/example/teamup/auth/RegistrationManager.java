package com.example.teamup.auth;

import android.content.Context;
import android.util.Log;

import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 회원가입 과정을 관리하는 클래스
 * 단계적 회원가입과 이메일 인증을 처리
 */
public class RegistrationManager {
    
    private static final String TAG = "RegistrationManager";
    private static RegistrationManager instance;
    
    private RegistrationManager() {}
    
    public static synchronized RegistrationManager getInstance() {
        if (instance == null) {
            instance = new RegistrationManager();
        }
        return instance;
    }
    
    // ===== 이메일 인증 관련 =====
    
    /**
     * 이메일 인증번호 발송
     */
    public void sendEmailVerification(String email, EmailVerificationCallback callback) {
        EmailVerificationRequest request = new EmailVerificationRequest(email);
        
        RetrofitClient.getInstance()
                .getApiService()
                .sendEmailVerification(request)
                .enqueue(new Callback<EmailVerificationResponse>() {
                    @Override
                    public void onResponse(Call<EmailVerificationResponse> call, Response<EmailVerificationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "이메일 인증번호 발송 성공: " + response.body().getMessage());
                            callback.onSuccess(response.body().getMessage());
                        } else {
                            String errorMessage = "이메일 인증번호 발송 실패";
                            if (response.code() == 400) {
                                errorMessage = "올바른 이메일 주소를 입력해주세요.";
                            } else if (response.code() >= 500) {
                                errorMessage = "서버 오류가 발생했습니다.";
                            }
                            Log.e(TAG, "이메일 인증번호 발송 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<EmailVerificationResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "이메일 인증번호 발송 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 이메일 인증번호 검증
     */
    public void verifyEmail(String email, String verificationCode, EmailVerificationCallback callback) {
        EmailVerificationCode request = new EmailVerificationCode(email, verificationCode);
        
        RetrofitClient.getInstance()
                .getApiService()
                .verifyEmail(request)
                .enqueue(new Callback<EmailVerificationResponse>() {
                    @Override
                    public void onResponse(Call<EmailVerificationResponse> call, Response<EmailVerificationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "이메일 인증 성공: " + response.body().getMessage());
                            callback.onSuccess(response.body().getMessage());
                        } else {
                            String errorMessage = "이메일 인증 실패";
                            if (response.code() == 400) {
                                errorMessage = "인증번호가 올바르지 않거나 만료되었습니다.";
                            }
                            Log.e(TAG, "이메일 인증 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<EmailVerificationResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "이메일 인증 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // ===== 사용자 ID 중복 검사 =====
    
    /**
     * 사용자 ID 중복 검사
     */
    public void checkUserId(String userId, UserIdCheckCallback callback) {
        UserIdCheckRequest request = new UserIdCheckRequest(userId);
        
        RetrofitClient.getInstance()
                .getApiService()
                .checkUserId(request)
                .enqueue(new Callback<UserIdCheckResponse>() {
                    @Override
                    public void onResponse(Call<UserIdCheckResponse> call, Response<UserIdCheckResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserIdCheckResponse result = response.body();
                            Log.d(TAG, "사용자 ID 중복 검사 완료: " + result.getMessage());
                            callback.onResult(result.isAvailable(), result.getMessage());
                        } else {
                            String errorMessage = "사용자 ID 중복 검사 실패";
                            Log.e(TAG, "사용자 ID 중복 검사 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserIdCheckResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "사용자 ID 중복 검사 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // ===== 스킬/역할 목록 조회 =====
    
    /**
     * 사용 가능한 스킬 목록 조회
     */
    public void getAvailableSkills(SkillsCallback callback) {
        RetrofitClient.getInstance()
                .getApiService()
                .getAvailableSkills()
                .enqueue(new Callback<List<Skill>>() {
                    @Override
                    public void onResponse(Call<List<Skill>> call, Response<List<Skill>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "스킬 목록 조회 성공: " + response.body().size() + "개");
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "스킬 목록 조회 실패";
                            Log.e(TAG, "스킬 목록 조회 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Skill>> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "스킬 목록 조회 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 사용 가능한 역할 목록 조회
     */
    public void getAvailableRoles(RolesCallback callback) {
        RetrofitClient.getInstance()
                .getApiService()
                .getAvailableRoles()
                .enqueue(new Callback<List<Role>>() {
                    @Override
                    public void onResponse(Call<List<Role>> call, Response<List<Role>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "역할 목록 조회 성공: " + response.body().size() + "개");
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage = "역할 목록 조회 실패";
                            Log.e(TAG, "역할 목록 조회 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Role>> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "역할 목록 조회 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // ===== 단계적 회원가입 =====
    
    /**
     * 회원가입 1단계: 기본 정보 + 이메일 인증
     */
    public void completeStep1(String userId, String name, String email, String password, String verificationCode, StepCallback callback) {
        RegistrationStep1 step1 = new RegistrationStep1(userId, name, email, password, verificationCode);
        
        RetrofitClient.getInstance()
                .getApiService()
                .completeStep1(step1)
                .enqueue(new Callback<StepResponse>() {
                    @Override
                    public void onResponse(Call<StepResponse> call, Response<StepResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StepResponse result = response.body();
                            Log.d(TAG, "1단계 완료: " + result.getMessage());
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "1단계 완료 실패";
                            if (response.code() == 400) {
                                errorMessage = "입력 정보가 올바르지 않습니다.";
                            }
                            Log.e(TAG, "1단계 완료 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<StepResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "1단계 완료 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 회원가입 2단계: 스킬 + 역할 선택
     */
    public void completeStep2(String userId, List<Integer> skillIds, List<String> customSkills, List<Integer> roleIds, List<String> customRoles, StepCallback callback) {
        RegistrationStep2 step2 = new RegistrationStep2(userId, skillIds, customSkills, roleIds, customRoles);
        
        RetrofitClient.getInstance()
                .getApiService()
                .completeStep2(step2)
                .enqueue(new Callback<StepResponse>() {
                    @Override
                    public void onResponse(Call<StepResponse> call, Response<StepResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StepResponse result = response.body();
                            Log.d(TAG, "2단계 완료: " + result.getMessage());
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "2단계 완료 실패";
                            if (response.code() == 400) {
                                errorMessage = "스킬과 역할을 최소 1개 이상 선택해주세요.";
                            }
                            Log.e(TAG, "2단계 완료 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<StepResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "2단계 완료 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 회원가입 3단계: 공모전 수상 경험
     */
    public void completeStep3(String userId, List<Experience> experiences, StepCallback callback) {
        RegistrationStep3 step3 = new RegistrationStep3(userId, experiences);
        
        RetrofitClient.getInstance()
                .getApiService()
                .completeStep3(step3)
                .enqueue(new Callback<StepResponse>() {
                    @Override
                    public void onResponse(Call<StepResponse> call, Response<StepResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StepResponse result = response.body();
                            Log.d(TAG, "3단계 완료: " + result.getMessage());
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "3단계 완료 실패";
                            Log.e(TAG, "3단계 완료 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<StepResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "3단계 완료 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 회원가입 4단계: 성향테스트 (선택사항)
     */
    public void completeStep4(String userId, boolean skipPersonalityTest, Object personalityResults, StepCallback callback) {
        RegistrationStep4 step4 = new RegistrationStep4(userId, skipPersonalityTest, personalityResults);
        
        RetrofitClient.getInstance()
                .getApiService()
                .completeStep4(step4)
                .enqueue(new Callback<StepResponse>() {
                    @Override
                    public void onResponse(Call<StepResponse> call, Response<StepResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StepResponse result = response.body();
                            Log.d(TAG, "4단계 완료: " + result.getMessage());
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "4단계 완료 실패";
                            Log.e(TAG, "4단계 완료 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<StepResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "4단계 완료 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 전체 회원가입 완료
     */
    public void completeRegistration(String userId, StepCallback callback) {
        RetrofitClient.getInstance()
                .getApiService()
                .completeRegistration(userId)
                .enqueue(new Callback<StepResponse>() {
                    @Override
                    public void onResponse(Call<StepResponse> call, Response<StepResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StepResponse result = response.body();
                            Log.d(TAG, "회원가입 완료: " + result.getMessage());
                            callback.onSuccess(result);
                        } else {
                            String errorMessage = "회원가입 완료 실패";
                            Log.e(TAG, "회원가입 완료 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<StepResponse> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "회원가입 완료 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    /**
     * 회원가입 진행 상태 확인
     */
    public void getRegistrationStatus(String userId, RegistrationStatusCallback callback) {
        RetrofitClient.getInstance()
                .getApiService()
                .getRegistrationStatus(userId)
                .enqueue(new Callback<RegistrationStatus>() {
                    @Override
                    public void onResponse(Call<RegistrationStatus> call, Response<RegistrationStatus> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RegistrationStatus status = response.body();
                            Log.d(TAG, "회원가입 상태 조회: " + status.getCurrentStep() + "단계");
                            callback.onSuccess(status);
                        } else {
                            String errorMessage = "회원가입 상태 조회 실패";
                            Log.e(TAG, "회원가입 상태 조회 실패 - HTTP " + response.code());
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<RegistrationStatus> call, Throwable t) {
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        Log.e(TAG, "회원가입 상태 조회 네트워크 오류: " + t.getMessage(), t);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // ===== 콜백 인터페이스들 =====
    
    public interface EmailVerificationCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }
    
    public interface UserIdCheckCallback {
        void onResult(boolean available, String message);
        void onError(String errorMessage);
    }
    
    public interface SkillsCallback {
        void onSuccess(List<Skill> skills);
        void onError(String errorMessage);
    }
    
    public interface RolesCallback {
        void onSuccess(List<Role> roles);
        void onError(String errorMessage);
    }
    
    public interface StepCallback {
        void onSuccess(StepResponse response);
        void onError(String errorMessage);
    }
    
    public interface RegistrationStatusCallback {
        void onSuccess(RegistrationStatus status);
        void onError(String errorMessage);
    }
}
