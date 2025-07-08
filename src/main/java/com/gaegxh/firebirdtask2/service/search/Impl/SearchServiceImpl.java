package com.gaegxh.firebirdtask2.service.search.Impl;

import com.gaegxh.firebirdtask2.config.RouteConfig;
import com.gaegxh.firebirdtask2.model.TicketQueryRequest;
import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.gaegxh.firebirdtask2.service.Api.TicketService;
import com.gaegxh.firebirdtask2.service.exporter.TrainExporter;
import com.gaegxh.firebirdtask2.service.search.SearchService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final TicketService ticketService;
    private final TrainExporter trainExporter;
    private final RouteConfig routeConfig;

    public SearchServiceImpl(TicketService ticketService, TrainExporter trainExporter, RouteConfig routeConfig) {
        this.ticketService = ticketService;
        this.trainExporter = trainExporter;
        this.routeConfig =routeConfig;
    }

    @Override
    public void search(TicketQueryRequest searchRequest) throws Exception {
        List<TrainInfo> trains =ticketService.getTickets(searchRequest);
        String filePath =searchRequest.getDepartureDate()+ "_"
                + routeConfig.getName(searchRequest.getDepartureStationUuid())
                + "_"
                + routeConfig.getName(searchRequest.getArrivalStationUuid())
                + ".csv";

        trainExporter.export(trains,filePath);
        System.out.println("Список поездов сохранен. Имя файла: "+filePath);



    }
}
