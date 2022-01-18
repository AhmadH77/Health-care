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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout email , name , password;
    ExtendedFloatingActionButton register;


    private RadioGroup genderRadioGroup;
    private RadioButton gender;
    private RadioGroup accountTypeRadioGroup;
    private RadioButton accountType;
    private ProgressBar progressBar;
    private LinearLayout progressBar_layout;
    private CardView form;
    private TextView go_to_login;

    //
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews();
        initActions();
    }

    private void initActions() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()) {

                    addUSer();
                }
            }
        });

        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLogin = new Intent(RegisterActivity.this , LoginActivity.class);
                startActivity(goToLogin);
                finish();
            }
        });
    }

    private boolean validate() {
        boolean validate = true;
        if(email.getEditText().getText().toString().replaceAll(" ","").equals("")) {
            email.setError("Email is Required");
            validate = false;
        }else
            email.setError(null);

        if(name.getEditText().getText().toString().replaceAll(" ","").equals("")) {
            name.setError("Name is Required");
            validate = false;
        }else
            name.setError(null);

        if(password.getEditText().getText().toString().replaceAll(" ","").equals("")) {
            password.setError("Password is Required");
            validate = false;
        }else
            password.setError(null);
        return validate;
    }

    private void addUSer() {

        //
        progressBar.setVisibility(View.VISIBLE);
        progressBar_layout.setVisibility(View.VISIBLE);
        form.setVisibility(View.GONE);
        mAuth.createUserWithEmailAndPassword(
                email.getEditText().getText().toString(),
                password.getEditText().getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");

                            genderRadioGroup = (RadioGroup) findViewById(R.id.gender_select);
                            int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                            gender = (RadioButton) findViewById(selectedId);

                            accountTypeRadioGroup = (RadioGroup) findViewById(R.id.account_type_group);
                            int selectedId1 = accountTypeRadioGroup.getCheckedRadioButtonId();
                            accountType = (RadioButton) findViewById(selectedId1);

                            Log.i("TAG", "onClick: " + email.getEditText().getText() + "  " + gender.getText() + "  " + accountType.getText());

                            SharedPreferences preferences = getSharedPreferences("user" ,Context.MODE_PRIVATE);
                            // Create a new user with a first and last name
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email.getEditText().getText().toString());
                            user.put("name", name.getEditText().getText().toString());
                            user.put("password", password.getEditText().getText().toString());
                            user.put("gender", gender.getText());
                            user.put("latitude", preferences.getString("latitude","0"));
                            user.put("longitude", preferences.getString("longitude","0"));
                            user.put("accountType", accountType.getText().toString().equals("Patient") ? 2 : 1);
                            if(accountType.getText().toString().equals("Patient"))
                                user.put("status", "Healthy");

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            // Add a new document with a generated ID
                            db.collection("users").document(currentUser.getUid())
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            progressBar_layout.setVisibility(View.GONE);
                                            form.setVisibility(View.VISIBLE);

                                            Log.i("TAG", "onComplete: " + task.isSuccessful());
                                            if (task.isSuccessful()){
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putBoolean("logged", true);
                                                editor.putString("accountType", accountType.getText().toString().equals("Patient") ? "2" : "1");
                                                editor.apply();
                                                if(accountType.getText().toString().equals("Patient")) {
                                                    Intent goToMain = new Intent(RegisterActivity.this, MainHomePatientsActivity.class);
                                                    startActivity(goToMain);
                                                    finish();
                                                }
                                                else{
                                                    Intent goToMain = new Intent(RegisterActivity.this, MainHomeActivity.class);
                                                    startActivity(goToMain);
                                                    finish();
                                                }
                                            }
                                            else{
                                                Log.i("TAG", "onComplete: fail  " + task.getResult());
                                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            progressBar.setVisibility(View.GONE);
                            progressBar_layout.setVisibility(View.GONE);
                            form.setVisibility(View.VISIBLE);
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void initViews() {
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        progressBar_layout = findViewById(R.id.progressBar_layout);
        form = findViewById(R.id.form);
        go_to_login = findViewById(R.id.go_to_login);
    }
}