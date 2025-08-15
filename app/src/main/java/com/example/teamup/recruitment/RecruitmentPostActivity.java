package com.example.teamup.recruitment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.teamup.R;
import com.example.teamup.databinding.ActivityRecruitmentPostBinding;
import java.util.ArrayList;
import java.util.List;

public class RecruitmentPostActivity extends AppCompatActivity {

    private ActivityRecruitmentPostBinding binding;
    private boolean isEditMode = false; // 현재 모드를 저장하는 변수 (false: 새 글, true: 수정)
    private int postIdToEdit = -1;      // 수정할 게시글의 ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecruitmentPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupMemberCountSpinner();
        handleIntent();
        setupSubmitButton();
    }

    /**
     * Intent를 확인하여 '새 글' 모드와 '수정' 모드를 구분하고 UI를 설정합니다.
     */
    private void handleIntent() {
        Intent intent = getIntent();
        // "POST_ID" 키로 데이터가 넘어왔는지 확인하여 수정 모드인지 판단
        if (intent.hasExtra("POST_ID")) {
            isEditMode = true;
            postIdToEdit = intent.getIntExtra("POST_ID", -1);
            String contestName = intent.getStringExtra("CONTEST_NAME");
            String title = intent.getStringExtra("TITLE");
            String content = intent.getStringExtra("CONTENT");
            int memberCount = intent.getIntExtra("MEMBER_COUNT", 0);

            // UI에 기존 데이터 채우기
            TextView contestNameTextView = binding.contestNameBox.findViewById(R.id.tv_contest_name);
            contestNameTextView.setText(contestName);
            binding.etSubjectInput.setText(title);
            binding.etContentInput.setText(content);
            setSpinnerSelection(binding.spinnerMemberCount, memberCount + "명");
            binding.submitButton.setText("수정 완료");

        } else {
            // 새 글 모드
            isEditMode = false;
            String contestName = intent.getStringExtra("CONTEST_NAME");
            TextView contestNameTextView = binding.contestNameBox.findViewById(R.id.tv_contest_name);
            if (contestNameTextView != null && contestName != null) {
                contestNameTextView.setText(contestName);
            }
            binding.submitButton.setText("작성");
        }
    }

    /**
     * 모집 인원 수를 선택하는 Spinner를 설정합니다.
     */
    private void setupMemberCountSpinner() {
        List<String> memberCountList = new ArrayList<>();
        memberCountList.add("인원 미정");
        for (int i = 1; i <= 10; i++) {
            memberCountList.add(i + "명");
        }
        memberCountList.add("10명 이상");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, memberCountList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMemberCount.setAdapter(adapter);
    }

    /**
     * '작성' 또는 '수정 완료' 버튼의 클릭 이벤트를 설정합니다.
     */
    private void setupSubmitButton() {
        binding.submitButton.setOnClickListener(v -> {
            String title = binding.etSubjectInput.getText().toString().trim();
            String content = binding.etContentInput.getText().toString().trim();
            String selectedMemberCount = (String) binding.spinnerMemberCount.getSelectedItem();

            // 입력 유효성 검사
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: 여기서 데이터를 서버로 전송하는 API를 호출합니다.

            if (isEditMode) {
                // 수정 모드일 경우
                Toast.makeText(this, "게시글 " + postIdToEdit + "이(가) 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 새 글 모드일 경우
                Toast.makeText(this, "새로운 모집글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
            }

            // 작업 완료 후 현재 화면 종료
            finish();
        });
    }

    /**
     * Spinner에서 특정 값을 가진 아이템을 선택 상태로 만듭니다.
     */
    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}