package com.example.habtrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {
    private ArrayList<Integer> data;
    private int selected;

    /**
     * Constructor with parameters.
     *
     * @param data list of icons
     */
    public IconAdapter(ArrayList<Integer> data) {
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
    public IconAdapter.IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_item, parent, false);
        return new IconAdapter.IconViewHolder(v);
    }

    /**
     * Initialize components in card view
     */
    public class IconViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout container;
        private ImageView icon;

        public IconViewHolder(View v) {
            super(v);
            container = (ConstraintLayout) v.findViewById(R.id.container);
            icon = (ImageView) v.findViewById(R.id.icon);
            setIsRecyclable(false);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected = data.get(getLayoutPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Fill the contents of the page with the appropriate data.
     *
     * @param ivh view holder
     * @param position position in data
     */
    @Override
    public void onBindViewHolder(final IconAdapter.IconViewHolder ivh, final int position) {
        int id = data.get(position);

        ivh.icon.setImageDrawable(ContextCompat.getDrawable(ivh.icon.getContext(), id));

        if (id == selected)
            ivh.container.setBackground(ContextCompat
                    .getDrawable(ivh.container.getContext(),R.drawable.icon_selected));
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
