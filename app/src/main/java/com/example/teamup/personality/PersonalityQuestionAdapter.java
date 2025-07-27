package com.example.teamup.personality;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PersonalityQuestionAdapter extends RecyclerView.Adapter<PersonalityQuestionAdapter.QuestionViewHolder> {

    private List<PersonalityQuestion> questions;
    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onOptionSelected(int questionIndex, String option);
    }

    public PersonalityQuestionAdapter(List<PersonalityQuestion> questions, OnOptionSelectedListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_personality_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        PersonalityQuestion question = questions.get(position);
        holder.bind(question, position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQuestion;
        private MaterialButton btnOptionA, btnOptionB;
        private int currentPosition;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tv_question);
            btnOptionA = itemView.findViewById(R.id.btn_option_a);
            btnOptionB = itemView.findViewById(R.id.btn_option_b);

            btnOptionA.setOnClickListener(v -> {
                selectOption(btnOptionA, btnOptionB);
                if (listener != null) {
                    listener.onOptionSelected(currentPosition, "A");
                }
            });

            btnOptionB.setOnClickListener(v -> {
                selectOption(btnOptionB, btnOptionA);
                if (listener != null) {
                    listener.onOptionSelected(currentPosition, "B");
                }
            });
        }

        public void bind(PersonalityQuestion question, int position) {
            currentPosition = position;
            tvQuestion.setText((position + 1) + ". " + question.getQuestion());
            btnOptionA.setText("A. " + question.getOptionA());
            btnOptionB.setText("B. " + question.getOptionB());

            // Reset button states
            resetButtonStates();
        }

        private void selectOption(MaterialButton selectedButton, MaterialButton unselectedButton) {
            // Reset both buttons to unselected state
            selectedButton.setBackgroundTintList(itemView.getContext().getColorStateList(android.R.color.white));
            selectedButton.setTextColor(itemView.getContext().getColor(android.R.color.black));
            unselectedButton.setBackgroundTintList(itemView.getContext().getColorStateList(android.R.color.white));
            unselectedButton.setTextColor(itemView.getContext().getColor(android.R.color.black));

            // Set selected button to selected state
            selectedButton.setBackgroundTintList(itemView.getContext().getColorStateList(android.R.color.darker_gray));
            selectedButton.setTextColor(itemView.getContext().getColor(android.R.color.white));
        }

        private void resetButtonStates() {
            btnOptionA.setBackgroundTintList(itemView.getContext().getColorStateList(android.R.color.white));
            btnOptionA.setTextColor(itemView.getContext().getColor(android.R.color.black));
            btnOptionB.setBackgroundTintList(itemView.getContext().getColorStateList(android.R.color.white));
            btnOptionB.setTextColor(itemView.getContext().getColor(android.R.color.black));
        }
    }
} 