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
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.prolificinteractive.materialcalendarview.CalendarDay;

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
     * @param data data to update personal wall with
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
    public class PersonalViewHolder extends RecyclerView.ViewHolder {
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
            context = cardView.getContext();

            //checks off habit for day
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation buttonPress = AnimationUtils.loadAnimation(context, R.anim.button_press);

                    buttonPress.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            int pos = getLayoutPosition();
                            Map<String, Object> item = data.get(pos);
                            ArrayList<CalendarDay> dates = getDates(pos);

                            //add today's date to item
                            boolean containsToday = dates.contains(CalendarDay.today());
                            if (containsToday)
                                dates.remove(CalendarDay.today());
                            else
                                dates.add(CalendarDay.today());

                            //sets the dates
                            item.put("dates", dates);
                            data.set(pos, item);
                            notifyItemChanged(pos);

                            //move item
                            data.remove(item);
                            if (containsToday)
                                data.add(0, item);
                            else
                                data.add(item);
                            notifyItemMoved(pos, data.size() - 1);
                            layoutManager.scrollToPosition(0);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    cardView.startAnimation(buttonPress);
                }
            });
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

        setHabit(pvh, item);
        setChecked(pvh);
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
     * Get date from dataset.
     *
     * @param pos position of data
     * @return list of dates of habit
     */
    private ArrayList<CalendarDay> getDates(int pos) {
        final Map<String, Object> item = data.get(pos);
        return (ArrayList<CalendarDay>) item.get("dates");
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
     * Sets habit name and streak.
     *
     * @param pvh view holder
     */
    private void setChecked(PersonalViewHolder pvh) {
        ArrayList<CalendarDay> dates = getDates(pvh.getAdapterPosition());
        if (dates == null || dates.size() <= 0) return;

        boolean isComplete = dates.get(dates.size() - 1).equals(CalendarDay.today());
        if (isComplete) {
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
}
