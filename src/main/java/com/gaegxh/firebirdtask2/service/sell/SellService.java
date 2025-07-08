package com.gaegxh.firebirdtask2.service.sell;

import com.gaegxh.firebirdtask2.model.ClientDetailsRequest;
import com.gaegxh.firebirdtask2.model.PassengerRequest;
import com.gaegxh.firebirdtask2.model.PaymentRequestDto;
import com.gaegxh.firebirdtask2.model.TicketQueryRequest;

import java.io.IOException;

public interface SellService {
    public void sell(TicketQueryRequest searchRequest, PassengerRequest passengerRequest, ClientDetailsRequest clientDetailsRequest, PaymentRequestDto paymentRequest) throws IOException;
}
