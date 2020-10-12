package com.nayeem_bd.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private TextView messageTextView;
    private Button sendButton;
    private EditText messageEditText;
    private ScrollView scrollView;
    DatabaseReference root;
    String user_name,group_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageEditText = findViewById(R.id.messageEditTextId);
        messageTextView = findViewById(R.id.messageTextViewId);
        sendButton = findViewById(R.id.sendButtonId);
        scrollView = findViewById(R.id.messageScrollViewId);

        group_name = getIntent().getExtras().get("group_name").toString();
        user_name = getIntent().getExtras().get("user_name").toString();

        setTitle(group_name);

        root = FirebaseDatabase.getInstance().getReference().child(group_name);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Map<String ,Object> unique_key = new HashMap<String ,Object>();
                String temp_key = root.push().getKey();
               // root.updateChildren(unique_key);

                Map<String ,Object> userMessage = new HashMap<String ,Object>();
                DatabaseReference userRef = root.child(temp_key);
                userMessage.put("name",user_name);
                userMessage.put("message",messageEditText.getText().toString());
                userRef.updateChildren(userMessage);

                messageEditText.setText("");
                messageEditText.requestFocus();
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_messages(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_messages(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void append_messages(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
            String Message = (String) ((DataSnapshot)i.next()).getValue();
            String User_name = (String) ((DataSnapshot)i.next()).getValue();

            messageTextView.append(User_name + " : "+ Message +"\n\n");

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }
}