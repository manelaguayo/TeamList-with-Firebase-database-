package com.manelaguayo.todolist;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseTestActivity extends AppCompatActivity {

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test2);

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        mDatabase = FirebaseDatabase.getInstance().getReference();

      /*  mDatabase.child("users").child(userId).child("name").setValue(userName);
        mDatabase.child("users").child(userId).child("mail").setValue(userEmail);
        String key= mDatabase.child("users").child(userId).child("teams").push().getKey();
        mDatabase.child("users").child(userId).child("teams").child(item.getTitle()).setValue(true);

        //TEAMS

        mDatabase.child("teams").child(item.getTitle()).child("url").setValue(item.getUrl());*/

    }
}
