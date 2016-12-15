package com.cameocoder.capstoneproject.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("street")
    @Expose
    private Object street;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("province")
    @Expose
    private Object province;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("country")
    @Expose
    private Object country;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("house")
    @Expose
    private String house;

    /**
     * No args constructor for use in serialization
     *
     */
    public Place() {
    }

    /**
     *
     * @param id
     * @param unit
     * @param source
     * @param locale
     * @param street
     * @param name
     * @param province
     * @param lng
     * @param house
     * @param lat
     * @param country
     * @param city
     */
    public Place(String name, Object city, String unit, Object street, String source, String id, String lng, Object province, String locale, Object country, String lat, String house) {
        this.name = name;
        this.city = city;
        this.unit = unit;
        this.street = street;
        this.source = source;
        this.id = id;
        this.lng = lng;
        this.province = province;
        this.locale = locale;
        this.country = country;
        this.lat = lat;
        this.house = house;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The city
     */
    public Object getCity() {
        return city;
    }

    /**
     *
     * @param city
     * The city
     */
    public void setCity(Object city) {
        this.city = city;
    }

    /**
     *
     * @return
     * The unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     *
     * @param unit
     * The unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     *
     * @return
     * The street
     */
    public Object getStreet() {
        return street;
    }

    /**
     *
     * @param street
     * The street
     */
    public void setStreet(Object street) {
        this.street = street;
    }

    /**
     *
     * @return
     * The source
     */
    public String getSource() {
        return source;
    }

    /**
     *
     * @param source
     * The source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The lng
     */
    public String getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     * The lng
     */
    public void setLng(String lng) {
        this.lng = lng;
    }

    /**
     *
     * @return
     * The province
     */
    public Object getProvince() {
        return province;
    }

    /**
     *
     * @param province
     * The province
     */
    public void setProvince(Object province) {
        this.province = province;
    }

    /**
     *
     * @return
     * The locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     *
     * @param locale
     * The locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     *
     * @return
     * The country
     */
    public Object getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    public void setCountry(Object country) {
        this.country = country;
    }

    /**
     *
     * @return
     * The lat
     */
    public String getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The house
     */
    public String getHouse() {
        return house;
    }

    /**
     *
     * @param house
     * The house
     */
    public void setHouse(String house) {
        this.house = house;
    }

    @Override
    public int describeContents() {
        return 0;
    }

   @Override
    public void writeToParcel(Parcel dest, int flags) {
       dest.writeString(id);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };


    private Place(Parcel in) {
        id = in.readString();

    }
}