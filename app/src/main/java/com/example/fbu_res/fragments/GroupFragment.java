package com.example.fbu_res.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.fbu_res.GroupMessagesActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.adapters.GroupFragmentPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseClassName;
import com.parse.ParseUser;
import com.pubnub.api.PubNub;

import java.sql.Timestamp;
import java.util.concurrent.Executor;

@ParseClassName("Group")
public class GroupFragment extends Fragment {

    PubNub mPubnub_DataStream;
    DatabaseReference RootRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
// Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.Viewpager);
        GroupFragmentPagerAdapter pagerAdapter = new GroupFragmentPagerAdapter(getFragmentManager(),
                getContext());
        viewPager.setAdapter(pagerAdapter);

        RootRef = FirebaseDatabase.getInstance().getReference();
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fabBtn = view.findViewById(R.id.fabNewGroup);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewGroup(v);
            }
        });

    }

    public void requestNewGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupFragment.this.getContext()
                , R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(GroupFragment.this.getContext());
        groupNameField.setHint("e.g. Ayyo's Group?");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString() +
                        ParseUser.getCurrentUser().getUsername() + new Timestamp(System.currentTimeMillis());
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(GroupFragment.this.getContext(), "Please write more..."
                            , Toast.LENGTH_SHORT);
                } else {
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private void createNewGroup(final String groupName) {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(GroupFragment.this.getContext(), groupName + " is Created Succesfully", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

}
