package com.example.habtrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends AppCompatActivity {
    private ArrayList<Map<String, Object>> habitItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        //initialize top bar color
        final Window window = getWindow();
        final CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.main_layout);
        cl.setBackgroundColor(ContextCompat.getColor(this, R.color.purple));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple));
        window.getDecorView().setSystemUiVisibility(
                cl.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //enter animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn.setStartOffset(100);

        //get intent data
        Bundle extras = getIntent().getExtras();

        //profile pic
        final CircleImageView profilePic = (CircleImageView) findViewById(R.id.profile_pic);
        byte[] b = extras.getByteArray("pfp");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        profilePic.setImageBitmap(bmp);
        profilePic.startAnimation(fadeIn);

        //username
        final String usrStr = extras.getString("username");
        final TextView username = findViewById(R.id.username);
        username.setText((usrStr != null)? usrStr : "Username");
        username.startAnimation(fadeIn);

        //bio
        String bioStr = extras.getString("bio");
        final TextView bio = findViewById(R.id.bio);
        bio.setText((bioStr != null)? bioStr : "Bio");
        bio.startAnimation(fadeIn);

        //number of friends
        String numFriends = extras.getString("numFriends");
        final TextView friends = findViewById(R.id.friends);
        friends.setText((numFriends != null)? numFriends + " friends" : "0 friends");
        friends.startAnimation(fadeIn);

        //number of habits
        String numHabits = extras.getString("numHabits");
        final TextView totalHabits = findViewById(R.id.num_habits);
        totalHabits.setText((numHabits != null)? numHabits + " habits" : "0 habits");
        totalHabits.startAnimation(fadeIn);

        //user's habits
        habitItems = new ArrayList<>();
        habitItems.add(new HashMap<String, Object>());
        habitItems.add(new HashMap<String, Object>());
        habitItems.add(new HashMap<String, Object>());
        habitItems.add(new HashMap<String, Object>());

        //initialize wall RecyclerView
        RecyclerView rv = (RecyclerView) findViewById(R.id.habits);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RecyclerView.Adapter<WallAdapter.WallViewHolder> adapter = new WallAdapter(habitItems, llm);
        rv.setAdapter(adapter);

        //friend option button
        ConstraintLayout friendOption = (ConstraintLayout) findViewById(R.id.friend_option);
        friendOption.startAnimation(fadeIn);

        //back button
        final ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //options button
        final ImageButton options = (ImageButton) findViewById(R.id.options);

        //fade top on scroll
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alphaVal = 1.0f - Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange());

                back.setAlpha(alphaVal);
                options.setAlpha(alphaVal);
                profilePic.setAlpha(alphaVal);
                username.setAlpha(alphaVal);
                bio.setAlpha(alphaVal);
                friends.setAlpha(alphaVal);
                totalHabits.setAlpha(alphaVal);

                if (alphaVal == 0.0) {
                    cl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.background));
                    window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background));

                    int uiFlag = getApplicationContext().getResources().getConfiguration().uiMode
                            & Configuration.UI_MODE_NIGHT_MASK;
                    int darkFlag = Configuration.UI_MODE_NIGHT_YES;
                    window.getDecorView().setSystemUiVisibility((uiFlag == darkFlag)?
                            cl.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR:
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                else {
                    cl.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));
                    window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));
                    window.getDecorView().setSystemUiVisibility(
                            cl.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        });
    }
}