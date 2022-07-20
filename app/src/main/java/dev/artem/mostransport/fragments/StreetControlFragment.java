package dev.artem.mostransport.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;

import dev.artem.mostransport.R;
import dev.artem.mostransport.activities.MainActivity;
import dev.artem.mostransport.models.Mark;
import dev.artem.mostransport.models.Street;

public class StreetControlFragment extends Fragment {
    private DatabaseReference mDatabase;
    private DatabaseReference streetsReference;
    private DatabaseReference marksReference;

    static GenericTypeIndicator<ArrayList<Street>> genericTypeIndicator;
    static GenericTypeIndicator<ArrayList<Mark>> genericTypeIndicator2;

    public ImageView backBTN;

    MainActivity activity;
    private View rootView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.street_control, null);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);

        init();
        return rootView;
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        streetsReference = mDatabase.child("streets");
        marksReference = mDatabase.child("allMarks");

        genericTypeIndicator = new GenericTypeIndicator<ArrayList<Street>>() {};
        genericTypeIndicator2 = new GenericTypeIndicator<ArrayList<Mark>>() {};

        backBTN = (ImageView)rootView.findViewById(R.id.backBTN);

        initListeners();
    }

    private void initListeners() {
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.big_container, MapFragment.newInstance());
                ft.commit();
            }
        });

    }
}
