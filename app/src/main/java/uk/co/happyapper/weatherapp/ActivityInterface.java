package uk.co.happyapper.weatherapp;

/**
 * Created by marksheekey on 14/03/2018.
 */

public interface ActivityInterface {
    void gotTown(WeatherFromAPI wft);
    void setError(String text);
}
