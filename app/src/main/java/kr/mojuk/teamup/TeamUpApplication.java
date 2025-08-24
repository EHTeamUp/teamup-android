package kr.mojuk.teamup;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;

public class TeamUpApplication extends Application {
    private static final String TAG = "TeamUpApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Firebase 초기화
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase 초기화 성공");
        } catch (Exception e) {
            Log.e(TAG, "Firebase 초기화 실패: " + e.getMessage());
        }
    }
}
