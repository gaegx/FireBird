package com.gaegxh.firebirdtask2.service.parse.Impl;

import java.util.HashSet;
import java.util.Set;

public class UniqueTrainInfoTracker {
    private final Set<String> uniqueKeys = new HashSet<>();

    public boolean contains(String key) {
        return uniqueKeys.contains(key);
    }

    public void add(String key) {
        uniqueKeys.add(key);
    }
}
