package com.weather.service;

import com.weather.model.CurrentWeatherResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.weather.jobs.WeatherDataProvider;

@RestController
@RequestMapping
public class RESTServiceW {

    @GetMapping("/weather/{cityName}")
    public CurrentWeatherResponse getCurrentWeather(@PathVariable String cityName) throws CityNotFoundException {
        CurrentWeatherResponse returnData = WeatherDataProvider.getCurrentWeatherInfo(cityName);
        if (returnData == null) {
            throw new CityNotFoundException();
        }
        return returnData;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No available data for this city")
    private static class CityNotFoundException extends RuntimeException{}
}
