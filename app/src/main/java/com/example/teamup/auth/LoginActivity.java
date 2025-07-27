package com.example.teamup.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teamup.MainActivity;
import com.example.teamup.R;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etId, etPassword;
    private MaterialButton btnLogin, btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etId = findViewById(R.id.et_id);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignIn = findViewById(R.id.btn_sign_in);

        // Setup button click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etId.getText().toString();
                String password = etPassword.getText().toString();
                
                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter ID and Password", Toast.LENGTH_SHORT).show();
                } else {
                    // Show success message and go back to MainActivity
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Close LoginActivity and return to MainActivity
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etId.getText().toString();
                String password = etPassword.getText().toString();
                
                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter ID and Password", Toast.LENGTH_SHORT).show();
                } else {
                    // Show success message and go back to MainActivity
                    Toast.makeText(LoginActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Close LoginActivity and return to MainActivity
                }
            }
        });
    }
} 