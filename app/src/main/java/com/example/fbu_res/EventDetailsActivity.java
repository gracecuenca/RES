package com.example.fbu_res;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fbu_res.adapters.EventDetailsAdapter;
import com.example.fbu_res.fragments.GroupFragment;
import com.example.fbu_res.models.User;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {

    // event to display
    Event event;
    List<Object> groups;
    DatabaseReference RootRef;


    final int PICK_PHOTO_CODE = 1046;
    File photoFileName;
    ParseFile pf;

    // event attributes
    EventDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        bindDataToAdapter();

        ImageView back = findViewById(R.id.ivBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void bindDataToAdapter() {

        // unwrapping the event sent by the intent and initializing attributes
        event = (Event) Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));

        RecyclerView rvDetails = (RecyclerView) findViewById(R.id.rvDetails);

        groups = new ArrayList<>();
        // Create adapter passing in the sample user data
        adapter = new EventDetailsAdapter(groups, event);
        // Attach the adapter to the recyclerview to populate items
        rvDetails.setAdapter(adapter);
        // Set layout manager to position the items
        rvDetails.setLayoutManager(new LinearLayoutManager(this));

        RootRef = FirebaseDatabase.getInstance().getReference();

        getGroups();

        FloatingActionButton create = findViewById(R.id.fabEventGroup);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewGroup(v);
            }
        });

        if(((User) ParseUser.getCurrentUser()).getType().equals("User")) {
            ((ViewGroup) create.getParent()).removeView(create);
        }
    }

    public void requestNewGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.MyCustomTheme);

        View viewCreate = view.getRootView();
        builder.setView(viewCreate);

        final EditText groupNameField = viewCreate.findViewById(R.id.etGroupName);


        RoundedImageView ivGetPicture = viewCreate.findViewById(R.id.ivGroupImage);
        ivGetPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for picking a photo from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.
                if (intent.resolveActivity(EventDetailsActivity.this.getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(intent, PICK_PHOTO_CODE);
                }

            }
        });

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString() +
                        ParseUser.getCurrentUser().getUsername() + new Timestamp(System.currentTimeMillis());
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(EventDetailsActivity.this, "Please write more..."
                            , Toast.LENGTH_SHORT);
                } else {
                    createNewGroup(groupName, groupNameField.getText().toString());
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createNewGroup(final String groupName, String name) {
        RootRef.child("Groups").child(groupName.replaceAll("[^a-zA-Z0-9]", "")).setValue("")
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EventDetailsActivity.this, groupName + " is Created Succesfully", Toast.LENGTH_SHORT);
                        }
                    }
                });
        final Group newGroup = new Group();
        newGroup.setName(name);
        newGroup.setNumMembs(1);
        newGroup.setImage(conversionBitmapParseFile(drawableToBitmap(getResources().getDrawable(R.drawable.ic_launcher_background))));
        ParseUser user = ParseUser.getCurrentUser();
        newGroup.addMember(user);
        newGroup.setType("Event");
        newGroup.setAssociatedEvent(event);
        newGroup.setOwner(user);
        ParseUser owner = event.getOwner();
        owner.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                newGroup.setOfficial(object.getUsername().equals(ParseUser.getCurrentUser().getUsername()));
                newGroup.setChannelName(groupName.replaceAll("[^a-zA-Z0-9]", ""));
                newGroup.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("Group Fragment", "Saved succesfully");
                    }
                });
                groups.add(newGroup);
            }
        });

        adapter.notifyDataSetChanged();
    }

    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
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

    public void getGroups() {
        // Define the class we would like to query
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.include("associated_event");
        query.whereEqualTo("associated_event", event);
        query.whereNotEqualTo("members", user);
        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if (e == null) {
                    groups.add("");
                    adapter.notifyItemInserted(0);
                    for(int i = 0; i < objects.size(); ++i) {
                        groups.add(objects.get(i));
                        adapter.notifyItemInserted(i+1);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
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

            ImageView ivPreview = findViewById(R.id.ivGroupImage);
            ivPreview.setImageBitmap(selectedImage);
            photoFileName = new File(getRealPathFromURI(this.getApplicationContext(), photoUri));
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
}
