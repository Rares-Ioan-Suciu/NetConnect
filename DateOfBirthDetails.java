package com.netconnect.Applications.UserApp.Details;

import com.netconnect.Applications.UserApp.Exception.InvalidDateOfBirthException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateOfBirthDetails extends UserDetails {
    private static final int MIN_AGE = 18;
    private final LocalDate dateOfBirth;

    public DateOfBirthDetails(String dateOfBirth) throws InvalidDateOfBirthException {
        this.dateOfBirth = parseDateOfBirth(dateOfBirth);
        verifyInfo();
    }

    public String getDetails() {
        return dateOfBirth.toString();
    }

    private LocalDate parseDateOfBirth(String date) throws InvalidDateOfBirthException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidDateOfBirthException("Date of birth must be in YYYY-MM-DD format.");
        }
    }


    public boolean isVerified() {
        try {
            verifyInfo();
            return true;
        } catch (InvalidDateOfBirthException e) {
            return false;
        }
    }

    @Override
    public void verifyInfo() throws InvalidDateOfBirthException
    {
        LocalDate today = LocalDate.now();
        int age = Period.between(dateOfBirth, today).getYears();
        if (age < MIN_AGE) {
            throw new InvalidDateOfBirthException("User must be at least " + MIN_AGE + " years old.");
        }
    }
}
