package kr.mojuk.teamup.fragments;

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
import kr.mojuk.teamup.auth.ProfileManager;
import kr.mojuk.teamup.auth.TokenManager;
import kr.mojuk.teamup.auth.RegistrationManager;
import kr.mojuk.teamup.api.model.Experience;
import kr.mojuk.teamup.api.model.UserProfileResponse;
import kr.mojuk.teamup.api.model.UserRolesResponse;
import kr.mojuk.teamup.api.model.UserSkillsResponse;
import kr.mojuk.teamup.api.model.Skill;
import kr.mojuk.teamup.api.model.Role;

import kr.mojuk.teamup.personality.PersonalityTestQuestionFragment;
import kr.mojuk.teamup.personality.PersonalityTestResultFragment;
import kr.mojuk.teamup.api.model.PersonalityProfileResponse;
import kr.mojuk.teamup.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MypageProfileFragment extends Fragment {

    private static final String TAG = "MypageProfileFragment";

    private LinearLayout llUserId;
    private LinearLayout llLanguagesAndRoles;
    private LinearLayout llExperience;
    private LinearLayout llTeamTendency;
    
    // 프로필 정보 표시용 TextView들
    private TextView tvUserId, tvUserSkillsAndRoles, tvUserExperiences, tvTeamTendency;
    
    // ">" 화살표 TextView들
    private TextView tvSkillsArrow, tvExperienceArrow, tvTendencyArrow;
    
    // Manager 인스턴스들
    private TokenManager tokenManager;
    private ProfileManager profileManager;
    private RegistrationManager registrationManager;
    
    // 전체 스킬/역할 목록 (ID를 이름으로 변환하기 위해)
    private List<Skill> allSkills;
    private List<Role> allRoles;
    

    // 다른 사용자 프로필 표시를 위한 변수들
    private String targetUserId;
    private boolean isViewMode = false; // true: 다른 사용자 프로필 보기, false: 내 프로필

    /**
     * 다른 사용자의 프로필을 보기 위한 newInstance 메서드
     */
    public static MypageProfileFragment newInstance(String userId) {
        MypageProfileFragment fragment = new MypageProfileFragment();
        Bundle args = new Bundle();
        args.putString("target_user_id", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypage_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Manager 초기화
        tokenManager = TokenManager.getInstance(requireContext());
        profileManager = ProfileManager.getInstance(requireContext());
        registrationManager = RegistrationManager.getInstance();
        
        // 대상 사용자 ID 가져오기
        if (getArguments() != null) {
            targetUserId = getArguments().getString("TARGET_USER_ID");
        }

        // 인자 확인하여 모드 설정
        if (getArguments() != null) {
            targetUserId = getArguments().getString("target_user_id");
            if (targetUserId != null) {
                isViewMode = true; // 다른 사용자 프로필 보기 모드
            }
        }

        initViews(view);
        setClickListeners();
        
        // 프로필 정보 로드
        loadProfileInfo();
    }

    private void initViews(View view) {
        llUserId = view.findViewById(R.id.ll_user_id);
        llLanguagesAndRoles = view.findViewById(R.id.ll_languages_and_roles);
        llExperience = view.findViewById(R.id.ll_experience);
        llTeamTendency = view.findViewById(R.id.ll_team_tendency);
        
        // 프로필 정보 표시용 TextView들
        tvUserId = view.findViewById(R.id.tv_user_id);
        tvUserSkillsAndRoles = view.findViewById(R.id.tv_user_skills_and_roles);
        tvUserExperiences = view.findViewById(R.id.tv_user_experiences);
        tvTeamTendency = view.findViewById(R.id.tv_team_tendency);
        
        // ">" 화살표 TextView들
        tvSkillsArrow = view.findViewById(R.id.tv_skills_arrow);
        tvExperienceArrow = view.findViewById(R.id.tv_experience_arrow);
        tvTendencyArrow = view.findViewById(R.id.tv_tendency_arrow);
    }

    /**
     * 프로필 정보 로드
     */
    private void loadProfileInfo() {
        // 사용자 ID 표시
        String userId = isViewMode ? targetUserId : tokenManager.getUserId();
        if (tvUserId != null && userId != null) {
            tvUserId.setText("ID: " + userId);
        }
        
        // 통합 프로필 API로 모든 정보 로드
        loadUserProfileData(userId);
    }

  

    
    /**
     * 통합 프로필 API로 사용자 데이터 로드
     */
    private void loadUserProfileData(String userId) {
        Log.d(TAG, "통합 프로필 API로 데이터 로드 시작: " + userId);
        // 먼저 스킬/역할 목록을 로드한 후 프로필 데이터 로드
        loadSkillsAndRolesForMapping(userId);
    }
    
    /**
     * 스킬/역할 목록 로드 (ID -> 이름 매핑용)
     */
    private void loadSkillsAndRolesForMapping(String userId) {
        Log.d(TAG, "스킬/역할 목록 로드 시작 (매핑용)");
        
        // 스킬 목록 로드
        registrationManager.getAvailableSkills(new RegistrationManager.SkillsCallback() {
            @Override
            public void onSuccess(List<Skill> skills) {
                allSkills = skills;
                Log.d(TAG, "스킬 목록 로드 완료: " + skills.size() + "개");
                
                // 역할 목록 로드
                registrationManager.getAvailableRoles(new RegistrationManager.RolesCallback() {
                    @Override
                    public void onSuccess(List<Role> roles) {
                        allRoles = roles;
                        Log.d(TAG, "역할 목록 로드 완료: " + roles.size() + "개");
                        
                        // 이제 통합 프로필 API 호출
                        loadUserProfileFromAPI(userId);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "역할 목록 로드 실패: " + errorMessage);
                        // 실패해도 프로필 데이터는 로드
                        loadUserProfileFromAPI(userId);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "스킬 목록 로드 실패: " + errorMessage);
                // 실패해도 프로필 데이터는 로드
                loadUserProfileFromAPI(userId);
            }
        });
    }
    
    /**
     * API에서 프로필 데이터 로드
     */

    private void loadUserProfileFromAPI(String userId) {
        RetrofitClient.getInstance()
                .getApiService()
                .getUserProfile(userId)
                .enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserProfileResponse profile = response.body();
                            Log.d(TAG, "프로필 로드 성공: " + profile.getUserId());
                            
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    // 각 데이터를 분리해서 바인딩
                                    bindSkillsAndRoles(profile.getSkills(), profile.getRoles());
                                    bindExperiences(profile.getExperiences());
                                    bindPersonalityInfo(profile.getPersonalityProfile());
                                });
                            }
                        } else {
                            Log.e(TAG, "프로필 로드 실패: " + response.code());
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    // 실패 시 빈 데이터로 표시
                                    bindSkillsAndRoles(new UserSkillsResponse(), new UserRolesResponse());
                                    bindExperiences(null);
                                    bindPersonalityInfo(null);
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                        Log.e(TAG, "프로필 로드 네트워크 오류: " + t.getMessage());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                // 실패 시 빈 데이터로 표시
                                bindSkillsAndRoles(new UserSkillsResponse(), new UserRolesResponse());
                                bindExperiences(null);
                                bindPersonalityInfo(null);
                            });
                        }
                    }
                });
    }
    
    /**
     * 사용자 경험 정보 표시
     */
    private void displayUserExperiences(List<Experience> experiences) {
        if (tvUserExperiences == null) return;
        
        if (experiences == null || experiences.isEmpty()) {
            tvUserExperiences.setText("등록된 경험이 없습니다");
        } else {
            tvUserExperiences.setText(formatExperiencesDisplay(experiences));
        }
    }

    /**
     * 사용자 성향 정보 표시
     */
    private void displayUserPersonalityInfo(PersonalityProfileResponse personalityProfile) {
        if (tvTeamTendency == null) return;
        
        if (personalityProfile == null) {
            tvTeamTendency.setText("등록된 성향 정보가 없습니다");
        } else {
            String profileCode = personalityProfile.getProfileCode();
            String displayName = getPersonalityDisplayName(profileCode);
            tvTeamTendency.setText("성향: " + displayName);
            Log.d(TAG, "성향 정보 로드 성공: " + profileCode + " -> " + displayName);
        }
    }
    
    /**
     * 성향 코드를 표시 이름으로 변환
     */
    private String getPersonalityDisplayName(String profileCode) {
        if (profileCode == null) return "알 수 없는 성향";
        
        switch (profileCode) {
            case "STRATEGIC_LEADER":
                return "전략 리더";
            case "EXECUTION_LEADER":
                return "실행 리더";
            case "VISIONARY_LEADER":
                return "비전 리더";
            case "DYNAMIC_LEADER":
                return "다이내믹 리더";
            case "RELIABLE_PARTNER":
                return "신뢰 파트너";
            case "ENERGETIC_SUPPORTER":
                return "열정 서포터";
            case "CAREFUL_SUPPORTER":
                return "섬세한 서포터";
            case "BALANCE_SUPPORTER":
                return "균형 서포터";
            default:
                Log.w(TAG, "알 수 없는 성향 코드: " + profileCode);
                return "알 수 없는 성향";
        }
    }

    /**
     * 스킬/역할 데이터 바인딩
     */
    private void bindSkillsAndRoles(UserSkillsResponse skills, UserRolesResponse roles) {
        Log.d(TAG, "=== 스킬/역할 데이터 바인딩 시작 ===");
        
        // null 체크
        if (skills == null) skills = new UserSkillsResponse();
        if (roles == null) roles = new UserRolesResponse();
        
        // 디버깅 로그
        Log.d(TAG, "스킬 ID 개수: " + (skills.getSkillIds() != null ? skills.getSkillIds().size() : 0));
        Log.d(TAG, "스킬 IDs: " + (skills.getSkillIds() != null ? skills.getSkillIds().toString() : "null"));
        Log.d(TAG, "역할 ID 개수: " + (roles.getRoleIds() != null ? roles.getRoleIds().size() : 0));
        Log.d(TAG, "역할 IDs: " + (roles.getRoleIds() != null ? roles.getRoleIds().toString() : "null"));
        
        // 스킬/역할 정보 표시
        displayUserSkillsAndRoles(skills, roles);
    }
    
    /**
     * 경험 데이터 바인딩
     */
    private void bindExperiences(List<Experience> experiences) {
        Log.d(TAG, "=== 경험 데이터 바인딩 시작 ===");
        Log.d(TAG, "경험 개수: " + (experiences != null ? experiences.size() : 0));
        
        // 경험 정보 표시
        displayUserExperiences(experiences);
    }
    
    /**
     * 성향 데이터 바인딩
     */
    private void bindPersonalityInfo(PersonalityProfileResponse personalityProfile) {
        Log.d(TAG, "=== 성향 데이터 바인딩 시작 ===");
        
        // 통합 프로필에서 성향 정보가 없으면 별도 API로 가져오기
        if (personalityProfile != null) {
            Log.d(TAG, "통합 프로필에서 성향 정보 로드: " + personalityProfile.getProfileCode());
            displayUserPersonalityInfo(personalityProfile);
        } else {
            Log.d(TAG, "통합 프로필에 성향 정보 없음, 별도 API 호출");
            loadUserPersonalityProfileForDisplay();
        }
    }
    


    /**
     * 사용자 기술/역할 정보 표시
     */
    private void displayUserSkillsAndRoles(UserSkillsResponse skills, UserRolesResponse roles) {
        if (tvUserSkillsAndRoles == null) return;
        
        // null 체크 추가
        if (skills == null) skills = new UserSkillsResponse();
        if (roles == null) roles = new UserRolesResponse();
        
        // 디버깅 로그
        Log.d(TAG, "=== 기술/역할 표시 디버깅 ===");
        Log.d(TAG, "스킬 ID 개수: " + (skills.getSkillIds() != null ? skills.getSkillIds().size() : 0));
        Log.d(TAG, "스킬 IDs: " + (skills.getSkillIds() != null ? skills.getSkillIds().toString() : "null"));
        Log.d(TAG, "커스텀 스킬: " + (skills.getCustomSkills() != null ? skills.getCustomSkills().toString() : "null"));
        Log.d(TAG, "역할 ID 개수: " + (roles.getRoleIds() != null ? roles.getRoleIds().size() : 0));
        Log.d(TAG, "역할 IDs: " + (roles.getRoleIds() != null ? roles.getRoleIds().toString() : "null"));
        Log.d(TAG, "커스텀 역할: " + (roles.getCustomRoles() != null ? roles.getCustomRoles().toString() : "null"));
        
        StringBuilder displayText = new StringBuilder();
        
        // 스킬과 역할 존재 여부 확인 (null 체크 포함)
        boolean hasSkills = (skills.getSkillIds() != null && !skills.getSkillIds().isEmpty()) || 
                           (skills.getCustomSkills() != null && !skills.getCustomSkills().isEmpty());
        boolean hasRoles = (roles.getRoleIds() != null && !roles.getRoleIds().isEmpty()) || 
                          (roles.getCustomRoles() != null && !roles.getCustomRoles().isEmpty());
        
        Log.d(TAG, "hasSkills: " + hasSkills + ", hasRoles: " + hasRoles);
        
        if (hasSkills && hasRoles) {
            // 둘 다 있으면 둘 다 표시 (기술 우선)
            displayText.append("기술: ");
            displayText.append(formatSkillsDisplay(skills));
            displayText.append(" / 역할: ");
            displayText.append(formatRolesDisplay(roles));
        } else if (hasSkills) {
            // 스킬만 있으면 스킬 정보 표시
            displayText.append("기술: ");
            displayText.append(formatSkillsDisplay(skills));
        } else if (hasRoles) {
            // 역할만 있으면 역할 정보 표시
            displayText.append("역할: ");
            displayText.append(formatRolesDisplay(roles));
        } else {
            // 둘 다 없으면 기본 메시지
            displayText.append("등록된 기술/역할이 없습니다");
        }
        
        tvUserSkillsAndRoles.setText(displayText.toString());
        Log.d(TAG, "최종 표시 텍스트: " + displayText.toString());
    }
    
    /**
     * 스킬 표시 형식 생성
     */
    private String formatSkillsDisplay(UserSkillsResponse skills) {
        StringBuilder result = new StringBuilder();
        
        // null 체크
        if (skills == null) return "등록된 기술이 없습니다";
        
        Log.d(TAG, "=== 스킬 표시 형식 생성 ===");
        Log.d(TAG, "스킬 ID 개수: " + (skills.getSkillIds() != null ? skills.getSkillIds().size() : 0));
        Log.d(TAG, "커스텀 스킬 개수: " + (skills.getCustomSkills() != null ? skills.getCustomSkills().size() : 0));
        
        // 전체 스킬 리스트 생성 (기존 + 커스텀)
        List<String> allSkills = new ArrayList<>();
        
        // 기존 스킬들 추가
        if (skills.getSkillIds() != null && !skills.getSkillIds().isEmpty()) {
            for (Integer skillId : skills.getSkillIds()) {
                String skillName = getSkillNameById(skillId);
                if (skillName != null && !skillName.isEmpty()) {
                    allSkills.add(skillName);
                }
            }
        }
        
        // 커스텀 스킬들 추가
        if (skills.getCustomSkills() != null && !skills.getCustomSkills().isEmpty()) {
            allSkills.addAll(skills.getCustomSkills());
        }
        
        Log.d(TAG, "전체 스킬 개수: " + allSkills.size());
        
        if (allSkills.isEmpty()) {
            return "등록된 기술이 없습니다";
        } else if (allSkills.size() == 1) {
            return allSkills.get(0);
        } else {
            // 2개 이상이면 첫 번째 + "외 n개"
            int remainingCount = allSkills.size() - 1;
            result.append(allSkills.get(0)).append(" 외 ").append(remainingCount).append("개");
            Log.d(TAG, "스킬 표시: " + allSkills.get(0) + " 외 " + remainingCount + "개");
        }
        
        Log.d(TAG, "스킬 표시 최종 결과: " + result.toString());
        return result.toString();
    }
    
    /**
     * 역할 표시 형식 생성
     */
    private String formatRolesDisplay(UserRolesResponse roles) {
        StringBuilder result = new StringBuilder();
        
        // null 체크
        if (roles == null) return "등록된 역할이 없습니다";
        
        Log.d(TAG, "=== 역할 표시 형식 생성 ===");
        Log.d(TAG, "역할 ID 개수: " + (roles.getRoleIds() != null ? roles.getRoleIds().size() : 0));
        Log.d(TAG, "커스텀 역할 개수: " + (roles.getCustomRoles() != null ? roles.getCustomRoles().size() : 0));
        
        // 전체 역할 리스트 생성 (기존 + 커스텀)
        List<String> allRoles = new ArrayList<>();
        
        // 기존 역할들 추가
        if (roles.getRoleIds() != null && !roles.getRoleIds().isEmpty()) {
            for (Integer roleId : roles.getRoleIds()) {
                String roleName = getRoleNameById(roleId);
                if (roleName != null && !roleName.isEmpty()) {
                    allRoles.add(roleName);
                }
            }
        }
        
        // 커스텀 역할들 추가
        if (roles.getCustomRoles() != null && !roles.getCustomRoles().isEmpty()) {
            allRoles.addAll(roles.getCustomRoles());
        }
        
        Log.d(TAG, "전체 역할 개수: " + allRoles.size());
        
        if (allRoles.isEmpty()) {
            return "등록된 역할이 없습니다";
        } else if (allRoles.size() == 1) {
            return allRoles.get(0);
        } else {
            // 2개 이상이면 첫 번째 + "외 n개"
            int remainingCount = allRoles.size() - 1;
            result.append(allRoles.get(0)).append(" 외 ").append(remainingCount).append("개");
            Log.d(TAG, "역할 표시: " + allRoles.get(0) + " 외 " + remainingCount + "개");
        }
        
        Log.d(TAG, "역할 표시 최종 결과: " + result.toString());
        return result.toString();
    }
    
    /**
     * 스킬 ID로 이름 찾기
     */
    private String getSkillNameById(Integer skillId) {
        Log.d(TAG, "스킬 ID로 이름 찾기: " + skillId + ", 전체 스킬 개수: " + (allSkills != null ? allSkills.size() : 0));
        
        if (allSkills != null) {
            for (Skill skill : allSkills) {
                if (skill.getSkillId() == skillId) {
                    Log.d(TAG, "스킬 매칭 성공: ID=" + skillId + " -> " + skill.getName());
                    return skill.getName();
                }
            }
        }
        Log.w(TAG, "스킬 매칭 실패: ID=" + skillId + " -> 알 수 없는 스킬");
        return "알 수 없는 스킬";
    }
    
    /**
     * 역할 ID로 이름 찾기
     */
    private String getRoleNameById(Integer roleId) {
        Log.d(TAG, "역할 ID로 이름 찾기: " + roleId + ", 전체 역할 개수: " + (allRoles != null ? allRoles.size() : 0));
        
        if (allRoles != null) {
            for (Role role : allRoles) {
                if (role.getRoleId() == roleId) {
                    Log.d(TAG, "역할 매칭 성공: ID=" + roleId + " -> " + role.getName());
                    return role.getName();
                }
            }
        }
        Log.w(TAG, "역할 매칭 실패: ID=" + roleId + " -> 알 수 없는 역할");
        return "알 수 없는 역할";
    }
    
    /**
     * 경험 표시 형식 생성
     */
    private String formatExperiencesDisplay(List<Experience> experiences) {
        if (experiences.isEmpty()) {
            return "등록된 경험이 없습니다";
        }
        
        StringBuilder result = new StringBuilder();
        
        if (experiences.size() == 1) {
            result.append(experiences.get(0).getContestName());
        } else {
            // 2개 이상이면 첫 번째 + "외 n개"
            int remainingCount = experiences.size() - 1;
            result.append(experiences.get(0).getContestName()).append(" 외 ").append(remainingCount).append("개");
        }
        
        return result.toString();
    }

    private void setClickListeners() {


        // ll_user_id에 뒤로가기 리스너 추가 (뷰 모드에서만)
        if (isViewMode) {
            llUserId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 뒤로가기
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            });
        }

        // 뷰 모드에서는 수정 버튼들을 비활성화
        if (isViewMode) {
            // ">" 텍스트 숨기기
            hideArrowTexts();
            
            // 클릭 리스너 제거 (수정 불가) 
            llLanguagesAndRoles.setClickable(false);
            llExperience.setClickable(false);
            llTeamTendency.setClickable(false);
        } else {
            // 내 프로필 모드일 때만 수정 가능
            llUserId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 마이페이지 메인으로 돌아가기
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).showFragment(new MypageFragment());
                    }
                }
            });

            llLanguagesAndRoles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 스킬/역할 수정 화면으로 이동 (마이페이지 모드)
                    if (getActivity() instanceof MainActivity) {
                        String userId = tokenManager.getUserId();
                        MypageInterestFragment fragment = MypageInterestFragment.newInstance(
                            MypageInterestFragment.SOURCE_MYPAGE, 
                            userId
                        );
                        ((MainActivity) getActivity()).showFragment(fragment);
                    }
                }
            });

            llExperience.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ExperienceFragment로 이동 (마이페이지 모드)
                    showExperienceFragment();
                }
            });

            llTeamTendency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 성향 테스트 Fragment로 이동
                    if (getActivity() instanceof MainActivity) {
                        // 사용자 성향 프로필 조회
                        loadUserPersonalityProfile();
                    }
                }
            });
        }
    }

    /**
     * 뷰 모드에서 ">" 텍스트를 숨기는 메서드
     */
    private void hideArrowTexts() {
        Log.d(TAG, "'>' 텍스트 숨기기 시작");
        // 직접 참조로 ">" TextView들을 숨기기
        if (tvSkillsArrow != null) {
            tvSkillsArrow.setVisibility(View.GONE);
        }
        
        if (tvExperienceArrow != null) {
            tvExperienceArrow.setVisibility(View.GONE);
        }
        
        if (tvTendencyArrow != null) {
            tvTendencyArrow.setVisibility(View.GONE);
        }
    }


    
    private void loadUserPersonalityProfile() {
        // 대상 사용자 ID가 있으면 해당 사용자의 정보를 로드, 없으면 현재 사용자의 정보를 로드
        String userId = (targetUserId != null) ? targetUserId : tokenManager.getUserId();
        
        RetrofitClient.getInstance()
                .getApiService()
                .getUserPersonalityProfile(userId)
                .enqueue(new Callback<PersonalityProfileResponse>() {
                    @Override
                    public void onResponse(Call<PersonalityProfileResponse> call, Response<PersonalityProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PersonalityProfileResponse profile = response.body();
                            Log.d("MypageProfileFragment", "성향 프로필 조회 성공: " + profile.getProfileCode());
                            
                            // 성향 테스트 결과 Fragment로 이동
                            PersonalityTestResultFragment resultFragment = new PersonalityTestResultFragment();
                            Bundle args = new Bundle();
                            args.putString("personalityType", profile.getProfileCode());
                            args.putSerializable("personalityTraits", profile.getTraitsJson());
                            args.putBoolean("isFromSignup", false); // 마이페이지에서 호출됨을 명시
                            resultFragment.setArguments(args);
                            
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).showFragment(resultFragment);
                            }
                        } else {
                            Log.e("MypageProfileFragment", "성향 프로필 조회 실패: " + response.code());
                            // 성향 테스트 결과가 없으면 테스트 Fragment로 이동
                            PersonalityTestQuestionFragment questionFragment = new PersonalityTestQuestionFragment();
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).showFragment(questionFragment);
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<PersonalityProfileResponse> call, Throwable t) {
                        Log.e("MypageProfileFragment", "성향 프로필 조회 네트워크 오류: " + t.getMessage());
                        // 네트워크 오류 시 테스트 Fragment로 이동
                        PersonalityTestQuestionFragment questionFragment = new PersonalityTestQuestionFragment();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).showFragment(questionFragment);
                        }
                    }
                });
    }
    
    /**
     * 사용자 성향 프로필 별도 API로 로드 (프로필 표시용)
     */
    private void loadUserPersonalityProfileForDisplay() {
        String userId = targetUserId != null ? targetUserId : tokenManager.getUserId();
        Log.d(TAG, "성향 프로필 API 호출 (표시용): " + userId);
        
        RetrofitClient.getInstance()
                .getApiService()
                .getUserPersonalityProfile(userId)
                .enqueue(new Callback<PersonalityProfileResponse>() {
                    @Override
                    public void onResponse(Call<PersonalityProfileResponse> call, Response<PersonalityProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PersonalityProfileResponse personalityProfile = response.body();
                            Log.d(TAG, "성향 프로필 로드 성공: " + personalityProfile.getProfileCode());
                            
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    displayUserPersonalityInfo(personalityProfile);
                                });
                            }
                        } else {
                            Log.e(TAG, "성향 프로필 로드 실패: " + response.code());
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    displayUserPersonalityInfo(null);
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PersonalityProfileResponse> call, Throwable t) {
                        Log.e(TAG, "성향 프로필 네트워크 오류: " + t.getMessage());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                displayUserPersonalityInfo(null);
                            });
                        }
                    }
                });
    }
    
    
    private void showExperienceFragment() {
        // ExperienceFragment 생성 (마이페이지 모드)
        ExperienceFragment experienceFragment = ExperienceFragment.newInstance(true, tokenManager.getUserId());
        
        // Fragment 교체
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showFragment(experienceFragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 화면이 다시 활성화될 때 프로필 정보 새로고침
        if (tokenManager.isLoggedIn()) {
            loadProfileInfo();
        }
    }
}
