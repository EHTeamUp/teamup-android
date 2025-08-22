package kr.mojuk.teamup.personality;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.mojuk.teamup.R;
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
    
    /**
     * 질문 목록을 업데이트하는 메서드
     */
    public void updateQuestions(List<PersonalityQuestion> newQuestions) {
        this.questions = newQuestions;
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

            // 버튼 상태 초기화
            resetButtonStates();
        }

        private void selectOption(MaterialButton selectedButton, MaterialButton unselectedButton) {
            // 두 버튼 모두 선택되지 않은 상태로 초기화
            selectedButton.setBackgroundTintList(itemView.getContext().getColorStateList(android.R.color.white));
            selectedButton.setTextColor(itemView.getContext().getColor(android.R.color.black));
            unselectedButton.setBackgroundTintList(itemView.getContext().getColorStateList(android.R.color.white));
            unselectedButton.setTextColor(itemView.getContext().getColor(android.R.color.black));

            // 선택된 버튼을 선택된 상태로 설정
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