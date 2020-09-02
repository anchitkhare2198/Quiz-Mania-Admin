package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class VideoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Dialog loadingDialog;
    RecyclerView.LayoutManager layoutManager;
    androidx.appcompat.widget.SearchView searchView;
    DatabaseReference myRef;
    private List<VideoModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        getSupportActionBar().setTitle("All Videos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = FirebaseDatabase.getInstance().getReference("Videos");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        recyclerView = findViewById(R.id.rv_video);
        searchView = findViewById(R.id.search_video);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadingDialog.show();

        list = new ArrayList<>();
        final VideoAdapter adapter = new VideoAdapter(list);
        recyclerView.setAdapter(adapter);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        list.add(new VideoModel(dataSnapshot1.child("fullName").getValue().toString(),
                                dataSnapshot1.child("videoName").getValue().toString(),
                                dataSnapshot1.child("videoUrl").getValue().toString()));

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
                Toast.makeText(VideoActivity.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
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
        ArrayList<VideoModel> list2 = new ArrayList<>();
        for (VideoModel object : list){
            if (object.getFullName().toLowerCase().contains(s.toLowerCase())){
                list2.add(object);
            }
        }
        VideoAdapter adapter2 = new VideoAdapter(list2);
        recyclerView.setAdapter(adapter2);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyComparator implements Comparator<VideoModel> {
        @Override
        public int compare(VideoModel p1, VideoModel p2) {
            return p1.getFullName().compareTo(p2.getFullName());
        }
    }
}
