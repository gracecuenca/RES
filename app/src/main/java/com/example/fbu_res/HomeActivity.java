package com.example.fbu_res;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fbu_res.fragments.BusinessGroupsFragment;
import com.example.fbu_res.fragments.GroupFragment;
import com.example.fbu_res.fragments.HomeFragment;
import com.example.fbu_res.fragments.ProfileFragment;
import com.example.fbu_res.fragments.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        // bottomNavigationView.setItemIconTintList(null);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.action_groups:
                        if (ParseUser.getCurrentUser().get("type").equals("Consumer")) {
                            fragment = new GroupFragment();
                        } else {
                            fragment = new BusinessGroupsFragment();
                        }
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection to home
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.directMessage:
                directMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void directMessage(){
        Intent i = new Intent(this.getApplicationContext(), DirectMessageActivity.class);
        startActivity(i);
    }
}
