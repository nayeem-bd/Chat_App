package com.nayeem_bd.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button addGroupButton;
    private EditText addGroupEditText;
    private ListView listView;
    private ArrayList<String> groupList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private String Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addGroupButton = findViewById(R.id.addGroupButtonId);
        addGroupEditText = findViewById(R.id.addGroupId);
        listView = findViewById(R.id.listviewId);

        arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.listitem,R.id.listtextViewId,groupList);
        listView.setAdapter(arrayAdapter);

        user_check();
      //  request_username();

        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference(addGroupEditText.getText().toString());
                databaseReference.setValue("");
                addGroupEditText.setText("");
                addGroupEditText.requestFocus();
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(addGroupEditText.getText().toString());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    groupList.add(dataSnapshot1.getKey().toString());

                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("user_name",Name);
                intent.putExtra("group_name",adapterView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
    }

    private void user_check() {
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("user_name_key")){
            Name = sharedPreferences.getString("user_name_key","User not Found");
            if(Name.equals("User not Found")){
                request_username();
            }
        }
        else{
            request_username();
        }
    }

    private void request_username() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter UserName");
        final EditText inputName = new EditText(MainActivity.this);
        builder.setView(inputName);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Name = inputName.getText().toString().trim();
                if(Name.isEmpty()){
                    request_username();
                }
                SharedPreferences sharedPreferences = getSharedPreferences("userDetails",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_name_key",Name);
                editor.commit();
                Toast.makeText(MainActivity.this,"Welcome "+ Name,Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                request_username();
            }
        });
        builder.show();
    }
}