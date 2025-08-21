package com.example.teamup.personality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.google.android.material.button.MaterialButton;

public class PersonalityTestResultActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_personality_test_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 뷰 초기화
        btnFinishTest = findViewById(R.id.btn_finish_test);
        tvResultTitle = findViewById(R.id.tv_result_title);
        
        // CardView 내부 TextView들 초기화
        tvCardProfileCode = findViewById(R.id.tv_card_profile_code);
        tvCardDescription = findViewById(R.id.tv_card_description);
        tvCardTraits = findViewById(R.id.tv_card_traits);

        // 전달받은 결과 데이터 표시
        displayTestResult();

        // 버튼 클릭 리스너 설정
        btnFinishTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메인 화면으로 이동
                Intent intent = new Intent(PersonalityTestResultActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    
    /**
     * 테스트 결과를 화면에 표시하는 메서드
     */
    private void displayTestResult() {
        Intent intent = getIntent();
        
        String profileCode = intent.getStringExtra("profile_code");
        String displayName = intent.getStringExtra("display_name");
        String description = intent.getStringExtra("description");
        String traits = intent.getStringExtra("traits");
        String completedAt = intent.getStringExtra("completed_at");
        
        // API에서 받은 데이터가 있으면 표시, 없으면 기본값 사용
        String finalProfileCode = (profileCode != null) ? profileCode : "STRATEGIC_LEADER";
        String finalDisplayName = (displayName != null) ? displayName : "전략 리더";
        String finalDescription = (description != null) ? description : "계획적이고 꼼꼼한 리더";
        String finalTraits = (traits != null && !traits.isEmpty()) ? traits : "LEADER, MORNING, ANALYTIC, QUALITY";
        
        // 결과 제목 업데이트
        tvResultTitle.setText("당신은 " + finalDisplayName + "!");
        
        // CardView 내부 TextView들 업데이트
        tvCardProfileCode.setText("프로필 코드: " + finalProfileCode);
        tvCardDescription.setText("설명: " + finalDescription);
        tvCardTraits.setText("특성: " + finalTraits);
    }
} 