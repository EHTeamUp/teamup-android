package kr.mojuk.teamup.util;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 임시 플레이스홀더 프래그먼트입니다.
 * 아직 구현되지 않은 화면으로 이동하는 네비게이션 테스트를 위해 사용됩니다.
 */
public class PlaceholderFragment extends Fragment {

    private static final String SCREEN_NAME_KEY = "SCREEN_NAME";

    /**
     * 어떤 화면에 대한 임시 프래그먼트인지 이름을 받아 새 인스턴스를 생성합니다.
     * @param screenName 표시할 화면 이름 (예: "지원자 목록")
     * @return PlaceholderFragment의 새 인스턴스
     */
    public static PlaceholderFragment newInstance(String screenName) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(SCREEN_NAME_KEY, screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // XML 레이아웃 파일 없이 코드로 간단한 TextView를 생성하여 화면을 구성합니다.
        TextView textView = new TextView(getContext());
        if (getArguments() != null) {
            String screenName = getArguments().getString(SCREEN_NAME_KEY, "알 수 없는 화면");
            textView.setText(screenName + " 화면입니다.\n(나중에 실제 화면으로 교체됩니다)");
        }
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return textView;
    }
}
