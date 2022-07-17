package dev.artem.mostransport.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.artem.mostransport.R;
import dev.artem.mostransport.activities.MainActivity;
import dev.artem.mostransport.adapters.StreetsRecycleAdapter;
import dev.artem.mostransport.models.Mark;
import dev.artem.mostransport.models.Street;
import dev.artem.mostransport.utils.SpacesItemDecoration;

public class StreetsFragment extends Fragment {
    private DatabaseReference mDatabase;
    private DatabaseReference streetsReference;
    private DatabaseReference marksReference;

    private TextView textViewToolBar;

    static GenericTypeIndicator<ArrayList<Street>> genericTypeIndicator;
    static GenericTypeIndicator<ArrayList<Mark>> genericTypeIndicator2;

    MainActivity activity;
    private View rootView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_streets, null);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imageToolBar).setVisibility(View.GONE);
        toolbar.findViewById(R.id.search_icon).setVisibility(View.GONE);
        textViewToolBar = (TextView)toolbar.findViewById(R.id.textViewToolBar);
        textViewToolBar.setText("Список улиц");
        textViewToolBar.setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.ham_icon).setVisibility(View.GONE);
        toolbar.findViewById(R.id.backBTN).setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        init();
        return rootView;
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        streetsReference = mDatabase.child("streets");
        marksReference = mDatabase.child("allMarks");

        genericTypeIndicator = new GenericTypeIndicator<ArrayList<Street>>() {};
        genericTypeIndicator2 = new GenericTypeIndicator<ArrayList<Mark>>() {};

        initListeners();
    }

    private void initListeners() {
        ValueEventListener streetsValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Street> streets = dataSnapshot.getValue(genericTypeIndicator);
                RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.streets_list);
                recyclerView.addItemDecoration(new SpacesItemDecoration(20));
                StreetsRecycleAdapter adapter = new StreetsRecycleAdapter(activity, streets);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        streetsReference.addListenerForSingleValueEvent(streetsValueListener);
        ValueEventListener marksValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Mark> marks = dataSnapshot.getValue(genericTypeIndicator2);

                ((TextView)rootView.findViewById(R.id.habsTV)).setText(String.valueOf(marks.size()));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        marksReference.addListenerForSingleValueEvent(marksValueListener);
    }
}