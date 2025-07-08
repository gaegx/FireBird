package com.gaegxh.firebirdtask2.service.parse.Impl;

import com.google.gson.*;

import java.util.UUID;

public class TrainJsonExtractor {

    public String extractString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive()) return null;
        return obj.get(key).getAsString();
    }

    public JsonObject extractJsonObject(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonObject()) return null;
        return obj.getAsJsonObject(key);
    }

    public String extractNestedName(JsonObject obj, String key, String nameField) {
        JsonObject nested = extractJsonObject(obj, key);
        if (nested == null) return "Unknown";
        return extractString(nested, nameField) != null ? extractString(nested, nameField) : "Unknown";
    }

    public String extractUuid(JsonObject obj, String key, String uuidField) {
        JsonObject nested = extractJsonObject(obj, key);
        if (nested == null) return null;
        String uuidStr = extractString(nested, uuidField);
        if (uuidStr == null) return null;
        try {
            UUID.fromString(uuidStr);
            return uuidStr;
        } catch (IllegalArgumentException e) {
            System.err.println("Некорректный UUID для " + key + ": " + uuidStr);
            return null;
        }
    }

    public int extractPrice(JsonObject fareObj) {
        JsonObject priceObj = extractJsonObject(fareObj, "price");
        if (priceObj == null) return 0;
        try {
            return priceObj.has("number") ? priceObj.get("number").getAsInt() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public String extractCurrency(JsonObject fareObj) {
        JsonObject priceObj = extractJsonObject(fareObj, "price");
        if (priceObj == null) return "Unknown";
        return extractString(priceObj, "currency_code") != null ? extractString(priceObj, "currency_code") : "Unknown";
    }

    public String extractFareName(JsonObject fareObj) {
        if (fareObj == null) return "Unknown";
        JsonObject fareInner = extractJsonObject(fareObj, "fare");
        if (fareInner != null) {
            String name = extractString(fareInner, "name");
            if (name != null) return name;
        }
        return extractString(fareObj, "name") != null ? extractString(fareObj, "name") : "Unknown";
    }

    public String extractCity(JsonObject obj, String key) {
        JsonObject station = extractJsonObject(obj, key);
        if (station == null) return "Unknown";
        JsonObject address = extractJsonObject(station, "address");
        if (address == null) return "Unknown";
        return extractString(address, "locality") != null ? extractString(address, "locality") : "Unknown";
    }
}
