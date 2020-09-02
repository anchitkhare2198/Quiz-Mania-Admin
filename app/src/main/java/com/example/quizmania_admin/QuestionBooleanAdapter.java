package com.example.quizmania_admin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionBooleanAdapter extends RecyclerView.Adapter<QuestionBooleanAdapter.viewHolder> {

    private List<QuestionModel> list;
    private String category;
    private DeleteListener listener;

    public QuestionBooleanAdapter(List<QuestionModel> list, String category, DeleteListener listener) {
        this.list = list;
        this.category = category;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String question = list.get(position).getQuestion();
        String answer = list.get(position).getAnswer();

        holder.setData(question,answer,position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private TextView question,answer;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
        }

        private void setData(String question, String answer, final int position) {
            int p =  Questions.list.size();
            this.question.setText("Ques.) " + question);
            this.answer.setText("Ans.) " + answer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIntent = new Intent(itemView.getContext(), Add_boolean_question.class);
                    editIntent.putExtra("categoryName", category);
                    editIntent.putExtra("setId", list.get(position).getSet());
                    editIntent.putExtra("position", position);
                    itemView.getContext().startActivity(editIntent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(position, list.get(position).getId());
                    return false;
                }
            });
        }
    }

    public interface DeleteListener{
        void onLongClick(int position, String id);
    }
}
