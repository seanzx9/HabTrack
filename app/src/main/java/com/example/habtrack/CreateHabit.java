package com.example.habtrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class CreateHabit extends AppCompatActivity {
    private TextInputEditText habitName;
    private ArrayList<Integer> icons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        //initialize top bar color
        final Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));
        int uiFlag = this.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        int darkFlag = Configuration.UI_MODE_NIGHT_YES;
        window.getDecorView().setSystemUiVisibility((uiFlag == darkFlag)?
                window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR:
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //enter animation
        Animation enter = AnimationUtils.loadAnimation(this, R.anim.item_enter_from_top);

        //habit name title
        TextView habitNameTitle = (TextView) findViewById(R.id.habit_name_text);
        enter.setStartOffset(50);
        habitNameTitle.startAnimation(enter);

        //habit name text view
        TextInputLayout habitNameText = (TextInputLayout) findViewById(R.id.habit_name);
        habitNameText.startAnimation(enter);

        //habit name input field
        habitName = (TextInputEditText) findViewById(R.id.habit_name_edit);
        habitName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!habitName.hasFocus()) habitName.requestFocus();
            }
        });

        //frequency title
        TextView frequencyTitle = (TextView) findViewById(R.id.frequency_text);
        enter = AnimationUtils.loadAnimation(this, R.anim.item_enter_from_top);
        enter.setStartOffset(300);
        frequencyTitle.startAnimation(enter);

        //frequency radio group
        RadioGroup frequency = (RadioGroup) findViewById(R.id.frequency);
        frequency.startAnimation(enter);

        //visibility title
        TextView visibilityTitle = (TextView) findViewById(R.id.visibility_text);
        enter = AnimationUtils.loadAnimation(this, R.anim.item_enter_from_top);
        enter.setStartOffset(700);
        visibilityTitle.startAnimation(enter);

        //visibility radio group
        RadioGroup visibility = (RadioGroup) findViewById(R.id.visibility);
        visibility.startAnimation(enter);

        //icon title
        TextView iconTitle = (TextView) findViewById(R.id.icons_text);
        enter = AnimationUtils.loadAnimation(this, R.anim.item_enter_from_top);
        enter.setStartOffset(1000);
        iconTitle.startAnimation(enter);

        //set icon recycler view
        loadIcons();
        RecyclerView rv = (RecyclerView) findViewById(R.id.icons);
        GridLayoutManager glm = new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(glm);
        RecyclerView.Adapter<IconAdapter.IconViewHolder> adapter = new IconAdapter(icons);
        rv.setAdapter(adapter);
        
        //cancel button
        TextView cancel = (TextView) findViewById(R.id.cancel_text);
        enter = AnimationUtils.loadAnimation(this, R.anim.item_enter_from_bottom);
        enter.setStartOffset(1300);
        cancel.startAnimation(enter);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //create button
        ConstraintLayout create = (ConstraintLayout) findViewById(R.id.create);
        create.startAnimation(enter);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * Loads all habit icons
     */
    private void loadIcons() {
        icons = new ArrayList<>();
        icons.add(R.drawable.habit_alc);
        icons.add(R.drawable.habit_baseball);
        icons.add(R.drawable.habit_basketball);
        icons.add(R.drawable.habit_bath);
        icons.add(R.drawable.habit_bike);
        icons.add(R.drawable.habit_boat);
        icons.add(R.drawable.habit_book);
        icons.add(R.drawable.habit_boxing);
        icons.add(R.drawable.habit_brush);
        icons.add(R.drawable.habit_bus);
        icons.add(R.drawable.habit_cake);
        icons.add(R.drawable.habit_calendar);
        icons.add(R.drawable.habit_camera);
        icons.add(R.drawable.habit_car);
        icons.add(R.drawable.habit_card);
        icons.add(R.drawable.habit_carriage);
        icons.add(R.drawable.habit_chat);
        icons.add(R.drawable.habit_coffee);
        icons.add(R.drawable.habit_computer);
        icons.add(R.drawable.habit_dining);
        icons.add(R.drawable.habit_flower);
        icons.add(R.drawable.habit_food);
        icons.add(R.drawable.habit_football);
        icons.add(R.drawable.habit_game);
        icons.add(R.drawable.habit_gas);
        icons.add(R.drawable.habit_golf);
        icons.add(R.drawable.habit_grill);
        icons.add(R.drawable.habit_groceries);
        icons.add(R.drawable.habit_happy);
        icons.add(R.drawable.habit_headphone);
        icons.add(R.drawable.habit_leaf);
        icons.add(R.drawable.habit_money);
        icons.add(R.drawable.habit_moon);
        icons.add(R.drawable.habit_music);
        icons.add(R.drawable.habit_palette);
        icons.add(R.drawable.habit_person);
        icons.add(R.drawable.habit_pet);
        icons.add(R.drawable.habit_phone);
        icons.add(R.drawable.habit_power);
        icons.add(R.drawable.habit_run);
        icons.add(R.drawable.habit_school);
        icons.add(R.drawable.habit_sleep);
        icons.add(R.drawable.habit_smoke);
        icons.add(R.drawable.habit_soccer);
        icons.add(R.drawable.habit_swimming);
        icons.add(R.drawable.habit_tea);
        icons.add(R.drawable.habit_tennis);
        icons.add(R.drawable.habit_time);
        icons.add(R.drawable.habit_train);
        icons.add(R.drawable.habit_trash);
        icons.add(R.drawable.habit_trophy);
        icons.add(R.drawable.habit_volleyball);
        icons.add(R.drawable.habit_walk);
        icons.add(R.drawable.habit_water);
        icons.add(R.drawable.habit_weight);
        icons.add(R.drawable.habit_work);
        icons.add(R.drawable.habit_wrench);
    }
}