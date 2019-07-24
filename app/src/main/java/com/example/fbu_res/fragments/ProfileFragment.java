package com.example.fbu_res.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fbu_res.LoginActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.models.Consumer;
import com.parse.Parse;
import com.parse.ParseUser;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private Consumer user;
    private ImageView ivProfileImage;
    private TextView tvDisplayname;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setting up the current user
        user = (Consumer) ParseUser.getCurrentUser();

        // showing the profile of the user
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvDisplayname = (TextView) view.findViewById(R.id.tvDisplayname);

        if(user.getProfileImage() != null){
            Glide.with(getContext()).load(user.getProfileImage().getUrl()).into(ivProfileImage);
        }
        tvDisplayname.setText(user.getDisplayname());

        // logout button functionality
        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    private void logout(){
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }

}
