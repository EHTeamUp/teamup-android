package com.example.teamup.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
// Firebase 설정 완료 후 주석 해제
// import com.google.firebase.messaging.FirebaseMessagingService;
// import com.google.firebase.messaging.RemoteMessage;

// Firebase 설정 완료 후 주석 해제하고 FirebaseMessagingService 상속
public class FirebaseMessagingService /* extends FirebaseMessagingService */ {

    private static final String TAG = "TeamUpFCM";
    private static final String CHANNEL_ID = "teamup_notifications";
    private static final String CHANNEL_NAME = "TeamUp 알림";
    private static final String CHANNEL_DESCRIPTION = "TeamUp 앱의 알림 채널";

    // Firebase 설정 완료 후 주석 해제
    /*
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "새로운 FCM 토큰: " + token);
        
        // 토큰을 서버에 전송
        sendFcmTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "메시지 수신: " + remoteMessage.getFrom());

        // 알림 데이터 확인
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "메시지 데이터: " + remoteMessage.getData());
        }

        // 알림 메시지 확인
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "알림 제목: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "알림 내용: " + remoteMessage.getNotification().getBody());
            
            // 알림 표시
            sendNotification(
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody()
            );
        }
    }
    */

    /**
     * 알림 채널 생성 (Android 8.0 이상)
     */
    private void createNotificationChannel() {
        // Firebase 설정 완료 후 주석 해제
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        */
    }

    /**
     * 알림 표시
     */
    private void sendNotification(String title, String messageBody) {
        // Firebase 설정 완료 후 주석 해제
        /*
        // 알림 채널 생성
        createNotificationChannel();

        // MainActivity로 이동하는 Intent 생성
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // 알림 소리 설정
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // 알림 빌더 생성
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title != null ? title : "TeamUp")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

        // 알림 매니저를 통해 알림 표시
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
        */
    }

    /**
     * FCM 토큰을 서버에 전송
     */
    private void sendFcmTokenToServer(String token) {
        // Firebase 설정 완료 후 주석 해제
        // FCM 토큰 매니저를 통해 서버에 전송
        // FcmTokenManager.getInstance(this).updateFcmToken(token);
    }
}
