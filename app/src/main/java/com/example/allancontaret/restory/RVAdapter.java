package com.example.allancontaret.restory;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
    List<Restaurant> restaurants;

    RVAdapter(List<Restaurant> restaurants){
        this.restaurants = restaurants;
    }
    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        Log.i("name", restaurants.get(position).name);
        //holder.restaurantPhoto.setImageResource(restaurants.get(position).img);
        holder.restaurantName.setText(restaurants.get(position).name);
        holder.restaurantAge.setText(restaurants.get(position).address);
        //restaurantPhoto should be ImageView or your CustomImageView
        // vive Glide !!!
        Glide.with(holder.restaurantPhoto.getContext())
                .load("http://api.gregoirejoncour.xyz/images/"+restaurants.get(position).image)
                .fitCenter()
                .into(holder.restaurantPhoto);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.test, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView restaurantName;
        TextView restaurantAge;
        ImageView restaurantPhoto;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            restaurantName = (TextView)itemView.findViewById(R.id.person_name);
            restaurantAge = (TextView)itemView.findViewById(R.id.person_age);
            restaurantPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }

    }

}