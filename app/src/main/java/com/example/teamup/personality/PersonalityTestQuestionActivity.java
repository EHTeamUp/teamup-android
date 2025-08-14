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

        // 초기화
        selectedAnswers = new HashMap<>();
        
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
            // 모든 질문이 답변되면 결과 페이지 이동
            Intent intent = new Intent(PersonalityTestQuestionActivity.this, PersonalityTestResultActivity.class);
            startActivity(intent);
        } else {
            // 모든 질문이 답변되지 않으면 Toast 메시지
            Toast.makeText(PersonalityTestQuestionActivity.this, "모든 항목이 선택되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onOptionSelected(int questionIndex, String option) {
        selectedAnswers.put(questionIndex, option);
        
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