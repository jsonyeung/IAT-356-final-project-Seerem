package com.example.seeremapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seeremapp.R;
import com.example.seeremapp.UserProfileActivity;
import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.containers.Worksite;
import com.example.seeremapp.fragment.UserProfileFragment;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public UsersAdapter(Context context, List<User> data) {
      this.mInflater = LayoutInflater.from(context);
      this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = mInflater.inflate(R.layout.card_li, parent, false);
      return new UsersAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
      User user = mData.get(position);
      holder.heading.setText(user.getFirstName() + " " + user.getLastName());
      holder.caption.setText(user.getEmail());

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent i = new Intent(holder.itemView.getContext(), UserProfileActivity.class);
          i.putExtra("email", user.getEmail());
          holder.itemView.getContext().startActivity(i);
        }
      });
    }

    // total number of rows
    @Override
    public int getItemCount() {
      return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
      TextView heading, caption;
      ImageView avatar;

      ViewHolder(View itemView) {
        super(itemView);
        heading = itemView.findViewById(R.id.heading);
        caption = itemView.findViewById(R.id.caption);
        avatar = itemView.findViewById(R.id.avatar);

        avatar.setVisibility(View.GONE);
        itemView.setOnClickListener(this);
      }

      @Override
      public void onClick(View view) {
      }
    }
  }