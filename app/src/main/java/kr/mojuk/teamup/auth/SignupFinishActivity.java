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
        
        // 회원가입 완료 API 호출
        completeRegistration();
    }

    private void initViews() {
        btnGoToLogin = findViewById(R.id.btn_go_to_login);
        
        // 회원가입 완료 시 FCM 토큰 초기화
        FcmTokenManager.getInstance(this).clearFcmTokenOnSignup();
        
        btnGoToLogin.setOnClickListener(v -> {
            Toast.makeText(this, "로그인 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
            
            // LoginActivity로 이동
            Intent intent = new Intent(SignupFinishActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    /**
     * 회원가입 완료 API 호출
     */
    private void completeRegistration() {
        Log.d(TAG, "회원가입 완료 API 호출: userId=" + userId);
        
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
                            String errorMessage = "회원가입 완료 처리 중 오류가 발생했습니다.";
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "오류 응답: " + errorBody);
                                    errorMessage += " (" + errorBody + ")";
                                } catch (Exception e) {
                                    Log.e(TAG, "오류 응답 읽기 실패", e);
                                }
                            }
                            Toast.makeText(SignupFinishActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
