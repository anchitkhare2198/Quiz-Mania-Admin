package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Resume extends AppCompatActivity {

    androidx.appcompat.widget.SearchView searchView;
    private RecyclerView recyclerView;
    private Dialog loadingDialog;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference myRef;
    private List<ResumeModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        getSupportActionBar().setTitle("All Resumes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = FirebaseDatabase.getInstance().getReference("Resumes");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        searchView = findViewById(R.id.search_resume);

        recyclerView = findViewById(R.id.rv_resume);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadingDialog.show();

        list = new ArrayList<>();
        final ResumeAdapter adapter = new ResumeAdapter(list);
        recyclerView.setAdapter(adapter);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        list.add(new ResumeModel(dataSnapshot1.child("fullName").getValue().toString(),
                                dataSnapshot1.child("resume").getValue().toString()));

                        Collections.sort(list, new MyComparator());
                    }
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                }else{
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
                Toast.makeText(Resume.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
            }
        });

        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
    }

    private void search(String s){
        ArrayList<ResumeModel> list2 = new ArrayList<>();
        for (ResumeModel object : list){
            if (object.getResume().toLowerCase().contains(s.toLowerCase())){
                list2.add(object);
            }
        }
        ResumeAdapter adapter2 = new ResumeAdapter(list2);
        recyclerView.setAdapter(adapter2);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyComparator implements Comparator<ResumeModel> {
        @Override
        public int compare(ResumeModel p1, ResumeModel p2) {
            return p1.getResume().compareTo(p2.getResume());
        }
    }
}
