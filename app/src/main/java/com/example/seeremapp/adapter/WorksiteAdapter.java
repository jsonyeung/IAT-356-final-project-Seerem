package com.example.seeremapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seeremapp.R;
import com.example.seeremapp.WorksiteDashboardActivity;
import com.example.seeremapp.database.containers.Worksite;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class WorksiteAdapter extends RecyclerView.Adapter<WorksiteAdapter.ViewHolder> {
  private List<Worksite> mData;
  private LayoutInflater mInflater;

  // data is passed into the constructor
  public WorksiteAdapter(Context context, List<Worksite> data) {
    this.mInflater = LayoutInflater.from(context);
    this.mData = data;
  }

  // inflates the row layout from xml when needed
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.card_li, parent, false);
    return new ViewHolder(view);
  }

  // binds the data to the TextView in each row
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Worksite worksite = mData.get(position);
    holder.heading.setText(worksite.getCompany() + " — " + worksite.getWorksiteName());
    holder.caption.setText(worksite.getAddress());

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Context context = view.getContext();
        Intent i = new Intent(context, WorksiteDashboardActivity.class);
        i.putExtra("wid", worksite.getId());
        context.startActivity(i);
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