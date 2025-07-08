package com.gaegxh.firebirdtask2.service.exporter;

import com.gaegxh.firebirdtask2.model.TrainInfo;

import java.util.List;

public interface TrainExporter {
    void export(List<TrainInfo> trainInfoList, String filePath) throws Exception;
}