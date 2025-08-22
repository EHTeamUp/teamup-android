package kr.mojuk.teamup.notification;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NotificationPermissionHelper {
    private static final String TAG = "NotificationPermission";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    /**
     * 알림 권한이 필요한지 확인
     */
    public static boolean isNotificationPermissionRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    /**
     * 알림 권한이 허용되었는지 확인
     */
    public static boolean isNotificationPermissionGranted(Context context) {
        if (!isNotificationPermissionRequired()) {
            return true; // Android 13 미만에서는 권한이 필요하지 않음
        }
        
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 알림 권한 요청
     */
    public static void requestNotificationPermission(Activity activity) {
        if (!isNotificationPermissionRequired()) {
            Log.d(TAG, "알림 권한이 필요하지 않습니다.");
            return;
        }

        if (isNotificationPermissionGranted(activity)) {
            Log.d(TAG, "알림 권한이 이미 허용되어 있습니다.");
            return;
        }

        Log.d(TAG, "알림 권한을 요청합니다.");
        ActivityCompat.requestPermissions(
            activity,
            new String[]{Manifest.permission.POST_NOTIFICATIONS},
            NOTIFICATION_PERMISSION_REQUEST_CODE
        );
    }

    /**
     * 권한 요청 결과 처리
     */
    public static boolean handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "알림 권한이 허용되었습니다.");
                return true;
            } else {
                Log.d(TAG, "알림 권한이 거부되었습니다.");
                return false;
            }
        }
        return false;
    }
}
