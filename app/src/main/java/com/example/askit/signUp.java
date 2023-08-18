package com.example.askit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.remote.FirestoreChannel;
import com.google.firebase.ktx.Firebase;

public class signUp extends AppCompatActivity {

    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText  conPassword;
    MaterialButton signUp;
    TextView login;
    ProgressBar progressBar;

    public static String aname="Buddy";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name=findViewById(R.id.edtSignUpFullName);
        email=findViewById(R.id.edtSignUpEmail);
        password=findViewById(R.id.edtSignUpPassword);
conPassword=findViewById(R.id.edtSignUpConfirmPassword);
      signUp  =findViewById(R.id.btnSignUp);
       login =findViewById(R.id.txtSignIn);
       progressBar =findViewById(R.id.signUpProgressBar);

          login.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  startActivity(new Intent(signUp.this, LoginActivity.class));
                  finish();
              }
          });
    }


    public void createAccount(View view) {
      aname=name.getText().toString();

String amail=email.getText().toString();
        String apass=password.getText().toString();
        String conPass=conPassword.getText().toString();

if(validateData(amail,apass,conPass)==true){

addtoFirebse(amail,apass);


}

    }




    public void addtoFirebse(String email,String pass){

        changeInProgress(true);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(signUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){

                    Toast.makeText(signUp.this, "account Created", Toast.LENGTH_SHORT).show();

                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(aname).build();
                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                    firebaseUser.updateProfile(userProfileChangeRequest);
                   firebaseAuth.signOut();
                   finish();

                       Intent it=new Intent(signUp.this, LoginActivity.class);
                       startActivity(it);
                }
                else {
                    Toast.makeText(signUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }



    boolean validateData(String e,String passwor,String confirmPasswor){
        //validate the data that are input by user.

        if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
            email.setError("Email is invalid");

            return false;
        }
        if(passwor.length()<6){
            password.setError("Password length is invalid");
            return false;
        }
        if(!passwor.equals(confirmPasswor)){
            conPassword.setError("Password not matched");
            return false;
        }
        if(aname.equals("")){
            name.setError("Please Enter the name!");
        }
        return true;
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            signUp.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            signUp.setVisibility(View.VISIBLE);
        }
    }


    static CollectionReference getCollectionReff(){

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        return FirebaseFirestore.getInstance().collection("messeges").document(firebaseUser.getUid()).collection(firebaseUser.getDisplayName());
    }


}