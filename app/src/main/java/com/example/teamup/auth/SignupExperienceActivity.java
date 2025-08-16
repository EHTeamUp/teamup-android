package com.example.teamup.auth;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamup.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SignupExperienceActivity extends AppCompatActivity {
    
    private EditText etContestName, etAwardTitle, etExperience;
    private TextView btnNext, tvPrevious;
    private LinearLayout llExperienceSection;
    private Button btnAddExperience;
    
    // 동적으로 추가된 입력 폼들을 관리
    private List<View> additionalForms = new ArrayList<>();
    
    // 이전 액티비티에서 전달받은 데이터
    private String userId, userPassword, userName, userEmail;
    private String[] languages, roles;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_experience);
        
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
        languages = intent.getStringArrayExtra("languages");
        roles = intent.getStringArrayExtra("roles");
    }
    
    @SuppressLint("WrongViewCast")
    private void initViews() {
        etContestName = findViewById(R.id.et_contest_name);
        etAwardTitle = findViewById(R.id.et_award_title);
        etExperience = findViewById(R.id.et_description);
        btnNext = findViewById(R.id.tv_skip);
        tvPrevious = findViewById(R.id.tv_previous);
        llExperienceSection = findViewById(R.id.ll_experience_section);
        btnAddExperience = findViewById(R.id.btn_add_experience);
        
        // btnNext는 TextView이므로 클릭 가능하도록 설정
        btnNext.setClickable(true);
        btnNext.setFocusable(true);
        
        // 첫 번째 폼의 날짜 필드에 DatePicker 설정
        setupDatePicker();
        
        // 첫 번째 폼의 입력 상태를 실시간으로 감지
        setupTextWatchers();
    }
    
    private void setupTextWatchers() {
        android.text.TextWatcher textWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateButtonText();
            }
        };
        
        etContestName.addTextChangedListener(textWatcher);
        etAwardTitle.addTextChangedListener(textWatcher);
        etExperience.addTextChangedListener(textWatcher);
    }
    
    private void setClickListeners() {
        // Previous 버튼
        tvPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignupTestActivity로 이동
                Intent intent = new Intent(SignupExperienceActivity.this, SignupTestActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("password", userPassword);
                intent.putExtra("name", userName);
                intent.putExtra("email", userEmail);
                intent.putExtra("languages", languages);
                intent.putExtra("roles", roles);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });
        
        // 추가 버튼
        btnAddExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExperienceForm();
            }
        });
        
        // Next/Skip 버튼
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = btnNext.getText().toString();
                
                if (buttonText.equals("Next →")) {
                    // Next 버튼일 때는 첫 번째 폼 + 추가 폼들의 유효성 검사 수행
                    if (validateInput() && validateAdditionalForms()) {
                        // 다음 액티비티로 이동 (SignupFinishActivity)
                        Intent intent = new Intent(SignupExperienceActivity.this, SignupFinishActivity.class);
                        intent.putExtra("id", userId);
                        intent.putExtra("password", userPassword);
                        intent.putExtra("name", userName);
                        intent.putExtra("email", userEmail);
                        intent.putExtra("languages", languages);
                        intent.putExtra("roles", roles);
                        intent.putExtra("contestName", etContestName.getText().toString().trim());
                        intent.putExtra("awardTitle", etAwardTitle.getText().toString().trim());
                        intent.putExtra("experience", etExperience.getText().toString().trim());
                        startActivity(intent);
                    }
                } else {
                    // Skip 버튼일 때는 유효성 검사 없이 바로 이동
                    Intent intent = new Intent(SignupExperienceActivity.this, SignupFinishActivity.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("password", userPassword);
                    intent.putExtra("name", userName);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("languages", languages);
                    intent.putExtra("roles", roles);
                    intent.putExtra("contestName", etContestName.getText().toString().trim());
                    intent.putExtra("awardTitle", etAwardTitle.getText().toString().trim());
                    intent.putExtra("experience", etExperience.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
        
        // 초기 버튼 상태 설정
        updateButtonText();
    }
    
    private boolean validateInput() {
        String contestName = etContestName.getText().toString().trim();
        String awardTitle = etAwardTitle.getText().toString().trim();
        String experience = etExperience.getText().toString().trim();
        
        EditText etDate = findViewById(R.id.et_date);
        String date = etDate.getText().toString().trim();
        
        if (contestName.isEmpty()) {
            Toast.makeText(this, "공모전명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (awardTitle.isEmpty()) {
            Toast.makeText(this, "수상명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (date.isEmpty()) {
            Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (experience.isEmpty()) {
            Toast.makeText(this, "설명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private boolean validateAdditionalForms() {
        for (View form : additionalForms) {
            EditText etContestName = form.findViewById(R.id.et_contest_name_additional);
            EditText etAwardTitle = form.findViewById(R.id.et_award_title_additional);
            EditText etDescription = form.findViewById(R.id.et_description_additional);
            EditText etDate = form.findViewById(R.id.et_date_additional);

            if (etContestName.getText().toString().trim().isEmpty() ||
                etAwardTitle.getText().toString().trim().isEmpty() ||
                etDate.getText().toString().trim().isEmpty() ||
                etDescription.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "모든 경험 입력 폼을 완료해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void addExperienceForm() {
        // 새로운 입력 폼을 위한 레이아웃 생성
        View newForm = LayoutInflater.from(this).inflate(R.layout.item_experience_form, llExperienceSection, false);
        
        // 구분선 추가
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        ));
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        
        // 삭제 버튼에 클릭 리스너 추가
        TextView tvRemoveForm = newForm.findViewById(R.id.tv_remove_form);
        tvRemoveForm.setClickable(true);
        tvRemoveForm.setFocusable(true);
        tvRemoveForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeExperienceForm(newForm, divider);
            }
        });
        
        // 추가된 폼의 EditText들에 TextWatcher 설정
        setupAdditionalFormTextWatchers(newForm);
        
        // 추가 버튼 앞에 새로운 폼과 구분선 추가
        int addButtonIndex = llExperienceSection.indexOfChild(btnAddExperience);
        llExperienceSection.addView(divider, addButtonIndex);
        llExperienceSection.addView(newForm, addButtonIndex + 1);
        
        // 추가된 폼을 리스트에 저장
        additionalForms.add(newForm);
        
        // 버튼 텍스트 업데이트
        updateButtonText();
        
        Toast.makeText(this, "새로운 경험 입력 폼이 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }
    
    private void removeExperienceForm(View form, View divider) {
        // 폼과 구분선 제거
        llExperienceSection.removeView(form);
        llExperienceSection.removeView(divider);
        
        // 리스트에서도 제거
        additionalForms.remove(form);
        
        // 버튼 텍스트 업데이트
        updateButtonText();
        
        Toast.makeText(this, "경험 입력 폼이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void updateButtonText() {
        // 첫 번째 폼이 완성되었는지 확인
        boolean firstFormComplete = isFirstFormComplete();
        
        // 첫 번째 폼이 완성되면 "Next", 아니면 "Skip"
        if (firstFormComplete) {
            btnNext.setText("Next →");
        } else {
            btnNext.setText("Skip →");
        }
    }
    
    private boolean isFirstFormComplete() {
        String contestName = etContestName.getText().toString().trim();
        String awardTitle = etAwardTitle.getText().toString().trim();
        String experience = etExperience.getText().toString().trim();
        
        EditText etDate = findViewById(R.id.et_date);
        String date = etDate.getText().toString().trim();
        
        return !contestName.isEmpty() && !awardTitle.isEmpty() && !date.isEmpty() && !experience.isEmpty();
    }

    private void setupAdditionalFormTextWatchers(View form) {
        EditText etContestName = form.findViewById(R.id.et_contest_name_additional);
        EditText etAwardTitle = form.findViewById(R.id.et_award_title_additional);
        EditText etDescription = form.findViewById(R.id.et_description_additional);
        EditText etDate = form.findViewById(R.id.et_date_additional);
        
        android.text.TextWatcher textWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateButtonText();
            }
        };
        
        etContestName.addTextChangedListener(textWatcher);
        etAwardTitle.addTextChangedListener(textWatcher);
        etDescription.addTextChangedListener(textWatcher);
        
        // 추가된 폼의 날짜 필드에 DatePicker 설정
        setupAdditionalFormDatePicker(etDate);
    }

    private void setupDatePicker() {
        EditText etDate = findViewById(R.id.et_date);
        
        // 오늘 날짜를 최대 날짜로 설정
        Calendar calendar = Calendar.getInstance();
        int maxYear = calendar.get(Calendar.YEAR);
        int maxMonth = calendar.get(Calendar.MONTH);
        int maxDay = calendar.get(Calendar.DAY_OF_MONTH);
        
        etDate.setFocusable(false);
        etDate.setClickable(true);
        
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SignupExperienceActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, month, dayOfMonth);
                            
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String selectedDateString = dateFormat.format(selectedDate.getTime());
                            etDate.setText(selectedDateString);
                            
                            // 날짜 선택 후 버튼 텍스트 업데이트
                            updateButtonText();
                        }
                    },
                    maxYear, maxMonth, maxDay
                );
                
                // 오늘 날짜까지만 선택 가능하도록 설정
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                
                datePickerDialog.show();
            }
        });
    }

    private void setupAdditionalFormDatePicker(EditText etDate) {
        // 오늘 날짜를 최대 날짜로 설정
        Calendar calendar = Calendar.getInstance();
        int maxYear = calendar.get(Calendar.YEAR);
        int maxMonth = calendar.get(Calendar.MONTH);
        int maxDay = calendar.get(Calendar.DAY_OF_MONTH);
        
        etDate.setFocusable(false);
        etDate.setClickable(true);
        
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SignupExperienceActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, month, dayOfMonth);
                            
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String selectedDateString = dateFormat.format(selectedDate.getTime());
                            etDate.setText(selectedDateString);
                            
                            // 날짜 선택 후 버튼 텍스트 업데이트
                            updateButtonText();
                        }
                    },
                    maxYear, maxMonth, maxDay
                );
                
                // 오늘 날짜까지만 선택 가능하도록 설정
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                
                datePickerDialog.show();
            }
        });
    }
}
