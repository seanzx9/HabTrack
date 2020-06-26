package com.example.habtrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        //get token from login
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            token = bundle.getString("token");

        openFragment(WallFragment.newInstance(token));

        //initialize bottom navbar
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.wall:
                                openFragment(WallFragment.newInstance(token));
                                return true;
                            case R.id.friends:
                                openFragment(FriendsFragment.newInstance());
                                return true;
                            case R.id.post:
                                openFragment(PostFragment.newInstance());
                                return true;
                            case R.id.notifications:
                                openFragment(NotificationsFragment.newInstance());
                                return true;
                            case R.id.profile:
                                openFragment(ProfileFragment.newInstance());
                                return true;
                        }
                        return false;
                    }
                });
    }

    /**
     * Handles opening fragments with animation and effects.
     *
     * @param fragment Fragment to open.
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}