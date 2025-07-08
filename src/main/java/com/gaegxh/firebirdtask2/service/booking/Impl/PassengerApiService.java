package com.gaegxh.firebirdtask2.service.booking.Impl;

import com.gaegxh.firebirdtask2.config.CookieConfig;
import com.gaegxh.firebirdtask2.model.PassengerRequest;
import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PassengerApiService {

    private static final Logger logger = LoggerFactory.getLogger(PassengerApiService.class);

    private final CookieConfig cookieConfig;
    private final Gson gson;

    public PassengerApiService(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
        this.gson = new Gson();
    }

    public boolean sendPassengers(String bookingId, PassengerRequest request) {

        try {
            String body = gson.toJson(request);

            HttpResponse<String> response = Unirest.post("https://back.rail.ninja/api/v1/external-booking/" + bookingId + "/passenger")
                    .header("Content-Type", "application/json")
                    .header("Cookie", cookieConfig.getCookie())
                    .header("x-api-user-key", "4ae3369b0952f1c1176deec94708f3a7")
                    .body(body)
                    .asString();

            if (response.getStatus() == 200 || response.getStatus() == 201 || response.getStatus() == 204) {
                logger.info("Passengers sent successfully");
                return true;
            } else {
                logger.error("Failed to send passengers, status: {}, body: {}", response.getStatus(), response.getBody());
            }
        } catch (Exception e) {
            logger.error("Exception during sending passengers", e);
        }
        return false;
    }
}
