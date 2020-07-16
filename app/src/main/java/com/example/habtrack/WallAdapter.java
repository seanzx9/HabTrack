package com.example.habtrack;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.WallViewHolder> {
    private ArrayList<HashMap<String, Object>> data;
    private RecyclerView.LayoutManager layoutManager;

    /**
     * Constructor with parameters.
     *
     * @param data data to update wall with
     */
    public WallAdapter(ArrayList<HashMap<String, Object>> data, RecyclerView.LayoutManager lm) {
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
    public WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wall_item, parent, false);
        return new WallViewHolder(v);
    }

    /**
     * initialize components in card view
     */
    public static class WallViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout card;
        private CircleImageView profilePic;
        private TextView username;
        private ImageView habitIcon;
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
        private ToggleButton likeIcon;
        private TextView likes;
        private ToggleButton expandButton;

        public WallViewHolder(View v) {
            super(v);
            card = (ConstraintLayout) v.findViewById(R.id.card);
            profilePic = (CircleImageView) v.findViewById(R.id.profile_pic);
            username = (TextView) v.findViewById(R.id.username);
            habitIcon = (ImageView) v.findViewById(R.id.habit_icon);
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
            likeIcon = (ToggleButton) v.findViewById(R.id.like_icon);
            likes = (TextView) v.findViewById(R.id.likes);
            expandButton = (ToggleButton) v.findViewById(R.id.expand_button);
        }
    }

    /**
     * Fill the contents of the page with the appropriate data.
     *
     * @param wvh view holder
     * @param position position in data
     */
    @Override
    public void onBindViewHolder(final WallViewHolder wvh, final int position) {
        final HashMap<String, Object> item = data.get(position);

        wvh.setIsRecyclable(false);

        setProfile(wvh, item);
        setHabitNameAndIcon(wvh, item);
        setStatistics(wvh, item);
        setCalendar(wvh, item);
        setChart(wvh, item);
        setExpandButton(wvh, position);
        setLikePanel(wvh, item);
    }

    /**
     * Sets profile image and username of habit's creator.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setProfile(final WallViewHolder wvh, HashMap<String, Object> item) {
        //profile image
        Drawable pfp;
         if (item.get("pfp") != null) {
             pfp = ContextCompat.getDrawable(wvh.habitIcon.getContext(), (int) item.get("pfp"));
         }
         else {
             pfp = ContextCompat.getDrawable(wvh.habitIcon.getContext(), R.drawable.default_pfp);
             wvh.profilePic.setImageTintList(ColorStateList
                     .valueOf(ContextCompat.getColor(wvh.card.getContext(), R.color.icon_tint)));
         }
        wvh.profilePic.setImageDrawable(pfp);

        //username
        String usernameStr = (item.get("username") != null)? item.get("username").toString() : "Habit Name";
        wvh.username.setText(usernameStr);
    }

    /**
     * Sets habit name and icon.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setHabitNameAndIcon(final WallViewHolder wvh, HashMap<String, Object> item) {
        //name of habit
        String habitName = (item.get("habitName") != null)?
                item.get("habitName").toString() : "Habit Name";
        wvh.habitName.setText(habitName);

        //habit icon
        Drawable habitIcon = (item.get("iconId") != null)?
                ContextCompat.getDrawable(wvh.habitIcon.getContext(), (int) item.get("iconId")):
                ContextCompat.getDrawable(wvh.habitIcon.getContext(), R.drawable.app_icon);
        wvh.habitIcon.setImageDrawable(habitIcon);
    }

    /**
     * Sets current streak, best streak, total, and date started.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setStatistics(final WallViewHolder wvh, HashMap<String, Object> item) {
        //frequency
        String frequency = (item.get("frequency") != null)? item.get("frequency").toString() : "days";
        wvh.frequency.setText(frequency);
        wvh.bestFrequency.setText(frequency);
        wvh.totalFrequency.setText(frequency);

        //current streak of habit
        String curStreakNum = (item.get("curStreak") != null)? item.get("curStreak").toString() : "0";
        wvh.streak.setText(curStreakNum);

        //best streak of habit
        String bestStreakNum = (item.get("bestStreak") != null)? item.get("bestStreak").toString() : "0";
        wvh.bestStreak.setText(bestStreakNum);

        //total
        String totalNum = (item.get("total") != null)? item.get("total").toString() : "0";
        wvh.total.setText(totalNum);
    }

    /**
     * Sets calender view.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setCalendar(final WallViewHolder wvh, HashMap<String, Object> item) {
        CalendarDay[] dates = (CalendarDay[]) item.get("dates");
        if (dates == null || dates.length <= 0) return;

        //selection color
        wvh.mcv.setSelectionColor(getCalendarColor(wvh, item));

        //select days
        for (CalendarDay date : dates)
            wvh.mcv.setDateSelected(date, true);
    }

    /**
     * Sets pie chart data.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setChart(final WallViewHolder wvh, HashMap<String, Object> item) {
        //data
        List<SliceValue> pieData = new ArrayList<>();
        int grey = ContextCompat.getColor(wvh.chart.getContext(), R.color.pie_off);
        pieData.add(new SliceValue(17, grey));
        pieData.add(new SliceValue(83, getColor(wvh, item)));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasCenterCircle(true).setCenterCircleScale(0.75f);
        wvh.chart.setPieChartData(pieChartData);

        //chart percent text color
        wvh.percent.setTextColor(getColor(wvh, item));
    }

    /**
     * Sets listener for expand button.
     *
     * @param wvh view holder
     * @param pos position of card in recycler view
     */
    private void setExpandButton(final WallViewHolder wvh, final int pos) {
        //toggle button to expand card
        wvh.expandButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    wvh.expandButton.animate().rotation(180).setDuration(350).start();
                    expand(wvh);

                }
                else {
                    wvh.expandButton.animate().rotation(0).setDuration(350).start();
                    collapse(wvh);
                }

                RecyclerView.SmoothScroller smoothScroller = new
                        LinearSmoothScroller(wvh.card.getContext()) {
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
                smoothScroller.setTargetPosition(pos);
                layoutManager.startSmoothScroll(smoothScroller);
            }
        });
    }

    /**
     * Sets like button toggle and number of likes.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setLikePanel(final WallViewHolder wvh, final HashMap<String, Object> item) {
        //like listener
        wvh.likeIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    wvh.likeIcon.setBackgroundTintList(ColorStateList.valueOf(getColor(wvh, item)));
                }
                else {
                    int color = ContextCompat.getColor(wvh.likeIcon.getContext(), R.color.black);
                    wvh.likeIcon.setBackgroundTintList(ColorStateList.valueOf(color));
                }
            }
        });

        //like toggle
        boolean isLiked = (item.get("isLiked") != null) && (boolean) item.get("isLiked");
        wvh.likeIcon.setChecked(isLiked);

        //number of likes
        String likesNum = (item.get("likes") != null)?
                item.get("likes").toString() : "0";
        String likeStr = likesNum + " likes";
        wvh.likes.setText(likeStr);
    }

    /**
     * Gets and returns habit's color.
     *
     * @param item element of data
     * @return color of habit
     */
    private int getColor(final WallViewHolder wvh, HashMap<String, Object> item) {
        int colorId = (item.get("color") != null)? (int) item.get("color") : R.color.black;
        return ContextCompat.getColor(wvh.card.getContext(), colorId);
    }

    /**
     * Formats date for string.
     *
     * @param date date to format
     * @return formatted date string
     */
    private String formatDate(CalendarDay date) {
        String original = date.toString();
        String clean = original.replaceAll("[CalendrDy{}]", "");
        String[] split = clean.split("-");

        String year = split[0];
        String day = split[2];

        String monthNum = split[1];
        String month;
        switch (monthNum) {
            case "1":
                month = "Jan";
            case "2":
                month = "Feb";
            case "3":
                month = "Mar";
            case "4":
                month = "Apr";
            case "5":
                month = "May";
            case "6":
                month = "Jun";
            case "7":
                month = "Jul";
            case "8":
                month = "Aug";
            case "9":
                month = "Sep";
            case "10":
                month = "Oct";
            case "11":
                month = "Nov";
            case "12":
                month = "Dec";
            default:
                month = "Jan";
        }

        return month + " " + day + ", " + year;
    }

    /**
     * Returns selection color of calendar depending on habit color.
     *
     * @param wvh view holder
     * @param item element of data
     * @return selection color of calendar view
     */
    private int getCalendarColor(final WallViewHolder wvh, HashMap<String, Object> item) {
        int colorId = (item.get("color") != null)? (int) item.get("color") : R.color.black;
        switch(colorId) {
            case R.color.purple:
                return ContextCompat.getColor(wvh.card.getContext(), R.color.calendar_purple);
            case R.color.red:
                return ContextCompat.getColor(wvh.card.getContext(), R.color.calendar_red);
            case R.color.yellow:
                return ContextCompat.getColor(wvh.card.getContext(), R.color.calendar_yellow);
            case R.color.blue:
                return ContextCompat.getColor(wvh.card.getContext(), R.color.calendar_blue);
            case R.color.green:
                return ContextCompat.getColor(wvh.card.getContext(), R.color.calendar_green);
            default:
                return ContextCompat.getColor(wvh.card.getContext(), R.color.calendar_black);
        }
    }

    /**
     * Getter for the size of the data.
     *
     * @return number of components in the data
     */
    @Override
    public int getItemCount() { return data.size(); }

    /**
     * Expand the card to show extra statistics.
     *
     * @param holder view holder
     */
    public void expand(WallAdapter.WallViewHolder holder) {
        final View v = holder.card;
        int prevHeight = v.getHeight();

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartOffset(50);
        fadeIn.setDuration(350);
        holder.expandContainer.startAnimation(fadeIn);

        holder.expandContainer.setVisibility(View.VISIBLE);

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
     *
     * @param holder view holder
     */
    public void collapse(WallAdapter.WallViewHolder holder) {
        final View v = holder.card;
        int prevHeight = v.getHeight();

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(200);
        holder.expandContainer.startAnimation(fadeOut);

        holder.expandContainer.setVisibility(View.GONE);

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
