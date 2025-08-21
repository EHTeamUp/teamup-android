package com.example.teamup.personality;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.teamup.R;
import com.example.teamup.MainActivity;
import com.example.teamup.api.model.PersonalityTraits;

public class PersonalityTestResultFragment extends Fragment {

    private String personalityType;
    private PersonalityTraits personalityTraits;
    private String personalityTraitsJson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personality_test_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bundle에서 personalityType과 traits 받기
        Bundle args = getArguments();
        if (args != null) {
            personalityType = args.getString("personalityType", "CAREFUL_SUPPORTER");
            personalityTraits = (PersonalityTraits) args.getSerializable("personalityTraits");
            personalityTraitsJson = args.getString("personalityTraitsJson", "");
        } else {
            personalityType = "CAREFUL_SUPPORTER"; // 기본값
            personalityTraits = null;
            personalityTraitsJson = "";
        }

        // 결과 표시
        displayPersonalityResult(view);
        
        // 뒤로가기 버튼 설정
        setupBackButton(view);
    }

    private void displayPersonalityResult(View view) {
        TextView tvResultTitle = view.findViewById(R.id.tv_result_title);
        TextView tvTraitGoal = view.findViewById(R.id.tv_trait_goal);
        TextView tvTraitRole = view.findViewById(R.id.tv_trait_role);
        TextView tvTraitTime = view.findViewById(R.id.tv_trait_time);
        TextView tvTraitProblem = view.findViewById(R.id.tv_trait_problem);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        
        // 성향 타입 설정
        if (personalityType != null && !personalityType.isEmpty()) {
            tvResultTitle.setText(personalityType);
        } else {
            tvResultTitle.setText("CAREFUL_SUPPORTER");
        }
        
        // Traits 정보 설정 (API 데이터 사용)
        if (personalityTraits != null) {
            tvTraitGoal.setText("Goal: " + getGoalFromProfileCode(personalityType));
            tvTraitRole.setText("Role: " + (personalityTraits.getRole() != null ? personalityTraits.getRole() : "UNKNOWN"));
            tvTraitTime.setText("Time: " + (personalityTraits.getTime() != null ? personalityTraits.getTime() : "UNKNOWN"));
            tvTraitProblem.setText("Problem: " + getProblemFromProfileCode(personalityType));
        } else {
            // 기본값 설정
            tvTraitGoal.setText("Goal: QUALITY");
            tvTraitRole.setText("Role: SUPPORTER");
            tvTraitTime.setText("Time: NIGHT");
            tvTraitProblem.setText("Problem: ANALYTIC");
        }
        
        // 설명 설정 (성향 타입에 따른 동적 설명)
        tvDescription.setText(getDescriptionFromProfileCode(personalityType));
    }
    
    private String getGoalFromProfileCode(String profileCode) {
        if (profileCode == null) return "QUALITY";
        
        switch (profileCode) {
            case "STRATEGIC_LEADER":
                return "STRATEGY";
            case "CAREFUL_SUPPORTER":
                return "QUALITY";
            case "CREATIVE_INNOVATOR":
                return "INNOVATION";
            case "COLLABORATIVE_TEAMWORKER":
                return "COLLABORATION";
            default:
                return "QUALITY";
        }
    }
    
    private String getProblemFromProfileCode(String profileCode) {
        if (profileCode == null) return "ANALYTIC";
        
        switch (profileCode) {
            case "STRATEGIC_LEADER":
                return "STRATEGIC";
            case "CAREFUL_SUPPORTER":
                return "ANALYTIC";
            case "CREATIVE_INNOVATOR":
                return "CREATIVE";
            case "COLLABORATIVE_TEAMWORKER":
                return "COLLABORATIVE";
            default:
                return "ANALYTIC";
        }
    }
    
    private String getDescriptionFromProfileCode(String profileCode) {
        if (profileCode == null) return "당신은 신중하며, 분석적이고 목표 지향적인 성향을 가진 지원자 타입입니다.";
        
        switch (profileCode) {
            case "STRATEGIC_LEADER":
                return "당신은 전략적 사고와 리더십을 갖춘 지도자 타입입니다. 큰 그림을 그리고 팀을 이끄는 능력이 뛰어납니다.";
            case "CAREFUL_SUPPORTER":
                return "당신은 신중하며, 분석적이고 목표 지향적인 성향을 가진 지원자 타입입니다. 꼼꼼한 작업과 체계적인 접근을 선호합니다.";
            case "CREATIVE_INNOVATOR":
                return "당신은 창의적이고 혁신적인 아이디어를 가진 혁신가 타입입니다. 새로운 해결책을 찾고 변화를 추구합니다.";
            case "COLLABORATIVE_TEAMWORKER":
                return "당신은 협력적이고 팀워크를 중시하는 팀워커 타입입니다. 다른 사람들과의 소통과 협업을 통해 성과를 만들어냅니다.";
            default:
                return "당신은 신중하며, 분석적이고 목표 지향적인 성향을 가진 지원자 타입입니다.";
        }
    }
    
    private void setupBackButton(View view) {
        LinearLayout llBackNavigation = view.findViewById(R.id.ll_back_navigation);
        llBackNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MypageProfileFragment로 돌아가기
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showMypageProfileFragment();
                }
            }
        });
    }
}
