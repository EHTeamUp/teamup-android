package kr.mojuk.teamup.recruitment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.ApiService;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.*;

import kr.mojuk.teamup.auth.TokenManager;
import kr.mojuk.teamup.databinding.FragmentContestRecruitmentDetailBinding;
import kr.mojuk.teamup.databinding.PopupApplyFormBinding;
import kr.mojuk.teamup.util.PlaceholderFragment;
import kr.mojuk.teamup.fragments.MypageProfileFragment;
import kr.mojuk.teamup.util.PlaceholderFragment;
import kr.mojuk.teamup.applicant.ApplicantListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestRecruitmentDetailFragment extends Fragment implements CommentAdapter.CommentActionListener {

    private FragmentContestRecruitmentDetailBinding binding;
    private ApiService apiService;

    private TeamMemberAdapter teamMemberAdapter;
    private CommentAdapter commentAdapter;

    private final List<CommentResponse> flatCommentList = new ArrayList<>();

    private int recruitmentPostId = -1;
    private String currentUserId;
    private RecruitmentPostResponse currentPost;
    private ContestInformation currentContest;
    private CommentResponse replyToComment = null;

    private enum UserRole { AUTHOR, MEMBER, APPLICANT }

    // ▼▼▼ 수정된 부분 ▼▼▼
    // 여러 비동기 호출을 추적하기 위한 카운터
    private AtomicInteger loadingCounter;
    // ▲▲▲ 수정된 부분 ▲▲▲

    public static ContestRecruitmentDetailFragment newInstance(int recruitmentPostId) {
        ContestRecruitmentDetailFragment fragment = new ContestRecruitmentDetailFragment();
        Bundle args = new Bundle();
        args.putInt("POST_ID", recruitmentPostId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recruitmentPostId = getArguments().getInt("POST_ID", -1);
        }
        currentUserId = TokenManager.getInstance(requireContext()).getUserId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContestRecruitmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (recruitmentPostId == -1 || currentUserId == null) {
            Toast.makeText(getContext(), "정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        apiService = RetrofitClient.getInstance().getApiService();
        setupRecyclerViews();
        setupCommentInput();
        loadAllData(); // ▼▼▼ 수정된 부분 ▼▼▼

        binding.llContestTitleBar.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    // ▼▼▼ 수정된 부분 ▼▼▼
    private void showLoading(boolean isLoading) {
        if (binding == null) return;
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.svContent.setVisibility(View.GONE);
            binding.llCommentInput.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.svContent.setVisibility(View.VISIBLE);
            binding.llCommentInput.setVisibility(View.VISIBLE);
        }
    }

    private void checkAllDataLoaded() {
        if (loadingCounter.decrementAndGet() == 0) {
            if(getActivity() != null) {
                getActivity().runOnUiThread(() -> showLoading(false));
            }
        }
    }

    private void loadAllData() {
        showLoading(true);
        loadingCounter = new AtomicInteger(5); // 5개의 API 호출을 기다림

        loadInitialRecruitmentPost();
        loadComments();
        checkApplicationStatus();
        checkUserRole(); // checkUserRole은 내부적으로 API를 호출하므로 카운트 포함
    }
    // ▲▲▲ 수정된 부분 ▲▲▲

    private void setupRecyclerViews() {
        teamMemberAdapter = new TeamMemberAdapter();
        binding.rvTeamMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTeamMembers.setAdapter(teamMemberAdapter);

        teamMemberAdapter.setOnMemberClickListener(userId -> {
            // 해당 팀원의 프로필로 이동
            navigateToFragment(MypageProfileFragment.newInstance(userId));
        });

        commentAdapter = new CommentAdapter(flatCommentList, currentUserId, this);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvComments.setAdapter(commentAdapter);
    }

    private void setupCommentInput() {
        binding.btnCommentSubmit.setOnClickListener(v -> {
            String content = binding.etCommentInput.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer parentId = (replyToComment != null) ? replyToComment.getCommentId() : null;
            CommentCreate newComment = new CommentCreate(content, recruitmentPostId, currentUserId, parentId);

            apiService.createComment(newComment).enqueue(new Callback<CommentResponse>() {
                @Override
                public void onResponse(@NonNull Call<CommentResponse> call, @NonNull Response<CommentResponse> response) {
                    if (binding == null) return; // Fragment가 destroy된 경우 처리하지 않음

                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        resetCommentInput();
                        loadComments(); // 댓글만 다시 로드
                    } else {
                        handleApiError("댓글 등록 실패", response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CommentResponse> call, @NonNull Throwable t) {
                    handleApiFailure("createComment", t);
                }
            });
        });
    }

    private void resetCommentInput() {
        binding.etCommentInput.setText("");
        binding.etCommentInput.setHint("내용을 적어주세요.");
        replyToComment = null;
        hideKeyboard();
    }

    private void loadInitialRecruitmentPost() {
        apiService.getRecruitmentPost(recruitmentPostId).enqueue(new Callback<RecruitmentPostResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecruitmentPostResponse> call, @NonNull Response<RecruitmentPostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPost = response.body();
                    if (binding != null) {
                        binding.tvPostTitle.setText(currentPost.getTitle());
                        binding.tvPostContent.setText(currentPost.getContent());
                    }

                    // 연관된 데이터 로딩 시작
                    loadContestDetails(currentPost.getContestId());
                    loadTeamMembersAndCheckRole(currentPost.getRecruitmentCount());
                } else {
                    handleApiError("게시글 정보 로딩 실패", response.code());
                    checkAllDataLoaded(); // 실패해도 카운트 감소
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecruitmentPostResponse> call, @NonNull Throwable t) {
                handleApiFailure("getRecruitmentPost", t);
                checkAllDataLoaded(); // 실패해도 카운트 감소
            }
        });
    }

    private void loadContestDetails(int contestId) {
        apiService.getContestDetail(contestId).enqueue(new Callback<ContestInformation>() {
            @Override
            public void onResponse(@NonNull Call<ContestInformation> call, @NonNull Response<ContestInformation> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    currentContest = response.body();
                    binding.contestTitleTextInBar.setText(currentContest.getName());

                    String dDayText = currentContest.getdDayText();
                    boolean isClosed = "마감됨".equals(dDayText) || (dDayText != null && dDayText.startsWith("D+"));

                    if (isClosed) {
                        binding.ddayText.setText("마감");
                        binding.btnApply.setEnabled(false);
                        binding.tvApplyText.setText("모집 마감");


                        binding.ivApplyArrowRight.setVisibility(View.GONE); // 화살표 숨기기
                        // 댓글 작성 비활성화
                        binding.etCommentInput.setEnabled(false);
                        binding.etCommentInput.setHint("댓글을 작성 할 수 없습니다");
                    } else {
                        binding.ddayText.setText(dDayText);

                        // 모집 중일 경우 댓글 작성 활성화
                        binding.etCommentInput.setEnabled(true);
                        binding.btnCommentSubmit.setEnabled(true);
                        binding.etCommentInput.setHint("내용을 적어주세요.");
                    }

                    if (getContext() != null) {
                        Glide.with(getContext()).load(currentContest.getPosterImgUrl()).into(binding.ivPoster);

                    }
                }
                checkAllDataLoaded(); // ▼▼▼ 수정된 부분 ▼▼▼
            }
            @Override
            public void onFailure(@NonNull Call<ContestInformation> call, @NonNull Throwable t) {
                handleApiFailure("getContestDetail", t);
                checkAllDataLoaded(); // ▼▼▼ 수정된 부분 ▼▼▼
            }
        });
    }

    private void loadTeamMembersAndCheckRole(int totalRecruitmentCount) {
        apiService.getAcceptedApplicationsByPost(recruitmentPostId).enqueue(new Callback<List<ApplicationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ApplicationResponse>> call, @NonNull Response<List<ApplicationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ApplicationResponse> teamMembers = response.body();
                    teamMemberAdapter.submitList(teamMembers);

                    if (binding != null) {
                        int currentMembersCount = teamMembers.size();
                        String memberCountText = String.format(Locale.getDefault(), "팀원 (%d / %d)", currentMembersCount, totalRecruitmentCount);
                        binding.teamMemberCount.setText(memberCountText);
                    }
                }
                checkAllDataLoaded(); // 로딩 카운터 감소
                // checkUserRole(); // 역할 확인은 별도 API 호출에서 처리
            }
            @Override
            public void onFailure(@NonNull Call<List<ApplicationResponse>> call, @NonNull Throwable t) {
                handleApiFailure("getAcceptedApplicationsByPost", t);
                checkAllDataLoaded(); // 실패해도 카운트 감소
            }
        });
    }

    private void loadComments() {
        apiService.getCommentsByPost(recruitmentPostId).enqueue(new Callback<List<CommentWithReplies>>() {
            @Override
            public void onResponse(@NonNull Call<List<CommentWithReplies>> call, @NonNull Response<List<CommentWithReplies>> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<CommentWithReplies> comments = response.body();
                    flatCommentList.clear();

                    if (comments.isEmpty()) {
                        binding.rvComments.setVisibility(View.GONE);
                        binding.tvNoComments.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvComments.setVisibility(View.VISIBLE);
                        binding.tvNoComments.setVisibility(View.GONE);

                        for (CommentWithReplies comment : comments) {
                            flatCommentList.add(comment);
                            if (comment.getReplies() != null) {
                                flatCommentList.addAll(comment.getReplies());
                            }
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                } else {
                    binding.rvComments.setVisibility(View.GONE);
                    binding.tvNoComments.setVisibility(View.VISIBLE);
                }
                checkAllDataLoaded(); // ▼▼▼ 수정된 부분 ▼▼▼
            }

            @Override
            public void onFailure(@NonNull Call<List<CommentWithReplies>> call, @NonNull Throwable t) {
                handleApiFailure("getCommentsByPost", t);
                if (binding != null) {
                    binding.rvComments.setVisibility(View.GONE);
                    binding.tvNoComments.setVisibility(View.VISIBLE);
                }
                checkAllDataLoaded(); // ▼▼▼ 수정된 부분 ▼▼▼
            }
        });
    }

    private void checkUserRole() {
        apiService.checkPostAuthor(recruitmentPostId, currentUserId).enqueue(new Callback<CheckAuthorResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckAuthorResponse> call, @NonNull Response<CheckAuthorResponse> response) {
                if (binding == null) return;

                UserRole role = UserRole.APPLICANT;
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isAuthor()) {
                        role = UserRole.AUTHOR;
                    } else {
                        List<ApplicationResponse> currentMembers = teamMemberAdapter.getCurrentList();
                        if (currentMembers != null) {
                            boolean isMember = currentMembers.stream().anyMatch(member -> member.getUserId().equals(currentUserId));
                            if (isMember) {
                                role = UserRole.MEMBER;
                            }
                        }
                    }
                }
                updateUiBasedOnRole(role);
                checkAllDataLoaded(); // ▼▼▼ 수정된 부분 ▼▼▼
            }
            @Override
            public void onFailure(@NonNull Call<CheckAuthorResponse> call, @NonNull Throwable t) {
                handleApiFailure("checkPostAuthor", t);
                checkAllDataLoaded(); // ▼▼▼ 수정된 부분 ▼▼▼
            }
        });
    }

    private void setAppliedState() {
        if (binding == null) return;

        binding.btnApply.setEnabled(false);
        binding.tvApplyText.setText("지원 완료");
        binding.ivApplyArrowRight.setVisibility(View.GONE);
    }

    private void checkApplicationStatus() {
        apiService.getApplicationsByPost(recruitmentPostId).enqueue(new Callback<List<Application>>() {
            @Override
            public void onResponse(@NonNull Call<List<Application>> call, @NonNull Response<List<Application>> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    boolean hasApplied = response.body().stream()
                            .anyMatch(application -> application.getUserId().equals(currentUserId));

                    if (hasApplied) {
                        setAppliedState();
                    }
                }
                checkAllDataLoaded(); // 로딩 카운터 감소
            }
            @Override
            public void onFailure(@NonNull Call<List<Application>> call, @NonNull Throwable t) {
                handleApiFailure("getApplicationsByPost", t);
                checkAllDataLoaded(); // 실패해도 카운트 감소
            }
        });
    }


    private void updateUiBasedOnRole(UserRole role) {
        if (binding == null) {
            return;
        }

        switch (role) {
            case AUTHOR:
                binding.llRecruiterView.setVisibility(View.VISIBLE);
                binding.btnApply.setVisibility(View.GONE);
                binding.fabContainer.setVisibility(View.VISIBLE);
                setupAuthorButtons();
                break;
            case MEMBER:
                binding.llRecruiterView.setVisibility(View.GONE);
                binding.btnApply.setVisibility(View.GONE);
                binding.fabEdit.setVisibility(View.GONE);
                binding.fabDelete.setVisibility(View.GONE);
                break;
            case APPLICANT:
            default:
                binding.llRecruiterView.setVisibility(View.GONE);
                binding.btnApply.setVisibility(View.VISIBLE);
                binding.fabEdit.setVisibility(View.GONE);
                binding.fabDelete.setVisibility(View.GONE);
                binding.btnApply.setOnClickListener(v -> showApplyDialog());
                break;
        }
    }

    private void setupAuthorButtons() {
        if (binding == null) {
            return;
        }

        binding.tvViewApplicants.setOnClickListener(v -> {
            navigateToFragment(ApplicantListFragment.newInstance(recruitmentPostId));
        });

        binding.fabEdit.setOnClickListener(v -> {
            if (currentPost != null && currentContest != null) {
                navigateToFragment(RecruitmentPostFragment.newInstanceForEdit(
                        currentPost.getRecruitmentPostId(),
                        currentPost.getContestId(),
                        currentContest.getName(),
                        currentPost.getTitle(),
                        currentPost.getContent(),
                        currentPost.getRecruitmentCount()
                ));
            } else {
                Toast.makeText(getContext(), "게시글 정보를 불러오는 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.fabDelete.setOnClickListener(v -> {
            showDeletePostConfirmationDialog();
        });
    }

    private void navigateToFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showApplyDialog() {
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        PopupApplyFormBinding popupBinding = PopupApplyFormBinding.inflate(getLayoutInflater());
        builder.setView(popupBinding.getRoot());
        AlertDialog dialog = builder.create();

        popupBinding.applySubmit.setOnClickListener(v -> {
            String message = popupBinding.applyMessage.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(getContext(), "지원 메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            ApplicationCreate dto = new ApplicationCreate(recruitmentPostId, currentUserId, message);
            apiService.createApplication(dto).enqueue(new Callback<ApplicationResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApplicationResponse> call, @NonNull Response<ApplicationResponse> response) {
                    if (binding == null) return;

                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "지원이 완료되었습니다.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        setAppliedState(); // 헬퍼 메서드 호출
                    } else {
                        handleApiError("지원 실패", response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApplicationResponse> call, @NonNull Throwable t) {
                    handleApiFailure("createApplication", t);
                }
            });
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void showDeletePostConfirmationDialog() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("게시글 삭제")
                .setMessage("정말로 이 게시글을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    deleteRecruitmentPost();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteRecruitmentPost() {
        apiService.deleteRecruitmentPost(recruitmentPostId, currentUserId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (binding == null) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    handleApiError("게시글 삭제 실패", response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handleApiFailure("deleteRecruitmentPost", t);
            }
        });
    }

    @Override
    public void onReplyClick(CommentResponse comment) {
        if (comment.getParentCommentId() == null) {
            replyToComment = comment;
        } else {
            replyToComment = findParentCommentById(comment.getParentCommentId());
        }

        if (replyToComment == null) {
            replyToComment = comment; // Fallback
        }

        binding.etCommentInput.setHint("@" + comment.getUserId() + "님에게 답글 남기기");
        binding.etCommentInput.requestFocus();
        showKeyboard();
    }

    private CommentResponse findParentCommentById(int parentId) {
        for (CommentResponse c : flatCommentList) {
            if (c.getCommentId() == parentId) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void onSaveClick(CommentResponse comment, String newContent) {
        CommentUpdate commentUpdate = new CommentUpdate(newContent);
        apiService.updateComment(comment.getCommentId(), commentUpdate).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommentResponse> call, @NonNull Response<CommentResponse> response) {
                if (binding == null) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    loadComments();
                } else {
                    handleApiError("댓글 수정 실패", response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommentResponse> call, @NonNull Throwable t) {
                handleApiFailure("updateComment", t);
            }
        });
    }

    @Override
    public void onDeleteClick(CommentResponse comment) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("댓글 삭제")
                .setMessage("정말로 이 댓글을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    deleteComment(comment.getCommentId());
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteComment(int commentId) {
        apiService.deleteComment(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (binding == null) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    loadComments();
                } else {
                    handleApiError("댓글 삭제 실패", response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handleApiFailure("deleteComment", t);
            }
        });
    }

    private void showKeyboard() {
        if (getContext() == null) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.etCommentInput, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        if (getContext() == null || getView() == null) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    private void handleApiError(String message, int code) {
        if(getContext() != null) Toast.makeText(getContext(), message + " (코드: " + code + ")", Toast.LENGTH_SHORT).show();
    }

    private void handleApiFailure(String methodName, Throwable t) {
        Log.e("RecruitmentDetail", methodName + " API Call Failed", t);
        if (getContext() != null) {
            Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}