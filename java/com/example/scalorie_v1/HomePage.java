package com.example.scalorie_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    ImageView btnProfile;
    ProgressDialog p;
    TextView tvUserName, calories_left;
    DatabaseReference reference, reference2;
    String uid;
    String currentDay, dayInData, bmr, dayCalories;
    int points = 0, count;
    ImageButton fab_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        p = ProgressDialog.show(HomePage.this, "sending data", "Loading please wait...", true);
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        p.setCancelable(false);
        p.setMessage("Loading...");
        p.show();
        fab_add = (ImageButton) findViewById(R.id.fab_add);
        btnProfile = (ImageView) findViewById(R.id.btnProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        calories_left = (TextView) findViewById(R.id.calories_left);
        btnProfile.setOnClickListener(this);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("data");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dayInData = snapshot.child("currentDay").getValue().toString();

                bmr = snapshot.child("bmr").getValue().toString();
                dayCalories = snapshot.child("dayCal").getValue().toString();
                calories_left.setText(snapshot.child("dayCal").getValue().toString());
                tvUserName.setText(snapshot.child("userName").getValue().toString());
                points =Integer.parseInt(snapshot.child("points").getValue().toString());
                if (!snapshot.child("urlProfile").getValue(String.class).equals(""))
                {
                    System.out.println("by");
                    Glide.with(HomePage.this).load(snapshot.child("urlProfile").getValue().toString()).into(btnProfile);

                }
                p.dismiss();
                checkDay();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });
        reference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("daily product");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child("count").getValue().toString().equals("0"))
                {
                    replaceFragment(new fragment_show_data());
                }
                else
                {
                    replaceFragment(new fragment_show_data());

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        DateFormat df = new SimpleDateFormat("d MMM");
        currentDay = df.format(Calendar.getInstance().getTime());
        fab_add.setOnClickListener(this);
    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.show_products,fragment);
        fragmentTransaction.commit();
    }
    private void checkDay() {
        System.out.println(dayInData + " == "+currentDay);
        if (!currentDay.equals(dayInData))
        {
            System.out.println("hi");
           checkPoints();
        }

    }

    public void checkPoints() {
        int cal;
        String message;
        cal = Integer.parseInt(dayCalories);
        System.out.println(cal);

        if(cal>=0 && cal<= 300)
        {
            points += cal;
            message = "good job you work perfectly you got " + points + " points";
        }
        else if(cal<0)
        {
            points += cal;
            message = "sorry You're over the limit of the calories you got " + points + " points";

        }
        else {
            if((cal * -1) + 300<-300)
            {
                points += -300;
            }
            else
            {
                points += (cal * -1)+300;
            }
            message = "It's not healthy to not eat too much, the app recommend not eating less than 300 of the bmr. that's why you got it " + points + " points (your bmr " + bmr +")" ;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("point update").setMessage(message).setCancelable(true).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })
                .show();
        System.out.println(message);

        changeData();



    }
    private void changeData()
    {

        FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/currentDay").setValue(currentDay);
        FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/points").setValue(points);
        FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/dayCal").setValue(bmr);
        dayInData = currentDay;
    }
    @Override
    public void onClick(View v) {
        if(v==btnProfile)
        {
            Intent intent = new Intent(HomePage.this, Profilepage.class);
            startActivity(intent);
        }
        else if(v == fab_add)
        {
            final Dialog dialog = new Dialog(HomePage.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.add_choice_dialog);
            dialog.show();
            Button custom = dialog.findViewById(R.id.btn_custom);

            custom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    final Dialog custom_dialog = new Dialog(HomePage.this);
                    custom_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    custom_dialog.setCancelable(true);
                    custom_dialog.setContentView(R.layout.add_custom_dialog);
                    custom_dialog.show();
                    Button submit = custom_dialog.findViewById(R.id.submit_product);
                    EditText name = custom_dialog.findViewById(R.id.et_name_of_product);
                    EditText calorie_100 = custom_dialog.findViewById(R.id.et_calorie_100);
                    EditText gram = custom_dialog.findViewById(R.id.et_gram);

                    custom_dialog.show();
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                                if (name.getText().toString().isEmpty())
                                {
                                    name.setError("the name is empty");
                                }
                                else if (calorie_100.getText().toString().isEmpty())
                                {
                                    calorie_100.setError("the calories to 100 is empty");

                                }
                                else if (gram.getText().toString().isEmpty())
                                {
                                    gram.setError("the gram is empty");

                                }
                                else
                                {
                                    product product =new product(name.getText().toString(),Integer.parseInt(calorie_100.getText().toString()),Integer.parseInt(gram.getText().toString()));
                                    reference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("daily product");
                                    reference2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            count = Integer.parseInt(snapshot.child("count").getValue().toString());
                                            count = count+1;
                                            addData(product,count);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });

                                }


                        }
                    });
                }
            });

        }
    }
    public void addData(product product, int count)
    {
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("daily product").child("count").setValue(String.valueOf(count));

        dayCalories = String.valueOf(Integer.parseInt(dayCalories) - product.getCal());
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("daily product").child(String.valueOf(count)).child("name").setValue(product.getName());
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("daily product").child(String.valueOf(count)).child("cal").setValue(product.getCal());
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("daily product").child(String.valueOf(count)).child("gram").setValue(product.getGram());
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("data").child("dayCal").setValue(dayCalories);

        Intent intent = new Intent(HomePage.this,HomePage.class);
        startActivity(intent);

    }
}