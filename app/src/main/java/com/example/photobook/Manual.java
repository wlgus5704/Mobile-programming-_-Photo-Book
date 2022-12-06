package com.example.photobook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Manual extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
    }


    public void menualbtn(View view){
        Intent intent = new Intent(Manual.this, Drawing_Activity.class);
        startActivity(intent);
    }

}
