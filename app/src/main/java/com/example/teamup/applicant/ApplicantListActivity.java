package com.example.teamup.applicant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.MainActivity;
import com.example.teamup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApplicantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApplicantAdapter adapter;
    private List<ApplicantData> applicantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_applicant_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_applicant_list);
    }

    private void setupRecyclerView() {
        applicantList = new ArrayList<>();
        loadApplicantsFromJson();
        
        adapter = new ApplicantAdapter(applicantList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadApplicantsFromJson() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.applicants);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray applicantsArray = jsonObject.getJSONArray("applicants");
            
            for (int i = 0; i < applicantsArray.length(); i++) {
                JSONObject applicant = applicantsArray.getJSONObject(i);
                
                String id = applicant.getString("id");
                String name = applicant.getString("name");
                String title = applicant.getString("title");
                String role = applicant.getString("role");
                String personality = applicant.getString("personality");
                String experience = applicant.getString("experience");
                String description = applicant.getString("description");
                
                JSONArray skillsArray = applicant.getJSONArray("skills");
                String[] skills = new String[skillsArray.length()];
                for (int j = 0; j < skillsArray.length(); j++) {
                    skills[j] = skillsArray.getString(j);
                }
                
                applicantList.add(new ApplicantData(id, name, title, role, skills, personality, experience, description));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // JSON 로드 실패시 기본 데이터 사용
            applicantList.add(new ApplicantData("1", "z_one_1", "배리어프리 앱 개발 콘테스트 지원", "풀스택 개발자", 
                new String[]{"파이썬", "Spring Boot", "기획"}, "리더형", "3년", "웹 개발 경험이 풍부합니다."));
        }
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btn_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_contest).setOnClickListener(v -> {
            // 공모전 화면으로 이동
        });

        findViewById(R.id.btn_board).setOnClickListener(v -> {
            // 게시판 화면으로 이동
        });

        findViewById(R.id.btn_my_page).setOnClickListener(v -> {
            // 마이페이지 화면으로 이동
        });
    }

    // 지원자 데이터 클래스
    public static class ApplicantData {
        private String id;
        private String name;
        private String title;
        private String role;
        private String[] skills;
        private String personality;
        private String experience;
        private String description;

        public ApplicantData(String id, String name, String title, String role, String[] skills, 
                           String personality, String experience, String description) {
            this.id = id;
            this.name = name;
            this.title = title;
            this.role = role;
            this.skills = skills;
            this.personality = personality;
            this.experience = experience;
            this.description = description;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getTitle() { return title; }
        public String getRole() { return role; }
        public String[] getSkills() { return skills; }
        public String getPersonality() { return personality; }
        public String getExperience() { return experience; }
        public String getDescription() { return description; }
    }

    // 지원자 목록 어댑터 클래스
    public static class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ViewHolder> {
        private List<ApplicantData> dataList;

        public ApplicantAdapter(List<ApplicantData> dataList) {
            this.dataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_applicant_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ApplicantData data = dataList.get(position);
            holder.tvApplicationTitle.setText(data.getTitle());
            holder.tvApplicantInfo.setText("지원자: " + data.getName());
            holder.tvRoleTag.setText(data.getRole());
            
            // 스킬 태그 설정 (최대 3개까지만 표시)
            int skillCount = Math.min(data.getSkills().length, 3);
            holder.tvSkillPython.setText(data.getSkills()[0]);
            if (skillCount > 1) {
                holder.tvSkillSpring.setText(data.getSkills()[1]);
            }
            if (skillCount > 2) {
                holder.tvSkillPlanning.setText(data.getSkills()[2]);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvApplicationTitle, tvApplicantInfo, tvRoleTag;
            TextView tvSkillPython, tvSkillSpring, tvSkillPlanning;

            public ViewHolder(View itemView) {
                super(itemView);
                tvApplicationTitle = itemView.findViewById(R.id.tv_application_title);
                tvApplicantInfo = itemView.findViewById(R.id.tv_applicant_info);
                tvRoleTag = itemView.findViewById(R.id.tv_role_tag);
                tvSkillPython = itemView.findViewById(R.id.tv_skill_python);
                tvSkillSpring = itemView.findViewById(R.id.tv_skill_spring);
                tvSkillPlanning = itemView.findViewById(R.id.tv_skill_planning);
            }
        }
    }
} 