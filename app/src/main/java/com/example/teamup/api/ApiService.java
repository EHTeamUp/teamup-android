package com.example.teamup.api;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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
}
