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
import com.example.teamup.api.ApiService;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.Application;
import com.example.teamup.api.model.ApplicationStatusUpdate;
import com.example.teamup.api.model.ApplicationReject;
import com.example.teamup.api.model.ApiResponse;
import com.example.teamup.api.model.RecruitmentPostResponse;
import com.example.teamup.api.model.ContestResponse;
import com.example.teamup.recruitment.TeamSynergyScoreActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplicantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApplicantAdapter adapter;
    private List<Application> applicantList;
    private TextView tvSelectedCount;
    private TextView tvNavigationText;
    private MaterialButton btnSynergyCheck;
    private MaterialButton btnAcceptSelected;
    private ApiService apiService;
    private int currentRecruitmentPostId = 3; // 현재 게시글 ID

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
        tvNavigationText = findViewById(R.id.tv_navigation_text);
        btnSynergyCheck = findViewById(R.id.btn_synergy_check);
        btnAcceptSelected = findViewById(R.id.btn_accept_selected);
        
        // API 서비스 초기화
        apiService = RetrofitClient.getInstance().getApiService();
    }

    private void setupRecyclerView() {
        applicantList = new ArrayList<>();
        
        adapter = new ApplicantAdapter(applicantList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // 모집 게시글 정보 로드 후 공모전 정보 로드
        loadRecruitmentPostInfo();
        
        // API에서 지원자 목록 로드
        loadApplicantsFromApi();
    }

    private void setupSelectionControls() {
        // 시너지 확인 버튼
        btnSynergyCheck.setOnClickListener(v -> {
            // 선택된 지원자들 시너지 확인
            List<Application> selectedApplicants = adapter.getSelectedApplicants();
            
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
        
        // 선택된 지원자 수락 버튼 (전체 수락)
        btnAcceptSelected.setOnClickListener(v -> {
            acceptSelectedApplicants();
        });
    }

    public void updateSelectedCount() {
        int selectedCount = adapter.getSelectedCount();
        tvSelectedCount.setText(selectedCount + "명 선택됨");
    }
    
    /**
     * 모집 게시글 정보를 로드하는 메서드
     */
    private void loadRecruitmentPostInfo() {
        Call<RecruitmentPostResponse> call = apiService.getRecruitmentPost(currentRecruitmentPostId);
        
        call.enqueue(new Callback<RecruitmentPostResponse>() {
            @Override
            public void onResponse(Call<RecruitmentPostResponse> call, Response<RecruitmentPostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecruitmentPostResponse post = response.body();
                    // 모집 게시글 정보를 받은 후 공모전 정보 로드
                    loadContestInfo(post.getContestId());
                } else {
                    // API 실패시 기본 텍스트 사용
                    tvNavigationText.setText(" 배리어프리 앱 개발 콘테스트");
                }
            }
            
            @Override
            public void onFailure(Call<RecruitmentPostResponse> call, Throwable t) {
                // 네트워크 오류시 기본 텍스트 사용
                tvNavigationText.setText(" 배리어프리 앱 개발 콘테스트");
            }
        });
    }
    
    /**
     * 공모전 정보를 로드하는 메서드
     */
    private void loadContestInfo(int contestId) {
        Call<ContestResponse> call = apiService.getContestDetail(contestId);
        
        call.enqueue(new Callback<ContestResponse>() {
            @Override
            public void onResponse(Call<ContestResponse> call, Response<ContestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ContestResponse contest = response.body();
                    // 디버깅을 위한 로그 추가
                    System.out.println("공모전 ID: " + contest.getContestId());
                    System.out.println("공모전 이름: " + contest.getName());
                    System.out.println("공모전 URL: " + contest.getContestUrl());
                    
                    // 네비게이션 텍스트에 공모전 이름 설정
                    tvNavigationText.setText(contest.getName());
                    
                    // UI 스레드에서 실행 확인
                    runOnUiThread(() -> {
                        tvNavigationText.setText(contest.getName());
                        System.out.println("UI에 설정된 이름: " + contest.getName());
                    });
                } else {
                    // API 실패시 기본 텍스트 사용
                    tvNavigationText.setText(" 배리어프리 앱 개발 콘테스트");
                    System.out.println("공모전 API 실패: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ContestResponse> call, Throwable t) {
                // 네트워크 오류시 기본 텍스트 사용
                tvNavigationText.setText(" 배리어프리 앱 개발 콘테스트");
                System.out.println("공모전 API 네트워크 오류: " + t.getMessage());
            }
        });
    }
    
    /**
     * API에서 지원자 목록을 로드하는 메서드
     */
    private void loadApplicantsFromApi() {
        // recruitment_post_id가 3인 게시글의 지원자 목록 조회
        Call<List<Application>> call = apiService.getApplicationsByPost(currentRecruitmentPostId);
        
        call.enqueue(new Callback<List<Application>>() {
            @Override
            public void onResponse(Call<List<Application>> call, Response<List<Application>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    applicantList.clear();
                    applicantList.addAll(response.body());
                    adapter.updateSelectionList();
                    adapter.notifyDataSetChanged();
                    
                    // 로딩 성공 메시지
//                    Toast.makeText(ApplicantListActivity.this,
//                        "지원자 목록을 성공적으로 불러왔습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // API 응답 실패시 기본 데이터 사용
                    loadDefaultApplicants();
                    Toast.makeText(ApplicantListActivity.this, 
                        "API 응답 실패. 기본 데이터를 사용합니다.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<Application>> call, Throwable t) {
                // 네트워크 오류시 기본 데이터 사용
                loadDefaultApplicants();
                Toast.makeText(ApplicantListActivity.this, 
                    "네트워크 오류. 기본 데이터를 사용합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 기본 지원자 데이터를 로드하는 메서드 (API 실패시 사용)
     */
    private void loadDefaultApplicants() {
        applicantList.clear();
        
        // 기본 지원자 데이터 생성
        Application defaultApp1 = new Application();
        defaultApp1.setApplicationId(1);
        defaultApp1.setUserId("user1");
        defaultApp1.setUserName("z_one_1");
        defaultApp1.setMessage("배리어프리 앱 개발 콘테스트 지원");
        defaultApp1.setUserRole("풀스택 개발자");
        defaultApp1.setUserPersonality("리더형");
        defaultApp1.setUserExperience("3년");
        defaultApp1.setUserSkills(new String[]{"파이썬", "Spring Boot", "기획"});
        
        Application defaultApp2 = new Application();
        defaultApp2.setApplicationId(2);
        defaultApp2.setUserId("user2");
        defaultApp2.setUserName("개발자_김철수");
        defaultApp2.setMessage("프론트엔드 개발 경험이 있습니다.");
        defaultApp2.setUserRole("프론트엔드 개발자");
        defaultApp2.setUserPersonality("협력형");
        defaultApp2.setUserExperience("2년");
        defaultApp2.setUserSkills(new String[]{"JavaScript", "React", "Vue.js"});
        
        applicantList.add(defaultApp1);
        applicantList.add(defaultApp2);
        
        adapter.updateSelectionList();
        adapter.notifyDataSetChanged();
    }
    
    /**
     * 특정 지원자를 수락하는 메서드 (개별 수락)
     */
    public void acceptApplicant(String userId, String userName) {
        // API 요청 데이터 생성 (한 명씩만 수락)
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        ApplicationStatusUpdate statusUpdate = new ApplicationStatusUpdate(currentRecruitmentPostId, userIds);
        
        // API 호출
        Call<ApiResponse> call = apiService.acceptApplications(statusUpdate);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ApplicantListActivity.this, 
                        response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    
                    // 수락된 지원자 상태 업데이트
                    updateApplicantStatus(userId, "accepted");
                } else {
                    Toast.makeText(ApplicantListActivity.this, 
                        "수락 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ApplicantListActivity.this, 
                    "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 수락된 지원자의 상태를 업데이트하는 메서드
     */
    private void updateApplicantStatus(String userId, String status) {
        for (int i = 0; i < applicantList.size(); i++) {
            if (applicantList.get(i).getUserId().equals(userId)) {
                applicantList.get(i).setStatus(status);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }
    
    /**
     * 선택된 모든 지원자를 수락하는 메서드 (전체 수락)
     */
    public void acceptSelectedApplicants() {
        List<Application> selectedApplicants = adapter.getSelectedApplicants();
        
        if (selectedApplicants.isEmpty()) {
            Toast.makeText(this, "수락할 지원자를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 선택된 지원자들의 user_id 목록 생성
        List<String> userIds = new ArrayList<>();
        for (Application applicant : selectedApplicants) {
            userIds.add(applicant.getUserId());
        }
        
        // API 요청 데이터 생성
        ApplicationStatusUpdate statusUpdate = new ApplicationStatusUpdate(currentRecruitmentPostId, userIds);
        
        // API 호출
        Call<ApiResponse> call = apiService.acceptApplications(statusUpdate);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ApplicantListActivity.this, 
                        response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    
                    // 선택된 모든 지원자 상태 업데이트
                    for (Application applicant : selectedApplicants) {
                        updateApplicantStatus(applicant.getUserId(), "accepted");
                    }
                } else {
                    Toast.makeText(ApplicantListActivity.this, 
                        "수락 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ApplicantListActivity.this, 
                    "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 특정 지원자를 거절하는 메서드
     */
    public void rejectApplicant(String userId, String userName) {
        // API 요청 데이터 생성
        ApplicationReject rejectData = new ApplicationReject(currentRecruitmentPostId, userId);
        
        // API 호출
        Call<ApiResponse> call = apiService.rejectApplication(rejectData);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ApplicantListActivity.this, 
                        response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    
                    // 거절된 지원자를 목록에서 제거
                    removeApplicantFromList(userId);
                } else {
                    Toast.makeText(ApplicantListActivity.this, 
                        "거절 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ApplicantListActivity.this, 
                    "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 거절된 지원자를 목록에서 제거하는 메서드
     */
    private void removeApplicantFromList(String userId) {
        for (int i = 0; i < applicantList.size(); i++) {
            if (applicantList.get(i).getUserId().equals(userId)) {
                applicantList.remove(i);
                // 선택 상태 리스트도 함께 업데이트
                adapter.updateSelectionList();
                adapter.notifyDataSetChanged();
                // 선택된 개수 업데이트
                updateSelectedCount();
                break;
            }
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



    // 지원자 목록 어댑터 클래스
    public static class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ViewHolder> {
        private List<Application> dataList;
        private List<Boolean> selectedItems;
        private ApplicantListActivity activity;

        public ApplicantAdapter(List<Application> dataList, ApplicantListActivity activity) {
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
            Application data = dataList.get(position);
            
            // 지원자 이름을 제목으로 설정 (userName이 없으면 userId 사용)
            String displayName = (data.getUserName() != null && !data.getUserName().isEmpty()) 
                ? data.getUserName() : data.getUserId();
            holder.tvApplicationTitle.setText(displayName);
            
            // 지원 메시지 설정
            holder.tvApplicantMessage.setText(data.getMessage());
            
            // 지원 상태에 따른 UI 처리
            String status = data.getStatus();
            if ("accepted".equals(status)) {
                // 수락된 지원자는 체크박스 선택 및 버튼 텍스트 변경
                holder.cbApplicantSelect.setEnabled(false);
                holder.cbApplicantSelect.setChecked(true);
                holder.btnAcceptTeam.setVisibility(View.VISIBLE);
                holder.btnAcceptTeam.setText("수락 완료");
                holder.btnAcceptTeam.setEnabled(false);
                holder.btnReject.setVisibility(View.GONE);
                // 수락된 지원자는 선택 상태로 설정
                selectedItems.set(position, true);
            } else if ("rejected".equals(status)) {
                // 거절된 지원자는 목록에서 제거되므로 이 부분은 실행되지 않음
                holder.cbApplicantSelect.setEnabled(false);
                holder.btnAcceptTeam.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
            } else {
                // 대기 중인 지원자는 정상 처리
                holder.cbApplicantSelect.setEnabled(true);
                holder.btnAcceptTeam.setVisibility(View.VISIBLE);
                holder.btnAcceptTeam.setText("수락");
                holder.btnAcceptTeam.setEnabled(true);
                holder.btnReject.setVisibility(View.VISIBLE);
                // 대기 중인 지원자는 선택 상태에 따라 설정
                holder.cbApplicantSelect.setChecked(selectedItems.get(position));
            }
            
            // 체크박스 리스너
            holder.cbApplicantSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (holder.cbApplicantSelect.isEnabled()) {
                    selectedItems.set(position, isChecked);
                    activity.updateSelectedCount();
                }
            });
            
            // 버튼 리스너들 - 개별 수락과 거절 버튼 표시
            holder.btnAcceptTeam.setOnClickListener(v -> {
                // 개별 수락 처리
                activity.acceptApplicant(data.getUserId(), displayName);
            });
            
            holder.btnReject.setOnClickListener(v -> {
                // 개별 거절 처리
                activity.rejectApplicant(data.getUserId(), displayName);
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

        public List<Application> getSelectedApplicants() {
            List<Application> selected = new ArrayList<>();
            for (int i = 0; i < selectedItems.size(); i++) {
                if (selectedItems.get(i)) {
                    Application app = dataList.get(i);
                    // 대기 중인 지원자만 선택 가능
                    if ("pending".equals(app.getStatus())) {
                        selected.add(app);
                    }
                }
            }
            return selected;
        }
        
        /**
         * 데이터가 변경될 때 선택 상태를 업데이트하는 메서드
         */
        public void updateSelectionList() {
            selectedItems.clear();
            for (int i = 0; i < dataList.size(); i++) {
                selectedItems.add(false);
            }
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