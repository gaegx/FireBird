package com.gaegxh.firebirdtask2.service.Api;


import com.gaegxh.firebirdtask2.model.TicketQueryRequest;
import com.gaegxh.firebirdtask2.model.TrainInfo;

import java.util.List;

public interface TicketService {
    List<TrainInfo> getTickets(TicketQueryRequest request);
}
