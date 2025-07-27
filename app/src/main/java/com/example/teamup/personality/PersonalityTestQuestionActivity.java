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

        // Initialize
        selectedAnswers = new HashMap<>();
        
        // Load questions
        questions = PersonalityQuestion.loadQuestions(this);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup result button
        setupResultButton();
    }
    
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new PersonalityQuestionAdapter(questions, this);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupResultButton() {
        btnResult = findViewById(R.id.btn_result);
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAnswers.size() == questions.size()) {
                    // All questions answered - navigate to result activity
                    Intent intent = new Intent(PersonalityTestQuestionActivity.this, PersonalityTestResultActivity.class);
                    startActivity(intent);
                } else {
                    // Show message when not all questions are answered
                    Toast.makeText(PersonalityTestQuestionActivity.this, "모든 항목이 선택되어 있지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    @Override
    public void onOptionSelected(int questionIndex, String option) {
        selectedAnswers.put(questionIndex, option);
        
        // Update result button state
        if (selectedAnswers.size() == questions.size()) {
            btnResult.setEnabled(true);
        } else {
            btnResult.setEnabled(false);
        }
    }
} 