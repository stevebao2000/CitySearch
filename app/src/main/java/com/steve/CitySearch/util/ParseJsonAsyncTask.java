package com.steve.CitySearch.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.stream.JsonReader;
import com.steve.CitySearch.MainActivity;
import com.steve.CitySearch.R;
import com.steve.CitySearch.model.City;
import com.steve.CitySearch.viewmodel.CityRepo;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ParseJsonAsyncTask extends AsyncTask<Void, Void, ArrayList<City>> {
    Context mcontext;
    static int count=0;

    public ParseJsonAsyncTask(Context context) {
        this.mcontext = new WeakReference<>(context).get();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e("TAG", "Load Json file: starting");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        int index = (int)(count/100000);
    }

    @Override
    protected ArrayList<City> doInBackground(Void... src) {
        int srcFileId = R.raw.cities;
        ArrayList<City> response = new ArrayList<>();

        try {
            JsonReader jsonReader;
            jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(mcontext.getResources().openRawResource(srcFileId))));
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                jsonReader.beginObject();
                City city = new City();
                while (jsonReader.hasNext()) {
                    String next = jsonReader.nextName();
                    if (next.equals("name")){
                        city.setName(jsonReader.nextString());
                    }
                    if (next.equals("_id")){
                        city.set_id(jsonReader.nextInt());
                    }
                    if (next.equals("country")){
                        city.setCountry(jsonReader.nextString());
                    }
                    if (next.equals("coord")){
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String coord = jsonReader.nextName();
                            if (coord.equals("lat")) {
                                city.setLat(jsonReader.nextDouble());
                            }
                            if (coord.equals("lon")) {
                                city.setLng(jsonReader.nextDouble());
                            }
                        }
                        jsonReader.endObject();
                    }
                }
                jsonReader.endObject();

                response.add(city);
            }
            jsonReader.endArray();
            jsonReader.close();
            return response;
        } catch (FileNotFoundException e) {
            Log.e("JsonParse", "File not found error");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("JsonParse", "Parsing error");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<City> cities) {
        super.onPostExecute(cities);
        Log.e("TAG", "Load Json file: finished");
        Toast.makeText(mcontext, "Loading finished", Toast.LENGTH_SHORT).show();
        CityRepo.cityList = cities;
        CityRepo.buildCharIndexHashMap();
        MainActivity.hideProgressBar();
    }
}
