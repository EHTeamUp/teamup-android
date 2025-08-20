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
    private String optionC;
    private String optionD;
    private String typeA;
    private String typeB;
    private String typeC;
    private String typeD;

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

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getTypeA() {
        return typeA;
    }

    public String getTypeB() {
        return typeB;
    }

    public String getTypeC() {
        return typeC;
    }

    public String getTypeD() {
        return typeD;
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

        // 새로운 6문항 성향 테스트
        questions.add(new PersonalityQuestion(1, 
            "새로운 프로젝트를 시작할 때, 나는?", 
            "전체 구조와 문제를 먼저 분석한다.", 
            "작은 부분부터 바로 실행해본다.", 
            "독창적인 아이디어나 방식을 제시한다.", 
            "팀원들과 협력할 방법을 먼저 찾는다.",
            "분석형", "실행형", "창의형", "협력형"));

        questions.add(new PersonalityQuestion(2, 
            "문제가 발생했을 때, 나는?", 
            "자료와 데이터를 찾아 해결책을 마련한다.", 
            "직접 부딪혀가며 해결한다.", 
            "새로운 접근법을 떠올린다.", 
            "주변 사람들에게 조언을 구하고 함께 논의한다.",
            "분석형", "실행형", "창의형", "협력형"));

        questions.add(new PersonalityQuestion(3, 
            "내가 성취감을 느끼는 순간은?", 
            "복잡한 문제를 논리적으로 풀었을 때", 
            "주어진 일을 신속하게 끝냈을 때", 
            "창의적인 아이디어로 주목받았을 때", 
            "모두가 만족하는 결과를 함께 만들어냈을 때",
            "분석형", "실행형", "창의형", "협력형"));

        questions.add(new PersonalityQuestion(4, 
            "회의 시간에 나는 보통?", 
            "데이터를 기반으로 의견을 제시한다.", 
            "실현 가능한 실행 방안을 말한다.", 
            "새로운 가능성을 탐색하는 아이디어를 낸다.", 
            "다른 사람들의 의견을 조율한다.",
            "분석형", "실행형", "창의형", "협력형"));

        questions.add(new PersonalityQuestion(5, 
            "중요한 결정을 내려야 할 때, 나는?", 
            "충분히 분석하고 신중히 결정한다.", 
            "빠르게 결정을 내려 실행한다.", 
            "남들과 다른 차별화된 선택을 한다.", 
            "여러 사람의 의견을 반영하려 한다.",
            "분석형", "실행형", "창의형", "협력형"));

        questions.add(new PersonalityQuestion(6, 
            "내가 선호하는 팀 역할은?", 
            "문제 해결사 (논리와 분석 담당)", 
            "실행가 (즉시 행동하고 추진하는 담당)", 
            "아이디어 뱅크 (새로운 발상 담당)", 
            "조율자 (사람들을 모으고 협력 담당)",
            "분석형", "실행형", "창의형", "협력형"));

        return questions;
    }

    /**
     * 백엔드에서 질문 데이터를 받아오는 메서드 
     */
    public static List<PersonalityQuestion> loadQuestionsFromAPI() {
        // TODO: 백엔드 API 호출로 질문 데이터 받아오기
        
        // 실패시 빈 리스트 반환 
        return new ArrayList<>();
    }
} 