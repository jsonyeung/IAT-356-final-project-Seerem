package com.example.seeremapp.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seeremapp.R;
import com.example.seeremapp.UserProfileActivity;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Document;
import com.example.seeremapp.database.containers.User;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
  private List<Document> mData;
  private LayoutInflater mInflater;
  private String role;

  // data is passed into the constructor
  public DocumentAdapter(Context context, List<Document> data, String role) {
    this.mInflater = LayoutInflater.from(context);
    this.mData = data;
    this.role = role;
  }

  // inflates the row layout from xml when needed
  @Override
  public DocumentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.card_li, parent, false);
    return new DocumentAdapter.ViewHolder(view);
  }

  // binds the data to the TextView in each row
  @Override
  public void onBindViewHolder(DocumentAdapter.ViewHolder holder, int position) {
    Document doc = mData.get(position);

    if (role.equals("ADMIN")) {
      holder.delete.setVisibility(View.VISIBLE);
    }

    if (doc.getType().equals("URL")) {
      holder.heading.setText(doc.getPath());
      holder.caption.setText(doc.getType());

      // open URL using web intent
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(doc.getPath()));
            view.getContext().startActivity(i);
          } catch(Exception err) { Log.i("test", err.getMessage()); }
        }
      });

    } else if (doc.getType().equals("DOC")) {
      holder.heading.setText(doc.getName());
      holder.caption.setText(doc.getType());

      // open document by asking for an implicit intent
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          try {
            File file = new File(Uri.parse(doc.getPath()).getPath());

            Log.e("test", "exists: " + file.exists());
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file),"application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent intent = Intent.createChooser(target, "Open File");
            try {
              view.getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
              Toast.makeText(view.getContext(), "You do not have an app to read the following document", Toast.LENGTH_LONG).show();
            }
          } catch(Exception err) { Log.i("test", err.getMessage()); }
        }
      });
    }

    // delete document
    holder.delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        WorksiteDB worksiteDB = WorksiteDB.getInstance(view.getContext());
        worksiteDB.deleteDocument(doc.getId(), doc.getPath());
        mData.remove(position);
        notifyItemRangeChanged(position, mData.size());
        notifyDataSetChanged();
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
    ImageView avatar, delete;

    ViewHolder(View itemView) {
      super(itemView);
      heading = itemView.findViewById(R.id.heading);
      caption = itemView.findViewById(R.id.caption);
      avatar = itemView.findViewById(R.id.avatar);
      delete = itemView.findViewById(R.id.closeButton);

      avatar.setVisibility(View.GONE);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
    }
  }
}
