package com.steve.CitySearch;
/**
 * copy right Steve Bao 2019
 * steve_bao@yahoo.com
 */
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.steve.CitySearch.model.City;

import java.util.ArrayList;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.NodeHolder> {
    ArrayList<City> cityNameList = new ArrayList<>();
        CustomItemClickListener mCallback;

    public interface CustomItemClickListener {
        public void onItemClick(int position);
    }

    public CityListAdapter(Context context, ArrayList<City> cityArrayList) {
        super();

        this.cityNameList = cityArrayList;
        try{
            mCallback = (CustomItemClickListener) context;  // for callback
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CustomItemClickListener");
        }
    }

    @NonNull
    @Override
    public NodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_item, parent, false);

        return new NodeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final NodeHolder nodeHolder, final int position) {
        City thisCity = getItem(position);

        nodeHolder.tvName.setText(thisCity.getName());
        nodeHolder.tvCountry.setText(thisCity.getCountry());
        nodeHolder.tvLat.setText(String.valueOf(thisCity.getLat()));
        nodeHolder.tvLng.setText(String.valueOf(thisCity.getLng()));
        nodeHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onItemClick(position);
            }
        });
    }

    public City getItem(int position) {
        return cityNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return cityNameList.size();
    }

    class NodeHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvCountry;
        private TextView tvLat;
        private TextView tvLng;

        public NodeHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvname);
            tvCountry = itemView.findViewById(R.id.tvcountry);
            tvLat = itemView.findViewById(R.id.lat);
            tvLng = itemView.findViewById(R.id.lng);
        }
    }
}
