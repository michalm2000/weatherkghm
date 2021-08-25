package com.weather.jobs.impl;

import com.weather.jobs.IconConverter;
import com.weather.jobs.WeatherDataProvider;
import com.weather.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Component
public class CurrentWeatherDataProvider extends WeatherDataProvider {

    @Value("${uri}")
    protected String URI;

    protected static HashMap<String, WeatherResponse> weatherInfo;

    public CurrentWeatherDataProvider(){
        weatherInfo = new HashMap<>();
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected String extractWeatherIcon(HashMap response) {
        ArrayList list = (ArrayList) response.get("weather");
        HashMap map = (HashMap) list.get(0);
        return (String) map.get("icon");
    }
    @SuppressWarnings("rawtypes")
    @Override
    protected String extractTemp(HashMap response) {
        HashMap map = (HashMap) response.get("main");
        return map.get("temp").toString();
    }

    @SuppressWarnings("rawtypes")
    private String[] extractCoordinates(HashMap response){
        HashMap map = (HashMap) response.get("coord");
        System.out.println(Arrays.toString(new String[]{map.get("lon").toString(), map.get("lat").toString()}));
        return new String[]{map.get("lon").toString(), map.get("lat").toString()};
    }

    @SuppressWarnings("rawtypes")
    @Scheduled(fixedDelay = 240000, initialDelay = 100)
    private void downloadWeatherInfo(){
        for (String cityName: cityList){
            HashMap response = restTemplate.getForObject(URI + cityName.replace('_', ' ') + units
                    + "&appid=" + APP_ID + units, HashMap.class);
            try {
                WeatherResponse wr = new WeatherResponse();
                wr.setIconB64(IconConverter.getDataURIFromIconCode(extractWeatherIcon(response)));
                wr.setTemp(extractTemp(response));
                weatherInfo.put(cityName, wr);
                ForecastDataProvider.setCoordinates(cityName, extractCoordinates(response));
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public static WeatherResponse getWeatherInfo(String cityName){
        return weatherInfo.get(cityName);
    }


}
