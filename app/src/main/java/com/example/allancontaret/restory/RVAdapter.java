package com.example.allancontaret.restory;

import android.content.Context;
import android.content.Intent;
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

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RestaurantViewHolder>{
    List<Restaurant> restaurants;

    RVAdapter(List<Restaurant> restaurants){
        this.restaurants = restaurants;
    }

    /*public void swap(List<Restaurant> datas){
        restaurants.clear();
        restaurants.addAll(datas);
        notifyDataSetChanged();
    }*/

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        Log.i("restaurantLoader", String.valueOf(restaurants.get(position)));
        holder.restaurantName.setText(restaurants.get(position).name);
        holder.resto = restaurants.get(position);
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
    public RestaurantViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.restaurant_card, viewGroup, false);
        RestaurantViewHolder pvh = new RestaurantViewHolder(v);
        return pvh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        CardView cv;
        TextView restaurantName;
        ImageView restaurantPhoto;
        Restaurant resto;

        RestaurantViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            cv = (CardView)itemView.findViewById(R.id.cv);
            restaurantName = (TextView)itemView.findViewById(R.id.restaurant_name);
            restaurantPhoto = (ImageView)itemView.findViewById(R.id.restaurant_photo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    final Intent intent =  new Intent(context, RestaurantActivity.class);
                    intent.putExtra("MyClass", resto);
                    context.startActivity(intent);
                }
            });
        }

    }

}