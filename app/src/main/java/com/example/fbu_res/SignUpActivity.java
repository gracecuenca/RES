package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button consumerBtn = findViewById(R.id.btnConsumer);
        consumerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, ConsumerSignUp.class);
                startActivity(i);
            }
        });

        Button businessBtn = findViewById(R.id.btnBusiness);
        businessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, BusinessSignUp.class);
                startActivity(i);
            }
        });
    }
}
