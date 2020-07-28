package com.example.habtrack;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {
    private LinearLayoutManager llm;
    private RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> adapter;
    private ArrayList<Map<String, Object>> notificationItems;
    private ArrayList<Map<String, Object>> database; //test data

    public NotificationsFragment() {}

    public static NotificationsFragment newInstance() { return new NotificationsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        //initialize top bar color
        final Window window = getActivity().getWindow();
        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.purple));
        window.getDecorView().setSystemUiVisibility(
                view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //initialize arraylist for notifications
        notificationItems = new ArrayList<>();
        generateTestData();
        loadData();

        //recyclerview for notifications
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.notifications);
        llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        adapter = new NotificationsAdapter(notificationItems, llm);
        rv.setAdapter(adapter);

        return view;
    }

    /**
     * Generates test data in place of database.
     */
    private void generateTestData() {
        database = new ArrayList<>();

        //random data
        String[] testUsername = {"gurp956", "seanzx9", "jupy", "john_smith", "jane_doe", "batman",
                "superman123", "watermelon_lover12", "UsERnaMe", "adam_jones", "a_ham8"};
        String[] testMessages = {"<USERNAME> requested to be your friend.",
                                "You and <USERNAME> are now friends.",
                                "<USERNAME> started a new habit",
                                "You've completed <HABIT_NAME> 100 times!"};
        String[] testHabits = {"Work Out", "Read", "Meditate", "Drink water", "Nap", "Eat fruit",
                "Practice piano", "Work on project", "Finish essay", "Read the news"};
        Boolean[] readArray = {true, false};

        //generate random data for testing
        for (int i = 0; i <= 500; i++) {
            Map<String, Object> item = new HashMap<>();
            //item.put("pfp", R.drawable.default_pfp);
            item.put("username", testUsername[(int)(Math.random() * 11)]);
            item.put("habitName", testHabits[(int)(Math.random() * 10)]);
            item.put("isRead", readArray[(int)(Math.random() * 2)]);
            item.put("message", testMessages[(int)(Math.random() * 4)]);
            database.add(item);
        }
    }
    /**
     * Loads data into list.
     */
    private void loadData() {
        notificationItems.addAll(database);
    }
}