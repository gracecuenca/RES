package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.parse.ParseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

public class GroupMessagesActivity extends AppCompatActivity {

    PubNub mPubnub_DataStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messages);

        PNConfiguration config = new PNConfiguration();
        config.setPublishKey(getResources().getString(R.string.PUBNUB_SUBSCRIBE_KEY));
        config.setSubscribeKey(getResources().getString(R.string.PUBNUB_PUBLISH_KEY));
        config.setUuid(ParseUser.getCurrentUser().getUsername());
        config.setSecure(true);
        mPubnub_DataStream = new PubNub(config);
    }
}
