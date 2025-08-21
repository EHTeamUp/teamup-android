package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.PersonalityProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupTestBaseActivity extends AppCompatActivity {

    private static final String TAG = "SignupTestBaseActivity";
    
    private String userId;
    private TextView btnPrevious, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_test_base);
        
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
    }
    
    private void initViews() {
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        
        btnPrevious.setOnClickListener(v -> goToPreviousStep());
        btnNext.setOnClickListener(v -> proceedToNextStep());
    }
    
    private void proceedToNextStep() {
        // 사용자의 성향 테스트 완료 여부 확인
        checkPersonalityTestCompletion();
    }
    
    private void checkPersonalityTestCompletion() {
        RetrofitClient.getInstance()
                .getApiService()
                .getUserPersonalityProfile(userId)
                .enqueue(new Callback<PersonalityProfileResponse>() {
                    @Override
                    public void onResponse(Call<PersonalityProfileResponse> call, Response<PersonalityProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // 성향 테스트가 완료된 경우: 결과 화면으로 이동
                            PersonalityProfileResponse profile = response.body();
                            Log.d(TAG, "성향 테스트 완료됨: " + profile.getProfileCode());
                            
                            Intent intent = new Intent(SignupTestBaseActivity.this, com.example.teamup.personality.PersonalityTestResultActivity.class);
                            intent.putExtra("personalityType", profile.getProfileCode());
                            intent.putExtra("personalityTraits", profile.getTraitsJson());
                            intent.putExtra("userId", userId);
                            intent.putExtra("fromSignup", true);
                            startActivity(intent);
                        } else {
                            // 성향 테스트가 완료되지 않은 경우: 테스트 시작 화면으로 이동
                            Log.d(TAG, "성향 테스트 미완료: " + response.code());
                            
                            Intent intent = new Intent(SignupTestBaseActivity.this, com.example.teamup.personality.MainPersonalityTestActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("fromSignup", true);
                            startActivity(intent);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<PersonalityProfileResponse> call, Throwable t) {
                        Log.e(TAG, "성향 테스트 완료 여부 확인 실패: " + t.getMessage());
                        Toast.makeText(SignupTestBaseActivity.this, "테스트를 모두 진행해야 해요.", Toast.LENGTH_SHORT).show();
                        
                        // 네트워크 오류 시에도 테스트 시작 화면으로 이동
                        Intent intent = new Intent(SignupTestBaseActivity.this, com.example.teamup.personality.MainPersonalityTestActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("fromSignup", true);
                        startActivity(intent);
                    }
                });
    }
    
    private void goToPreviousStep() {
        finish(); // 이전 Activity로 돌아가기
    }
}
