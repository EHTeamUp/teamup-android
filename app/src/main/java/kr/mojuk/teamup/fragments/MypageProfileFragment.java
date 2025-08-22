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

import java.util.List;

public class MypageProfileFragment extends Fragment {

    private static final String TAG = "MypageProfileFragment";

    private TextView tvBackArrow;
    private LinearLayout llUserId;
    private LinearLayout llLanguagesAndRoles;
    private LinearLayout llExperience;
    private LinearLayout llTeamTendency;
    
    // 프로필 정보 표시용 TextView들
    private TextView tvUserId, tvUserSkillsAndRoles, tvUserExperiences, tvTeamTendency;
    
    // Manager 인스턴스들
    private TokenManager tokenManager;
    private ProfileManager profileManager;
    private RegistrationManager registrationManager;
    
    // 전체 스킬/역할 목록 (ID를 이름으로 변환하기 위해)
    private List<Skill> allSkills;
    private List<Role> allRoles;
    
    // 대상 사용자 ID (다른 사용자의 프로필을 볼 때 사용)
    private String targetUserId;

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

        initViews(view);
        setClickListeners();
        
        // 프로필 정보 로드
        loadProfileInfo();
    }

    private void initViews(View view) {
        tvBackArrow = view.findViewById(R.id.tv_back_arrow);
        llUserId = view.findViewById(R.id.ll_user_id);
        llLanguagesAndRoles = view.findViewById(R.id.ll_languages_and_roles);
        llExperience = view.findViewById(R.id.ll_experience);
        llTeamTendency = view.findViewById(R.id.ll_team_tendency);
        
        // 프로필 정보 표시용 TextView들
        tvUserId = view.findViewById(R.id.tv_user_id);
        tvUserSkillsAndRoles = view.findViewById(R.id.tv_user_skills_and_roles);
        tvUserExperiences = view.findViewById(R.id.tv_user_experiences);
        tvTeamTendency = view.findViewById(R.id.tv_team_tendency);
    }

    /**
     * 프로필 정보 로드
     */
    private void loadProfileInfo() {
        // 사용자 ID 표시 (대상 사용자 ID가 있으면 해당 ID, 없으면 현재 로그인한 사용자 ID)
        String displayUserId = (targetUserId != null) ? targetUserId : tokenManager.getUserId();
        if (tvUserId != null && displayUserId != null) {
            tvUserId.setText("ID: " + displayUserId);
        }
        
        // 통합된 기술/역할 정보 로드
        loadUserSkillsAndRoles();
        
        // 경험 정보 로드
        loadUserExperiences();
        
        // 사용자 성향 정보 로드
        loadUserPersonalityInfo();
    }

    /**
     * 사용자 기술/역할 정보 통합 로드
     */
    private void loadUserSkillsAndRoles() {
        Log.d(TAG, "사용자 기술/역할 정보 로드 시작");
        
        // 먼저 전체 스킬/역할 목록을 로드
        loadAllSkillsAndRoles();
    }
    
    /**
     * 전체 스킬/역할 목록 로드
     */
    private void loadAllSkillsAndRoles() {
        // 전체 스킬 목록 로드
        registrationManager.getAvailableSkills(new RegistrationManager.SkillsCallback() {
            @Override
            public void onSuccess(List<Skill> skills) {
                allSkills = skills;
                Log.d(TAG, "전체 스킬 로드 완료: " + allSkills.size() + "개");
                
                // 전체 역할 목록 로드
                registrationManager.getAvailableRoles(new RegistrationManager.RolesCallback() {
                    @Override
                    public void onSuccess(List<Role> roles) {
                        allRoles = roles;
                        Log.d(TAG, "전체 역할 로드 완료: " + allRoles.size() + "개");
                        
                        // 전체 목록 로드 완료 후 사용자 데이터 로드
                        loadUserData();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "전체 역할 로드 실패: " + errorMessage);
                        // 실패해도 사용자 데이터는 로드
                        loadUserData();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "전체 스킬 로드 실패: " + errorMessage);
                // 실패해도 사용자 데이터는 로드
                loadUserData();
            }
        });
    }
    
    /**
     * 사용자 데이터 로드 (전체 목록 로드 후 호출)
     */
    private void loadUserData() {
        // 대상 사용자 ID가 있으면 해당 사용자의 정보를 로드, 없으면 현재 사용자의 정보를 로드
        String userId = (targetUserId != null) ? targetUserId : tokenManager.getUserId();
        
        // 사용자 스킬 정보 로드
        profileManager.getUserSkills(requireContext(), new ProfileManager.UserSkillsCallback() {
            @Override
            public void onSuccess(UserSkillsResponse skills) {
                // 사용자 역할 정보 로드
                profileManager.getUserRoles(requireContext(), new ProfileManager.UserRolesCallback() {
                    @Override
                    public void onSuccess(UserRolesResponse roles) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                displayUserSkillsAndRoles(skills, roles);
                            });
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "사용자 역할 로드 실패: " + errorMessage);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                displayUserSkillsAndRoles(skills, new UserRolesResponse());
                            });
                        }
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "사용자 스킬 로드 실패: " + errorMessage);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        displayUserSkillsAndRoles(new UserSkillsResponse(), new UserRolesResponse());
                    });
                }
            }
        });
    }
    
    /**
     * 사용자 기술/역할 정보 표시
     */
    private void displayUserSkillsAndRoles(UserSkillsResponse skills, UserRolesResponse roles) {
        if (tvUserSkillsAndRoles == null) return;
        
        // 디버깅 로그
        Log.d(TAG, "=== 기술/역할 표시 디버깅 ===");
        Log.d(TAG, "스킬 ID 개수: " + skills.getSkillIds().size());
        Log.d(TAG, "스킬 IDs: " + skills.getSkillIds().toString());
        Log.d(TAG, "커스텀 스킬: " + skills.getCustomSkills().toString());
        Log.d(TAG, "역할 ID 개수: " + roles.getRoleIds().size());
        Log.d(TAG, "역할 IDs: " + roles.getRoleIds().toString());
        Log.d(TAG, "커스텀 역할: " + roles.getCustomRoles().toString());
        
        StringBuilder displayText = new StringBuilder();
        
        // 스킬과 역할 존재 여부 확인
        boolean hasSkills = !skills.getSkillIds().isEmpty() || !skills.getCustomSkills().isEmpty();
        boolean hasRoles = !roles.getRoleIds().isEmpty() || !roles.getCustomRoles().isEmpty();
        
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
        
        // 기존 스킬들 처리
        if (!skills.getSkillIds().isEmpty()) {
            for (int i = 0; i < skills.getSkillIds().size(); i++) {
                Integer skillId = skills.getSkillIds().get(i);
                String skillName = getSkillNameById(skillId);
                
                if (i == 0) {
                    result.append(skillName);
                } else {
                    // 2개 이상이면 첫 번째 + "등"
                    result.append(" 등");
                    break;
                }
            }
        }
        
        // 커스텀 스킬들 처리
        if (!skills.getCustomSkills().isEmpty()) {
            if (result.length() > 0) {
                result.append(", ");
            }
            
            for (int i = 0; i < skills.getCustomSkills().size(); i++) {
                String customSkill = skills.getCustomSkills().get(i);
                
                if (i == 0) {
                    result.append(customSkill);
                } else {
                    // 2개 이상이면 첫 번째 + "등"
                    result.append(" 등");
                    break;
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 역할 표시 형식 생성
     */
    private String formatRolesDisplay(UserRolesResponse roles) {
        StringBuilder result = new StringBuilder();
        
        // 기존 역할들 처리
        if (!roles.getRoleIds().isEmpty()) {
            for (int i = 0; i < roles.getRoleIds().size(); i++) {
                Integer roleId = roles.getRoleIds().get(i);
                String roleName = getRoleNameById(roleId);
                
                if (i == 0) {
                    result.append(roleName);
                } else {
                    // 2개 이상이면 첫 번째 + "등"
                    result.append(" 등");
                    break;
                }
            }
        }
        
        // 커스텀 역할들 처리
        if (!roles.getCustomRoles().isEmpty()) {
            if (result.length() > 0) {
                result.append(", ");
            }
            
            for (int i = 0; i < roles.getCustomRoles().size(); i++) {
                String customRole = roles.getCustomRoles().get(i);
                
                if (i == 0) {
                    result.append(customRole);
                } else {
                    // 2개 이상이면 첫 번째 + "등"
                    result.append(" 등");
                    break;
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 스킬 ID로 이름 찾기
     */
    private String getSkillNameById(Integer skillId) {
        if (allSkills != null) {
            for (Skill skill : allSkills) {
                if (skill.getSkillId() == skillId) {
                    return skill.getName();
                }
            }
        }
        return "알 수 없는 스킬";
    }
    
    /**
     * 역할 ID로 이름 찾기
     */
    private String getRoleNameById(Integer roleId) {
        if (allRoles != null) {
            for (Role role : allRoles) {
                if (role.getRoleId() == roleId) {
                    return role.getName();
                }
            }
        }
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
        
        for (int i = 0; i < experiences.size(); i++) {
            Experience experience = experiences.get(i);
            
            if (i == 0) {
                // 첫 번째 경험만 표시
                result.append(experience.getAwardName());
            } else {
                // 2개 이상이면 첫 번째 + "등"
                result.append(" 등");
                break;
            }
        }
        
        return result.toString();
    }

    /**
     * 사용자 경험 정보 로드
     */
    private void loadUserExperiences() {
        // 대상 사용자 ID가 있으면 해당 사용자의 정보를 로드, 없으면 현재 사용자의 정보를 로드
        String userId = (targetUserId != null) ? targetUserId : tokenManager.getUserId();
        
        profileManager.getUserExperiences(requireContext(), new ProfileManager.UserExperiencesCallback() {
            @Override
            public void onSuccess(List<Experience> experiences) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (tvUserExperiences != null) {
                            if (experiences.isEmpty()) {
                                tvUserExperiences.setText("등록된 경험이 없습니다");
                            } else {
                                tvUserExperiences.setText(formatExperiencesDisplay(experiences));
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.e(TAG, "경험 정보 로드 실패: " + errorMessage);
                        if (tvUserExperiences != null) {
                            tvUserExperiences.setText("경험: 로드 실패");
                        }
                    });
                }
            }
        });
    }

    private void setClickListeners() {
        tvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지 메인으로 돌아가기
                if (getActivity() != null) {
                    // MainActivity의 MypageFragment로 이동
                    ((MainActivity) getActivity()).showFragment(new MypageFragment());
                }
            }
        });

        // 다른 사용자의 프로필을 볼 때는 편집 기능을 비활성화
        if (targetUserId == null) {
            // 현재 사용자의 프로필일 때만 편집 기능 활성화
            llUserId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 마이페이지 메인으로 돌아가기
                    if (getActivity() != null) {
                        // MainActivity의 MypageFragment로 이동
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
        } else {
            // 다른 사용자의 프로필일 때는 편집 기능 비활성화
            llUserId.setClickable(false);
            llLanguagesAndRoles.setClickable(false);
            llExperience.setClickable(false);
            llTeamTendency.setClickable(false);
        }
    }

    private void loadUserPersonalityInfo() {
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
                            Log.d("MypageProfileFragment", "성향 정보 로드 성공: " + profile.getProfileCode());
                            
                            // UI 업데이트
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (tvTeamTendency != null) {
                                        tvTeamTendency.setText(profile.getProfileCode());
                                    }
                                });
                            }
                        } else {
                            Log.e("MypageProfileFragment", "성향 정보 로드 실패: " + response.code());
                            // 성향 정보가 없으면 "미완료" 표시
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (tvTeamTendency != null) {
                                        tvTeamTendency.setText("미완료");
                                    }
                                });
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<PersonalityProfileResponse> call, Throwable t) {
                        Log.e("MypageProfileFragment", "성향 정보 로드 네트워크 오류: " + t.getMessage());
                        // 네트워크 오류 시 "미완료" 표시
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (tvTeamTendency != null) {
                                    tvTeamTendency.setText("미완료");
                                }
                            });
                        }
                    }
                });
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
    
    private boolean hasPersonalityTestResult() {
        // TODO: 실제로는 데이터베이스나 SharedPreferences에서 사용자의 성향 테스트 결과 여부를 확인
        // 현재는 임시로 false를 반환 (테스트를 위해 필요시 true로 변경)
        return false;
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
