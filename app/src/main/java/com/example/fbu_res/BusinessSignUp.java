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
import com.example.fbu_res.models.Business;
import com.example.fbu_res.models.Consumer;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class BusinessSignUp extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_sign);

        final EditText etName = findViewById(R.id.etName);
        final EditText etUsername = findViewById(R.id.etUsername);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etPhoneNum = findViewById(R.id.etPhoneNum);
        final EditText etAdd1 = findViewById(R.id.etAdd1);
        final EditText etAdd2 = findViewById(R.id.etAdd2);
        final EditText etZipcode = findViewById(R.id.etZipcode);
        final EditText etCity = findViewById(R.id.etCity);
        final EditText etState = findViewById(R.id.etState);
        final EditText etCountry = findViewById(R.id.etCountry);


        final Button signupBtn = findViewById(R.id.btnSignUp);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(etName.getText().toString(), etUsername.getText().toString(),
                        etPassword.getText().toString(), etEmail.getText().toString()
                        , etPhoneNum.getText().toString(), etAdd1.getText().toString()
                       , etAdd2.getText().toString(), etZipcode.getText().toString()
                        , etCity.getText().toString(), etState.getText().toString()
                        , etCountry.getText().toString());
            }
        });
    }

    private void signUp(String name, String username, String password, String email, String phoneNum
                    , String add1, String add2, String zipcode, String city, String state, String country) {
        Business user = new Business();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setDisplayname(name);
        user.setPhonenumber(phoneNum);
        Address address = new Address();
        address.setAddressline1(add1);
        address.setAddressline2(add2);
        address.setZipcode(zipcode);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setName(name);
        user.setAddress(address);
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
