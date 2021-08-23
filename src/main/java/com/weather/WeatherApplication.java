package com.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@SpringBootApplication
@RestController
@EnableScheduling
public class WeatherApplication {
    @Value("${appid}")
    private String APP_ID;

    @Value("${uri}")
    private String URI;

    @Value("${city.list}")
    private String[] cityList;

    private HashMap<String, Object> weatherInfo;

    public WeatherApplication(){
        weatherInfo =  new HashMap<>();
    }

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }

    @GetMapping("/weather/{cityName}")
    public Object getWeather(@PathVariable String cityName) throws CityNotFoundException {
        Object returnData = weatherInfo.get(cityName);
        if (returnData == null) {
            throw new CityNotFoundException();
        }
        return returnData;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No available data for this city")
    private static class CityNotFoundException extends RuntimeException{}

    @Scheduled(fixedDelay = 120000, initialDelay = 0)
    private void getWeatherInfo(){
        HashMap<String, Object> weatherInfo = new HashMap<>();
        for (String cityName: cityList){
            RestTemplate restTemplate = new RestTemplate();
            Object obj = restTemplate.getForObject(URI + cityName.replace('_', ' ')
                    +"&appid=" + APP_ID, Object.class);
            weatherInfo.put(cityName, obj);
        }
        this.weatherInfo = weatherInfo;
    }

}
