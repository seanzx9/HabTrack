package com.example.habtrack;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

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

        //welcome text animation
        Animation enterTop = AnimationUtils.loadAnimation(getContext(), R.anim.item_enter_from_top);
        TextView notification = (TextView) view.findViewById(R.id.notifications_text);
        notification.startAnimation(enterTop);

        //initialize arraylist for notifications
        notificationItems = new ArrayList<>();
        generateTestData();
        loadNextData(0);

        //recyclerview for notifications
        final RecyclerView rv = (RecyclerView) view.findViewById(R.id.notifications);
        llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        adapter = new NotificationsAdapter(notificationItems, llm);
        rv.setAdapter(adapter);

        //lazy load notifications
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData(page);
            }
        });

        //delete notification on swipe
        ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int pos = viewHolder.getLayoutPosition();
                notificationItems.remove(pos);
                adapter.notifyItemRemoved(pos);
                adapter.notifyItemRangeChanged(pos, adapter.getItemCount());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        rv.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                rv.smoothScrollToPosition(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        //scroll to top on click
        ConstraintLayout topBar = (ConstraintLayout) view.findViewById(R.id.top_bar);
        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.smoothScrollToPosition(0);
            }
        });

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
     * Loads next notifications.
     *
     * @param offset page offset
     */
    public void loadNextData(int offset) {
        for (int i = offset; i < offset + 10; i++)
            notificationItems.add(database.get(i));
        if (offset > 0) adapter.notifyDataSetChanged();
    }
}