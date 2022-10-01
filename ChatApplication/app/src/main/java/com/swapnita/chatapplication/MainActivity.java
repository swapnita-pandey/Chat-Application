package com.swapnita.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    Button sendButton;
    EditText message, senderName;
    ListView listview;
    DatabaseReference myRef;
    ArrayAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Let's Chat");

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://swapp-135e1-default-rtdb.asia-southeast1.firebasedatabase.app");
        myRef = database.getReference();

        sendButton = findViewById(R.id.sendButton);
        message = findViewById(R.id.message);
        senderName = findViewById(R.id.senderName);
        listview = findViewById(R.id.listview);

        ArrayList<String> messageList = new ArrayList<String>();
        messageAdapter = new ArrayAdapter<String>(this, R.layout.listitem, messageList);
        listview.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sender = senderName.getText().toString();
                String msg = message.getText().toString();
                if (TextUtils.isEmpty(sender)) {
                    Toast.makeText(MainActivity.this, "Please write your name!", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(MainActivity.this, "Please write a message!", Toast.LENGTH_LONG).show();
                } else {
                    //messageAdapter.add(sender + ": \n\n" + msg);

                    myRef.push().setValue(sender + ": \n\n" + msg);

                    message.setText("");
                }

            }
        });
        loadMessage();
    }

    private void loadMessage() {

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                messageAdapter.add(snapshot.getValue().toString());

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }
    }


    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_option) {
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        return true;
    }



}