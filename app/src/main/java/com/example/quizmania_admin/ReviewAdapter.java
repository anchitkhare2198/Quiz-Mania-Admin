package com.example.quizmania_admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.viewHolder> {

    List<ReviewModel> reviewModelList;

    public ReviewAdapter(List<ReviewModel> reviewModelList) {
        this.reviewModelList = reviewModelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.name.setText(reviewModelList.get(position).getFullName());
        holder.review.setText(reviewModelList.get(position).getReview());
    }

    @Override
    public int getItemCount() {
        return reviewModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView name,review;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Review_Name);
            review = itemView.findViewById(R.id.Review_text);
        }
    }

}
