package com.example.habtrack;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import java.util.ArrayList;
import java.util.HashMap;

public class WallFragment extends Fragment {
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter<WallAdapter.WallViewHolder> adapter;
    private ArrayList<HashMap<String, Object>> wallItems;
    private ArrayList<HashMap<String, Object>> database; //test data
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
        database = new ArrayList<>();
        generateTestData();
        loadNextData(0);

        //initialize RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.wall);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WallAdapter(wallItems, layoutManager);
        recyclerView.setAdapter(adapter);

        //lazy load wall items
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextData(page);
            }
        });

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

    /**
     * Generates test data in place of database.
     */
    private void generateTestData() {
        //random data
        String[] testUsernames = {"john_smith", "jane_doe", "gurp956", "seanzx9", "jupy"};
        String[] testHabits = {"Work Out", "Read", "Meditate", "Drink water", "Nap", "Eat fruit",
                "Practice piano", "Work on project", "Finish essay", "Read the news"};
        int[] testIcons = {R.drawable.habit_alc, R.drawable.habit_baseball,
                R.drawable.habit_basketball, R.drawable.habit_bath, R.drawable.habit_bike,
                R.drawable.habit_book, R.drawable.habit_boxing, R.drawable.habit_brush,
                R.drawable.habit_coffee, R.drawable.habit_phone, R.drawable.habit_person,
                R.drawable.habit_food, R.drawable.habit_money, R.drawable.habit_leaf};
        String[] testFrequency = {"days", "weeks", "months"};
        Boolean[] likedArray = {true, false};
        int[] colors = {R.color.purple, R.color.green, R.color.red, R.color.blue, R.color.black,
                R.color.yellow};
        CalendarDay[] dates = {CalendarDay.from(2020, 7, 10),
                CalendarDay.from(2020, 7, 12),
                CalendarDay.from(2020, 7, 14),
                CalendarDay.from(2020, 7, 15),
                CalendarDay.from(2020, 7, 17)};

        //generate random data for testing
        for (int i = 0; i <= 500; i++) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("color", colors[(int)(Math.random() * 6)]);
            //item.put("pfp", R.drawable.default_pfp);
            item.put("username", testUsernames[(int)(Math.random() * 5)]);
            item.put("iconId", testIcons[(int)(Math.random() * 14)]);
            item.put("habitName", testHabits[(int)(Math.random() * 10)]);
            int streak = (int)(Math.random() * 200) + 1;
            item.put("curStreak", streak);
            item.put("bestStreak", streak + (int)(Math.random() * 200));
            item.put("total", streak * 2 + (int)(Math.random() * 200));
            item.put("frequency", testFrequency[(int)(Math.random() * 3)]);
            item.put("dates", dates);
            item.put("isLiked", likedArray[(int)(Math.random()* 2)]);
            item.put("likes", (int)(Math.random() * 500));
            database.add(item);
        }
    }

    /**
     * Loads next items to the wall.
     *
     * @param offset page offset
     */
    public void loadNextData(int offset) {
        for (int i = offset; i < offset + 10; i++)
            wallItems.add(database.get(i));
        if (offset > 0) adapter.notifyDataSetChanged();
    }
}