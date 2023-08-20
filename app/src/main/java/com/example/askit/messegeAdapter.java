package com.example.askit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class messegeAdapter extends RecyclerView.Adapter<viewHolder> {

Context context;
ArrayList<modleClass> arr;
    boolean flag = true;
    boolean isSpeechCompleted = false;
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
    holder.mic.setVisibility(View.VISIBLE);


    holder.mic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {


if(flag==true) {
    MainActivity.ts.speak(messege.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
    isSpeechCompleted=false;
    new Thread(() -> {
        while (!isSpeechCompleted) {
            if (MainActivity.ts.isSpeaking()) {
                holder.mic.setImageResource(R.drawable.baseline_mic_off_24);
                flag = false;

            } else {

                // Speech synthesis is completed, perform your action here
                holder.mic.setImageResource(R.drawable.baseline_mic_24);
                isSpeechCompleted = true;
                MainActivity.ts.stop();
                flag = true;
            }
        }
    }).start();

}
else{
    holder.mic.setImageResource(R.drawable.baseline_mic_24);

    MainActivity.ts.stop();
    flag = true;

}



        }
    });
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
ImageButton mic;

    public viewHolder(@NonNull View itemView) {
        super(itemView);
       left=itemView.findViewById(R.id.left_chat_view);
        right=itemView.findViewById(R.id.right_chat_view);
        bot=itemView.findViewById(R.id.left_chat_text_view);
        me=itemView.findViewById(R.id.right_chat_text_view);
        mic=itemView.findViewById(R.id.mic);


    }
}