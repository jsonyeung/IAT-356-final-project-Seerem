package com.example.seeremapp.misc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.seeremapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class InviteDialog extends AppCompatDialogFragment {
  private String inviteCode;

  public InviteDialog(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflator = getActivity().getLayoutInflater();

    View view = inflator.inflate(R.layout.invite_modal, null);
    ((TextView) view.findViewById(R.id.inviteCode)).setText(inviteCode);

    builder.setView(view)
           .setTitle("Invite Code");

    return builder.create();
  }
}
