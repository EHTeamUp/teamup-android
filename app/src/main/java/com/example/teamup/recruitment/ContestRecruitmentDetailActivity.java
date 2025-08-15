package com.example.teamup.recruitment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.example.teamup.applicant.ApplicantListActivity;
import com.example.teamup.recruitment.TeamSynergyScoreActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamup.R;
import com.example.teamup.databinding.ActivityContestRecruitmentDetailBinding;
import com.example.teamup.databinding.PopupApplyFormBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContestRecruitmentDetailActivity extends AppCompatActivity implements CommentAdapter.OnReplyClickListener  {

    private ActivityContestRecruitmentDetailBinding binding;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> flatCommentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContestRecruitmentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int postId = getIntent().getIntExtra("POST_ID", -1);
        if (postId != -1) {
            RecruitmentPostDetail postDetail = loadPostDetails(postId);
            updateUi(postDetail);
            setupCommentSection();
            setupCommentSubmitButton();
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
        // 기본 정보 설정
        TextView titleInBar = binding.llContestTitleBar.findViewById(R.id.contest_title_text_in_bar);
        if (titleInBar != null) {
            titleInBar.setText(postDetail.getContestTitle());
        }
        binding.ivPoster.setImageResource(postDetail.getPosterResourceId());
        binding.tvPrize.setText("상금: " + postDetail.getPrize());
        TextView dDayText = binding.llDeadline.findViewById(R.id.dday_text);
        if(dDayText != null) {
            dDayText.setText(postDetail.getdDay());
        }

        // 팀원 목록 RecyclerView 설정
        TeamMemberAdapter memberAdapter = new TeamMemberAdapter(postDetail.getTeamMembers());
        binding.rvTeamMembers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTeamMembers.setAdapter(memberAdapter);
        binding.teamMemberCount.setText("팀원 (" + postDetail.getTeamMembers().size() + "/6)");

        // 사용자 역할에 따른 UI 분기
        if (postDetail.isRecruiter()) {
            binding.llRecruiterView.setVisibility(View.VISIBLE);
            binding.btnApply.setVisibility(View.GONE);

            // '지원자 더 보러가기' 버튼 클릭 리스너
            binding.tvViewApplicants.setOnClickListener(v -> {
                Intent intent = new Intent(this, ApplicantListActivity.class);
                startActivity(intent);
            });

            // '팀 시너지 점수' 버튼 클릭 리스너
            binding.tvTeamSynergy.setOnClickListener(v -> {
                Intent intent = new Intent(this, TeamSynergyScoreActivity.class);
                startActivity(intent);
            });
        } else {
            binding.llRecruiterView.setVisibility(View.GONE);
            binding.btnApply.setVisibility(View.VISIBLE);
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

        // 다이얼로그 배경을 투명하게 설정
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
        // 1. 입력창 힌트 변경
        binding.etCommentInput.setHint("@" + username + "님에게 답글 남기기");

        // 2. 입력창으로 포커스 이동
        binding.etCommentInput.requestFocus();

        // 3. 키보드 올리기
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
     * 임시 상세 데이터를 생성하는 메서드
     */
    private RecruitmentPostDetail loadPostDetails(int postId) {
        if (postId == 1) {
            return new RecruitmentPostDetail("배리어프리 앱 개발 콘테스트", R.drawable.poster_sample1, "100만원", "D-10",
                    Arrays.asList(new TeamMember("hong_gil"), new TeamMember("dev_master")), true
            );
        } else {
            return new RecruitmentPostDetail("정부 데이터 활용 해커톤", R.drawable.poster_sample2, "500만원", "D-5",
                    Arrays.asList(new TeamMember("data_expert"), new TeamMember("ai_lover"), new TeamMember("web_god")), false
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