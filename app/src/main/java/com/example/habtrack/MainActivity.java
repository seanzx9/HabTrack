package com.example.habtrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        openFragment(WallFragment.newInstance());

        //initialize bottom navbar
        BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.wall:
                                openFragment(WallFragment.newInstance());
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

        //mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
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
    }
}