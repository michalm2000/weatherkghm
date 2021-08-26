package com.weather.service;

import com.weather.jobs.impl.CurrentWeatherDataProvider;
import com.weather.jobs.impl.ForecastDataProvider;
import com.weather.model.DailyResponse;
import com.weather.model.WeatherResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@RestController
@RequestMapping
public class RESTServiceW {
    
    //get current weather
    @GetMapping("/weather/{cityName}")
    public WeatherResponse getCurrentWeather(@PathVariable String cityName) throws CityNotFoundException {
        WeatherResponse returnData = (WeatherResponse) CurrentWeatherDataProvider.getWeatherInfo(cityName);
        if (returnData == null) {
            throw new CityNotFoundException();
        }
        return returnData;
    }
    //get weather forecast
    @GetMapping("/forecast/{cityName}")
    public Object getForecast(@PathVariable String cityName) throws CityNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        ArrayList<DailyResponse> returnData = ForecastDataProvider.getForecast(cityName);
        if (returnData == null) {
            throw new CityNotFoundException();
        }
        return returnData;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No available data for this city")
    private static class CityNotFoundException extends RuntimeException{}
}
