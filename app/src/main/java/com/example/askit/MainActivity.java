package com.example.askit;

import static com.example.askit.signUp.aname;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.remote.WatchChange;
import com.nitish.typewriterview.TypeWriterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    TypeWriterView txt;
    ImageView send;
    EditText edtext;

    ImageButton menUBtn;
RecyclerView recyclerView;
messegeAdapter messegeadapter;
    String message = "Hello";

ArrayList<modleClass> messegelist=new ArrayList<>();

    FirebaseUser firebaseUser;



    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build();

    JSONArray jarry=new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menUBtn=findViewById(R.id.menuBtn);
        txt = findViewById(R.id.txtWelcome);
        txt.animateText("Hello "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName() +", How can I help you?");
        txt.setCharacterDelay(100);
        txt.setVisibility(View.VISIBLE);
        send=findViewById(R.id.send_btn);
        edtext=findViewById(R.id.message_edit_text);
        recyclerView=findViewById(R.id.chat_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
messegeadapter=new messegeAdapter(MainActivity.this,messegelist);

recyclerView.setAdapter(messegeadapter);

        menUBtn.setOnClickListener(v-> loadMenu());

send.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        message=edtext.getText().toString();
        addtochat(message,modleClass.sentByMe);
        getResponse(message);
        edtext.setText("");
        txt.setVisibility(View.GONE);


    }
});



    }




    void loadMenu(){
        PopupMenu popupMenu=new PopupMenu(MainActivity.this,menUBtn);
        popupMenu.getMenu().add("Show History");
        popupMenu.getMenu().add("Hide History");
        popupMenu.getMenu().add("Log Out");


        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

             if(menuItem.getTitle()=="Log Out"){

                 FirebaseAuth.getInstance().signOut();
                 startActivity(new Intent(MainActivity.this, LoginActivity.class));
                 finish();
                 return true;
             }
             if(menuItem.getTitle()=="Show History"){

messegelist.clear();
messegeadapter.notifyDataSetChanged();
txt.setVisibility(View.INVISIBLE);
                 fetchFromFireStore();
                 return true;
             }
             if(menuItem.getTitle()=="Hide History"){
                 messegelist.clear();
                 messegeadapter.notifyDataSetChanged();

                txt.setVisibility(View.VISIBLE);
             }

                return false;
            }
        });

    }

    void clearAll() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

String str= firebaseUser.getUid();

        db.collection("messeges").document(str).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Not Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        void addtochat(String message,String sentBy){

runOnUiThread(new Runnable() {
    @Override
    public void run() {
messegelist.add(new modleClass(message,sentBy));
addToFirebase(new modleClass(message,sentBy,Timestamp.now()));
messegeadapter.notifyDataSetChanged();
recyclerView.smoothScrollToPosition(messegeadapter.getItemCount());
    }
});

    }



    void addresponse(String response){
        messegelist.remove(messegelist.size()-1);
       addtochat(response,modleClass.sentByBot);
    }


    private void getResponse(String query) {


        messegelist.add(new modleClass("Typing...",modleClass.sentByBot));

        String url = "https://api.openai.com/v1/chat/completions";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo");

           
JSONObject job=new JSONObject();
            job.put("role", "user");
            job.put("content", query);

            JSONObject job2=new JSONObject();
            job2.put("role", "system");
            job2.put("content", "you are my personal assistant and your name is jenny");


            jarry.put(job2);
            jarry.put(job);

            jsonObject.put("messages",jarry);



        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body= RequestBody.create(jsonObject.toString(), JSON);
        okhttp3.Request request=new okhttp3.Request.Builder()
                .url(url).header("Authorization","Bearer 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").post(body).build();

          client.newCall(request).enqueue(new Callback() {
              @Override
              public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("error",e.getMessage());
                  addresponse("Failed due to: "+e.getMessage());
              }

              @Override
              public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                  if(response.isSuccessful()) {
                      try {
                          JSONObject jobj = new JSONObject(response.body().string());

                          JSONArray jarray = jobj.getJSONArray("choices");

                          JSONObject object = jarray.getJSONObject(0);
                          String str=object.getJSONObject("message").getString("content").trim();


                          addresponse(str);

                          JSONObject additionalResponse1 = new JSONObject();
                          try {
                              additionalResponse1.put("role", "assistant");
                          } catch (JSONException e) {
                              throw new RuntimeException(e);
                          }
                          try {
                              additionalResponse1.put("content", str);
                          } catch (JSONException e) {
                              throw new RuntimeException(e);
                          }

                          jarry.put(additionalResponse1);

                      } catch (JSONException e) {
                          throw new RuntimeException(e);
                      }
                  }
                  else {
                      addresponse("Failed due to: "+response.body().toString());



                  }
              }
          });



    }

   void addToFirebase(modleClass modle){

       DocumentReference documentReference;
       documentReference=signUp.getCollectionReff().document();
       documentReference.set(modle).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

           }
       });

   }
   void fetchFromFireStore() {
       FirebaseFirestore documentReference =FirebaseFirestore.getInstance();
       FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
documentReference.collection("messeges").document(firebaseUser.getUid()).collection(firebaseUser.getDisplayName()).orderBy("timestamp", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
    @Override
    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
        // after getting the data we are calling on success method
        // and inside this method we are checking if the received
        // query snapshot is empty or not.
        if (!queryDocumentSnapshots.isEmpty()) {
            // if the snapshot is not empty we are
            // hiding our progress bar and adding

            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot d : list) {
                // after getting this list we are passing
                // that list to our object class.
                modleClass c = d.toObject(modleClass.class);

                // and we will pass this object class
                // inside our arraylist which we have
                // created for recycler view.
                messegelist.add(c);


            }

            messegeadapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messegeadapter.getItemCount());
            // after adding the data to recycler view.
            // we are calling recycler view notifyDataSetChanged
            // method to notify that data has been changed in recycler view.

        } else {
            // if the snapshot is empty we are displaying a toast message.
            txt.setVisibility(View.VISIBLE);
        }
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        // if we do not get any data or any error we are displaying
        // a toast message that we do not get any data
        txt.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
    }
});



   }

}


//tools:context=".signUp"