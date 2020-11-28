package com.example.seeremapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.seeremapp.R;
import com.example.seeremapp.adapter.ChatAdapter;
import com.example.seeremapp.adapter.UsersAdapter;
import com.example.seeremapp.database.UserDB;
import com.example.seeremapp.database.WorksiteDB;
import com.example.seeremapp.database.containers.Chat;
import com.example.seeremapp.database.containers.User;
import com.example.seeremapp.database.containers.Worksite;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorksiteDashboardChatFragment extends Fragment {
  private UserDB userDB;
  private WorksiteDB worksiteDB;
  private Worksite worksite;

  private List<Chat> messages = new ArrayList<>();
  private RecyclerView chatList;
  private ChatAdapter chatAdapter;
  private User user;

  public WorksiteDashboardChatFragment() {
    // Required empty public constructor
  }

  // TODO: Rename and change types and number of parameters
  public static WorksiteDashboardChatFragment newInstance(int wid) {
    WorksiteDashboardChatFragment fragment = new WorksiteDashboardChatFragment();
    Bundle args = new Bundle();
    args.putInt("WID", wid);
    fragment.setArguments(args);
    return fragment;
  }

  // FRAGMENT ACTIVITY
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    worksiteDB = WorksiteDB.getInstance(getContext());
    userDB = UserDB.getInstance(getContext());

    if (getArguments() != null) {
      int wid = getArguments().getInt("WID");
      worksite = worksiteDB.getWorksite(wid);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = (View) inflater.inflate(R.layout.fragment_worksite_dashboard_chat, container, false);

    // set references
    EditText messageInput = view.findViewById(R.id.chatInput);
    ImageButton messageSendButton = view.findViewById(R.id.chatButton);

    // send message functionality
    messageSendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
          worksiteDB.addLoggedUserChatMessage(worksite.getId(), message);

          messages.clear();
          messages.addAll(worksiteDB.getWorksiteChats(worksite.getId()));
          //  Toast.makeText(view.getContext(), "size: " + messages.size(), Toast.LENGTH_SHORT).show();

          chatAdapter.notifyDataSetChanged();
          if (messages.size() > 0) chatList.smoothScrollToPosition(messages.size()-1);
        }

        messageInput.setText("");
      }
    });

    // populate messages
    try {
      messages = worksiteDB.getWorksiteChats(worksite.getId());
      user = userDB.getLoggedUser();

      chatList = view.findViewById(R.id.chatList);
      chatAdapter = new ChatAdapter(getContext(), messages, user);
      LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getContext());

      chatList.getRecycledViewPool().setMaxRecycledViews(0, 0);
      chatList.setLayoutManager(chatLayoutManager);
      chatList.setAdapter(chatAdapter);

      if (messages.size() > 0) chatList.smoothScrollToPosition(messages.size()-1);
    } catch (Exception err) {}

    // Inflate the layout for this fragment
    return view;
  }


}
