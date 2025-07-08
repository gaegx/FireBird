package com.gaegxh.firebirdtask2.service.parse;

import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.google.gson.JsonElement;


import java.util.List;

public interface TrainInfoParserService {
    List<TrainInfo> parse(JsonElement jsonData);
}
