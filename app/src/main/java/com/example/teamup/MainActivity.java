package com.example.teamup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teamup.auth.LoginActivity;
import com.example.teamup.auth.LoginManager;
import com.example.teamup.MypageActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        
        // Setup custom navigation for profile
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_profile) {

                // 로그인 상태에 따라 다르게 동작
                if (LoginManager.isLoggedIn()) {
                    // 로그인된 경우 마이페이지 이동 코드 추가
                    Toast.makeText(MainActivity.this, "마이페이지로 이동", Toast.LENGTH_SHORT).show();
//                     Intent intent = new Intent(MainActivity.this, MypageActivity.class);
//                     startActivity(intent);
                } else {
                    // 로그인되지 않은 경우 로그인 페이지로 이동
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            } else if (itemId == R.id.navigation_home) {
                // Handle home navigation
                return true;
            } else if (itemId == R.id.navigation_contest) {
                // Handle contest navigation
                return true;
            } else if (itemId == R.id.navigation_board) {
                // Handle board navigation
                return true;
            }
            return false;
        });
    }
}