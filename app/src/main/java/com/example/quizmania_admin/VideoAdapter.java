package com.example.quizmania_admin;

import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.viewHolder> {

    List<VideoModel> videoModelList;
    private long mLastClickTime = 0;
    String newpos;
    String newname;

    public VideoAdapter(List<VideoModel> videoModelList) {
        this.videoModelList = videoModelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resume_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.name.setText(videoModelList.get(position).getFullName());
        holder.newpos1 = videoModelList.get(position).getVideoUrl();
        holder.newname2 = videoModelList.get(position).getVideoName();
    }

    @Override
    public int getItemCount() {
        return videoModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView name;
        String newpos1,newname2;
        public viewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Resume_Name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(itemView.getContext(),PlayVideo.class);
                    i.putExtra("Url",newpos1);
                    i.putExtra("videoName",newname2);
                    i.putExtra("Name",name.getText().toString());
                    itemView.getContext().startActivity(i);
                }
            });
        }
    }
}
