package com.healthcare.methods.auth;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.healthcare.MainActivity;
import com.healthcare.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class UserAuth {

    static String TAG = "test log ";

    public static void signUp(FirebaseFirestore db, UserInfo userInfo ) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        mAuth.createUserWithEmailAndPassword("email11@email.com", "password")
//                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(activity, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });


        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("full_name", userInfo.getName());
        user.put("email", userInfo.getEmail());
        user.put("gender", userInfo.getGender());
        user.put("password", userInfo.getPassword());
        user.put("account_type", userInfo.getAccountType());


        // Add a new document with a generated ID
        db.collection("users").document()
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Successful");
                            //TODO
                        } else
                            Log.i(TAG, "onComplete: " + task.getException().getMessage());
                    }
                });


    }
}
