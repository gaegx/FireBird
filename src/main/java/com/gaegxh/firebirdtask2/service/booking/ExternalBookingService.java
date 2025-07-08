package com.gaegxh.firebirdtask2.service.booking;

import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.google.gson.JsonElement;

public interface ExternalBookingService {
    public JsonElement sendBookingRequest(TrainInfo trainInfo);

}
