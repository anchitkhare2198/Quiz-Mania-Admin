package com.example.quizmania_admin;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResumeAdapter extends RecyclerView.Adapter<ResumeAdapter.viewHolder> {

    List<ResumeModel> resumeModelList;
    private long mLastClickTime = 0;
    public static String newpos;
    String newname;

    public ResumeAdapter(List<ResumeModel> resumeModelList) {
        this.resumeModelList = resumeModelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resume_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.Name.setText(resumeModelList.get(position).getResume());
        holder.newpos1 = resumeModelList.get(position).getFullName();
        holder.newname2 = resumeModelList.get(position).getResume();
    }

    @Override
    public int getItemCount() {
        return resumeModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView Name;
        String newpos1,newname2;
        public viewHolder(@NonNull final View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.Resume_Name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(itemView.getContext(),PdfView.class);
                    i.putExtra("Url",newpos1);
                    i.putExtra("Name",newname2);
                    itemView.getContext().startActivity(i);

//                    Intent i = new Intent();
//                    i.setType(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(newpos));
//                    itemView.getContext().startActivity(i);

//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse(newpos), "application/pdf");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    Intent newIntent = Intent.createChooser(intent, "Open File");
//                    try {
//                        itemView.getContext().startActivity(newIntent);
//                    } catch (ActivityNotFoundException e) {
//                        // Instruct the user to install a PDF reader here, or something
//                    }

//                    String url = newpos;
//                    Uri webpage = Uri.parse(url);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//                    itemView.getContext().startActivity(intent);

                }
            });
        }
    }
}
