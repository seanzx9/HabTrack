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

import java.util.ArrayList;
import java.util.HashMap;

public class WallFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<HashMap<String, Object>> wallItems;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
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
        adapter = new WallAdapter(wallItems);
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
        int[] testIcons = {R.drawable.post, R.drawable.friends_on, R.drawable.notification_on};
        String[] testFrequency = {"days", "weeks", "months"};
        Boolean[] likedArray = {true, false};

        //generate random data for testing
        for (int i = 1; i <= 500; i++) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("profilePic", R.drawable.profile_on);
            item.put("username", testUsernames[(int)(Math.random() * 5)]);
            item.put("iconId", testIcons[(int)(Math.random() * 3)]);
            item.put("habitName", testHabits[(int)(Math.random() * 10)]);
            int streak = (int)(Math.random() * 200) + 1;
            item.put("curStreak", streak);
            item.put("longStreak", streak + (int)(Math.random() * 200));
            item.put("frequency", testFrequency[(int)(Math.random() * 3)]);
            item.put("isLiked", likedArray[(int)(Math.random()* 2)]);
            item.put("likes", (int)(Math.random() * 500));
            wallItems.add(item);
        }
    }
}