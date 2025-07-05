package com.unitrack;

public class NoteService {
    private String note = "";
    private static final String[] quotes = {
        "Push yourself, because no one else is going to do it for you.",
        "Success is the sum of small efforts, repeated.",
        "You are capable of amazing things.",
        "Dream big. Work hard. Stay focused.",
        "Discipline outlasts motivation."
    };

    public String loadNote() {
        return note;
    }

    public void saveNote(String text) {
        this.note = text;
    }

    public String getMotivationQuote() {
        int index = (int) (Math.random() * quotes.length);
        return quotes[index];
    }
}
