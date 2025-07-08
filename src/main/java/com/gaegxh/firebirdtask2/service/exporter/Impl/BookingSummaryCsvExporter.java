package com.gaegxh.firebirdtask2.service.exporter.Impl;

import com.gaegxh.firebirdtask2.model.BookingSummary;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
@Service
public class BookingSummaryCsvExporter {

    public void exportToCsv(List<BookingSummary> bookings,String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            writer.writeNext(new String[] {
                "Booking ID", "From Station", "To Station", "Departure Time",
                "Train Class", "Price", "Currency", "Passengers", "Payment Status"
            });

            // Данные
            for (BookingSummary booking : bookings) {
                writer.writeNext(booking.toCsvRow());
            }
        }
    }
}
