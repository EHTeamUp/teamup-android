package com.example.teamup.api;

import com.example.teamup.api.model.EmailVerificationCode;
import com.example.teamup.api.model.EmailVerificationRequest;
import com.example.teamup.api.model.EmailVerificationResponse;
import com.example.teamup.api.model.Experience;
import com.example.teamup.api.model.ExperienceCreate;
import com.example.teamup.api.model.LoginRequest;
import com.example.teamup.api.model.LoginResponse;
import com.example.teamup.api.model.Application;
import com.example.teamup.api.model.ApplicationStatusUpdate;
import com.example.teamup.api.model.ApplicationReject;
import com.example.teamup.api.model.ApiResponse;
import com.example.teamup.api.model.PersonalityProfileResponse;
import com.example.teamup.api.model.ProfileUpdateResponse;
import com.example.teamup.api.model.RecruitmentPostResponse;
import com.example.teamup.api.model.ContestResponse;
import com.example.teamup.api.model.PersonalityQuestionResponse;
import com.example.teamup.api.model.PersonalityTestRequest;
import com.example.teamup.api.model.PersonalityTestResponse;
import com.example.teamup.api.model.RegistrationStatus;
import com.example.teamup.api.model.RegistrationStep1;
import com.example.teamup.api.model.RegistrationStep2;
import com.example.teamup.api.model.RegistrationStep3;
import com.example.teamup.api.model.RegistrationStep4;
import com.example.teamup.api.model.Role;
import com.example.teamup.api.model.RoleUpdate;
import com.example.teamup.api.model.Skill;
import com.example.teamup.api.model.SkillUpdate;
import com.example.teamup.api.model.StepResponse;
import com.example.teamup.api.model.SynergyAnalysisRequest;
import com.example.teamup.api.model.SynergyAnalysisResponse;
import com.example.teamup.api.model.FcmTokenRequest;
import com.example.teamup.api.model.UserCreateWithVerification;
import com.example.teamup.api.model.UserIdCheckRequest;
import com.example.teamup.api.model.UserIdCheckResponse;
import com.example.teamup.api.model.UserRolesResponse;
import com.example.teamup.api.model.UserSkillsResponse;
import com.example.teamup.api.model.UserUpdateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * FastAPI 서버와 통신하기 위한 Retrofit API 인터페이스
 */
public interface ApiService {
    
    // ===== 인증 관련 API =====

    /**
     * 사용자 로그인 API
     * POST /api/v1/users/login
     */
    @POST("api/v1/users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    /**
     * 특정 게시글의 지원자 목록 조회 API
     * GET /api/v1/applications/post/{recruitment_post_id}
     */
    @GET("api/v1/applications/post/{recruitment_post_id}")
    Call<List<Application>> getApplicationsByPost(@Path("recruitment_post_id") int recruitmentPostId);

    /**
     * 지원자 수락 API
     * PUT /api/v1/applications/accept
     */
    @PUT("api/v1/applications/accept")
    Call<ApiResponse> acceptApplications(@Body ApplicationStatusUpdate statusUpdate);

    /**
     * 지원자 거절 API
     * PUT /api/v1/applications/reject
     */
    @PUT("api/v1/applications/reject")
    Call<ApiResponse> rejectApplication(@Body ApplicationReject rejectData);

    /**
     * 특정 모집 게시글 조회 API
     * GET /api/v1/recruitments/{recruitment_post_id}
     */
    @GET("api/v1/recruitments/{recruitment_post_id}")
    Call<RecruitmentPostResponse> getRecruitmentPost(@Path("recruitment_post_id") int recruitmentPostId);

    /**
     * 공모전 상세 정보 조회 API
     * GET /api/v1/contests/{contest_id}
     */
    @GET("api/v1/contests/{contest_id}")
    Call<ContestResponse> getContestDetail(@Path("contest_id") int contestId);

    /**
     * 시너지 분석 API
     * POST /api/v1/synergy/analyze
     */
    @POST("api/v1/synergy/analyze")
    Call<SynergyAnalysisResponse> analyzeSynergy(@Body SynergyAnalysisRequest request);

    /**
     * FCM 토큰 업데이트 API
     * PUT /api/v1/notifications/fcm-token
     */
    @PUT("api/v1/notifications/fcm-token")
    Call<String> updateFcmToken(@Body FcmTokenRequest request);

    /**
     * 현재 사용자 정보 조회 API
     * GET /api/v1/users/me
     */
    @GET("api/v1/users/me")
    Call<LoginRequest> getCurrentUser(@Header("Authorization") String authorization);

    /**
     * 회원정보 수정 API (이름, 비밀번호)
     * PUT /api/v1/users/mypage
     */
    @PUT("api/v1/users/mypage")
    Call<LoginRequest> updateUserInfo(@Header("Authorization") String authorization, @Body UserUpdateRequest userInfo);

    // ===== 회원가입 관련 API =====

    /**
     * 이메일 인증번호 발송
     * POST /api/v1/registration/send-verification
     */
    @POST("api/v1/registration/send-verification")
    Call<EmailVerificationResponse> sendEmailVerification(@Body EmailVerificationRequest request);

    /**
     * 이메일 인증번호 검증
     * POST /api/v1/registration/verify-email
     */
    @POST("api/v1/registration/verify-email")
    Call<EmailVerificationResponse> verifyEmail(@Body EmailVerificationCode request);

    /**
     * 사용자 ID 중복 검사
     * POST /api/v1/registration/check-userid
     */
    @POST("api/v1/registration/check-userid")
    Call<UserIdCheckResponse> checkUserId(@Body UserIdCheckRequest request);

    /**
     * 회원가입 (이메일 인증 필요)
     * POST /api/v1/registration/register
     */
    @POST("api/v1/registration/register")
    Call<LoginRequest> register(@Body UserCreateWithVerification user);

    /**
     * 사용 가능한 스킬 목록 조회
     * GET /api/v1/registration/skills
     */
    @GET("api/v1/registration/skills")
    Call<List<Skill>> getAvailableSkills();

    /**
     * 사용 가능한 역할 목록 조회
     * GET /api/v1/registration/roles
     */
    @GET("api/v1/registration/roles")
    Call<List<Role>> getAvailableRoles();

    /**
     * 회원가입 1단계: 기본 정보 + 이메일 인증
     * POST /api/v1/registration/step1
     */
    @POST("api/v1/registration/step1")
    Call<StepResponse> completeStep1(@Body RegistrationStep1 step1);

    /**
     * 회원가입 2단계: 스킬 + 역할 선택
     * POST /api/v1/registration/step2
     */
    @POST("api/v1/registration/step2")
    Call<StepResponse> completeStep2(@Body RegistrationStep2 step2);

    /**
     * 회원가입 3단계: 공모전 수상 경험
     * POST /api/v1/registration/step3
     */
    @POST("api/v1/registration/step3")
    Call<StepResponse> completeStep3(@Body RegistrationStep3 step3);

    /**
     * 회원가입 4단계: 성향테스트 (선택사항)
     * POST /api/v1/registration/step4
     */
    @POST("api/v1/registration/step4")
    Call<StepResponse> completeStep4(@Body RegistrationStep4 step4);

    /**
     * 전체 회원가입 완료
     * POST /api/v1/registration/complete
     */
    @POST("api/v1/registration/complete")
    Call<StepResponse> completeRegistration(@Body String userId);

    /**
     * 회원가입 진행 상태 확인
     * GET /api/v1/registration/status/{user_id}
     */
    @GET("api/v1/registration/status/{user_id}")
    Call<RegistrationStatus> getRegistrationStatus(@Path("user_id") String userId);


    // ===== 프로필 관련 API =====

    /**
     * 프로필 - 스킬 수정
     * PUT /api/v1/profile/skills
     */
    @PUT("api/v1/profile/skills")
    Call<ProfileUpdateResponse> updateUserSkills(@Header("Authorization") String authorization, @Body SkillUpdate skillUpdate);

    /**
     * 프로필 - 역할 수정
     * PUT /api/v1/profile/roles
     */
    @PUT("api/v1/profile/roles")
    Call<ProfileUpdateResponse> updateUserRoles(@Header("Authorization") String authorization, @Body RoleUpdate roleUpdate);

    /**
     * 프로필 - 공모전 수상 경험 수정
     * PUT /api/v1/profile/experiences
     */
    @PUT("api/v1/profile/experiences")
    Call<ProfileUpdateResponse> updateUserExperiences(@Header("Authorization") String authorization, @Body ExperienceCreate experienceCreate);

    /**
     * 프로필 - 현재 사용자의 스킬 조회
     * GET /api/v1/profile/skills
     */
    @GET("api/v1/profile/skills")
    Call<UserSkillsResponse> getUserSkills(@Header("Authorization") String authorization);

    /**
     * 프로필 - 현재 사용자의 역할 조회
     * GET /api/v1/profile/roles
     */
    @GET("api/v1/profile/roles")
    Call<UserRolesResponse> getUserRoles(@Header("Authorization") String authorization);

    /**
     * 프로필 - 현재 사용자의 공모전 수상 경험 조회
     * GET /api/v1/profile/experiences
     */
    @GET("api/v1/profile/experiences")
    Call<List<Experience>> getUserExperiences(@Header("Authorization") String authorization);


    // ===== 성향 테스트 관련 API =====

    /**
     * 성향 테스트 질문 조회 API
     * GET /api/v1/personality/questions
     */
    @GET("api/v1/personality/questions")
    Call<PersonalityQuestionResponse> getPersonalityQuestions();

    /**
     * 성향 테스트 제출 API
     * POST /api/v1/personality/test
     */
    @POST("api/v1/personality/test")
    Call<PersonalityTestResponse> submitPersonalityTest(@Body PersonalityTestRequest request);

    @GET("api/v1/personality/user-profile/{user_id}")
    Call<PersonalityProfileResponse> getUserPersonalityProfile(@Path("user_id") String userId);



}
