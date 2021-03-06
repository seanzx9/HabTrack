package com.example.habtrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
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
     * @param data data to update friends list with
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
     * Initialize components in card view
     */
    public class FriendsViewHolder extends RecyclerView.ViewHolder {
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
            context = cardView.getContext();

            //remove friend
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getLayoutPosition();
                    Animation buttonPress = AnimationUtils.loadAnimation(context, R.anim.button_press);
                    remove.startAnimation(buttonPress);
                    ((Vibrator) context.getSystemService(VIBRATOR_SERVICE))
                            .vibrate(VibrationEffect.createOneShot(10,25));

                    Map<String, Object> item = data.get(pos);
                    String userStr = (item.get("username") != null)? item.get("username").toString() : "Username";

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to remove " + userStr + " as a friend?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                data.remove(pos);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, getItemCount());
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setCancelable(true)
                            .create();
                    dialog.getWindow().setBackgroundDrawable(ContextCompat
                            .getDrawable(context, R.drawable.rounded_dialog));
                    dialog.show();
                    dialog.getWindow().setDimAmount(0.75f);
                }
            });

            //view profile on click
            View.OnClickListener viewProfile = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewProfile.class);
                    Bundle extras = new Bundle();
                    Map<String, Object> item = data.get(getLayoutPosition());

                    //profile pic
                    Drawable pfp;
                    if (item.get("pfp") != null && (int) item.get("pfp") != R.drawable.default_pfp) {
                        pfp = ContextCompat.getDrawable(context, (int) item.get("pfp"));
                    }
                    else {
                        pfp = ContextCompat.getDrawable(context, R.drawable.default_pfp);
                        pfp.setTint(ContextCompat.getColor(context, R.color.white));
                    }
                    Bitmap bitmap = getBitmap(pfp);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    extras.putByteArray("pfp", b);

                    //username
                    String userStr = username.getText().toString();
                    extras.putString("username", userStr);

                    //bio
                    String bioStr = (item.get("bio") != null)? item.get("bio").toString() : "Bio";
                    extras.putString("bio", bioStr);

                    //number of friends
                    int numFriends = (item.get("numFriends") != null)? (int) item.get("numFriends") : 0;
                    extras.putString("numFriends", Integer.toString(numFriends));

                    //number of habits
                    int numHabits = (item.get("numHabits") != null)? (int) item.get("numHabits") : 0;
                    extras.putString("numHabits", Integer.toString(numHabits));

                    intent.putExtras(extras);
                    context.startActivity(intent);
                }
            };
            profilePic.setOnClickListener(viewProfile);
            username.setOnClickListener(viewProfile);
        }

        /**
         * Gets bitmap from drawable.
         *
         * @param drawable drawable to convert
         * @return bitmap of drawable
         */
        public Bitmap getBitmap(Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }
    }

    /**
     * Fill the contents of the page with the appropriate data.
     *
     * @param fvh view holder
     * @param position position in data
     */
    @Override
    public void onBindViewHolder(final FriendsAdapter.FriendsViewHolder fvh, final int position) {
        final Map<String, Object> item = data.get(position);
        context = fvh.cardView.getContext();

        setProfile(fvh, item);
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
     * Sets the profile pic and username.
     *
     * @param fvh view holder
     * @param item element of data
     */
    private void setProfile(FriendsViewHolder fvh, Map<String, Object> item) {
        //profile pic
        Drawable pfp;
        if (item.get("pfp") != null && (int) item.get("pfp") != R.drawable.default_pfp) {
            pfp = ContextCompat.getDrawable(context, (int) item.get("pfp"));
        }
        else {
            pfp = ContextCompat.getDrawable(context, R.drawable.default_pfp);
            fvh.profilePic.setImageTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(context, R.color.text)));
        }
        fvh.profilePic.setImageDrawable(pfp);

        //username
        String userStr = (item.get("username") != null)? item.get("username").toString() : "Username";
        fvh.username.setText(userStr);
    }
}
