package com.health_care.screen.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.health_care.R;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout email, password;
    ExtendedFloatingActionButton login;
    TextView go_to_register;
    LinearLayout progressBar_layout;
    CardView login_form;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews();
        initAction();
    }

    private void initAction() {

        go_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(goToRegister);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    signIn();
                }
            }
        });
    }

    private boolean validate() {
        boolean validate = true;
        if (email.getEditText().getText().toString().replaceAll(" ", "").equals("")) {
            email.setError("Email is Required");
            validate = false;
        } else
            email.setError(null);


        if (password.getEditText().getText().toString().replaceAll(" ", "").equals("")) {
            password.setError("Password is Required");
            validate = false;
        } else
            password.setError(null);
        return validate;
    }

    void signIn() {
        progressBar_layout.setVisibility(View.VISIBLE);
        login_form.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(email.getEditText().getText().toString(), password.getEditText().getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar_layout.setVisibility(View.GONE);
                        login_form.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("logged", true);
                            editor.apply();

                            Intent goToMain = new Intent(LoginActivity.this, MainHomeActivity.class);
                            startActivity(goToMain);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                });
    }

    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        go_to_register = findViewById(R.id.go_to_register);
        progressBar_layout = findViewById(R.id.progressBar_layout);
        login_form = findViewById(R.id.login_form);
    }
}