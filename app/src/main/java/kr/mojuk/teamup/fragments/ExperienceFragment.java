package kr.mojuk.teamup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.mojuk.teamup.R;
import kr.mojuk.teamup.MainActivity;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.Experience;
import kr.mojuk.teamup.api.model.ExperienceCreate;
import kr.mojuk.teamup.api.model.ProfileUpdateResponse;
import kr.mojuk.teamup.auth.TokenManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExperienceFragment extends Fragment {

    private static final String TAG = "ExperienceFragment";
    
    private LinearLayout llExperienceFormsContainer;
    private Button btnModifyExperience;
    private LinearLayout llBackNavigation;
    
    private List<View> experienceFormViews;
    private List<Experience> experiences;
    private boolean isMypageMode;
    private String userId;
    
    public interface ExperienceFragmentListener {
        void onBackPressed();
        void onExperienceUpdated();
        void onFormContentChanged(boolean hasContent); // form 내용 변경 감지
    }
    
    private ExperienceFragmentListener listener;
    
    public static ExperienceFragment newInstance(boolean isMypageMode, String userId) {
        ExperienceFragment fragment = new ExperienceFragment();
        Bundle args = new Bundle();
        args.putBoolean("isMypageMode", isMypageMode);
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ExperienceFragmentListener) {
            listener = (ExperienceFragmentListener) context;
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_experience, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 인자 받기
        Bundle args = getArguments();
        if (args != null) {
            isMypageMode = args.getBoolean("isMypageMode", false);
            userId = args.getString("userId", "");
        }
        
        initViews(view);
        setupListeners();
        
        if (isMypageMode) {
            // 마이페이지 모드: 기존 경험 정보 로드
            loadUserExperiences();
            btnModifyExperience.setVisibility(View.VISIBLE);
            llBackNavigation.setVisibility(View.VISIBLE); // 뒤로가기 버튼 표시
        } else {
            // 회원가입 모드: 빈 폼 표시
            addExperienceForm();
            llBackNavigation.setVisibility(View.GONE); // 뒤로가기 버튼 숨김
        }
    }
    
    private void initViews(View view) {
        llExperienceFormsContainer = view.findViewById(R.id.ll_experience_forms_container);
        btnModifyExperience = view.findViewById(R.id.btn_modify_experience);
        llBackNavigation = view.findViewById(R.id.ll_back_navigation);
        
        experienceFormViews = new ArrayList<>();
        experiences = new ArrayList<>();
    }
    
    private void setupListeners() {
        llBackNavigation.setOnClickListener(v -> {
            if (isMypageMode) {
                // 마이페이지 모드에서는 MypageProfileFragment로 이동
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showMypageProfileFragment();
                }
            } else {
                // 회원가입 모드에서는 기존 리스너 사용
                if (listener != null) {
                    listener.onBackPressed();
                }
            }
        });
        
        btnModifyExperience.setOnClickListener(v -> updateExperiences());
    }
    
    private void loadUserExperiences() {
        String token = TokenManager.getInstance(requireContext()).getAccessToken();
        if (token == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        RetrofitClient.getInstance()
                .getApiService()
                .getUserExperiences("Bearer " + token)
                .enqueue(new Callback<List<Experience>>() {
                    @Override
                    public void onResponse(Call<List<Experience>> call, Response<List<Experience>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            experiences = response.body();
                            displayExperiences();
                        } else {
                            Log.e(TAG, "경험 정보 로드 실패: " + response.code());
                            Toast.makeText(requireContext(), "경험 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<List<Experience>> call, Throwable t) {
                        Log.e(TAG, "경험 정보 로드 네트워크 오류", t);
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void displayExperiences() {
        llExperienceFormsContainer.removeAllViews();
        experienceFormViews.clear();
        
        if (experiences.isEmpty()) {
            // 경험이 없으면 빈 폼 하나 추가
            addExperienceForm();
        } else {
            // 기존 경험들을 폼으로 표시
            for (int i = 0; i < experiences.size(); i++) {
                Experience experience = experiences.get(i);
                addExperienceFormWithData(experience);
            }
            
            // 마지막 폼의 추가 버튼 표시
            if (!experienceFormViews.isEmpty()) {
                View lastForm = experienceFormViews.get(experienceFormViews.size() - 1);
                Button btnAddForm = lastForm.findViewById(R.id.btn_add_experience_form);
                btnAddForm.setVisibility(View.VISIBLE);
                btnAddForm.setOnClickListener(v -> addExperienceForm());
            }
        }
    }
    
    private void addExperienceForm() {
        // 기존 폼들의 추가 버튼 숨기기
        for (View formView : experienceFormViews) {
            Button btnAddForm = formView.findViewById(R.id.btn_add_experience_form);
            btnAddForm.setVisibility(View.GONE);
        }
        
        addExperienceFormWithData(null);
        
        // 새로 추가된 폼의 추가 버튼 표시
        if (!experienceFormViews.isEmpty()) {
            View lastForm = experienceFormViews.get(experienceFormViews.size() - 1);
            Button btnAddForm = lastForm.findViewById(R.id.btn_add_experience_form);
            btnAddForm.setVisibility(View.VISIBLE);
            btnAddForm.setOnClickListener(v -> addExperienceForm());
        }
    }
    
    private void addExperienceFormWithData(Experience experience) {
        View formView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_experience_form, llExperienceFormsContainer, false);
        
        // 폼 요소들 찾기
        EditText etContestName = formView.findViewById(R.id.et_contest_name_additional);
        Spinner spinnerCategory = formView.findViewById(R.id.spinner_category);
        EditText etDate = formView.findViewById(R.id.et_date_additional);
        CheckBox cbAwardReceived = formView.findViewById(R.id.cb_award_received);
        EditText etDescription = formView.findViewById(R.id.et_description_additional);
        
        // 카테고리 스피너 설정
        String[] categories = {"카테고리 선택", "웹/앱", "AI/데이터 사이언스", "아이디어/기획", "IoT/임베디드", "게임", "정보보안/블록체인"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        
        // 날짜 필드에 달력 기능 추가
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    etDate.setText(selectedDate);
                },
                year, month, day
            );
            
            // 오늘 이후 날짜 선택 불가
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });
        
        // 기존 데이터가 있으면 폼에 채우기
        if (experience != null) {
            etContestName.setText(experience.getAwardName());
            
            // 카테고리 설정
            String category = experience.getHostOrganization();
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(category)) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
            
            etDate.setText(experience.getAwardDate());
            cbAwardReceived.setChecked(true); // 수상 경험이므로 체크
            etDescription.setText(experience.getDescription());
        }
        
        // X 버튼 설정 (첫 번째 폼이 아니면 표시)
        TextView tvRemoveForm = formView.findViewById(R.id.tv_remove_form);
        if (experienceFormViews.isEmpty()) {
            // 첫 번째 폼이면 X 버튼 숨기기
            tvRemoveForm.setVisibility(View.GONE);
        } else {
            // 첫 번째 폼이 아니면 X 버튼 표시하고 클릭 리스너 설정
            tvRemoveForm.setVisibility(View.VISIBLE);
            tvRemoveForm.setOnClickListener(v -> removeExperienceForm(formView));
        }
        
        // 추가 버튼 클릭 리스너 (마지막 폼에만 표시)
        Button btnAddForm = formView.findViewById(R.id.btn_add_experience_form);
        // 기본적으로는 숨김으로 설정 (addExperienceForm에서 마지막 폼만 표시)
        btnAddForm.setVisibility(View.GONE);
        
        llExperienceFormsContainer.addView(formView);
        experienceFormViews.add(formView);
        
        // form 내용 변경 감지 리스너 추가
        setupFormChangeListeners(formView);
    }
    
    private void removeExperienceForm(View formView) {
        llExperienceFormsContainer.removeView(formView);
        experienceFormViews.remove(formView);
        
        // 폼이 삭제된 후 첫 번째 폼의 X 버튼 숨기기
        if (!experienceFormViews.isEmpty()) {
            View firstForm = experienceFormViews.get(0);
            TextView tvRemoveForm = firstForm.findViewById(R.id.tv_remove_form);
            tvRemoveForm.setVisibility(View.GONE);
        }
        
        // 마지막 폼의 추가 버튼 표시
        if (!experienceFormViews.isEmpty()) {
            View lastForm = experienceFormViews.get(experienceFormViews.size() - 1);
            Button btnAddForm = lastForm.findViewById(R.id.btn_add_experience_form);
            btnAddForm.setVisibility(View.VISIBLE);
            btnAddForm.setOnClickListener(v -> addExperienceForm());
        }
        
        // form 내용 변경 감지
        checkFormContentAndNotify();
    }
    
    /**
     * form 내용 변경 감지 리스너 설정
     */
    private void setupFormChangeListeners(View formView) {
        EditText etContestName = formView.findViewById(R.id.et_contest_name_additional);
        Spinner spinnerCategory = formView.findViewById(R.id.spinner_category);
        EditText etDate = formView.findViewById(R.id.et_date_additional);
        CheckBox cbAwardReceived = formView.findViewById(R.id.cb_award_received);
        EditText etDescription = formView.findViewById(R.id.et_description_additional);
        
        // 텍스트 변경 리스너
        android.text.TextWatcher textWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormContentAndNotify();
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        };
        
        etContestName.addTextChangedListener(textWatcher);
        etDate.addTextChangedListener(textWatcher);
        etDescription.addTextChangedListener(textWatcher);
        
        // 스피너 변경 리스너
        spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                checkFormContentAndNotify();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        // 체크박스 변경 리스너
        cbAwardReceived.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkFormContentAndNotify();
        });
    }
    
    /**
     * form 내용이 있는지 확인하고 리스너에 알림
     */
    private void checkFormContentAndNotify() {
        boolean hasContent = false;
        
        for (View formView : experienceFormViews) {
            EditText etContestName = formView.findViewById(R.id.et_contest_name_additional);
            Spinner spinnerCategory = formView.findViewById(R.id.spinner_category);
            EditText etDate = formView.findViewById(R.id.et_date_additional);
            CheckBox cbAwardReceived = formView.findViewById(R.id.cb_award_received);
            EditText etDescription = formView.findViewById(R.id.et_description_additional);
            
            // 하나라도 내용이 있으면 hasContent = true
            if (!etContestName.getText().toString().trim().isEmpty() ||
                spinnerCategory.getSelectedItemPosition() > 0 || // "카테고리 선택"이 아닌 경우
                !etDate.getText().toString().trim().isEmpty() ||
                cbAwardReceived.isChecked() ||
                !etDescription.getText().toString().trim().isEmpty()) {
                hasContent = true;
                break;
            }
        }
        
        // 리스너에 알림
        if (listener != null) {
            listener.onFormContentChanged(hasContent);
        }
    }
    
    private int getFilterIdByCategory(String category) {
        switch (category) {
            case "웹/앱":
                return 1;
            case "AI/데이터 사이언스":
                return 2;
            case "아이디어/기획":
                return 3;
            case "IoT/임베디드":
                return 4;
            case "게임":
                return 5;
            case "정보보안/블록체인":
                return 6;
            default:
                return 1; // 기본값
        }
    }
    
    private void updateExperiences() {
        List<Experience> updatedExperiences = new ArrayList<>();
        boolean hasError = false;
        
        for (int i = 0; i < experienceFormViews.size(); i++) {
            View formView = experienceFormViews.get(i);
            EditText etContestName = formView.findViewById(R.id.et_contest_name_additional);
            Spinner spinnerCategory = formView.findViewById(R.id.spinner_category);
            EditText etDate = formView.findViewById(R.id.et_date_additional);
            CheckBox cbAwardReceived = formView.findViewById(R.id.cb_award_received);
            EditText etDescription = formView.findViewById(R.id.et_description_additional);
            
            String contestName = etContestName.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String date = etDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            // 필수 필드 검증
            if (!contestName.isEmpty() || !category.equals("카테고리 선택") || !date.isEmpty()) {
                // 하나라도 입력된 경우 모든 필수 필드 검증
                if (contestName.isEmpty()) {
                    Toast.makeText(requireContext(), (i + 1) + "번째 폼의 공모전명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    hasError = true;
                    break;
                }
                
                if (category.equals("카테고리 선택")) {
                    Toast.makeText(requireContext(), (i + 1) + "번째 폼의 카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    hasError = true;
                    break;
                }
                
                if (date.isEmpty()) {
                    Toast.makeText(requireContext(), (i + 1) + "번째 폼의 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    hasError = true;
                    break;
                }
                
                // 모든 필수 필드가 입력된 경우에만 추가
                Experience experience = new Experience();
                experience.setContestName(contestName);  // contest_name 필드
                experience.setAwardName(contestName);     // award_name 필드 (동일한 값)
                experience.setHostOrganization(category); // host_organization 필드
                experience.setAwardDate(date);           // award_date 필드
                experience.setDescription(description);   // description 필드
                
                // 카테고리별 filter_id 매핑
                int filterId = getFilterIdByCategory(category);
                experience.setFilterId(filterId);
                
                updatedExperiences.add(experience);
            }
        }
        
        if (hasError) {
            return; // 오류가 있으면 API 호출하지 않음
        }
        
        // API 호출
        String token = TokenManager.getInstance(requireContext()).getAccessToken();
        if (token == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 디버깅을 위한 로그 추가
        Log.d(TAG, "업데이트할 경험 개수: " + updatedExperiences.size());
        for (int i = 0; i < updatedExperiences.size(); i++) {
            Experience exp = updatedExperiences.get(i);
            Log.d(TAG, "경험 " + (i + 1) + ": " + exp.getContestName() + ", " + exp.getHostOrganization() + ", " + exp.getAwardDate() + ", filter_id: " + exp.getFilterId());
        }
        
        // ExperienceCreate 객체 생성 전 로그
        Log.d(TAG, "ExperienceCreate 객체 생성 전 - experiences 크기: " + updatedExperiences.size());
        for (Experience exp : updatedExperiences) {
            Log.d(TAG, "Experience 객체 - contest_name: " + exp.getContestName() + ", filter_id: " + exp.getFilterId());
        }
        
        // ExperienceCreate 객체 생성
        ExperienceCreate experienceCreate = new ExperienceCreate(updatedExperiences);
        Log.d(TAG, "ExperienceCreate 객체 생성 완료: " + experienceCreate.toString());
        
        RetrofitClient.getInstance()
                .getApiService()
                .updateUserExperiences("Bearer " + token, experienceCreate)
                .enqueue(new Callback<ProfileUpdateResponse>() {
                    @Override
                    public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "경험 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.onExperienceUpdated();
                            }
                        } else {
                            Log.e(TAG, "경험 정보 수정 실패: " + response.code());
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "오류 응답 없음";
                                Log.e(TAG, "오류 응답: " + errorBody);
                            } catch (IOException e) {
                                Log.e(TAG, "오류 응답 읽기 실패", e);
                            }
                            Toast.makeText(requireContext(), "경험 정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                        Log.e(TAG, "경험 정보 수정 네트워크 오류", t);
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    // 회원가입에서 사용할 때 선택된 경험들 반환
    public List<Experience> getSelectedExperiences() {
        List<Experience> selectedExperiences = new ArrayList<>();
        
        for (View formView : experienceFormViews) {
            EditText etContestName = formView.findViewById(R.id.et_contest_name_additional);
            Spinner spinnerCategory = formView.findViewById(R.id.spinner_category);
            EditText etDate = formView.findViewById(R.id.et_date_additional);
            CheckBox cbAwardReceived = formView.findViewById(R.id.cb_award_received);
            EditText etDescription = formView.findViewById(R.id.et_description_additional);
            
            String contestName = etContestName.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String date = etDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            // 모든 필수 필드가 입력되었는지 확인
            // "카테고리 선택"은 빈 값으로 처리
            if (!contestName.isEmpty() && 
                !category.equals("카테고리 선택") && 
                !date.isEmpty() && 
                !description.isEmpty()) {
                
                Experience experience = new Experience();
                experience.setContestName(contestName);  // contest_name 필드에 설정
                experience.setAwardName(contestName);    // award_name 필드에도 동일한 값 설정
                experience.setHostOrganization(category);
                experience.setAwardDate(date);
                experience.setDescription(description);
                
                // 카테고리별 filter_id 매핑
                int filterId = getFilterIdByCategory(category);
                experience.setFilterId(filterId);
                
                selectedExperiences.add(experience);
            }
        }
        
        return selectedExperiences;
    }
    
    /**
     * form에 내용이 있는지 확인 (모든 form 검사)
     */
    public boolean hasAnyFormContent() {
        for (View formView : experienceFormViews) {
            EditText etContestName = formView.findViewById(R.id.et_contest_name_additional);
            Spinner spinnerCategory = formView.findViewById(R.id.spinner_category);
            EditText etDate = formView.findViewById(R.id.et_date_additional);
            CheckBox cbAwardReceived = formView.findViewById(R.id.cb_award_received);
            EditText etDescription = formView.findViewById(R.id.et_description_additional);
            
            String contestName = etContestName.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String date = etDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            // 하나라도 내용이 있으면 true 반환
            if (!contestName.isEmpty() || 
                !category.equals("카테고리 선택") || 
                !date.isEmpty() || 
                !description.isEmpty() ||
                cbAwardReceived.isChecked()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * form view 리스트 반환
     */
    public List<View> getExperienceFormViews() {
        return experienceFormViews;
    }
}
