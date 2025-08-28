package kr.mojuk.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.StepResponse;
import kr.mojuk.teamup.notification.FcmTokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupFinishActivity extends AppCompatActivity {

    private static final String TAG = "SignupFinishActivity";
    private Button btnGoToLogin;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_finish);

        // userId 받기
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        
        // 회원가입 완료 API 호출 (성향 프로필 생성 시간 확보를 위해 지연)
        new android.os.Handler().postDelayed(() -> {
            completeRegistration();
        }, 1000); // 1초 대기
    }

    private void initViews() {
        btnGoToLogin = findViewById(R.id.btn_go_to_login);
        
        // 회원가입 완료 시 FCM 토큰 초기화
        FcmTokenManager.getInstance(this).clearFcmTokenOnSignup();
        
        btnGoToLogin.setOnClickListener(v -> {
//            Toast.makeText(this, "로그인 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
            
            // LoginActivity로 이동
            Intent intent = new Intent(SignupFinishActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 3;
    
    /**
     * 회원가입 완료 API 호출
     */
    private void completeRegistration() {
        Log.d(TAG, "회원가입 완료 API 호출: userId=" + userId + " (시도 " + (retryCount + 1) + "/" + MAX_RETRY_COUNT + ")");
        
        RetrofitClient.getInstance()
                .getApiService()
                .completeRegistration(userId)
                .enqueue(new Callback<StepResponse>() {
                    @Override
                    public void onResponse(Call<StepResponse> call, Response<StepResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StepResponse result = response.body();
                            Log.d(TAG, "회원가입 완료 성공: " + result.getMessage());
                            Toast.makeText(SignupFinishActivity.this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "회원가입 완료 실패 - HTTP " + response.code());
                            String errorMessage = "";
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "오류 응답: " + errorBody);
                                    errorMessage = errorBody;
                                } catch (Exception e) {
                                    Log.e(TAG, "오류 응답 읽기 실패", e);
                                }
                            }
                            
                            // "No matching personality profile found" 오류 시 재시도
                            if (errorMessage.contains("No matching personality profile found") && retryCount < MAX_RETRY_COUNT) {
                                retryCount++;
                                Log.d(TAG, "성향 프로필 생성 대기 중... " + (retryCount * 2) + "초 후 재시도");
                                
                                new android.os.Handler().postDelayed(() -> {
                                    completeRegistration();
                                }, retryCount * 2000); // 2초, 4초, 6초 후 재시도
                            } else {
                                String finalErrorMessage = "회원가입 완료 처리 중 오류가 발생했습니다.";
                                if (!errorMessage.isEmpty()) {
                                    finalErrorMessage += " (" + errorMessage + ")";
                                }
                                Toast.makeText(SignupFinishActivity.this, finalErrorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StepResponse> call, Throwable t) {
                        Log.e(TAG, "회원가입 완료 네트워크 오류: " + t.getMessage(), t);
                        Toast.makeText(SignupFinishActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
