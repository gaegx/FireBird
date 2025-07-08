package com.gaegxh.firebirdtask2;

import com.gaegxh.firebirdtask2.config.RouteConfig;
import com.gaegxh.firebirdtask2.model.SearchCriteria;
import com.gaegxh.firebirdtask2.model.TicketQueryRequest;
import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.gaegxh.firebirdtask2.service.booking.ExternalBookingService;
import com.gaegxh.firebirdtask2.service.Api.TicketService;
import com.gaegxh.firebirdtask2.service.search.SearchService;
import com.gaegxh.firebirdtask2.service.ui.ConsoleGui;
import com.google.gson.JsonElement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
public class FireBirdTask2Application {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(FireBirdTask2Application.class, args);



        ConsoleGui consoleGui = context.getBean(ConsoleGui.class);
        consoleGui.start();



    }

}