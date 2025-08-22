package kr.mojuk.teamup.api;

import android.content.Context;
import java.util.concurrent.TimeUnit;

import kr.mojuk.teamup.auth.TokenManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

/**
 * Retrofit 클라이언트 싱글톤 클래스
 */
public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/"; // Android 에뮬레이터에서 로컬 서버 접근
    //실제 디바이스 사용 시: "http://192.168.1.100:8000/" (실제 서버 IP 주소)
    
    private static RetrofitClient instance;
    private static RetrofitClient instanceWithAuth;
    private Retrofit retrofit;
    private ApiService apiService;
    private Context context;
    
    private RetrofitClient(Context context) {
        this.context = context.getApplicationContext();
        
        // JWT 토큰 인터셉터
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                TokenManager tokenManager = TokenManager.getInstance(RetrofitClient.this.context);
                Request original = chain.request();
                
                android.util.Log.d("RetrofitClient", "🔍 JWT 인터셉터 실행 - URL: " + original.url());
                android.util.Log.d("RetrofitClient", "로그인 상태: " + tokenManager.isLoggedIn());
                
                // JWT 토큰이 있는 경우 헤더에 추가
                if (tokenManager.isLoggedIn()) {
                    String token = tokenManager.getAccessToken();
                    android.util.Log.d("RetrofitClient", "가져온 토큰: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
                    
                    if (token != null && !token.trim().isEmpty()) {
                        Request authorized = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                        android.util.Log.d("RetrofitClient", "✅ JWT 토큰을 Authorization 헤더에 추가했습니다");
                        return chain.proceed(authorized);
                    } else {
                        android.util.Log.w("RetrofitClient", "⚠️ 토큰이 null이거나 빈 값입니다");
                    }
                } else {
                    android.util.Log.w("RetrofitClient", "⚠️ 로그인되지 않은 상태입니다");
                }
                
                android.util.Log.d("RetrofitClient", "❌ JWT 토큰 없이 요청을 진행합니다");
                return chain.proceed(original);
            }
        };
        
        // HTTP 로깅 인터셉터 설정
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // OkHttpClient 타임아웃 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)  // JWT 토큰 인터셉터 추가
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
    
    public static synchronized RetrofitClient getInstance(Context context) {
        if (instanceWithAuth == null) {
            instanceWithAuth = new RetrofitClient(context);
        }
        return instanceWithAuth;
    }
    
    // 기존 코드와의 호환성을 위한 메서드 (JWT 토큰 없이 사용)
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            // Context 없이 초기화 (JWT 토큰 인터셉터 없음)
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    // JWT 토큰 없이 사용하는 생성자
    private RetrofitClient() {
        // HTTP 로깅 인터셉터 설정
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // OkHttpClient 타임아웃 설정 (JWT 인터셉터 없음)
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
    
    public ApiService getApiService() {
        return apiService;
    }
    
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
