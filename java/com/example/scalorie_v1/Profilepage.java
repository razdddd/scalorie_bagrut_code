package com.example.scalorie_v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class Profilepage extends AppCompatActivity implements View.OnClickListener {
    TextView tv_age,tv_height,tv_weight,tv_gender,tv_userName1,tv_startWeight, tv_userName2, tv_email,tv_startDate,tv_point;
    ImageView profilePic, profileBack;
    DatabaseReference reference;
    ProgressDialog p;

    Uri uri ;
    Button btn_change, btn_share, btn_Home;
    String uid;
    String age, height, weight, gender, nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_height = (TextView) findViewById(R.id.tv_height);
        tv_weight = (TextView) findViewById(R.id.tv_weight);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_userName1 = (TextView) findViewById(R.id.tvUserName1);
        tv_userName2 = (TextView) findViewById(R.id.tvUserName2);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_startDate = (TextView) findViewById(R.id.tv_DayOfStart);
        tv_point = (TextView) findViewById(R.id.tv_point);
        tv_startWeight = (TextView) findViewById(R.id.tv_StartWeight) ;
        btn_Home = (Button) findViewById(R.id.HopePageBtn);
        btn_share = (Button) findViewById(R.id.btnSharePoint);
        profilePic = (ImageView) findViewById(R.id.foto_profile);
        profileBack = (ImageView) findViewById(R.id.back_photo);
        profilePic.setOnClickListener(this);


        p = ProgressDialog.show(Profilepage.this, "sending data", "Loading please wait...", true);
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        p.setCancelable(false);
        p.setMessage("Loading...");
        p.show();


        btn_change = (Button) findViewById(R.id.btn_change);
        btn_change.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_Home.setOnClickListener(this);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("data");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                age = snapshot.child("age").getValue().toString();
                height = snapshot.child("height").getValue().toString();
                weight = snapshot.child("weight").getValue().toString();
                nickname = snapshot.child("userName").getValue().toString();

                tv_age.setText(age+"y");
                tv_height.setText(height+"cm");
                tv_weight.setText(weight+"kg");
                tv_userName1.setText(nickname);
                tv_userName2.setText(snapshot.child("userName").getValue().toString());
                tv_email.setText(snapshot.child("gmail").getValue().toString());
                tv_startDate.setText(snapshot.child("dayOfStart").getValue().toString());
                tv_point.setText(snapshot.child("points").getValue().toString());
                tv_startWeight.setText(snapshot.child("StartWeight").getValue().toString()+"kg");
                if(snapshot.child("gender").getValue().toString().equals("0"))
                {
                    tv_gender.setText("male");
                    gender = "male";
                }
                else
                {
                    tv_gender.setText("female");
                    gender = "female";

                }
                if (!snapshot.child("urlProfile").getValue(String.class).equals(""))
                {
                    Glide.with(Profilepage.this).load(snapshot.child("urlProfile").getValue().toString()).into(profilePic);
                    Glide.with(Profilepage.this).load(snapshot.child("urlProfile").getValue().toString()).into(profileBack);

                }
                p.dismiss();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                p.dismiss();
            }
        });
    }
    public void openDialog()
    {
        ImagePicker.Companion.with(Profilepage.this).
                crop().
                maxResultSize(1080, 1080).
                start(101);
    }

    // get image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == Activity.RESULT_OK &&data!=null)&&data.getData()!=null) {
            uri = data.getData();
            profilePic.setImageURI(uri);
            profileBack.setImageURI(uri);
            System.out.println("hi");
            uploadImage();
        }
        else
        {
            System.out.println("by");
            Toast.makeText(Profilepage.this,"no image selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        p = ProgressDialog.show(Profilepage.this, "sending data", "Loading please wait...", true);
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        p.setCancelable(false);
        p.setMessage("Loading...");
        p.show();
        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(uri)
        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Uri> task) {
                            if (task.isSuccessful())
                            {
                                System.out.println("ho");
                                uploadProfilePic(task.getResult().toString());
                            }

                        }
                    });
                    Toast.makeText(Profilepage.this," image uploaded",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Profilepage.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                p.dismiss();
            }
        });
    }
    private void uploadProfilePic(String uri)
    {
        System.out.println("sssss");
        FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/urlProfile").setValue(uri);
    }

    @Override
    public void onClick(View v) {
        if (v == profilePic)
        {
            openDialog();
        }
        else if(v== btn_Home)
        {
            Intent intent = new Intent(Profilepage.this,HomePage.class);
             startActivity(intent);
            finish();
        }
        else if(v==btn_share)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Scalorie");
            intent.putExtra(Intent.EXTRA_TEXT, "look i got " + tv_point.getText().toString() +" points on scalorie application. I recommend you download the app and start too!");
            startActivity(Intent.createChooser(intent, "Share via"));
        }
        else if(v == btn_change)
        {
            final Dialog dialog  = new Dialog (Profilepage.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.change_info_dialog);
            final EditText etNickname = dialog.findViewById(R.id.et_nickname);
            final EditText etAge = dialog.findViewById(R.id.et_age);
            final EditText etWeight = dialog.findViewById(R.id.et_weight);
            final EditText etHeight = dialog.findViewById(R.id.et_height);
            final Button btnFinish = dialog.findViewById(R.id.btn_finish);
            final TextView title = dialog.findViewById(R.id.Title);

            etNickname.setText(nickname);
            etAge.setText(age);
            etWeight.setText(weight);
            etHeight.setText(height);
            title.setText("change data");
            dialog.show();
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnFinish  == v)
                    {
                        p = ProgressDialog.show(Profilepage.this, "sending data", "Loading please wait...", true);
                        p.setCancelable(true);
                        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        p.setMessage("Loading...");
                        p.show();

                        if ((etAge.getText().toString().isEmpty() && etHeight.getText().toString().isEmpty()) && etWeight.getText().toString().isEmpty())
                        {
                            if (etNickname.toString().isEmpty())
                            {
                                etNickname.setError("empty");
                            }
                            else if (etWeight.getText().toString().isEmpty())
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
                        else if (etNickname.getText().toString().length() <  6)
                        {
                            etNickname.setError("nickname is to short min 6 letters");
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
                        else {
                            int bmr;
                            if (gender.equals("male")) {

                                bmr = (10 * Integer.parseInt(etWeight.getText().toString())) + (int) (6.25 * Integer.parseInt(etHeight.getText().toString())) - (5 * Integer.parseInt(etAge.getText().toString())) + 5;
                            }
                            else
                            {
                                bmr = (10 * Integer.parseInt(etWeight.getText().toString())) + (int) (6.25 * Integer.parseInt(etHeight.getText().toString())) - (5 * Integer.parseInt(etAge.getText().toString())) - 161;

                            }

                            FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/userName").setValue(etNickname.getText().toString());
                            FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/height").setValue(etHeight.getText().toString());
                            FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/weight").setValue(etWeight.getText().toString());
                            FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/age").setValue(etAge.getText().toString());
                            FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/bmr").setValue(bmr);
                            FirebaseDatabase.getInstance().getReference("Users/"+uid+"/data/dayCal").setValue(bmr);

                            p.dismiss();
                            dialog.dismiss();

                        }
                    }
                    p.dismiss();

                }
            });

        }

    }
}