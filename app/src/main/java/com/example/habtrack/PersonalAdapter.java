package com.example.habtrack;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.PersonalViewHolder> {
    private ArrayList<Map<String, Object>> data;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    /**
     * Constructor with parameters.
     *
     * @param data data to update wall with
     */
    public PersonalAdapter(ArrayList<Map<String, Object>> data, RecyclerView.LayoutManager lm) {
        this.data = data;
        this.layoutManager = lm;
    }

    /**
     * Inflates view with layout.
     *
     * @param parent parent ViewGroup
     * @param viewType type of view
     * @return new WallViewHolder
     */
    @Override
    public PersonalAdapter.PersonalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.personal_item, parent, false);
        return new PersonalViewHolder(v);
    }

    /**
     * Initialize components in card view
     */
    public static class PersonalViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ConstraintLayout card;
        private TextView habitName;
        private TextView streak;

        public PersonalViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            card = (ConstraintLayout) v.findViewById(R.id.card);
            habitName = (TextView) v.findViewById(R.id.habit_name);
            streak = (TextView) v.findViewById(R.id.streak);
        }
    }

    /**
     * Fill the contents of the page with the appropriate data.
     *
     * @param pvh view holder
     * @param position position in data
     */
    @Override
    public void onBindViewHolder(final PersonalAdapter.PersonalViewHolder pvh, final int position) {
        final Map<String, Object> item = data.get(position);
        context = pvh.cardView.getContext();

        pvh.setIsRecyclable(false);

        setHabit(pvh, item);
        setClickBehavior(pvh);
    }

    /**
     * Getter for the size of the data.
     *
     * @return number of components in the data
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Sets habit name and streak.
     *
     * @param pvh view holder
     * @param item element of data
     */
    private void setHabit(PersonalViewHolder pvh, Map<String, Object> item) {
        //name of habit
        String habitName = (item.get("habitName") != null)?
                item.get("habitName").toString() : "Habit Name";
        pvh.habitName.setText(habitName);

        //habit streak
        String streakNum = (item.get("curStreak") != null)? item.get("curStreak").toString() : "0";
        String frequency = (item.get("frequency") != null)? item.get("frequency").toString() : "days";
        pvh.streak.setText(streakNum + " " + frequency);
    }

    /**
     * Sets listened for card view.
     *
     * @param pvh view holder
     */
    private void setClickBehavior(final PersonalViewHolder pvh) {
        pvh.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation buttonPress = AnimationUtils.loadAnimation(context, R.anim.button_press);

                buttonPress.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        data.add(data.remove(pvh.getAdapterPosition()));
                        notifyItemMoved(pvh.getAdapterPosition(), data.size() - 1);
                        layoutManager.scrollToPosition(0);

                        int hintColor = ContextCompat.getColor(context, R.color.hint);
                        if (pvh.habitName.getCurrentTextColor() != hintColor) {
                            pvh.card.setBackgroundColor(ContextCompat.getColor(context, R.color.checked));
                            pvh.habitName.setTextColor(ContextCompat.getColor(context, R.color.hint));
                            pvh.streak.setTextColor(ContextCompat.getColor(context, R.color.hint));
                        }
                        else {
                            pvh.card.setBackgroundColor(ContextCompat.getColor(context, R.color.card));
                            pvh.habitName.setTextColor(ContextCompat.getColor(context, R.color.text));
                            pvh.streak.setTextColor(ContextCompat.getColor(context, R.color.text));
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                pvh.cardView.startAnimation(buttonPress);
            }
        });
    }
}
