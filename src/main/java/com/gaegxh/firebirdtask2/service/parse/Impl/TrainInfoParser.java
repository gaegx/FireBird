package com.gaegxh.firebirdtask2.service.parse.Impl;

import com.gaegxh.firebirdtask2.config.CookieConfig;
import com.gaegxh.firebirdtask2.model.TrainInfo;
import com.gaegxh.firebirdtask2.service.parse.TrainInfoParserService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TrainInfoParser implements TrainInfoParserService {

    private final TrainJsonExtractor extractor = new TrainJsonExtractor();
    private final TrainApiClient apiClient;
    private final UniqueTrainInfoTracker uniqueTracker = new UniqueTrainInfoTracker();

    @Autowired
    public TrainInfoParser(CookieConfig cookieConfig) {
        this.apiClient = new TrainApiClient(cookieConfig);
    }

    @Override
    public List<TrainInfo> parse(JsonElement jsonData) {
        List<TrainInfo> trains = new ArrayList<>();
        if (jsonData == null || !jsonData.isJsonObject()) {
            System.err.println("Ошибка: JSON не является объектом или null");
            return trains;
        }

        JsonObject root = jsonData.getAsJsonObject();
        String searchSessionId = extractor.extractString(root, "search_session_id");
        JsonObject trainsObject = extractor.extractJsonObject(root, "trains");
        if (trainsObject == null) return trains;

        TrainInfoBuilder builder = new TrainInfoBuilder(extractor, apiClient, uniqueTracker);
        for (Map.Entry<String, JsonElement> entry : trainsObject.entrySet()) {
            if (!entry.getValue().isJsonObject()) continue;

            List<TrainInfo> infos = builder.buildTrainInfos(entry.getValue().getAsJsonObject(), entry.getKey(), searchSessionId);
            trains.addAll(infos);
        }

        return trains;
    }
}