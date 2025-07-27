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

    public static List<PersonalityQuestion> loadQuestions(Context context) {
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
            Log.e("PersonalityQuestion", "Error loading questions", e);
        }

        return questions;
    }
} 