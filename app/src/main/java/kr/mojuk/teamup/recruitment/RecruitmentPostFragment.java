package kr.mojuk.teamup.recruitment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.mojuk.teamup.api.ApiService;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.RecruitmentPostRequest;
import kr.mojuk.teamup.api.model.RecruitmentPostResponse;
import kr.mojuk.teamup.auth.TokenManager;
import kr.mojuk.teamup.databinding.FragmentRecruitmentPostBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecruitmentPostFragment extends Fragment {

    private FragmentRecruitmentPostBinding binding;
    private ApiService apiService;

    // --- 모드 구분을 위한 변수들 ---
    private boolean isEditMode = false;
    private int postIdToEdit = -1;
    private int contestId = -1;
    private String contestName = "";

    private String currentUserId = null;

    // '새 글 작성' 모드용 newInstance
    public static RecruitmentPostFragment newInstanceForCreate(int contestId, String contestName) {
        RecruitmentPostFragment fragment = new RecruitmentPostFragment();
        Bundle args = new Bundle();
        args.putBoolean("IS_EDIT_MODE", false);
        args.putInt("CONTEST_ID", contestId);
        args.putString("CONTEST_NAME", contestName);
        fragment.setArguments(args);
        return fragment;
    }

    // '수정' 모드용 newInstance
    public static RecruitmentPostFragment newInstanceForEdit(int postId, int contestId, String contestName, String title, String content, int memberCount) {
        RecruitmentPostFragment fragment = new RecruitmentPostFragment();
        Bundle args = new Bundle();
        args.putBoolean("IS_EDIT_MODE", true);
        args.putInt("POST_ID_TO_EDIT", postId);
        args.putInt("CONTEST_ID", contestId);
        args.putString("CONTEST_NAME", contestName);
        args.putString("TITLE", title);
        args.putString("CONTENT", content);
        args.putInt("MEMBER_COUNT", memberCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Arguments에서 데이터 추출
        if (getArguments() != null) {
            isEditMode = getArguments().getBoolean("IS_EDIT_MODE");
            contestId = getArguments().getInt("CONTEST_ID", -1);
            contestName = getArguments().getString("CONTEST_NAME", "");
            if (isEditMode) {
                postIdToEdit = getArguments().getInt("POST_ID_TO_EDIT", -1);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecruitmentPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 수정: TokenManager를 사용하여 로그인 상태 확인 및 사용자 ID 가져오기 ---
        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        currentUserId = tokenManager.getUserId();

        apiService = RetrofitClient.getInstance().getApiService();

        setupMemberCountSpinner();
        setupUI();
        setupSubmitButton();

        binding.contestNameBox.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setupUI() {
        binding.tvContestName.setText(contestName);
        if (isEditMode) {
            binding.etSubjectInput.setText(getArguments().getString("TITLE", ""));
            binding.etContentInput.setText(getArguments().getString("CONTENT", ""));
            setSpinnerSelectionByValue(binding.spinnerMemberCount, getArguments().getInt("MEMBER_COUNT", 0));
            binding.submitButton.setText("수정 완료");
        } else {
            binding.submitButton.setText("작성");
        }
    }

    private void setupSubmitButton() {
        binding.submitButton.setOnClickListener(v -> {
            String title = binding.etSubjectInput.getText().toString().trim();
            String content = binding.etContentInput.getText().toString().trim();
            int memberCount = parseMemberCount((String) binding.spinnerMemberCount.getSelectedItem());

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || contestId == -1) {
                Toast.makeText(getContext(), "모든 정보를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (memberCount == 0) {
                Toast.makeText(getContext(), "모집 인원을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            RecruitmentPostRequest postRequest = new RecruitmentPostRequest(title, content, memberCount, contestId, currentUserId);

            if (isEditMode) {
                updatePost(postIdToEdit, postRequest);
            } else {
                createPost(postRequest);
            }
        });
    }

    private void createPost(RecruitmentPostRequest postRequest) {
        apiService.createRecruitmentPost(postRequest).enqueue(new Callback<RecruitmentPostResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecruitmentPostResponse> call, @NonNull Response<RecruitmentPostResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "게시글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<RecruitmentPostResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePost(int postId, RecruitmentPostRequest postRequest) {
        apiService.updateRecruitmentPost(postId, postRequest).enqueue(new Callback<RecruitmentPostResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecruitmentPostResponse> call, @NonNull Response<RecruitmentPostResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "게시글이 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "글 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<RecruitmentPostResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Helper Methods ---
    private void setupMemberCountSpinner() {
        List<String> memberCountList = new ArrayList<>();
        memberCountList.add("인원 미정");
        for (int i = 1; i <= 10; i++) {
            memberCountList.add(i + "명");
        }
        memberCountList.add("10명 이상");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, memberCountList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMemberCount.setAdapter(adapter);
    }

    private int parseMemberCount(String selection) {
        if ("인원 미정".equals(selection)) return 0;
        if ("10명 이상".equals(selection)) return 11;
        try {
            return Integer.parseInt(selection.replace("명", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setSpinnerSelectionByValue(Spinner spinner, int value) {
        String valueToSelect;
        if (value == 0) valueToSelect = "인원 미정";
        else if (value > 10) valueToSelect = "10명 이상";
        else valueToSelect = value + "명";

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(valueToSelect)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}