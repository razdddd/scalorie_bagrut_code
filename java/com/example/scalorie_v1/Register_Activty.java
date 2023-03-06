package com.example.scalorie_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class Register_Activty extends AppCompatActivity implements View.OnClickListener {

    final String EMAIL_PATTERN = ("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
    private FirebaseAuth firebaseAuth;
    SharedPreferences sp_isLogin;



    EditText etEmail, etPassword, etConformPassword;
    Button btnRegister,btnHaveAccount;
    ProgressDialog p;
    ProgressDialog progressDialog;
    String gmailStr, passwordStr, conPasswordStr, genderStr;
    int age, height, weight, gender, bmr;
    RadioButton rb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activty);
        etEmail = (EditText) findViewById(R.id.inputEmail);
        etPassword = (EditText) findViewById(R.id.inputPassword);
        etConformPassword = (EditText) findViewById(R.id.inputConformPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnHaveAccount = (Button) findViewById(R.id.alreadyHaveAccount);
        sp_isLogin = getSharedPreferences("isLogin",0);

        btnHaveAccount.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        if(v==btnRegister)
        {
            gmailStr = etEmail.getText().toString();
            passwordStr = etPassword.getText().toString();
            conPasswordStr = etConformPassword.getText().toString();

            if(gmailStr.isEmpty() || passwordStr.isEmpty() || conPasswordStr.isEmpty())
            {
                if(gmailStr.isEmpty())
                {
                    etEmail.setError("the gmail is missing");
                }
                else if(passwordStr.isEmpty())
                {
                    etPassword.setError("the password is missing");

                }
                else {
                    etConformPassword.setError("the confirm password is missing");

                }
            }
            else if(!gmailStr.matches(EMAIL_PATTERN))
            {
                etEmail.setError("the gmail is not correct");
            }
            else if(!checkPassword(etPassword,passwordStr))
            {
                System.out.println("password is not correct");
            }
            else if(!passwordStr.equals(conPasswordStr))
            {
                etConformPassword.setError("the password and the confirm aren't the same");

            }
            else
            {

                System.out.println("hello");
                openDialog();

            }


        }
        else if(v==btnHaveAccount)
        {

            Intent intent = new Intent(Register_Activty.this,Login_Activty.class);
            startActivity(intent);
            finish();
        }
    }




    public void openDialog()
    {
        final Dialog dialog  = new Dialog (Register_Activty.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.register_dialog);
        final EditText etAge = dialog.findViewById(R.id.et_age);
        final EditText etWeight = dialog.findViewById(R.id.et_weight);
        final EditText etHeight = dialog.findViewById(R.id.et_height);
        final Button btnFinish = dialog.findViewById(R.id.btn_finish);
        final RadioGroup radioGroup = dialog.findViewById(R.id.rg_genders);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id= radioGroup.getCheckedRadioButtonId();
                rb=( RadioButton) dialog.findViewById(id);
                if ((etAge.getText().toString().isEmpty() && etHeight.getText().toString().isEmpty()) && etWeight.getText().toString().isEmpty())
                {
                    if (etWeight.getText().toString().isEmpty())
                    {
                        etWeight.setError("empty");
                    }
                    else  if (etHeight.getText().toString().isEmpty())
                    {
                        etHeight.setError("empty");
                    }
                    else
                    {
                        etAge.setError("empty");
                    }
                }
                else if(Integer.parseInt(etAge.getText().toString())>120 || Integer.parseInt(etAge.getText().toString())<0)
                {
                    etAge.setError("the age is not correct");
                }
                else if(Integer.parseInt(etWeight.getText().toString())>1000 || Integer.parseInt(etWeight.getText().toString())<0)
                {
                    etWeight.setError("the weight is not correct");
                }
                else if(Integer.parseInt(etHeight.getText().toString())>300 || Integer.parseInt(etHeight.getText().toString())<10)
                {
                    etHeight.setError("the height is not correct");
                }
                else if(rb == null)
                {
                    Toast.makeText(Register_Activty.this,"what is your gender",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    age = Integer.parseInt(etAge.getText().toString());
                    height = Integer.parseInt(etHeight.getText().toString());
                    weight = Integer.parseInt(etWeight.getText().toString());
                    genderStr = rb.getText().toString();
                    register();
                }
            }
        });
        dialog.show();
    }

    public void register() {
        if (genderStr.equals("Man")) {
            gender = 0;
            bmr = (10 * weight) + (int) (6.25 * height) - (5 * age) + 5;
        } else {
            gender = 1;
            bmr = (10 * weight) + (int) (6.25 * height) - (5 * age) - 161;
        }
        p = ProgressDialog.show(Register_Activty.this, "sending data", "Loading please wait...", true);
        p.setCancelable(true);
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        p.setMessage("Loading...");
        p.show();

        firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String email = etEmail.getText().toString();
                    user user = new user(age,height,gender,weight,bmr,email);
                    product product = new product("check", 50,6);





                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("data")
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(Register_Activty.this, "Successfully registered", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Register_Activty.this,HomePage.class);
                                startActivity(intent);


                            }
                            else
                            {
                                Toast.makeText(Register_Activty.this, "Registration Error", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Register_Activty.this,Register_Activty.class);
                                startActivity(intent);

                            }
                            finish();
                        }
                    });



                } else {
                    Toast.makeText(Register_Activty.this, "Registration Error", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Register_Activty.this,Register_Activty.class);
                    startActivity(intent);
                    finish();
                }



                p.dismiss();

            }
        });
    }

        // check password
        public static boolean checkPassword(EditText etPassword, String password) {
            boolean SmallChar = false;
            boolean isNumber = false;
            if (password.length() < 6 || password.length() > 15) {
                etPassword.setError("The password should contain 6 to 15 characters");
                return false;
            }
            for (int i = 0; i < password.length(); i++) {
                for (char c = 'a'; c <= 'z'; c++) {
                    if (c == password.charAt(i)) {
                        SmallChar = true;
                    }
                }
                for (int j = 0; j < 10; j++) {
                    if (String.valueOf(j).charAt(0) == password.charAt(i)) {
                        isNumber = true;
                    }
                }
            }
            if (!SmallChar) {
                etPassword.setError("The password must contain at least one small letter (a,b,z,d.....)");
                return false;
            }
            if (!isNumber) {
                etPassword.setError("The password must contain at least one number (1,2,3,4.....) ");
                return false;
            }
            return true;
        }
}