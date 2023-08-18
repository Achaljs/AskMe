package com.example.askit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class messegeAdapter extends RecyclerView.Adapter<viewHolder> {

Context context;
ArrayList<modleClass> arr;

    public messegeAdapter(Context context, ArrayList<modleClass> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.chatcard,parent,false);
        viewHolder vh=new viewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
modleClass messege=arr.get(position);
if(messege.sentBy.equals(modleClass.sentByMe)){
          holder.right.setVisibility(View.VISIBLE);
    holder.left.setVisibility(View.GONE);
    holder.me.setText(messege.getMessage());
}
else {
    holder.left.setVisibility(View.VISIBLE);
    holder.right.setVisibility(View.GONE);
    holder.bot.setText(messege.getMessage());
}

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}
class viewHolder extends RecyclerView.ViewHolder {

LinearLayout left,right;

TextView bot,me;

    public viewHolder(@NonNull View itemView) {
        super(itemView);
       left=itemView.findViewById(R.id.left_chat_view);
        right=itemView.findViewById(R.id.right_chat_view);
        bot=itemView.findViewById(R.id.left_chat_text_view);
        me=itemView.findViewById(R.id.right_chat_text_view);


    }
}