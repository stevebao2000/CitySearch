package com.steve.CitySearch.viewmodel;
/**
 * copy right Steve Bao 2019
 * steve_bao@yahoo.com
 */
import android.arch.lifecycle.ViewModel;
import com.steve.CitySearch.model.City;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CityRepo extends ViewModel {

    public static ArrayList<City> cityList = new ArrayList<>();

    // recording the ranges of cities start with char, "234,345"
    public static HashMap<Integer, String> charIndexMap = new LinkedHashMap<>();
    // This information is used to calculate the STEP during search.
    public static HashMap<Integer, Integer> secondIndexMap = new LinkedHashMap<>();

    public List<City> getSubList(int start, int end) {
        int listsize= cityList.size();
        if (start < 0 || start > listsize) return null;

        if(end < start || end > listsize) return null;

        return cityList.subList(start, end);
    }

    public static String getCityName(int index) {
        if ( 0 <= index && index < cityList.size())
            return cityList.get(index).getName();
        else
            return "";
    }
    public ArrayList<City> getCityList() {
        return cityList;
    }

    public void setCityList(ArrayList<City> clist) {
        cityList = clist;
    }

    public static int getCityCount() {
        return cityList.size();
    }

    public static int getSecondCharDoubleWeight(char ch) {
        int chInt = ch;
        if (secondIndexMap.containsKey(chInt))
            return secondIndexMap.get(chInt);
        else
            return 0;
    }

    public static String getHashValue(int i) {
        if (charIndexMap.containsKey(i))
            return charIndexMap.get(i);
        else
            return "";
    }

    // build statistics count for a--z. ignore other chars.
    private static void addCharToHashMap(int ch) {

        if (ch >= 97 && ch <= 122) {
            if (secondIndexMap.containsKey(ch))
                secondIndexMap.put(ch, secondIndexMap.get(ch) + 1);
            else
                secondIndexMap.put(ch, 1);
        }
    }

    // build statistics count for the second char and third char for fast searching.
    private static void addStatisticsCountForSecondThirdChar(String name) {
        int nextChar;
        if(name.length() > 1) {
            nextChar = name.charAt(1);
            addCharToHashMap(nextChar);
        }
        if(name.length() > 2) {
            nextChar = name.charAt(2);
            addCharToHashMap(nextChar);
        }
    }

    /**
     *   two important things did here:
     *   1. soft the cityList.
     *   2. build First Char Index HashMap.
     *      The key is ascii code the first char.
     *      The value is a String make of a pair of index "fromIndex,toIndex".
     */
    public static void buildCharIndexHashMap() {
        // sort the cityList
        //Collections.sort(cityList, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(cityList, new Comparator<City>() {

            @Override
            public int compare(City c1, City c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });
        int len = cityList.size();
        if (len < 1) return;

        char firstChar=cityList.get(0).getName().charAt(0);
        int startIndex = 0, movingIndex = 0;
        String name;

        for (int i=0; i < len; i++ ) {
            name = cityList.get(i).getName();

            // build first char index for fast searching.
            if (firstChar == name.charAt(0)) {
                movingIndex = i;
            }else {
                int charInt = firstChar;
                movingIndex=i; // subList(fromIndex, toIndex); This toIndex is exclusive. So we need movingIndex + 1.
                charIndexMap.put(charInt, String.valueOf(startIndex) + "," + String.valueOf(movingIndex));
              //  Log.d("Map", "=======>key:" + charInt + ": " + String.valueOf(startIndex) + "," + String.valueOf(movingIndex));
                startIndex=i;
                firstChar = name.charAt(0);
            }
        }
        int charInt = firstChar;
        charIndexMap.put(charInt, String.valueOf(startIndex) + "," + String.valueOf(len));
    }
}
