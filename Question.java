package com.netconnect.Applications.UserApp.Details;
import java.util.List;

public class Question {
    private final String text;

    public  Question(String text)
    {
        this.text = text;
    }

    public Question(String text, List<String> options, String correctAnswer) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}