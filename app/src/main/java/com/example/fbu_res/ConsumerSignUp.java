package com.example.fbu_res;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbu_res.models.Address;
import com.example.fbu_res.models.Consumer;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ConsumerSignUp extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_sign);

        final EditText etName = findViewById(R.id.etName);
        final EditText etUsername = findViewById(R.id.etUsername);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etPhoneNum = findViewById(R.id.etPassword);




        final Button signupBtn = findViewById(R.id.btnSignUp);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(etName.getText().toString(), etUsername.getText().toString(),
                        etPassword.getText().toString(), etEmail.getText().toString()
                , etPhoneNum.getText().toString());
            }
        });
    }

    private void signUp(String name, String username, String password, String email, String phoneNum) {
        Consumer user = new Consumer();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setKeyDisplayname(name);
        user.setKeyPhonenumber(phoneNum);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignUpActivity", "Signup successful.");
                    Intent intent = new Intent(ConsumerSignUp.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("SignUpActivity", "Signup failure");
                    e.printStackTrace();
                }
            }
        });
    }



}
