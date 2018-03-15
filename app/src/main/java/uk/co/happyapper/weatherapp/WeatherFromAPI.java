package uk.co.happyapper.weatherapp;

import java.util.ArrayList;

/**
 * Created by marksheekey on 14/03/2018.
 */

public class WeatherFromAPI {
    public ArrayList<Weather> weather;
    public Main main;
    public Wind wind;
    public String name;


    public class Main{
        public float temp;
        public float pressure;
        public int humidity;
        public float temp_min;
        public float temp_max;
    }

    public class Wind{
        public float speed;
        public float deg;
        public float gust;
    }

    public class Weather{
        public String main;
        public String description;
        public String icon;
    }

}
