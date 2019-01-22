package com.steve.CitySearch.model;

public class City {
    String name;
    String country;
    int _id;
    double lng;
    double lat;

    public City() {}

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int get_id() {
        return _id;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

}
