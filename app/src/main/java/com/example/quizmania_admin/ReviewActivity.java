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

public class ReviewActivity extends AppCompatActivity {

    SearchView searchView;
    private DatabaseReference myRef;
    RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Dialog loadingDialog;

    private List<ReviewModel> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        getSupportActionBar().setTitle("All Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = FirebaseDatabase.getInstance().getReference("User_Reviews");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        searchView = findViewById(R.id.search_review);
        recyclerView = findViewById(R.id.rv_review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadingDialog.show();

        list = new ArrayList<>();
        final ReviewAdapter adapter = new ReviewAdapter(list);
        recyclerView.setAdapter(adapter);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        list.add(new ReviewModel(dataSnapshot1.child("fullName").getValue().toString(),
                                dataSnapshot1.child("review").getValue().toString()));

                        Collections.sort(list, new MyComparator());
                    }
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismiss();
                Toast.makeText(ReviewActivity.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
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
        ArrayList<ReviewModel> list2 = new ArrayList<>();
        for (ReviewModel object : list){
            if (object.getFullName().toLowerCase().contains(s.toLowerCase())){
                list2.add(object);
            }
        }
        ReviewAdapter adapter2 = new ReviewAdapter(list2);
        recyclerView.setAdapter(adapter2);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyComparator implements Comparator<ReviewModel> {
        @Override
        public int compare(ReviewModel p1, ReviewModel p2) {
            return p1.getFullName().compareTo(p2.getFullName());
        }
    }
}
