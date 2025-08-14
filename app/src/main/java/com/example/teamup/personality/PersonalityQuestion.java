package com.example.teamup.personality;

import android.content.Context;
import android.util.Log;

import com.example.teamup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PersonalityQuestion {
    private int id;
    private String question;
    private String optionA;
    private String optionB;

    public PersonalityQuestion(int id, String question, String optionA, String optionB) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    /**
     * 질문 데이터를 로드하는 메서드
     * 현재는 더미 데이터를 사용하지만, 나중에 백엔드에서 받아올 예정
     */
    public static List<PersonalityQuestion> loadQuestions(Context context) {
        // TODO: 백엔드에서 질문 데이터를 받아올 예정
        // return loadQuestionsFromAPI();
        
        // 임시로 더미 데이터 사용
        return loadDummyQuestions(context);
    }

    /**
     * 더미 질문 데이터를 로드하는 메서드
     */
    public static List<PersonalityQuestion> loadDummyQuestions(Context context) {
        List<PersonalityQuestion> questions = new ArrayList<>();

        try {
            // Load JSON file from raw resources
            InputStream inputStream = context.getResources().openRawResource(R.raw.personality_questions);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray questionsArray = jsonObject.getJSONArray("questions");

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionObj = questionsArray.getJSONObject(i);
                int id = questionObj.getInt("id");
                String question = questionObj.getString("question");
                String optionA = questionObj.getString("optionA");
                String optionB = questionObj.getString("optionB");

                questions.add(new PersonalityQuestion(id, question, optionA, optionB));
            }

        } catch (IOException | JSONException e) {
            Log.e("PersonalityQuestion", "Error loading dummy questions", e);
        }

        return questions;
    }

    /**
     * 백엔드에서 질문 데이터를 받아오는 메서드 (구현 예정)
     */
    public static List<PersonalityQuestion> loadQuestionsFromAPI() {
        // TODO: 백엔드 API 호출로 질문 데이터 받아오기
        /*
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<PersonalityQuestion>> call = apiService.getPersonalityQuestions();
        
        try {
            Response<List<PersonalityQuestion>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            Log.e("PersonalityQuestion", "Error loading questions from API", e);
        }
        */
        
        // 실패시 빈 리스트 반환 (호출하는 쪽에서 더미 데이터로 대체)
        return new ArrayList<>();
    }
} 