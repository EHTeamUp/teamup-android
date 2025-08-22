package com.example.teamup.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.example.teamup.MainActivity;
import com.example.teamup.R;

public class MypageInterestActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvBackArrow;
    private LinearLayout cvNavigation;
    
    // Language related views
    private ChipGroup chipGroupLanguages;
    private EditText etLanguageInput;
    private Button btnAddLanguage;
    
    // Role related views
    private ChipGroup chipGroupRoles;
    private EditText etRoleInput;
    private Button btnAddRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_interest);

        initViews();
        setClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        tvBackArrow = findViewById(R.id.tv_back_arrow);
        cvNavigation = findViewById(R.id.cv_navigation);
        
        // Language views
        chipGroupLanguages = findViewById(R.id.chipGroupLanguages);
        etLanguageInput = findViewById(R.id.et_language_input);
        btnAddLanguage = findViewById(R.id.btn_add_language);
        
        // Role views
        chipGroupRoles = findViewById(R.id.chipGroupRoles);
        etRoleInput = findViewById(R.id.et_role_input);
        btnAddRole = findViewById(R.id.btn_add_role);
    }

    private void setClickListeners() {
        tvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageInterestActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cvNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageInterestActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnAddLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLanguage = etLanguageInput.getText().toString().trim();
                if (!newLanguage.isEmpty()) {
                    addLanguageChip(newLanguage);
                    etLanguageInput.setText("");
                }
            }
        });

        btnAddRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newRole = etRoleInput.getText().toString().trim();
                if (!newRole.isEmpty()) {
                    addRoleChip(newRole);
                    etRoleInput.setText("");
                }
            }
        });
    }

    private void addLanguageChip(String language) {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.view_chip_choice, chipGroupLanguages, false);
        chip.setText(language);
        chipGroupLanguages.addView(chip);
    }

    private void addRoleChip(String role) {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.view_chip_choice, chipGroupRoles, false);
        chip.setText(role);
        chipGroupRoles.addView(chip);
    }

    private void setupBottomNavigation() {
        // 마이페이지 탭을 선택된 상태로 설정
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        
        // 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(MypageInterestActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_contest) {
                // 공모전 화면으로 이동
                return true;
            } else if (itemId == R.id.navigation_board) {
                // 게시판 화면으로 이동
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // 마이페이지 메인으로 이동
                Intent intent = new Intent(MypageInterestActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
