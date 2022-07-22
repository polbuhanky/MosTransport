package dev.artem.mostransport.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.artem.mostransport.GraphsActivity;
import dev.artem.mostransport.R;
import dev.artem.mostransport.activities.MainActivity;
import dev.artem.mostransport.custom.ProgressBarCustom;
import dev.artem.mostransport.models.Mark;
import dev.artem.mostransport.models.Street;
import dev.artem.mostransport.models.User;

public class MapFragment extends Fragment implements ClusterListener, MapObjectTapListener, SearchView.OnQueryTextListener{
    private DatabaseReference mDatabase;
    private DatabaseReference userReference;
    private DatabaseReference streetReference;

    private ImageView railWear;
    private ImageView gaugeMonitor;
    private ImageView vibration;
    private ImageView networkBreak;
    private ImageView actionPlan;
    private ImageView environmentMonitor;

    GenericTypeIndicator<ArrayList<Mark>> genericTypeIndicator;
    GenericTypeIndicator<ArrayList<Street>> streetsGenericTypeIndicator;

    private OnLinkItemSelectedListener mListener;

    private MapView mapView;
    private static final float FONT_SIZE = 20;
    private static final float MARGIN_SIZE = 3;
    private static final float STROKE_SIZE = 5;
    MainActivity activity;
    private View rootView;
    public double startLon = 37.615560;
    public double startLat = 55.752220;
    public float startZoom = 10.0f;
    ArrayList<Street> allStreets = new ArrayList<>();
    ArrayList<Mark> allMarks = new ArrayList<>();

    ClusterizedPlacemarkCollection clusterizedCollection;

    // Интерфейс для взаимодействия между activity (используется для передачи нажатия на объект) --->
    public interface OnLinkItemSelectedListener {
        public void onLinkItemSelected(int id);
    }
    // Интерфейс для взаимодействия между activity (используется для передачи нажатия на объект) ---<

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;

        if (activity instanceof OnLinkItemSelectedListener) { // Проверка на подключения прослушивателя activity
            mListener = (OnLinkItemSelectedListener) activity;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, null);
        init();
        mapView.getMap().move(
                new CameraPosition(new Point(startLat, startLon), startZoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 2),
                null);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.imageToolBar).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.search_icon).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.textViewToolBar).setVisibility(View.GONE);
        toolbar.findViewById(R.id.shieldComin).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.ham_icon).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.backBTN).setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);

        return rootView;
    }
    public static MapFragment newInstance(String startLon, String startLat){
        MapFragment mapFragment = new MapFragment();
        mapFragment.startLat = Double.parseDouble(startLat);
        mapFragment.startLon = Double.parseDouble(startLon);
        mapFragment.startZoom =18.0f;
        return mapFragment;
    }
    public static MapFragment newInstance(String startLon, String startLat, Street street){
        MapFragment mapFragment = new MapFragment();

        mapFragment.startLat = Double.parseDouble(startLat);
        mapFragment.startLon = Double.parseDouble(startLon);
        mapFragment.startZoom =18.0f;
        return mapFragment;
    }
    public static MapFragment newInstance(){
        return new MapFragment();
    }
    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userReference = mDatabase.child("allMarks");
        streetReference = mDatabase.child("streets");
        genericTypeIndicator = new GenericTypeIndicator<ArrayList<Mark>>() {};
        streetsGenericTypeIndicator = new GenericTypeIndicator<ArrayList<Street>>() {};
        railWear = (ImageView)rootView.findViewById(R.id.ivRailWear);
        gaugeMonitor = (ImageView)rootView.findViewById(R.id.ivGaugeMonitor);
        vibration = (ImageView)rootView.findViewById(R.id.vibration);
        networkBreak = (ImageView)rootView.findViewById(R.id.networkBreak);
        actionPlan = (ImageView)rootView.findViewById(R.id.actionPlan);
        environmentMonitor = (ImageView)rootView.findViewById(R.id.environmentMonitor);

        mapView = (MapView) rootView.findViewById(R.id.mapview);
        clusterizedCollection = mapView.getMap().getMapObjects().addClusterizedPlacemarkCollection(this);
        initListeners();
    }

    private void initListeners() {
        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addPointsOnMap(dataSnapshot.getValue(genericTypeIndicator));
                allMarks.addAll(dataSnapshot.getValue(genericTypeIndicator));
                clusterizedCollection.clusterPlacemarks(30, 15);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ValueEventListener valueListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allStreets.addAll(dataSnapshot.getValue(streetsGenericTypeIndicator));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userReference.addListenerForSingleValueEvent(valueListener);
        streetReference.addListenerForSingleValueEvent(valueListener1);


        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLinkItemSelected(view.getId()); // Передача activity id нажатого обьекта
            }
        };

        railWear.setOnClickListener(clickListener);
        gaugeMonitor.setOnClickListener(clickListener);
        vibration.setOnClickListener(clickListener);
        networkBreak.setOnClickListener(clickListener);
        actionPlan.setOnClickListener(clickListener);
        environmentMonitor.setOnClickListener(clickListener);
    }

    @Override
    public void onClusterAdded(Cluster cluster) {
        cluster.getPlacemarks().get(0).getDirection();
        cluster.getAppearance().setIcon(new TextImageProvider(Integer.toString(cluster.getSize())));
    }


    private void addPointsOnMap(ArrayList<Mark> marks){
        for(Mark mark : marks){
            int defect = Integer.parseInt(mark.getDefect());
            ImageProvider imageProvider;
            switch (defect){
                case 1: imageProvider = ImageProvider.fromResource(
                        activity, R.drawable.sensor_green);
                    break;
                case 2: imageProvider = ImageProvider.fromResource(
                        activity, R.drawable.sensor_yellow);
                    break;
                case 3: imageProvider = ImageProvider.fromResource(
                        activity, R.drawable.sensor_orange);
                    break;
                default: imageProvider = ImageProvider.fromResource(
                        activity, R.drawable.sensor_red);}

            PlacemarkMapObject mapObject = clusterizedCollection.addPlacemark(new Point(Double.parseDouble(mark.getSens_lat()), Double.parseDouble(mark.getSens_long())));
            mapObject.setUserData(mark);
            mapObject.setIcon(imageProvider);
            mapObject.addTapListener(this);
        }
    }
    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
        RadioButton networkBreak1; // Переменная статуса разрыва сети
        RadioButton networkBreak2; // Переменная статуса разрыва сети

        Mark mark1 = (Mark) mapObject.getUserData();
        Toast.makeText(getContext(), mark1.getSim_id(), Toast.LENGTH_SHORT ).show();
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.showinfo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
        dialog.setCancelable(true);
        dialog.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, GraphsActivity.class));
            }
        });
        ProgressBarCustom progressBarCustom = new ProgressBarCustom(dialog);
        networkBreak1 = dialog.findViewById(R.id.networkBreak1);
        networkBreak2 = dialog.findViewById(R.id.networkBreak2);

        ((TextView)dialog.findViewById(R.id.hub_textview)).setText("Хаб " +mark1.getId());

        Street street = allStreets.get(Integer.parseInt(mark1.getStreet_id()) -1);

        // Определение цвета и заполненности шкалы вибрации по defect --->
        int color = Color.YELLOW;
        int vibr = 0;

        switch (mark1.getDefect()){
            case "1":
                color = Color.GREEN;
                vibr = 100;
                break;
            case "2":
                color = Color.YELLOW;
                vibr = 75;
                break;
            case "3":
                color = Color.parseColor("#FF9800");
                vibr = 50;
                break;
            case "4":
                color = Color.RED;
                vibr = 25;
                break;
        }
        // Определение цвета и заполненности шкалы вибрации по defect ---<

        ((ImageView)dialog.findViewById(R.id.colorStatus)).setImageTintList(ColorStateList.valueOf(color)); // Установка цвета для Статуса Хаба

        // Подсчет заряда, кол-во хабов, и сигнала --->
        int hubsCount = 0;
        double minVoltPhone = Double.valueOf(mark1.getVolt_phone());
        double maxVoltPhone = 0;
        double minRssi = Double.valueOf(mark1.getRssi_phone());
        double maxRssi = 0;

        for (Mark m : allMarks){
            if (mark1.getStreet_id().equals(m.getStreet_id())){
                hubsCount++;
                if (minVoltPhone > Double.valueOf(m.getVolt_phone())) minVoltPhone = Double.valueOf(m.getVolt_phone());
                if (maxVoltPhone < Double.valueOf(m.getVolt_phone())) maxVoltPhone = Double.valueOf(m.getVolt_phone());
                if (minRssi > Double.valueOf(m.getRssi_phone())) minRssi = Double.valueOf(m.getRssi_phone());
                if (maxRssi < Double.valueOf(m.getRssi_phone())) maxRssi = Double.valueOf(m.getRssi_phone());
            }
        }
        // Подсчет заряда, кол-во хабов, и сигнала ---<

        ((TextView)dialog.findViewById(R.id.Xabs)).setText("Хабов\n" + hubsCount);
        ((TextView)dialog.findViewById(R.id.voltPhoneTextView)).setText("Заряд\n" + minVoltPhone + "В\n" + maxVoltPhone + "В");
        ((TextView)dialog.findViewById(R.id.rssiPhoneTextView)).setText("Сигнал\n" + minRssi + "дБм\n" + maxRssi + "дБм");
        ((TextView)dialog.findViewById(R.id.streetTV)).setText(street.getStreet_type() + " " + street.getStreet_name());

        progressBarCustom.SetPrecent("Iznos",100, Color.GREEN);
        progressBarCustom.SetPrecent("Vibr",vibr, color);
        progressBarCustom.SetPrecent("Sred",100, Color.GREEN);
        progressBarCustom.SetPrecent("Koleya",100, Color.GREEN);

        dialog.show();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public class TextImageProvider extends ImageProvider {
        @Override
        public String getId() {
            return "text_" + text;
        }
        private final String text;
        @Override
        public Bitmap getImage() {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(metrics);
            Paint textPaint = new Paint();
            textPaint.setTextSize(FONT_SIZE * metrics.density);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            float widthF = textPaint.measureText(text);
            Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
            float heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top);
            float textRadius = (float)Math.sqrt(widthF * widthF + heightF * heightF) / 2;
            float internalRadius = textRadius + MARGIN_SIZE * metrics.density;
            float externalRadius = internalRadius + STROKE_SIZE * metrics.density;
            int width = (int) (2 * externalRadius + 0.5);
            Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setColor(Color.WHITE);
            canvas.drawCircle(width / 2, width / 2, externalRadius, backgroundPaint);
            backgroundPaint.setColor(Color.RED);
            canvas.drawCircle(width / 2, width / 2, internalRadius, backgroundPaint);
            canvas.drawText(
                    text,
                    width / 2,
                    width / 2 - (textMetrics.ascent + textMetrics.descent) / 2,
                    textPaint);
            return bitmap;
        }
        public TextImageProvider(String text) {
            this.text = text;
        }
    }



}









