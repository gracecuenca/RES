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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class AddEventActivity extends AppCompatActivity {

    public static final String APP_TAG = "AddEventActivity";

    // date regex
    private static final Pattern date =
            Pattern.compile("^((0|1)\\d{1})/((0|1|2)\\d{1})/((19|20)\\d{2})");
    public static final Pattern zipcode =
            Pattern.compile("^\\d{5}$");

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

    // photo
    ParseFile pf;

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
                setTextError(etName);
                validateNextButton();
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
                setTextError(etLocationName);
                validateNextButton();
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
                setTextError(etAddressLine1);
                validateNextButton();
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
                setTextError(etAddressLine2);
                validateNextButton();
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
                setTextError(etZipcode);
                setZipcodeError(etZipcode);
                validateNextButton();
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
                setTextError(etCity);
                validateNextButton();
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
                setTextError(etState);
                validateNextButton();
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
                setTextError(etCountry);
                validateNextButton();
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
                etDate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(isInputEmpty(etDate)) setTextError(etDate);
                        setDateError(etDate);
                        validateCreateButton();
                    }
                });
                etDescription = (EditText) findViewById(R.id.etDescription);
                etDescription.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(isInputEmpty(etDescription)) setTextError(etDescription);
                        validateCreateButton();
                    }
                });

                ivPreview = (ImageView) findViewById(R.id.ivPreview);

                btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
                btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);

                // initially making the create button unclickable
                btnCreateEvent.setEnabled(false);
                btnCreateEvent.setClickable(false);

                btnSelectImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPickPhoto(v);
                    }
                });

                btnCreateEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Consumer user = (Consumer) ParseUser.getCurrentUser();

                        final Address address = new Address();
                        address.setName(etLocationName.getText().toString());
                        address.setAddressline1(etAddressLine1.getText().toString());
                        address.setAddressline2(etAddressLine2.getText().toString());
                        address.setCity(etCity.getText().toString());
                        address.setState(etState.getText().toString());
                        address.setZipcode(etZipcode.getText().toString());
                        address.setCountry(etCountry.getText().toString());

                        address.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                final Event event = new Event();

                                // setting the attributes for an event
                                event.setName(etName.getText().toString());
                                event.setDate(etDate.getText().toString());
                                event.setDescription(etDescription.getText().toString());
                                event.setLocation(address);
                                event.setOwner(user);
                                // checking to see if the user uploaded a file
                                if(photoFile == null || ivPreview.getDrawable() == null){
                                    Log.e(APP_TAG, "No photo to submit");
                                    Toast.makeText(getApplicationContext(), "There is no photo!", Toast.LENGTH_LONG).show();
                                }
                                // event.setImage(new ParseFile(photoFile));
                                event.setImage(pf);
                                event.setOwner(ParseUser.getCurrentUser());
                                event.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        user.addCreatedEvents(event);
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
                                            event.setDistanceToUser(user.getLocation().distanceInMilesTo(address.getParseGeoPoint(Address.KEY_PIN)));
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
        return et.getText().toString().length() == 0;
    }

    // setting the error
    public void setTextError(EditText et){
        if(isInputEmpty(et)) et.setError("This field cannot be empty");
        else et.setError(null);
    }

    // setting date error
    public void setDateError(EditText et){
        if(!isCorrectDate(et)){
            et.setError("Please format valid date as: mm/dd/yyyy");
            return;
        }
        et.setError(null);
    }

    // setting zipcode error
    public void setZipcodeError(EditText et){
        if(!isCorrectZipcde(et)){
            et.setError("Please format valid zipcode as: xxxxx");
            return;
        }
        et.setError(null);
    }

    // does the input text match the correct date format
    public boolean isCorrectDate(EditText et){
        return date.matcher(et.getText().toString()).matches() &&
                Integer.parseInt(et.getText().toString().substring(6)) >= Calendar.getInstance().get(Calendar.YEAR);
    }

    // is this a correct zipcode format
    public boolean isCorrectZipcde(EditText et){
        return zipcode.matcher(et.getText().toString()).matches();
    }

    public void validateCreateButton(){
        if(!isInputEmpty(etDate) && isCorrectDate(etDate) && !isInputEmpty(etDescription) &&
        ivPreview.getDrawable() != null){
            btnCreateEvent.setClickable(true);
            btnCreateEvent.setEnabled(true);
        }else{
            btnCreateEvent.setEnabled(false);
            btnCreateEvent.setClickable(false);
        }
    }

    public void validateNextButton(){
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

            // compression
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final byte[] d = stream.toByteArray();

            pf = new ParseFile(d);
            pf.saveInBackground();

            ivPreview.setImageBitmap(selectedImage);
            photoFileName = getRealPathFromURI(getApplicationContext(), photoUri);
            photoFile = new File(photoFileName);
            validateCreateButton();
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
