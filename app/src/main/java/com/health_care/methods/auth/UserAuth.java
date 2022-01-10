package com.health_care.methods.auth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.health_care.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class UserAuth {

    static String TAG = "test log ";
    Activity activity;

    public UserAuth(Activity activity){
        this.activity = activity;
    }

    public void signUp(FirebaseFirestore db, UserInfo userInfo) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(userInfo.getEmail(), "password")
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Create a new user with a first and last name
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("full_name", userInfo.getName());
                            userMap.put("email", userInfo.getEmail());
                            userMap.put("gender", userInfo.getGender());
                            userMap.put("password", userInfo.getPassword());
                            userMap.put("account_type", userInfo.getAccountType());


                            // Add a new document with a generated ID
                            db.collection("users").document(user.getUid())
                                    .set(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i(TAG, "onComplete: Successful   " + task.getResult());
                                                //TODO
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("id", user.getUid());
                                                editor.apply();
                                            } else
                                                Log.i(TAG, "onComplete: " + task.getException().getMessage());
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void getUserInfo(FirebaseFirestore db, String id){
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
