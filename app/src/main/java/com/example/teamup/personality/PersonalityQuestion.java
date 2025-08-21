package com.example.teamup.personality;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class PersonalityQuestion {
    private int id;
    private String question;
    private String optionA, optionB, optionC, optionD;
    private String typeA, typeB, typeC, typeD;
    
    public PersonalityQuestion(int id, String question, String optionA, String optionB, String optionC, String optionD,
                              String typeA, String typeB, String typeC, String typeD) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.typeA = typeA;
        this.typeB = typeB;
        this.typeC = typeC;
        this.typeD = typeD;
    }
    
    // Getters
    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getTypeA() { return typeA; }
    public String getTypeB() { return typeB; }
    public String getTypeC() { return typeC; }
    public String getTypeD() { return typeD; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setQuestion(String question) { this.question = question; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setTypeA(String typeA) { this.typeA = typeA; }
    public void setTypeB(String typeB) { this.typeB = typeB; }
    public void setTypeC(String typeC) { this.typeC = typeC; }
    public void setTypeD(String typeD) { this.typeD = typeD; }
    
    // 더미 데이터 로드 메서드
    public static List<PersonalityQuestion> loadQuestions(Context context) {
        List<PersonalityQuestion> questions = new ArrayList<>();
        
        questions.add(new PersonalityQuestion(
            1, "팀 프로젝트에서 나는...",
            "계획을 세우고 체계적으로 진행한다", "즉흥적으로 문제를 해결한다",
            "창의적인 아이디어를 제안한다", "팀원들과 협력하여 진행한다",
            "분석형", "실행형", "창의형", "협력형"
        ));
        
        questions.add(new PersonalityQuestion(
            2, "문제가 발생했을 때 나는...",
            "원인을 분석하고 해결책을 찾는다", "빠르게 행동하여 해결한다",
            "새로운 접근 방법을 시도한다", "다른 사람의 의견을 듣는다",
            "분석형", "실행형", "창의형", "협력형"
        ));
        
        questions.add(new PersonalityQuestion(
            3, "새로운 기술을 배울 때 나는...",
            "기본 원리를 이해하고 체계적으로 학습한다", "실습을 통해 직접 경험한다",
            "다양한 방법으로 창의적으로 접근한다", "다른 사람과 함께 학습한다",
            "분석형", "실행형", "창의형", "협력형"
        ));
        
        questions.add(new PersonalityQuestion(
            4, "팀 회의에서 나는...",
            "논리적으로 분석하여 의견을 제시한다", "구체적인 실행 방안을 제안한다",
            "혁신적인 아이디어를 제안한다", "팀원들의 의견을 조율한다",
            "분석형", "실행형", "창의형", "협력형"
        ));
        
        return questions;
    }
} 