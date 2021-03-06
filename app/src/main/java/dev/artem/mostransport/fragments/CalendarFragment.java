package dev.artem.mostransport.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.artem.mostransport.R;
import dev.artem.mostransport.activities.MainActivity;
import dev.artem.mostransport.adapters.CalendarAdapter;
import dev.artem.mostransport.adapters.StreetsRecycleAdapter;
import dev.artem.mostransport.models.Mark;
import dev.artem.mostransport.models.Street;
import dev.artem.mostransport.utils.SpacesItemDecoration;

public class CalendarFragment extends androidx.fragment.app.DialogFragment implements CalendarAdapter.OnItemListener {
    private DatabaseReference mDatabase;
    private DatabaseReference streetsReference;
    private DatabaseReference marksReference;

    public static Map<Integer, String[]> Month = new HashMap<Integer, String[]>();

    public TextView dayStart;
    public TextView monthStart;
    public TextView yearsStart;
    public TextView dayEnd;
    public TextView monthEnd;
    public TextView yearsEnd;

    public TextView hours;
    public TextView minutes;

    public ImageView hoursUp;
    public ImageView hoursDown;
    public ImageView minutesUp;
    public ImageView minutesDown;
    public Button backBTN;

    public Button buttonSelect;

    public static int Time = 510;

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    private Boolean start = false;

    private int dayStartInt, dayEndInt = 0;
    private LocalDate dateStart, dateEnd = null;

    static GenericTypeIndicator<ArrayList<Street>> genericTypeIndicator;
    static GenericTypeIndicator<ArrayList<Mark>> genericTypeIndicator2;


    MainActivity activity;
    private View rootView;

    public interface OnInputListener {
        void sendInput(int time, int day1, int day2, LocalDate input1, LocalDate input2);
    }

    public OnInputListener onInputListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

        try {
            onInputListener = (OnInputListener) getTargetFragment();
            Log.d(getClass().getSimpleName(), "onAttach: " + onInputListener );
        } catch (ClassCastException e) {
            Log.d(getClass().getSimpleName(), "onAttach: ClassCastException : " + e.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_calendar, null);

        Month.put(1, "???????????? ??????".split(" "));
        Month.put(2, "?????????????? ????????".split(" "));
        Month.put(3, "???????? ????????".split(" "));
        Month.put(4, "???????????? ??????".split(" "));
        Month.put(5, "?????? ??????".split(" "));
        Month.put(6, "???????? ????????".split(" "));
        Month.put(7, "???????? ????????".split(" "));
        Month.put(8, "???????????? ??????".split(" "));
        Month.put(9, "???????????????? ????????".split(" "));
        Month.put(10, "?????????????? ??????".split(" "));
        Month.put(11, "???????????? ????????".split(" "));
        Month.put(12, "?????????????? ??????".split(" "));

        dayStart = (TextView) rootView.findViewById(R.id.DaysStart);
        monthStart = (TextView) rootView.findViewById(R.id.MonthStart);
        yearsStart = (TextView) rootView.findViewById(R.id.YearsStart);
        dayEnd = (TextView) rootView.findViewById(R.id.DaysEnd);
        monthEnd = (TextView) rootView.findViewById(R.id.MonthEnd);
        yearsEnd = (TextView) rootView.findViewById(R.id.YearsEnd);
        backBTN = (Button) rootView.findViewById(R.id.button2);
        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);

        hoursUp = (ImageView) rootView.findViewById(R.id.hoursUp);
        hoursDown = (ImageView) rootView.findViewById(R.id.hoursDown);
        minutesUp = (ImageView) rootView.findViewById(R.id.minutesUp);
        minutesDown = (ImageView) rootView.findViewById(R.id.minutesDown);
        rootView.findViewById(R.id.nextMonth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonthAction();
            }
        });
        rootView.findViewById(R.id.previousMonth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonthAction();
            }
        });
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.equals(hoursUp)) Time += 60;
                if (view.equals(hoursDown)) Time -= 60;
                if (view.equals(minutesUp)) Time += 1;
                if (view.equals(minutesDown)) Time -= 1;

                if (Time > 1440) {
                    Time = 1440;
                } else if (Time < 0) {
                    Time = 0;
                }

                String time_hours = String.valueOf(Time / 60);
                if (time_hours.length() == 1) time_hours = "0" + time_hours;
                String time_minutes = String.valueOf(Time % 60);
                if (time_minutes.length() == 1) time_minutes = "0" + time_minutes;

                hours.setText(time_hours);
                minutes.setText(time_minutes);
            }
        };
        hoursUp.setOnClickListener(clickListener);
        hoursDown.setOnClickListener(clickListener);
        minutesUp.setOnClickListener(clickListener);
        minutesDown.setOnClickListener(clickListener);

        hours = (TextView) rootView.findViewById(R.id.hours);
        minutes = (TextView) rootView.findViewById(R.id.minutes);

        buttonSelect = (Button) rootView.findViewById(R.id.buttonSelect);

        initWidgets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = LocalDate.now();
        }
        setMonthView();
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.big_container, MapFragment.newInstance());
                ft.commit();
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });
        // ------------------------------------------------- ???????????? ???????????? ------------------------------------------------->
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (dateStart != null && dateEnd != null) {
                    String monthFirst = monthYearFromDate(dateStart).split(" ")[0]; // ?????????? ?? ???????????????? ???????????????????? ????????????????????
                    String yearFirst = monthYearFromDate(dateStart).split(" ")[1]; // ?????? ?? ???????????????? ???????????????????? ????????????????????

                    String monthSecond = monthYearFromDate(dateEnd).split(" ")[0]; // ?????????? ?? ???????????????? ?????????????????????????? ????????????????????
                    String yearSecond = monthYearFromDate(dateEnd).split(" ")[1]; // ?????? ?? ???????????????? ?????????????????????????? ????????????????????

                    // Time - ???????????????????? ?? ?????????????? ???????????????? ?????????????????? ?????????? (?? ??????????????)
                    // dateStart - ???????????????????? ?? ?????????????? ???????????????? ???????? ???????????? ???????????????????? (LocalDate)
                    // dateEnd - ???????????????????? ?? ?????????????? ???????????????? ???????? ?????????? ???????????????????? (LocalDate)

                    if (onInputListener != null) {
                        onInputListener.sendInput(Time, dayStartInt, dayEndInt, dateStart, dateEnd);
                        Log.d("Calendar", "???????????????????? ????????????????????");
                    }
                    if (getDialog() != null){
                        getDialog().dismiss();
                    }

                    System.out.println("[!] ?????????????????? ??????????: " + Time + " | (????????/??????????/??????) C: " + dayStartInt + "/" + monthFirst + "/" + yearFirst + " ????: " + dayEndInt + "/" + monthSecond + "/" + yearSecond);
                } else {
                    Toast.makeText(activity, "???????????????? ????????????????????", Toast.LENGTH_LONG).show();
                }
            }
        });
        // ------------------------------------------------- ???????????? ???????????? -------------------------------------------------<
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
                ArrayList<Mark> marks = dataSnapshot.getValue(genericTypeIndicator2);
                RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.streets_list);
                recyclerView.addItemDecoration(new SpacesItemDecoration(20));
                StreetsRecycleAdapter adapter = new StreetsRecycleAdapter(activity, streets, marks);
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

                ((TextView) rootView.findViewById(R.id.habsTV)).setText(marks.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        streetsReference.addListenerForSingleValueEvent(marksValueListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
        CalendarAdapter calendarAdapter;

        if (dateStart != null && dateEnd != null) {
            if (less(dayStartInt, dayEndInt, dateStart, dateEnd)) {
                calendarAdapter = new CalendarAdapter(daysInMonth, this, dayStartInt, dayEndInt, dateStart, dateEnd, selectedDate);
            } else {
                calendarAdapter = new CalendarAdapter(daysInMonth, this, dayEndInt, dayStartInt, dateEnd, dateStart, selectedDate);
            }
        } else {
            calendarAdapter = new CalendarAdapter(daysInMonth, this, dayStartInt, dayEndInt, dateStart, dateEnd, selectedDate);
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity, 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i < 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }


        return daysInMonthArray;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return Month.get(date.getMonthValue())[0] + " " + date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getMonth(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return Month.get(date.getMonthValue())[1] + " " + date.format(formatter);
    }

    private void initWidgets() {
        calendarRecyclerView = rootView.findViewById(R.id.calendarRecyclerView);
        monthYearText = rootView.findViewById(R.id.MonthYear);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, TextView day) {
        String dayText = (String) day.getText();

        day.setBackgroundResource(R.drawable.days_cell);

        if (!dayText.equals("")) {
            start = !start;

            if (start) {
                dayStartInt = Integer.valueOf(dayText);
                dateStart = selectedDate;
                dateEnd = null;

                dayStart.setText("?");
                monthStart.setText("???");
                yearsStart.setText("????");
                dayEnd.setText("?");
                monthEnd.setText("???");
                yearsEnd.setText("????");
            } else {
                dayEndInt = Integer.valueOf(dayText);
                dateEnd = selectedDate;

                if (less(dayStartInt, dayEndInt, dateStart, dateEnd)) {
                    setRange(dayStartInt, dayEndInt, getMonth(dateStart), getMonth(dateEnd));
                } else {
                    setRange(dayEndInt, dayStartInt, getMonth(dateEnd), getMonth(dateStart));
                }
            }

            setMonthView();
        }
    }

    public void setRange(int day1, int day2, String MonthYearStart, String MonthYearEnd) {
        dayStart.setText(String.valueOf(day1));
        dayEnd.setText(String.valueOf(day2));

        monthStart.setText(MonthYearStart.split(" ")[0].toUpperCase());
        yearsStart.setText(MonthYearStart.split(" ")[1]);
        monthEnd.setText(MonthYearEnd.split(" ")[0].toUpperCase());
        yearsEnd.setText(MonthYearEnd.split(" ")[1]);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean less(int day1, int day2, LocalDate date1, LocalDate date2) {

        if (date1.getYear() < date2.getYear()) return true;
        if (date1.getYear() > date2.getYear()) return false;

        if (date1.getMonthValue() < date2.getMonthValue()) return true;
        if (date1.getMonthValue() > date2.getMonthValue()) return false;

        if (day1 <= day2) return true;
        if (day1 > day2) return false;

        return false;
    }


}
