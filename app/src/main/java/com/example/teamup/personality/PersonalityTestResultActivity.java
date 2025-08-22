package com.example.teamup.personality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.google.android.material.button.MaterialButton;
import com.example.teamup.api.model.PersonalityTraits;
public class PersonalityTestResultActivity extends AppCompatActivity {

    private LinearLayout llBackNavigation;
    private String userId;
    private boolean fromSignup;
    private String personalityType;
    private PersonalityTraits personalityTraits;

    private MaterialButton btnFinishTest;
    private TextView tvResultTitle;

    // CardView 내부 TextView들
    private TextView tvCardProfileCode;
    private TextView tvCardDescription;
    private TextView tvCardTraits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_personality_test_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 회원가입에서 온 경우 userId와 fromSignup 플래그 받기
        userId = getIntent().getStringExtra("userId");
        fromSignup = getIntent().getBooleanExtra("fromSignup", false);
        personalityType = getIntent().getStringExtra("personalityType");
        personalityTraits = (PersonalityTraits) getIntent().getSerializableExtra("personalityTraits");

        // 뷰 초기화
        llBackNavigation = findViewById(R.id.ll_back_navigation);

        // 성향 결과 표시
        displayPersonalityResult();

        // 뒤로가기 버튼 클릭 리스너 설정
        llBackNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromSignup && userId != null) {
                    // 회원가입에서 온 경우: 회원가입 완료 단계로 이동
                    Intent intent = new Intent(PersonalityTestResultActivity.this, com.example.teamup.auth.SignupFinishActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    // 일반적인 경우: 메인 화면으로 이동
                    Intent intent = new Intent(PersonalityTestResultActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    private void displayPersonalityResult() {
        TextView tvResultTitle = findViewById(R.id.tv_result_title);
        TextView tvTraitGoal = findViewById(R.id.tv_trait_goal);
        TextView tvTraitRole = findViewById(R.id.tv_trait_role);
        TextView tvTraitTime = findViewById(R.id.tv_trait_time);
        TextView tvTraitProblem = findViewById(R.id.tv_trait_problem);
        TextView tvDescription = findViewById(R.id.tv_description);

        // 성향 타입 설정
        if (personalityType != null && !personalityType.isEmpty()) {
            tvResultTitle.setText(personalityType);
        } else {
            tvResultTitle.setText("CAREFUL_SUPPORTER");
        }

        // Traits 정보 설정 (API 데이터 사용)
        if (personalityTraits != null) {
            tvTraitGoal.setText("Goal: " + getGoalFromProfileCode(personalityType));
            tvTraitRole.setText("Role: " + (personalityTraits.getRole() != null ? personalityTraits.getRole() : "UNKNOWN"));
            tvTraitTime.setText("Time: " + (personalityTraits.getTime() != null ? personalityTraits.getTime() : "UNKNOWN"));
            tvTraitProblem.setText("Problem: " + getProblemFromProfileCode(personalityType));
        } else {
            // 기본값 설정
            tvTraitGoal.setText("Goal: QUALITY");
            tvTraitRole.setText("Role: SUPPORTER");
            tvTraitTime.setText("Time: NIGHT");
            tvTraitProblem.setText("Problem: ANALYTIC");
        }

        // 설명 설정 (성향 타입에 따른 동적 설명)
        tvDescription.setText(getDescriptionFromProfileCode(personalityType));
    }

    private String getGoalFromProfileCode(String profileCode) {
        if (profileCode == null) return "QUALITY";

        switch (profileCode) {
            case "STRATEGIC_LEADER":
                return "STRATEGY";
            case "CAREFUL_SUPPORTER":
                return "QUALITY";
            case "CREATIVE_INNOVATOR":
                return "INNOVATION";
            case "COLLABORATIVE_TEAMWORKER":
                return "COLLABORATION";
            default:
                return "QUALITY";
        }
    }

    private String getProblemFromProfileCode(String profileCode) {
        if (profileCode == null) return "ANALYTIC";

        switch (profileCode) {
            case "STRATEGIC_LEADER":
                return "STRATEGIC";
            case "CAREFUL_SUPPORTER":
                return "ANALYTIC";
            case "CREATIVE_INNOVATOR":
                return "CREATIVE";
            case "COLLABORATIVE_TEAMWORKER":
                return "COLLABORATIVE";
            default:
                return "ANALYTIC";
        }
    }

    private String getDescriptionFromProfileCode(String profileCode) {
        if (profileCode == null) return "당신은 신중하며, 분석적이고 목표 지향적인 성향을 가진 지원자 타입입니다.";

        switch (profileCode) {
            case "STRATEGIC_LEADER":
                return "당신은 전략적 사고와 리더십을 갖춘 지도자 타입입니다. 큰 그림을 그리고 팀을 이끄는 능력이 뛰어납니다.";
            case "CAREFUL_SUPPORTER":
                return "당신은 신중하며, 분석적이고 목표 지향적인 성향을 가진 지원자 타입입니다. 꼼꼼한 작업과 체계적인 접근을 선호합니다.";
            case "CREATIVE_INNOVATOR":
                return "당신은 창의적이고 혁신적인 아이디어를 가진 혁신가 타입입니다. 새로운 해결책을 찾고 변화를 추구합니다.";
            case "COLLABORATIVE_TEAMWORKER":
                return "당신은 협력적이고 팀워크를 중시하는 팀워커 타입입니다. 다른 사람들과의 소통과 협업을 통해 성과를 만들어냅니다.";
            default:
                return "당신은 신중하며, 분석적이고 목표 지향적인 성향을 가진 지원자 타입입니다.";
        }
    }
}