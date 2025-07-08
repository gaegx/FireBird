package com.gaegxh.firebirdtask2.service.Api;

import com.gaegxh.firebirdtask2.model.TrainInfo;

import java.util.List;

public interface BestTicketSelector {
    boolean isBetterTicket(TrainInfo candidate, TrainInfo currentBest, String criteria);
    boolean isAcceptableTicket(TrainInfo ticket, String criteria);

}