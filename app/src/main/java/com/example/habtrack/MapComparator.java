package com.example.habtrack;

import java.util.Comparator;
import java.util.Map;

public class MapComparator implements Comparator<Map<String, Object>> {
    private final String key;

    public MapComparator(String key) {
        this.key = key;
    }

    public int compare(Map<String, Object> first, Map<String, Object> second) {
        String firstValue = first.get(key).toString();
        String secondValue = second.get(key).toString();
        return firstValue.compareTo(secondValue);
    }
}