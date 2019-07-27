package com.programmerbaper.skripsipembeli.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.programmerbaper.skripsipembeli.R;

public class FeedBackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        //TODO GET ID TRANSAKSI FROM GET EXTRAS HERE

        Button button = findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO UPDATE RATING HERE
                Intent intent = new Intent(FeedBackActivity.this,PilihPedagangActivity.class);
                startActivity(intent);
            }
        });
    }
}
