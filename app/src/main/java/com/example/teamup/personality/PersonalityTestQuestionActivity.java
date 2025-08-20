package com.example.teamup.personality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.R;
import com.example.teamup.personality.PersonalityQuestion;
import com.example.teamup.personality.PersonalityQuestionAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalityTestQuestionActivity extends AppCompatActivity implements PersonalityQuestionAdapter.OnOptionSelectedListener {

    private List<PersonalityQuestion> questions;
    private PersonalityQuestionAdapter adapter;
    private MaterialButton btnResult;
    private Map<Integer, String> selectedAnswers;
    private Map<Integer, String> selectedTypes;
    private String userId;
    private boolean fromSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personality_test_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 회원가입에서 온 경우 userId와 fromSignup 플래그 받기
        userId = getIntent().getStringExtra("userId");
        fromSignup = getIntent().getBooleanExtra("fromSignup", false);

        // 초기화
        selectedAnswers = new HashMap<>();
        selectedTypes = new HashMap<>();
        
        // 더미데이터 로드, 나중에 백엔드에서 로드
        loadQuestions();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup result button
        setupResultButton();
    }
    
    /**
     * 질문 데이터를 로드하는 메서드
     */
    private void loadQuestions() {
        // TODO: 백엔드에서 질문 데이터를 받아올 예정
        // loadQuestionsFromAPI();
        
        // 임시로 더미 데이터 사용
        questions = loadDummyQuestions();
    }
    
    /**
     * 더미 질문 데이터를 로드하는 메서드
     */
    private List<PersonalityQuestion> loadDummyQuestions() {
        return PersonalityQuestion.loadQuestions(this);
    }
    
    /**
     * 백엔드에서 질문 데이터를 받아오는 메서드
     */
    private void loadQuestionsFromAPI() {
        // TODO: 백엔드 API 호출로 질문 데이터 받아오기
    }
    
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new PersonalityQuestionAdapter(questions, this);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupResultButton() {
        btnResult = findViewById(R.id.btn_result);
        // 버튼을 항상 활성화 상태로 설정
        btnResult.setEnabled(true);
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndNavigateToResult();
            }
        });
    }
    
    /**
     * 모든 질문이 답변되었는지 확인하고 결과 페이지로 이동하는 메서드
     */
    private void checkAndNavigateToResult() {
        if (selectedAnswers.size() == questions.size()) {
            // 성향 분석
            String personalityType = analyzePersonality();
            
            // 모든 질문이 답변되면 결과 페이지 이동
            Intent intent = new Intent(PersonalityTestQuestionActivity.this, PersonalityTestResultActivity.class);
            intent.putExtra("personalityType", personalityType);
            if (fromSignup && userId != null) {
                intent.putExtra("userId", userId);
                intent.putExtra("fromSignup", true);
            }
            startActivity(intent);
        } else {
            // 모든 질문이 답변되지 않으면 Toast 메시지
            Toast.makeText(PersonalityTestQuestionActivity.this, "모든 항목이 선택되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String analyzePersonality() {
        // 각 성향별 카운트
        int analyticalCount = 0;
        int executionCount = 0;
        int creativeCount = 0;
        int cooperativeCount = 0;
        
        for (String type : selectedTypes.values()) {
            switch (type) {
                case "분석형":
                    analyticalCount++;
                    break;
                case "실행형":
                    executionCount++;
                    break;
                case "창의형":
                    creativeCount++;
                    break;
                case "협력형":
                    cooperativeCount++;
                    break;
            }
        }
        
        // 가장 많이 선택된 성향 찾기
        int maxCount = Math.max(Math.max(analyticalCount, executionCount), Math.max(creativeCount, cooperativeCount));
        
        // 동점이 있는지 확인
        boolean hasTie = false;
        String result = "";
        
        if (analyticalCount == maxCount) {
            result = "분석형";
            hasTie = true;
        }
        if (executionCount == maxCount) {
            if (hasTie) {
                result += "/실행형";
            } else {
                result = "실행형";
                hasTie = true;
            }
        }
        if (creativeCount == maxCount) {
            if (hasTie) {
                result += "/창의형";
            } else {
                result = "창의형";
                hasTie = true;
            }
        }
        if (cooperativeCount == maxCount) {
            if (hasTie) {
                result += "/협력형";
            } else {
                result = "협력형";
            }
        }
        
        return result;
    }
    
    @Override
    public void onOptionSelected(int questionIndex, String option, String type) {
        selectedAnswers.put(questionIndex, option);
        selectedTypes.put(questionIndex, type);
        
        // 결과 버튼 상태 업데이트
        updateResultButtonState();
    }
    
    /**
     * 결과 버튼의 활성화/비활성화 상태를 업데이트하는 메서드 
     */
    private void updateResultButtonState() {
        if (selectedAnswers.size() == questions.size()) {
            // 모든 질문이 답변됨 
            btnResult.setAlpha(1.0f);
        } else {
            // 모든 질문이 답변되지 않음
            btnResult.setAlpha(0.7f);
        }
    }
} 