package com.example.askit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText emailEditText,passwordEditText;
    MaterialButton loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView,forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.edtSignInEmail);
        passwordEditText = findViewById(R.id.edtSignInPassword);
        loginBtn = findViewById(R.id.btnSignIn);
        progressBar = findViewById(R.id.signInProgressBar);
        createAccountBtnTextView = findViewById(R.id.txtSignUp);
        forgotPassword=findViewById(R.id.txtForgotPassword);


        createAccountBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,signUp.class));
                finish();
            }
        });



        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle(" Recover Password !");

                builder.setMessage("Check your email to reset password.");
                builder.setIcon(R.drawable.baseline_mail_outline_24);
                LinearLayout linearLayout=new LinearLayout(LoginActivity.this);
                final EditText emailet= new EditText(LoginActivity.this);

                // write the email using which you registered
                emailet.setHint("Enter your e-mail.");
                emailet.setMinEms(12);

                emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                linearLayout.addView(emailet);
                linearLayout.setPadding(20,10,10,10);

                builder.setView(linearLayout);

                // Click on Recover and a email will be sent to your registered email id
                builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email=emailet.getText().toString().trim();
                        if(email.equals("")){
                            emailet.setError("enter the email");
                        }
                        else {
                            beginRecovery(email);
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.setOnShowListener(arg0 -> {
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.light_red));
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.light_red));
                });
                alert.show();
            }
        });

    }


void beginRecovery(String Foremail){
    ProgressDialog loadingBar=new ProgressDialog(this);
    loadingBar.setMessage("Sending Email....");
    loadingBar.setCanceledOnTouchOutside(false);
    loadingBar.show();
FirebaseAuth mAuth=FirebaseAuth.getInstance();
    mAuth.sendPasswordResetEmail(Foremail).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            loadingBar.dismiss();
            if(task.isSuccessful())
            {
                // if isSuccessful then done message will be shown
                // and you can change the password
                Toast.makeText(LoginActivity.this,"Check your mail ",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(LoginActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            loadingBar.dismiss();
            Toast.makeText(LoginActivity.this,"Error Failed",Toast.LENGTH_LONG).show();
        }
    });
}

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email,String password){
        //validate the data that are input by user.

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        return true;
    }



    public void login(View view) {

        String email  = emailEditText.getText().toString();
        String password  = passwordEditText.getText().toString();


        boolean isValidated = validateData(email,password);
        if(isValidated==true){
            loginAccountInFirebase(email,password);
        }



    }

 void loginAccountInFirebase(String email,String password){
changeInProgress(true);
     FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
     firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
             changeInProgress(false);

             if(task.isSuccessful()){
                 if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                 }
                 else{
                     new AestheticDialog.Builder(LoginActivity.this, DialogStyle.TOASTER, DialogType.WARNING)
                             .setTitle("Error!")
                             .setMessage("Email not Verified, Please check your mail")
                             .setCancelable(true)
                             .setDarkMode(false)

                             .setGravity(Gravity.TOP)
                             .setAnimation(DialogAnimation.SLIDE_RIGHT)

                             .show();
                 }
             }
             else {


                 new AestheticDialog.Builder(LoginActivity.this, DialogStyle.FLAT, DialogType.ERROR)
                         .setTitle("Error!")
                         .setMessage(task.getException().getLocalizedMessage())
                         .setCancelable(true)
                         .setDarkMode(true)
                         .setGravity(Gravity.CENTER)
                         .setAnimation(DialogAnimation.SLIDE_RIGHT)

                        .show();

                 //Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
             }

         }
     });


 }
}