package kr.mojuk.teamup.fragments;

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

import kr.mojuk.teamup.MainActivity;
import kr.mojuk.teamup.R;

import kr.mojuk.teamup.auth.RegistrationManager;
import kr.mojuk.teamup.auth.ProfileManager;
import kr.mojuk.teamup.api.model.Skill;
import kr.mojuk.teamup.api.model.Role;
import kr.mojuk.teamup.api.model.UserSkillsResponse;
import kr.mojuk.teamup.api.model.UserRolesResponse;
import kr.mojuk.teamup.api.model.RoleUpdate;
import kr.mojuk.teamup.api.model.ProfileUpdateResponse;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.auth.TokenManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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
    
    // Skills arrow buttons
    private TextView btnIndicatorSkillsLeft;
    private TextView btnIndicatorSkillsRight;
    
    // Skills pagination
    private LinearLayout llSkillsPageIndicator;
    private int currentSkillsPage = 0;
    private List<Skill> allSkills = new ArrayList<>();
    
    // Role related views
    private ScrollView scrollViewRoles;
    private ChipGroup chipGroupRoles;
    
    // Roles arrow buttons
    private TextView btnIndicatorRolesLeft;
    private TextView btnIndicatorRolesRight;
    
    // Roles pagination
    private LinearLayout llRolesPageIndicator;
    private int currentRolesPage = 0;
    private List<Role> allRoles = new ArrayList<>();
    
    // 선택된 아이템들 (마이페이지용) - 중복 방지를 위해 Set 사용
    private Set<Integer> selectedSkillIds = new HashSet<>();
    private Set<Integer> selectedRoleIds = new HashSet<>();
    
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
            // 저장된 데이터 복원
            restoreSavedData();
        }
    }

    private void initViews(View view) {
        // Back button
        tvBackArrow = view.findViewById(R.id.tv_back_arrow);
        llBackNavigation = view.findViewById(R.id.ll_back_navigation);
        
        // Language views
        scrollViewSkills = view.findViewById(R.id.scrollViewSkills);
        chipGroupLanguages = view.findViewById(R.id.chipGroupLanguages);
        
        // Skills arrow buttons
        btnIndicatorSkillsLeft = view.findViewById(R.id.btn_indicator_skills_left);
        btnIndicatorSkillsRight = view.findViewById(R.id.btn_indicator_skills_right);
        
        // Skills pagination views
        llSkillsPageIndicator = view.findViewById(R.id.ll_skills_page_indicator);
        
        // Role views
        scrollViewRoles = view.findViewById(R.id.scrollViewRoles);
        chipGroupRoles = view.findViewById(R.id.chipGroupRoles);
        
        // Roles arrow buttons
        btnIndicatorRolesLeft = view.findViewById(R.id.btn_indicator_roles_left);
        btnIndicatorRolesRight = view.findViewById(R.id.btn_indicator_roles_right);
        
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
                // 마이페이지 모드에서는 변경사항을 서버에 저장
                if (SOURCE_MYPAGE.equals(source)) {
                    Log.d(TAG, "뒤로가기 버튼 클릭 - 변경사항 저장");
                    Log.d(TAG, "저장할 스킬: " + selectedSkillIds.size() + "개");
                    Log.d(TAG, "저장할 역할: " + selectedRoleIds.size() + "개");
                    
                    // 스킬과 역할 모두 저장
                    updateUserSkills();
                    updateUserRoles();
                }
                
                // 마이페이지 프로필로 돌아가기
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showFragment(new MypageProfileFragment());
                }
            }
        });



        // Skills arrow buttons
        btnIndicatorSkillsLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousSkillsPage();
            }
        });

        btnIndicatorSkillsRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSkillsPage();
            }
        });

        // Roles arrow buttons
        btnIndicatorRolesLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousRolesPage();
            }
        });

        btnIndicatorRolesRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextRolesPage();
            }
        });


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
        
        int totalSkills = allSkills.size();
        int startIndex = currentSkillsPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalSkills);
        
        Log.d(TAG, "updateSkillsPage: page=" + currentSkillsPage + ", total=" + totalSkills + ", showing " + startIndex + " to " + (endIndex-1));
        
        // 현재 페이지에 해당하는 아이템들만 표시
        for (int i = startIndex; i < endIndex; i++) {
            Skill skill = allSkills.get(i);
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.view_chip_choice, chipGroupLanguages, false);
            
            chip.setText(skill.getName());
            chip.setTag(skill.getSkillId());
            
            // 선택 상태 표시 (마이페이지 모드 또는 회원가입 모드에서 저장된 데이터가 있는 경우)
            if (selectedSkillIds.contains(skill.getSkillId())) {
                chip.setChecked(true);
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
        
        // 화살표 버튼 가시성 업데이트
        updateSkillsArrowButtons(totalPages);
        
        Log.d(TAG, "updateSkillsPage: 표시된 칩 개수=" + chipGroupLanguages.getChildCount());
    }

    /**
     * 역할 페이지 업데이트
     */
    private void updateRolesPage() {
        chipGroupRoles.removeAllViews();
        
        int totalRoles = allRoles.size();
        int startIndex = currentRolesPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalRoles);
        
        Log.d(TAG, "updateRolesPage: page=" + currentRolesPage + ", total=" + totalRoles + ", showing " + startIndex + " to " + (endIndex-1));
        
        // 현재 페이지에 해당하는 아이템들만 표시
        for (int i = startIndex; i < endIndex; i++) {
            Role role = allRoles.get(i);
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.view_chip_choice, chipGroupRoles, false);
            
            chip.setText(role.getName());
            chip.setTag(role.getRoleId());
            
            // 선택 상태 표시 (마이페이지 모드 또는 회원가입 모드에서 저장된 데이터가 있는 경우)
            if (selectedRoleIds.contains(role.getRoleId())) {
                chip.setChecked(true);
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
        
        // 화살표 버튼 가시성 업데이트
        updateRolesArrowButtons(totalPages);
        
        Log.d(TAG, "updateRolesPage: 표시된 칩 개수=" + chipGroupRoles.getChildCount());
    }

    /**
     * 기술 페이징 설정
     */
    private void setupSkillsPagination() {
        int totalSkills = allSkills.size();
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
        int totalRoles = allRoles.size();
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
     * 기술 화살표 버튼 가시성 업데이트
     */
    private void updateSkillsArrowButtons(int totalPages) {
        if (totalPages <= 1) {
            // 페이지가 1개 이하면 화살표 버튼 숨기기
            btnIndicatorSkillsLeft.setVisibility(View.GONE);
            btnIndicatorSkillsRight.setVisibility(View.GONE);
        } else {
            // 첫 번째 페이지면 왼쪽 화살표 숨기기
            if (currentSkillsPage == 0) {
                btnIndicatorSkillsLeft.setVisibility(View.GONE);
            } else {
                btnIndicatorSkillsLeft.setVisibility(View.VISIBLE);
            }
            
            // 마지막 페이지면 오른쪽 화살표 숨기기
            if (currentSkillsPage == totalPages - 1) {
                btnIndicatorSkillsRight.setVisibility(View.GONE);
            } else {
                btnIndicatorSkillsRight.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 저장된 데이터 복원
     */
    private void restoreSavedData() {
        // 저장된 스킬 데이터 복원
        List<Skill> savedSkills = registrationManager.getSavedSkills();
        
        // 기존 선택 상태 초기화
        selectedSkillIds.clear();
        
        // 저장된 스킬 ID 복원
        for (Skill savedSkill : savedSkills) {
            selectedSkillIds.add(savedSkill.getSkillId());
        }
        
        // 저장된 역할 데이터 복원
        List<Role> savedRoles = registrationManager.getSavedRoles();
        
        // 기존 선택 상태 초기화
        selectedRoleIds.clear();
        
        // 저장된 역할 ID 복원
        for (Role savedRole : savedRoles) {
            selectedRoleIds.add(savedRole.getRoleId());
        }
        
        Log.d(TAG, "저장된 데이터 복원 완료: " + selectedSkillIds.size() + "개 스킬, " + selectedRoleIds.size() + "개 역할");
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
     * 역할 화살표 버튼 가시성 업데이트
     */
    private void updateRolesArrowButtons(int totalPages) {
        if (totalPages <= 1) {
            // 페이지가 1개 이하면 화살표 버튼 숨기기
            btnIndicatorRolesLeft.setVisibility(View.GONE);
            btnIndicatorRolesRight.setVisibility(View.GONE);
        } else {
            // 첫 번째 페이지면 왼쪽 화살표 숨기기
            if (currentRolesPage == 0) {
                btnIndicatorRolesLeft.setVisibility(View.GONE);
            } else {
                btnIndicatorRolesLeft.setVisibility(View.VISIBLE);
            }
            
            // 마지막 페이지면 오른쪽 화살표 숨기기
            if (currentRolesPage == totalPages - 1) {
                btnIndicatorRolesRight.setVisibility(View.GONE);
            } else {
                btnIndicatorRolesRight.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 이전 기술 페이지로 이동
     */
    private void previousSkillsPage() {
        int totalSkills = allSkills.size();
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
        int totalSkills = allSkills.size();
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
        int totalRoles = allRoles.size();
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
        int totalRoles = allRoles.size();
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
        }
        
        Log.d(TAG, "현재 선택된 스킬 IDs: " + selectedSkillIds.toString());
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
        }
        
        Log.d(TAG, "현재 선택된 역할 IDs: " + selectedRoleIds.toString());
        Log.d(TAG, "=== 회원가입 역할 선택 처리 끝 ===");
    }

    /**
     * 사용자 스킬 업데이트 (마이페이지용)
     */
    private void updateUserSkills() {
        Log.d(TAG, "=== 스킬 API 업데이트 시작 ===");
        Log.d(TAG, "selectedSkillIds: " + selectedSkillIds.toString());
        
        ProfileManager.getInstance(requireContext()).updateUserSkills(
            requireContext(), 
            new ArrayList<>(selectedSkillIds), 
            new ArrayList<>(), 
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
                            
                            // 서버에서 이미 존재하는 스킬 에러인지 확인
                            if (errorMessage.contains("이미 존재합니다") || errorMessage.contains("기존 스킬을 선택해주세요")) {
                                android.widget.Toast.makeText(requireContext(), "이미 존재하는 스킬입니다. 기존 스킬 목록에서 선택해주세요.", android.widget.Toast.LENGTH_LONG).show();
                            } else {
                                android.widget.Toast.makeText(requireContext(), "스킬 업데이트 실패: " + errorMessage, android.widget.Toast.LENGTH_LONG).show();
                            }
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
        
        // 토큰 확인
        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        if (!tokenManager.isLoggedIn()) {
            Log.e(TAG, "❌ 로그인되지 않음");
            return;
        }
        
        String token = "Bearer " + tokenManager.getAccessToken();
        Log.d(TAG, "토큰 확인 완료");
        
        // RoleUpdate 객체 생성
        RoleUpdate roleUpdate = new RoleUpdate(new ArrayList<>(selectedRoleIds), new ArrayList<>());
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
                        String errorBody = "에러 바디 없음";
                        try {
                            errorBody = response.errorBody() != null ? response.errorBody().string() : "에러 바디 없음";
                            Log.e(TAG, "에러 바디: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "에러 바디 읽기 실패", e);
                            errorBody = "에러 바디 읽기 실패";
                        }
                        
                        final String finalErrorBody = errorBody;  // final 변수로 복사
                        
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                // 서버에서 이미 존재하는 역할 에러인지 확인
                                if (finalErrorBody.contains("이미 존재합니다") || finalErrorBody.contains("기존 역할을 선택해주세요")) {
                                    android.widget.Toast.makeText(requireContext(), "이미 존재하는 역할입니다. 기존 역할 목록에서 선택해주세요.", android.widget.Toast.LENGTH_LONG).show();
                                } else {
                                    android.widget.Toast.makeText(requireContext(), "역할 업데이트 실패: " + response.code(), android.widget.Toast.LENGTH_LONG).show();
                                }
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



    public java.util.List<Integer> getSelectedRoleIds() {
        return new java.util.ArrayList<>(selectedRoleIds);
    }


    
    /**
     * 선택된 스킬 객체들을 반환
     */
    public java.util.List<Skill> getSelectedSkills() {
        java.util.List<Skill> selectedSkills = new java.util.ArrayList<>();
        for (Skill skill : allSkills) {
            if (selectedSkillIds.contains(skill.getSkillId())) {
                selectedSkills.add(skill);
            }
        }
        return selectedSkills;
    }
    
    /**
     * 선택된 역할 객체들을 반환
     */
    public java.util.List<Role> getSelectedRoles() {
        java.util.List<Role> selectedRoles = new java.util.ArrayList<>();
        for (Role role : allRoles) {
            if (selectedRoleIds.contains(role.getRoleId())) {
                selectedRoles.add(role);
            }
        }
        return selectedRoles;
    }
}
