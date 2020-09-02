package com.example.quizmania_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.jintin.mixadapter.MixAdapter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Questions extends AppCompatActivity {
    private Button single_add, excel_add;
    private RecyclerView recyclerView;
    private QuestionsAdapter adapter;
    private QuestionBooleanAdapter adapter2;
    public static List<QuestionModel> list;
    public static List<QuestionModel> list2;
    private Dialog loadingDialog;
    private DatabaseReference myRef;
    private String setId;
    private String categoryName;
    public static final int CELL_COUNT = 6;
    private TextView loadingText;
    private String DirectoryName;
    private int p = 0;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        DirectoryName = Categories.DirectoryName;
        categoryName = getIntent().getStringExtra("category");
        setId = getIntent().getStringExtra("setId");
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = FirebaseDatabase.getInstance().getReference();

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_edit));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        loadingText = loadingDialog.findViewById(R.id.loading_text);


        single_add = findViewById(R.id.single_add_btn);
        excel_add = findViewById(R.id.excel_add_btn);
        recyclerView = findViewById(R.id.ques_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        adapter = new QuestionsAdapter(list, categoryName, new QuestionsAdapter.DeleteListener() {
            @Override
            public void onLongClick(final int position, final String id) {
                new AlertDialog.Builder(Questions.this, R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Question")
                        .setMessage("Are you sure you want to delete this question ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                myRef.child("Sets").child(setId).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            list.remove(position);
                                            adapter.notifyItemRemoved(position);
                                        } else {
                                            Toast.makeText(Questions.this, "Failed to delete!!", Toast.LENGTH_SHORT).show();
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
        //recyclerView.setAdapter(adapter);

        list2 = new ArrayList<>();
        adapter2 = new QuestionBooleanAdapter(list2, categoryName, new QuestionBooleanAdapter.DeleteListener() {
            @Override
            public void onLongClick(final int position, final String id) {
                new AlertDialog.Builder(Questions.this, R.style.Theme_AppCompat_Light_Dialog).setTitle("Delete Question")
                        .setMessage("Are you sure you want to delete this question ?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                myRef.child("Sets").child(setId).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            list2.remove(position);
                                            adapter2.notifyItemRemoved(position);
                                        } else {
                                            Toast.makeText(Questions.this, "Failed to delete!!", Toast.LENGTH_SHORT).show();
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
        //recyclerView.setAdapter(adapter2);

        Switch sw = findViewById(R.id.boolean_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    single_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent i = new Intent(Questions.this, Add_boolean_question.class);
                            i.putExtra("categoryName", categoryName);
                            i.putExtra("setId", setId);
                            startActivity(i);
                        }
                    });
                }
                else{

                    single_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent i = new Intent(Questions.this, AddQuestion.class);
                            i.putExtra("categoryName", categoryName);
                            i.putExtra("setId", setId);
                            startActivity(i);
                        }
                    });
                }
            }
        });

        getData(categoryName, setId);
        //booleandata(categoryName,setId);

        single_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(Questions.this, AddQuestion.class);
                i.putExtra("categoryName", categoryName);
                i.putExtra("setId", setId);
                startActivity(i);
            }
        });

        excel_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (ActivityCompat.checkSelfPermission(Questions.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectFile();
                } else {
                    ActivityCompat.requestPermissions(Questions.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                }
            }
        });

        MixAdapter<RecyclerView.ViewHolder> Mixadapter = new MixAdapter<>();
        Mixadapter.addAdapter(adapter);
        Mixadapter.addAdapter(adapter2);
        recyclerView.setAdapter(Mixadapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectFile();
            } else {
                Toast.makeText(this, "Please Grant Permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectFile() {
        String[] mimetype = {"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType(mimetype[1]);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Select File"), 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PackageManager packageManager = getPackageManager();



        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {

                List activitiesXls = packageManager.queryIntentActivities(data,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafeXls = activitiesXls.size() > 0;
//                String a = data.getData().toString();
//                System.out.println(a);
//                String filepath = data.getData().getPath();
//                System.out.println(filepath);
                //String ext = filepath.substring(filepath.lastIndexOf("."));

                if (!isIntentSafeXls){
                    Toast.makeText(this, "Please choose an Excel file only. ", Toast.LENGTH_SHORT).show();
                }else {
                    readFile(data.getData());
                }
                //String ext = getExt(filepath); //if (filepath.endsWith(".xlsx"))  if (ext.equals("xlsx"))
//                if (filepath.endsWith(".xlsx")){
//                    //readFile(data.getData());
//                    Toast.makeText(this, "File Selected. ", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(this, "Please choose an Excel file only. ", Toast.LENGTH_SHORT).show();
//                }
            }
        }
    }

    private String getExt(String filepath){
        int strlength = filepath.lastIndexOf(".");
        if (strlength > 0){
            filepath.substring(strlength+1).toLowerCase();
            System.out.println(filepath);
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(final String categoryName, final String setId) {
        loadingDialog.show();
        myRef.child("Sets").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.child("optionC").exists() && dataSnapshot1.child("optionD").exists()){
                            if (dataSnapshot1.child("optionC").getValue().toString() != "" && dataSnapshot1.child("optionD").getValue().toString() != ""){
                                String id = dataSnapshot1.getKey();
                                String question = dataSnapshot1.child("question").getValue().toString();
                                String a = dataSnapshot1.child("optionA").getValue().toString();
                                String b = dataSnapshot1.child("optionB").getValue().toString();
                                String c = dataSnapshot1.child("optionC").getValue().toString();
                                String d = dataSnapshot1.child("optionD").getValue().toString();
                                String correctAnswer = dataSnapshot1.child("correctAnswer").getValue().toString();
                                list.add(new QuestionModel(id,question,a,b,c,d,correctAnswer,setId));
                            }
                        }else{
                            //booleandata(categoryName,setId);
                            String id = dataSnapshot1.getKey();
                            String question = dataSnapshot1.child("question").getValue().toString();
                            String a = dataSnapshot1.child("optionA").getValue().toString();
                            String b = dataSnapshot1.child("optionB").getValue().toString();
                            String correctAnswer = dataSnapshot1.child("correctAnswer").getValue().toString();
                            list2.add(new QuestionModel(id,question,a,b,correctAnswer,setId));
                        }
                    }
                }
                loadingDialog.dismiss();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Questions.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });

    }

    private void booleandata(final String categoryName, final String setId){
        loadingDialog.show();
        myRef.child("Sets").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String newcate = dataSnapshot1.child("correctAnswer").getValue().toString();
                        System.out.println(newcate);
                        if (dataSnapshot1.child("optionC").exists() || dataSnapshot1.child("optionD").exists()){
                            if (dataSnapshot1.child("optionC").getValue().toString() != "" && dataSnapshot1.child("optionD").getValue().toString() != ""){
                                //break;
                            }
                        }else{
                            String id = dataSnapshot1.getKey();
                            String question = dataSnapshot1.child("question").getValue().toString();
                            String a = dataSnapshot1.child("optionA").getValue().toString();
                            String b = dataSnapshot1.child("optionB").getValue().toString();
                            String correctAnswer = dataSnapshot1.child("correctAnswer").getValue().toString();
                            list2.add(new QuestionModel(id, question, a, b, correctAnswer, setId));
                        }
                    }
                }

                loadingDialog.dismiss();
                adapter2.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Questions.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });
    }

    private void readFile(final Uri fileuri) {

        loadingText.setText("Scanning Questions...");
        loadingDialog.show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                final HashMap<String, Object> parentMap = new HashMap<>();
                final List<QuestionModel> tempList = new ArrayList<>();

                final HashMap<String, Object> parentMap2 = new HashMap<>();
                final List<QuestionModel> tempList2 = new ArrayList<>();

                try {
                    InputStream inputStream = getContentResolver().openInputStream(fileuri);
                    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

                    int rowCount = sheet.getPhysicalNumberOfRows();

                    if (rowCount > 0) {

                        for (int r = 0; r < rowCount; r++) {
                            Row row = sheet.getRow(r);

                            if (row.getPhysicalNumberOfCells() == 4){

                                String question = getCellData(row, 0, formulaEvaluator);
                                String a = getCellData(row, 1, formulaEvaluator);
                                String b = getCellData(row, 2, formulaEvaluator);
                                String correctAns = getCellData(row, 3, formulaEvaluator);

                                if (correctAns.equals(a) || correctAns.equals(b)){
                                    HashMap<String, Object> questionMap = new HashMap<>();
                                    questionMap.put("question", question);
                                    questionMap.put("optionA", a);
                                    questionMap.put("optionB", b);
                                    questionMap.put("correctAnswer", correctAns);
                                    questionMap.put("setId", setId);

                                    String id = UUID.randomUUID().toString();

                                    parentMap2.put(id, questionMap);

                                    tempList2.add(new QuestionModel(id, question, a, b, correctAns, setId));

                                }else {

                                    final int finalR1 = r;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingText.setText("Loading...");
                                            loadingDialog.dismiss();
                                            Toast.makeText(Questions.this, "Row no. " + (finalR1 +1) + " has no correct option. ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                            }

                            else if (row.getPhysicalNumberOfCells() == CELL_COUNT) {

                                String question = getCellData(row, 0, formulaEvaluator);
                                String a = getCellData(row, 1, formulaEvaluator);
                                String b = getCellData(row, 2, formulaEvaluator);
                                String c = getCellData(row, 3, formulaEvaluator);
                                String d = getCellData(row, 4, formulaEvaluator);
                                String correctAns = getCellData(row, 5, formulaEvaluator);

                                if (correctAns.equals(a) || correctAns.equals(b) || correctAns.equals(c) || correctAns.equals(d)) {

                                    HashMap<String, Object> questionMap = new HashMap<>();
                                    questionMap.put("question", question);
                                    questionMap.put("optionA", a);
                                    questionMap.put("optionB", b);
                                    questionMap.put("optionC", c);
                                    questionMap.put("optionD", d);
                                    questionMap.put("correctAnswer", correctAns);
                                    questionMap.put("setId", setId);

                                    String id = UUID.randomUUID().toString();

                                    parentMap.put(id, questionMap);

                                    tempList.add(new QuestionModel(id, question, a, b, c, d, correctAns, setId));


                                } else {

                                    final int finalR1 = r;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingText.setText("Loading...");
                                            loadingDialog.dismiss();
                                            Toast.makeText(Questions.this, "Row no. " + (finalR1 +1) + " has no correct option. ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }

                            }
                            else {
                                final int finalR = r;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingText.setText("Loading...");
                                        loadingDialog.dismiss();
                                        Toast.makeText(Questions.this, "Row no. " + (finalR +1) + " has incorrect data. ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingText.setText("Uploading...");

                                FirebaseDatabase.getInstance().getReference()
                                        .child("Sets").child(setId).updateChildren(parentMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            list2.addAll(tempList2);
                                            adapter2.notifyDataSetChanged();
                                        } else {
                                            loadingText.setText("Loading...");
                                            Toast.makeText(Questions.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });

                                FirebaseDatabase.getInstance().getReference()
                                        .child("Sets").child(setId).updateChildren(parentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            list.addAll(tempList);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            loadingText.setText("Loading...");
                                            Toast.makeText(Questions.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        });

                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingText.setText("Loading...");
                                loadingDialog.dismiss();
                                Toast.makeText(Questions.this, "File is Empty ", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                } catch (final FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingText.setText("Loading...");
                            loadingDialog.dismiss();
                            Toast.makeText(Questions.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingText.setText("Loading...");
                            loadingDialog.dismiss();
                            Toast.makeText(Questions.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    private String getCellData(Row row, int cellPosition, FormulaEvaluator formulaEvaluator) {

        String value = "";

        Cell cell = row.getCell(cellPosition);

        switch (cell.getCellType()) {

            case Cell.CELL_TYPE_BOOLEAN:
                return value + cell.getBooleanCellValue();

            case Cell.CELL_TYPE_NUMERIC:
                return value + cell.getNumericCellValue();

            case Cell.CELL_TYPE_STRING:
                return value + cell.getStringCellValue();

            default:
                return value;
        }
    }
}
