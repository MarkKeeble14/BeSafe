package com.example.besafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://besafe-55c8d.firebaseio.com/");
        DatabaseReference myRef = database.getReference("BeSafe");
        myRef.setValue("Hello, World!");

    }

    public void onClick(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }
}
