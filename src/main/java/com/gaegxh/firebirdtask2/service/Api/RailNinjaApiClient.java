package com.gaegxh.firebirdtask2.service.Api;

import com.gaegxh.firebirdtask2.model.TicketQueryRequest;
import kong.unirest.HttpResponse;

public interface RailNinjaApiClient {
    HttpResponse<String> performInitialRequest(TicketQueryRequest request);
    HttpResponse<String> performBatchRequest(String searchSessionId);
    void updateSessionCookies(HttpResponse<String> response);
}