package dev.artem.mostransport.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.mapview.MapView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dev.artem.mostransport.fragments.CalendarFragment;
import dev.artem.mostransport.fragments.DialogFragment;
import dev.artem.mostransport.fragments.GraphsAndReportsFragment;
import dev.artem.mostransport.fragments.MapFragment;
import dev.artem.mostransport.R;
import dev.artem.mostransport.fragments.StreetsFragment;
import dev.artem.mostransport.models.Mark;
import dev.artem.mostransport.models.Street;
import dev.artem.mostransport.models.User;
import dev.artem.mostransport.utils.StatusBarUtils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, MapFragment.OnLinkItemSelectedListener {

    private DatabaseReference mDatabase;
    private DatabaseReference userReference;
    private DatabaseReference streetsReference;
    private DatabaseReference marksReference;
    GenericTypeIndicator<ArrayList<Street>> genericTypeIndicator;
    GenericTypeIndicator<ArrayList<Mark>> genericTypeIndicator2;


    private DrawerLayout drawerLayout;
    public Toolbar toolbar;
    private NavigationView navigationView;
    FragmentManager myFragmentManager;
    MapFragment mapFragment;
    ImageView searchBTN;
    DataAdapter adapter;
    RecyclerView recyclerView;
    SearchView searchView;
    Dialog dialog;
    private ArrayList<Street> data = new ArrayList<>();
    private ArrayList<Street> allStreets = new ArrayList<>();
    private ArrayList<Mark> allMarks = new ArrayList<>();

    private DialogFragment dialogFragment;

    private static boolean firstCreate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.initialize(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorAccent), 0);
        init();
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.big_container, mapFragment);
            fragmentTransaction.commit();
        }
        firstCreate = true;
    }

    private void openStreetFragment() {
        StreetsFragment streetsFragment = new StreetsFragment();
        FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.big_container, streetsFragment);
        fragmentTransaction.commit();
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionPlan:
                CalendarFragment calendarFragment = new CalendarFragment();
                FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.big_container, calendarFragment);
                fragmentTransaction.commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.SensorMap:
            case R.id.networkBreak:
            case R.id.railWear:
            case R.id.gaugeMonitor:
            case R.id.environmentMonitor:
            case R.id.vibration:
                openStreetFragment();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.reportsAndGraphs:
                GraphsAndReportsFragment graphsAndReportsFragment = new GraphsAndReportsFragment();
                FragmentTransaction ft = myFragmentManager.beginTransaction();
                ft.add(R.id.big_container, graphsAndReportsFragment);
                ft.commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                Toast.makeText(MainActivity.this, id, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        streetsReference = mDatabase.child("streets");
        marksReference = mDatabase.child("allMarks");
        userReference = mDatabase.child("userpass").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        genericTypeIndicator = new GenericTypeIndicator<ArrayList<Street>>() {
        };
        genericTypeIndicator2 = new GenericTypeIndicator<ArrayList<Mark>>() {};
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        myFragmentManager = getSupportFragmentManager();
        mapFragment = new MapFragment();
        initListeners();
        searchBTN = findViewById(R.id.search_icon);
    }

    private void initListeners() {

        ValueEventListener userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                TextView nameTV = navigationView.getHeaderView(0).findViewById(R.id.tvName);
                nameTV.setText(user.getName());
                TextView postTV = navigationView.getHeaderView(0).findViewById(R.id.tvProfess);
                postTV.setText(user.getPost());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        navigationView.getHeaderView(0).findViewById(R.id.signout_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences autoAuth = getSharedPreferences("auto_auth", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = autoAuth.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
            }
        });
        toolbar.findViewById(R.id.backBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment mapFragment = new MapFragment();
                FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                toolbar.findViewById(R.id.imageToolBar).setVisibility(View.VISIBLE);
                toolbar.findViewById(R.id.search_icon).setVisibility(View.VISIBLE);
                toolbar.findViewById(R.id.textViewToolBar).setVisibility(View.GONE);
                toolbar.findViewById(R.id.shieldComin).setVisibility(View.VISIBLE);
                fragmentTransaction.replace(R.id.big_container, mapFragment);
                fragmentTransaction.commit();
            }

        });

        ValueEventListener streetValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Street> streets = dataSnapshot.getValue(genericTypeIndicator);
                if (streets != null) {
                    allStreets.addAll(streets);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        ValueEventListener marksValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Mark> marks = dataSnapshot.getValue(genericTypeIndicator2);
                if (marks != null) {
                    allMarks.addAll(marks);
                }
                if (firstCreate){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    dialogFragment = new DialogFragment(MainActivity.this, allStreets, allMarks);
                    ft.add(R.id.big_container, dialogFragment);
                    ft.commit();
                    firstCreate = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        userReference.addListenerForSingleValueEvent(userValueListener);
        streetsReference.addListenerForSingleValueEvent(streetValueListener);
        marksReference.addListenerForSingleValueEvent(marksValueListener);
        toolbar.findViewById(R.id.ham_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        toolbar.findViewById(R.id.search_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_search);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                dialog.setCancelable(true);
                recyclerView = dialog.findViewById(R.id.found_itemsRV);
                searchView = dialog.findViewById(R.id.admin_searchSV);


                data.clear();
                data.addAll(allStreets);

                adapter = new DataAdapter(MainActivity.this, data);
                recyclerView.setAdapter(adapter);
                // Locate the EditText in listview_main.xml
                searchView.setOnQueryTextListener(MainActivity.this);
                dialog.show();
            }
        });

        toolbar.findViewById(R.id.shieldComin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogFragment == null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    dialogFragment = new DialogFragment(MainActivity.this, allStreets, allMarks);
                    ft.add(R.id.big_container, dialogFragment);
                    ft.commit();
                } else {
                    dialogFragment.dismiss();
                    dialogFragment = null;
                }
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

    // События mapFragment --->
    @Override
    public void onLinkItemSelected(int id) {
        switch (id) {
            case R.id.actionPlan:
                CalendarFragment calendarFragment = new CalendarFragment();
                FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.big_container, calendarFragment);
                fragmentTransaction.commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.networkBreak:
            case R.id.ivRailWear:
            case R.id.ivGaugeMonitor:
            case R.id.environmentMonitor:
            case R.id.vibration:
                openStreetFragment();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                Toast.makeText(MainActivity.this, id, Toast.LENGTH_SHORT).show();
        }
    }
    // События mapFragment ---<

    class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        private final LayoutInflater inflater;
        private final ArrayList<Street> data;
        private final ArrayList<Street> arrayListForFiltering = new ArrayList<>();

        private final Context context;

        public DataAdapter(Context context, ArrayList<Street> data) {
            this.context = context;
            this.data = data;
            this.inflater = LayoutInflater.from(context);
            this.arrayListForFiltering.addAll(data);
        }

        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.item_admin_searched, parent, false);
            return new DataAdapter.ViewHolder(view, context);
        }


        @Override
        public void onBindViewHolder(final DataAdapter.ViewHolder holder, int position) {
            Street street = data.get(position);
            String itemName = street.getStreet_type() + " " + street.getStreet_name();
            holder.itemBackgroundLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.big_container, MapFragment.newInstance(street.getLongitude(), street.getLatitude()));
                    ft.commit();
                    dialog.cancel();
                }
            });

            holder.itemNameTV.setText(itemName);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            //final ImageView flagView;
            TextView itemNameTV;
            LinearLayout itemBackgroundLayout;

            //final TextView nameView, capitalView;
            ViewHolder(View view, final Context context1) {
                super(view);
                itemNameTV = view.findViewById(R.id.item_nameET);
                itemBackgroundLayout = view.findViewById(R.id.searched_item_background_layout);
            }
        }

        public void filter(String charText) {
            charText = charText.toLowerCase();
            data.clear();
            if (charText.length() == 0) {
                data.addAll(arrayListForFiltering);
            } else {
                for (Street key : arrayListForFiltering) {
                    String streetName = key.getStreet_type() + " " + key.getStreet_name();
                    if (streetName.toLowerCase().contains(charText)) {
                        data.add(key);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private String getSpecificationNameByKey(String key) {
        // return specifications.child(key).child("name").getValue(String.class);
        return null;
    }

    private String getOperationNameByKey(String key) {
        /*
        if (key != null) {
            String operationName;
            /*if (operationGroups == null) {
                saveOperationGroups();
            }
            for (DataSnapshot operationgGroup : operationGroups.getChildren()) {
                operationName = operationgGroup.child("Operations").child(key).child("name").getValue(String.class);
                if (operationName != null) {
                    return operationName;
                }
            }
*/
        return null;
    }

}


/*
        Button exitBTN = findViewById(R.id.exit);
        exitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences autoAuth = getSharedPreferences("auto_auth", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = autoAuth.edit();
                if (autoAuth.contains("Password") && autoAuth.contains("Email") && autoAuth.contains("isNecessary")) {
                    editor.putBoolean("isNecessary", false);
                    editor.apply();}
                try {
                    FirebaseAuth.getInstance().signOut();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
            }
        });
 */