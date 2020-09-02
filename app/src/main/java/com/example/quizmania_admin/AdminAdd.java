package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAdd extends AppCompatActivity {

    private TextInputEditText email, password;
    private Button add_admin;
    private Dialog loadingDialog;
    private TextView loadingText;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add);

        firebaseAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Add Admin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.admin_email);
        password = findViewById(R.id.admin_password);
        add_admin = findViewById(R.id.admin_add);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        add_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AdminAdd.this, R.style.Theme_AppCompat_Light_Dialog).setTitle("Add Admin")
                        .setMessage("Adding Admin will log you out from the current session.\n Are you sure ?")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (email.getText().toString().isEmpty()){
                                    email.setError("Required");
                                    return;
                                }
                                else{
                                    email.setError(null);
                                }
                                if (password.getText().toString().isEmpty()){
                                    password.setError("Required");
                                    return;
                                }
                                else{
                                    password.setError(null);
                                }

                                loadingText.setText("Adding Admin..");
                                loadingDialog.show();

                                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            loadingDialog.dismiss();
                                            //Toast.makeText(AdminAdd.this,"Admin added successfully!!",Toast.LENGTH_SHORT).show();
                                            sendEmailVerification();
                                        }else{
                                            loadingDialog.dismiss();
                                            Toast.makeText(AdminAdd.this,"Failed to add admin!!",Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendEmailVerification() {
        final FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(AdminAdd.this, " Admin Added, Verification Email sent ", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        loadingDialog.dismiss();
                        Intent i = new Intent(AdminAdd.this, Admin_Login.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(AdminAdd.this, "Failed to send the verification email", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}
