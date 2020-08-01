package com.example.habtrack;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static android.content.Context.VIBRATOR_SERVICE;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    private ArrayList<Map<String, Object>> data;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    /**
     * Constructor with parameters.
     *
     * @param data data to update wall with
     */
    public ProfileAdapter(ArrayList<Map<String, Object>> data, RecyclerView.LayoutManager lm) {
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
    public ProfileAdapter.ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        return new ProfileAdapter.ProfileViewHolder(v);
    }

    /**
     * Initialize components in card view
     */
    public class ProfileViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout card;
        private ImageView habitIcon;
        private ImageButton editIcon;
        private TextView habitName;
        private ConstraintLayout expandContainer;
        private MaterialCalendarView mcv;
        private PieChartView chart;
        private TextView percent;
        private TextView streak;
        private TextView bestStreak;
        private TextView total;
        private TextView frequency;
        private TextView bestFrequency;
        private TextView totalFrequency;
        private TextView likes;
        private ToggleButton expandButton;

        public ProfileViewHolder(View v) {
            super(v);
            final CardView cardView = (CardView) v.findViewById(R.id.card_view);
            card = (ConstraintLayout) v.findViewById(R.id.card);
            habitIcon = (ImageView) v.findViewById(R.id.habit_icon);
            editIcon = (ImageButton) v.findViewById(R.id.edit);
            habitName = (TextView) v.findViewById(R.id.habit_name);
            expandContainer = (ConstraintLayout) v.findViewById(R.id.expand_container);
            mcv = (MaterialCalendarView) v.findViewById(R.id.calendar);
            chart = (PieChartView) v.findViewById(R.id.chart);
            percent = (TextView) v.findViewById(R.id.percent);
            streak = (TextView) v.findViewById(R.id.streak);
            bestStreak = (TextView) v.findViewById(R.id.best_streak);
            total = (TextView) v.findViewById(R.id.total);
            frequency = (TextView) v.findViewById(R.id.frequency);
            bestFrequency = (TextView) v.findViewById(R.id.best_frequency);
            totalFrequency = (TextView) v.findViewById(R.id.total_frequency);
            likes = (TextView) v.findViewById(R.id.likes);
            expandButton = (ToggleButton) v.findViewById(R.id.expand_button);
            context = cardView.getContext();
            setIsRecyclable(false);

            //listener to edit habit
            View.OnClickListener editHabitListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), EditHabit.class);
                    context.startActivity(intent);
                }
            };
            editIcon.setOnClickListener(editHabitListener);
            habitIcon.setOnClickListener(editHabitListener);
            habitName.setOnClickListener(editHabitListener);

            //expand and collapse card
            expandButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        expandButton.animate().rotation(180).setDuration(350).start();
                        expand();
                    }
                    else {
                        expandButton.animate().rotation(0).setDuration(350).start();
                        collapse();
                    }

                    RecyclerView.SmoothScroller smoothScroller = new
                            LinearSmoothScroller(context) {
                                private static final float MILLISECONDS_PER_INCH = 100f;

                                @Override
                                protected int getVerticalSnapPreference() {
                                    return LinearSmoothScroller.SNAP_TO_START;
                                }

                                @Override
                                protected float calculateSpeedPerPixel(DisplayMetrics dm) {
                                    return MILLISECONDS_PER_INCH / dm.densityDpi;
                                }
                            };
                    smoothScroller.setTargetPosition(getLayoutPosition());
                    layoutManager.startSmoothScroll(smoothScroller);
                }
            });

            //long click expands and collapses
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    expandButton.performClick();
                    ((Vibrator) context.getSystemService(VIBRATOR_SERVICE))
                            .vibrate(VibrationEffect.createOneShot(10,25));
                    return false;
                }
            });
        }

        /**
         * Expand the card to show extra statistics.
         */
        public void expand() {
            final View v = card;
            int prevHeight = v.getHeight();

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setStartOffset(0);
            fadeIn.setDuration(350);
            expandContainer.startAnimation(fadeIn);

            expandContainer.setVisibility(View.VISIBLE);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            v.measure(widthSpec, heightSpec);
            final int targetHeight = v.getMeasuredHeight();

            ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.getLayoutParams().height = (int) animation.getAnimatedValue();
                    v.requestLayout();
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.setDuration(350);
            valueAnimator.start();
        }

        /**
         * Collapses card to simplified view.
         */
        public void collapse() {
            final View v = card;
            int prevHeight = v.getHeight();

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new DecelerateInterpolator());
            fadeOut.setDuration(200);
            expandContainer.startAnimation(fadeOut);

            expandContainer.setVisibility(View.GONE);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            v.measure(widthSpec, heightSpec);
            final int targetHeight = v.getMeasuredHeight();

            ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.getLayoutParams().height = (int) animation.getAnimatedValue();
                    v.requestLayout();
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.setDuration(200);
            valueAnimator.start();
        }
    }

    /**
     * Fill the contents of the page with the appropriate data.
     *
     * @param pvh view holder
     * @param position position in data
     */
    @Override
    public void onBindViewHolder(final ProfileAdapter.ProfileViewHolder pvh, final int position) {
        final Map<String, Object> item = data.get(position);

        setHabitNameAndIcon(pvh, item);
        setStatistics(pvh, item);
        setCalendar(pvh, item);
        setChart(pvh, item);
        setLikes(pvh, item);
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

    private void setHabitNameAndIcon(ProfileViewHolder pvh, Map<String, Object> item) {
        //name of habit
        String habitName = (item.get("habitName") != null)? item.get("habitName").toString() : "Habit Name";
        pvh.habitName.setText(habitName);

        //habit icon
        Drawable habitIcon = (item.get("iconId") != null)?
                ContextCompat.getDrawable(context, (int) item.get("iconId")):
                ContextCompat.getDrawable(context, R.drawable.app_icon);
        pvh.habitIcon.setImageDrawable(habitIcon);
    }

    /**
     * Sets extra statistics of habit.
     *
     * @param pvh view holder
     * @param item element of data
     */
    private void setStatistics(ProfileViewHolder pvh, Map<String, Object> item) {
        //frequency
        String frequency = (item.get("frequency") != null)? item.get("frequency").toString() : "days";
        pvh.frequency.setText(frequency);
        pvh.bestFrequency.setText(frequency);
        pvh.totalFrequency.setText(frequency);

        //current streak of habit
        String curStreakNum = (item.get("curStreak") != null)? item.get("curStreak").toString() : "0";
        pvh.streak.setText(curStreakNum);

        //best streak of habit
        String bestStreakNum = (item.get("bestStreak") != null)? item.get("bestStreak").toString() : "0";
        pvh.bestStreak.setText(bestStreakNum);

        //total
        String totalNum = (item.get("total") != null)? item.get("total").toString() : "0";
        pvh.total.setText(totalNum);
    }

    /**
     * Sets calendar of completed days.
     *
     * @param pvh view holder
     * @param item element of data
     */
    private void setCalendar(ProfileViewHolder pvh, Map<String, Object> item) {
        ArrayList<CalendarDay> dates = (ArrayList<CalendarDay>) item.get("dates");
        if (dates == null || dates.size() <= 0) return;

        //select days
        for (CalendarDay date : dates)
            pvh.mcv.setDateSelected(date, true);
    }

    /**
     * Sets completion chart.
     *
     * @param pvh view holder
     * @param item element of data
     */
    private void setChart(ProfileViewHolder pvh, Map<String, Object> item) {
        //data
        List<SliceValue> pieData = new ArrayList<>();
        int grey = ContextCompat.getColor(context, R.color.pie_off);
        pieData.add(new SliceValue(17, grey));
        pieData.add(new SliceValue(83, ContextCompat.getColor(context, R.color.purple)));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasCenterCircle(true).setCenterCircleScale(0.75f);
        pvh.chart.setPieChartData(pieChartData);
    }

    /**
     * Sets number of likes the habit has.
     *
     * @param pvh view holder
     * @param item element of data
     */
    private void setLikes(ProfileViewHolder pvh, Map<String, Object> item) {
        //number of likes
        String likesNum = (item.get("likes") != null)?
                item.get("likes").toString() : "0";
        String likeStr = likesNum + " likes";
        pvh.likes.setText(likeStr);
    }
}
