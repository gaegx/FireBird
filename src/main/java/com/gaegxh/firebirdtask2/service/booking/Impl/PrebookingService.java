package com.gaegxh.firebirdtask2.service.booking.Impl;

import com.gaegxh.firebirdtask2.config.CookieConfig;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PrebookingService {

    private static final Logger logger = LoggerFactory.getLogger(PrebookingService.class);


    private final CookieConfig cookieConfig;

    public PrebookingService(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }



    public String sendPrebooking(String bookingId) {

        try {
            HttpResponse<String> response = Unirest.post("https://back.rail.ninja/api/v1/external-booking/"+ bookingId + "/prebooking")
                    .header("Content-Type", "application/json")
                    .header("Cookie", cookieConfig.getCookie())
                    .header("x-api-user-key", "4ae3369b0952f1c1176deec94708f3a7")
                    .asString();


            if (response.getStatus() != 200) {
                throw new RuntimeException("Prebooking failed with status " + response.getStatus());
            }

            return response.getBody();
        } catch (UnirestException e) {
            throw new RuntimeException("Prebooking request failed: " + e.getMessage(), e);
        }
    }
}
