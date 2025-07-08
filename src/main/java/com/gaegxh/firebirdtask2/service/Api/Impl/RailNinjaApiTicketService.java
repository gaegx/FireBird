package com.gaegxh.firebirdtask2.service.Api.Impl;

import com.gaegxh.firebirdtask2.config.CookieConfig;
import com.gaegxh.firebirdtask2.model.SearchCriteria;
import com.gaegxh.firebirdtask2.model.TicketQueryRequest;
import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.gaegxh.firebirdtask2.service.Api.BestTicketSelector;
import com.gaegxh.firebirdtask2.service.Api.RailNinjaApiClient;
import com.gaegxh.firebirdtask2.service.Api.TicketService;
import com.gaegxh.firebirdtask2.service.parse.TrainInfoParserService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RailNinjaApiTicketService implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(RailNinjaApiTicketService.class);
    private static final int MAX_BATCH_ATTEMPTS = 10;

    private final TrainInfoParserService parser;
    private final RailNinjaApiClient apiClient;
    private final BestTicketSelector bestTicketSelector;
    private final CookieConfig cookieConfig;

    public RailNinjaApiTicketService(TrainInfoParserService parser,
                                     RailNinjaApiClient apiClient,
                                     BestTicketSelector bestTicketSelector,
                                     CookieConfig cookieConfig) {
        this.parser = parser;
        this.apiClient = apiClient;
        this.bestTicketSelector = bestTicketSelector;
        this.cookieConfig = cookieConfig;
    }

    @Override
    public List<TrainInfo> getTickets(TicketQueryRequest request) {
        return request.getSearchCriteria().getSearchType() == SearchCriteria.SearchType.SEARCH
                ? performSearchMode(request)
                : performPurchaseMode(request);
    }

    private List<TrainInfo> performSearchMode(TicketQueryRequest request) {
        List<TrainInfo> tickets = new ArrayList<>();

        try {
            HttpResponse<String> response = apiClient.performInitialRequest(request);
            if (response.getStatus() == 200) {
                apiClient.updateSessionCookies(response);
                JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
                tickets.addAll(parser.parse(json));

                String sessionId = JsonUtils.getTextSafe(json, "search_session_id");
                boolean finished = JsonUtils.getBooleanSafe(json, "finished", false);

                int attempts = 0;
                while (!finished && attempts < MAX_BATCH_ATTEMPTS) {
                    HttpResponse<String> batchResp = apiClient.performBatchRequest(sessionId);
                    if (batchResp.getStatus() == 200) {
                        apiClient.updateSessionCookies(batchResp);
                        JsonObject batchJson = JsonParser.parseString(batchResp.getBody()).getAsJsonObject();
                        tickets.addAll(parser.parse(batchJson));

                        finished = JsonUtils.getBooleanSafe(batchJson, "finished", false);
                        sessionId = JsonUtils.getTextSafe(batchJson, "search_session_id");
                    } else {
                        logger.error("Ошибка батч-запроса: {}", batchResp.getStatus());
                        break;
                    }
                    attempts++;
                    if (!finished) Thread.sleep(100);
                }
            } else {
                logger.error("Ошибка начального запроса: {}", response.getStatus());
            }
        } catch (Exception e) {
            logger.error("Ошибка при запросе к API (SEARCH):", e);
        }

        return tickets;
    }

    private List<TrainInfo> performPurchaseMode(TicketQueryRequest request) {
        List<TrainInfo> tickets = new ArrayList<>();
        String criteria = request.getSearchCriteria().getBestTicketCriteria();


        try {
            HttpResponse<String> response = apiClient.performInitialRequest(request);
            if (response.getStatus() == 200) {
                apiClient.updateSessionCookies(response);
                JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
                List<TrainInfo> initialTickets = parser.parse(json);
                tickets.addAll(initialTickets);
                boolean bestTicket;
                for (TrainInfo ticket : tickets) {
                    bestTicket = bestTicketSelector.isAcceptableTicket(ticket, criteria);
                    if (bestTicket) {
                        return List.of(ticket);
                    }
                }

                String sessionId = JsonUtils.getTextSafe(json, "search_session_id");
                boolean finished = JsonUtils.getBooleanSafe(json, "finished", false);

                int attempts = 0;
                while (!finished && attempts < MAX_BATCH_ATTEMPTS) {
                    HttpResponse<String> batchResp = apiClient.performBatchRequest(sessionId);
                    if (batchResp.getStatus() == 200) {
                        apiClient.updateSessionCookies(batchResp);
                        JsonObject batchJson = JsonParser.parseString(batchResp.getBody()).getAsJsonObject();

                        List<TrainInfo> batchTickets = parser.parse(batchJson);
                        tickets.addAll(batchTickets);

                        for (TrainInfo ticket : tickets) {
                            bestTicket = bestTicketSelector.isAcceptableTicket(ticket, criteria);
                            if (bestTicket) {
                                return List.of(ticket);
                            }
                        }

                        finished = JsonUtils.getBooleanSafe(batchJson, "finished", false);
                        sessionId = JsonUtils.getTextSafe(batchJson, "search_session_id");


                        Thread.sleep(1000);
                    } else {
                        logger.error("Ошибка батч-запроса: {}", batchResp.getStatus());
                        break;
                    }
                    attempts++;
                }
            } else {
                logger.error("Ошибка начального запроса: {}", response.getStatus());
            }
        } catch (Exception e) {
            logger.error("Ошибка при запросе к API (PURCHASE):", e);
        }

        return null;
    }
}
