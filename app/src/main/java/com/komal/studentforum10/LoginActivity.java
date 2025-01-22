package com.komal.studentforum10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText login_email;
    private EditText login_password;
    private Button login_btn;
    private Button login_signup_btn;
    private ProgressBar login_progress;
    private Button forgotPasswordBtn;
    private Button guestLoginBtn;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth.AuthStateListener authListener;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        FirebaseApp.initializeApp(this);

        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        login_btn = (Button) findViewById(R.id.login_btn);
        login_signup_btn = (Button) findViewById(R.id.login_signup_btn);
        login_progress = (ProgressBar) findViewById(R.id.login_progress);
        forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn);
        guestLoginBtn = (Button) findViewById(R.id.guestLoginBtn);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        guestLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login_progress.setVisibility(View.VISIBLE);

                Task<AuthResult> resultTask = mAuth.signInAnonymously();

                resultTask.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        login_progress.setVisibility(View.INVISIBLE);
                        Intent mainIntent = new Intent(LoginActivity.this, StudentForum.class);
                        startActivity(mainIntent);
                        finish();

                    }
                });
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String loginEmail = login_email.getText().toString();
                String loginPassword = login_password.getText().toString();

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {

                    login_progress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                String token_id = FirebaseInstanceId.getInstance().getToken();
                                String current_user_id = mAuth.getCurrentUser().getUid();

                                Map<String, Object> tokenMap = new HashMap<>();
                                tokenMap.put("token_id", token_id);

                                firebaseFirestore.collection("Users").document(current_user_id)
                                        .update(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            Intent mainIntent = new Intent(LoginActivity.this, StudentForum.class);
                                            startActivity(mainIntent);
                                            finish();

                                        } else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();

                            }

                            login_progress.setVisibility(View.INVISIBLE);

                        }
                    });

                }

            }
        });


        login_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(myIntent);

            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
                finish();

            }
        });


    }

}

