package com.gaegxh.firebirdtask2.service.exporter.Impl;

import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.gaegxh.firebirdtask2.service.exporter.TrainExporter;

import java.io.FileWriter;
import java.util.List;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

@Service
public class CsvTicketExporter implements TrainExporter {
    public CsvTicketExporter() {
    }

    public void export(List<TrainInfo> trains, String filePath) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            String[] header = {
                    "TrainNo", "From", "To", "Departure", "Arrival",
                    "Duration", "Price", "TrainType", "SeatClass", "Fare", "Currency", "TicketType"
            };
            writer.writeNext(header);

            for (TrainInfo train : trains) {
                writer.writeNext(train.toCsvRow());
            }
        }
    }
}