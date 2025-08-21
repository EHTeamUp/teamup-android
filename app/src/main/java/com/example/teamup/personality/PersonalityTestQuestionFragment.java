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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.R;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.PersonalityQuestionResponse;
import com.example.teamup.api.model.ApiPersonalityQuestion;
import com.example.teamup.api.model.PersonalityOption;
import com.example.teamup.api.model.PersonalityProfileResponse;
import com.example.teamup.api.model.PersonalityTestSubmitRequest;
import com.example.teamup.api.model.PersonalityTestAnswer;
import com.example.teamup.MainActivity;
import com.example.teamup.auth.SignupTestBaseActivity;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
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
    private PersonalityQuestionAdapter adapter;
    private MaterialButton btnResult;
    private Map<Integer, String> selectedAnswers;
    private Map<Integer, String> selectedTypes;
    private String userId;
    private boolean fromSignup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personality_test_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bundle에서 데이터 받기
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("userId");
            fromSignup = args.getBoolean("fromSignup", false);
        }

        // 초기화
        selectedAnswers = new HashMap<>();
        selectedTypes = new HashMap<>();
        
        // 질문 데이터 로드
        loadQuestions();
        
        // Setup RecyclerView
        setupRecyclerView(view);
        
        // Setup result button
        setupResultButton(view);
    }
    
    /**
     * 질문 데이터를 로드하는 메서드
     */
    private void loadQuestions() {
        // API에서 질문 데이터를 받아오기
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
                                        if (adapter != null) {
                                            adapter.updateQuestions(questions);
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Log.e(TAG, "API에서 받은 질문이 비어있습니다");
                                        Toast.makeText(requireContext(), "질문을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Log.e(TAG, "API 호출 실패: " + response.code());
                            // API 호출 실패 시 오류 메시지 표시
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), "질문을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<PersonalityQuestionResponse> call, Throwable t) {
                        // 네트워크 오류 시 오류 메시지 표시
                        Log.e(TAG, "네트워크 오류", t);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
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
                
                Log.d(TAG, "옵션 A: " + optionA + " (" + typeA + ")");
                Log.d(TAG, "옵션 B: " + optionB + " (" + typeB + ")");
                
                PersonalityQuestion localQuestion = new PersonalityQuestion(
                    apiQuestion.getId(),
                    apiQuestion.getText(),
                    optionA, optionB, "", "",
                    typeA, typeB, "", ""
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
    
    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        if (recyclerView != null) {
            // 빈 리스트로 초기화 (API 로딩 전까지)
            questions = new ArrayList<>();
            adapter = new PersonalityQuestionAdapter(questions, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);
        }
    }
    

    
    private void setupResultButton(View view) {
        btnResult = view.findViewById(R.id.btn_result);
        if (btnResult != null) {
            // 초기에는 모든 질문이 답변되지 않았으므로 비활성화
            btnResult.setEnabled(false);
            btnResult.setAlpha(0.7f);
            btnResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAndNavigateToResult();
                }
            });
        }
    }
    
    /**
     * 결과 버튼의 활성화/비활성화 상태를 업데이트하는 메서드
     */
    private void updateResultButtonState() {
        if (btnResult != null) {
            if (selectedAnswers.size() == questions.size()) {
                // 모든 질문이 답변됨
                btnResult.setEnabled(true);
                btnResult.setAlpha(1.0f);
            } else {
                // 모든 질문이 답변되지 않음
                btnResult.setEnabled(false);
                btnResult.setAlpha(0.7f);
            }
        }
    }

    @Override
    public void onOptionSelected(int questionIndex, String option, String type) {
        selectedAnswers.put(questionIndex, option);
        selectedTypes.put(questionIndex, type);
        
        Log.d(TAG, "선택된 답변: " + questionIndex + "번 질문 - " + option + " (" + type + ")");
        
        // 결과 버튼 상태 업데이트
        updateResultButtonState();
    }
    
    private void checkAndNavigateToResult() {
        // 모든 질문에 답변했는지 확인
        if (selectedAnswers.size() == questions.size()) {
            // 모든 질문에 답변했으면 API로 결과 제출
            submitPersonalityTestToAPI();
        } else {
            // 모든 질문이 답변되지 않으면 Toast 메시지
            Toast.makeText(requireContext(), "모든 질문에 답변해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void submitPersonalityTestToAPI() {
        // 답변 데이터를 API 형식으로 변환
        List<PersonalityTestAnswer> answers = new ArrayList<>();
        
        Log.d(TAG, "=== 성향 테스트 답변 데이터 ===");
        for (int i = 0; i < questions.size(); i++) {
            PersonalityQuestion question = questions.get(i);
            String selectedAnswer = selectedAnswers.get(i);
            
            Log.d(TAG, "질문 " + (i + 1) + ": " + question.getQuestion());
            Log.d(TAG, "선택된 답변: " + selectedAnswer);
            Log.d(TAG, "A: " + question.getOptionA() + ", B: " + question.getOptionB());
            
            int optionId = 1; // 기본값
            if (selectedAnswer != null) {
                if (selectedAnswer.equals(question.getOptionA())) {
                    optionId = 1;
                    Log.d(TAG, "→ A 옵션 선택 (option_id: 1)");
                } else if (selectedAnswer.equals(question.getOptionB())) {
                    optionId = 2;
                    Log.d(TAG, "→ B 옵션 선택 (option_id: 2)");
                }
            }
            
            answers.add(new PersonalityTestAnswer(question.getId(), optionId));
            Log.d(TAG, "→ 최종: question_id=" + question.getId() + ", option_id=" + optionId);
        }
        
        // API 요청 객체 생성
        PersonalityTestSubmitRequest request = new PersonalityTestSubmitRequest(userId, answers);
        Log.d(TAG, "API 요청: userId=" + userId + ", answers count=" + answers.size());
        
        // API 호출
        RetrofitClient.getInstance()
                .getApiService()
                .submitPersonalityTest(request)
                .enqueue(new Callback<PersonalityProfileResponse>() {
                    @Override
                    public void onResponse(Call<PersonalityProfileResponse> call, Response<PersonalityProfileResponse> response) {
                        Log.d(TAG, "API 응답: " + response.code() + " " + response.message());
                        
                        if (response.isSuccessful() && response.body() != null) {
                            // API 호출 성공: 결과 Fragment로 이동
                            PersonalityProfileResponse profile = response.body();
                            Log.d(TAG, "성향 프로필 받음: " + profile.getProfileCode());
                            
                            if (fromSignup && getActivity() instanceof SignupTestBaseActivity) {
                                // 회원가입에서 온 경우: SignupTestBaseActivity로 결과 전달
                                Gson gson = new Gson();
                                String traitsJson = gson.toJson(profile.getTraitsJson());
                                ((SignupTestBaseActivity) getActivity()).onPersonalityTestCompleted(
                                    profile.getProfileCode(), 
                                    traitsJson
                                );
                            } else {
                                // 일반적인 경우: 결과 Fragment로 이동
                                PersonalityTestResultFragment resultFragment = new PersonalityTestResultFragment();
                                Bundle args = new Bundle();
                                args.putString("personalityType", profile.getProfileCode());
                                Gson gson = new Gson();
                                String traitsJson = gson.toJson(profile.getTraitsJson());
                                args.putString("personalityTraitsJson", traitsJson);
                                resultFragment.setArguments(args);
                                
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).showFragment(resultFragment);
                                }
                            }
                        } else {
                            // API 호출 실패
                            String errorMessage = "테스트 결과 저장에 실패했습니다.";
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "API 오류 응답: " + errorBody);
                                    errorMessage += " (" + errorBody + ")";
                                } catch (Exception e) {
                                    Log.e(TAG, "오류 응답 읽기 실패", e);
                                }
                            }
                            Log.e(TAG, "API 실패: " + response.code() + " - " + errorMessage);
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<PersonalityProfileResponse> call, Throwable t) {
                        // 네트워크 오류
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
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
