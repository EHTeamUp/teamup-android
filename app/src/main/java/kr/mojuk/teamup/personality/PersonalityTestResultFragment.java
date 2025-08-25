package kr.mojuk.teamup.personality;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.MainActivity;
import kr.mojuk.teamup.api.model.PersonalityTraits;
import com.google.gson.Gson;

public class PersonalityTestResultFragment extends Fragment {

    private String personalityType;
    private PersonalityTraits personalityTraits;
    private String personalityTraitsJson;
    private boolean isFromSignup = false; // 회원가입 과정에서 호출되었는지 여부

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
            isFromSignup = args.getBoolean("isFromSignup", false); // 회원가입 과정 여부
            
            // personalityTraits가 null이고 JSON이 있으면 JSON에서 객체로 변환
            if (personalityTraits == null && personalityTraitsJson != null && !personalityTraitsJson.isEmpty()) {
                try {
                    Gson gson = new Gson();
                    personalityTraits = gson.fromJson(personalityTraitsJson, PersonalityTraits.class);
                    Log.d("PersonalityTestResultFragment", "JSON에서 PersonalityTraits 객체 생성 성공");
                } catch (Exception e) {
                    Log.e("PersonalityTestResultFragment", "JSON에서 PersonalityTraits 객체 생성 실패", e);
                }
            }
        } else {
            personalityType = "CAREFUL_SUPPORTER"; // 기본값
            personalityTraits = null;
            personalityTraitsJson = "";
            isFromSignup = false;
        }

        // 결과 표시
        displayPersonalityResult(view);
        
        // 뒤로가기 버튼 설정
        setupBackButton(view);
        
        // 회원가입 과정이면 "테스트 다시 하기" 버튼 숨기기
        setupRetakeButton(view);
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
        Log.d("PersonalityTestResultFragment", "=== Traits 정보 설정 ===");
        Log.d("PersonalityTestResultFragment", "personalityType: " + personalityType);
        Log.d("PersonalityTestResultFragment", "personalityTraits: " + (personalityTraits != null ? "not null" : "null"));
        Log.d("PersonalityTestResultFragment", "personalityTraitsJson: " + personalityTraitsJson);
        
        if (personalityTraits != null) {
            Log.d("PersonalityTestResultFragment", "Role: " + personalityTraits.getRole());
            Log.d("PersonalityTestResultFragment", "Time: " + personalityTraits.getTime());
            Log.d("PersonalityTestResultFragment", "Goal: " + personalityTraits.getGoal());
            Log.d("PersonalityTestResultFragment", "Problem: " + personalityTraits.getProblem());
            
            // 서버에서 받은 실제 데이터 사용 (한글 매핑)
            tvTraitGoal.setText("Goal: " + (personalityTraits.getGoal() != null ? getGoalFromServerValue(personalityTraits.getGoal()) : "UNKNOWN"));
            tvTraitRole.setText("Role: " + (personalityTraits.getRole() != null ? getRoleFromServerValue(personalityTraits.getRole()) : "UNKNOWN"));
            tvTraitTime.setText("Time: " + (personalityTraits.getTime() != null ? getTimeFromServerValue(personalityTraits.getTime()) : "UNKNOWN"));
            tvTraitProblem.setText("Problem: " + (personalityTraits.getProblem() != null ? getProblemFromServerValue(personalityTraits.getProblem()) : "UNKNOWN"));
        } else {
            Log.d("PersonalityTestResultFragment", "personalityTraits가 null이므로 기본값 사용");
            // 기본값 설정
            tvTraitGoal.setText("Goal: QUALITY");
            tvTraitRole.setText("Role: SUPPORTER");
            tvTraitTime.setText("Time: NIGHT");
            tvTraitProblem.setText("Problem: ANALYTIC");
        }
        
        // 설명 설정 (성향 타입에 따른 동적 설명)
        tvDescription.setText(getDescriptionFromProfileCode(personalityType));
    }

    // 서버 값에 대한 한글 매핑 메서드들
    private String getGoalFromServerValue(String serverValue) {
        if (serverValue == null) return "품질";
        
        switch (serverValue) {
            case "QUALITY":
                return "품질";
            case "SCHEDULE":
                return "일정";
            default:
                return "품질";
        }
    }
    
    private String getRoleFromServerValue(String serverValue) {
        if (serverValue == null) return "서포터";
        
        switch (serverValue) {
            case "LEADER":
                return "리더";
            case "SUPPORTER":
                return "서포터";
            default:
                return "서포터";
        }
    }
    
    private String getTimeFromServerValue(String serverValue) {
        if (serverValue == null) return "밤형";
        
        switch (serverValue) {
            case "MORNING":
                return "아침형";
            case "NIGHT":
                return "밤형";
            default:
                return "밤형";
        }
    }
    
    private String getProblemFromServerValue(String serverValue) {
        if (serverValue == null) return "분석형";
        
        switch (serverValue) {
            case "ANALYTIC":
                return "분석형";
            case "ADHOC":
                return "즉흥형";
            default:
                return "분석형";
        }
    }

    // 목표 (Goal) - ProfileCode 기반 (기존 메서드)
    private String getGoalFromProfileCode(String profileCode) {
        if (profileCode == null) return "품질";

        switch (profileCode) {
            case "STRATEGIC_LEADER":
            case "VISIONARY_LEADER":
            case "RELIABLE_PARTNER":
            case "CAREFUL_SUPPORTER":
                return "품질";

            case "EXECUTION_LEADER":
            case "DYNAMIC_LEADER":
            case "ENERGETIC_SUPPORTER":
            case "BALANCE_SUPPORTER":
                return "일정";

            default:
                return "품질";
        }
    }

    // 문제 해결 방식 (Problem)
    private String getProblemFromProfileCode(String profileCode) {
        if (profileCode == null) return "분석형";

        switch (profileCode) {
            case "STRATEGIC_LEADER":
            case "VISIONARY_LEADER":
            case "RELIABLE_PARTNER":
            case "CAREFUL_SUPPORTER":
                return "분석형";

            case "EXECUTION_LEADER":
            case "DYNAMIC_LEADER":
            case "ENERGETIC_SUPPORTER":
            case "BALANCE_SUPPORTER":
                return "즉흥형";

            default:
                return "분석형";
        }
    }

    // 역할 (Role)
    private String getRoleFromProfileCode(String profileCode) {
        if (profileCode == null) return "서포터";

        switch (profileCode) {
            case "STRATEGIC_LEADER":
            case "EXECUTION_LEADER":
            case "VISIONARY_LEADER":
            case "DYNAMIC_LEADER":
                return "리더";

            case "RELIABLE_PARTNER":
            case "ENERGETIC_SUPPORTER":
            case "CAREFUL_SUPPORTER":
            case "BALANCE_SUPPORTER":
                return "서포터";

            default:
                return "서포터";
        }
    }

    // 활동 시간대 (Time)
    private String getTimeFromProfileCode(String profileCode) {
        if (profileCode == null) return "밤형";

        switch (profileCode) {
            case "STRATEGIC_LEADER":
            case "DYNAMIC_LEADER":
            case "RELIABLE_PARTNER":
            case "BALANCE_SUPPORTER":
                return "아침형";

            case "EXECUTION_LEADER":
            case "VISIONARY_LEADER":
            case "ENERGETIC_SUPPORTER":
            case "CAREFUL_SUPPORTER":
                return "밤형";

            default:
                return "밤형";
        }
    }

    private String getDescriptionFromProfileCode(String profileCode) {
        if (profileCode == null)
            return "당신은 신중하며, 분석적이고 목표 지향적인 성향을 가진 지원자 타입입니다.";

        switch (profileCode) {
            case "STRATEGIC_LEADER":
                return "당신은 전략적 사고와 계획 능력을 갖춘 리더입니다. 큰 그림을 그리고 팀을 체계적으로 이끄는 데 강점을 보입니다.";
            case "EXECUTION_LEADER":
                return "당신은 실행력이 뛰어난 리더입니다. 빠르게 결단하고 행동하여 팀을 이끌며 목표 달성을 주도합니다.";
            case "VISIONARY_LEADER":
                return "당신은 깊은 통찰력과 높은 기준을 가진 리더입니다. 넓은 시야와 비전을 바탕으로 팀을 미래로 이끕니다.";
            case "DYNAMIC_LEADER":
                return "당신은 활력 있고 추진력 있는 리더입니다. 빠른 판단과 에너지로 팀에 활기를 불어넣고 속도감 있게 주도합니다.";
            case "RELIABLE_PARTNER":
                return "당신은 든든하고 신뢰할 수 있는 파트너입니다. 꼼꼼하고 안정적인 성향으로 팀을 묵묵히 뒷받침합니다.";
            case "ENERGETIC_SUPPORTER":
                return "당신은 열정적이고 에너지가 넘치는 서포터입니다. 적극적인 태도로 팀 분위기를 고조시키고 활력을 불어넣습니다.";
            case "CAREFUL_SUPPORTER":
                return "당신은 신중하며 분석적인 서포터입니다. 꼼꼼하고 세심한 성향으로 팀의 완성도를 높이고 품질을 책임집니다.";
            case "BALANCE_SUPPORTER":
                return "당신은 균형 잡힌 서포터입니다. 팀 리더와 조화를 이루며 유연하게 상황에 맞춰 협력하고 안정적인 분위기를 유지합니다.";
            default:
                return "당신은 신중하며, 분석적이고 목표 지향적인 성향을 가진 지원자 타입입니다.";
        }
    }


    private void setupBackButton(View view) {
        LinearLayout llBackNavigation = view.findViewById(R.id.ll_back_navigation);
        
        if (isFromSignup) {
            // 회원가입 과정이면 뒤로가기 버튼 숨기기
            llBackNavigation.setVisibility(View.GONE);
        } else {
            // 마이페이지에서 접근한 경우에만 뒤로가기 버튼 표시
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
    
    private void setupRetakeButton(View view) {
        com.google.android.material.button.MaterialButton btnEdit = view.findViewById(R.id.btn_edit);
        
        if (isFromSignup) {
            // 회원가입 과정이면 버튼 숨기기
            btnEdit.setVisibility(View.GONE);
        } else {
            // 마이페이지에서 접근한 경우: 테스트 다시 하기 기능 구현
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // MainActivity에서 성향 테스트 시작 Fragment를 ll_back_navigation 아래에 표시
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).showPersonalityTestInMypage();
                    }
                }
            });
        }
    }
}
