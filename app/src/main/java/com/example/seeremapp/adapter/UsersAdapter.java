package com.example.seeremapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeremapp.R;
import com.example.seeremapp.UserProfileActivity;
import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.User;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private WorksiteDB worksiteDB;
    private List<User> mData;
    private LayoutInflater mInflater;
    private int wid;

    // data is passed into the constructor
    public UsersAdapter(Context context, List<User> data, int wid) {
      this.mInflater = LayoutInflater.from(context);
      this.mData = data;
      this.wid = wid;

      worksiteDB = WorksiteDB.getInstance(context);
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
      String userRole = worksiteDB.getUserRole(user.getEmail(), wid);

      holder.heading.setText(user.getFirstName() + " " + user.getLastName());
      holder.caption.setText(user.getEmail());
      holder.status.setText(userRole);
      holder.status.setVisibility(View.VISIBLE);
      holder.statusCircle.setVisibility(View.GONE);

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent i = new Intent(holder.itemView.getContext(), UserProfileActivity.class);
          i.putExtra("email", user.getEmail());
          holder.itemView.getContext().startActivity(i);
        }
      });

      // user verification
      if (!worksiteDB.checkVerified(user.getEmail(), wid)) {
        if (worksiteDB.getLoggedUserRole(wid).equals("WORKER"))
          holder.itemView.setVisibility(View.GONE);

        holder.itemView.setAlpha(0.7f);
        holder.status.setText("REQUIRES VERIFY");
        holder.statusCircle.setVisibility(View.VISIBLE);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View view) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which){
                  case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    worksiteDB.setUserVerify(user.getEmail(), wid, true);
                    notifyDataSetChanged();
                    dialog.dismiss();
                    break;

                  case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    worksiteDB.kickUser(user.getEmail(), wid);
                    mData.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mData.size());
                    dialog.dismiss();
                    break;
                }
              }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle(user.getFirstName() + " " + user.getLastName() + " wants to join the worksite")
                   .setPositiveButton("Verify", dialogClickListener)
                   .setNegativeButton("Reject", dialogClickListener).show();

            return false;
          }
        });

        return;
      }

      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
          List<String> items = new ArrayList<>();
          items.add("View details");

          String currentRole = worksiteDB.getLoggedUserRole(wid);
          UserDB userDB = UserDB.getInstance(holder.itemView.getContext());

          try {
            User loggedUser = userDB.getLoggedUser();

            if (!user.getEmail().equals(loggedUser.getEmail())) {
              // ADMIN only
              if (currentRole.equals("ADMIN")) {
                items.add("Change role/permissions");
                items.add("Kick " + user.getFirstName());
              }

              // SUPERVISOR only
              else if (!userRole.equals("ADMIN") && currentRole.equals("SUPERVISOR")) {
                items.add("Change role/permissions");
              }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle(user.getFirstName() + " " + user.getLastName() + ": User Options");
            builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {

              // on context menu click
              @Override
              public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                  case 0:
                    holder.itemView.performClick();
                    break;

                  case 1:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.itemView.getContext());
                    alertDialog.setTitle("Change Role to:");

                    String[] items = {"Supervisor", "Worker"};

                    int checkedItem = 1;
                    for (int i = 0; i < items.length; i++) {
                      if (userRole.equals(items[i].toUpperCase())) {
                        checkedItem = i;
                        break;
                      }
                    }

                    alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                          case 0:
                            worksiteDB.updateRole(wid, user.getEmail(), "SUPERVISOR");
                            break;
                          case 1:
                            worksiteDB.updateRole(wid, user.getEmail(), "WORKER");
                            break;
                        }

                        notifyDataSetChanged();
                        dialog.dismiss();
                      }
                    });

                    AlertDialog alert = alertDialog.create();
                    alert.show();
                    break;

                  case 2:
                    worksiteDB.kickUser(user.getEmail(), wid);
                    mData.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mData.size());
                    break;
                }
              }
            });

            builder.show();
          } catch(Exception err) {}

          return true;
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
      TextView heading, caption, status;
      ImageView avatar, statusCircle;

      ViewHolder(View itemView) {
        super(itemView);
        heading = itemView.findViewById(R.id.heading);
        caption = itemView.findViewById(R.id.caption);
        status = itemView.findViewById(R.id.status);
        statusCircle = itemView.findViewById(R.id.statusCircle);
        avatar = itemView.findViewById(R.id.avatar);

        avatar.setVisibility(View.GONE);
        itemView.setOnClickListener(this);
      }

      @Override
      public void onClick(View view) {
      }
    }
  }