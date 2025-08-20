package com.example.teamup.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.example.teamup.adapters.ChipPagerAdapter;
import com.example.teamup.auth.RegistrationManager;
import com.example.teamup.auth.ProfileManager;
import com.example.teamup.api.model.Skill;
import com.example.teamup.api.model.Role;
import com.example.teamup.api.model.UserSkillsResponse;
import com.example.teamup.api.model.UserRolesResponse;
import com.example.teamup.api.model.SkillUpdate;
import com.example.teamup.api.model.RoleUpdate;
import com.example.teamup.api.model.ProfileUpdateResponse;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.auth.TokenManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 마이페이지 관심사 설정 Fragment
 */
public class MypageInterestFragment extends Fragment {

    private static final String TAG = "MypageInterestFragment";
    private static final int ITEMS_PER_PAGE = 10;
    
    // Fragment 매개변수 키
    private static final String ARG_SOURCE = "source";
    private static final String ARG_USER_ID = "user_id";
    
    // 소스 타입
    public static final String SOURCE_MYPAGE = "mypage";
    public static final String SOURCE_SIGNUP = "signup";
    
    // Fragment 매개변수
    private String source;
    private String userId;
    
    // Back button
    private TextView tvBackArrow;
    private LinearLayout llBackNavigation;
    
    // Language related views
    private ScrollView scrollViewSkills;
    private ChipGroup chipGroupLanguages;
    private EditText etLanguageInput;
    private Button btnAddLanguage;
    
    // Skills pagination
    private LinearLayout llSkillsPageIndicator;
    private int currentSkillsPage = 0;
    private List<Skill> allSkills = new ArrayList<>();
    
    // Role related views
    private ScrollView scrollViewRoles;
    private ChipGroup chipGroupRoles;
    private EditText etRoleInput;
    private Button btnAddRole;
    
    // Roles pagination
    private LinearLayout llRolesPageIndicator;
    private int currentRolesPage = 0;
    private List<Role> allRoles = new ArrayList<>();
    
    // 커스텀 추가 리스트
    private List<String> customSkills = new ArrayList<>();
    private List<String> customRoles = new ArrayList<>();
    
    // 선택된 아이템들 (마이페이지용) - 중복 방지를 위해 Set 사용
    private Set<Integer> selectedSkillIds = new HashSet<>();
    private Set<Integer> selectedRoleIds = new HashSet<>();
    private List<String> selectedCustomSkills = new ArrayList<>();
    private List<String> selectedCustomRoles = new ArrayList<>();
    
    // 제스처 감지
    private GestureDetector skillsGestureDetector;
    private GestureDetector rolesGestureDetector;
    
    // Manager
    private RegistrationManager registrationManager;

    /**
     * Fragment 생성 메서드
     */
    public static MypageInterestFragment newInstance(String source, String userId) {
        MypageInterestFragment fragment = new MypageInterestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SOURCE, source);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypage_interest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 매개변수 읽기
        if (getArguments() != null) {
            source = getArguments().getString(ARG_SOURCE, SOURCE_SIGNUP);
            userId = getArguments().getString(ARG_USER_ID, "");
            Log.d(TAG, "매개변수 읽기 성공: source=" + source + ", userId=" + userId);
        } else {
            Log.w(TAG, "매개변수가 없음 - 기본값 사용: source=" + SOURCE_SIGNUP);
            source = SOURCE_SIGNUP;
            userId = "";
        }
        
        Log.d(TAG, "Fragment 생성 완료: source=" + source + ", userId=" + userId);

        // Manager 초기화
        registrationManager = RegistrationManager.getInstance();

        initViews(view);
        setupGestureDetectors();
        setClickListeners();
        
        // 소스에 따라 다른 동작
        if (SOURCE_MYPAGE.equals(source)) {
            loadUserSkillsAndRoles(); // 마이페이지: 사용자 기존 데이터 로드
        } else {
            loadSkillsAndRoles(); // 회원가입: 전체 스킬/역할 로드
        }
    }

    private void initViews(View view) {
        // Back button
        tvBackArrow = view.findViewById(R.id.tv_back_arrow);
        llBackNavigation = view.findViewById(R.id.ll_back_navigation);
        
        // Language views
        scrollViewSkills = view.findViewById(R.id.scrollViewSkills);
        chipGroupLanguages = view.findViewById(R.id.chipGroupLanguages);
        etLanguageInput = view.findViewById(R.id.et_language_input);
        btnAddLanguage = view.findViewById(R.id.btn_add_language);
        
        // Skills pagination views
        llSkillsPageIndicator = view.findViewById(R.id.ll_skills_page_indicator);
        
        // Role views
        scrollViewRoles = view.findViewById(R.id.scrollViewRoles);
        chipGroupRoles = view.findViewById(R.id.chipGroupRoles);
        etRoleInput = view.findViewById(R.id.et_role_input);
        btnAddRole = view.findViewById(R.id.btn_add_role);
        
        // Roles pagination views
        llRolesPageIndicator = view.findViewById(R.id.ll_roles_page_indicator);
        
        // Signup 모드일 때 뒤로가기 버튼 숨기기
        if (SOURCE_SIGNUP.equals(source)) {
            if (llBackNavigation != null) {
                llBackNavigation.setVisibility(View.GONE);
            }
        }
    }

    private void setupGestureDetectors() {
        // 기술 스와이프 제스처 감지
        skillsGestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "Skills onFling: velocityX=" + velocityX + ", velocityY=" + velocityY);
                
                // 최소 속도 임계값 설정
                float minVelocity = 100f;
                float minDistance = 50f;
                
                if (e1 != null && e2 != null) {
                    float distanceX = e2.getX() - e1.getX();
                    float distanceY = e2.getY() - e1.getY();
                    
                    if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > minDistance) {
                        if (distanceX > 0 && velocityX > minVelocity) {
                            // 오른쪽으로 스와이프 - 이전 페이지
                            Log.d(TAG, "Skills: 오른쪽 스와이프 감지");
                            previousSkillsPage();
                            return true;
                        } else if (distanceX < 0 && velocityX < -minVelocity) {
                            // 왼쪽으로 스와이프 - 다음 페이지
                            Log.d(TAG, "Skills: 왼쪽 스와이프 감지");
                            nextSkillsPage();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        // 역할 스와이프 제스처 감지
        rolesGestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "Roles onFling: velocityX=" + velocityX + ", velocityY=" + velocityY);
                
                // 최소 속도 임계값 설정
                float minVelocity = 100f;
                float minDistance = 50f;
                
                if (e1 != null && e2 != null) {
                    float distanceX = e2.getX() - e1.getX();
                    float distanceY = e2.getY() - e1.getY();
                    
                    if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > minDistance) {
                        if (distanceX > 0 && velocityX > minVelocity) {
                            // 오른쪽으로 스와이프 - 이전 페이지
                            Log.d(TAG, "Roles: 오른쪽 스와이프 감지");
                            previousRolesPage();
                            return true;
                        } else if (distanceX < 0 && velocityX < -minVelocity) {
                            // 왼쪽으로 스와이프 - 다음 페이지
                            Log.d(TAG, "Roles: 왼쪽 스와이프 감지");
                            nextRolesPage();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        // ScrollView에 터치 리스너 설정
        scrollViewSkills.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean handled = skillsGestureDetector.onTouchEvent(event);
                if (!handled) {
                    // 제스처가 처리되지 않으면 기본 터치 동작 수행
                    v.onTouchEvent(event);
                }
                return handled;
            }
        });

        scrollViewRoles.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean handled = rolesGestureDetector.onTouchEvent(event);
                if (!handled) {
                    // 제스처가 처리되지 않으면 기본 터치 동작 수행
                    v.onTouchEvent(event);
                }
                return handled;
            }
        });
    }

    private void setClickListeners() {
        // 뒤로가기 버튼
        llBackNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지 프로필로 돌아가기
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showFragment(new MypageProfileFragment());
                }
            }
        });

        btnAddLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLanguage = etLanguageInput.getText().toString().trim();
                if (!newLanguage.isEmpty()) {
                    addLanguageChip(newLanguage);
                    etLanguageInput.setText("");
                }
            }
        });

        btnAddRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newRole = etRoleInput.getText().toString().trim();
                if (!newRole.isEmpty()) {
                    addRoleChip(newRole);
                    etRoleInput.setText("");
                }
            }
        });


    }

    private void addLanguageChip(String language) {
        if (!language.trim().isEmpty()) {
            customSkills.add(language.trim());
            Log.d(TAG, "커스텀 스킬 추가: " + language + ", 총 커스텀 스킬: " + customSkills.size());
            
            // 페이징 재계산
            int totalSkills = allSkills.size() + customSkills.size();
            int totalPages = (int) Math.ceil((double) totalSkills / ITEMS_PER_PAGE);
            
            // 새로 추가된 아이템이 있는 페이지로 이동
            if (totalPages > 1) {
                currentSkillsPage = totalPages - 1;
                Log.d(TAG, "커스텀 스킬 추가 후 페이지 이동: " + currentSkillsPage + "/" + (totalPages-1));
            }
            
            updateSkillsPage();
            setupSkillsPagination();
        }
    }

    private void addRoleChip(String role) {
        if (!role.trim().isEmpty()) {
            customRoles.add(role.trim());
            Log.d(TAG, "커스텀 역할 추가: " + role + ", 총 커스텀 역할: " + customRoles.size());
            
            // 페이징 재계산
            int totalRoles = allRoles.size() + customRoles.size();
            int totalPages = (int) Math.ceil((double) totalRoles / ITEMS_PER_PAGE);
            
            // 새로 추가된 아이템이 있는 페이지로 이동
            if (totalPages > 1) {
                currentRolesPage = totalPages - 1;
                Log.d(TAG, "커스텀 역할 추가 후 페이지 이동: " + currentRolesPage + "/" + (totalPages-1));
            }
            
            updateRolesPage();
            setupRolesPagination();
        }
    }

    /**
     * 마이페이지용: 사용자 기존 스킬/역할 로드
     */
    private void loadUserSkillsAndRoles() {
        Log.d(TAG, "마이페이지 모드: 전체 스킬/역할 로드 후 사용자 데이터 로드");
        
        // 먼저 전체 스킬 목록을 로드
        registrationManager.getAvailableSkills(new RegistrationManager.SkillsCallback() {
            @Override
            public void onSuccess(List<Skill> skills) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allSkills = skills;
                        Log.d(TAG, "전체 스킬 로드 완료: " + allSkills.size() + "개");
                        
                        // 전체 스킬 로드 후 사용자 기존 스킬 로드
                        loadUserSkills();
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "전체 스킬 로드 실패: " + errorMessage);
            }
        });
        
        // 먼저 전체 역할 목록을 로드
        registrationManager.getAvailableRoles(new RegistrationManager.RolesCallback() {
            @Override
            public void onSuccess(List<Role> roles) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allRoles = roles;
                        Log.d(TAG, "전체 역할 로드 완료: " + allRoles.size() + "개");
                        
                        // 전체 역할 로드 후 사용자 기존 역할 로드
                        loadUserRoles();
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "전체 역할 로드 실패: " + errorMessage);
            }
        });
    }
    
    /**
     * 사용자 기존 스킬 로드
     */
    private void loadUserSkills() {
        ProfileManager.getInstance(requireContext()).getUserSkills(requireContext(), new ProfileManager.UserSkillsCallback() {
            @Override
            public void onSuccess(UserSkillsResponse skills) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(TAG, "사용자 스킬 로드 성공: " + skills.getSkillIds().size() + "개");
                        Log.d(TAG, "API 응답 스킬 IDs: " + skills.getSkillIds().toString());
                        
                        // 기존 스킬들을 선택된 상태로 표시 (Set이므로 중복 자동 제거)
                        int beforeSize = selectedSkillIds.size();
                        selectedSkillIds.addAll(skills.getSkillIds());
                        int afterSize = selectedSkillIds.size();
                        
                        Log.d(TAG, "스킬 추가 전: " + beforeSize + "개, 추가 후: " + afterSize + "개");
                        Log.d(TAG, "최종 selectedSkillIds: " + selectedSkillIds.toString());
                        
                        // 커스텀 스킬들도 추가
                        customSkills.addAll(skills.getCustomSkills());
                        Log.d(TAG, "커스텀 스킬: " + customSkills.size() + "개");
                        
                        currentSkillsPage = 0;
                        updateSkillsPage();
                        setupSkillsPagination();
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "사용자 스킬 로드 실패: " + errorMessage);
                // 실패해도 전체 스킬은 표시
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        currentSkillsPage = 0;
                        updateSkillsPage();
                        setupSkillsPagination();
                    });
                }
            }
        });
    }
    
    /**
     * 사용자 기존 역할 로드
     */
    private void loadUserRoles() {
        ProfileManager.getInstance(requireContext()).getUserRoles(requireContext(), new ProfileManager.UserRolesCallback() {
            @Override
            public void onSuccess(UserRolesResponse roles) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(TAG, "사용자 역할 로드 성공: " + roles.getRoleIds().size() + "개");
                        Log.d(TAG, "API 응답 역할 IDs: " + roles.getRoleIds().toString());
                        
                        // 기존 역할들을 선택된 상태로 표시 (Set이므로 중복 자동 제거)
                        int beforeSize = selectedRoleIds.size();
                        selectedRoleIds.addAll(roles.getRoleIds());
                        int afterSize = selectedRoleIds.size();
                        
                        Log.d(TAG, "역할 추가 전: " + beforeSize + "개, 추가 후: " + afterSize + "개");
                        Log.d(TAG, "최종 selectedRoleIds: " + selectedRoleIds.toString());
                        
                        // 커스텀 역할들도 추가
                        customRoles.addAll(roles.getCustomRoles());
                        Log.d(TAG, "커스텀 역할: " + customRoles.size() + "개");
                        
                        currentRolesPage = 0;
                        updateRolesPage();
                        setupRolesPagination();
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "사용자 역할 로드 실패: " + errorMessage);
                // 실패해도 전체 역할은 표시
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        currentRolesPage = 0;
                        updateRolesPage();
                        setupRolesPagination();
                    });
                }
            }
        });
    }

    /**
     * API에서 기술과 역할 데이터 로드 (회원가입용)
     */
    private void loadSkillsAndRoles() {
        // 기술 데이터 로드
        registrationManager.getAvailableSkills(new RegistrationManager.SkillsCallback() {
            @Override
            public void onSuccess(List<Skill> skills) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allSkills = skills;
                        currentSkillsPage = 0;
                        updateSkillsPage();
                        setupSkillsPagination();
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                // 에러 시 기본 칩들 유지
            }
        });

        // 역할 데이터 로드
        registrationManager.getAvailableRoles(new RegistrationManager.RolesCallback() {
            @Override
            public void onSuccess(List<Role> roles) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allRoles = roles;
                        currentRolesPage = 0;
                        updateRolesPage();
                        setupRolesPagination();
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                // 에러 시 기본 칩들 유지
            }
        });
    }

    /**
     * 기술 페이지 업데이트
     */
    private void updateSkillsPage() {
        chipGroupLanguages.removeAllViews();
        
        // 전체 아이템 리스트 생성 (기존 스킬 + 커스텀 스킬)
        List<Object> allItems = new ArrayList<>();
        allItems.addAll(allSkills);
        allItems.addAll(customSkills);
        
        int totalSkills = allItems.size();
        int startIndex = currentSkillsPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalSkills);
        
        Log.d(TAG, "updateSkillsPage: page=" + currentSkillsPage + ", total=" + totalSkills + ", showing " + startIndex + " to " + (endIndex-1));
        
        // 현재 페이지에 해당하는 아이템들만 표시
        for (int i = startIndex; i < endIndex; i++) {
            Object item = allItems.get(i);
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.view_chip_choice, chipGroupLanguages, false);
            
            if (item instanceof Skill) {
                Skill skill = (Skill) item;
                chip.setText(skill.getName());
                chip.setTag(skill.getSkillId());
                
                // 마이페이지 모드에서 선택 상태 표시
                if (SOURCE_MYPAGE.equals(source) && selectedSkillIds.contains(skill.getSkillId())) {
                    chip.setChecked(true);
                }
            } else if (item instanceof String) {
                String customSkill = (String) item;
                chip.setText(customSkill);
                chip.setTag("custom_" + customSkill);
                
                // 마이페이지 모드에서 선택 상태 표시
                if (SOURCE_MYPAGE.equals(source) && selectedCustomSkills.contains(customSkill)) {
                    chip.setChecked(true);
                }
            }
            
            // Chip 클릭 리스너 설정
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(TAG, "스킬 Chip 클릭: " + chip.getText() + ", isChecked=" + isChecked + ", source=" + source);
                if (SOURCE_MYPAGE.equals(source)) {
                    Log.d(TAG, "마이페이지 모드 - handleSkillSelection 호출");
                    handleSkillSelection(chip, isChecked);
                } else {
                    Log.d(TAG, "회원가입 모드 - 선택 데이터 저장");
                    handleSkillSelectionForSignup(chip, isChecked);
                }
            });
            
            chipGroupLanguages.addView(chip);
        }
        
        // 페이지 인디케이터 업데이트
        int totalPages = (int) Math.ceil((double) totalSkills / ITEMS_PER_PAGE);
        updateSkillsPageIndicator(totalPages);
        
        Log.d(TAG, "updateSkillsPage: 표시된 칩 개수=" + chipGroupLanguages.getChildCount());
    }

    /**
     * 역할 페이지 업데이트
     */
    private void updateRolesPage() {
        chipGroupRoles.removeAllViews();
        
        // 전체 아이템 리스트 생성 (기존 역할 + 커스텀 역할)
        List<Object> allItems = new ArrayList<>();
        allItems.addAll(allRoles);
        allItems.addAll(customRoles);
        
        int totalRoles = allItems.size();
        int startIndex = currentRolesPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalRoles);
        
        Log.d(TAG, "updateRolesPage: page=" + currentRolesPage + ", total=" + totalRoles + ", showing " + startIndex + " to " + (endIndex-1));
        
        // 현재 페이지에 해당하는 아이템들만 표시
        for (int i = startIndex; i < endIndex; i++) {
            Object item = allItems.get(i);
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.view_chip_choice, chipGroupRoles, false);
            
            if (item instanceof Role) {
                Role role = (Role) item;
                chip.setText(role.getName());
                chip.setTag(role.getRoleId());
                
                // 마이페이지 모드에서 선택 상태 표시
                if (SOURCE_MYPAGE.equals(source) && selectedRoleIds.contains(role.getRoleId())) {
                    chip.setChecked(true);
                }
            } else if (item instanceof String) {
                String customRole = (String) item;
                chip.setText(customRole);
                chip.setTag("custom_" + customRole);
                
                // 마이페이지 모드에서 선택 상태 표시
                if (SOURCE_MYPAGE.equals(source) && selectedCustomRoles.contains(customRole)) {
                    chip.setChecked(true);
                }
            }
            
            // Chip 클릭 리스너 설정
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(TAG, "역할 Chip 클릭: " + chip.getText() + ", isChecked=" + isChecked + ", source=" + source);
                if (SOURCE_MYPAGE.equals(source)) {
                    Log.d(TAG, "마이페이지 모드 - handleRoleSelection 호출");
                    handleRoleSelection(chip, isChecked);
                } else {
                    Log.d(TAG, "회원가입 모드 - 선택 데이터 저장");
                    handleRoleSelectionForSignup(chip, isChecked);
                }
            });
            
            chipGroupRoles.addView(chip);
        }
        
        // 페이지 인디케이터 업데이트
        int totalPages = (int) Math.ceil((double) totalRoles / ITEMS_PER_PAGE);
        updateRolesPageIndicator(totalPages);
        
        Log.d(TAG, "updateRolesPage: 표시된 칩 개수=" + chipGroupRoles.getChildCount());
    }

    /**
     * 기술 페이징 설정
     */
    private void setupSkillsPagination() {
        int totalSkills = allSkills.size() + customSkills.size();
        int totalPages = (int) Math.ceil((double) totalSkills / ITEMS_PER_PAGE);
        Log.d(TAG, "setupSkillsPagination: totalPages=" + totalPages);
        if (totalPages > 1) {
            llSkillsPageIndicator.setVisibility(View.VISIBLE);
            Log.d(TAG, "setupSkillsPagination: 인디케이터 VISIBLE로 설정");
        } else {
            llSkillsPageIndicator.setVisibility(View.GONE);
            Log.d(TAG, "setupSkillsPagination: 인디케이터 GONE으로 설정");
        }
    }

    /**
     * 역할 페이징 설정
     */
    private void setupRolesPagination() {
        int totalRoles = allRoles.size() + customRoles.size();
        int totalPages = (int) Math.ceil((double) totalRoles / ITEMS_PER_PAGE);
        if (totalPages > 1) {
            llRolesPageIndicator.setVisibility(View.VISIBLE);
        } else {
            llRolesPageIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * 기술 페이지 인디케이터 업데이트
     */
    private void updateSkillsPageIndicator(int totalPages) {
        Log.d(TAG, "updateSkillsPageIndicator: totalPages=" + totalPages + ", currentSkillsPage=" + currentSkillsPage);
        
        llSkillsPageIndicator.removeAllViews();
        
        for (int i = 0; i < totalPages; i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            
            if (i == currentSkillsPage) {
                dot.setBackgroundResource(android.R.drawable.radiobutton_on_background);
            } else {
                dot.setBackgroundResource(android.R.drawable.radiobutton_off_background);
            }
            
            llSkillsPageIndicator.addView(dot);
        }
        
        Log.d(TAG, "updateSkillsPageIndicator: 인디케이터 업데이트 완료, 자식 뷰 개수=" + llSkillsPageIndicator.getChildCount());
    }

    /**
     * 역할 페이지 인디케이터 업데이트
     */
    private void updateRolesPageIndicator(int totalPages) {
        llRolesPageIndicator.removeAllViews();
        
        for (int i = 0; i < totalPages; i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            
            if (i == currentRolesPage) {
                dot.setBackgroundResource(android.R.drawable.radiobutton_on_background);
            } else {
                dot.setBackgroundResource(android.R.drawable.radiobutton_off_background);
            }
            
            llRolesPageIndicator.addView(dot);
        }
    }

    /**
     * 이전 기술 페이지로 이동
     */
    private void previousSkillsPage() {
        int totalSkills = allSkills.size() + customSkills.size();
        int totalPages = (int) Math.ceil((double) totalSkills / ITEMS_PER_PAGE);
        
        if (currentSkillsPage > 0) {
            currentSkillsPage--;
            updateSkillsPage();
            Log.d(TAG, "previousSkillsPage: " + currentSkillsPage);
        }
    }

    /**
     * 다음 기술 페이지로 이동
     */
    private void nextSkillsPage() {
        int totalSkills = allSkills.size() + customSkills.size();
        int totalPages = (int) Math.ceil((double) totalSkills / ITEMS_PER_PAGE);
        
        if (currentSkillsPage < totalPages - 1) {
            currentSkillsPage++;
            updateSkillsPage();
            Log.d(TAG, "nextSkillsPage: " + currentSkillsPage);
        }
    }

    /**
     * 이전 역할 페이지로 이동
     */
    private void previousRolesPage() {
        int totalRoles = allRoles.size() + customRoles.size();
        int totalPages = (int) Math.ceil((double) totalRoles / ITEMS_PER_PAGE);
        
        if (currentRolesPage > 0) {
            currentRolesPage--;
            updateRolesPage();
            Log.d(TAG, "previousRolesPage: " + currentRolesPage);
        }
    }

    /**
     * 다음 역할 페이지로 이동
     */
    private void nextRolesPage() {
        int totalRoles = allRoles.size() + customRoles.size();
        int totalPages = (int) Math.ceil((double) totalRoles / ITEMS_PER_PAGE);
        
        if (currentRolesPage < totalPages - 1) {
            currentRolesPage++;
            updateRolesPage();
            Log.d(TAG, "nextRolesPage: " + currentRolesPage);
        }
    }

    /**
     * 스킬 선택 처리 (마이페이지용)
     */
    private void handleSkillSelection(Chip chip, boolean isChecked) {
        Object tag = chip.getTag();
        Log.d(TAG, "=== 스킬 선택 처리 시작 ===");
        Log.d(TAG, "Chip 텍스트: " + chip.getText() + ", isChecked: " + isChecked + ", tag: " + tag);
        
        if (tag instanceof Integer) {
            Integer skillId = (Integer) tag;
            Log.d(TAG, "기존 스킬 처리: skillId=" + skillId);
            Log.d(TAG, "처리 전 selectedSkillIds: " + selectedSkillIds.toString());
            
            if (isChecked) {
                if (!selectedSkillIds.contains(skillId)) {
                    selectedSkillIds.add(skillId);
                    Log.d(TAG, "스킬 추가됨: " + skillId);
                } else {
                    Log.d(TAG, "스킬 이미 선택되어 있음: " + skillId);
                }
            } else {
                boolean removed = selectedSkillIds.remove(skillId);
                Log.d(TAG, "스킬 제거 시도: " + skillId + ", 제거됨: " + removed);
            }
            
            Log.d(TAG, "처리 후 selectedSkillIds: " + selectedSkillIds.toString());
            Log.d(TAG, "총 선택된 스킬: " + selectedSkillIds.size());
            
            // DB 업데이트
            updateUserSkills();
        } else if (tag instanceof String && ((String) tag).startsWith("custom_")) {
            String customSkill = ((String) tag).substring(7); // "custom_" 제거
            Log.d(TAG, "커스텀 스킬 처리: customSkill=" + customSkill);
            Log.d(TAG, "처리 전 selectedCustomSkills: " + selectedCustomSkills.toString());
            
            if (isChecked) {
                if (!selectedCustomSkills.contains(customSkill)) {
                    selectedCustomSkills.add(customSkill);
                    Log.d(TAG, "커스텀 스킬 추가됨: " + customSkill);
                } else {
                    Log.d(TAG, "커스텀 스킬 이미 선택되어 있음: " + customSkill);
                }
            } else {
                boolean removed = selectedCustomSkills.remove(customSkill);
                Log.d(TAG, "커스텀 스킬 제거 시도: " + customSkill + ", 제거됨: " + removed);
            }
            
            Log.d(TAG, "처리 후 selectedCustomSkills: " + selectedCustomSkills.toString());
            
            // DB 업데이트
            updateUserSkills();
        } else {
            Log.w(TAG, "알 수 없는 tag 형식: " + tag);
        }
        Log.d(TAG, "=== 스킬 선택 처리 끝 ===");
    }

    /**
     * 역할 선택 처리 (마이페이지용)
     */
    private void handleRoleSelection(Chip chip, boolean isChecked) {
        Object tag = chip.getTag();
        Log.d(TAG, "=== 역할 선택 처리 시작 ===");
        Log.d(TAG, "Chip 텍스트: " + chip.getText() + ", isChecked: " + isChecked + ", tag: " + tag);
        
        if (tag instanceof Integer) {
            Integer roleId = (Integer) tag;
            Log.d(TAG, "기존 역할 처리: roleId=" + roleId);
            Log.d(TAG, "처리 전 selectedRoleIds: " + selectedRoleIds.toString());
            
            if (isChecked) {
                if (!selectedRoleIds.contains(roleId)) {
                    selectedRoleIds.add(roleId);
                    Log.d(TAG, "역할 추가됨: " + roleId);
                } else {
                    Log.d(TAG, "역할 이미 선택되어 있음: " + roleId);
                }
            } else {
                boolean removed = selectedRoleIds.remove(roleId);
                Log.d(TAG, "역할 제거 시도: " + roleId + ", 제거됨: " + removed);
            }
            
            Log.d(TAG, "처리 후 selectedRoleIds: " + selectedRoleIds.toString());
            Log.d(TAG, "총 선택된 역할: " + selectedRoleIds.size());
            
            // DB 업데이트
            updateUserRoles();
        } else if (tag instanceof String && ((String) tag).startsWith("custom_")) {
            String customRole = ((String) tag).substring(7); // "custom_" 제거
            Log.d(TAG, "커스텀 역할 처리: customRole=" + customRole);
            Log.d(TAG, "처리 전 selectedCustomRoles: " + selectedCustomRoles.toString());
            
            if (isChecked) {
                if (!selectedCustomRoles.contains(customRole)) {
                    selectedCustomRoles.add(customRole);
                    Log.d(TAG, "커스텀 역할 추가됨: " + customRole);
                } else {
                    Log.d(TAG, "커스텀 역할 이미 선택되어 있음: " + customRole);
                }
            } else {
                boolean removed = selectedCustomRoles.remove(customRole);
                Log.d(TAG, "커스텀 역할 제거 시도: " + customRole + ", 제거됨: " + removed);
            }
            
            Log.d(TAG, "처리 후 selectedCustomRoles: " + selectedCustomRoles.toString());
            
            // DB 업데이트
            updateUserRoles();
        } else {
            Log.w(TAG, "알 수 없는 tag 형식: " + tag);
        }
        Log.d(TAG, "=== 역할 선택 처리 끝 ===");
    }

    /**
     * 스킬 선택 처리 (회원가입용)
     */
    private void handleSkillSelectionForSignup(Chip chip, boolean isChecked) {
        Object tag = chip.getTag();
        Log.d(TAG, "=== 회원가입 스킬 선택 처리 시작 ===");
        Log.d(TAG, "Chip 텍스트: " + chip.getText() + ", isChecked: " + isChecked + ", tag: " + tag);
        
        if (tag instanceof Integer) {
            Integer skillId = (Integer) tag;
            Log.d(TAG, "기존 스킬 처리: skillId=" + skillId);
            
            if (isChecked) {
                if (!selectedSkillIds.contains(skillId)) {
                    selectedSkillIds.add(skillId);
                    Log.d(TAG, "스킬 추가됨: " + skillId);
                } else {
                    Log.d(TAG, "스킬 이미 선택되어 있음: " + skillId);
                }
            } else {
                boolean removed = selectedSkillIds.remove(skillId);
                Log.d(TAG, "스킬 제거됨: " + skillId + ", 제거 성공: " + removed);
            }
        } else if (tag instanceof String && ((String) tag).startsWith("custom_")) {
            String customSkill = ((String) tag).substring(7); // "custom_" 제거
            Log.d(TAG, "커스텀 스킬 처리: customSkill=" + customSkill);
            
            if (isChecked) {
                if (!selectedCustomSkills.contains(customSkill)) {
                    selectedCustomSkills.add(customSkill);
                    Log.d(TAG, "커스텀 스킬 추가됨: " + customSkill);
                } else {
                    Log.d(TAG, "커스텀 스킬 이미 선택되어 있음: " + customSkill);
                }
            } else {
                boolean removed = selectedCustomSkills.remove(customSkill);
                Log.d(TAG, "커스텀 스킬 제거됨: " + customSkill + ", 제거 성공: " + removed);
            }
        }
        
        Log.d(TAG, "현재 선택된 스킬 IDs: " + selectedSkillIds.toString());
        Log.d(TAG, "현재 선택된 커스텀 스킬: " + selectedCustomSkills.toString());
        Log.d(TAG, "=== 회원가입 스킬 선택 처리 끝 ===");
    }

    /**
     * 역할 선택 처리 (회원가입용)
     */
    private void handleRoleSelectionForSignup(Chip chip, boolean isChecked) {
        Object tag = chip.getTag();
        Log.d(TAG, "=== 회원가입 역할 선택 처리 시작 ===");
        Log.d(TAG, "Chip 텍스트: " + chip.getText() + ", isChecked: " + isChecked + ", tag: " + tag);
        
        if (tag instanceof Integer) {
            Integer roleId = (Integer) tag;
            Log.d(TAG, "기존 역할 처리: roleId=" + roleId);
            
            if (isChecked) {
                if (!selectedRoleIds.contains(roleId)) {
                    selectedRoleIds.add(roleId);
                    Log.d(TAG, "역할 추가됨: " + roleId);
                } else {
                    Log.d(TAG, "역할 이미 선택되어 있음: " + roleId);
                }
            } else {
                boolean removed = selectedRoleIds.remove(roleId);
                Log.d(TAG, "역할 제거됨: " + roleId + ", 제거 성공: " + removed);
            }
        } else if (tag instanceof String && ((String) tag).startsWith("custom_")) {
            String customRole = ((String) tag).substring(7); // "custom_" 제거
            Log.d(TAG, "커스텀 역할 처리: customRole=" + customRole);
            
            if (isChecked) {
                if (!selectedCustomRoles.contains(customRole)) {
                    selectedCustomRoles.add(customRole);
                    Log.d(TAG, "커스텀 역할 추가됨: " + customRole);
                } else {
                    Log.d(TAG, "커스텀 역할 이미 선택되어 있음: " + customRole);
                }
            } else {
                boolean removed = selectedCustomRoles.remove(customRole);
                Log.d(TAG, "커스텀 역할 제거됨: " + customRole + ", 제거 성공: " + removed);
            }
        }
        
        Log.d(TAG, "현재 선택된 역할 IDs: " + selectedRoleIds.toString());
        Log.d(TAG, "현재 선택된 커스텀 역할: " + selectedCustomRoles.toString());
        Log.d(TAG, "=== 회원가입 역할 선택 처리 끝 ===");
    }

    /**
     * 사용자 스킬 업데이트 (마이페이지용)
     */
    private void updateUserSkills() {
        Log.d(TAG, "=== 스킬 API 업데이트 시작 ===");
        Log.d(TAG, "selectedSkillIds: " + selectedSkillIds.toString());
        Log.d(TAG, "selectedCustomSkills: " + selectedCustomSkills.toString());
        
        ProfileManager.getInstance(requireContext()).updateUserSkills(
            requireContext(), 
            new ArrayList<>(selectedSkillIds), 
            selectedCustomSkills, 
            new ProfileManager.ProfileUpdateCallback() {
                @Override
                public void onSuccess(ProfileUpdateResponse response) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Log.d(TAG, "✅ 스킬 업데이트 API 성공: " + response.getMessage());
                            // Toast 메시지로 사용자에게 알림
                            android.widget.Toast.makeText(requireContext(), "스킬이 업데이트되었습니다.", android.widget.Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Log.e(TAG, "❌ 스킬 업데이트 API 실패: " + errorMessage);
                            android.widget.Toast.makeText(requireContext(), "스킬 업데이트 실패: " + errorMessage, android.widget.Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }
        );
        Log.d(TAG, "=== 스킬 API 업데이트 요청 완료 ===");
    }

    /**
     * 사용자 역할 업데이트 (마이페이지용)
     */
    private void updateUserRoles() {
        Log.d(TAG, "=== 역할 API 업데이트 시작 ===");
        Log.d(TAG, "selectedRoleIds: " + selectedRoleIds.toString());
        Log.d(TAG, "selectedCustomRoles: " + selectedCustomRoles.toString());
        
        // 토큰 확인
        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        if (!tokenManager.isLoggedIn()) {
            Log.e(TAG, "❌ 로그인되지 않음");
            return;
        }
        
        String token = "Bearer " + tokenManager.getAccessToken();
        Log.d(TAG, "토큰 확인 완료");
        
        // RoleUpdate 객체 생성
        RoleUpdate roleUpdate = new RoleUpdate(new ArrayList<>(selectedRoleIds), selectedCustomRoles);
        Log.d(TAG, "RoleUpdate 객체 생성 완료");
        
        // API 호출
        RetrofitClient.getInstance().getApiService().updateUserRoles(token, roleUpdate)
            .enqueue(new retrofit2.Callback<ProfileUpdateResponse>() {
                @Override
                public void onResponse(retrofit2.Call<ProfileUpdateResponse> call, 
                                     retrofit2.Response<ProfileUpdateResponse> response) {
                    Log.d(TAG, "API 응답 받음: code=" + response.code() + ", isSuccessful=" + response.isSuccessful());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        ProfileUpdateResponse result = response.body();
                        Log.d(TAG, "✅ 역할 업데이트 API 성공: " + result.getMessage());
                        
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                android.widget.Toast.makeText(requireContext(), "역할이 업데이트되었습니다", android.widget.Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        Log.e(TAG, "❌ 역할 업데이트 API 실패: " + response.code() + " " + response.message());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "에러 바디 없음";
                            Log.e(TAG, "에러 바디: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "에러 바디 읽기 실패", e);
                        }
                        
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                android.widget.Toast.makeText(requireContext(), "역할 업데이트 실패: " + response.code(), android.widget.Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                }
                
                @Override
                public void onFailure(retrofit2.Call<ProfileUpdateResponse> call, Throwable t) {
                    Log.e(TAG, "❌ 역할 업데이트 API 호출 실패", t);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            android.widget.Toast.makeText(requireContext(), "네트워크 오류: " + t.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                        });
                    }
                }
            });
        Log.d(TAG, "=== 역할 API 업데이트 요청 완료 ===");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fragment가 다시 활성화될 때마다 데이터 새로고침
        if (SOURCE_MYPAGE.equals(source)) {
            loadUserSkillsAndRoles();
        } else {
            loadSkillsAndRoles();
        }
    }

    // 선택된 데이터를 반환하는 메서드들 (Activity에서 호출)
    public java.util.List<Integer> getSelectedSkillIds() {
        return new java.util.ArrayList<>(selectedSkillIds);
    }

    public java.util.List<String> getSelectedCustomSkills() {
        return new java.util.ArrayList<>(selectedCustomSkills);
    }

    public java.util.List<Integer> getSelectedRoleIds() {
        return new java.util.ArrayList<>(selectedRoleIds);
    }

    public java.util.List<String> getSelectedCustomRoles() {
        return new java.util.ArrayList<>(selectedCustomRoles);
    }
}
