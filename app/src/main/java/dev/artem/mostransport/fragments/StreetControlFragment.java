package dev.artem.mostransport.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;

import dev.artem.mostransport.GraphsActivity;
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
    public ImageView graficIV;
    public ImageView actionPlanIV;

    public RecyclerView streetRV;

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
        graficIV = (ImageView)rootView.findViewById(R.id.graficIV);
        actionPlanIV = (ImageView)rootView.findViewById(R.id.actionPlanIV);

        streetRV = (RecyclerView)rootView.findViewById(R.id.streetRV);

        int[] colors = {R.color.red, R.color.yellow};
        String[] typeProblem = {"Разрыв\nсети", "Высокая\nвибрация"};
        int[] numbersPlots = {1,3};
        String[] textPlots = {"участок", "участка"};

        KardsAdapter kardsAdapter = new KardsAdapter(activity, colors, typeProblem, numbersPlots, textPlots);
        streetRV.setAdapter(kardsAdapter);

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

        graficIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, GraphsActivity.class);
                startActivity(intent); activity.finish();
            }
        });

        actionPlanIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarFragment calendarFragment = new CalendarFragment();
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.big_container, calendarFragment);
                fragmentTransaction.commit();
            }
        });
    }

    static class KardsAdapter extends RecyclerView.Adapter<StreetControlFragment.KardsAdapter.ViewHolder> {
        private static GraphsAndReportsFragment.GraphsAdapter.ClickListener clickListener;
        private final LayoutInflater inflater;

        private int[] color;
        private String[] typeProblem;
        private int[] numbersPlots;
        private String[] textPlots;

        private final Context context;

        public KardsAdapter(Context context, int[] color, String[] typeProblem, int[] numbersPlots, String[] textPlots) {
            this.context = context;
            this.color = color;
            this.typeProblem = typeProblem;
            this.numbersPlots = numbersPlots;
            this.textPlots = textPlots;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public StreetControlFragment.KardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_street_control, parent, false);
            return new StreetControlFragment.KardsAdapter.ViewHolder(view, context);
        }


        @Override
        public void onBindViewHolder(final StreetControlFragment.KardsAdapter.ViewHolder holder, int position) {
            holder.typeProblemTV.setText(String.valueOf(typeProblem[position]));
            holder.numbersPlotsTV.setText(String.valueOf(numbersPlots[position]));
            holder.textPlots.setText(String.valueOf(textPlots[position]));
            holder.itemStreetSolidStatus.setBackgroundResource(color[position]);
        }

        @Override
        public int getItemCount() {
            return typeProblem.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView typeProblemTV;
            TextView numbersPlotsTV;
            TextView textPlots;
            LinearLayout itemStreetSolidStatus;
            ViewHolder(View view, final Context context1) {
                super(view);
                typeProblemTV = view.findViewById(R.id.typeProblemTV);
                numbersPlotsTV = view.findViewById(R.id.numbersPlotsTV);
                textPlots = view.findViewById(R.id.textPlots);
                itemStreetSolidStatus = view.findViewById(R.id.itemStreetSolidStatus);
            }

            @Override
            public void onClick(View view) {
                clickListener.onItemClick(getAdapterPosition(), view);
            }

            @Override
            public boolean onLongClick(View view) {
                clickListener.onItemLongClick(getAdapterPosition(), view);
                return false;
            }
        }

        public interface ClickListener {
            void onItemClick(int position, View v);
            void onItemLongClick(int position, View v);
        }
    }
}
