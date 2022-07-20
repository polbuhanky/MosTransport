package dev.artem.mostransport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import dev.artem.mostransport.R;
import dev.artem.mostransport.models.Mark;
import dev.artem.mostransport.models.Street;

public class DialogFragment extends androidx.fragment.app.DialogFragment implements View.OnClickListener {
    private final Context context;
    private Street street;
    private List<Mark> marks;

    public DialogFragment(Context context, Street street, List<Mark> marks){
        this.context = context;
        this.street = street;
        this.marks = marks;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.shield_dialog, null);

        TextView streetName = (TextView)v.findViewById(R.id.streetName);
        streetName.setText(street.getStreet_type() + " " + street.getStreet_name());

        TextView habsTV = (TextView)v.findViewById(R.id.habsTV);
        TextView chargeTV = (TextView)v.findViewById(R.id.chargeTV);
        TextView plotsTV = (TextView)v.findViewById(R.id.plotsTV);

        ImageView shieldImageView = (ImageView)v.findViewById(R.id.shieldImageView);
        String id_street = street.getId();

        // Поиск максимального уровня defect, также подсчет заряда и кол-во хабов --->
        int maxDefect = 0;
        int hubsCount = 0;
        double minVoltPhone = Double.valueOf(marks.get(0).getVolt_phone());
        double maxVoltPhone = 0;

        for (Mark m : marks){
            if (m.getStreet_id() != null) {
                if (m.getStreet_id().equals(id_street)) {
                    maxDefect = Math.max(Integer.parseInt(m.getDefect()), maxDefect);
                    hubsCount++;
                    if (minVoltPhone > Double.valueOf(m.getVolt_phone())) minVoltPhone = Double.valueOf(m.getVolt_phone());
                    if (maxVoltPhone < Double.valueOf(m.getVolt_phone())) maxVoltPhone = Double.valueOf(m.getVolt_phone());
                }
            }
        }
        habsTV.setText("Хабов\n" + hubsCount);
        chargeTV.setText("Заряд\n" + minVoltPhone + "В\n" + maxVoltPhone + "В");
        // Поиск максимального уровня defect, также подсчет заряда и кол-во хабов ---<

        int shieldIdBackground = R.drawable.shield_green;

        // Установка цвета щита в зависимости от уровня defect --->
        switch (maxDefect){
            case 1:
                shieldIdBackground = R.drawable.shield_green;
                break;
            case 2:
                shieldIdBackground = R.drawable.shield_yel;
                break;
            case 3:
                shieldIdBackground = R.drawable.shield_or;
                break;
            case 4:
                shieldIdBackground = R.drawable.shield_red;
                break;
        }
        shieldImageView.setImageResource(shieldIdBackground);
        // Установка цвета щита в зависимости от уровня defect ---<

        v.findViewById(R.id.shieldImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager myFragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                StreetControlFragment streetControlFragment = new StreetControlFragment();
                FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.big_container, streetControlFragment);
                fragmentTransaction.commit();

                dismiss();
            }
        });

        v.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
