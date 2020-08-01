package com.example.habtrack;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {
    private ArrayList<Map<String, Object>> data;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;

    /**
     * Constructor with parameters.
     *
     * @param data data to update wall with
     */
    public NotificationsAdapter(ArrayList<Map<String, Object>> data, RecyclerView.LayoutManager lm) {
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
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationsViewHolder(v);
    }

    /**
     * Initialize components in card view
     */
    public class NotificationsViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ConstraintLayout card;
        private CircleImageView profilePic;
        private TextView message;
        private TextView date;
        private ImageButton options;

        public NotificationsViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            card = (ConstraintLayout) v.findViewById(R.id.card);
            profilePic = (CircleImageView) v.findViewById(R.id.profile_pic);
            message = (TextView) v.findViewById(R.id.message);
            date = (TextView) v.findViewById(R.id.date);
            options = (ImageButton) v.findViewById(R.id.options);

            //notification options pop up
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, options);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem option) {
                            int pos = getLayoutPosition();
                            switch (option.getItemId()) {
                                case R.id.mark_read:
                                    Map<String, Object> item = data.get(pos);
                                    boolean isRead = getIsRead(pos);
                                    item.put("isRead", !isRead);
                                    data.set(pos, item);
                                    notifyItemChanged(pos);
                                    return true;
                                case R.id.delete:
                                    data.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos, getItemCount());
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    popup.inflate(R.menu.notification_options);
                    if (getIsRead(getLayoutPosition()))
                        popup.getMenu().getItem(0).setTitle(R.string.mark_unread);
                    popup.show();
                }
            });
        }
    }

    /**
     * Fill the contents of the page with the appropriate data.
     *
     * @param nvh view holder
     * @param position position in data
     */
    @Override
    public void onBindViewHolder(final NotificationsViewHolder nvh, final int position) {
        final Map<String, Object> item = data.get(position);
        context = nvh.cardView.getContext();

        setPic(nvh, item);
        setMessage(nvh, item);
        setDate(nvh, item);
        setOptionsColor(nvh);
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
     * Checks if notification is read
     *
     * @param pos position of view
     * @return true if read, false if not
     */
    private boolean getIsRead(int pos) {
        final Map<String, Object> item = data.get(pos);
        return (item.get("isRead") != null) && (boolean) item.get("isRead");
    }

    /**
     * Sets pic for notification.
     *
     * @param nvh view holder
     * @param item element of data
     *
     */
    private void setPic(NotificationsViewHolder nvh, Map<String, Object> item) {
        Drawable pfp;
        if (item.get("pfp") != null && (int) item.get("pfp") != R.drawable.default_pfp) {
            pfp = ContextCompat.getDrawable(context, (int) item.get("pfp"));
        }
        else {
            pfp = ContextCompat.getDrawable(context, R.drawable.default_pfp);
            nvh.profilePic.setImageTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(context, R.color.text)));
        }
        nvh.profilePic.setImageDrawable(pfp);
    }

    /**
     * Sets message of notification.
     *
     * @param nvh view holder
     * @param item element of data
     */
    private void setMessage(NotificationsViewHolder nvh, Map<String, Object> item) {
        String msgStr = (item.get("message") != null)? item.get("message").toString() : "Message";

        if (msgStr.contains("<USERNAME>")) {
            String usrStr = (item.get("username") != null)? item.get("username").toString() : "Username";
            msgStr = msgStr.replaceAll("<USERNAME>", usrStr);
        }
        else if (msgStr.contains("<HABIT_NAME>")) {
            String habStr = (item.get("habitName") != null)? item.get("habitName").toString() : "Habit";
            msgStr = msgStr.replaceAll("<HABIT_NAME>", habStr);
        }

        nvh.message.setText(msgStr);
    }

    /**
     * Sets the date of the notification.
     *
     * @param nvh view holder
     * @param item element of data
     */
    private void setDate(NotificationsViewHolder nvh, Map<String, Object> item) {
        String dateStr = (item.get("date") != null)? item.get("date").toString() : "Date";
        nvh.date.setText(dateStr);
    }

    /**
     * Sets up and handles options for notifications.
     *
     * @param nvh view holder
     */
    private void setOptionsColor(final NotificationsViewHolder nvh) {
        nvh.card.setBackgroundColor(ContextCompat.getColor(context,
                (getIsRead(nvh.getAdapterPosition()))? R.color.card : R.color.offset_purple));
    }
}
