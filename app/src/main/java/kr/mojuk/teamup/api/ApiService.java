package kr.mojuk.teamup.api;

import kr.mojuk.teamup.api.model.EmailVerificationCode;
import kr.mojuk.teamup.api.model.EmailVerificationRequest;
import kr.mojuk.teamup.api.model.EmailVerificationResponse;
import kr.mojuk.teamup.api.model.Experience;
import kr.mojuk.teamup.api.model.ExperienceCreate;
import kr.mojuk.teamup.api.model.ApplicationCreate;
import kr.mojuk.teamup.api.model.ApplicationResponse;
import kr.mojuk.teamup.api.model.CheckAuthorResponse;
import kr.mojuk.teamup.api.model.CommentCreate;
import kr.mojuk.teamup.api.model.CommentResponse;
import kr.mojuk.teamup.api.model.CommentUpdate;
import kr.mojuk.teamup.api.model.CommentWithReplies;
import kr.mojuk.teamup.api.model.ContestsListResponse;
import kr.mojuk.teamup.api.model.FilterItem;
import kr.mojuk.teamup.api.model.RecruitmentPostDTO;
import kr.mojuk.teamup.api.model.RecruitmentPostRequest;
import kr.mojuk.teamup.api.model.RecruitmentPostResponse;
import kr.mojuk.teamup.api.model.UserActivityResponse;
import kr.mojuk.teamup.api.model.ContestInformation;
import kr.mojuk.teamup.api.model.LoginRequest;
import kr.mojuk.teamup.api.model.LoginResponse;
import kr.mojuk.teamup.api.model.Application;
import kr.mojuk.teamup.api.model.ApplicationStatusUpdate;
import kr.mojuk.teamup.api.model.ApplicationReject;
import kr.mojuk.teamup.api.model.ApiResponse;
import kr.mojuk.teamup.api.model.PersonalityProfileResponse;
import kr.mojuk.teamup.api.model.ProfileUpdateResponse;
import kr.mojuk.teamup.api.model.PersonalityQuestionResponse;
import kr.mojuk.teamup.api.model.PersonalityTestRequest;
import kr.mojuk.teamup.api.model.PersonalityTestResponse;
import kr.mojuk.teamup.api.model.RegistrationStatus;
import kr.mojuk.teamup.api.model.RegistrationStep1;
import kr.mojuk.teamup.api.model.RegistrationStep2;
import kr.mojuk.teamup.api.model.RegistrationStep3;
import kr.mojuk.teamup.api.model.RegistrationStep4;
import kr.mojuk.teamup.api.model.Role;
import kr.mojuk.teamup.api.model.RoleUpdate;
import kr.mojuk.teamup.api.model.Skill;
import kr.mojuk.teamup.api.model.SkillUpdate;
import kr.mojuk.teamup.api.model.StepResponse;
import kr.mojuk.teamup.api.model.SynergyAnalysisRequest;
import kr.mojuk.teamup.api.model.SynergyAnalysisResponse;
import kr.mojuk.teamup.api.model.FcmTokenRequest;
import kr.mojuk.teamup.api.model.UserCreateWithVerification;
import kr.mojuk.teamup.api.model.UserDTO;
import kr.mojuk.teamup.api.model.UserIdCheckRequest;
import kr.mojuk.teamup.api.model.UserIdCheckResponse;
import kr.mojuk.teamup.api.model.UserRolesResponse;
import kr.mojuk.teamup.api.model.UserSkillsResponse;
import kr.mojuk.teamup.api.model.UserUpdateRequest;
import kr.mojuk.teamup.api.model.UserProfileResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
     * 사용자 로그아웃 API
     * POST /api/v1/users/logout
     */
    @POST("api/v1/users/logout")
    Call<ApiResponse> logout();

    @GET("api/v1/contests/")
    Call<ContestsListResponse> getContests();

    // --- 지원 관련 API ---
    // 특정 모집글의 수락된 지원자(팀원) 목록 조회
    @GET("/api/v1/applications/post/{recruitment_post_id}/accepted")
    Call<List<ApplicationResponse>> getAcceptedApplicants(@Path("recruitment_post_id") int postId);

    // --- 모집글 작성/수정 API ---

    @POST("api/v1/recruitments/create")
    Call<RecruitmentPostResponse> createRecruitmentPost(@Body RecruitmentPostRequest postRequest);

    @PUT("api/v1/recruitments/update/{recruitment_post_id}")
    Call<RecruitmentPostResponse> updateRecruitmentPost(
            @Path("recruitment_post_id") int postId,
            @Body RecruitmentPostRequest postRequest
    );

    // 모집 게시글 삭제
    @DELETE("/api/v1/recruitments/delete/{recruitment_post_id}")
    Call<Void> deleteRecruitmentPost(@Path("recruitment_post_id") int postId);



    // 특정 사용자의 활동 내역 (작성글, 참여글)을 조회하는 API

    @GET("api/v1/applications/user/{user_id}/activity")
    Call<UserActivityResponse> getUserActivity(@Path("user_id") String userId);


    //모든 모집 게시글 목록을 조회하는 API (전체 게시판용)
    @GET("api/v1/recruitments/read")
    Call<List<RecruitmentPostDTO>> getAllRecruitmentPosts();


    //특정 공모전에 속한 모집글 목록을 조회하는 API
    @GET("api/v1/recruitments/contest/{contest_id}")
    Call<List<RecruitmentPostDTO>> getRecruitmentPostsByContest(@Path("contest_id") int contestId);

    // 새로 추가된 필터 목록 조회 API
    @GET("api/v1/contests/filters")
    Call<List<FilterItem>> getFilters();

    // 새로 추가된 특정 필터로 공모전 조회 API
    @GET("api/v1/contests/filter/{filter_id}")
    Call<ContestsListResponse> getContestsByFilter(@Path("filter_id") int filterId);

    // ==================== ContestRecruitmentDetailActivity에 필요한 API들 ====================



    // 2. 수락된 팀원 목록 조회 (기존에 정의했지만, 여기서 사용됩니다)
    @GET("api/v1/applications/post/{recruitment_post_id}/accepted")
    Call<List<ApplicationResponse>> getAcceptedApplicationsByPost(@Path("recruitment_post_id") int postId);

    // 3. 댓글 목록 조회
    @GET("api/v1/comments/post/{recruitment_post_id}")
    Call<List<CommentWithReplies>> getCommentsByPost(@Path("recruitment_post_id") int postId);

    // 4. 지원하기
    @POST("api/v1/applications/")
    Call<ApplicationResponse> createApplication(@Body ApplicationCreate applicationData);

    // 5. 작성자 확인
    @GET("api/v1/recruitments/check-author/{recruitment_post_id}")
    Call<CheckAuthorResponse> checkPostAuthor(
            @Path("recruitment_post_id") int postId,
            @Query("user_id") String userId
    );

    // 6. 모집글 삭제
    @DELETE("api/v1/recruitments/delete/{recruitment_post_id}")
    Call<Void> deleteRecruitmentPost(
            @Path("recruitment_post_id") int postId,
            @Query("user_id") String userId
    );

    // 7. 새 댓글/대댓글 작성
    @POST("api/v1/comments/")
    Call<CommentResponse> createComment(@Body CommentCreate commentData);

    /**
     * 기존 댓글을 수정하는 API
     * @param commentId 수정할 댓글의 ID
     * @param commentUpdateData 수정할 내용을 담은 객체
     * @return 수정된 댓글 정보를 담은 Call 객체
     */
    @PUT("api/v1/comments/{comment_id}")
    Call<CommentResponse> updateComment(
            @Path("comment_id") int commentId,
            @Body CommentUpdate commentUpdateData
    );

    /**
     * 기존 댓글을 삭제하는 API
     * @param commentId 삭제할 댓글의 ID
     * @return 성공 여부만 반환하는 Call 객체 (응답 본문 없음)
     */
    @DELETE("api/v1/comments/{comment_id}")
    Call<Void> deleteComment(@Path("comment_id") int commentId);

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
    Call<RecruitmentPostResponse> getRecruitmentPost(@Path("recruitment_post_id") int recruitment_post_id);
    
    /**
     * 공모전 상세 정보 조회 API
     * GET /api/v1/contests/{contest_id}
     */
    @GET("api/v1/contests/{contest_id}")
    Call<ContestInformation> getContestDetail(@Path("contest_id") int contestId);

    @GET("api/v1/contests/latest")
    Call<List<ContestInformation>> getLatestContests();

    @GET("api/v1/recruitments/latest")
    Call<List<RecruitmentPostResponse>> getLatestRecruitments();
    

    
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
    Call<ApiResponse> updateFcmToken(@Body FcmTokenRequest request);



    /**
     * 현재 사용자 정보 조회 API
     * GET /api/v1/users/me
     */
    @GET("api/v1/users/me")
    Call<UserDTO> getCurrentUser(@Header("Authorization") String authorization);

    // ===== 회원가입 관련 API =====

    /**
     * 회원정보 수정 API (이름, 비밀번호)
     * PUT /api/v1/users/mypage
     */
    @PUT("api/v1/users/mypage")
    Call<UserDTO> updateUserInfo(@Header("Authorization") String authorization, @Body UserUpdateRequest userInfo);


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
    Call<StepResponse> completeRegistration(@Query("user_id") String userId);

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

    /**
     * 프로필 - 특정 사용자의 스킬 조회
     * GET /api/v1/profile/skills/{user_id}
     */
    @GET("api/v1/profile/skills/{user_id}")
    Call<UserSkillsResponse> getUserSkillsByUserId(@Path("user_id") String userId);

    /**
     * 프로필 - 특정 사용자의 역할 조회
     * GET /api/v1/profile/roles/{user_id}
     */
    @GET("api/v1/profile/roles/{user_id}")
    Call<UserRolesResponse> getUserRolesByUserId(@Path("user_id") String userId);

    /**
     * 프로필 - 특정 사용자의 공모전 수상 경험 조회
     * GET /api/v1/profile/experiences/{user_id}
     */
    @GET("api/v1/profile/experiences/{user_id}")
    Call<List<Experience>> getUserExperiencesByUserId(@Path("user_id") String userId);

    /**
     * 프로필 - 특정 사용자의 전체 프로필 정보 조회
     * GET /api/v1/profile/{user_id}
     */
    @GET("api/v1/profile/{user_id}")
    Call<UserProfileResponse> getUserProfile(@Path("user_id") String userId);


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
