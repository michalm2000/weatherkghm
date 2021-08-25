package com.weather.jobs.impl;

import com.weather.jobs.IconConverter;
import com.weather.jobs.WeatherDataProvider;
import com.weather.model.DailyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
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
        return new String[]{map.get("max").toString(), map.get("min").toString()} ;//extracting max and min temp to array
    }
    @SuppressWarnings("rawtype")
    private ArrayList<DailyResponse> makeDailyResponse(HashMap response) throws IOException {
        ArrayList<DailyResponse> dailyResponseArrayList = new ArrayList<>();
        ArrayList<HashMap> dailyList = (ArrayList) response.get("daily");
        for (HashMap daily: dailyList) {
            String[] temp = extractTemp(daily);
            dailyResponseArrayList.add(new DailyResponse(IconConverter.getDataURIFromIconCode(extractWeatherIcon(daily)),
                    temp[0], temp[1], daily.get("dt").toString()));
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
