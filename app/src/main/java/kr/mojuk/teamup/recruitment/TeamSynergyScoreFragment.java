package kr.mojuk.teamup.recruitment;

import android.os.Bundle;
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
    private TextView tvSynergyScore;
    private TokenManager tokenManager;

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
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadDummyData();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_team_members);
        tvSynergyScore = view.findViewById(R.id.tv_synergy_score);
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
        
        // 시너지 분석 API 호출
        performSynergyAnalysis();
    }

    /**
     * UI 업데이트 메서드들
     */
    private void updateSynergyScore(int score) {
        if (tvSynergyScore != null) {
            tvSynergyScore.setText(String.valueOf(score));
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

        // 시너지 분석 요청 생성
        SynergyAnalysisRequest request = new SynergyAnalysisRequest(userIds);

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
                            Log.d("TeamSynergyScore", "응답 메시지: " + synergyResponse.getMessage());
                            
                            if (synergyResponse.getUsers() != null) {
                                Log.d("TeamSynergyScore", "사용자 수: " + synergyResponse.getUsers().size());
                                for (SynergyAnalysisResponse.User user : synergyResponse.getUsers()) {
                                    Log.d("TeamSynergyScore", "사용자 ID: " + user.getUserId());
                                    Log.d("TeamSynergyScore", "사용자 이름: " + user.getName());
                                    Log.d("TeamSynergyScore", "사용자 이메일: " + user.getEmail());
                                }
                            }
                            
                            if (synergyResponse.getTeamAnalysis() != null) {
                                Log.d("TeamSynergyScore", "팀 시너지 점수: " + synergyResponse.getTeamAnalysis().getTeamSynergyScore());
                                Log.d("TeamSynergyScore", "분석 요약: " + synergyResponse.getTeamAnalysis().getAnalysisSummary());
                                
                                // UI에 시너지 점수 업데이트
                                Double synergyScore = synergyResponse.getTeamAnalysis().getTeamSynergyScore();
                                if (synergyScore != null) {
                                    updateSynergyScore(synergyScore.intValue());
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
