package kr.mojuk.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.PersonalityProfileResponse;
import kr.mojuk.teamup.personality.PersonalityTestQuestionFragment;
import kr.mojuk.teamup.personality.PersonalityTestResultActivity;
import kr.mojuk.teamup.personality.PersonalityTestResultFragment;
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

        // 기본적으로 성향 테스트 시작 화면 표시
        showPersonalityTestFragment();
    }

    private void initViews() {
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);

        btnPrevious.setOnClickListener(v -> goToPreviousStep());
        btnNext.setOnClickListener(v -> proceedToNextStep());

        // 초기에는 Next 버튼 숨기기 (테스트 결과 페이지가 아니므로)
        updateNextButtonVisibility();
    }

    /**
     * 현재 Fragment에 따라 Next 버튼 표시/숨김 제어
     */
    private void updateNextButtonVisibility() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof PersonalityTestResultFragment) {
            // 테스트 결과 페이지인 경우 Next 버튼 표시
            btnNext.setVisibility(View.VISIBLE);
        } else {
            // 테스트 결과 페이지가 아닌 경우 Next 버튼 숨기기
            btnNext.setVisibility(View.GONE);
        }
    }

    private void proceedToNextStep() {
        Intent intent = new Intent(SignupTestBaseActivity.this, SignupFinishActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
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

                            Intent intent = new Intent(SignupTestBaseActivity.this, PersonalityTestResultActivity.class);
                            intent.putExtra("personalityType", profile.getProfileCode());
                            intent.putExtra("personalityTraits", profile.getTraitsJson());
                            intent.putExtra("userId", userId);
                            intent.putExtra("fromSignup", true);
                            startActivity(intent);
                        } else {
                            // 성향 테스트가 완료되지 않은 경우: Fragment로 테스트 시작 화면 표시
                            Log.d(TAG, "성향 테스트 미완료: " + response.code());
                            showPersonalityTestFragment();
                        }
                    }

                    @Override
                    public void onFailure(Call<PersonalityProfileResponse> call, Throwable t) {
                        Log.e(TAG, "성향 테스트 완료 여부 확인 실패: " + t.getMessage());
                        Toast.makeText(SignupTestBaseActivity.this, "테스트를 모두 진행해야 해요.", Toast.LENGTH_SHORT).show();

                        // 네트워크 오류 시에도 Fragment로 테스트 시작 화면 표시
                        showPersonalityTestFragment();
                    }
                });
    }

    /**
     * 성향 테스트 시작 화면 표시
     */
    private void showPersonalityTestFragment() {
        // MainPersonalityTestActivity를 Fragment로 표시하기 위해 View 생성
        View personalityTestView = LayoutInflater.from(this).inflate(R.layout.fragment_main_personality_test, null);

        // 시작 버튼 클릭 리스너 설정
        MaterialButton btnStart = personalityTestView.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> {
            // PersonalityTestQuestionFragment로 교체
            showPersonalityTestQuestionFragment();
        });

        // Fragment 컨테이너에 View 추가
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.removeAllViews();
        fragmentContainer.addView(personalityTestView);

        // Next 버튼 숨기기 (테스트 시작 화면이므로)
        updateNextButtonVisibility();
    }

    private void goToPreviousStep() {
        finish(); // 이전 Activity로 돌아가기
    }

    /**
     * 성향 테스트 완료 시 호출되는 메서드 (Fragment에서 호출)
     */
    public void onPersonalityTestCompleted(String personalityType, String personalityTraits) {
        // 성향 테스트 완료 후 잠시 대기 (백엔드에서 성향 프로필 생성 시간 확보)
        Log.d(TAG, "성향 테스트 완료, 2초 후 결과 화면으로 이동");

        new android.os.Handler().postDelayed(() -> {
            showPersonalityTestResultFragment(personalityType, personalityTraits);
        }, 2000); // 2초 대기
    }

    /**
     * 성향 테스트 질문 Fragment 표시
     */
    private void showPersonalityTestQuestionFragment() {
        // PersonalityTestQuestionFragment 생성
        PersonalityTestQuestionFragment fragment =
                new PersonalityTestQuestionFragment();

        // Bundle로 데이터 전달
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putBoolean("fromSignup", true);
        fragment.setArguments(args);

        // Fragment 교체
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // 뒤로가기 스택에 추가
                .commit();

        // Fragment 트랜잭션 완료 후 Next 버튼 숨기기
        getSupportFragmentManager().executePendingTransactions();
        updateNextButtonVisibility();
    }

    /**
     * 성향 테스트 결과 Fragment 표시
     */
    private void showPersonalityTestResultFragment(String personalityType, String personalityTraits) {
        // PersonalityTestResultFragment 생성
        PersonalityTestResultFragment fragment =
                new PersonalityTestResultFragment();

        // Bundle로 데이터 전달
        Bundle args = new Bundle();
        args.putString("personalityType", personalityType);
        args.putString("personalityTraitsJson", personalityTraits);
        args.putBoolean("isFromSignup", true); // 회원가입 과정임을 표시
        fragment.setArguments(args);

        // Fragment 교체
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // 뒤로가기 스택에 추가
                .commit();

        // Fragment 트랜잭션 완료 후 Next 버튼 표시
        getSupportFragmentManager().executePendingTransactions();
        updateNextButtonVisibility();
    }
}
