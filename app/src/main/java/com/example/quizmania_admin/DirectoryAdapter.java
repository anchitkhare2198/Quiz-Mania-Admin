package com.example.quizmania_admin;

import android.content.Intent;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.viewHolder> {

    List<DirectoryModel> list;
    private DeleteListener deleteListener;
    private long mLastClickTime = 0;

    public DirectoryAdapter(List<DirectoryModel> list, DeleteListener deleteListener) {
        this.list = list;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.directory_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.setData(list.get(position).getDir_Name(),list.get(position).getKey(),position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private ImageButton delete;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.directory_Title);
            delete = itemView.findViewById(R.id.Directory_delete);
        }

        public void setData(final String Dir_name, final String key, final int position){
            this.title.setText(Dir_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(itemView.getContext(),Categories.class);
                    i.putExtra("title", Dir_name);
                    i.putExtra("position", position);
                    i.putExtra("key", key);
                    itemView.getContext().startActivity(i);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDelete(key, position);
                }
            });

        }


    }

    public interface DeleteListener{
        public void onDelete(String key, int position);
    }
}
