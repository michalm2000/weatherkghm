package com.weather.jobs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public class WeatherDataProvider {
    @Value("${appid}")
    private String APP_ID;

    @Value("${uri}")
    private String URI;

    @Value("${city.list}")
    private String[] cityList;

    private static HashMap<String, Object> weatherInfo;
    private final RestTemplate restTemplate;

    public WeatherDataProvider(){
        weatherInfo = new HashMap<>();
        restTemplate = new RestTemplate();
    }

    @Scheduled(fixedDelay = 120000, initialDelay = 0)
    private void getWeatherInfo(){
        HashMap<String, Object> weatherInfo = new HashMap<>();
        for (String cityName: cityList){
            WeatherDataProvider.weatherInfo.put(
                    cityName, restTemplate.getForObject(URI + cityName.replace('_', ' ')
                    +"&appid=" + APP_ID, Object.class));
        }
        WeatherDataProvider.weatherInfo = weatherInfo;
    }

    public static Object getCurrentWeatherInfo(String cityName){
        return weatherInfo.getOrDefault(cityName, "hehehehehehehehe");
    }
}