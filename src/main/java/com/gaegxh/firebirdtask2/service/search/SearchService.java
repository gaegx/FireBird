package com.gaegxh.firebirdtask2.service.search;


import com.gaegxh.firebirdtask2.model.TicketQueryRequest;


public interface SearchService {
    public void search(TicketQueryRequest searchRequest) throws Exception;
}
