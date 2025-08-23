package kr.mojuk.teamup.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import kr.mojuk.teamup.MainActivity;
import kr.mojuk.teamup.api.ApiService;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.ContestInformation;
import kr.mojuk.teamup.api.model.RecruitmentPostResponse;
import kr.mojuk.teamup.api.model.ContestsListResponse;
import kr.mojuk.teamup.contest.ContestListFragment;
import kr.mojuk.teamup.contest.ContestInformationDetailFragment;
import kr.mojuk.teamup.recruitment.ContestRecruitmentListFragment;
import kr.mojuk.teamup.recruitment.ContestRecruitmentDetailFragment;

import kr.mojuk.teamup.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private LinearLayout containerContests;
    private LinearLayout containerRecruitments;
    private LayoutInflater inflater;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout btnRecruitmentList = root.findViewById(R.id.btn_recruitment_list);
        LinearLayout btnContestList = root.findViewById(R.id.btn_contest_list);

        btnContestList.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ContestListFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            
            // 하단 네비게이션 바 공모전 탭 활성화
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setBottomNavigationItem(R.id.navigation_contest);
            }
        });

        btnRecruitmentList.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ContestRecruitmentListFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            
            // 하단 네비게이션 바 게시판 탭 활성화
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setBottomNavigationItem(R.id.navigation_board);
            }
        });

        this.inflater = inflater;
        containerContests = root.findViewById(R.id.containerContests);
        containerRecruitments = root.findViewById(R.id.containerRecruitments);

        loadLatestContests();
        loadLatestRecruitments();

        return root;
    }
    // 최신 공모전 목록 로드
    private void loadLatestContests() {
        Log.d("HomeFragment", "🔍 loadLatestContests 시작");
        ApiService api = RetrofitClient.getInstance().getApiService();
        api.getLatestContests().enqueue(new Callback<List<ContestInformation>>() {
            @Override
            public void onResponse(Call<List<ContestInformation>> call, Response<List<ContestInformation>> response) {
                Log.d("HomeFragment", "API 응답 받음 - 성공: " + response.isSuccessful() + ", 코드: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeFragment", "응답 body 크기: " + response.body().size());
                    containerContests.removeAllViews();
                    int totalContests = 0;
                    for (ContestInformation contest : response.body()) {
                        if (contest != null) {
                            Log.d("HomeFragment", "공모전 추가: " + contest.getName());
                            View itemView = inflater.inflate(R.layout.item_contest, containerContests, false);

                            TextView tvTitle = itemView.findViewById(R.id.tvContestTitle);
                            TextView tvDDay = itemView.findViewById(R.id.tvContestDDay);
                            ImageView ivThumb = itemView.findViewById(R.id.ivContestThumbnail);

                            tvTitle.setText(contest.getName());
                            tvDDay.setText(contest.getdDayText());

                            // 썸네일 Glide로 로딩
                            Glide.with(itemView.getContext())
                                    .load(contest.getPosterImgUrl())
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(ivThumb);

                            // 공모전 아이템 클릭 리스너 추가
                            itemView.setOnClickListener(v -> {
                                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                ContestInformationDetailFragment fragment = ContestInformationDetailFragment.newInstance(contest);
                                transaction.replace(R.id.fragment_container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                
                                // 하단 네비게이션 바 공모전 탭 활성화
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).setBottomNavigationItem(R.id.navigation_contest);
                                }
                            });
                            
                            containerContests.addView(itemView);
                            totalContests++;
                        }
                    }
                    Log.d("HomeFragment", "총 " + totalContests + "개 공모전 아이템 추가 완료");
                } else {
                    Log.e("HomeFragment", "API 응답 실패 또는 body가 null - 코드: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("HomeFragment", "에러 내용: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("HomeFragment", "에러 내용 읽기 실패", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ContestInformation>> call, Throwable t) {
                Log.e("HomeFragment", "loadLatestContests 네트워크 실패", t);
            }

        });
    }

    // 최신 팀원 모집 게시글 로드
    private void loadLatestRecruitments() {
        ApiService api = RetrofitClient.getInstance().getApiService();
        api.getLatestRecruitments().enqueue(new Callback<List<RecruitmentPostResponse>>() {
            @Override
            public void onResponse(Call<List<RecruitmentPostResponse>> call, Response<List<RecruitmentPostResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    containerRecruitments.removeAllViews();
                    for (RecruitmentPostResponse recruit : response.body()) {
                        if (recruit != null) {
                            View itemView = inflater.inflate(R.layout.item_team_recruitment, containerRecruitments, false);

                            TextView titleText = itemView.findViewById(R.id.titleText);
                            TextView dDayText = itemView.findViewById(R.id.dDayText);
                            TextView peopleText = itemView.findViewById(R.id.peopleText);
                            TextView organizerText = itemView.findViewById(R.id.organizerText);

                                                    titleText.setText(recruit.getTitle());
                        
                        // D-day 계산
                        String calculatedDDay = calculateDDay(recruit.getDueDate());
                        dDayText.setText(calculatedDDay);
                        
                        peopleText.setText("모집 인원: " + recruit.getRecruitmentCount());
                        organizerText.setText("모집자: " + recruit.getUserId());

                            // 팀원 모집 게시글 아이템 클릭 리스너 추가
                            itemView.setOnClickListener(v -> {
                                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                ContestRecruitmentDetailFragment fragment = ContestRecruitmentDetailFragment.newInstance(recruit.getRecruitmentPostId());
                                transaction.replace(R.id.fragment_container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                
                                // 하단 네비게이션 바 게시판 탭 활성화
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).setBottomNavigationItem(R.id.navigation_board);
                                }
                            });
                            
                            containerRecruitments.addView(itemView);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RecruitmentPostResponse>> call, Throwable t) {
                Log.e("HomeFragment", "loadLatestRecruitments failed", t);
            }
        });
    }
    
    /**
     * 날짜로 D-Day 계산
     * @param dueDateString 
     * @return 
     */
    private String calculateDDay(String dueDateString) {
        try {
            LocalDate dueDate = LocalDate.parse(dueDateString, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate today = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(today, dueDate);

            if (daysBetween == 0) {
                return "D-Day";
            } else if (daysBetween > 0) {
                return "D-" + daysBetween;
            } else {
                return "D+" + Math.abs(daysBetween);
            }
        } catch (Exception e) {
            return "날짜 미정";
        }
    }
}
