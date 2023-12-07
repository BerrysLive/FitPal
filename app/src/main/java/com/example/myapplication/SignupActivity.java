package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    EditText editText_email;
    EditText editText_password;
    EditText editText_confirm_password;
    Button register_button;
    Button return_button;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        editText_email = findViewById(R.id.register_email);
        editText_password = findViewById(R.id.password);
        editText_confirm_password = findViewById(R.id.confirmpassword);
        register_button = findViewById(R.id.registerbutton);
        return_button = findViewById(R.id.return_to_login);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editText_email.getText().toString().trim();
                String password = editText_password.getText().toString();
                String confirmPassword = editText_confirm_password.getText().toString();

                // Check if any field is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this, "Missing Field: Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Missing Field: Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "Missing Field: Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if password and confirm password are the same
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Register the user with Firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign up success
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                // TODO: Navigate to next activity or any other action
                            } else {
                                // If sign up fails, display a message to the user.
                                Toast.makeText(SignupActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return_to_login();
            }
        });
    }

    public void return_to_login(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
