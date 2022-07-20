package dev.artem.mostransport.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.artem.mostransport.R;
import dev.artem.mostransport.activities.MainActivity;
import dev.artem.mostransport.fragments.DialogFragment;
import dev.artem.mostransport.fragments.MapFragment;
import dev.artem.mostransport.models.Mark;
import dev.artem.mostransport.models.Street;

public class StreetsRecycleAdapter extends RecyclerView.Adapter<StreetsRecycleAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Street> streets;
    private final List<Mark> marks;
    private final Context context;

    public StreetsRecycleAdapter(Context context, List<Street> states, List<Mark> marks) {
        this.context = context;
        this.streets = states;
        this.marks = marks;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public StreetsRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_streets_fragment, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(StreetsRecycleAdapter.ViewHolder holder, int position) {
        Street street = streets.get(position);
        holder.textView.setText(street.getStreet_type() + " " + street.getStreet_name());
        holder.imageView.setColorFilter(Color.parseColor("#0bda51"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                ft.add(R.id.big_container, MapFragment.newInstance(street.getLongitude(), street.getLatitude(), street));
                ft.add(R.id.big_container, new DialogFragment(context, street, marks));
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return streets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView textView;

        ViewHolder(View view, final Context context1) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.krug);
            textView = (TextView) view.findViewById(R.id.text);
        }
    }
}
