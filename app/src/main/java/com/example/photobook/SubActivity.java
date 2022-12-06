package com.example.photobook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SubActivity extends AppCompatActivity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sub);


        }


        public void make(View view) {
            Intent intent = new Intent(SubActivity.this, Manual.class);
            startActivity(intent);
        }

        public void imgview(View view) {
            Intent intent = new Intent(SubActivity.this, Result.class);
            startActivity(intent);
        }



}
