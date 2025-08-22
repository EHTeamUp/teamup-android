package com.example.teamup.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 클라이언트 싱글톤 클래스
 */
public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/"; // Android 에뮬레이터에서 로컬 서버 접근
    //실제 디바이스 사용 시: "http://192.168.1.100:8000/" (실제 서버 IP 주소)
    
    private static RetrofitClient instance;
    private Retrofit retrofit;
    private ApiService apiService;
    
    private RetrofitClient() {
        // HTTP 로깅 인터셉터 설정
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // OkHttpClient 타임아웃 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)      
                .readTimeout(60, TimeUnit.SECONDS)         
                .writeTimeout(60, TimeUnit.SECONDS)        
                .build();
        
        // Retrofit 설정
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        // API 서비스 생성
        apiService = retrofit.create(ApiService.class);
    }
    
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    public ApiService getApiService() {
        return apiService;
    }
    
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
