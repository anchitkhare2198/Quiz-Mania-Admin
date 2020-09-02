package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Chapters extends AppCompatActivity {

    private GridView gridView;
    private Dialog loadingDialog;
    private GridAdapter adapter;
    private String categoryName;
    private List<String> sets;
    private String DirectoryName;
    private DatabaseReference myRef;
    int p = 101;
    int firstThree = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        DirectoryName = Categories.DirectoryName;
        //Toolbar toolbar = findViewById(R.id.toolbar);

        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        categoryName = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(categoryName);

        gridView = findViewById(R.id.gridview);

        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Subjects").child(DirectoryName).child(getIntent().getStringExtra("key")).child("sets").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        String newcate = dataSnapshot1.getKey();
                        //System.out.println(newcate);
                        String a = "";
                        if (newcate.length()>3){
                            a = newcate.substring(0,3);
                            forid(a);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chapters.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });

        //System.out.println(firstThree);

        sets = Categories.list.get(getIntent().getIntExtra("position", 0)).getSets();
        adapter = new GridAdapter(sets, getIntent().getStringExtra("title"), new GridAdapter.GridListener() {


            @Override
            public void addSet() {
                firstThree++;
                String b = String.valueOf(firstThree);
                final String id = b + UUID.randomUUID().toString();
                loadingDialog.show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();

//                database.getReference().child("Subjects").child(DirectoryName).child(getIntent().getStringExtra("key")).child("sets").child(id).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()){
//                            String newcate = dataSnapshot.getKey();
//                            System.out.println(newcate);
//                            String a = "";
//                            if (newcate.length()>3){
//                                a = newcate.substring(0,3);
//                                forid(a);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(Chapters.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
//                    }
//                });

                database.getReference().child("Subjects").child(DirectoryName).child(getIntent().getStringExtra("key")).child("sets").child(id).setValue("Set Id").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            sets.add(id);
                            firstThree++;
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(Chapters.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
            }

            @Override
            public void onLongClick(final String setId , int position) {

                new AlertDialog.Builder(Chapters.this, R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Set " +position)
                        .setMessage("Are you sure you want to delete this set ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();

                                myRef.child("Sets").child(setId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            myRef.child("Subjects").child(DirectoryName).child(Categories.list.get(getIntent().getIntExtra("position", 0)).getKey())
                                                    .child("sets").child(setId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        sets.remove(setId);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                    else{
                                                        Toast.makeText(Chapters.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();

                                                    }
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                        }
                                        else{
                                            Toast.makeText(Chapters.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismiss();
                                        }

                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
        gridView.setAdapter(adapter);
    }

    private void forid(String first){
        firstThree = Integer.parseInt(first);
        //System.out.println("First three "+firstThree);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
