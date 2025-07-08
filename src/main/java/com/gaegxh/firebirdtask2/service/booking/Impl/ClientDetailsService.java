package com.gaegxh.firebirdtask2.service.booking.Impl;

import com.gaegxh.firebirdtask2.config.CookieConfig;
import com.gaegxh.firebirdtask2.model.ClientDetailsRequest;
import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClientDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(ClientDetailsService.class);


    private final CookieConfig cookieConfig;
    private final Gson gson = new Gson();

    public ClientDetailsService(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    public String sendClientDetails(String bookingId, ClientDetailsRequest details) {
        String body = gson.toJson(details);


        try {
            HttpResponse<String> response = Unirest.post("https://back.rail.ninja/api/v2/booking/"+bookingId+"/client-details")
                    .header("Content-Type", "application/json")
                    .header("Cookie", cookieConfig.getCookie())
                    .header("x-api-user-key", "4ae3369b0952f1c1176deec94708f3a7")
                    .body(body)
                    .asString();

            logger.debug("Client-details response code: {}", response.getStatus());
            logger.debug("Client-details response body: {}", response.getBody());

            int status = response.getStatus();
            if (status != 200 && status != 201 && status != 204) {
                throw new RuntimeException("Client-details request failed with status: " + status);
            }


            return response.getBody();
        } catch (UnirestException e) {
            throw new RuntimeException("Client-details request failed: " + e.getMessage(), e);
        }
    }
}
