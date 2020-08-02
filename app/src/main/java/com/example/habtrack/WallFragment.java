package com.example.habtrack;

import android.content.res.Configuration;
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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WallFragment extends Fragment {
    private RecyclerView.Adapter<WallAdapter.WallViewHolder> adapterWall;
    private ArrayList<Map<String, Object>> wallItems;
    private ArrayList<Map<String, Object>> personalItems;
    private ArrayList<Map<String, Object>> database; //test data
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
        final View view =  inflater.inflate(R.layout.fragment_wall, container, false);

        //initialize top bar color
        final Window window = getActivity().getWindow();
        final CoordinatorLayout cl = (CoordinatorLayout) view.findViewById(R.id.main_layout);
        cl.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple));
        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.purple));
        window.getDecorView().setSystemUiVisibility(
                view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //welcome text animation
        Animation enterTop = AnimationUtils.loadAnimation(getContext(), R.anim.delayed_enter_from_top);
        final TextView welcome = (TextView) view.findViewById(R.id.welcome);
        welcome.startAnimation(enterTop);

        //initialize arraylist for wall
        wallItems = new ArrayList<>();
        personalItems = new ArrayList<>();
        generateTestData();
        loadNextData(0);

        //initialize personal RecyclerView
        final RecyclerView rvPersonal = (RecyclerView) view.findViewById(R.id.personal);
        LinearLayoutManager llmHorizontal = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvPersonal.setLayoutManager(llmHorizontal);
        RecyclerView.Adapter<PersonalAdapter.PersonalViewHolder> adapterPersonal =
                new PersonalAdapter(personalItems, llmHorizontal);
        rvPersonal.setAdapter(adapterPersonal);

        //fade top on scroll
        final AppBarLayout appBar = (AppBarLayout) view.findViewById(R.id.app_bar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alphaVal = 1.0f - Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange());

                rvPersonal.setAlpha(alphaVal);
                welcome.setAlpha(alphaVal);

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

        //initialize wall RecyclerView
        RecyclerView rvWall = (RecyclerView) view.findViewById(R.id.wall);
        LinearLayoutManager llmVertical = new LinearLayoutManager(getContext());
        rvWall.setLayoutManager(llmVertical);
        adapterWall = new WallAdapter(wallItems, llmVertical);
        rvWall.setAdapter(adapterWall);

        //lazy load wall items
        rvWall.addOnScrollListener(new EndlessRecyclerViewScrollListener(llmVertical) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
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
        database = new ArrayList<>();

        //random data
        String[] testUsername = {"gurp956", "seanzx9", "jupy", "john_smith", "jane_doe", "batman",
                "superman123", "watermelon_lover12", "UsERnaMe", "adam_jones", "a_ham8"};
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
        for (int i = 0; i <= 500; i++) {
            Map<String, Object> item = new HashMap<>();
            //item.put("pfp", R.drawable.default_pfp);
            item.put("username", testUsername[(int)(Math.random() * 11)]);
            item.put("bio", "This is bio number " + i);
            item.put("iconId", testIcons[(int)(Math.random() * 14)]);
            item.put("habitName", testHabits[(int)(Math.random() * 10)]);
            item.put("frequency", testFrequency[(int)(Math.random() * 3)]);
            int streak = (int)(Math.random() * 200) + 1;
            item.put("curStreak", streak);
            item.put("bestStreak", streak + (int)(Math.random() * 200));
            item.put("total", streak * 2 + (int)(Math.random() * 200));
            item.put("dates", dates);
            item.put("isLiked", likedArray[(int)(Math.random()* 2)]);
            item.put("likes", (int)(Math.random() * 500));
            item.put("numFriends", (int)(Math.random() * 1500));
            item.put("numHabits", (int)(Math.random() * 20));
            database.add(item);
        }

        //random data for personal habits
        for (int i = 0; i < 10; i++) {
            dates = new ArrayList<>();
            dates.add(CalendarDay.from(2020, 7, 20));
            dates.add(CalendarDay.from(2020, 7, 23));
            dates.add(CalendarDay.from(2020, 7, 24));
            dates.add(CalendarDay.from(2020, 7, 26));
            Map<String, Object> item = new HashMap<>();
            item.put("habitName", testHabits[(int)(Math.random() * 10)]);
            int streak = (int)(Math.random() * 200) + 1;
            item.put("curStreak", streak);
            item.put("frequency", testFrequency[(int)(Math.random() * 3)]);
            item.put("dates", dates);
            personalItems.add(item);
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
        if (offset > 0) adapterWall.notifyDataSetChanged();
    }
}