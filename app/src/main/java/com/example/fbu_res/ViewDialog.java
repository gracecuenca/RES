package com.example.fbu_res;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewDialog {
    Activity activity;
    Dialog dialog;
    Context context;
    // we need the context else we can not create the dialog so get context in constructor
    public ViewDialog(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.custom_loading_layout);

        //...initialize the imageView form inflated layout
        ImageView gifImageView = dialog.findViewById(R.id.ivLoading);
        Glide.with(context).asGif().load(R.drawable.loading).into(gifImageView);
        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog(){
        dialog.dismiss();
    }


}
