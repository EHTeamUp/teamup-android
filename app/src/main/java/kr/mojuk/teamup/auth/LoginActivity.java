package kr.mojuk.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import kr.mojuk.teamup.MainActivity;
import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.LoginRequest;
import kr.mojuk.teamup.api.model.LoginResponse;
import kr.mojuk.teamup.notification.FcmTokenManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    
    private EditText etId, etPassword;
    private MaterialButton btnLogin, btnSignIn;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // TokenManager 초기화
        tokenManager = TokenManager.getInstance(this);

        // 뷰 초기화
        etId = findViewById(R.id.et_id);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignIn = findViewById(R.id.btn_sign_in);

        // 버튼 클릭 리스너 설정
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면 이동 (Activity 기반)
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 로그인 수행
     */
    private void performLogin() {
        String id = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // 입력값 검증
        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.please_enter_id_password, Toast.LENGTH_SHORT).show();
            return;
        }

        // 로그인 버튼 비활성화
        btnLogin.setEnabled(false);
        btnLogin.setText("Login...");

        // API를 통한 로그인 요청
        loginWithAPI(id, password);
    }

    /**
     * FastAPI 서버를 통한 로그인 요청
     */
    private void loginWithAPI(String userId, String password) {
        // 로그인 요청 데이터 생성
        LoginRequest loginRequest = new LoginRequest(userId, password);
        
        // Retrofit을 통한 API 호출
        RetrofitClient.getInstance()
                .getApiService()
                .login(loginRequest)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        // 로그인 버튼 다시 활성화
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                        
                        if (response.isSuccessful() && response.body() != null) {
                            // 로그인 성공
                            LoginResponse loginResponse = response.body();
                            
                            // 토큰 저장 (JWT에서 사용자 정보 자동 추출)
                            tokenManager.saveToken(
                                loginResponse.getAccessToken(), 
                                loginResponse.getTokenType()
                            );
                            
                            // JWT에서 사용자 ID 추출
                            String userId = JwtUtils.getUserIdFromToken(loginResponse.getAccessToken());
                            
                            Log.d(TAG, "로그인 성공: " + userId);
                            Log.d(TAG, "저장된 JWT 토큰: " + loginResponse.getAccessToken().substring(0, Math.min(20, loginResponse.getAccessToken().length())) + "...");
                            Log.d(TAG, "TokenManager 로그인 상태: " + tokenManager.isLoggedIn());
                            Log.d(TAG, "TokenManager에서 가져온 토큰: " + (tokenManager.getAccessToken() != null ? tokenManager.getAccessToken().substring(0, Math.min(20, tokenManager.getAccessToken().length())) + "..." : "null"));
                            
                            // 로그인 상태 업데이트
                            LoginManager.setLoggedIn(true);
                            
                            // FCM 토큰을 서버에 전송
                            FcmTokenManager.getInstance(LoginActivity.this).sendFcmTokenOnLogin();
                            
                            // 성공 메시지 표시
                            Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                            
                            // MainActivity로 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            
                        } else {
                            // 로그인 실패 (서버 응답은 있지만 실패)
                            String errorMessage = "로그인 실패";
                            if (response.code() == 401) {
                                errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";
                            } else if (response.code() == 404) {
                                errorMessage = "서버를 찾을 수 없습니다.";
                            } else if (response.code() >= 500) {
                                errorMessage = "서버 오류가 발생했습니다.";
                            }
                            
                            Log.e(TAG, "로그인 실패 - HTTP " + response.code() + ": " + errorMessage);
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // 로그인 버튼 다시 활성화
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                        
                        // 네트워크 오류 처리
                        String errorMessage = "네트워크 오류가 발생했습니다.";
                        if (t.getMessage() != null) {
                            if (t.getMessage().contains("Failed to connect")) {
                                errorMessage = "서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.";
                            } else if (t.getMessage().contains("timeout")) {
                                errorMessage = "요청 시간이 초과되었습니다.";
                            }
                        }
                        
                        Log.e(TAG, "로그인 네트워크 오류: " + t.getMessage(), t);
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
} 