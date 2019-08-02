package com.example.fbu_res.fragments;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.fbu_res.R;
import com.example.fbu_res.adapters.GroupFragmentPagerAdapter;
import com.example.fbu_res.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

public class GroupFragment extends Fragment {

    DatabaseReference RootRef;
    int RESULT_OK = 291;
    int REQUEST_CODE = 47;
    final int PICK_PHOTO_CODE = 1046;
    File photoFileName;
    ParseFile pf;
    View viewCreate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Set toolbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.searchToolbar);
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        setHasOptionsMenu(true);

// Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.Viewpager);
        GroupFragmentPagerAdapter pagerAdapter = new GroupFragmentPagerAdapter(getFragmentManager());
        pagerAdapter.addFragment(new MyGroupsFragment(), "My Groups");
        pagerAdapter.addFragment(new InterestGroupFragment(), "Interest Groups");
        pagerAdapter.addFragment(new EventGroupFragment(), "Event Groups");

        LayoutInflater factory = LayoutInflater.from(GroupFragment.this.getContext());
        viewCreate = factory.inflate(R.layout.fragment_creategroup, null);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        RootRef = FirebaseDatabase.getInstance().getReference();
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void requestNewGroup() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.MyCustomTheme);

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
                if (intent.resolveActivity(GroupFragment.this.getContext().getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(intent, PICK_PHOTO_CODE);
                }

            }
        });

        // ivGetPicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_defaultprofilepic));
        //ivGetPicture.setLayoutParams(new LinearLayout.LayoutParams(200, 400));
        // builder.setView(ivGetPicture);
        // ivGetPicture.setId(R.id.ivProfileImg);


        // ivGetPicture.setCornerRadius((float) 30);
        // ivGetPicture.setBorderWidth((float) 2);


        /*ivGetPicture.setOnClickListener(new View.OnClickListener() {
        });*/

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString() +
                        ParseUser.getCurrentUser().getUsername() + new Timestamp(System.currentTimeMillis());
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(GroupFragment.this.getContext(), "Please write more..."
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
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(GroupFragment.this.getContext(), groupName + " is Created Succesfully", Toast.LENGTH_SHORT);
                        }
                    }
                });
        Group newGroup = new Group();
        newGroup.setName(name);
        newGroup.setNumMembs(1);
        if(pf == null) {
            newGroup.setImage(conversionBitmapParseFile(drawableToBitmap(getResources().getDrawable(R.drawable.ic_launcher_background))));
        } else {
            newGroup.setImage(pf);
        }
        ParseUser user = ParseUser.getCurrentUser();
        newGroup.addMember(user);
        newGroup.setType("Interests");
        newGroup.setOwner(ParseUser.getCurrentUser());
        newGroup.setChannelName(groupName.replaceAll("[^a-zA-Z0-9]", ""));
        newGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Group Fragment", "Saved succesfully");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), photoUri);
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

            ImageView ivPreview = viewCreate.findViewById(R.id.ivGroupImage);
            ivPreview.setImageBitmap(selectedImage);
            photoFileName = new File(getRealPathFromURI(this.getContext().getApplicationContext(), photoUri));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newGroup:
                requestNewGroup();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
}
