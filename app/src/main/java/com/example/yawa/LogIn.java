package com.example.yawa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogIn extends AppCompatActivity {
Button login; EditText email_input, pass_input;
String email_value, pass_value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        login = findViewById(R.id.login);
        email_input = findViewById(R.id.email_input);
        pass_input = findViewById(R.id.pass_input);
        login.setOnClickListener(v -> {
            email_value = email_input.getText().toString();
            pass_value = pass_input.getText().toString();
            if (!email_value.isEmpty() && !pass_value.isEmpty()) {
                if (email_value.equals("user") && pass_value.equals("user")) {
                    Intent intent = new Intent(LogIn.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LogIn.this, "Ay mali, ulitin mo!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}