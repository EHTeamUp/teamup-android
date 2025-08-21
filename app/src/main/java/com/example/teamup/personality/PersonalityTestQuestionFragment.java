package com.example.teamup.personality;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.teamup.R;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.PersonalityQuestionResponse;
import com.example.teamup.api.model.ApiPersonalityQuestion;
import com.example.teamup.api.model.PersonalityOption;
import com.example.teamup.MainActivity;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalityTestQuestionFragment extends Fragment implements PersonalityQuestionAdapter.OnOptionSelectedListener {

    private static final String TAG = "PersonalityTestQuestionFragment";

    private List<PersonalityQuestion> questions;
    private MaterialButton btnResult;
    private Map<Integer, String> selectedAnswers;
    private Map<Integer, String> selectedTypes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_personality_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 초기화
        selectedAnswers = new HashMap<>();
        selectedTypes = new HashMap<>();
        
        // 질문 데이터 로드
        loadQuestions();
        
        // Setup question display
        setupQuestionDisplay(view);
        
        // Setup result button
        setupResultButton(view);
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
        return PersonalityQuestion.loadQuestions(requireContext());
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
                        Log.d(TAG, "API 응답 상태: " + response.code());
                        
                        if (response.isSuccessful() && response.body() != null) {
                            PersonalityQuestionResponse questionResponse = response.body();
                            List<ApiPersonalityQuestion> apiQuestions = questionResponse.getQuestions();
                            
                            Log.d(TAG, "API에서 받은 질문 개수: " + (apiQuestions != null ? apiQuestions.size() : 0));
                            
                            // API 응답을 기존 PersonalityQuestion 형식으로 변환
                            questions = convertApiQuestionsToLocalQuestions(apiQuestions);
                            
                            Log.d(TAG, "변환된 질문 개수: " + (questions != null ? questions.size() : 0));
                            
                            // UI 업데이트
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (questions != null && !questions.isEmpty()) {
                                        Log.d(TAG, "API 질문으로 UI 업데이트");
                                        displayQuestion(getView(), questions.get(0));
                                    } else {
                                        Log.d(TAG, "API 질문이 비어있어서 더미 데이터 사용");
                                        questions = loadDummyQuestions();
                                        if (!questions.isEmpty()) {
                                            displayQuestion(getView(), questions.get(0));
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.e(TAG, "API 호출 실패: " + response.code());
                            // API 호출 실패 시 더미 데이터 사용
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    questions = loadDummyQuestions();
                                    if (!questions.isEmpty()) {
                                        displayQuestion(getView(), questions.get(0));
                                    }
                                });
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<PersonalityQuestionResponse> call, Throwable t) {
                        // 네트워크 오류 시 더미 데이터 사용
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                questions = loadDummyQuestions();
                                if (!questions.isEmpty()) {
                                    displayQuestion(getView(), questions.get(0));
                                }
                            });
                        }
                    }
                });
    }
    
    /**
     * API 응답을 기존 PersonalityQuestion 형식으로 변환
     */
    private List<PersonalityQuestion> convertApiQuestionsToLocalQuestions(List<ApiPersonalityQuestion> apiQuestions) {
        List<PersonalityQuestion> localQuestions = new ArrayList<>();
        
        if (apiQuestions == null) {
            Log.e(TAG, "API 질문 리스트가 null입니다");
            return localQuestions;
        }
        
        Log.d(TAG, "변환 시작: API 질문 개수 = " + apiQuestions.size());
        
        for (ApiPersonalityQuestion apiQuestion : apiQuestions) {
            List<PersonalityOption> options = apiQuestion.getOptions();
            
            Log.d(TAG, "질문 ID: " + apiQuestion.getId() + ", 텍스트: " + apiQuestion.getText());
            Log.d(TAG, "옵션 개수: " + (options != null ? options.size() : 0));
            
            if (options != null && options.size() >= 2) {
                // API에서 받은 옵션들을 기존 형식으로 변환
                String optionA = options.get(0).getText();
                String optionB = options.get(1).getText();
                
                String typeA = options.get(0).getType();
                String typeB = options.get(1).getType();
                
                // 2개 옵션만 있는 경우 C, D는 빈 문자열로 설정
                String optionC = options.size() >= 3 ? options.get(2).getText() : "";
                String optionD = options.size() >= 4 ? options.get(3).getText() : "";
                
                String typeC = options.size() >= 3 ? options.get(2).getType() : "";
                String typeD = options.size() >= 4 ? options.get(3).getType() : "";
                
                Log.d(TAG, "옵션 A: " + optionA + " (" + typeA + ")");
                Log.d(TAG, "옵션 B: " + optionB + " (" + typeB + ")");
                if (options.size() >= 3) Log.d(TAG, "옵션 C: " + optionC + " (" + typeC + ")");
                if (options.size() >= 4) Log.d(TAG, "옵션 D: " + optionD + " (" + typeD + ")");
                
                PersonalityQuestion localQuestion = new PersonalityQuestion(
                    apiQuestion.getId(),
                    apiQuestion.getText(),
                    optionA, optionB, optionC, optionD,
                    typeA, typeB, typeC, typeD
                );
                
                localQuestions.add(localQuestion);
                Log.d(TAG, "질문 변환 완료: " + localQuestion.getQuestion());
            } else {
                Log.e(TAG, "옵션이 부족합니다: " + (options != null ? options.size() : 0) + "개");
            }
        }
        
        Log.d(TAG, "변환 완료: 총 " + localQuestions.size() + "개 질문");
        return localQuestions;
    }
    
    private void setupQuestionDisplay(View view) {
        // 첫 번째 질문만 표시 (단일 질문 화면이므로)
        if (questions != null && !questions.isEmpty()) {
            PersonalityQuestion question = questions.get(0);
            displayQuestion(view, question);
        }
    }
    
    private void displayQuestion(View view, PersonalityQuestion question) {
        TextView tvQuestion = view.findViewById(R.id.tv_question);
        MaterialButton btnOptionA = view.findViewById(R.id.btn_option_a);
        MaterialButton btnOptionB = view.findViewById(R.id.btn_option_b);
        MaterialButton btnOptionC = view.findViewById(R.id.btn_option_c);
        MaterialButton btnOptionD = view.findViewById(R.id.btn_option_d);
        
        // 질문 텍스트 설정
        tvQuestion.setText(question.getQuestion());
        
        // 옵션 버튼 설정
        btnOptionA.setText(question.getOptionA());
        btnOptionB.setText(question.getOptionB());
        
        // C, D 옵션이 비어있으면 숨기기
        if (question.getOptionC() != null && !question.getOptionC().isEmpty()) {
            btnOptionC.setVisibility(View.VISIBLE);
            btnOptionC.setText(question.getOptionC());
            btnOptionC.setOnClickListener(v -> onOptionSelected(0, question.getOptionC(), question.getTypeC()));
        } else {
            btnOptionC.setVisibility(View.GONE);
        }
        
        if (question.getOptionD() != null && !question.getOptionD().isEmpty()) {
            btnOptionD.setVisibility(View.VISIBLE);
            btnOptionD.setText(question.getOptionD());
            btnOptionD.setOnClickListener(v -> onOptionSelected(0, question.getOptionD(), question.getTypeD()));
        } else {
            btnOptionD.setVisibility(View.GONE);
        }
        
        // A, B 옵션 클릭 리스너 설정
        btnOptionA.setOnClickListener(v -> onOptionSelected(0, question.getOptionA(), question.getTypeA()));
        btnOptionB.setOnClickListener(v -> onOptionSelected(0, question.getOptionB(), question.getTypeB()));
    }
    
    private void setupResultButton(View view) {
        btnResult = view.findViewById(R.id.btn_result);
        if (btnResult != null) {
            // 버튼을 항상 활성화 상태로 설정
            btnResult.setEnabled(true);
            btnResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAndNavigateToResult();
                }
            });
        }
    }

    @Override
    public void onOptionSelected(int questionIndex, String option, String type) {
        selectedAnswers.put(questionIndex, option);
        selectedTypes.put(questionIndex, type);
        
        Log.d(TAG, "선택된 답변: " + questionIndex + "번 질문 - " + option + " (" + type + ")");
        
        // 모든 질문에 답변했는지 확인
        checkAndNavigateToResult();
    }
    
    private void checkAndNavigateToResult() {
        if (selectedAnswers.size() == questions.size()) {
            // 모든 질문에 답변했으면 결과 화면으로 이동
            String personalityType = analyzePersonality();
            
            // PersonalityTestResultFragment로 이동
            PersonalityTestResultFragment resultFragment = new PersonalityTestResultFragment();
            Bundle args = new Bundle();
            args.putString("personalityType", personalityType);
            resultFragment.setArguments(args);
            
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showFragment(resultFragment);
            }
        }
    }
    
    private String analyzePersonality() {
        int analyticalCount = 0;
        int executionCount = 0;
        int creativeCount = 0;
        int cooperativeCount = 0;

        for (String type : selectedTypes.values()) {
            switch (type) {
                case "분석형": analyticalCount++; break;
                case "실행형": executionCount++; break;
                case "창의형": creativeCount++; break;
                case "협력형": cooperativeCount++; break;
            }
        }
        
        int maxCount = Math.max(Math.max(analyticalCount, executionCount), Math.max(creativeCount, cooperativeCount));
        String result = "";
        boolean hasTie = false;
        
        if (analyticalCount == maxCount) { result = "분석형"; hasTie = true; }
        if (executionCount == maxCount) { if (hasTie) result += "/실행형"; else result = "실행형"; hasTie = true; }
        if (creativeCount == maxCount) { if (hasTie) result += "/창의형"; else result = "창의형"; hasTie = true; }
        if (cooperativeCount == maxCount) { if (hasTie) result += "/협력형"; else result = "협력형"; }
        
        return result;
    }
}
