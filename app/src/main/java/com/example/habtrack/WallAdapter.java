package com.example.habtrack;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import static android.content.Context.VIBRATOR_SERVICE;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.WallViewHolder> {
    private ArrayList<Map<String, Object>> data;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    /**
     * Constructor with parameters.
     *
     * @param data data to update wall with
     */
    public WallAdapter(ArrayList<Map<String, Object>> data, RecyclerView.LayoutManager lm) {
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
     * Initialize components in card view
     */
    public static class WallViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
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
            cardView = (CardView) v.findViewById(R.id.card_view);
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
        final Map<String, Object> item = data.get(position);
        context = wvh.cardView.getContext();

        wvh.setIsRecyclable(false);
        if (position == 0) adjustMarginTop(wvh);
        if (position == data.size() - 1) adjustMarginBottom(wvh);

        setProfile(wvh, item);
        setHabitNameAndIcon(wvh, item);
        setStatistics(wvh, item);
        setCalendar(wvh, item);
        setChart(wvh, item);
        setExpandButton(wvh, position);
        setLikePanel(wvh, item);
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

    private void adjustMarginTop(WallViewHolder wvh) {
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) wvh.cardView.getLayoutParams();
        int topMargin = dpToPx(context, 15);
        int sideMargin = dpToPx(context, 10);
        layoutParams.setMargins(sideMargin, topMargin, sideMargin, 0);
        wvh.cardView.requestLayout();
    }

    private void adjustMarginBottom(WallViewHolder wvh) {
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) wvh.cardView.getLayoutParams();
        int topMargin = dpToPx(context, 20);
        int bottomMargin = dpToPx(context, 25);
        int sideMargin = dpToPx(context, 10);
        layoutParams.setMargins(sideMargin, topMargin, sideMargin, bottomMargin);
        wvh.cardView.requestLayout();
    }

    /**
     * Sets profile image and username of habit's creator.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setProfile(WallViewHolder wvh, Map<String, Object> item) {
        //profile image
        Drawable pfp;
        if (item.get("pfp") != null && (int) item.get("pfp") != R.drawable.default_pfp) {
            pfp = ContextCompat.getDrawable(context, (int) item.get("pfp"));
        }
        else {
            pfp = ContextCompat.getDrawable(context, R.drawable.default_pfp);
            wvh.profilePic.setBorderColor(ContextCompat.getColor(context, android.R.color.transparent));
            wvh.profilePic.setImageTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(context, R.color.text)));
        }
        wvh.profilePic.setImageDrawable(pfp);

        //username
        String usrStr = (item.get("username") != null)? item.get("username").toString() : "Username";
        wvh.username.setText(usrStr);
    }

    /**
     * Sets habit name and icon.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setHabitNameAndIcon(WallViewHolder wvh, Map<String, Object> item) {
        //name of habit
        String habitName = (item.get("habitName") != null)? item.get("habitName").toString() : "Habit Name";
        wvh.habitName.setText(habitName);

        //habit icon
        Drawable habitIcon = (item.get("iconId") != null)?
                ContextCompat.getDrawable(context, (int) item.get("iconId")):
                ContextCompat.getDrawable(context, R.drawable.app_icon);
        wvh.habitIcon.setImageDrawable(habitIcon);
    }

    /**
     * Sets current streak, best streak, total, and date started.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setStatistics(WallViewHolder wvh, Map<String, Object> item) {
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
    private void setCalendar(WallViewHolder wvh, Map<String, Object> item) {
        CalendarDay[] dates = (CalendarDay[]) item.get("dates");
        if (dates == null || dates.length <= 0) return;

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
    private void setChart(WallViewHolder wvh, Map<String, Object> item) {
        //data
        List<SliceValue> pieData = new ArrayList<>();
        int grey = ContextCompat.getColor(context, R.color.pie_off);
        pieData.add(new SliceValue(17, grey));
        pieData.add(new SliceValue(83, getColor(R.color.purple)));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasCenterCircle(true).setCenterCircleScale(0.75f);
        wvh.chart.setPieChartData(pieChartData);
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
                smoothScroller.setTargetPosition(pos);
                layoutManager.startSmoothScroll(smoothScroller);
            }
        });

        wvh.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                wvh.expandButton.performClick();
                ((Vibrator) context.getSystemService(VIBRATOR_SERVICE))
                        .vibrate(VibrationEffect.createOneShot(10,25));
                return false;
            }
        });
    }

    /**
     * Sets like button toggle and number of likes.
     *
     * @param wvh view holder
     * @param item element of data
     */
    private void setLikePanel(final WallViewHolder wvh, final Map<String, Object> item) {
        //like listener
        wvh.likeIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    int purple = ContextCompat.getColor(context, R.color.purple);
                    wvh.likeIcon.setBackgroundTintList(ColorStateList.valueOf(purple));
                }
                else {
                    int icon = ContextCompat.getColor(context, R.color.like_tint);
                    wvh.likeIcon.setBackgroundTintList(ColorStateList.valueOf(icon));
                }
            }
        });

        wvh.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wvh.likeIcon.performClick();
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
     * Gets color by id.
     *
     * @param id color id
     * @return color from id
     */
    private int getColor(int id) {
        return ContextCompat.getColor(context, id);
    }

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

    /**
     * Converts dp to px.
     *
     * @param dp value in dp
     * @param context context to get resources and device specific display metrics
     * @return converted value as px
     */
    public int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
