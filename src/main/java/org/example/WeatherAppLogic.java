package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherAppLogic {
    public static JSONObject getWeatherData(String locationName)
    {
        JSONArray locationData = getLocationData(locationName);

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=Europe%2FMoscow";

        try
        {
            HttpURLConnection connection = fetchApiResponse(urlString);

            if (connection.getResponseCode() != 200)
            {
                System.out.println("Error: could not connect to API");
                return null;
            }

            StringBuilder resJson = new StringBuilder();
            Scanner sc = new Scanner(connection.getInputStream());
            while (sc.hasNext())
            {
                resJson.append(sc.nextLine());
            }
            sc.close();
            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resJsonObj = (JSONObject) parser.parse(String.valueOf(resJson));

            JSONObject hourly = (JSONObject) resJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");

            int index = findIndex(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");

            double temperature = (double) temperatureData.get(index);

            JSONArray weatherCode = (JSONArray) hourly.get("weathercode");

            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            JSONArray relativeWindSpeed = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) relativeWindSpeed.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }

    private static String convertWeatherCode(long code) {
        String weatherCond = "";
        if (code == 0L)
        {
            weatherCond = "Ясно";
        } else if (code <= 3L && code > 0L) {
            weatherCond = "Облачно";
        } else if ((code >= 51L && code <= 67L) || (code >= 80L && code <= 99L)) {
            weatherCond = "Дождливо";
        } else if (code >= 71L && code <= 77L) {
            weatherCond = "Снег";
        }

        return weatherCond;
    }

    private static int findIndex(JSONArray timeArray) {
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeArray.size(); i++)
        {
            String time = (String) timeArray.get(i);

            if (time.equalsIgnoreCase(currentTime))
            {
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime()
    {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        return currentDateTime.format(formatter);
    }

    public static JSONArray getLocationData(String locationName)
    {
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
                + locationName + "&count=10&language=en&format=json";

        try
        {
            HttpURLConnection connection = fetchApiResponse(urlString);

            if (connection.getResponseCode() != 200)
            {
                System.out.println("Error: could not connect to API");
                return null;
            } else
            {
                StringBuilder resJson = new StringBuilder();
                Scanner sc = new Scanner(connection.getInputStream());

                while (sc.hasNext())
                {
                    resJson.append(sc.nextLine());
                }

                sc.close();
                connection.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resJsonObj = (JSONObject) parser.parse(String.valueOf(resJson));

                return (JSONArray) resJsonObj.get("results");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();
            return connection;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
