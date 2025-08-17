package com.example.teamup.recruitment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.R;
import com.example.teamup.applicant.ApplicantListActivity;

import java.util.ArrayList;
import java.util.List;

public class TeamSynergyScoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TeamMemberAdapter adapter;
    private List<TeamMemberData> teamMemberList;
    private TextView tvSynergyScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_team_synergy_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadDummyData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_team_members);
        tvSynergyScore = findViewById(R.id.tv_synergy_score);
    }

    private void setupRecyclerView() {
        teamMemberList = new ArrayList<>();
        adapter = new TeamMemberAdapter(teamMemberList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // 지원자 추가 모집 버튼
        Button btnAddApplicant = findViewById(R.id.btn_add_applicant);
        if (btnAddApplicant != null) {
            btnAddApplicant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 지원자 추가 모집 화면으로 이동
                    Intent intent = new Intent(TeamSynergyScoreActivity.this, ApplicantListActivity.class);
                    startActivity(intent);
                }
            });
        }

        // 뒤로가기 화살표
        View backArrow = findViewById(R.id.iv_back_arrow);
        if (backArrow != null) {
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // 현재 액티비티 종료
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

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_team_member, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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

            public ViewHolder(View itemView) {
                super(itemView);
                tvMemberName = itemView.findViewById(R.id.tv_member_name);
                tvMemberRole = itemView.findViewById(R.id.tv_member_role);
                tvMemberSkills = itemView.findViewById(R.id.tv_member_skills);
                tvMemberType = itemView.findViewById(R.id.tv_member_type);
            }
        }
    }
} 