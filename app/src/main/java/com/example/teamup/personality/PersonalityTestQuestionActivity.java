package com.example.teamup.personality;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.R;
import com.example.teamup.api.ApiService;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.PersonalityQuestionResponse;
import com.example.teamup.api.model.PersonalityTestRequest;
import com.example.teamup.api.model.PersonalityTestResponse;
import com.example.teamup.api.model.PersonalityAnswer;
import com.example.teamup.auth.TokenManager;
import com.example.teamup.personality.PersonalityQuestion;
import com.example.teamup.personality.PersonalityQuestionAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalityTestQuestionActivity extends AppCompatActivity implements PersonalityQuestionAdapter.OnOptionSelectedListener {

    private List<PersonalityQuestion> questions;
    private PersonalityQuestionAdapter adapter;
    private MaterialButton btnResult;
    private Map<Integer, Integer> selectedAnswers; // questionId -> optionId로 변경
    private ApiService apiService;
    private TokenManager tokenManager;

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
        apiService = RetrofitClient.getInstance().getApiService();
        tokenManager = TokenManager.getInstance(this);

        // API에서 질문 데이터 로드
        loadQuestionsFromAPI();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup result button
        setupResultButton();
    }
    
    /**
     * API에서 질문 데이터를 로드하는 메서드
     */
    private void loadQuestionsFromAPI() {
        Call<PersonalityQuestionResponse> call = apiService.getPersonalityQuestions();

        call.enqueue(new Callback<PersonalityQuestionResponse>() {
            @Override
            public void onResponse(Call<PersonalityQuestionResponse> call, Response<PersonalityQuestionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PersonalityQuestionResponse questionResponse = response.body();
                    questions = convertApiQuestionsToLocalQuestions(questionResponse.getQuestions());

                    // UI 업데이트
                    runOnUiThread(() -> {
                        adapter.updateQuestions(questions);
                        adapter.notifyDataSetChanged();
                    });
                } else {
                    // API 실패시 더미 데이터 사용
                    runOnUiThread(() -> {
                        questions = loadDummyQuestions();
                        adapter.updateQuestions(questions);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(PersonalityTestQuestionActivity.this,
                            "API 응답 실패. 기본 데이터를 사용합니다.", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<PersonalityQuestionResponse> call, Throwable t) {
                // 네트워크 오류시 더미 데이터 사용
                runOnUiThread(() -> {
                    questions = loadDummyQuestions();
                    adapter.updateQuestions(questions);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(PersonalityTestQuestionActivity.this,
                        "네트워크 오류. 기본 데이터를 사용합니다.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    /**
     * API 질문을 로컬 질문 형식으로 변환하는 메서드
     */
    private List<PersonalityQuestion> convertApiQuestionsToLocalQuestions(List<com.example.teamup.api.model.ApiPersonalityQuestion> apiQuestions) {
        List<PersonalityQuestion> localQuestions = new ArrayList<>();

        for (com.example.teamup.api.model.ApiPersonalityQuestion apiQuestion : apiQuestions) {
            if (apiQuestion.getOptions() != null && apiQuestion.getOptions().size() >= 2) {
                String optionA = apiQuestion.getOptions().get(0).getText();
                String optionB = apiQuestion.getOptions().get(1).getText();

                // API에서 받은 실제 옵션 ID들을 저장
                int optionAId = apiQuestion.getOptions().get(0).getId();
                int optionBId = apiQuestion.getOptions().get(1).getId();

                // 디버깅을 위한 로그 출력
                System.out.println("Question " + apiQuestion.getId() + " (" + apiQuestion.getKeyName() + "): " + apiQuestion.getText());
                System.out.println("  Option A (ID: " + optionAId + "): " + optionA);
                System.out.println("  Option B (ID: " + optionBId + "): " + optionB);

                PersonalityQuestion localQuestion = new PersonalityQuestion(
                    apiQuestion.getId(),
                    apiQuestion.getText(),
                    optionA,
                    optionB,
                    optionAId,  // A 옵션의 실제 ID
                    optionBId   // B 옵션의 실제 ID
                );
                localQuestions.add(localQuestion);
            }
        }

        return localQuestions;
    }

    /**
     * 더미 질문 데이터를 로드하는 메서드 (API 실패시 사용)
     */
    private List<PersonalityQuestion> loadDummyQuestions() {
        return PersonalityQuestion.loadQuestions(this);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        questions = new ArrayList<>(); // 초기화
        adapter = new PersonalityQuestionAdapter(questions, this);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupResultButton() {
        btnResult = findViewById(R.id.btn_result);
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndNavigateToResult();
            }
        });
        updateResultButtonState();
    }
    
    /**
     * 모든 질문이 답변되었는지 확인하고 결과 페이지로 이동하는 메서드
     */
    private void checkAndNavigateToResult() {
        // 디버그 로그 추가
        Log.d("PersonalityTest", "Selected answers: " + selectedAnswers.size() + ", Questions: " + questions.size());

        if (selectedAnswers.size() == questions.size()) {
            // 모든 질문이 답변되면 API로 결과 제출
            submitPersonalityTest();
        } else {
            // 모든 질문이 답변되지 않으면 Toast 메시지
            Toast.makeText(PersonalityTestQuestionActivity.this, "모든 항목이 선택되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 성향 테스트 결과를 API로 제출하는 메서드
     */
    private void submitPersonalityTest() {
        // 답변 데이터 생성
        List<PersonalityAnswer> answers = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : selectedAnswers.entrySet()) {
            answers.add(new PersonalityAnswer(entry.getKey(), entry.getValue()));
        }

        // 디버깅을 위한 로그 출력
        System.out.println("=== 제출할 답변 데이터 ===");
        System.out.println("선택된 답변 개수: " + selectedAnswers.size());
        for (Map.Entry<Integer, Integer> entry : selectedAnswers.entrySet()) {
            System.out.println("Question ID: " + entry.getKey() + ", Option ID: " + entry.getValue());
        }

        // 각 질문의 상세 정보도 출력
        System.out.println("=== 질문 상세 정보 ===");
        for (PersonalityQuestion question : questions) {
            System.out.println("Question " + question.getId() + ": " + question.getQuestion());
            System.out.println("  Option A (ID: " + question.getOptionAId() + "): " + question.getOptionA());
            System.out.println("  Option B (ID: " + question.getOptionBId() + "): " + question.getOptionB());
        }

        // 로그인된 사용자 ID 가져오기
        String userId = tokenManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("User ID: " + userId);

        PersonalityTestRequest request = new PersonalityTestRequest(userId, answers);

        Call<PersonalityTestResponse> call = apiService.submitPersonalityTest(request);
        call.enqueue(new Callback<PersonalityTestResponse>() {
            @Override
            public void onResponse(Call<PersonalityTestResponse> call, Response<PersonalityTestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PersonalityTestResponse result = response.body();
                    // 결과 페이지로 이동하면서 결과 데이터 전달
                    Intent intent = new Intent(PersonalityTestQuestionActivity.this, PersonalityTestResultActivity.class);
                    intent.putExtra("profile_code", result.getProfileCode());
                    intent.putExtra("display_name", result.getDisplayName());
                    intent.putExtra("description", result.getDescription());

                    // traits Map을 JSON 배열 형태의 문자열로 변환
                    StringBuilder traitsBuilder = new StringBuilder();
                    if (result.getTraits() != null) {
                        for (Map.Entry<String, String> entry : result.getTraits().entrySet()) {
                            if (traitsBuilder.length() > 0) {
                                traitsBuilder.append(", ");
                            }
                            traitsBuilder.append(entry.getValue()); // 값만 추가 (JSON 배열 형태)
                        }
                    }
                    intent.putExtra("traits", traitsBuilder.toString());
                    intent.putExtra("completed_at", result.getCompletedAt());
                    startActivity(intent);
                } else {
                    // API 실패시 기본 결과 페이지로 이동
                    Toast.makeText(PersonalityTestQuestionActivity.this,
                        "결과 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PersonalityTestQuestionActivity.this, PersonalityTestResultActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<PersonalityTestResponse> call, Throwable t) {
                // 네트워크 오류시 기본 결과 페이지로 이동
                Toast.makeText(PersonalityTestQuestionActivity.this,
                    "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PersonalityTestQuestionActivity.this, PersonalityTestResultActivity.class);
                startActivity(intent);
            }
        });
    }
    
    @Override
    public void onOptionSelected(int questionIndex, String option) {
        // 선택된 옵션의 실제 ID를 저장
        int optionId;
        if (questionIndex < questions.size()) {
            PersonalityQuestion question = questions.get(questionIndex);
            if ("A".equals(option)) {
                optionId = question.getOptionAId();
            } else {
                optionId = question.getOptionBId();
            }

            int questionId = question.getId();
            selectedAnswers.put(questionId, optionId);
        }
        
        // 결과 버튼 상태 업데이트
        updateResultButtonState();
    }
    
    /**
     * 결과 버튼의 활성화/비활성화 상태를 업데이트하는 메서드 
     */
    private void updateResultButtonState() {
        if (selectedAnswers.size() == questions.size()) {
            // 모든 질문이 답변됨 
            btnResult.setEnabled(true);
            btnResult.setText("결과 보기");
        } else {
            // 모든 질문이 답변되지 않음
            btnResult.setEnabled(false);
            btnResult.setText("모든 항목을 선택해주세요 (" + selectedAnswers.size() + "/" + questions.size() + ")");
        }
    }
}