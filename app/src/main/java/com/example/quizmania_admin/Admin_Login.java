package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Admin_Login extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText email,password;
    private Button login;
    private TextView forgotpassword;
    private Dialog loadingDialog;
    private TextView loadingText;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__login);

        getSupportActionBar().setTitle("Admin Login");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        forgotpassword = findViewById(R.id.forgotpassword);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        firebaseAuth = FirebaseAuth.getInstance();

        final Intent i =new Intent(this,Main_Page.class);

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Admin_Login.this,ForgotPassword.class);
                startActivity(i);
            }
        });

        if(firebaseAuth.getCurrentUser()!=null)
        {
            //Category Intent
            startActivity(i);
            finish();
        }



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(email.getText().toString().isEmpty()){
                    email.setError("Required");
                    return;
                }
                else
                {
                    email.setError(null);
                }
                if(password.getText().toString().isEmpty())
                {
                    password.setError("Required");
                    return;
                }
                else{
                    password.setError(null);
                }

                loadingText.setText("Signing In");
                loadingDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            loadingDialog.dismiss();
                            checkEmailVerification();
                        }
                        else{
                            loadingDialog.dismiss();
                            Toast.makeText(Admin_Login.this,"Incorrect Email or Password",Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }

                });

            }
        });
    }

    private void checkEmailVerification()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag)
        {
            finish();
            Intent i = new Intent(this, Main_Page.class);
            startActivity(i);
            finish();
        }

        else
        {
            Toast.makeText(this, " Verify Your Email ", Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }
    }
}
