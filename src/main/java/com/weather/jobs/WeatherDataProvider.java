package com.weather.jobs;

import com.weather.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public abstract class WeatherDataProvider {
    @Value("${appid}")
    protected String APP_ID;
    @Value("${units}")
    protected String units;

    @Value("${city.list}")
    protected String[] cityList;

    protected static HashMap<String, WeatherResponse> weatherInfo;
    protected final RestTemplate restTemplate;

    public WeatherDataProvider() {
        weatherInfo = new HashMap<>();
        restTemplate = new RestTemplate();
    }

    @SuppressWarnings("rawtype")
    protected abstract String extractWeatherIcon(HashMap response);
    @SuppressWarnings("rawtype")
    protected abstract Object extractTemp(HashMap response);

}
