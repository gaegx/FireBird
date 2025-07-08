package com.gaegxh.firebirdtask2.service.Api.Impl;

import ch.qos.logback.classic.Logger;
import com.gaegxh.firebirdtask2.config.CookieConfig;
import com.gaegxh.firebirdtask2.model.TicketQueryRequest;
import com.gaegxh.firebirdtask2.service.Api.RailNinjaApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RailNinjaApiClientImpl implements RailNinjaApiClient {

    private final CookieConfig cookieConfig;
    private final Gson gson = new Gson();

    @Autowired
    public RailNinjaApiClientImpl(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    @Override
    public HttpResponse<String> performInitialRequest(TicketQueryRequest request) {
        JsonObject body = buildInitialRequestBody(request);
        return Unirest.post("https://back.rail.ninja/api/v2/timetable?frontendSearchRetry=1")
                .header("Content-Type", "application/json")
                .header("Cookie", cookieConfig.getCookie())
                .header("x-api-user-key", "4ae3369b0952f1c1176deec94708f3a7")
                .body(gson.toJson(body))
                .asString();
    }

    @Override
    public HttpResponse<String> performBatchRequest(String sessionId) {
        JsonObject body = new JsonObject();
        body.addProperty("search_session_id", sessionId);
        return Unirest.post("https://back.rail.ninja/api/v2/timetable/batch")
                .header("Content-Type", "application/json")
                .header("Cookie", cookieConfig.getCookie())
                .header("x-api-user-key", "4ae3369b0952f1c1176deec94708f3a7")
                .body(gson.toJson(body))
                .asString();
    }

    @Override
    public synchronized void updateSessionCookies(HttpResponse<String> response) {
        List<String> setCookieHeaders = response.getHeaders().get("Set-Cookie");
        if (setCookieHeaders != null && !setCookieHeaders.isEmpty()) {
            String allCookies = setCookieHeaders.stream()
                    .map(cookie -> {
                        int semicolonIndex = cookie.indexOf(';');
                        return semicolonIndex > 0 ? cookie.substring(0, semicolonIndex).trim() : cookie.trim();
                    })
                    .collect(Collectors.joining("; "));

            cookieConfig.setSessionCookie(allCookies);

        }
    }


    private JsonObject buildInitialRequestBody(TicketQueryRequest request) {
        JsonObject root = new JsonObject();
        JsonObject legs = new JsonObject();
        JsonObject leg1 = new JsonObject();
        leg1.addProperty("arrival_station", request.getArrivalStationUuid());
        leg1.addProperty("departure_date", request.getDepartureDate());
        leg1.addProperty("departure_station", request.getDepartureStationUuid());
        legs.add("1", leg1);
        root.add("legs", legs);

        JsonObject passengers = new JsonObject();
        passengers.addProperty("adults", 1);
        passengers.addProperty("children", 0);
        passengers.add("children_age", new JsonArray());
        root.add("passengers", passengers);

        return root;
    }
}