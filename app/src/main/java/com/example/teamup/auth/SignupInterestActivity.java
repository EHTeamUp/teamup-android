package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class SignupInterestActivity extends AppCompatActivity {

    private ChipGroup chipGroupLanguages, chipGroupRoles;
    private EditText etLanguageInput, etRoleInput;
    private Button btnAddLanguage, btnAddRole;
    private TextView tvPrevious, tvNext;

    // 이전 액티비티에서 전달받은 데이터
    private String userId, userPassword, userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_interest);

        // 이전 액티비티에서 데이터 받기
        receiveDataFromPreviousActivity();

        // 뷰 초기화
        initViews();
        
        // 클릭 리스너 설정
        setClickListeners();
    }

    private void receiveDataFromPreviousActivity() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        userPassword = intent.getStringExtra("password");
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");
    }

    private void initViews() {
        chipGroupLanguages = findViewById(R.id.chipGroupLanguages);
        chipGroupRoles = findViewById(R.id.chipGroupRoles);
        etLanguageInput = findViewById(R.id.et_language_input);
        etRoleInput = findViewById(R.id.et_role_input);
        btnAddLanguage = findViewById(R.id.btn_add_language);
        btnAddRole = findViewById(R.id.btn_add_role);
        tvPrevious = findViewById(R.id.tv_previous);
        tvNext = findViewById(R.id.tv_next);
    }

    private void setClickListeners() {
        // 언어 추가 버튼
        btnAddLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = etLanguageInput.getText().toString().trim();
                if (!language.isEmpty()) {
                    addLanguageChip(language);
                    etLanguageInput.setText("");
                } else {
                    Toast.makeText(SignupInterestActivity.this, "언어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 역할 추가 버튼
        btnAddRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = etRoleInput.getText().toString().trim();
                if (!role.isEmpty()) {
                    addRoleChip(role);
                    etRoleInput.setText("");
                } else {
                    Toast.makeText(SignupInterestActivity.this, "역할을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Previous 버튼
        tvPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 액티비티로 돌아가기
            }
        });

        // Next 버튼
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSelections()) {
                    // 선택된 관심사들을 다음 액티비티로 전달
                    List<String> selectedLanguages = getSelectedLanguages();
                    List<String> selectedRoles = getSelectedRoles();
                    
                    // TODO: 다음 액티비티로 이동 (예: SignupExperienceActivity)
                    Intent intent = new Intent(SignupInterestActivity.this, SignupTestActivity.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("password", userPassword);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putStringArrayListExtra("languages", new ArrayList<>(selectedLanguages));
                    intent.putStringArrayListExtra("roles", new ArrayList<>(selectedRoles));
                    startActivity(intent);
                    
                    Toast.makeText(SignupInterestActivity.this, "관심사가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addLanguageChip(String language) {
        Chip chip = new Chip(this, null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice);
        chip.setText(language);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(false);
        chipGroupLanguages.addView(chip);
    }
    

    private void addRoleChip(String role) {
        Chip chip = new Chip(this, null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice);
        chip.setText(role);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(false);
        chipGroupRoles.addView(chip);
    }

    private List<String> getSelectedLanguages() {
        List<String> selectedLanguages = new ArrayList<>();
        for (int i = 0; i < chipGroupLanguages.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupLanguages.getChildAt(i);
            if (chip.isChecked()) {
                selectedLanguages.add(chip.getText().toString());
            }
        }
        return selectedLanguages;
    }

    private List<String> getSelectedRoles() {
        List<String> selectedRoles = new ArrayList<>();
        for (int i = 0; i < chipGroupRoles.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupRoles.getChildAt(i);
            if (chip.isChecked()) {
                selectedRoles.add(chip.getText().toString());
            }
        }
        return selectedRoles;
    }

    private boolean validateSelections() {
        List<String> selectedLanguages = getSelectedLanguages();
        List<String> selectedRoles = getSelectedRoles();

        if (selectedLanguages.isEmpty()) {
            Toast.makeText(this, "관심있는 프로그래밍 언어를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedRoles.isEmpty()) {
            Toast.makeText(this, "원하는 역할을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
