package dev.artem.mostransport.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.listeners.OnCreateViewHolderListener;

import java.time.LocalDate;
import java.util.ArrayList;

import dev.artem.mostransport.GraphsActivity;
import dev.artem.mostransport.R;
import dev.artem.mostransport.activities.MainActivity;
import dev.artem.mostransport.adapters.CalendarAdapter;
import dev.artem.mostransport.models.Mark;
import dev.artem.mostransport.models.Street;

public class GraphsAndReportsFragment extends Fragment implements CalendarFragment.OnInputListener {

    private DatabaseReference mDatabase;
    private DatabaseReference streetsReference;
    private DatabaseReference marksReference;

    private ImageView moreData;
    private ImageView moreMonitoring;
    private ImageView morePeriod;
    private ImageView morePlots;
    private ImageView moreStatus;

    ArrayList<Street> allStreets = new ArrayList<>();

    private boolean statData = false;
    private boolean statMonitoring = false;
    private boolean statPeriod = false;
    private boolean statPlots = false;
    private boolean statStatus = false;

    static GenericTypeIndicator<ArrayList<Street>> genericTypeIndicator;
    static GenericTypeIndicator<ArrayList<Mark>> genericTypeIndicator2;

    public Button closeBTN;
    public Button buildBTN;

    MainActivity activity;
    private View rootView;

    RecyclerView recyclerViewData;
    RecyclerView recyclerViewMonitoring;
    RecyclerView recyclerViewPlots;
    RecyclerView recyclerViewStatus;

    GraphsAdapter adapter;

    TextView dataTV;
    TextView parametrsMonitoring;
    TextView periodTV;
    TextView selectPlotsTV;
    TextView statusTV;

    CalendarFragment calendarFragment;

    final String[] data = new String[] {
            "данные по событиям", "данные по хабу", "данные по участку"
    };

    final String[] dataMonitoring = new String[] {
            "дефекты сварных швов", "износ рабочих поверхностей рельсов", "недопустимые углы в плане", "наличие блуждающих токов", "горизонтальная вибрация",
            "вертикальные перепады" , "продольная вибрация", "вертикальная вибрация", "температура рельса", "уширение колеи", "кривизна путей", "влажность среды",
            "шумомер", "затопление", "разрыв сети"
    };

    final String[] dataStatus = new String[] {
            "норма", "хорошо", "плохо", "критично"
    };



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.graphs_and_reports, null);

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

        closeBTN = (Button)rootView.findViewById(R.id.closeBTN);
        buildBTN = (Button)rootView.findViewById(R.id.buildBTN);

        moreData = (ImageView)rootView.findViewById(R.id.moreData);
        moreMonitoring = (ImageView)rootView.findViewById(R.id.moreMonitoring);
        morePeriod = (ImageView)rootView.findViewById(R.id.morePeriod);
        morePlots = (ImageView)rootView.findViewById(R.id.morePlots);
        moreStatus = (ImageView)rootView.findViewById(R.id.moreStatus);

        recyclerViewData = rootView.findViewById(R.id.recyclerViewData);
        recyclerViewMonitoring = rootView.findViewById(R.id.recyclerViewMonitoring);
        recyclerViewPlots = rootView.findViewById(R.id.recyclerViewPlots);
        recyclerViewStatus = rootView.findViewById(R.id.recyclerViewStatus);

        dataTV = (TextView)rootView.findViewById(R.id.dataTV);
        parametrsMonitoring = (TextView)rootView.findViewById(R.id.parametrsMonitoring);
        periodTV = (TextView)rootView.findViewById(R.id.periodTV);
        selectPlotsTV = (TextView)rootView.findViewById(R.id.selectPlotsTV);
        statusTV = (TextView)rootView.findViewById(R.id.StatusTV);

        initListeners();
    }

    private void initListeners() {
        ValueEventListener valueListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allStreets.addAll(dataSnapshot.getValue(genericTypeIndicator));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        streetsReference.addListenerForSingleValueEvent(valueListener1);

        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.big_container, MapFragment.newInstance());
                ft.commit();
            }
        });

        buildBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, GraphsActivity.class);
                startActivity(intent); activity.finish();
            }
        });

        moreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statData){
                    // Закрытие списка --->
                    recyclerViewData.setAdapter(null);
                    ViewGroup.LayoutParams params = recyclerViewData.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    recyclerViewData.setLayoutParams(params);

                    moreData.setImageResource(R.drawable.arrow_down);
                    // Закрытие списка ---<
                }else {
                    // Создание списка --->
                    adapter = new GraphsAdapter(activity, data);
                    recyclerViewData.setAdapter(adapter);

                    // Проверка размера списка --->
                    ViewGroup.LayoutParams params = recyclerViewData.getLayoutParams();
                    params.height = 400;
                    if (recyclerViewData.getHeight() > 400){
                        recyclerViewData.setLayoutParams(params);
                    }
                    // Проверка размера списка ---<

                    moreData.setImageResource(R.drawable.arrow_up);

                    // Слушатели обьктов списка --->
                    adapter.setOnItemClickListener(new GraphsAdapter.ClickListener(){

                        @Override
                        public void onItemClick(int position, View v) {
                            //Toast.makeText(activity, "ItemClick: " + data[position], Toast.LENGTH_SHORT).show();
                            dataTV.setText(data[position]);
                        }

                        @Override
                        public void onItemLongClick(int position, View v) {

                        }
                    });
                    // Слушатели обьктов списка ---<
                    // Создание списка ---<
                }
                statData = !statData;
            }
        });

        moreMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statMonitoring){
                    // Закрытие списка --->
                    recyclerViewMonitoring.setAdapter(null);
                    ViewGroup.LayoutParams params = recyclerViewMonitoring.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    recyclerViewMonitoring.setLayoutParams(params);

                    moreMonitoring.setImageResource(R.drawable.arrow_down);
                    // Закрытие списка ---<
                }else {
                    // Создание списка --->
                    adapter = new GraphsAdapter(activity, dataMonitoring);
                    recyclerViewMonitoring.setAdapter(adapter);

                    // Проверка размера списка --->
                    ViewGroup.LayoutParams params = recyclerViewMonitoring.getLayoutParams();
                    params.height = 400;
                    if (recyclerViewMonitoring.getHeight() > 400){
                        recyclerViewMonitoring.setLayoutParams(params);
                    }
                    // Проверка размера списка ---<

                    moreMonitoring.setImageResource(R.drawable.arrow_up);

                    // Слушатели обьктов списка --->
                    adapter.setOnItemClickListener(new GraphsAdapter.ClickListener(){

                        @Override
                        public void onItemClick(int position, View v) {
                            //Toast.makeText(activity, "ItemClick: " + dataMonitoring[position], Toast.LENGTH_SHORT).show();
                            parametrsMonitoring.setText(dataMonitoring[position]);
                        }

                        @Override
                        public void onItemLongClick(int position, View v) {

                        }
                    });
                    // Слушатели обьктов списка ---<
                    // Создание списка ---<
                }
                statMonitoring = !statMonitoring;
            }
        });

        morePeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statPeriod = true;

                calendarFragment = new CalendarFragment();
                calendarFragment.setTargetFragment(GraphsAndReportsFragment.this, 1);
                if (statPeriod) {
                    calendarFragment.show(activity.getSupportFragmentManager(), "calendar");
                }
            }
        });

        morePlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statPlots){
                    // Закрытие списка --->
                    recyclerViewPlots.setAdapter(null);
                    ViewGroup.LayoutParams params = recyclerViewPlots.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    recyclerViewPlots.setLayoutParams(params);

                    morePlots.setImageResource(R.drawable.arrow_down);
                    // Закрытие списка ---<
                }else {
                    // Создание списка --->
                    String[] dataPlots = new String[allStreets.size()];
                    for (int i = 0; i < allStreets.size(); i++){
                        dataPlots[i] = allStreets.get(i).getStreet_type() + " " + allStreets.get(i).getStreet_name();
                    }
                    adapter = new GraphsAdapter(activity, dataPlots);
                    recyclerViewPlots.setAdapter(adapter);

                    // Проверка размера списка --->
                    ViewGroup.LayoutParams params = recyclerViewPlots.getLayoutParams();
                    params.height = 400;
                    if (recyclerViewPlots.getHeight() > 400){
                        recyclerViewPlots.setLayoutParams(params);
                    }
                    // Проверка размера списка ---<

                    morePlots.setImageResource(R.drawable.arrow_up);

                    // Слушатели обьктов списка --->
                    adapter.setOnItemClickListener(new GraphsAdapter.ClickListener(){

                        @Override
                        public void onItemClick(int position, View v) {
                            //Toast.makeText(activity, "ItemClick: " + dataPlots[position], Toast.LENGTH_SHORT).show();
                            selectPlotsTV.setText(dataPlots[position]);
                        }

                        @Override
                        public void onItemLongClick(int position, View v) {

                        }
                    });
                    // Слушатели обьктов списка ---<
                    // Создание списка ---<
                }
                statPlots = !statPlots;
            }
        });

        moreStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statStatus){
                    // Закрытие списка --->
                    recyclerViewStatus.setAdapter(null);
                    ViewGroup.LayoutParams params = recyclerViewStatus.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    recyclerViewStatus.setLayoutParams(params);

                    moreStatus.setImageResource(R.drawable.arrow_down);
                    // Закрытие списка ---<
                }else {
                    // Создание списка --->
                    adapter = new GraphsAdapter(activity, dataStatus);
                    recyclerViewStatus.setAdapter(adapter);

                    // Проверка размера списка --->
                    ViewGroup.LayoutParams params = recyclerViewStatus.getLayoutParams();
                    params.height = 400;
                    if (recyclerViewStatus.getHeight() > 400){
                        recyclerViewStatus.setLayoutParams(params);
                    }
                    // Проверка размера списка ---<

                    moreStatus.setImageResource(R.drawable.arrow_up);

                    // Слушатели обьктов списка --->
                    adapter.setOnItemClickListener(new GraphsAdapter.ClickListener(){

                        @Override
                        public void onItemClick(int position, View v) {
                            //Toast.makeText(activity, "ItemClick: " + dataStatus[position], Toast.LENGTH_SHORT).show();
                            statusTV.setText(dataStatus[position]);
                        }

                        @Override
                        public void onItemLongClick(int position, View v) {

                        }
                    });
                    // Слушатели обьктов списка ---<
                    // Создание списка ---<
                }
                statStatus = !statStatus;
            }
        });
    }

    // Связь между GraphsAndReportsFragment и CalendarFragment --->
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void sendInput(int time, int day1, int day2, LocalDate input1, LocalDate input2) {
        String monthFirst = calendarFragment.monthYearFromDate(input1).split(" ")[0]; // Месяц с которого начинается промежуток
        String yearFirst = calendarFragment.monthYearFromDate(input1).split(" ")[1]; // Год с которого начинается промежуток

        String monthSecond = calendarFragment.monthYearFromDate(input2).split(" ")[0]; // Месяц с которого заканчивается промежуток
        String yearSecond = calendarFragment.monthYearFromDate(input2).split(" ")[1]; // Год с которого заканчивается промежуток

        periodTV.setText(String.valueOf("C: " + day1 + "/" + monthFirst + "/" + yearFirst + " До: " + day2 + "/" + monthSecond + "/" + yearSecond));
        statPeriod = false;
        Log.d("GraphsAndReports", "Информация обработана");
    }
    // Связь между GraphsAndReportsFragment и CalendarFragment ---<


    static class GraphsAdapter extends RecyclerView.Adapter<GraphsAndReportsFragment.GraphsAdapter.ViewHolder> {
        private static ClickListener clickListener;
        private final LayoutInflater inflater;
        private final String[]data;

        private final Context context;

        public GraphsAdapter(Context context, String[] data) {
            this.context = context;
            this.data = data;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public GraphsAndReportsFragment.GraphsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.graphs_and_reports_list_item, parent, false);
            return new GraphsAndReportsFragment.GraphsAdapter.ViewHolder(view, context);
        }


        @Override
        public void onBindViewHolder(final GraphsAndReportsFragment.GraphsAdapter.ViewHolder holder, int position) {
            holder.itemTV.setText(data[position]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView itemTV;
            LinearLayout background;
            ViewHolder(View view, final Context context1) {
                super(view);
                itemTV = view.findViewById(R.id.itemTV);
                background = view.findViewById(R.id.backgroundItem);
                background.setOnClickListener(this);
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

        public void setOnItemClickListener(ClickListener clickListener) {
            GraphsAdapter.clickListener = clickListener;
        }

        public interface ClickListener {
            void onItemClick(int position, View v);
            void onItemLongClick(int position, View v);
        }
    }
}

