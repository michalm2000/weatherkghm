package com.weather.jobs.impl;

import com.weather.jobs.IconConverter;
import com.weather.jobs.WeatherDataProvider;
import com.weather.model.DailyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Component
public class ForecastDataProvider extends WeatherDataProvider {
    private static HashMap<String, String[]> coordinatesMap;
    @Value("${exclude}")
    private String exclude;
    @Value("${forecasturi}")
    private String URI;
    private static HashMap<String, ArrayList<DailyResponse>> weatherInfo;
    public ForecastDataProvider(){
        coordinatesMap = new HashMap<>();
        weatherInfo = new HashMap<>();
    }

    @Override
    @SuppressWarnings("rawtype")
    protected String extractWeatherIcon(HashMap daily) {
        ArrayList list = (ArrayList) daily.get("weather");
        HashMap map = (HashMap) list.get(0);
        return (String) map.get("icon");
    }

    @Override
    @SuppressWarnings("rawtype")
    protected String[] extractTemp(HashMap daily) {
        HashMap map = (HashMap) daily.get("temp");
        long max, min;
        try {
            max = Math.round((double) map.get("max"));
        } catch (ClassCastException e){
            max = (int) map.get("max");
        }
        try {
            min = Math.round((double) map.get("min"));
        } catch (ClassCastException e){
            min = (int) map.get("min");
        }
        return new String[]{String.valueOf(max),
                String.valueOf(min)} ;//extracting max and min temp to array
    }

    private String extractDate(int timestamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date(timestamp*1000L));
    }
    @SuppressWarnings("rawtype")
    private ArrayList<DailyResponse> makeDailyResponse(HashMap response) throws IOException {
        ArrayList<DailyResponse> dailyResponseArrayList = new ArrayList<>();
        ArrayList<HashMap> dailyList = (ArrayList) response.get("daily");
        for (HashMap daily: dailyList) {
            String[] temp = extractTemp(daily);
            System.out.println((int) daily.get("dt"));
            dailyResponseArrayList.add(new DailyResponse(IconConverter.getDataURIFromIconCode(extractWeatherIcon(daily)),
                    temp[0] + " °C", temp[1] + " °C", extractDate((int) daily.get("dt"))));
        }
        return dailyResponseArrayList;
    }


    @Scheduled(fixedDelay = 240000, initialDelay = 200)
    private void downloadWeatherInfo(){
        for (String cityName: cityList){
            String[] coords = coordinatesMap.get(cityName);
            HashMap response = restTemplate.getForObject(URI + coords[0] + "&lat=" + coords[1] + exclude + units +
                    "&appid=" + APP_ID, HashMap.class);
            try {
                weatherInfo.put(cityName, makeDailyResponse(response));
            } catch (NullPointerException | IOException e){
                e.printStackTrace();
            }
        }
    }


    public static void setCoordinates(String cityName, String[] coordinates){
        coordinatesMap.put(cityName, coordinates);
    }

    public static ArrayList<DailyResponse> getForecast(String cityName){
        return weatherInfo.get(cityName);
    }
}
