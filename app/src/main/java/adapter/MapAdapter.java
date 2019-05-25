package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.org.PRM391x_GoogleMap_khoidtFX01411.R;

import java.util.List;

import dbhelper.MapDbhelper;
import model.ModelMap;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder>  {

    List<ModelMap> maps;
    MapDbhelper mapDbHelper;
    private Context context;

    public MapAdapter(Context context,List<ModelMap> maps) {
        this.context = context;
        this.maps = maps;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public ViewHolder(View view){
            super(view);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_map, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        // xu ly set du lieu len textview

        TextView endAddress = viewHolder.itemView.findViewById(R.id.endaddress);
        TextView startAddress = viewHolder.itemView.findViewById(R.id.startaddress);
        TextView distance = viewHolder.itemView.findViewById(R.id.distance);
        TextView duration  = viewHolder.itemView.findViewById(R.id.duration);

        //final ModelMap map = maps.get(i);
        startAddress.setText("From: " + maps.get(i).getStartAddress());
        endAddress.setText("To: " + maps.get(i).getEndAddress());
        distance.setText("Distance: " + maps.get(i).getDistance());
        duration.setText("Duration: " + maps.get(i).getDuration());

    }

    @Override
    public int getItemCount() {
        return maps.size();
    }
}
