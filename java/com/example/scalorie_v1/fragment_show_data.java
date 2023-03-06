package com.example.scalorie_v1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class fragment_show_data extends Fragment {

    View view;
    RecyclerView recyclerView;
    DatabaseReference reference;
    String uid;

    MyAdapter myAdapter;
    ArrayList<product> products;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_show_data, container, false);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        recyclerView =view.findViewById(R.id.productList);

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("daily product");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        products = new ArrayList<>();
        myAdapter = new  MyAdapter(getContext(),products);
        recyclerView.setAdapter(myAdapter);

        System.out.println("razd");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (int i =0; i<= Integer.parseInt(snapshot.child("count").getValue().toString()); i++)
                {
                    System.out.println(i+" num");
                    product product = new product(snapshot.child(String.valueOf(i)).child("name").getValue().toString() + ""
                            ,Integer.parseInt(snapshot.child(String.valueOf(i)).child("cal").getValue().toString() +""),
                            Integer.parseInt(snapshot.child(String.valueOf(i)).child("gram").getValue().toString() + ""));
                    products.add(product);
                    System.out.println(i);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }
}