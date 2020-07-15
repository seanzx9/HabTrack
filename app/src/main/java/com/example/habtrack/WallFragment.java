package com.example.habtrack;

import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class WallFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<HashMap<String, Object>> wallItems;
    private FirebaseAuth mAuth;

    public WallFragment() {}

    public static WallFragment newInstance() { return new WallFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_wall, container, false);

        //back button ends activity
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                getActivity().finish();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        //initialize arraylist for wall
        wallItems = new ArrayList<>();
        loadWallFromDatabase();

        //initialize RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.wall);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WallAdapter(wallItems, layoutManager);
        recyclerView.setAdapter(adapter);

        //mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
        }
        else
            Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
    }

    private void loadWallFromDatabase() {
        //random data
        String[] testUsernames = {"john_smith", "jane_doe", "gurp956", "seanzx9", "jupy"};
        String[] testHabits = {"Work Out", "Read", "Meditate", "Drink water", "Nap", "Eat fruit",
                "Practice piano", "Work on project", "Finish essay", "Read the news"};
        int[] testIcons = {R.drawable.post_on, R.drawable.friends_on, R.drawable.notification_on};
        String[] testFrequency = {"days", "weeks", "months"};
        Boolean[] likedArray = {true, false};
        int[] colors = {R.color.purple, R.color.green, R.color.red, R.color.blue, R.color.black,
                R.color.yellow};
        CalendarDay[] dates = {CalendarDay.from(2020, 7, 10),
                CalendarDay.from(2020, 7, 11),
                CalendarDay.from(2020, 7, 14)};

        //generate random data for testing
        for (int i = 0; i <= 500; i++) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("color", colors[(int)(Math.random() * 6)]);
            item.put("profilePic", R.drawable.profile_on);
            item.put("username", testUsernames[(int)(Math.random() * 5)]);
            item.put("iconId", testIcons[(int)(Math.random() * 3)]);
            item.put("habitName", testHabits[(int)(Math.random() * 10)]);
            int streak = (int)(Math.random() * 200) + 1;
            item.put("curStreak", streak);
            item.put("bestStreak", streak + (int)(Math.random() * 200));
            item.put("total", streak * 2 + (int)(Math.random() * 200));
            item.put("frequency", testFrequency[(int)(Math.random() * 3)]);
            item.put("dates", dates);
            item.put("isLiked", likedArray[(int)(Math.random()* 2)]);
            item.put("likes", (int)(Math.random() * 500));
            wallItems.add(item);
        }
    }
}