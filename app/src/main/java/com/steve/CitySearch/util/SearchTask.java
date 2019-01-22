package com.steve.CitySearch.util;
/**
 * copy right Steve Bao 2019
 * steve_bao@yahoo.com
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.steve.CitySearch.BuildConfig;
import com.steve.CitySearch.MainActivity;
import com.steve.CitySearch.viewmodel.CityRepo;
import java.lang.ref.WeakReference;

public class SearchTask extends AsyncTask<String, Void, String> {

    Context mContext;
    public SearchTask(Context context) {
        this.mContext = new WeakReference<>(context).get();
    }

    protected void DEBUG_LOG(String s) {
       // if (BuildConfig.DEBUG)
          //  Log.e("SearchTask::", "==========> " + s);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DEBUG_LOG("Start search...");
    }

    @Override
    protected void onPostExecute(String range) {
        int low, up;
        if (range == null || range == "") {
            low = -1;
            up = -1;
        } else {
            String[] bounds = range.split(",");
            low = Integer.parseInt(bounds[0]);
            up = Integer.parseInt(bounds[1]);
        }
        MainActivity.refreshRecycleView(low, up);
    }

    /**
     *  Use binary search to find one entry with its initial chars matched with searching chars.
     * @param key : searching key
     * @param start : start index (included)
     * @param end : end index (excluded)
     * @return : index of matched entry, -1 if fail
     */
    public static int findCityIndexMatchInitChars(String key, int start, int end) {
        if (end <= start) return -1;
        int cityListSize = CityRepo.getCityCount();
        if (end > cityListSize)
            end = cityListSize;
        return bin_searchMatchedCity(start, end, key);
    }

    /**
     *  This method will return range as string "1234,3456".
     * @param key  : searching key
     * @return : index range as string "1234,3456".
     */
    public static String findCityRange(String key) {
        // if there is nothing to search, we do not show anything.
        if (key.length() < 1) return "";
        // if there is only one char, we already get the range during the preprocessing.
        if (key.length() == 1) {
            int i = key.charAt(0);
            return CityRepo.getHashValue(i);
        }

        int i = key.charAt(0);
        String range = CityRepo.getHashValue(i);
        // if the first char is not in the records, return "";
        if (range.length() < 1) return "";
        String bounds[] = range.split(",");
        int lowerBoundary = Integer.parseInt(bounds[0]);
        int upBoundary = Integer.parseInt(bounds[1]);

        DEBUG_LOG2("range: "  + lowerBoundary + ", " + upBoundary);
        // binary search one of the city match the search key.
        int pin = findCityIndexMatchInitChars(key, lowerBoundary, upBoundary);
        // if we can not find any city, return nothing.
        if (pin < 0) return "";

        DEBUG_LOG2("pin = " + pin);
        int stepSize = calculateCharStepSize(key, lowerBoundary, upBoundary);
        DEBUG_LOG2("stepSize: = "+stepSize );

        int low = findLowBoundaryMatchChars(key, lowerBoundary, pin, stepSize);  // inclusive
        int up =  findUpperBoundaryMatchChars(key, upBoundary, pin, stepSize);  // exclusive
        return String.valueOf(low) + "," + String.valueOf(up);
    }

    private static void DEBUG_LOG2(String s) {
      //  if (BuildConfig.DEBUG)
        //    Log.e("SearchTask2::", "==========> " + s);
    }

    /**
     *  This method gets called only when the step == 1.
     * @param key : searching key
     * @param upBound : up boundary of the range (exclusive)
     * @param pin : known matched city index.
     * @return : biggest index of matched city + 1;
     */
    private static int sweepUp(String key, int upBound, int pin) {
        int bound = pin;
        while (bound < upBound) {
            if (MatchInitialChars(CityRepo.getCityName(bound), key)) {
                bound++; // if match, the index move down.
            } else {
                return (bound);
            }
        }
        return (bound);
    }
     /**
     * Find the upper boundary of matched city names in the range
     * @param key: searching key
     * @param upBound: top of range.
     * @param pin: know matched name index
     * @param step: how fast the index moves up
     * @return: the upper boundary (exclusive).
     */
    private static int findUpperBoundaryMatchChars(String key, int upBound, int pin, int step){
        DEBUG_LOG2("findUpperBoundaryMatchChars()");
        int bound = pin;
        if (step == 1) { // if step is 1, we just move up thru a while loop.
            return sweepUp(key, upBound, pin);
        }

        // Push up by step until we pass the boundary:
        int backbound = bound;
        int frontbound = bound + step;
        while (frontbound < upBound) {
            if (MatchInitialChars(CityRepo.getCityName(frontbound), key)) {
                backbound = frontbound;
                frontbound += step;
                if (frontbound > upBound) frontbound = upBound; // if moved out of this section. put to the boundary.
            } else {
                return binarySearchUpperBoundary(key, backbound, frontbound);
            }
        }
        if (frontbound > upBound) frontbound = upBound;
        return binarySearchUpperBoundary(key, backbound, frontbound);
    }

    /**
     * After we target this location, we use binary search to find the spot.
     * @param key: searching key
     * @param back: lower index which always match the key
     * @param front: top index
     * @return: return top index boundary (exclusive).
     */
    private static int binarySearchUpperBoundary(String key, int back, int front) {
        DEBUG_LOG2("binarySearchUpperBoundary()");
        if (front == back) return back;
        if (front - back == 1) {
            return front;
        }
        int mid = back + (front - back)/2;
        int result = CompareInitialChars(CityRepo.getCityName(mid), key);
        if (result <= 0) {
            return binarySearchUpperBoundary(key, mid, front);
        } else {
            return binarySearchUpperBoundary(key, back, mid);
        }
    }

    // find the lowest entry (starting index) match the search key.
    private static int findLowBoundaryMatchChars(String key, int lowerBound, int pin, int step){
        DEBUG_LOG2("findLowBoundaryMatchChars()");
        int bound = pin;
        if (step == 1) { // if step is 1, we just move down thru a while loop.
            while (bound > lowerBound) {
                if (MatchInitialChars(CityRepo.getCityName(bound), key)) {
                    bound--; // if match, the index move down.
                } else {
                    return (bound+1);
                }
            }
            return (bound+1);
        }

        // lets move down by step:
        int backbound = bound;
        int frontbound = bound - step;
        while (frontbound > lowerBound) {
            int result = CompareInitialChars(CityRepo.getCityName(frontbound), key);
            if (result==0) {
                backbound = frontbound;
                frontbound -= step;
                if (frontbound < lowerBound) {
                    frontbound = lowerBound;
                    break;
                }
            } else {
                break;
                //return binSearchLowBoundary(key, backbound, frontbound);
            }
        }
        if (frontbound < lowerBound) frontbound = lowerBound;
        return binSearchLowBoundary(key, backbound, frontbound);
    }

    // match the key at back position, might not match at front. front <= back
    // need found the position at smallest match;
    private static int binSearchLowBoundary(String key, int back, int front) {
        if (front == back) return back;
        if (back - front == 1) {
            if (MatchInitialChars(CityRepo.getCityName(front), key)) {
                return front;
            }
            return back;
        }
        int mid = back - (back - front)/2;
        int result = CompareInitialChars(CityRepo.getCityName(mid), key);

        if (result >= 0 ) {
            return binSearchLowBoundary( key, mid, front);
        } else {
            return binSearchLowBoundary(key, back, mid);
        }
    }

    /**
     * Estimate the step size for searching all names match the key.
     * @param key
     * @param lowbound
     * @param upbound
     * @return
     */
    private static int calculateCharStepSize(String key, int lowbound, int upbound) {
        char ch2 = key.charAt(1);
        int weight = CityRepo.getSecondCharDoubleWeight(ch2);
        int citysize = CityRepo.getCityCount();

        int step = (int)(((upbound - lowbound) * (weight/2)) / citysize);
        int keyLen = key.length();

        // when the search key words getting longer, we reduce the step size;
        while (keyLen > 2) {
            step = (int)(step/10);
            if (step <= 1) return 1;
            keyLen --;
        }
        if (step < 1) step = 1;
        return step;
    }

    /**
     * binary search.
     * @param start - start index of the interval
     * @param end - end index of the interval
     * @param key - the searching key match the front potion of the name
     * @return - the index (pin) if found the matched name, -1 if not found.
     */

    private static int bin_searchMatchedCity(int start, int end, String key) {
        if (end <= start) return -1;
        if (end - start == 1) {
            if (MatchInitialChars(CityRepo.getCityName(start), key)) {
                return start;
            } else {
                return -1;
            }
        }

        int mid = (start + end) / 2;
         int result = CompareInitialChars(CityRepo.getCityName(mid), key);
        if (result == 0) {
            return mid;
        } else if (result > 0) { // if key is smaller than the name at mid
            return bin_searchMatchedCity(start, mid-1, key);
        } else {
            return bin_searchMatchedCity(mid + 1, end, key);
        }
    }

    /**
     *
     * @param name: name to compare
     * @param key: search key
     * @return: true if the initial part of the name matchs key, others false.
     */
    private static boolean MatchInitialChars(String name, String key) {
        boolean result = true;

        int keyLen = key.length();
        if (name.length() < keyLen) {
            result = false;
        } else {
            for (int i = 0; i < keyLen; i++) {
                if (name.charAt(i) != key.charAt(i)) {
                    result =  false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     *  This is similar to dictionary compare. Only compare upto the length of key.
     * @param name - city name
     * @param key - searching key
     *            namePartial is front part of name with same length (or less) of key
     * @return : if (namePartial < key) return -1, if (namePartial == key) return 0,
     * if (namePartial > key) return 1;
     */
    private static int CompareInitialChars(String name, String key) {
        int keyLen = key.length();
        int nameLen = name.length();
        int result;
        if (nameLen < keyLen) {
            result = CompareInit(name, key, nameLen);
            if (result == 0)
                result = -1;
        } else {
            result = CompareInit(name, key, keyLen);
        }
        return result;
    }

    // len is the minimam length of name and key.
    private static int CompareInit(String name, String key, int len) {
        for (int i=0; i<len; i++) {
            if (name.charAt(i) < key.charAt(i)) return -1;
            else if (name.charAt(i) > key.charAt(i)) return 1;
        }
        return 0;
    }

    @Override
    protected String doInBackground(String... strings) {
        DEBUG_LOG( strings[0]);
        return findCityRange(strings[0]);
    }
}
