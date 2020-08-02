package com.example.habtrack;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FriendsFragment extends Fragment {
    private ArrayList<Map<String, Object>> friendsItems;
    private ArrayList<Map<String, Object>> filteredItems;
    private ArrayList<Map<String, Object>> database; //test data

    public FriendsFragment() {}

    public static FriendsFragment newInstance() { return new FriendsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);

        //initialize top bar color
        final Window window = getActivity().getWindow();
        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.background));
        int uiFlag = getContext().getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        int darkFlag = Configuration.UI_MODE_NIGHT_YES;
        window.getDecorView().setSystemUiVisibility((uiFlag == darkFlag)?
                view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR:
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //initialize arraylist for grid
        friendsItems = new ArrayList<>();
        filteredItems = new ArrayList<>();
        generateTestData();
        loadData();

        //initialize friends RecyclerView
        final RecyclerView rvFriends = (RecyclerView) view.findViewById(R.id.friends);
        final GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        rvFriends.setLayoutManager(glm);
        final RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> adapterFriends =
                new FriendsAdapter(filteredItems);
        rvFriends.setAdapter(adapterFriends);

        //search bar
        filter("");
        adapterFriends.notifyDataSetChanged();
        final TextInputEditText searchBar = (TextInputEditText) view.findViewById(R.id.search_edit);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString().trim();
                filter(str);
                adapterFriends.notifyDataSetChanged();
                glm.scrollToPosition(0);
            }
        });

        //fade top on scroll
        final AppBarLayout appBar = (AppBarLayout) view.findViewById(R.id.app_bar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                TextInputLayout search = (TextInputLayout) view.findViewById(R.id.search);
                search.setAlpha(1.0f - Math.abs(verticalOffset / (float)
                        appBarLayout.getTotalScrollRange()));
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
            "superman123", "watermelon_lover12", "user123456789", "adam_jones", "a_ham8",
            "user_10", "ironman3000", "thanos", "lawrence_a", "abc123", "jeff_b"};

        //generate random data for testing
        for (int i = 0; i < 17; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("pfp", R.drawable.default_pfp);
            item.put("username", testUsername[i]);
            item.put("bio", "This is bio number " + i);
            item.put("numFriends", (int)(Math.random() * 1500));
            item.put("numHabits", (int)(Math.random() * 20));
            database.add(item);
        }
    }

    /**
     * Loads data into list.
     */
    private void loadData() {
        friendsItems.addAll(database);
    }

    /**
     * Filters friends for searching.
     *
     * @param searchFor string to look for in username
     */
    private void filter(String searchFor) {
        if (searchFor.equals("")) {
            filteredItems.addAll(friendsItems);
            Collections.sort(filteredItems, new MapComparator("username"));
        }
        else {
            filteredItems.clear();
            for (int i = 0; i < friendsItems.size(); i++) {
                Map<String, Object> item = friendsItems.get(i);
                String username = item.get("username").toString();
                if (username == null) continue;
                if (username.contains(searchFor)) {
                    double sim = similarity(searchFor, item.get("username").toString());
                    if (username.startsWith(searchFor)) sim += 1.0;
                    item.put("sim", sim);
                    filteredItems.add(item);
                }
            }
            Collections.sort(filteredItems, new MapComparator("sim").reversed());
        }
    }

    /**
     * Finds similarity between strings (0.0 being least, 1.0 being most).
     *
     * @param s1 first string to compare
     * @param s2 second string to compare
     * @return similarity value
     */
    public double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) return 1.0;
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    // Example implementation of the Levenshtein Edit Distance

    /**
     * Finds the Levenshtein edit distance.
     *
     * @param s1 first string to compare
     * @param s2 second string to compare
     * @return Levenshtein distance
     */
    public int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}