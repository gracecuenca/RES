package com.example.fbu_res;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.models.Event;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EventDetailsActivity extends AppCompatActivity {

    // event to display
    Event event;

    // event attributes
    private ImageView ivImage;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // TODO -- setup customized actionbar/ toolbar

        // unwrapping the event sent by the intent and initializing attributes
        event = (Event) Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));

        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvLocation = (TextView) findViewById(R.id.tvLocation);

        // loading the information from the Event object into the view
        tvTitle.setText(event.getName());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        tvDate.setText(dateFormat.format(event.getDate()));
        tvLocation.setText(event.getLocationString());
        tvDescription.setText(event.getDescription());
        ParseFile image = event.getImage();
        if(image != null){
            Glide.with(getApplicationContext()).load(image.getUrl()).into(ivImage);
        }

    }
}
