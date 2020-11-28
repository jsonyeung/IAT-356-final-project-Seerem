package com.example.seeremapp.misc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seeremapp.R;
import com.example.seeremapp.database.WorksiteDB;

import net.glxn.qrgen.android.QRCode;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.FileProvider;

public class InviteDialog extends AppCompatDialogFragment {
  private String inviteCode;

  public InviteDialog(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.invite_modal, null);

    // set Invite Code
    TextView inviteView = view.findViewById(R.id.inviteCode);
    inviteView.setText(inviteCode);

    // generate & set QR Code
    ImageView inviteQR = view.findViewById(R.id.inviteQR);
    Bitmap QRBitmap = QRCode.from(inviteCode).bitmap();
    inviteQR.setImageBitmap(QRBitmap);

    builder.setView(view)
           .setTitle("Invite Code");

    // Share QR code on click
    inviteQR.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        shareBitmap(QRBitmap);
      }
    });

    return builder.create();
  }

  private void shareBitmap(@NonNull Bitmap bitmap)
  {
    //---Save bitmap to external cache directory---//
    // get cache directory
    File cachePath = new File(getActivity().getExternalCacheDir(), "my_images/");
    cachePath.mkdirs();

    // create png file
    File file = new File(cachePath, inviteCode + ".png");
    FileOutputStream fileOutputStream;
    try
    {
      fileOutputStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
      fileOutputStream.flush();
      fileOutputStream.close();

    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    }

    //---Share File---//
    // get file uri
    Uri myImageFileUri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".provider", file);

    // create a intent
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    // intent info
    intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {""});
    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[Request] Join our Seerem worksite!");
    intent.putExtra(android.content.Intent.EXTRA_TEXT, "Please join our worksite using the code: " + inviteCode);
    intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri);

    intent.setType("image/png");
    startActivity(Intent.createChooser(intent, "Share with"));
  }
}
