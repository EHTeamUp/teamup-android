package com.example.teamup.applicant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.example.teamup.recruitment.TeamSynergyScoreActivity;
import com.google.android.material.button.MaterialButton;

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
    private TextView tvSelectedCount;
    private MaterialButton btnSynergyCheck;

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
        setupSelectionControls();
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_applicant_list);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        btnSynergyCheck = findViewById(R.id.btn_synergy_check);
    }

    private void setupRecyclerView() {
        applicantList = new ArrayList<>();
        loadApplicantsFromJson();
        
        adapter = new ApplicantAdapter(applicantList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSelectionControls() {
        // 시너지 확인 버튼
        btnSynergyCheck.setOnClickListener(v -> {
            // 선택된 지원자들 시너지 확인
            List<ApplicantData> selectedApplicants = adapter.getSelectedApplicants();
            
            if (selectedApplicants.isEmpty()) {
                // 선택된 지원자가 없으면 Toast 메시지 표시
                Toast.makeText(this, "시너지를 확인해 볼 지원자를 눌러주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // TeamSynergyScoreActivity로 이동
            Intent intent = new Intent(this, TeamSynergyScoreActivity.class);
            // 선택된 지원자 정보를 전달 (필요한 경우)
            intent.putExtra("selected_count", selectedApplicants.size());
            startActivity(intent);
        });
    }

    public void updateSelectedCount() {
        int selectedCount = adapter.getSelectedCount();
        tvSelectedCount.setText(selectedCount + "명 선택됨");
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
        private List<Boolean> selectedItems;
        private ApplicantListActivity activity;

        public ApplicantAdapter(List<ApplicantData> dataList, ApplicantListActivity activity) {
            this.dataList = dataList;
            this.selectedItems = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                selectedItems.add(false);
            }
            this.activity = activity;
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
            
            // 지원자 이름을 제목으로 설정
            holder.tvApplicationTitle.setText(data.getName());
            holder.tvApplicantMessage.setText(data.getDescription());
            
            // 체크박스 상태 설정
            holder.cbApplicantSelect.setChecked(selectedItems.get(position));
            
            // 체크박스 리스너
            holder.cbApplicantSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                selectedItems.set(position, isChecked);
                activity.updateSelectedCount();
            });
            
            // 버튼 리스너들
            holder.btnAcceptTeam.setOnClickListener(v -> {
                // 개별 팀원 수락 처리
                // TODO: 수락 로직 구현
            });
            
            holder.btnReject.setOnClickListener(v -> {
                // 개별 거절 처리
                // TODO: 거절 로직 구현
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public int getSelectedCount() {
            int count = 0;
            for (Boolean selected : selectedItems) {
                if (selected) count++;
            }
            return count;
        }

        public List<ApplicantData> getSelectedApplicants() {
            List<ApplicantData> selected = new ArrayList<>();
            for (int i = 0; i < selectedItems.size(); i++) {
                if (selectedItems.get(i)) {
                    selected.add(dataList.get(i));
                }
            }
            return selected;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbApplicantSelect;
            TextView tvApplicationTitle, tvApplicantMessage;
            MaterialButton btnAcceptTeam, btnReject;

            public ViewHolder(View itemView) {
                super(itemView);
                cbApplicantSelect = itemView.findViewById(R.id.cb_applicant_select);
                tvApplicationTitle = itemView.findViewById(R.id.tv_application_title);
                tvApplicantMessage = itemView.findViewById(R.id.tv_applicant_message);
                btnAcceptTeam = itemView.findViewById(R.id.btn_accept_team);
                btnReject = itemView.findViewById(R.id.btn_reject);
            }
        }
    }
} 