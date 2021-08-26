package com.weather.service;

import com.weather.jobs.impl.CurrentWeatherDataProvider;
import com.weather.jobs.impl.ForecastDataProvider;
import com.weather.model.DailyResponse;
import com.weather.model.WeatherResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;

@Controller

public class RESTServiceW {
    
    //get current weather
    @GetMapping("/weather/{cityName}")
    @ResponseBody
    public WeatherResponse getCurrentWeather(@PathVariable String cityName) throws CityNotFoundException {
        WeatherResponse returnData = CurrentWeatherDataProvider.getWeatherInfo(cityName);
        if (returnData == null) {
            throw new CityNotFoundException();
        }
        return returnData;
    }
    //get weather forecast
    @GetMapping("/forecast_data/{cityName}")
    @ResponseBody
    public ArrayList<DailyResponse> getForecastData(@PathVariable String cityName) throws CityNotFoundException {
        ArrayList<DailyResponse> returnData = ForecastDataProvider.getForecast(cityName);
        if (returnData == null) {
            throw new CityNotFoundException();
        }
        return returnData;
    }

    @GetMapping("/forecast/{cityName}")
    public String getForecast(@PathVariable String cityName, Model model){
        ArrayList<DailyResponse> returnData = ForecastDataProvider.getForecast(cityName);
        if (returnData == null) {
            throw new CityNotFoundException();
        }
        model.addAttribute("cityName", cityName);
        model.addAttribute("dailyWeather",returnData);
        return "forecast";
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No available data for this city")
    private static class CityNotFoundException extends RuntimeException{}
}
