package com.example.teamup.api;

import com.example.teamup.api.model.ApplicationCreate;
import com.example.teamup.api.model.ApplicationResponse;
import com.example.teamup.api.model.CheckAuthorResponse;
import com.example.teamup.api.model.CommentCreate;
import com.example.teamup.api.model.CommentResponse;
import com.example.teamup.api.model.CommentUpdate;
import com.example.teamup.api.model.CommentWithReplies;
import com.example.teamup.api.model.ContestsListResponse;
import com.example.teamup.api.model.FilterItem;
import com.example.teamup.api.model.RecruitmentPostDTO;
import com.example.teamup.api.model.RecruitmentPostRequest;
import com.example.teamup.api.model.RecruitmentPostResponse;
import com.example.teamup.api.model.UserActivityResponse;
import com.example.teamup.api.model.UserDTO;
import com.example.teamup.api.model.ContestInformation;
import com.example.teamup.api.model.LoginRequest;
import com.example.teamup.api.model.LoginResponse;
import com.example.teamup.api.model.Application;
import com.example.teamup.api.model.ApplicationStatusUpdate;
import com.example.teamup.api.model.ApplicationReject;
import com.example.teamup.api.model.ApiResponse;
import com.example.teamup.api.model.RecruitmentPostResponse;
import com.example.teamup.api.model.ContestResponse;
import com.example.teamup.api.model.PersonalityQuestionResponse;
import com.example.teamup.api.model.PersonalityTestRequest;
import com.example.teamup.api.model.PersonalityTestResponse;
import com.example.teamup.api.model.SynergyAnalysisRequest;
import com.example.teamup.api.model.SynergyAnalysisResponse;
import com.example.teamup.api.model.FcmTokenRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * FastAPI 서버와 통신하기 위한 Retrofit API 인터페이스
 */
public interface ApiService {
    
    /**
     * 사용자 로그인 API
     * POST /api/v1/users/login
     */
    @POST("api/v1/users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/v1/contests/")
    Call<ContestsListResponse> getContests();

    // 공모전 상세 정보 조회
    @GET("/api/v1/contests/{contest_id}")
    Call<ContestInformation> getContestDetail(@Path("contest_id") int contestId);

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

    // 1. 모집글 상세 정보 조회 (기존에 정의했지만, 여기서 사용됩니다)
    @GET("api/v1/recruitments/{recruitment_post_id}")
    Call<RecruitmentPostResponse> getRecruitmentPost(@Path("recruitment_post_id") int postId);

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
    Call<RecruitmentPostResponse> getRecruitmentPost(@Path("recruitment_post_id") int recruitmentPostId);
    
    /**
     * 공모전 상세 정보 조회 API
     * GET /api/v1/contests/{contest_id}
     */
    @GET("api/v1/contests/{contest_id}")
    Call<ContestResponse> getContestDetail(@Path("contest_id") int contestId);
    
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
}
