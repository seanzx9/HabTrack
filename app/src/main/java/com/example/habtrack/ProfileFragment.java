package com.example.habtrack;

import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //initialize top bar color
        final Window window = getActivity().getWindow();
        final CoordinatorLayout cl = (CoordinatorLayout) view.findViewById(R.id.main_layout);
        cl.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple));
        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.purple));
        window.getDecorView().setSystemUiVisibility(
                view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        return view;
    }
}