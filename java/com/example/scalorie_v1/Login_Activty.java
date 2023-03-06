package com.example.scalorie_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Activty extends AppCompatActivity implements View.OnClickListener {


    EditText etEmail, etPassword;
    Button btnLogin, btnSignup, forgetPass;
    private FirebaseAuth firebaseAuth;
    final String EMAIL_PATTERN = ("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");

    ProgressDialog p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignup = (Button) findViewById(R.id.btnSignUp);
        //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword =(EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        forgetPass = (Button) findViewById(R.id.forgotPassword);
        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        forgetPass.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnSignup) {
            Intent intent = new Intent(Login_Activty.this, Register_Activty.class);
            startActivity(intent);
            finish();
        }
        else if (v==forgetPass)
        {
            forgetPassword();
        }
        else if (v == btnLogin)
        {
            p = ProgressDialog.show(Login_Activty.this, "sending data", "Loading please wait...", true);
            p.setCancelable(false);
            p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            p.setMessage("Loading...");
            p.show();
            //start



            if (etEmail.getText().toString().isEmpty())
            {
                etEmail.setError("email is not correct");
                p.dismiss();

            }
            else if (etPassword.getText().toString().isEmpty())
            {
                etPassword.setError("the password is not correct");
                p.dismiss();

            }
            else
            {

                firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {

                                if(task.isSuccessful())
                                {
                                    Toast.makeText(Login_Activty.this, "auth_success",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(Login_Activty.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(Login_Activty.this, "auth_failed",Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
                p.dismiss();
            }
        }

    }

    private void forgetPassword() {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        final Dialog dialog  = new Dialog (Login_Activty.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.forget_pass_dialog);
       // ProgressBar progressBar = dialog.findViewById(R.id.pb_forgetPass);
       // progressBar.setVisibility(View.INVISIBLE);
        dialog.show();

        Button sendEmail = dialog.findViewById(R.id.btnForgetPassword);
        Button rememberPass = dialog.findViewById(R.id.remember_pass);
        EditText email = dialog.findViewById(R.id.et_forgetPass);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == sendEmail)
                {
           //         progressBar.setVisibility(View.VISIBLE);

                    if(email.getText().toString().isEmpty())
                    {
                        email.setError("the email is miss");
          //              progressBar.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        auth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(Login_Activty.this,"Check your email to reset your password!",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(Login_Activty.this,"Try again something got wrong",Toast.LENGTH_SHORT).show();
                                    email.setError("something go wrong...");
                                }
                            }
                        });
              //          progressBar.setVisibility(View.INVISIBLE);

                    }
                }
            }
        });
        rememberPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == rememberPass)
                {
                  dialog.dismiss();
                }
            }
        });
    }
}