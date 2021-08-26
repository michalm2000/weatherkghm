package com.weather.jobs;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

@Component
@EnableScheduling
public class IconConverter {
    private static HashMap<String, String> icons;

    @Value("${imageuri}")
    private String imageURI;

    @Value("${imageuritail}")
    private String imageURITail;

    @Value("${iconcode.list}")
    private String[] iconCodes;

    public IconConverter(){
        icons = new HashMap<>();
    }

    private String convertIconCodeToDataURI(String iconCode) throws IOException {
        System.out.println(imageURI + iconCode + imageURITail);
        byte[] imageBytes = IOUtils.toByteArray(new URL(imageURI + iconCode + imageURITail));
        return "data:image/png;base64,"+ Base64.getEncoder().encodeToString(imageBytes);
    }

    @Scheduled(fixedDelay = 86400000, initialDelay = 0)
    private void downloadIconURIs() throws IOException {
        if (icons.isEmpty()){
            for (String iconCode : iconCodes) {
                icons.put(iconCode, convertIconCodeToDataURI(iconCode));
            }
        }
    }

    public static String getDataURIFromIconCode(String iconCode){
        return icons.get(iconCode);
    }
}
