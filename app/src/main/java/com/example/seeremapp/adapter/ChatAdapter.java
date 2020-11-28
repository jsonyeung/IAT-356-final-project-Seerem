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
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Chat;
import com.example.seeremapp.database.containers.User;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
  private List<Chat> mData;
  private LayoutInflater mInflater;
  private User user;

  // data is passed into the constructor
  public ChatAdapter(Context context, List<Chat> data, User user) {
    this.mInflater = LayoutInflater.from(context);
    this.mData = data;
    this.user = user;
  }

  // inflates the row layout from xml when needed
  @Override
  public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.message_li, parent, false);
    return new ChatAdapter.ViewHolder(view);
  }

  // binds the data to the TextView in each row
  @Override
  public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
    Chat message = mData.get(position);

    if (message.getEmail().equals(user.getEmail())) {
      holder.bubbleReceive.setVisibility(View.GONE);
      holder.bubbleSend.setText(message.getMessage());
    } else {
      holder.bubbleSend.setVisibility(View.GONE);
      holder.bubbleReceive.setText(message.getName() + " (" + message.getRole() + "):\n" + message.getMessage());
    }

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
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
    private TextView bubbleReceive, bubbleSend;

    ViewHolder(View itemView) {
      super(itemView);
      bubbleReceive = itemView.findViewById(R.id.messageReceive);
      bubbleSend = itemView.findViewById(R.id.messageSend);

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
    }
  }
}
