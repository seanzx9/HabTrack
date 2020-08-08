package com.example.habtrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    public BottomNavigationView bnv;
    private Stack<Integer> fragments;
    private int curFragmentId;
    private boolean backPressed;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        //initialize top bar color
        final Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple));
        window.getDecorView().setSystemUiVisibility(window
                .getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        fragments = new Stack<>();
        openFragment(WallFragment.newInstance());
        curFragmentId = R.id.wall;
        backPressed = false;

        //initialize bottom navbar
        bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id != R.id.wall && id == curFragmentId) return true;

                        switch (id) {
                            case R.id.wall:
                                openFragment(WallFragment.newInstance());
                                curFragmentId = R.id.wall;
                                return true;
                            case R.id.friends:
                                openFragment(FriendsFragment.newInstance());
                                curFragmentId = R.id.friends;
                                return true;
                            case R.id.post:
                                Intent intent = new Intent(MainActivity.this, CreateHabit.class);
                                startActivity(intent);
                                return true;
                            case R.id.notifications:
                                openFragment(NotificationsFragment.newInstance());
                                curFragmentId = R.id.notifications;
                                return true;
                            case R.id.profile:
                                openFragment(ProfileFragment.newInstance());
                                curFragmentId = R.id.profile;
                                return true;
                        }
                        return false;
                    }
                });

        //mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        bnv.setSelectedItemId(curFragmentId);
    }

    /**
     * Navigate through app with back button.
     */
    @Override
    public void onBackPressed() {
        if (fragments.isEmpty() || curFragmentId == R.id.wall) {
            finish();
        }
        else {
            int id = fragments.pop();
            backPressed = true;
            bnv.setSelectedItemId(id);
            curFragmentId = id;
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null)
            //Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
            return;
        else
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
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

        if (!backPressed) fragments.push(curFragmentId);
        else backPressed = false;
    }
}