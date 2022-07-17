package dev.artem.mostransport.adapters;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

import dev.artem.mostransport.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;

    private int dayStartInt;
    private int dayEndInt;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private LocalDate dateNow;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener, int dayStart, int dayEnd, LocalDate dateStart, LocalDate dateEnd, LocalDate dateNow) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.dayStartInt = dayStart;
        this.dayEndInt = dayEnd;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.dateNow = dateNow;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_sample, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.16);
        return new CalendarViewHolder(view, onItemListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String days = daysOfMonth.get(position);
        holder.dayOfMonth.setText(days);
        holder.dayOfMonth.setTextSize(12);
        holder.dayOfMonth.setBackgroundResource(0);
        holder.dayOfMonth.setTextColor(Color.GRAY);

        if (days != "") {
            int daysInt = Integer.valueOf(days);

            if (dateStart != null && dateEnd != null) {

                if (Less(dayStartInt, daysInt, dateStart, dateNow) && Less(daysInt, dayEndInt, dateNow, dateEnd)) {
                    holder.dayOfMonth.setTextColor(Color.RED);
                    System.out.println("Less --------------- RED --------------");
                }else if (dateStart.isEqual(dateNow) && dateEnd.isEqual(dateNow)){
                    if (dayStartInt < daysInt && daysInt < dayEndInt) {
                        holder.dayOfMonth.setTextColor(Color.RED);
                        System.out.println("1 --------------- RED --------------");
                    }
                    if (daysInt == dayStartInt){
                        holder.dayOfMonth.setTextColor(Color.WHITE);
                        holder.dayOfMonth.setBackgroundResource(R.drawable.days_cell);
                    }
                    if (daysInt == dayEndInt){
                        holder.dayOfMonth.setTextColor(Color.WHITE);
                        holder.dayOfMonth.setBackgroundResource(R.drawable.days_cell);
                    }
                }else {
                    if (dateStart.isEqual(dateNow)) {
                        if (dayStartInt < daysInt) {
                            holder.dayOfMonth.setTextColor(Color.RED);
                            System.out.println("2 --------------- RED --------------");
                        }
                        if (daysInt == dayStartInt){
                            holder.dayOfMonth.setTextColor(Color.WHITE);
                            holder.dayOfMonth.setBackgroundResource(R.drawable.days_cell);
                        }
                    }

                    if (dateEnd.isEqual(dateNow)) {
                        if (daysInt < dayEndInt) {
                            holder.dayOfMonth.setTextColor(Color.RED);
                            System.out.println("3 --------------- RED --------------");
                        }
                        if (daysInt == dayEndInt){
                            holder.dayOfMonth.setTextColor(Color.WHITE);
                            holder.dayOfMonth.setBackgroundResource(R.drawable.days_cell);
                        }
                    }
                }

            }else if (dayStartInt != 0){
                if (daysInt == dayStartInt && dateStart.isEqual(dateNow)){
                    holder.dayOfMonth.setTextColor(Color.WHITE);
                    holder.dayOfMonth.setBackgroundResource(R.drawable.days_cell);
                }
            }

        }



        //System.out.println(position);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener{
        void onItemClick(int position, TextView day);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean Less(int day1, int day2, LocalDate date1, LocalDate date2){

        if (date1.getYear() < date2.getYear()) return true;
        if (date1.getYear() > date2.getYear()) return false;

        if (date1.getMonthValue() < date2.getMonthValue()) return true;
        if (date1.getMonthValue() > date2.getMonthValue()) return false;

        if (day1 < day2) return true;
        if (day1 > day2) return false;

        return false;
    }
}
