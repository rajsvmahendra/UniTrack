package com.unitrack;

import java.util.ArrayList;
import java.util.List;

public class TimetableService {
    private final List<String> timetable = new ArrayList<>();

    public List<String> getTimetable() {
        return timetable;
    }

    public void addEntry(String entry) {
        timetable.add(entry);
    }
}
