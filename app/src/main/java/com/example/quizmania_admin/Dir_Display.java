package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Dir_Display extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference myRef;

    public static List<DirectoryModel> list;
    private List<CategoryModel> list2;
    private RecyclerView recyclerView;
    private DirectoryAdapter adapter;
    private Dialog loadingDialog, category;
    private TextView loadingText, directoryName;
    Button add;
    String DirectoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir__display);

        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setTitle("Directories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list2 = Categories.list;
        //DirectoryName = getIntent().getStringExtra("DirectoryName");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        setDirectoryDialog();


        recyclerView = findViewById(R.id.rv_dir_display);
        //recyclerView.setMotionEventSplittingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        adapter = new DirectoryAdapter(list, new DirectoryAdapter.DeleteListener() {
            @Override
            public void onDelete(final String key, final int position) {
                new AlertDialog.Builder(Dir_Display.this,R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Directory")
                        .setMessage("Are you sure you want to delete this directory ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();

                                myRef.child("Subject_Dir").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            String newdir = list.get(position).getDir_Name();
                                            list.remove(position);
                                            System.out.println(newdir);
                                            myRef.child("Subjects").child(newdir).removeValue();
                                            Toast.makeText(Dir_Display.this,"Successfully deleted directory!!",Toast.LENGTH_SHORT).show();
                                        }
                                        adapter.notifyDataSetChanged();
                                        loadingDialog.dismiss();
                                    }
                                });


                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        loadingDialog.show();
        myRef.child("Subject_Dir").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        loadingDialog.show();
                        List<String> sets = new ArrayList<>();
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("sets").getChildren()){
                            sets.add(dataSnapshot2.getKey());
                        }

                        list.add(new DirectoryModel(dataSnapshot1.child("Dir_Name").getValue().toString(),
                                sets,dataSnapshot1.getKey()));
                        Collections.sort(list, new Dir_Display.MyComparator());
                    }
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                }else{
                    Toast.makeText(Dir_Display.this,"Add a Directory",Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Dir_Display.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.folder_add) {
            category.show();
        }

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDirectoryDialog(){

        category = new Dialog(this);
        category.setContentView(R.layout.add_directory_dialog);
        category.getWindow().setBackgroundDrawable(getDrawable(R.drawable.edit_text_border));
        category.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        category.setCancelable(true);

        directoryName = category.findViewById(R.id.Directory_Name);
        add = category.findViewById(R.id.Add_Dir_button);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(directoryName.getText().toString().isEmpty() || directoryName.getText() == null)
                {
                    directoryName.setError("Required");
                    return;
                }
                for (DirectoryModel model : list){
                    if(directoryName.getText().toString().equals(model.getDir_Name())){
                        directoryName.setError("Directory already Exists");
                        return;
                    }
                }
                //upload data
                category.dismiss();
                uploadDirectory();

            }
        });
    }

    private void uploadDirectory(){
        final String id = UUID.randomUUID().toString();

        myRef = FirebaseDatabase.getInstance().getReference();

        Map<String,Object> map = new HashMap<>();
        map.put("Dir_Name",directoryName.getText().toString());
        map.put("sets",0);

        myRef.child("Subject_Dir").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    list.add(new DirectoryModel(directoryName.getText().toString(),new ArrayList<String>(),id));
                    adapter.notifyDataSetChanged();
                    Collections.sort(list, new Dir_Display.MyComparator());
                }else{
                    Toast.makeText(Dir_Display.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class MyComparator implements Comparator<DirectoryModel> {
        @Override
        public int compare(DirectoryModel p1, DirectoryModel p2) {
            return p1.getDir_Name().compareTo(p2.getDir_Name());
        }
    }

}
