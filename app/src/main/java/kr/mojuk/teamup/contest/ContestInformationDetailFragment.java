package kr.mojuk.teamup.contest;

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
import kr.mojuk.teamup.R;
import kr.mojuk.teamup.api.ApiService;
import kr.mojuk.teamup.api.RetrofitClient;
import kr.mojuk.teamup.api.model.ContestInformation;
import kr.mojuk.teamup.api.model.Tag;
import kr.mojuk.teamup.databinding.FragmentContestInformationDetailBinding;
import kr.mojuk.teamup.recruitment.RecruitmentPostFragment;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContestInformationDetailFragment extends Fragment {

    private FragmentContestInformationDetailBinding binding;
    private ApiService apiService;
    private int contestId = -1;

    // Fragment 인스턴스를 생성하고 arguments를 설정하는 정적 메서드
    public static ContestInformationDetailFragment newInstance(ContestInformation contest) {
        ContestInformationDetailFragment fragment = new ContestInformationDetailFragment();
        Bundle args = new Bundle();
        args.putInt("CONTEST_ID", contest.getContestId());
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

    // ▼▼▼ 수정된 부분 ▼▼▼
    private void showLoading(boolean isLoading) {
        if (binding == null) return;
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.scrollView.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);
        }
    }
    // ▲▲▲ 수정된 부분 ▲▲▲

    private void loadContestDetailsFromApi(int contestId) {
        showLoading(true); // ▼▼▼ 수정된 부분 ▼▼▼
        apiService.getContestDetail(contestId).enqueue(new Callback<ContestInformation>() {
            @Override
            public void onResponse(@NonNull Call<ContestInformation> call, @NonNull Response<ContestInformation> response) {
                showLoading(false); // ▼▼▼ 수정된 부분 ▼▼▼
                if (response.isSuccessful() && response.body() != null) {
                    ContestInformation contestDetail = response.body();

                    // 디버깅용 로그 추가
                    Log.d("ContestDetailFragment", "Contest loaded: " + contestDetail.getName());
                    if (contestDetail.getTags() != null) {
                        Log.d("ContestDetailFragment", "Tags count: " + contestDetail.getTags().size());
                        for (Tag tag : contestDetail.getTags()) {
                            Log.d("ContestDetailFragment", "Tag: " + tag.getName() + " (ID: " + tag.getTagId() + ")");
                        }
                    } else {
                        Log.d("ContestDetailFragment", "No tags found");
                    }

                    updateUi(contestDetail);
                } else {
                    Toast.makeText(getContext(), "상세 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("ContestDetailFragment", "API response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContestInformation> call, @NonNull Throwable t) {
                showLoading(false); // ▼▼▼ 수정된 부분 ▼▼▼
                Log.e("ContestDetailFragment", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi(ContestInformation contestDetail) {
        binding.contestTitleTextInBar.setText(contestDetail.getName());

        // 포스터 이미지 로드
        if (getContext() != null) {
            Glide.with(getContext())
                    .load(contestDetail.getPosterImgUrl())
                    .placeholder(R.drawable.poster_sample1)
                    .error(R.drawable.poster_sample2)
                    .into(binding.ivPoster);
        }

        // 마감일 표시
        binding.tvDeadline.setText("모집마감 " + contestDetail.getdDayText());

        // 태그 표시 - 개선된 버전
        displayTags(contestDetail.getTags());

        // 공모전 사이트 URL 처리
        final String siteUrl = contestDetail.getContestUrl();
        if (siteUrl != null && !siteUrl.isEmpty()) {
            binding.tvGoToSite.setVisibility(View.VISIBLE);
            binding.tvGoToSite.setOnClickListener(v -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(siteUrl));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            binding.tvGoToSite.setVisibility(View.GONE);
        }

        // 글 작성 버튼
        binding.tvCreatePost.setOnClickListener(v -> {
            if (getActivity() != null) {
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

    // 태그를 표시하는 별도 메서드
    private void displayTags(List<Tag> tags) {
        if (tags != null && !tags.isEmpty()) {
            // 태그들을 "#태그명" 형식으로 변환하고 공백으로 연결
            String hashtags = tags.stream()
                    .map(Tag::getName)
                    .filter(name -> name != null && !name.trim().isEmpty()) // null이나 빈 문자열 제거
                    .map(name -> "#" + name.trim()) // 앞뒤 공백 제거하고 # 추가
                    .collect(Collectors.joining(" "));

            if (!hashtags.isEmpty()) {
                binding.tvHashtag.setText(hashtags);
                binding.tvHashtag.setVisibility(View.VISIBLE);
                Log.d("ContestDetailFragment", "Tags displayed: " + hashtags);
            } else {
                binding.tvHashtag.setVisibility(View.GONE);
                Log.d("ContestDetailFragment", "No valid tags to display");
            }
        } else {
            binding.tvHashtag.setVisibility(View.GONE);
            Log.d("ContestDetailFragment", "Tags list is null or empty");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }
}