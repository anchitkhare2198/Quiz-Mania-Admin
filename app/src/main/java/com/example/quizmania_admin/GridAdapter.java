package com.example.quizmania_admin;

import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {


    public List<String> sets;
    private String category;
    private GridListener gridListener;
    private long mLastClickTime = 0;
    int p = 0;

    public GridAdapter(List<String> sets, String category, GridListener gridListener)
    {
        this.sets = sets;
        this.category = category;
        this.gridListener = gridListener;

    }

    @Override
    public int getCount() {
        return sets.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view;

        if(convertView == null)
        {
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item,parent,false);
        }
        else
        {
            view = convertView;
        }

        if(position == 0)
        {
            ((TextView)view.findViewById(R.id.chapter)).setText("+");
        }
        else {
            ((TextView)view.findViewById(R.id.chapter)).setText(String.valueOf(position));
        }



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(position == 0)
                {
                    gridListener.addSet();
                }
                else {
                Intent i = new Intent(parent.getContext(), Questions.class);
                i.putExtra("category",category);
                i.putExtra("setId",sets.get(position - 1));
                parent.getContext().startActivity(i);
                }

//
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (position!=0){
                    gridListener.onLongClick(sets.get(position - 1),position);
                }
                return false;
            }
        });

        return view;
    }

    public interface GridListener{
        public void addSet();

        void onLongClick(String setId, int position);

    }
}
