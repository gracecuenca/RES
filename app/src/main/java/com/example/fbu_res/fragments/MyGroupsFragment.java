package com.example.fbu_res.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fbu_res.R;
import com.parse.ParseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

public class MyGroupsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    PubNub mPubnub_DataStream;

    public static GroupFragment newInstance(int page) {
        return new GroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_groups, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        PNConfiguration config = new PNConfiguration();
        config.setPublishKey(getResources().getString(R.string.PUBNUB_SUBSCRIBE_KEY));
        config.setSubscribeKey(getResources().getString(R.string.PUBNUB_SUBSCRIBE_KEY));
        config.setUuid(ParseUser.getCurrentUser().getUsername());
        config.setSecure(true);
        mPubnub_DataStream = new PubNub(config);
    }
}
