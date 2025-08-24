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
        loadInitialRecruitmentPost();
        checkApplicationStatus();

        binding.llContestTitleBar.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

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
                        loadComments();
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

                    loadContestDetails(currentPost.getContestId());
                    loadTeamMembersAndCheckRole(currentPost.getRecruitmentCount());
                    loadComments();
                    checkUserRole();
                } else {
                    handleApiError("게시글 정보 로딩 실패", response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecruitmentPostResponse> call, @NonNull Throwable t) {
                handleApiFailure("getRecruitmentPost", t);
            }
        });
    }

    private void loadContestDetails(int contestId) {
        apiService.getContestDetail(contestId).enqueue(new Callback<ContestInformation>() {
            @Override
            public void onResponse(@NonNull Call<ContestInformation> call, @NonNull Response<ContestInformation> response) {
                if (binding == null) return; // Fragment가 destroy된 경우 처리하지 않음

                if (response.isSuccessful() && response.body() != null) {
                    currentContest = response.body();
                    binding.contestTitleTextInBar.setText(currentContest.getName());

                    String dDayText = currentContest.getdDayText();
                    boolean isClosed = "마감됨".equals(dDayText) || (dDayText != null && dDayText.startsWith("D+"));

                    if (isClosed) {
                        binding.ddayText.setText("마감");
                        binding.btnApply.setEnabled(false);
                        binding.btnApply.setText("모집 마감");
                        if (getContext() != null) {
                            binding.btnApply.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                        }
                    } else {
                        binding.ddayText.setText(dDayText);
                    }

                    if (getContext() != null) {
                        Glide.with(getContext()).load(currentContest.getPosterImgUrl()).into(binding.ivPoster);

                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ContestInformation> call, @NonNull Throwable t) {
                handleApiFailure("getContestDetail", t);
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

                    checkUserRole();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<ApplicationResponse>> call, @NonNull Throwable t) {
                handleApiFailure("getAcceptedApplicationsByPost", t);
            }
        });
    }

    private void loadComments() {
        apiService.getCommentsByPost(recruitmentPostId).enqueue(new Callback<List<CommentWithReplies>>() {
            @Override
            public void onResponse(@NonNull Call<List<CommentWithReplies>> call, @NonNull Response<List<CommentWithReplies>> response) {
                if (binding == null) return; // Fragment가 destroy된 경우 처리하지 않음

                if (response.isSuccessful() && response.body() != null) {
                    flatCommentList.clear();
                    for (CommentWithReplies comment : response.body()) {
                        flatCommentList.add(comment);
                        if (comment.getReplies() != null) {
                            flatCommentList.addAll(comment.getReplies());
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<CommentWithReplies>> call, @NonNull Throwable t) {
                handleApiFailure("getCommentsByPost", t);
            }
        });
    }

    private void checkUserRole() {
        apiService.checkPostAuthor(recruitmentPostId, currentUserId).enqueue(new Callback<CheckAuthorResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckAuthorResponse> call, @NonNull Response<CheckAuthorResponse> response) {
                if (binding == null) return; // Fragment가 destroy된 경우 처리하지 않음

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
            }
            @Override
            public void onFailure(@NonNull Call<CheckAuthorResponse> call, @NonNull Throwable t) {
                handleApiFailure("checkPostAuthor", t);
            }
        });
    }

    private void setAppliedState() {
        if (binding == null) return;

        binding.btnApply.setEnabled(false);
        binding.btnApply.setText("지원 완료");

        // XML 레이아웃의 화살표 이미지뷰 ID가 'ivApplyArrow'라고 가정합니다.
        // 실제 ID가 다를 경우 이 부분을 수정해주세요.
         binding.ivApplyArrow.setVisibility(View.GONE);

        // (선택 사항) 지원 완료 시 배경색 등 디자인 변경
        // if (getContext() != null) {
        //     binding.btnApplyContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_gray));
        // }
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
            }
            @Override
            public void onFailure(@NonNull Call<List<Application>> call, @NonNull Throwable t) {
                handleApiFailure("getApplicationsByPost", t);
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
