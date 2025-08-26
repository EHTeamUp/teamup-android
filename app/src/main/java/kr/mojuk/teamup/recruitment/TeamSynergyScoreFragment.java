package kr.mojuk.teamup.recruitment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import kr.mojuk.teamup.R;
import kr.mojuk.teamup.applicant.ApplicantListFragment;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.SynergyAnalysisRequest;
import kr.mojuk.teamup.api.model.SynergyAnalysisResponse;
import kr.mojuk.teamup.auth.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamSynergyScoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private TeamMemberAdapter adapter;
    private List<TeamMemberData> teamMemberList;
    
    private RecyclerView scoreCardsRecyclerView;
    private ScoreCardAdapter scoreCardAdapter;
    private List<ScoreCardData> scoreCardList;
    
    private LinearLayout goodPointsContainer;
    private LinearLayout badPointsContainer;
    private TokenManager tokenManager;
    private int filterId; // filter_id 저장 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_synergy_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // TokenManager 초기화
        tokenManager = TokenManager.getInstance(requireContext());
        
        // filter_id 받아오기
        if (getArguments() != null) {
            filterId = getArguments().getInt("filter_id", 1); // 기본값 1
            Log.d("TeamSynergyScore", "filter_id 받음: " + filterId);
        }
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners(view);
        performSynergyAnalysis();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_team_members);
        scoreCardsRecyclerView = view.findViewById(R.id.rv_score_cards);
        goodPointsContainer = view.findViewById(R.id.good_points_container);
        badPointsContainer = view.findViewById(R.id.bad_points_container);
    }

    private void setupRecyclerView() {
        // Team Members RecyclerView
        teamMemberList = new ArrayList<>();
        adapter = new TeamMemberAdapter(teamMemberList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        // Score Cards RecyclerView
        scoreCardList = new ArrayList<>();
        scoreCardAdapter = new ScoreCardAdapter(scoreCardList);
        scoreCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        scoreCardsRecyclerView.setAdapter(scoreCardAdapter);
    }

    private void setupClickListeners(View view) {
        // 지원자 추가 모집 버튼
        Button btnAddApplicant = view.findViewById(R.id.btn_add_applicant);
        if (btnAddApplicant != null) {
            btnAddApplicant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 지원자 추가 모집 화면으로 이동 (프래그먼트로 변경)
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new ApplicantListFragment())
                            .addToBackStack(null)
                            .commit();
                    }
                }
            });
        }

        // 네비게이션 바 전체에 뒤로가기 클릭 리스너
        LinearLayout navigationBar = view.findViewById(R.id.navigation_bar);
        if (navigationBar != null) {
            Log.d("TeamSynergyScore", "네비게이션 바 클릭 리스너 설정됨");
            navigationBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TeamSynergyScore", "네비게이션 바 클릭됨");
                    // 프래그먼트 백스택에서 제거
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
        } else {
            Log.e("TeamSynergyScore", "네비게이션 바를 찾을 수 없음");
        }
    }

    /**
     * UI 업데이트 메서드들
     */
    private void updateScoreCards(double synergyScore, String filterName) {
        scoreCardList.clear();
        scoreCardList.add(new ScoreCardData("시너지 점수", String.format("%.0f%%", synergyScore)));
        scoreCardList.add(new ScoreCardData("공모전 카테고리", filterName != null ? filterName : "아이디어/기획"));
        scoreCardAdapter.notifyDataSetChanged();
    }

    /**
     * 좋은 점들을 UI에 표시
     */
    private void updateGoodPoints(List<SynergyAnalysisResponse.Point> goodPoints) {
        updatePointsContainer(goodPointsContainer, goodPoints, "현재 좋은 점이 없습니다.");
    }

    /**
     * 부족한 점들을 UI에 표시
     */
    private void updateBadPoints(List<SynergyAnalysisResponse.Point> badPoints) {
        updatePointsContainer(badPointsContainer, badPoints, "현재 부족한 점이 없습니다.");
    }

    /**
     * 점수 컨테이너를 업데이트하는 공통 메서드
     */
    private void updatePointsContainer(LinearLayout container, List<SynergyAnalysisResponse.Point> points, String emptyMessage) {
        if (container == null) return;
        
        container.removeAllViews();
        
        if (points == null || points.isEmpty()) {
            // 좋은 점/나쁜 점 없을 때 메시지 표시
            TextView noPointsText = new TextView(requireContext());
            noPointsText.setText(emptyMessage);
            noPointsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            noPointsText.setTextSize(16);
            noPointsText.setPadding(16, 8, 16, 8);
            container.addView(noPointsText);
            return;
        }
        
        for (int i = 0; i < points.size(); i++) {
            SynergyAnalysisResponse.Point point = points.get(i);
            
            // 텍스트를 담을 수직 레이아웃
            LinearLayout pointLayout = new LinearLayout(requireContext());
            pointLayout.setOrientation(LinearLayout.VERTICAL);
            pointLayout.setPadding(16, 16, 16, 16);
            pointLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
            

            
            // 메시지를 마침표(.)나 느낌표(!)를 기준으로 분리
            String message = point.getMessage();
            String[] sentences = message.split("([.!])");
            
            // 첫 번째 문장 (굵게)
            if (sentences.length > 0 && !sentences[0].trim().isEmpty()) {
                TextView firstSentenceText = new TextView(requireContext());
                String firstSentence = sentences[0].trim();
                if (!firstSentence.endsWith(".") && !firstSentence.endsWith("!")) {
                    firstSentence += ".";
                }
                firstSentenceText.setText(firstSentence);
                firstSentenceText.setTextColor(getResources().getColor(android.R.color.black));
                firstSentenceText.setTextSize(16);
                firstSentenceText.setTypeface(null, android.graphics.Typeface.BOLD);
                firstSentenceText.setPadding(0, 0, 0, 12);
                pointLayout.addView(firstSentenceText);
            }
            
            // 나머지 문장들 (일반)
            if (sentences.length > 1) {
                TextView remainingText = new TextView(requireContext());
                StringBuilder remainingSentences = new StringBuilder();
                for (int j = 1; j < sentences.length; j++) {
                    String sentence = sentences[j].trim();
                    if (!sentence.isEmpty()) {
                        if (remainingSentences.length() > 0) {
                            remainingSentences.append("\n");
                        }
                        remainingSentences.append(sentence);
                        // 원래 문장이 .이나 !로 끝났다면 추가
                        if (j < sentences.length - 1 || message.endsWith(".") || message.endsWith("!")) {
                            remainingSentences.append(message.charAt(message.indexOf(sentence) + sentence.length()));
                        }
                    }
                }
                if (remainingSentences.length() > 0) {
                    remainingText.setText(remainingSentences.toString());
                    remainingText.setTextColor(getResources().getColor(android.R.color.black));
                    remainingText.setTextSize(16);
                    remainingText.setPadding(0, 0, 0, 8);
                    pointLayout.addView(remainingText);
                }
            }
            
            container.addView(pointLayout);
            
            // 구분선 추가 (마지막 항목 제외)
            if (points.indexOf(point) < points.size() - 1) {
                View divider = new View(requireContext());
                divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
                params.setMargins(16, 0, 16, 0);
                divider.setLayoutParams(params);
                container.addView(divider);
            }
        }
    }

    /**
     * 시너지 분석 API를 호출하는 메서드
     */
    private void performSynergyAnalysis() {
        // Bundle에서 전달받은 사용자 ID 리스트 가져오기
        Bundle args = getArguments();
        List<String> userIds = new ArrayList<>();
        
        if (args != null && args.getStringArrayList("selected_user_ids") != null) {
            userIds = args.getStringArrayList("selected_user_ids");
            Log.d("TeamSynergyScore", "전달받은 사용자 ID 리스트: " + userIds);
        } else {
            Log.e("TeamSynergyScore", "전달받은 사용자 ID가 없습니다.");
            return;
        }

        // 시너지 분석 요청 생성 (filter_id 포함)
        SynergyAnalysisRequest request = new SynergyAnalysisRequest(userIds, filterId);
        Log.d("TeamSynergyScore", "filter_id 포함하여 요청 생성: " + filterId);

        Log.d("TeamSynergyScore", "시너지 분석 API 호출 시작");
        Log.d("TeamSynergyScore", "요청 데이터: " + userIds);

        // API 호출
        RetrofitClient.getInstance()
                .getApiService()
                .analyzeSynergy(request)
                .enqueue(new Callback<SynergyAnalysisResponse>() {
                    @Override
                    public void onResponse(Call<SynergyAnalysisResponse> call, Response<SynergyAnalysisResponse> response) {
                        Log.d("TeamSynergyScore", "API 응답 코드: " + response.code());
                        
                        if (response.isSuccessful() && response.body() != null) {
                            SynergyAnalysisResponse synergyResponse = response.body();
                            
                            Log.d("TeamSynergyScore", "시너지 분석 성공!");
                            
                            // 필터 이름 저장
                            String filterName = synergyResponse.getFilterName();
                            if (filterName != null) {
                                Log.d("TeamSynergyScore", "필터 이름: " + filterName);
                            }
                            
                            // 시너지 결과 처리
                            if (synergyResponse.getSynergyResult() != null) {
                                SynergyAnalysisResponse.SynergyResult result = synergyResponse.getSynergyResult();
                                Log.d("TeamSynergyScore", "=== 시너지 분석 결과 ===");
                                Log.d("TeamSynergyScore", "시너지 점수: " + result.getSynergyScore());
                                
                                // UI에 시너지 점수와 필터 이름 업데이트
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        updateScoreCards(result.getSynergyScore(), filterName);
                                    });
                                }
                                
                                // 설명 처리
                                if (result.getExplanation() != null) {
                                    SynergyAnalysisResponse.Explanation explanation = result.getExplanation();
                                    Log.d("TeamSynergyScore", "기준점: " + explanation.getBaseline());
                                    
                                    // 좋은 점들 로깅 및 UI 업데이트
                                    if (explanation.getGoodPoints() != null) {
                                        Log.d("TeamSynergyScore", "=== 좋은 점들 ===");
                                        for (SynergyAnalysisResponse.Point point : explanation.getGoodPoints()) {
                                            Log.d("TeamSynergyScore", "특성: " + point.getFeature());
                                            Log.d("TeamSynergyScore", "값: " + point.getValue());
                                            Log.d("TeamSynergyScore", "기여도: " + point.getContribution());
                                            Log.d("TeamSynergyScore", "메시지: " + point.getMessage());
                                            Log.d("TeamSynergyScore", "---");
                                        }
                                        
                                        // UI 업데이트
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(() -> {
                                                updateGoodPoints(explanation.getGoodPoints());
                                            });
                                        }
                                    }
                                    
                                    // 개선점들 로깅 및 UI 업데이트
                                    if (explanation.getBadPoints() != null) {
                                        Log.d("TeamSynergyScore", "=== 개선점들 ===");
                                        for (SynergyAnalysisResponse.Point point : explanation.getBadPoints()) {
                                            Log.d("TeamSynergyScore", "특성: " + point.getFeature());
                                            Log.d("TeamSynergyScore", "값: " + point.getValue());
                                            Log.d("TeamSynergyScore", "기여도: " + point.getContribution());
                                            Log.d("TeamSynergyScore", "메시지: " + point.getMessage());
                                            Log.d("TeamSynergyScore", "---");
                                        }
                                        
                                        // UI 업데이트
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(() -> {
                                                updateBadPoints(explanation.getBadPoints());
                                            });
                                        }
                                    }
                                }
                            }
                            
                            if (synergyResponse.getUsers() != null) {
                                Log.d("TeamSynergyScore", "사용자 수: " + synergyResponse.getUsers().size());
                                
                                // API 응답으로 팀원 데이터 업데이트
                                teamMemberList.clear();
                                for (SynergyAnalysisResponse.User user : synergyResponse.getUsers()) {
                                    Log.d("TeamSynergyScore", "사용자 ID: " + user.getUserId());
                                    
                                    // 스킬 정보 구성
                                    String skillsText = "스킬 정보 없음";
                                    if (user.getSkills() != null && !user.getSkills().isEmpty()) {
                                        StringBuilder skillsBuilder = new StringBuilder();
                                        for (SynergyAnalysisResponse.Skill skill : user.getSkills()) {
                                            if (skillsBuilder.length() > 0) skillsBuilder.append(", ");
                                            skillsBuilder.append(skill.getSkillName());
                                        }
                                        skillsText = skillsBuilder.toString();
                                        Log.d("TeamSynergyScore", "사용자 스킬: " + skillsText);
                                    }
                                    
                                    // 역할 정보 구성
                                    String roleText = "역할 미정";
                                    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                                        StringBuilder rolesBuilder = new StringBuilder();
                                        for (SynergyAnalysisResponse.Role role : user.getRoles()) {
                                            if (rolesBuilder.length() > 0) rolesBuilder.append(", ");
                                            rolesBuilder.append(role.getRoleName());
                                        }
                                        roleText = rolesBuilder.toString();
                                        Log.d("TeamSynergyScore", "사용자 역할: " + roleText);
                                    }
                                    
                                    // 성향 정보 구성
                                    String traitText = "성향 미정";
                                    if (user.getTraits() != null && user.getTraits().getDisplayName() != null) {
                                        traitText = user.getTraits().getDisplayName();
                                    }
                                    
                                    // 팀원 데이터 추가
                                    teamMemberList.add(new TeamMemberData(
                                        user.getUserId(),
                                        roleText,
                                        skillsText,
                                        traitText
                                    ));
                                }
                                
                                // UI 업데이트
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        adapter.notifyDataSetChanged();
                                        Log.d("TeamSynergyScore", "UI 업데이트 완료 - 팀원 수: " + teamMemberList.size());
                                        
                                        // 각 팀원 데이터 로깅
                                        for (int i = 0; i < teamMemberList.size(); i++) {
                                            TeamMemberData member = teamMemberList.get(i);
                                            Log.d("TeamSynergyScore", "팀원 " + i + ": " + member.getName() + 
                                                  " | 역할: " + member.getRole() + 
                                                  " | 스킬: " + member.getSkills() + 
                                                  " | 성향: " + member.getType());
                                        }
                                    });
                                }
                            }
                            
                        } else {
                            Log.e("TeamSynergyScore", "API 호출 실패: " + response.code());
                            if (response.errorBody() != null) {
                                try {
                                    Log.e("TeamSynergyScore", "에러 응답: " + response.errorBody().string());
                                } catch (Exception e) {
                                    Log.e("TeamSynergyScore", "에러 응답 읽기 실패: " + e.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SynergyAnalysisResponse> call, Throwable t) {
                        Log.e("TeamSynergyScore", "네트워크 오류: " + t.getMessage());
                        t.printStackTrace();
                    }
                });
    }
      
 
  
     // 팀원 데이터 클래스
    public static class TeamMemberData {
        private String name;
        private String role;
        private String skills;
        private String type;

        public TeamMemberData(String name, String role, String skills, String type) {
            this.name = name;
            this.role = role;
            this.skills = skills;
            this.type = type;
        }

        public String getName() { return name; }
        public String getRole() { return role; }
        public String getSkills() { return skills; }
        public String getType() { return type; }
    }

    // 팀원 어댑터 클래스
    public static class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.ViewHolder> {
        private List<TeamMemberData> dataList;

        public TeamMemberAdapter(List<TeamMemberData> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_team_member, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TeamMemberData data = dataList.get(position);
            holder.tvMemberName.setText(data.getName());
            holder.tvMemberRole.setText(data.getRole());
            holder.tvMemberSkills.setText(data.getSkills());
            holder.tvMemberType.setText(data.getType());
            
            // 데이터 바인딩 로그
            Log.d("TeamMemberAdapter", "바인딩 - 위치: " + position + 
                  " | 이름: " + data.getName() + 
                  " | 역할: " + data.getRole() + 
                  " | 스킬: " + data.getSkills() + 
                  " | 성향: " + data.getType());
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMemberName, tvMemberRole, tvMemberSkills, tvMemberType;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMemberName = itemView.findViewById(R.id.tv_member_name);
                tvMemberRole = itemView.findViewById(R.id.tv_member_role);
                tvMemberSkills = itemView.findViewById(R.id.tv_member_skills);
                tvMemberType = itemView.findViewById(R.id.tv_member_type);
            }
        }
    }

    // 점수 카드 데이터 클래스
    public static class ScoreCardData {
        private String title;
        private String value;

        public ScoreCardData(String title, String value) {
            this.title = title;
            this.value = value;
        }

        public String getTitle() { return title; }
        public String getValue() { return value; }
    }

    // 점수 카드 어댑터 클래스
    public static class ScoreCardAdapter extends RecyclerView.Adapter<ScoreCardAdapter.ViewHolder> {
        private List<ScoreCardData> dataList;

        public ScoreCardAdapter(List<ScoreCardData> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_score_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ScoreCardData data = dataList.get(position);
            holder.tvCardTitle.setText(data.getTitle());
            holder.tvCardValue.setText(data.getValue());
            
            // 카드 너비 설정 (화면 너비의 절반에서 마진 제외)
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }
            params.width = (int) (holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels * 0.43);
            holder.itemView.setLayoutParams(params);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCardTitle, tvCardValue;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCardTitle = itemView.findViewById(R.id.tv_card_title);
                tvCardValue = itemView.findViewById(R.id.tv_card_value);
            }
        }
    }
}
