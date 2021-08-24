package com.weather.service;

import com.weather.jobs.WeatherDataProvider;
import com.weather.model.WeatherResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class RESTServiceW {

    @GetMapping("/weather/{cityName}")
    public WeatherResponse getCurrentWeather(@PathVariable String cityName) throws CityNotFoundException {
        WeatherResponse returnData = WeatherDataProvider.getCurrentWeatherInfo(cityName);
        if (returnData == null) {
            throw new CityNotFoundException();
        }
        return returnData;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No available data for this city")
    private static class CityNotFoundException extends RuntimeException{}
}
