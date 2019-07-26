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
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class BusinessSignUp extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_sign);

        final EditText etName = findViewById(R.id.etName);
        final EditText etUsername = findViewById(R.id.etUsername);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etPhoneNum = findViewById(R.id.etPhoneNum);
        final EditText etAdd1 = findViewById(R.id.etAddressLine1);
        final EditText etAdd2 = findViewById(R.id.etAddressline2);
        final EditText etZipcode = findViewById(R.id.etCity);
        final EditText etCity = findViewById(R.id.etCity);
        final EditText etState = findViewById(R.id.etState);
        final EditText etCountry = findViewById(R.id.etCountry);


        final Button signupBtn = findViewById(R.id.btnSignUp);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Address address = new Address();
                address.setAddressline1(etAdd1.getText().toString());
                address.setAddressline2(etAdd2.getText().toString());
                address.setZipcode(etZipcode.getText().toString());
                address.setCity(etCity.getText().toString());
                address.setState(etState.getText().toString());
                address.setCountry(etCountry.getText().toString());
                address.setName(etName.getText().toString());
                address.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        signUp(etName.getText().toString(), etUsername.getText().toString()
                                , etPassword.getText().toString(), etEmail.getText().toString()
                                , etPhoneNum.getText().toString(), address);
                    }
                });
            }
        });
    }

    private void signUp(String name, String username, String password, String email, String phoneNum
                    , Address address) {
        Consumer user = new Consumer();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setDisplayname(name);
        user.setPhonenumber(phoneNum);
        user.setAddress(address);
        user.setType("Business");
        user.saveInBackground();
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignUpActivity", "Signup successful.");
                    Intent intent = new Intent(BusinessSignUp.this, LoginActivity.class);
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
