package com.gaegxh.firebirdtask2.service.Api.Impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {
    public static String getTextSafe(JsonObject obj, String key) {
        JsonElement elem = obj.get(key);
        return (elem != null && !elem.isJsonNull()) ? elem.getAsString() : "";
    }

    public static boolean getBooleanSafe(JsonObject obj, String key, boolean defaultVal) {
        JsonElement elem = obj.get(key);
        return (elem != null && !elem.isJsonNull()) ? elem.getAsBoolean() : defaultVal;
    }
}