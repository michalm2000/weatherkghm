package com.weather.jobs;

import com.weather.model.WeatherResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

@Component
public class WeatherDataProvider {
    @Value("${appid}")
    private String APP_ID;

    @Value("${uri}")
    private String URI;

    @Value("${city.list}")
    private String[] cityList;

    @Value("${imageuri}")
    private String imageURI;

    @Value("${imageuritail}")
    private String imageURITail;

    @Value("${iconcode.list}")
    private String[] iconCodes;

    private HashMap<String, String> icons;

    private static HashMap<String, WeatherResponse> weatherInfo;
    private final RestTemplate restTemplate;


    public WeatherDataProvider() throws IOException {
        weatherInfo = new HashMap<>();
        icons = new HashMap<>();
        restTemplate = new RestTemplate();
        downloadIconURIs();
    }
    @SuppressWarnings("rawtypes")
    private String extractWeatherIcon(HashMap response) {
        ArrayList list = (ArrayList) response.get("weather");
        HashMap map = (HashMap) list.get(0);
        return (String) map.get("icon");
    }
    @SuppressWarnings("rawtypes")
    private String extractTemp(HashMap response) {
        HashMap map = (HashMap) response.get("main");
        return map.get("temp").toString();
    }

    private String getDataURIFromIconCode(String iconCode) throws IOException {
        byte[] imageBytes = IOUtils.toByteArray(new URL(imageURI + iconCode + imageURITail));
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    @SuppressWarnings("rawtypes")
    @Scheduled(fixedDelay = 240000, initialDelay = 0)
    private void downloadCurrentWeatherInfo(){
        for (String cityName: cityList){
            HashMap response = restTemplate.getForObject(URI + cityName.replace('_', ' ')
                    +"&appid=" + APP_ID, HashMap.class);
            try {
                WeatherResponse wr = new WeatherResponse();
                wr.setIconB64(icons.get(extractWeatherIcon(response)));
                wr.setTemp(extractTemp(response));
                weatherInfo.put(cityName, wr);
            } catch (NullPointerException e){
                System.out.println("Error");
            }
        }
    }

    private void downloadIconURIs() throws IOException {
        for (String iconCode: iconCodes) {
            icons.put(iconCode, getDataURIFromIconCode(iconCode));
        }
    }

    public static WeatherResponse getCurrentWeatherInfo(String cityName){
        return weatherInfo.get(cityName);
    }
}
