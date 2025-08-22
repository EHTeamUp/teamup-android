package com.example.teamup.contest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.teamup.R;
import com.example.teamup.api.ApiService;
import com.example.teamup.api.RetrofitClient;
import com.example.teamup.api.model.ContestInformation;
import com.example.teamup.api.model.Tag;
import com.example.teamup.databinding.FragmentContestInformationDetailBinding;
import com.example.teamup.recruitment.RecruitmentPostFragment;

import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestInformationDetailFragment extends Fragment {

    private FragmentContestInformationDetailBinding binding;
    private ApiService apiService;
    private int contestId = -1;

    // Fragment 인스턴스를 생성하고 arguments를 설정하는 정적 메서드
    public static ContestInformationDetailFragment newInstance(int contestId) {
        ContestInformationDetailFragment fragment = new ContestInformationDetailFragment();
        Bundle args = new Bundle();
        args.putInt("CONTEST_ID", contestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Arguments에서 contestId를 안전하게 가져옴
        if (getArguments() != null) {
            contestId = getArguments().getInt("CONTEST_ID", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContestInformationDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getInstance().getApiService();

        if (contestId != -1) {
            loadContestDetailsFromApi(contestId);
        } else {
            Toast.makeText(getContext(), "공모전 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            // 이전 Fragment로 돌아가기
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }

        // 뒤로가기 버튼
        binding.btnBackTitle.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadContestDetailsFromApi(int contestId) {
        apiService.getContestDetail(contestId).enqueue(new Callback<ContestInformation>() {
            @Override
            public void onResponse(@NonNull Call<ContestInformation> call, @NonNull Response<ContestInformation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUi(response.body());
                } else {
                    Toast.makeText(getContext(), "상세 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContestInformation> call, @NonNull Throwable t) {
                Log.e("ContestDetailFragment", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi(ContestInformation contestDetail) {
        binding.contestTitleTextInBar.setText(contestDetail.getName());

        // Glide 사용 시 getActivity() 또는 getContext()로 Context를 전달
        if (getContext() != null) {
            Glide.with(getContext())
                    .load(contestDetail.getPosterImgUrl())
                    .placeholder(R.drawable.poster_sample1)
                    .error(R.drawable.poster_sample2)
                    .into(binding.ivPoster);
        }

        binding.tvDeadline.setText("모집마감 " + contestDetail.getdDayText());

        if (contestDetail.getTags() != null && !contestDetail.getTags().isEmpty()) {
            String hashtags = contestDetail.getTags().stream()
                    .map(Tag::getName)
                    .map(name -> "#" + name)
                    .collect(Collectors.joining(" "));
            binding.tvHashtag.setText(hashtags);
        } else {
            binding.tvHashtag.setVisibility(View.GONE);
        }

        final String siteUrl = contestDetail.getContestUrl();
        if (siteUrl != null && !siteUrl.isEmpty()) {
            binding.btnGoToSite.setVisibility(View.VISIBLE);
            binding.btnGoToSite.setOnClickListener(v -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(siteUrl));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            binding.btnGoToSite.setVisibility(View.GONE);
        }

        binding.btnCreatePost.setOnClickListener(v -> {
            if (getActivity() != null) {
                // '새 글 작성' 모드로 RecruitmentPostFragment를 호출합니다.
                RecruitmentPostFragment recruitmentFragment = RecruitmentPostFragment.newInstanceForCreate(
                        contestDetail.getContestId(),
                        contestDetail.getName()
                );

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, recruitmentFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}