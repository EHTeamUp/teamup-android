package com.example.teamup.api;

import com.example.teamup.model.LoginRequest;
import com.example.teamup.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
}
