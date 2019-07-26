package com.example.fbu_res;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

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
        user.setProfileImg(conversionBitmapParseFile(drawableToBitmap(getResources().getDrawable(R.drawable.ic_defaultprofilepic))));
        // user.saveInBackground();
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

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }
}
