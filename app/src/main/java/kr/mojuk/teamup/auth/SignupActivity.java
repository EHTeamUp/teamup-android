package kr.mojuk.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kr.mojuk.teamup.R;

import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.EmailVerificationCode;
import kr.mojuk.teamup.api.model.EmailVerificationRequest;
import kr.mojuk.teamup.api.model.EmailVerificationResponse;
import kr.mojuk.teamup.api.model.StepResponse;
import kr.mojuk.teamup.api.model.UserIdCheckRequest;
import kr.mojuk.teamup.api.model.UserIdCheckResponse;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText etId, etPassword, etPasswordConfirm, etName, etEmail, etEmailCode;
    private Button btnIdCheck, btnEmailSend, btnEmailVerify;
    private TextView btnNext;
    private TextView tvIdCheckResult, tvEmailVerifyResult;
    private RegistrationManager registrationManager;

    private boolean isIdAvailable = false;
    private boolean isEmailVerified = false;
    private String verificationCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        registrationManager = RegistrationManager.getInstance();
        initViews();
        setupListeners();
    }

    private void initViews() {
        etId = findViewById(R.id.et_id);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etEmailCode = findViewById(R.id.et_email_code);

        btnIdCheck = findViewById(R.id.btn_id_check);
        btnEmailSend = findViewById(R.id.btn_email_send);
        btnEmailVerify = findViewById(R.id.btn_email_verify);
        btnNext = findViewById(R.id.btn_next);

        tvIdCheckResult = findViewById(R.id.tv_id_check_result);
        tvEmailVerifyResult = findViewById(R.id.tv_email_verify_result);
        
        // 한글 입력 지원 설정
        setupKoreanInputSupport();
    }

    private void setupListeners() {
        btnIdCheck.setOnClickListener(v -> checkUserId());
        btnEmailSend.setOnClickListener(v -> sendEmailVerification());
        btnEmailVerify.setOnClickListener(v -> verifyEmailCode());
        btnNext.setOnClickListener(v -> proceedToNextStep());
    }
    
    private void setupKoreanInputSupport() {
        // 모든 EditText에 한글 입력 지원 설정
        etId.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        etName.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        
        // 이름 필드에 특별히 한글 입력 최적화
        etName.setHint("이름을 입력하세요");
        
        // 한글 입력을 위한 추가 설정
        etName.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_NEXT);
        etName.setSingleLine(true);
        
        // 디버깅을 위한 로그
        Log.d(TAG, "한글 입력 지원 설정 완료");
    }

    private void checkUserId() {
        String userId = etId.getText().toString().trim();
        
        if (userId.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnIdCheck.setEnabled(false);
        btnIdCheck.setText("확인 중...");

        // 실제 API 호출
        UserIdCheckRequest request = new UserIdCheckRequest(userId);
        Call<UserIdCheckResponse> call = RetrofitClient.getInstance().getApiService().checkUserId(request);
        
        call.enqueue(new Callback<UserIdCheckResponse>() {
            @Override
            public void onResponse(Call<UserIdCheckResponse> call, Response<UserIdCheckResponse> response) {
                runOnUiThread(() -> {
                    btnIdCheck.setEnabled(true);
                    btnIdCheck.setText("중복 확인");
                    
                    if (response.isSuccessful() && response.body() != null) {
                        UserIdCheckResponse result = response.body();
                        if (result.isAvailable()) {
                            isIdAvailable = true;
                            tvIdCheckResult.setText("사용 가능한 아이디입니다.");
                            tvIdCheckResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        } else {
                            isIdAvailable = false;
                            tvIdCheckResult.setText("이미 사용 중인 아이디입니다.");
                            tvIdCheckResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        }
                    } else {
                        isIdAvailable = false;
                        tvIdCheckResult.setText("아이디 확인 중 오류가 발생했습니다.");
                        tvIdCheckResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                });
            }

            @Override
            public void onFailure(Call<UserIdCheckResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    btnIdCheck.setEnabled(true);
                    btnIdCheck.setText("중복 확인");
                    isIdAvailable = false;
                    tvIdCheckResult.setText("네트워크 오류가 발생했습니다.");
                    tvIdCheckResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    Log.e(TAG, "아이디 중복 확인 실패", t);
                });
            }
        });
    }

    private void sendEmailVerification() {
        String email = etEmail.getText().toString().trim();
        
        if (email.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // @itc.ac.kr 형식 검증
        if (!email.endsWith("@itc.ac.kr")) {
            Toast.makeText(this, "ITC 학원 이메일(@itc.ac.kr)만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEmailSend.setEnabled(false);
        btnEmailSend.setText("전송 중...");

        EmailVerificationRequest request = new EmailVerificationRequest(email);
        RetrofitClient.getInstance().getApiService().sendEmailVerification(request).enqueue(new Callback<EmailVerificationResponse>() {
            @Override
            public void onResponse(Call<EmailVerificationResponse> call, Response<EmailVerificationResponse> response) {
                runOnUiThread(() -> {
                    btnEmailSend.setEnabled(true);
                    btnEmailSend.setText("인증번호 전송");
                    
                    if (response.isSuccessful() && response.body() != null) {
                        // 실제 인증번호는 이메일로 전송되므로 사용자가 입력한 번호를 사용
                        Toast.makeText(SignupActivity.this, "인증번호가 이메일로 전송되었습니다. 이메일을 확인해주세요.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignupActivity.this, "인증번호 전송에 실패했습니다", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "이메일 인증 전송 실패: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<EmailVerificationResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    btnEmailSend.setEnabled(true);
                    btnEmailSend.setText("인증번호 전송");
                    Toast.makeText(SignupActivity.this, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "이메일 인증 네트워크 오류", t);
                });
            }
        });
    }

    private void verifyEmailCode() {
        String inputCode = etEmailCode.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        
        if (inputCode.isEmpty()) {
            Toast.makeText(this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "이메일을 먼저 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // @itc.ac.kr 형식 검증
        if (!email.endsWith("@itc.ac.kr")) {
            Toast.makeText(this, "인하공전 이메일(@itc.ac.kr)만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        EmailVerificationCode request = new EmailVerificationCode(email, inputCode);
        RetrofitClient.getInstance().getApiService().verifyEmail(request).enqueue(new Callback<EmailVerificationResponse>() {
            @Override
            public void onResponse(Call<EmailVerificationResponse> call, Response<EmailVerificationResponse> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        isEmailVerified = true;
                        tvEmailVerifyResult.setText("이메일 인증이 완료되었습니다.");
                        tvEmailVerifyResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        Toast.makeText(SignupActivity.this, "이메일 인증이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        isEmailVerified = false;
                        tvEmailVerifyResult.setText("인증번호가 일치하지 않습니다.");
                        tvEmailVerifyResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        Toast.makeText(SignupActivity.this, "인증번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<EmailVerificationResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    isEmailVerified = false;
                    tvEmailVerifyResult.setText("인증 확인 중 오류가 발생했습니다.");
                    tvEmailVerifyResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    Toast.makeText(SignupActivity.this, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "이메일 인증 확인 네트워크 오류", t);
                });
            }
        });
    }

    private void proceedToNextStep() {
        if (!validateInputs()) {
            return;
        }

        String userId = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String inputVerificationCode = etEmailCode.getText().toString().trim();
        
        // 디버깅을 위한 로그
        Log.d(TAG, "회원가입 1단계 요청 데이터:");
        Log.d(TAG, "userId: " + userId);
        Log.d(TAG, "name: " + name);
        Log.d(TAG, "email: " + email);
        Log.d(TAG, "inputVerificationCode: " + inputVerificationCode);
        Log.d(TAG, "isEmailVerified: " + isEmailVerified);

        registrationManager.completeStep1(userId, name, email, password, inputVerificationCode, new RegistrationManager.StepCallback() {
            @Override
            public void onSuccess(StepResponse response) {
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "1단계 회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(SignupActivity.this, SignupInterestActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "1단계 회원가입에 실패했습니다: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean validateInputs() {
        String userId = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (userId.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isIdAvailable) {
            Toast.makeText(this, "아이디 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "비밀번호는 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
