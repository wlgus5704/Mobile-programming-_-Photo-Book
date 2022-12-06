package com.example.photobook;

import static com.example.photobook.Drawing_Activity.bitmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Result extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ImageView resultimg = findViewById(R.id.resultimg);
        resultimg.setImageBitmap(bitmap);

    }

    public void back(View v){
        Intent intent = new Intent(Result.this, SubActivity.class);
        startActivity(intent);
    }


}
