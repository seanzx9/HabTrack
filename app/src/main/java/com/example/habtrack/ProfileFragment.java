package com.example.habtrack;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private Map<String, Object> profData;
    private ArrayList<Map<String, Object>> profileItems;
    private ArrayList<Map<String, Object>> database; //test data

    public ProfileFragment() {}

    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //initialize top bar color
        final Window window = getActivity().getWindow();
        final CoordinatorLayout cl = (CoordinatorLayout) view.findViewById(R.id.main_layout);
        cl.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple));
        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.purple));
        window.getDecorView().setSystemUiVisibility(
                view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //initialize arraylist for profile habits
        profData = new HashMap<>();
        profileItems = new ArrayList<>();
        database = new ArrayList<>();
        generateTestData();
        loadData();

        //enter animation
        Animation enterTop = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        //settings button
        final ImageButton settings = (ImageButton) view.findViewById(R.id.settings);
        settings.setOnClickListener(this);

        //set profile picture
        final CircleImageView profilePic = (CircleImageView) view.findViewById(R.id.profile_pic);
        Drawable pfp;
        if (profData.get("pfp") != null && (int) profData.get("pfp") != R.drawable.default_pfp) {
            pfp = ContextCompat.getDrawable(getContext(), (int) profData.get("pfp"));
        }
        else {
            pfp = ContextCompat.getDrawable(getContext(), R.drawable.default_pfp);
            profilePic.setImageTintList(ColorStateList.valueOf(ContextCompat
                    .getColor(getContext(), R.color.white)));
        }
        profilePic.setImageDrawable(pfp);
        profilePic.setOnClickListener(this);
        profilePic.startAnimation(enterTop);

        //edit profile button
        final ImageButton editProfile = (ImageButton) view.findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(this);
        editProfile.startAnimation(enterTop);

        //set username
        final TextView username = (TextView) view.findViewById(R.id.username);
        String usrStr = (profData.get("username") != null)? profData.get("username").toString() : "Username";
        username.setText(usrStr);
        username.setOnClickListener(this);
        username.startAnimation(enterTop);

        //set bio
        final TextView bio = (TextView) view.findViewById(R.id.bio);
        String bioStr = (profData.get("bio") != null)? profData.get("bio").toString() : "Bio";
        bio.setText(bioStr);
        bio.setOnClickListener(this);
        bio.startAnimation(enterTop);

        //set number of friends
        final TextView friends = (TextView) view.findViewById(R.id.friends);
        int numFriends = (profData.get("friends") != null)? (int) profData.get("friends") : 0;
        String friendsStr = (numFriends != 1)? numFriends + " friends" : numFriends + " friend";
        friends.setText(friendsStr);
        friends.setOnClickListener(this);
        friends.startAnimation(enterTop);

        //set number of habits
        final TextView habits = (TextView) view.findViewById(R.id.num_habits);
        int numHabits = (profData.get("habits") != null)? (int) profData.get("habits") : 0;
        String habitsStr = (numHabits != 1)? numHabits + " habits" : numHabits + " habit";
        habits.setText(habitsStr);
        habits.startAnimation(enterTop);

        //fade top on scroll
        final AppBarLayout appBar = (AppBarLayout) view.findViewById(R.id.app_bar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alphaVal = 1.0f - Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange());

                settings.setAlpha(alphaVal);
                profilePic.setAlpha(alphaVal);
                username.setAlpha(alphaVal);
                editProfile.setAlpha(alphaVal);
                bio.setAlpha(alphaVal);
                friends.setAlpha(alphaVal);
                habits.setAlpha(alphaVal);

                if (alphaVal == 0.0) {
                    cl.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
                    window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.background));

                    int uiFlag = getContext().getResources().getConfiguration().uiMode
                            & Configuration.UI_MODE_NIGHT_MASK;
                    int darkFlag = Configuration.UI_MODE_NIGHT_YES;
                    window.getDecorView().setSystemUiVisibility((uiFlag == darkFlag)?
                            view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR:
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                else {
                    cl.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple));
                    window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.purple));
                    window.getDecorView().setSystemUiVisibility(
                            view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        });

        //initialize profile RecyclerView
        RecyclerView rvProfile = (RecyclerView) view.findViewById(R.id.habits);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvProfile.setLayoutManager(llm);
        RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> adapter = new ProfileAdapter(profileItems, llm);
        rvProfile.setAdapter(adapter);

        return view;
    }

    /**
     * Click listener.
     *
     * @param view current view
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.settings:
                intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
                break;
            case R.id.edit_profile: case R.id.profile_pic: case R.id.username: case R.id.bio:
                intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
                break;
            case R.id.friends:
                BottomNavigationView bnv =
                        (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                bnv.setSelectedItemId(R.id.friends);
                break;
        }
    }

    /**
     * Generates test data in place of database.
     */
    private void generateTestData() {
        database = new ArrayList<>();

        //random data
        String[] testHabits = {"Work Out", "Read", "Meditate", "Drink water", "Nap", "Eat fruit",
                "Practice piano", "Work on project", "Finish essay", "Read the news"};
        int[] testIcons = {R.drawable.habit_alc, R.drawable.habit_baseball,
                R.drawable.habit_basketball, R.drawable.habit_bath, R.drawable.habit_bike,
                R.drawable.habit_book, R.drawable.habit_boxing, R.drawable.habit_brush,
                R.drawable.habit_coffee, R.drawable.habit_phone, R.drawable.habit_person,
                R.drawable.habit_food, R.drawable.habit_money, R.drawable.habit_leaf};
        String[] testFrequency = {"days", "weeks", "months"};
        Boolean[] likedArray = {true, false};
        ArrayList<CalendarDay> dates = new ArrayList<>();
        dates.add(CalendarDay.from(2020, 7, 20));
        dates.add(CalendarDay.from(2020, 7, 23));
        dates.add(CalendarDay.from(2020, 7, 24));
        dates.add(CalendarDay.from(2020, 7, 26));

        //generate random data for testing
        for (int i = 0; i <= 10; i++) {
            Map<String, Object> item = new HashMap<>();
            //item.put("pfp", R.drawable.default_pfp);
            item.put("iconId", testIcons[(int)(Math.random() * 14)]);
            item.put("frequency", testFrequency[(int)(Math.random() * 3)]);
            item.put("habitName", testHabits[(int)(Math.random() * 10)]);
            int streak = (int)(Math.random() * 200) + 1;
            item.put("curStreak", streak);
            item.put("bestStreak", streak + (int)(Math.random() * 200));
            item.put("total", streak * 2 + (int)(Math.random() * 200));
            item.put("dates", dates);
            item.put("likes", (int)(Math.random() * 500));
            database.add(item);
        }

        profData.put("username", "seanzx9");
        profData.put("bio", "Hello, my name is Sean.");
        profData.put("friends", 245);
        profData.put("habits", 4);
    }

    /**
     * Loads data to list.
     */
    private void loadData() {
        profileItems.addAll(database);
    }
}