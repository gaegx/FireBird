package com.gaegxh.firebirdtask2.service.sell.Impl;

import com.gaegxh.firebirdtask2.config.RouteConfig;
import com.gaegxh.firebirdtask2.model.*;
import com.gaegxh.firebirdtask2.service.Api.TicketService;
import com.gaegxh.firebirdtask2.service.booking.ExternalBookingService;
import com.gaegxh.firebirdtask2.service.booking.Impl.ClientDetailsService;
import com.gaegxh.firebirdtask2.service.booking.Impl.PassengerApiService;

import com.gaegxh.firebirdtask2.service.booking.Impl.PaymentService;
import com.gaegxh.firebirdtask2.service.booking.Impl.PrebookingService;
import com.gaegxh.firebirdtask2.service.exporter.Impl.BookingSummaryCsvExporter;
import com.gaegxh.firebirdtask2.service.exporter.TrainExporter;
import com.gaegxh.firebirdtask2.service.parse.Impl.BookingIdParser;
import com.gaegxh.firebirdtask2.service.search.SearchService;
import com.gaegxh.firebirdtask2.service.sell.SellService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellServiceImpl implements SellService {

    private final TicketService ticketService;
    private final TrainExporter trainExporter;
    private final RouteConfig routeConfig;
    private final ExternalBookingService externalBookingService;
    private final PassengerApiService passengerApiService;
    private final ClientDetailsService clientDetailsService;
    private final PrebookingService prebookingService;
    private final BookingIdParser bookingIdParser;
    private final PaymentService paymentService;
    private final BookingSummaryCsvExporter bookingSummaryCsvExporter;




    @Override
    public void sell(TicketQueryRequest searchRequest, PassengerRequest passengerRequest, ClientDetailsRequest clientDetailsRequest, PaymentRequestDto paymentRequest) throws IOException {
        List<TrainInfo> trains = ticketService.getTickets(searchRequest);
        String bookId = bookingIdParser.parseBookingId(externalBookingService.sendBookingRequest(trains.get(0)));
        passengerApiService.sendPassengers(bookId, passengerRequest);
        clientDetailsService.sendClientDetails(bookId, clientDetailsRequest);
        prebookingService.sendPrebooking(bookId);
        var payment = paymentService.sendPayment(bookId, paymentRequest);
        BookingSummary order = mapToBookingSummary(bookId, trains.get(0), passengerRequest.getPassengers(), payment);

        String folderName = "Oreder";
        File folder = new File(folderName);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                throw new IOException("Не удалось создать папку для заказов: " + folderName);
            }
        }

        String filepath = folderName + File.separator + bookId + ".csv";
        bookingSummaryCsvExporter.exportToCsv(List.of(order), filepath);
    }

    public BookingSummary mapToBookingSummary(
            String bookingId,
            TrainInfo trainInfo,
            List<Passenger> passengers,
            String paymentStatus) {

        if (trainInfo == null) {
            throw new IllegalArgumentException("TrainInfo must not be null");
        }

        return BookingSummary.builder()
                .bookingId(bookingId)
                .fromStation(trainInfo.getDepartureStation())
                .toStation(trainInfo.getArrivalStation())
                .departureTime(trainInfo.getDepartureTime() + " / " + trainInfo.getArrivalTime() + " / " + trainInfo.getDuration())
                .trainClass(trainInfo.getTrainClass())
                .price(trainInfo.getPrice())
                .currency(trainInfo.getCurrency())
                .passengers(passengers)
                .paymentStatus(paymentStatus != null ? paymentStatus : "UNKNOWN")
                .build();
    }

}
