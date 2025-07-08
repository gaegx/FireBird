package com.gaegxh.firebirdtask2.config;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RouteConfig {

    private final Map<String, String> routes = new HashMap<>();
    private final CookieConfig cookieConfig;

    public RouteConfig(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    public String getUuid(String route) {
        String uuid = routes.get(route);
        return uuid;
    }
    public String getName(String uui) {
        return routes.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), uui))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

    }


    @PostConstruct
    public void init() {
        fetchAndStoreStationUuid(6, "Paris");
        fetchAndStoreStationUuid(223, "Lille");
        fetchAndStoreStationUuid(5,"Lisbon");
        fetchAndStoreStationUuid(35, "Porto");
    }

    private void fetchAndStoreStationUuid(int stationId, String stationKey) {
        try {
            HttpResponse<String> response = Unirest.get("https://back.rail.ninja/api/v1/station/" + stationId)
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Cookie", cookieConfig.getCookies())
                    .asString();

            if (response.getStatus() == 200) {
                JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
                String uuid = json.get("uuid").getAsString();
                routes.put(stationKey, uuid);
                System.out.println("Station " + stationKey + ": " + uuid);
            } else {
                System.err.println("Failed to fetch station " + stationKey + ": " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("Error fetching station " + stationKey + ": " + e.getMessage());
        }
    }
}
