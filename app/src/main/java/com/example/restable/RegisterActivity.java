package com.example.restable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    protected Button backButton;
    protected Button registerButton;
    protected EditText personNameEditText;
    protected EditText passwordEditText;
    protected EditText passwordConfirmEditText;
    protected EditText emailEditText;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        backButton = (Button) findViewById(R.id.buttonExitRegister);
        registerButton = (Button) findViewById(R.id.buttonRegisterUser);
        personNameEditText = (EditText) findViewById(R.id.editTextTextPersonName);
        emailEditText = (EditText) findViewById(R.id.editTextTextEmailAddressRegister);
        passwordEditText = (EditText) findViewById(R.id.editTextTextPasswordRegister);
        passwordConfirmEditText = (EditText) findViewById(R.id.editTextTextPasswordConfirmRegister);
        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        final String name = personNameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = passwordConfirmEditText.getText().toString().trim();

        if (name.isEmpty()) {
            personNameEditText.setError("Name is empty");
            personNameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email Address is empty");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email Address is not valid");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is empty");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be longer than 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            passwordConfirmEditText.setError("Password confirmation is empty");
            passwordConfirmEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            passwordConfirmEditText.setError("Password and password confirmation do not match");
            passwordEditText.requestFocus();
            passwordConfirmEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(name, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed. Try again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
    }
}