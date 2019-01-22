package com.steve.CitySearch;
/**
 * copy right Steve Bao 2019
 * steve_bao@yahoo.com
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.steve.CitySearch.model.City;
import com.steve.CitySearch.util.ParseJsonAsyncTask;
import com.steve.CitySearch.util.SearchTask;
import com.steve.CitySearch.viewmodel.CityRepo;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityListAdapter.CustomItemClickListener{

    static EditText edCitySearch;
    RecyclerView recyclerView;
    public static CityListAdapter cityAdapter;
    static TextView searchHint;
    static TextView loading;

    public static ArrayList<City> searchResultList = new ArrayList<>();
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        cityAdapter = new CityListAdapter(this, searchResultList);

        recyclerView.setAdapter(cityAdapter);

        loading = (TextView)findViewById(R.id.loading);
        searchHint = (TextView)findViewById(R.id.searchHint);
        progressBar = (ProgressBar)findViewById(R.id.propressBar);
        edCitySearch = (EditText)findViewById(R.id.search);
        edCitySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchResultList.clear();
                cityAdapter.notifyDataSetChanged();
                if (s == null) return;
                if (s.length() < 1) return;
                processTextChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CityRepo.getCityCount() > 1) {
            hideProgressBar();
        } else {
            showProgressBar();
            initProcessJsonFile();
        }
    }

    public static void refreshRecycleView(int low, int up) {
        if (low >= 0 && low < up) {
            searchResultList.addAll(CityRepo.cityList.subList(low, up));
            cityAdapter.notifyDataSetChanged();
        }
    }

    private void DEBUG_LOG(String s) {
      //  if (BuildConfig.DEBUG)
       //     Log.e("MAIN ", "=============> " + s);
    }

    /**
     *    When searching text changed, call the search Async Task to search matched cities.
     */
    public void processTextChanged(String searchTxt) {
          String[] searchStr = {searchTxt.toString()};
          SearchTask searchTask = new SearchTask(this);
          searchTask.execute(searchStr);
    }

    /**
     *    Load and prepare the city data from json file by AsyncTask
     */
    private void initProcessJsonFile() {
        if (CityRepo.getCityCount() > 1) return;
        ParseJsonAsyncTask parseJson = new ParseJsonAsyncTask(this);
        parseJson.execute();
    }

    public static void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        edCitySearch.setVisibility(View.GONE);
        searchHint.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar() {
        edCitySearch.setVisibility(View.VISIBLE);
        searchHint.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
    }

    /**
     * @param position : the corresponding index of cityList.
     *                 callback from recyclerView to implement onItemClick();
     */
    @Override
    public void onItemClick(int position) {
        String name = searchResultList.get(position).getName();
        String Country = searchResultList.get(position).getCountry();
        Double lat = searchResultList.get(position).getLat();
        Double lng = searchResultList.get(position).getLng();

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("cityAddr", name + ", " + Country);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        startActivity(intent);
    }
}
