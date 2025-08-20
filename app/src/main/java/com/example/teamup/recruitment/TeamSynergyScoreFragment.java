package com.example.teamup.recruitment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.R;
import com.example.teamup.api.ApiService;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.SynergyAnalysisRequest;
import com.example.teamup.api.model.SynergyAnalysisResponse;
import com.example.teamup.applicant.ApplicantListFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamSynergyScoreFragment extends Fragment {

    private static final String TAG = "TeamSynergyScoreFragment";
    private RecyclerView recyclerView;
    private TeamMemberAdapter adapter;
    private List<TeamMemberData> teamMemberList;
    private TextView tvSynergyScore;
    private ApiService apiService;
    private List<String> selectedUserIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_synergy_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        
        // Bundle에서 선택된 사용자 ID들 받기
        Bundle args = getArguments();
        if (args != null && args.containsKey("selected_user_ids")) {
            selectedUserIds = args.getStringArrayList("selected_user_ids");
            Log.d(TAG, "받은 사용자 ID들: " + selectedUserIds);
            
            // 시너지 분석 API 호출
            analyzeSynergy();
        } else {
            // 기본 더미 데이터 로드
            loadDummyData();
        }
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_team_members);
        tvSynergyScore = view.findViewById(R.id.tv_synergy_score);
        
        // API 서비스 초기화
        apiService = RetrofitClient.getInstance().getApiService();
    }

    private void setupRecyclerView() {
        teamMemberList = new ArrayList<>();
        adapter = new TeamMemberAdapter(teamMemberList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // 지원자 추가 모집 버튼
        Button btnAddApplicant = getView().findViewById(R.id.btn_add_applicant);
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

        // 뒤로가기 화살표
        View backArrow = getView().findViewById(R.id.iv_back_arrow);
        if (backArrow != null) {
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 프래그먼트 백스택에서 제거
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
        }
    }

    /**
     * 시너지 분석 API를 호출하는 메서드
     */
    private void analyzeSynergy() {
        if (selectedUserIds == null || selectedUserIds.isEmpty()) {
            Log.e(TAG, "선택된 사용자 ID가 없습니다.");
            return;
        }
        
        
        // API 요청 데이터 생성
        SynergyAnalysisRequest request = new SynergyAnalysisRequest(selectedUserIds);
        
        // API 호출
        Call<SynergyAnalysisResponse> call = apiService.analyzeSynergy(request);
        call.enqueue(new Callback<SynergyAnalysisResponse>() {
            @Override
            public void onResponse(Call<SynergyAnalysisResponse> call, Response<SynergyAnalysisResponse> response) {
                
                if (response.isSuccessful() && response.body() != null) {
                    SynergyAnalysisResponse result = response.body();
                    
                    // 팀 분석 결과 로그 출력
                    if (result.getTeamAnalysis() != null) {
                        SynergyAnalysisResponse.TeamAnalysis teamAnalysis = result.getTeamAnalysis();
                        Log.d(TAG, "=== 팀 분석 결과 ===");
                        Log.d(TAG, "팀 시너지 점수: " + teamAnalysis.getTeamSynergyScore());
                        Log.d(TAG, "분석 요약: " + teamAnalysis.getAnalysisSummary());
                        Log.d(TAG, "추천사항 개수: " + (teamAnalysis.getRecommendations() != null ? teamAnalysis.getRecommendations().size() : 0));
                        if (teamAnalysis.getRecommendations() != null) {
                            for (int i = 0; i < teamAnalysis.getRecommendations().size(); i++) {
                                Log.d(TAG, "추천사항 [" + (i+1) + "]: " + teamAnalysis.getRecommendations().get(i));
                            }
                        }
                    } else {
                        Log.w(TAG, "팀 분석 결과가 null입니다.");
                    }
                    
                    // 개별 점수 결과 로그 출력
                    if (result.getIndividualScores() != null) {
                        Log.d(TAG, "=== 개별 점수 결과 ===");
                        Log.d(TAG, "개별 점수 개수: " + result.getIndividualScores().size());
                        for (int i = 0; i < result.getIndividualScores().size(); i++) {
                            SynergyAnalysisResponse.IndividualScore score = result.getIndividualScores().get(i);
                            Log.d(TAG, "사용자 [" + (i+1) + "]: " + score.getName() + 
                                  ", 시너지 점수: " + score.getSynergyScore() + 
                                  ", 호환성: " + score.getCompatibility());
                        }
                    } else {
                        Log.w(TAG, "개별 점수 결과가 null입니다.");
                    }
                    
                    // 사용자 정보 로그 출력
                    if (result.getUsers() != null) {
                        Log.d(TAG, "=== 사용자 정보 ===");
                        Log.d(TAG, "사용자 정보 개수: " + result.getUsers().size());
                        for (int i = 0; i < result.getUsers().size(); i++) {
                            SynergyAnalysisResponse.User user = result.getUsers().get(i);
                            Log.d(TAG, "사용자 [" + (i+1) + "] - ID: " + user.getUserId() + 
                                  ", 이름: " + user.getName() + 
                                  ", 이메일: " + user.getEmail());
                        }
                    } else {
                        Log.w(TAG, "사용자 정보가 null입니다.");
                    }
                    
                    // UI 업데이트 (나중에 구현)
                    updateUIWithSynergyResult(result);
                    Log.d(TAG, "=== 시너지 분석 완료 ===");
                    
                } else {
                    Log.e(TAG, "=== 시너지 분석 API 실패 ===");
                    Log.e(TAG, "응답 코드: " + response.code());
                    Log.e(TAG, "응답 메시지: " + response.message());
                    // 실패시 더미 데이터 로드
                    loadDummyData();
                }
            }
            
            @Override
            public void onFailure(Call<SynergyAnalysisResponse> call, Throwable t) {
                Log.e(TAG, "=== 시너지 분석 API 네트워크 오류 ===");
                Log.e(TAG, "오류 메시지: " + t.getMessage());
                Log.e(TAG, "오류 타입: " + t.getClass().getSimpleName());
                // 실패시 더미 데이터 로드
                loadDummyData();
            }
        });
    }
    
    /**
     * 시너지 분석 결과로 UI를 업데이트하는 메서드 (나중에 구현)
     */
    private void updateUIWithSynergyResult(SynergyAnalysisResponse result) {
        // TODO: 실제 UI 업데이트 로직 구현
        Log.d(TAG, "UI 업데이트 메서드 호출됨");
    }
    
    /**
     * 더미 데이터를 로드하는 메서드
     */
    private void loadDummyData() {
        // 팀원 데이터 추가
        teamMemberList.clear();
        teamMemberList.add(new TeamMemberData("홍길동", "프론트엔드 개발자", "React, JavaScript, TypeScript", "리더형"));
        teamMemberList.add(new TeamMemberData("김영희", "백엔드 개발자", "Node.js, Express, MongoDB", "협력형"));
        teamMemberList.add(new TeamMemberData("이철수", "UI/UX 디자이너", "Figma, Sketch, Adobe XD", "창의형"));
        teamMemberList.add(new TeamMemberData("박민수", "DevOps 엔지니어", "Docker, AWS, Kubernetes", "분석형"));

        // 팀 시너지 점수 (백엔드에서 받아올 예정)
        updateSynergyScore(85);

        adapter.notifyDataSetChanged();
    }

    /**
     * UI 업데이트 메서드들
     */
    private void updateSynergyScore(int score) {
        if (tvSynergyScore != null) {
            tvSynergyScore.setText(String.valueOf(score));
        }
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
}
