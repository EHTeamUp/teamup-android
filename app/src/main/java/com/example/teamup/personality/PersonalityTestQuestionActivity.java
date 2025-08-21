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
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.PersonalityQuestionResponse;
import com.example.teamup.api.model.ApiPersonalityQuestion;
import com.example.teamup.api.model.PersonalityOption;
import com.example.teamup.api.model.PersonalityProfileResponse;
import com.example.teamup.api.model.PersonalityTraits;
import com.example.teamup.api.model.PersonalityTestSubmitRequest;
import com.example.teamup.api.model.PersonalityTestAnswer;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
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
        // 먼저 더미 데이터로 초기화
        questions = loadDummyQuestions();
        
        // 그 다음 API에서 질문 데이터를 받아오기
        loadQuestionsFromAPI();
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
        RetrofitClient.getInstance()
                .getApiService()
                .getPersonalityQuestions()
                .enqueue(new Callback<PersonalityQuestionResponse>() {
                    @Override
                    public void onResponse(Call<PersonalityQuestionResponse> call, Response<PersonalityQuestionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PersonalityQuestionResponse questionResponse = response.body();
                            List<ApiPersonalityQuestion> apiQuestions = questionResponse.getQuestions();
                            
                            // API 응답을 기존 PersonalityQuestion 형식으로 변환
                            questions = convertApiQuestionsToLocalQuestions(apiQuestions);
                            
                            // UI 업데이트
                            runOnUiThread(() -> {
                                adapter.updateQuestions(questions);
                                adapter.notifyDataSetChanged();
                            });
                        } else {
                            // API 호출 실패 시 더미 데이터 사용
                            runOnUiThread(() -> {
                                questions = loadDummyQuestions();
                                adapter.updateQuestions(questions);
                                adapter.notifyDataSetChanged();
                            });
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<PersonalityQuestionResponse> call, Throwable t) {
                        // 네트워크 오류 시 더미 데이터 사용
                        runOnUiThread(() -> {
                            questions = loadDummyQuestions();
                            adapter.updateQuestions(questions);
                            adapter.notifyDataSetChanged();
                        });
                    }
                });
    }
    
    /**
     * API 응답을 기존 PersonalityQuestion 형식으로 변환
     */
    private List<PersonalityQuestion> convertApiQuestionsToLocalQuestions(List<ApiPersonalityQuestion> apiQuestions) {
        List<PersonalityQuestion> localQuestions = new ArrayList<>();
        
        for (ApiPersonalityQuestion apiQuestion : apiQuestions) {
            List<PersonalityOption> options = apiQuestion.getOptions();
            
            if (options != null && options.size() >= 4) {
                // API에서 받은 옵션들을 기존 형식으로 변환
                String optionA = options.get(0).getText();
                String optionB = options.get(1).getText();
                String optionC = options.get(2).getText();
                String optionD = options.get(3).getText();
                
                String typeA = options.get(0).getType();
                String typeB = options.get(1).getType();
                String typeC = options.get(2).getType();
                String typeD = options.get(3).getType();
                
                PersonalityQuestion localQuestion = new PersonalityQuestion(
                    apiQuestion.getId(),
                    apiQuestion.getText(),
                    optionA, optionB, optionC, optionD,
                    typeA, typeB, typeC, typeD
                );
                
                localQuestions.add(localQuestion);
            }
        }
        
        return localQuestions;
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
            // API로 성향 테스트 결과 제출
            submitPersonalityTestToAPI();
        } else {
            // 모든 질문이 답변되지 않으면 Toast 메시지
            Toast.makeText(PersonalityTestQuestionActivity.this, "모든 항목이 선택되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void submitPersonalityTestToAPI() {
        // 답변 데이터를 API 형식으로 변환
        List<PersonalityTestAnswer> answers = new ArrayList<>();
        
        for (int i = 0; i < questions.size(); i++) {
            PersonalityQuestion question = questions.get(i);
            String selectedAnswer = selectedAnswers.get(i);
            
            // 선택된 답변에 따라 option_id 결정
            int optionId = 0; // 기본값
            if (selectedAnswer != null) {
                if (selectedAnswer.equals(question.getOptionA())) {
                    optionId = 0;
                } else if (selectedAnswer.equals(question.getOptionB())) {
                    optionId = 1;
                } else if (selectedAnswer.equals(question.getOptionC())) {
                    optionId = 2;
                } else if (selectedAnswer.equals(question.getOptionD())) {
                    optionId = 3;
                }
            }
            
            answers.add(new PersonalityTestAnswer(question.getId(), optionId));
        }
        
        // API 요청 객체 생성
        PersonalityTestSubmitRequest request = new PersonalityTestSubmitRequest(userId, answers);
        
        // API 호출
        RetrofitClient.getInstance()
                .getApiService()
                .submitPersonalityTest(request)
                .enqueue(new Callback<PersonalityProfileResponse>() {
                    @Override
                    public void onResponse(Call<PersonalityProfileResponse> call, Response<PersonalityProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // API 호출 성공: 결과 페이지로 이동
                            PersonalityProfileResponse profile = response.body();
                            
                            Intent intent = new Intent(PersonalityTestQuestionActivity.this, PersonalityTestResultActivity.class);
                            intent.putExtra("personalityType", profile.getProfileCode());
                            intent.putExtra("personalityTraits", profile.getTraitsJson());
                            intent.putExtra("userId", userId);
                            intent.putExtra("fromSignup", fromSignup);
                            startActivity(intent);
                        } else {
                            // API 호출 실패
                            Toast.makeText(PersonalityTestQuestionActivity.this, "테스트 결과 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<PersonalityProfileResponse> call, Throwable t) {
                        // 네트워크 오류
                        Toast.makeText(PersonalityTestQuestionActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
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
        
        // API에서 사용하는 영문 성향 타입으로 매핑
        if (analyticalCount == maxCount) {
            return "CAREFUL_SUPPORTER";
        } else if (executionCount == maxCount) {
            return "STRATEGIC_LEADER";
        } else if (creativeCount == maxCount) {
            return "CREATIVE_INNOVATOR";
        } else if (cooperativeCount == maxCount) {
            return "COLLABORATIVE_TEAMWORKER";
        } else {
            return "CAREFUL_SUPPORTER"; // 기본값
        }
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