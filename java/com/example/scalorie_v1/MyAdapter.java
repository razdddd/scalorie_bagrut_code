package com.example.scalorie_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<product> list;

    public MyAdapter(Context context, ArrayList<product> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.prodcut,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        product product = list.get(position);
        holder.tv_name.setText(product.getName()+"");
        holder.tv_gram.setText(product.getGram()+"");
        holder.tv_cal.setText(product.getCaloriesTo100()+"");
        holder.tv_calories.setText(product.getCal()+"");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_name,tv_gram,tv_cal,tv_calories;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.name_product);
            tv_gram = (TextView) itemView.findViewById(R.id.gram_product);
            tv_cal = (TextView) itemView.findViewById(R.id.product_cal_to100);
            tv_calories = (TextView) itemView.findViewById(R.id.calories_product);

        }
    }
}
