package com.example.habtrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    private ArrayList<Map<String, Object>> data;
    private Context context;

    /**
     * Constructor with parameters.
     *
     * @param data data to update wall with
     */
    public FriendsAdapter(ArrayList<Map<String, Object>> data) {
        this.data = data;
    }

    /**
     * Inflates view with layout.
     *
     * @param parent parent ViewGroup
     * @param viewType type of view
     * @return new WallViewHolder
     */
    @Override
    public FriendsAdapter.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item, parent, false);
        return new FriendsAdapter.FriendsViewHolder(v);
    }

    /**
     * initialize components in card view
     */
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private CircleImageView profilePic;
        private TextView username;
        private ConstraintLayout remove;

        public FriendsViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            profilePic = (CircleImageView) v.findViewById(R.id.profile_pic);
            username = (TextView) v.findViewById(R.id.username);
            remove = (ConstraintLayout) v.findViewById(R.id.remove);
        }
    }

    /**
     * Fill the contents of the page with the appropriate data.
     *
     * @param pvh view holder
     * @param position position in data
     */
    @Override
    public void onBindViewHolder(final FriendsAdapter.FriendsViewHolder pvh, final int position) {
        final Map<String, Object> item = data.get(position);
        context = pvh.cardView.getContext();

        pvh.setIsRecyclable(false);

        //profile image
        Drawable pfp;
        if (item.get("pfp") != null && (int) item.get("pfp") != R.drawable.default_pfp) {
            pfp = ContextCompat.getDrawable(context, (int) item.get("pfp"));
        }
        else {
            pfp = ContextCompat.getDrawable(context, R.drawable.default_pfp);
            pvh.profilePic.setBorderColor(ContextCompat.getColor(context, R.color.background));
            pvh.profilePic.setImageTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(context, R.color.black)));
        }
        pvh.profilePic.setImageDrawable(pfp);

        //username
        final String usernameStr = (item.get("username") != null) ? item.get("username").toString() : "Username";
        pvh.username.setText(usernameStr);

        //remove click listener
        pvh.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = pvh.cardView.getContext();
                Animation buttonPress = AnimationUtils.loadAnimation(context, R.anim.button_press);
                pvh.remove.startAnimation(buttonPress);
                ((Vibrator) context.getSystemService(VIBRATOR_SERVICE))
                        .vibrate(VibrationEffect.createOneShot(10,25));

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to remove " + usernameStr + " as a friend?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                data.remove(pvh.getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setCancelable(true)
                        .create();
                dialog.getWindow().setBackgroundDrawable(ContextCompat
                        .getDrawable(context, R.drawable.rounded_dialog));
                dialog.show();
                Button btn1 = (Button) dialog.findViewById(android.R.id.button1);
            }
        });
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
}
