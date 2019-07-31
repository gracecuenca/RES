package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fbu_res.models.Address;
import com.example.fbu_res.models.Consumer;
import com.example.fbu_res.models.Event;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    public static final String APP_TAG = "AddEventActivity";

    // fields that are required by user when creating an event
    EditText etName;
    EditText etLocationName;
    EditText etAddressLine1;
    EditText etAddressLine2;
    EditText etZipcode;
    EditText etCity;
    EditText etState;
    EditText etCountry;
    EditText etDate;
    EditText etDescription;
    ImageView ivPreview;

    // next button
    Button btnNext;
    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public String photoFileName;
    File photoFile;

    // button to launch gallery and select image
    Button btnSelectImage;

    // button to officially create the event and add to relation
    Button btnCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // clear cache before loading image into preview
        // deleteCache(this);

        // TODO -- possible to create a single text watcher that validates emptiness on inputs

        etName = (EditText) findViewById(R.id.etName);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isInputEmpty(etName)) setTextError(etName);
                validateButton();
            }
        });
        etLocationName = (EditText) findViewById(R.id.etLocationName);
        etLocationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isInputEmpty(etLocationName)) setTextError(etLocationName);
                validateButton();
            }
        });
        etAddressLine1 = (EditText) findViewById(R.id.etAdressline1);
        etAddressLine1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isInputEmpty(etAddressLine1)) setTextError(etAddressLine1);
                validateButton();
            }
        });
        etAddressLine2 = (EditText) findViewById(R.id.etAddressline2);
        etAddressLine2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isInputEmpty(etAddressLine2)) setTextError(etAddressLine2);
                validateButton();
            }
        });
        etZipcode = (EditText) findViewById(R.id.etZipcode);
        etZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isInputEmpty(etZipcode)) setTextError(etZipcode);
                validateButton();
            }
        });
        etCity = (EditText) findViewById(R.id.etCity);
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isInputEmpty(etCity)) setTextError(etCity);
                validateButton();
            }
        });
        etState = (EditText)findViewById(R.id.etState);
        etState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isInputEmpty(etState)) setTextError(etState);
                validateButton();
            }
        });
        etCountry = (EditText) findViewById(R.id.etCountry);
        etCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isInputEmpty(etCountry)) setTextError(etCountry);
                validateButton();
            }
        });

        // initially making the state of the button unclickable
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setEnabled(false);
        btnNext.setClickable(false);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_add_event_next);

                // setting up the rest of the attributes
                etDate = (EditText) findViewById(R.id.etDate);
                etDescription = (EditText) findViewById(R.id.etDescription);
                btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
                btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);

                Log.d("lol", "made it here");

                btnSelectImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPickPhoto(v);
                    }
                });

                btnCreateEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // creation of the event

                        // getting the current business in order to add to the relation
                        final Consumer user = (Consumer) ParseUser.getCurrentUser();

                        final Address address = new Address();
                        address.setName(etLocationName.getText().toString());
                        address.setAddressline1(etAddressLine1.getText().toString());
                        address.setAddressline2(etAddressLine2.getText().toString());
                        address.setCity(etCity.getText().toString());
                        address.setState(etState.getText().toString());
                        address.setZipcode(etZipcode.getText().toString());
                        address.setCountry(etCountry.getText().toString());

                        // TODO -- find a way to get the geopoint of a business specified location

                        address.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Event event = new Event();

                                // setting the attributes for an event
                                event.setName(etName.getText().toString());
                                event.setDate(etDate.getText().toString()); // TODO -- fix the date method
                                event.setDescription(etDescription.getText().toString());
                                event.setLocation(address);
                                event.setOwner(user);
                                // checking to see if the user uploaded a file
                                if(photoFile == null || ivPreview.getDrawable() == null){
                                    Log.e(APP_TAG, "No photo to submit");
                                    Toast.makeText(getApplicationContext(), "There is no photo!", Toast.LENGTH_LONG).show();
                                }
                                event.setImage(new ParseFile(photoFile));
                                event.setOwner(ParseUser.getCurrentUser());
                                event.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(getApplicationContext(), "successfully created the event", Toast.LENGTH_SHORT).show();
                                        setContentView(R.layout.activity_add_event);
                                        String strAddresss = address.getAddressline1() + " "+
                                                address.getAddressline2() + ", " +
                                                address.getCity() + ", " + address.getState()+ " "+
                                                address.getZipcode() + ", "+
                                                address.getCountry()+ " ";
                                        Geocoder geocoder = new Geocoder(getApplicationContext());
                                        List<android.location.Address> addresses;
                                        try{
                                            addresses = geocoder.getFromLocationName(strAddresss, 5);
                                            android.location.Address loc = addresses.get(0);
                                            address.setPin(new ParseGeoPoint(loc.getLatitude(), loc.getLongitude()));
                                        }catch (Exception eo){
                                            eo.printStackTrace();
                                        }

                                    }
                                });
                            }
                        });

                    }
                });


            }
        });

    }

    // does the input text have anything in it
    public boolean isInputEmpty(EditText et){
        if(et.getText().toString().length() == 0){
            return true;
        }
        return false;
    }

    // setting the error
    public void setTextError(EditText et){
        if(isInputEmpty(et)) et.setError("This field cannot be empty");
        else et.setError(null);
    }

    public void validateButton(){
        if(!isInputEmpty(etName) && !isInputEmpty(etLocationName) && !isInputEmpty(etAddressLine1) &&
        !isInputEmpty(etAddressLine2) && !isInputEmpty(etZipcode) && !isInputEmpty(etCity) &&
        !isInputEmpty(etState) && !isInputEmpty(etCountry)){
            btnNext.setClickable(true);
            btnNext.setEnabled(true);
        } else{
            btnNext.setClickable(false);
            btnNext.setEnabled(false);
        }
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Load the selected image into a preview
            ivPreview = (ImageView) findViewById(R.id.ivPreview);
            ivPreview.setImageBitmap(selectedImage);
            photoFileName = getRealPathFromURI(getApplicationContext(), photoUri);
            photoFile = new File(photoFileName);
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // deleting cache functions
    public static void deleteCache(Context context){
        try{
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir){
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
