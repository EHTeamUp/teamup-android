package kr.mojuk.teamup.auth;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * JWT 토큰에서 사용자 정보를 추출하는 유틸리티 클래스
 */
public class JwtUtils {
    
    private static final String TAG = "JwtUtils";
    
    /**
     * JWT 토큰에서 페이로드 부분을 디코딩하여 사용자 정보 추출
     */
    public static JsonObject decodeJwtPayload(String token) {
        try {
            // JWT 토큰은 "header.payload.signature" 형식
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                Log.e(TAG, "Invalid JWT token format");
                return null;
            }
            
            // 페이로드 부분 디코딩
            String payload = parts[1];
            String decodedPayload = new String(Base64.decode(payload, Base64.URL_SAFE));
            
            // Log.d(TAG, "JWT 페이로드 디코딩: " + decodedPayload);
            
            // JSON으로 파싱
            Gson gson = new Gson();
            return gson.fromJson(decodedPayload, JsonObject.class);
            
        } catch (Exception e) {
            Log.e(TAG, "Error decoding JWT token: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public static String getUserIdFromToken(String token) {
        JsonObject payload = decodeJwtPayload(token);
        if (payload != null) {
            // JWT 표준에서는 'sub' 필드가 사용자 ID를 나타냄
            if (payload.has("sub")) {
                String userId = payload.get("sub").getAsString();
                // Log.d(TAG, "JWT에서 추출된 사용자 ID (sub): " + userId);
                return userId;
            } else if (payload.has("user_id")) {
                String userId = payload.get("user_id").getAsString();
                // Log.d(TAG, "JWT에서 추출된 사용자 ID (user_id): " + userId);
                return userId;
            } else {
                Log.w(TAG, "JWT에서 사용자 ID를 찾을 수 없습니다. 페이로드: " + payload);
            }
        }
        return null;
    }
    
    /**
     * JWT 토큰에서 사용자 이름 추출
     */
    public static String getNameFromToken(String token) {
        JsonObject payload = decodeJwtPayload(token);
        if (payload != null && payload.has("name")) {
            return payload.get("name").getAsString();
        }
        return null;
    }
    
    /**
     * JWT 토큰에서 이메일 추출
     */
    public static String getEmailFromToken(String token) {
        JsonObject payload = decodeJwtPayload(token);
        if (payload != null && payload.has("email")) {
            return payload.get("email").getAsString();
        }
        return null;
    }
    
    /**
     * JWT 토큰의 만료 시간 확인
     */
    public static boolean isTokenExpired(String token) {
        JsonObject payload = decodeJwtPayload(token);
        if (payload != null && payload.has("exp")) {
            long expTime = payload.get("exp").getAsLong();
            long currentTime = System.currentTimeMillis() / 1000; // 초 단위로 변환
            return currentTime >= expTime;
        }
        return false;
    }
    
    /**
     * JWT 토큰의 모든 사용자 정보 추출
     */
    public static JsonObject getAllUserInfoFromToken(String token) {
        return decodeJwtPayload(token);
    }
}
