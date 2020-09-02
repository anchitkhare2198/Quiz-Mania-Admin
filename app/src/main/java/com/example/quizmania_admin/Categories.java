package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.ULocale;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Categories extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private Dialog loadingDialog, category;

    private CircleImageView add_Image;
    private EditText categoryName;
    private Button add_btn;

    private RecyclerView recyclerView;
    public static List<CategoryModel> list;
    private CategoryAdapter adapter;
    private Toolbar toolbar;
    private Uri image;
    private String downloadUrl;
    private FirebaseAuth firebaseAuth;
    private TextView loadingText;
    private List<String> subject_sets;
    public static String DirectoryName;
    //private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        firebaseAuth = FirebaseAuth.getInstance();

        DirectoryName = getIntent().getStringExtra("title");


        //subject_sets = Main_Page.list.get(getIntent().getIntExtra("position", 0)).getSets();
        //progressDialog = new ProgressDialog(this);




        //androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        //Toolbar toolbar = findViewById(R.id.toolbar);
//        Toolbar toolbar = findViewById(R.id.toolbar);

        //setSupportActionBar(toolbar);
        //setSupportActionBar(toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(DirectoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);

        setCategoryDialog();

        recyclerView = findViewById(R.id.rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        //subject_name = Dir_Display.list.get(getIntent().getIntExtra("position",0)).getSets();



        adapter = new CategoryAdapter(list, new CategoryAdapter.DeleteListener() {
            @Override
            public void onDelete(final String key, final int position) {

                new AlertDialog.Builder(Categories.this,R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Subject")
                        .setMessage("Are you sure you want to delete this subject ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                myRef.child("Subjects").child(DirectoryName).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            for (String setIds : list.get(position).getSets()){
                                                myRef.child("Sets").child(setIds).removeValue();
                                            }
                                            list.remove(position);

                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                            storageReference.child("Subjects").child(DirectoryName).child(categoryName.getText().toString()+".jpeg").delete();

                                            Collections.sort(list, new MyComparator());
                                            adapter.notifyDataSetChanged();
                                            loadingDialog.dismiss();

                                        }
                                        else{

                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }

//            @Override
//            public void SubjectSet() {
//                final String id = UUID.randomUUID().toString();
//                loadingDialog.show();
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                database.getReference().child("Subject_Dir").child(getIntent().getStringExtra("key")).child("sets").child(id).setValue("Subject Id").addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            subject_sets.add(id);
//                        }else{
//                            Toast.makeText(Categories.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
//                        }
//                        loadingDialog.dismiss();
//                    }
//                });
//            }
        });
        recyclerView.setAdapter(adapter);

        loadingDialog.show();
        myRef.child("Subjects").child(DirectoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    List<String> sets = new ArrayList<>();
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("sets").getChildren()){
                        sets.add(dataSnapshot2.getKey());
                    }

                    list.add(new CategoryModel(dataSnapshot1.child("name").getValue().toString(),
                            sets,
                            dataSnapshot1.child("url").getValue().toString(),
                            dataSnapshot1.getKey()));

                    Collections.sort(list, new MyComparator());
                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Categories.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();

            }
        });



    }



    private void setSupportActionBar(Toolbar toolbar) {
    }

//    private void setSupportActionBar(Toolbar toolbar) {
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu3,menu);

        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addmenu) {
            category.show();
        }

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setCategoryDialog(){

        category = new Dialog(this);
        category.setContentView(R.layout.add_category_dialog);
        category.getWindow().setBackgroundDrawable(getDrawable(R.drawable.edit_text_border));
        category.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        category.setCancelable(true);

        add_Image = category.findViewById(R.id.capture);
        categoryName = category.findViewById(R.id.subjectname);
        add_btn = category.findViewById(R.id.addbutton);



        add_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,101);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryName.getText().toString().isEmpty() || categoryName.getText() == null)
                {
                    categoryName.setError("Required");
                    return;
                }
                for (CategoryModel model : list){
                    if(categoryName.getText().toString().equals(model.getName())){
                        categoryName.setError("Subject already Exists");
                        return;
                    }
                }
                if(image == null)
                {
                    Toast.makeText(Categories.this,"Please Select your Image",Toast.LENGTH_SHORT).show();
                    return;
                }
                //upload data
                category.dismiss();
                uploadData();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101)
        {
            if(resultCode == RESULT_OK)
            {
                image = data.getData();
                add_Image.setImageURI(image);
            }
        }
    }

    private void uploadData(){
        loadingDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        //final StorageReference imageReference = storageReference.child("Subjects").child(image.getLastPathSegment());
        final StorageReference imageReference = storageReference.child("Subjects").child(DirectoryName).child(categoryName.getText().toString()+".jpeg");


        UploadTask uploadTask = imageReference.putFile(image);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUrl =  task.getResult().toString();
                            uploadCategoryName();
                        }
                        else{
                            loadingDialog.dismiss();
                            Toast.makeText(Categories.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(Categories.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadCategoryName(){

        final String id = UUID.randomUUID().toString();

        Map<String,Object> map = new HashMap<>();
        map.put("name",categoryName.getText().toString());
        map.put("sets",0);
        map.put("url",downloadUrl);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("Subjects").child(DirectoryName).child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    list.add(new CategoryModel(categoryName.getText().toString(),new ArrayList<String>(),downloadUrl,id));
                    adapter.notifyDataSetChanged();

                }
                else{
                    Toast.makeText(Categories.this,"Something went wrong!!",Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }

    public class MyComparator implements Comparator<CategoryModel> {
        @Override
        public int compare(CategoryModel p1, CategoryModel p2) {
            return p1.getName().compareTo(p2.getName());
        }
    }

}
