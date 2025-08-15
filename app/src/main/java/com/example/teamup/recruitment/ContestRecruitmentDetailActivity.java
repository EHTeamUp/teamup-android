package com.example.teamup.recruitment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.R;
import com.example.teamup.applicant.ApplicantListActivity;
import com.example.teamup.databinding.ActivityContestRecruitmentDetailBinding;
import com.example.teamup.databinding.PopupApplyFormBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.widget.TextView;
import android.widget.Toast;

public class ContestRecruitmentDetailActivity extends AppCompatActivity implements CommentAdapter.OnReplyClickListener {

    private ActivityContestRecruitmentDetailBinding binding;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> flatCommentList;
    private boolean isFabOpen = false;
    private RecruitmentPostDetail currentPostDetail; // 현재 게시글 데이터를 저장할 변수

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContestRecruitmentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int postId = getIntent().getIntExtra("POST_ID", -1);
        if (postId != -1) {
            currentPostDetail = loadPostDetails(postId); // 불러온 데이터를 멤버 변수에 저장
            updateUi(currentPostDetail);
            setupCommentSection();
            setupCommentSubmitButton();
            setupFabButtons(postId, currentPostDetail); // setupFabButtons에 데이터 전달
        } else {
            Toast.makeText(this, "ID를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.llContestTitleBar.setOnClickListener(v -> finish());
    }

    /**
     * UI를 상세 데이터로 채우는 메서드
     */
    private void updateUi(RecruitmentPostDetail postDetail) {
        TextView titleInBar = binding.llContestTitleBar.findViewById(R.id.contest_title_text_in_bar);
        if (titleInBar != null) {
            titleInBar.setText(postDetail.getContestTitle());
        }
        binding.ivPoster.setImageResource(postDetail.getPosterResourceId());
        binding.tvPrize.setText("상금: " + postDetail.getPrize());
        TextView dDayText = binding.llDeadline.findViewById(R.id.dday_text);
        if (dDayText != null) {
            dDayText.setText(postDetail.getdDay());
        }

        TeamMemberAdapter memberAdapter = new TeamMemberAdapter(postDetail.getTeamMembers());
        binding.rvTeamMembers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTeamMembers.setAdapter(memberAdapter);
        binding.teamMemberCount.setText("팀원 (" + postDetail.getTeamMembers().size() + "/6)");

        if (postDetail.isRecruiter()) {
            binding.llRecruiterView.setVisibility(View.VISIBLE);
            binding.btnApply.setVisibility(View.GONE);
            binding.fabEdit.setVisibility(View.VISIBLE);

            binding.tvViewApplicants.setOnClickListener(v -> {
                Intent intent = new Intent(this, ApplicantListActivity.class);
                startActivity(intent);
            });

            binding.tvTeamSynergy.setOnClickListener(v -> {
                Intent intent = new Intent(this, TeamSynergyScoreActivity.class);
                startActivity(intent);
            });
        } else {
            binding.llRecruiterView.setVisibility(View.GONE);
            binding.btnApply.setVisibility(View.VISIBLE);
            binding.fabEdit.setVisibility(View.GONE);
            binding.btnApply.setOnClickListener(v -> showApplyDialog());
        }
    }

    /**
     * '지원하기' 팝업 다이얼로그를 생성하고 보여주는 메서드
     */
    private void showApplyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        PopupApplyFormBinding popupBinding = PopupApplyFormBinding.inflate(getLayoutInflater());
        builder.setView(popupBinding.getRoot());
        AlertDialog dialog = builder.create();

        popupBinding.applySubmit.setOnClickListener(v -> {
            String message = popupBinding.applyMessage.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "지원 메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "지원이 완료되었습니다: " + message, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    /**
     * 댓글 섹션을 설정하는 메서드
     */
    private void setupCommentSection() {
        commentsRecyclerView = findViewById(R.id.rv_comments);
        List<Comment> nestedComments = createDummyComments();

        flatCommentList = new ArrayList<>();
        for (Comment comment : nestedComments) {
            flatCommentList.add(comment);
            if (comment.getReplies() != null) {
                flatCommentList.addAll(comment.getReplies());
            }
        }

        commentAdapter = new CommentAdapter(flatCommentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);
        commentAdapter.setOnReplyClickListener(this);
    }

    @Override
    public void onReplyClick(String username) {
        binding.etCommentInput.setHint("@" + username + "님에게 답글 남기기");
        binding.etCommentInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.etCommentInput, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 댓글 '작성' 버튼의 클릭 이벤트를 설정하는 메서드
     */
    private void setupCommentSubmitButton() {
        binding.btnCommentSubmit.setOnClickListener(v -> {
            String content = binding.etCommentInput.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Comment newComment = new Comment(
                    (int) System.currentTimeMillis(), "Me (나)", content, new ArrayList<>(), CommentAdapter.VIEW_TYPE_COMMENT
            );

            flatCommentList.add(newComment);
            commentAdapter.notifyItemInserted(flatCommentList.size() - 1);
            commentsRecyclerView.scrollToPosition(flatCommentList.size() - 1);
            binding.etCommentInput.setText("");
            binding.etCommentInput.setHint("내용을 적어주세요.");
            hideKeyboard(v);
        });
    }

    /**
     * 키보드를 숨기는 유틸리티 메서드
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 수정/삭제 플로팅 버튼의 기능과 확장 애니메이션을 설정하는 메서드
     */
    private void setupFabButtons(int postId, RecruitmentPostDetail postDetail) {
        binding.fabDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("삭제 확인")
                    .setMessage("정말 이 모집글을 삭제하시겠습니까?")
                    .setPositiveButton("삭제", (dialog, which) -> {
                        Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("취소", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        binding.fabEdit.setOnClickListener(v -> {
            if (isFabOpen) {
                Intent intent = new Intent(this, RecruitmentPostActivity.class);
                intent.putExtra("POST_ID", postId);
                intent.putExtra("CONTEST_NAME", postDetail.getContestTitle());
                intent.putExtra("TITLE", postDetail.getTitle());
                intent.putExtra("CONTENT", postDetail.getContent());
                intent.putExtra("MEMBER_COUNT", postDetail.getMemberCount());
                startActivity(intent);
                toggleFabMenu();
            } else {
                toggleFabMenu();
            }
        });
    }

    /**
     * 플로팅 버튼 메뉴를 열고 닫는 애니메이션을 처리하는 메서드
     */
    private void toggleFabMenu() {
        if (isFabOpen) {
            binding.fabDelete.animate().translationY(0).alpha(0).setDuration(300);
            binding.fabEdit.animate().rotation(0).setDuration(300);
            isFabOpen = false;
        } else {
            binding.fabDelete.setVisibility(View.VISIBLE);
            binding.fabDelete.setAlpha(0f);
            binding.fabDelete.animate().translationY(-binding.fabEdit.getHeight() - 32).alpha(1).setDuration(300);
            binding.fabEdit.animate().rotation(45).setDuration(300);
            isFabOpen = true;
        }
    }

    /**
     * 임시 상세 데이터를 생성하는 메서드
     */
    private RecruitmentPostDetail loadPostDetails(int postId) {
        if (postId == 1) {
            return new RecruitmentPostDetail(
                    "배리어프리 앱 개발 콘테스트", R.drawable.poster_sample1, "100만원", "D-10",
                    Arrays.asList(new TeamMember("hong_gil"), new TeamMember("dev_master")), true,
                    "프론트엔드 개발자 구합니다!", "React Native 경험자 우대합니다. 함께 좋은 앱 만들어요.", 2
            );
        } else {
            return new RecruitmentPostDetail(
                    "정부 데이터 활용 해커톤", R.drawable.poster_sample2, "500만원", "D-5",
                    Arrays.asList(new TeamMember("data_expert"), new TeamMember("ai_lover"), new TeamMember("web_god")), false,
                    "데이터 분석가/AI 엔지니어 모집", "공공 데이터 포털 API 사용 경험 있으신 분 찾습니다.", 4
            );
        }
    }

    /**
     * 임시로 대댓글이 포함된 댓글 데이터를 생성하는 메서드
     */
    private List<Comment> createDummyComments() {
        Comment reply1 = new Comment(3, "dev_master", "네, 좋습니다! 참여할게요.", null, CommentAdapter.VIEW_TYPE_REPLY);
        Comment reply2 = new Comment(4, "designer_kim", "저도 참여하고 싶어요!", null, CommentAdapter.VIEW_TYPE_REPLY);
        Comment parent1 = new Comment(1, "hong_gil", "https://open.kakao.com/o/XXXXXXXXX\n여기 들어와주세요~", Arrays.asList(reply1, reply2), CommentAdapter.VIEW_TYPE_COMMENT);
        Comment parent2 = new Comment(2, "game_lover", "혹시 기획자도 구하시나요?", new ArrayList<>(), CommentAdapter.VIEW_TYPE_COMMENT);
        return Arrays.asList(parent1, parent2);
    }
}