package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main_Page extends AppCompatActivity {

    private long mLastClickTime = 0;

    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;

    private Button  leaderboard, about_us, review_button, show_all , faq ,resume_button, videos_button;
    private Dialog category;
    private EditText directoryName;
    public static List<DirectoryModel> list;
    private Dialog loadingDialog;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__page);

        getSupportActionBar().setTitle("Admin Portal");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        firebaseAuth = FirebaseAuth.getInstance();

        //add_dir = findViewById(R.id.add_directory);
        resume_button = findViewById(R.id.Resume_Button);
        show_all = findViewById(R.id.Show_directory);
        leaderboard = findViewById(R.id.LeaderBoard_button);
        about_us = findViewById(R.id.About_Us);
        review_button = findViewById(R.id.Review_Button);
        videos_button = findViewById(R.id.Videos_Button);
        faq = findViewById(R.id.FAQs);

        show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Main_Page.this,Dir_Display.class);
                startActivity(i);
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Main_Page.this,LeaderBoard.class);
                startActivity(i);
            }
        });

        videos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Main_Page.this,VideoActivity.class);
                startActivity(i);
            }
        });

        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Main_Page.this,AboutUs.class);
                startActivity(i);
            }
        });

        resume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Main_Page.this,Resume.class);
                startActivity(i);
            }
        });

        review_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Main_Page.this,ReviewActivity.class);
                startActivity(i);
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Main_Page.this,FAQs.class);
                startActivity(i);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.Add_Admin){
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return super.onOptionsItemSelected(item);
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(Main_Page.this, AdminAdd.class);
            startActivity(i);



        }

        if (item.getItemId() == R.id.LogoutMenu){

            new AlertDialog.Builder(Main_Page.this, R.style.Theme_AppCompat_Light_Dialog).setTitle("Logout")
                    .setMessage("Are you sure you want to Logout from this session ?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingText.setText("Signing Out");
                            loadingDialog.show();
                            firebaseAuth.signOut();
                            Intent i = new Intent(Main_Page.this, Admin_Login.class);
                            loadingDialog.dismiss();
                            startActivity(i);
                            //loadingDialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}
