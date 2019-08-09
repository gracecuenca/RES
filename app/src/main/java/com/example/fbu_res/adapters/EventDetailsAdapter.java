package com.example.fbu_res.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_res.GroupMessagesActivity;
import com.example.fbu_res.ProfileActivity;
import com.example.fbu_res.R;
import com.example.fbu_res.models.User;
import com.example.fbu_res.models.Event;
import com.example.fbu_res.models.Group;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class EventDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Object> objects;
    Context context;
    int REQUEST_CODE = 47;
    private final int DETAILS = 0, GROUPS = 1;
    Event event;

    public EventDetailsAdapter(List<Object> objects, Event event){
        this.objects = objects;
        this.event = event;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case DETAILS:
                View viewDetails = inflater.inflate(R.layout.item_details, viewGroup, false);
                viewHolder = new ViewHolderEventDetails(viewDetails);
                break;
            case GROUPS:
                View viewGroups = inflater.inflate(R.layout.item_group, viewGroup, false);
                viewHolder = new ViewHolderGroups(viewGroups);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                viewHolder = new ViewHolderGroups(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case DETAILS:
                ViewHolderEventDetails vhEventDetails = (ViewHolderEventDetails) viewHolder;
                configureViewHolderEventDetails(vhEventDetails, position);
                break;
            case GROUPS:
                ViewHolderGroups vhGroups = (ViewHolderGroups) viewHolder;
                configureViewHolder2(vhGroups, position);
                break;
            default:
                ViewHolderGroups vh = (ViewHolderGroups) viewHolder;
                configureViewHolder2(vh, position);
                break;
        }    }

    private void configureViewHolderEventDetails(final ViewHolderEventDetails vhEventDetails, int position) {
        // loading the information from the Event object into the view
        vhEventDetails.tvTitle.setText(event.getName());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        vhEventDetails.tvDate.setText(dateFormat.format(event.getDate()));
        String add1, add2, city, state, zipcode, country;
        add1 = event.getLocation().getAddressline1() + "\n";
        if(event.getLocation().getAddressline2()==null || event.getLocation().getAddressline2().equals("")){
            add2 = "";
        }else{
            add2 = event.getLocation().getAddressline2()+"\n";
        }
        city = event.getLocation().getCity()+", ";
        state = event.getLocation().getState()+ " ";
        zipcode = event.getLocation().getZipcode() + "\n";
        country = event.getLocation().getCountry();
        String add = add1 + add2 + city + state + zipcode + country;
        vhEventDetails.tvLocation.setText(add);
        vhEventDetails.tvDescription.setText(event.getDescription());
        final User user = (User) event.getOwner();
        try {
            user.fetchIfNeeded();
            vhEventDetails.tvBusinessName.setText(user.getDisplayname());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        vhEventDetails.tvBusinessName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(vhEventDetails.itemView.getContext(), ProfileActivity.class);
                i.putExtra("objectId", user.getObjectId());
                v.getContext().startActivity(i);
            }
        });


        // businesses can't add events to their calendars/ itineraries
        if(((User)ParseUser.getCurrentUser()).getType().equals("Business")){
            vhEventDetails.btnAddToCalendar.setClickable(false);
            vhEventDetails.btnAddToCalendar.setEnabled(false);
            vhEventDetails.btnRemoveFromCalendar.setClickable(false);
            vhEventDetails.btnRemoveFromCalendar.setEnabled(false);
        }
        // but consumers can
        else if(((User)ParseUser.getCurrentUser()).getType().equals("Consumer")){
            final User currentUser = (User) ParseUser.getCurrentUser();
            vhEventDetails.btnAddToCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentUser.addInterestedEvent(event);
                    Toast.makeText(v.getContext(), event.getName()+ " has been added to itinerary " +
                            "under profile", Toast.LENGTH_SHORT).show();
                    vhEventDetails.btnAddToCalendar.setClickable(false);
                    vhEventDetails.btnAddToCalendar.setEnabled(false);
                    vhEventDetails.btnRemoveFromCalendar.setClickable(true);
                    user.addInterestedEvent(event);
                    user.addInterestedMap(event.getDate(), event);
                    user.saveInBackground();
                }
            });

            vhEventDetails.btnRemoveFromCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentUser.removeInterestedEvent(event);
                    Toast.makeText(v.getContext(), event.getName()+ " has been removed from itinerary " +
                            "under profile", Toast.LENGTH_SHORT).show();
                    vhEventDetails.btnRemoveFromCalendar.setClickable(false);
                    vhEventDetails.btnRemoveFromCalendar.setEnabled(false);
                    vhEventDetails.btnAddToCalendar.setClickable(true);
                    user.removeInterestedEvent(event);
                    user.removeInterestedMap(event.getDate(), event);
                    user.saveInBackground();
                }
            });

            // querying to see if the event is already in the list of the consumer's interested events
            ParseQuery q = currentUser.getInterestedEvents().getQuery();
            q.whereEqualTo(Event.KEY_NAME, event.getName());
            q.findInBackground(new FindCallback<User>() {
                @Override
                public void done(List items, ParseException e) {
                    if(items.size() == 1) {
                        vhEventDetails.btnAddToCalendar.setClickable(false);
                        vhEventDetails.btnAddToCalendar.setEnabled(false);
                        vhEventDetails.btnRemoveFromCalendar.setClickable(true);
                        vhEventDetails.btnRemoveFromCalendar.setEnabled(true);
                    }
                    else if(items.size() == 0) {
                        vhEventDetails.btnAddToCalendar.setClickable(true);
                        vhEventDetails.btnAddToCalendar.setEnabled(true);
                        vhEventDetails.btnRemoveFromCalendar.setClickable(false);
                        vhEventDetails.btnRemoveFromCalendar.setEnabled(true);
                    }
                }
            });

        }

    }

    private void configureViewHolder2(final ViewHolderGroups vhGroups, int position) {
        final Group group = (Group) objects.get(position);

        vhGroups.groupName.setText(group.getName());
        if(group.getImage()!=null){
            Glide.with(context).load(group.getImage().getUrl()).into(vhGroups.groupImage);
        }

        vhGroups.groupType.setText("");
        vhGroups.join.setText("Join");
        vhGroups.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setNumMembs(group.getNumMembs() + 1);
                group.addMember(ParseUser.getCurrentUser());
                Intent intent = new Intent(vhGroups.itemView.getContext(), GroupMessagesActivity.class);
                intent.putExtra("channel_name", group.getChannelName());
                ((Activity) vhGroups.itemView.getContext()).startActivityForResult(intent, REQUEST_CODE);
                objects.remove(group);
                notifyDataSetChanged();
            }
        });
        if(!group.getOfficial() && vhGroups.verified.getParent() != null) {
            ((ViewGroup) vhGroups.verified.getParent()).removeView(vhGroups.verified);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (objects.get(position) instanceof Group) {
            return GROUPS;
        }
        return DETAILS;
    }
    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolderGroups extends RecyclerView.ViewHolder{
        ImageView groupImage;
        TextView groupName;
        TextView groupType;
        Button join;
        ImageView verified;
        public ViewHolderGroups(View view){
            super(view);
            groupImage = view.findViewById(R.id.ivNewGroup);
            groupName = view.findViewById(R.id.tvDisplayname);
            groupType = view.findViewById(R.id.tvEventName);
            join = view.findViewById(R.id.btnJoin);
            verified = view.findViewById(R.id.ivVerified);
        }
    }

    public class ViewHolderEventDetails extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvLocation;
        private Button btnAddToCalendar;
        private Button btnRemoveFromCalendar;
        private TextView tvBusinessName;

        public ViewHolderEventDetails(View v) {
            super(v);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvDescription = (TextView) v.findViewById(R.id.tvDescription);
            tvLocation = (TextView) v.findViewById(R.id.tvLocation);
            btnAddToCalendar = (Button) v.findViewById(R.id.btnAddToCalendar);
            btnRemoveFromCalendar = (Button) v.findViewById(R.id.btnRemoveFromCalendar);
            tvBusinessName = v.findViewById(R.id.tvBusinessName);
        }
    }

    public void clear() {
        objects.clear();
        notifyDataSetChanged();
    }
}
